package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.EnumColor;
import mekanism.api.IConfigCardAccess;
import mekanism.api.TileNetworkList;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.SideData;
import mekanism.common.Upgrade;
import mekanism.common.Upgrade.IUpgradeInfoHandler;
import mekanism.common.base.FluidHandlerWrapper;
import mekanism.common.base.IComparatorSupport;
import mekanism.common.base.IFluidHandlerWrapper;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ITankManager;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.prefab.TileEntityMachine;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.FluidContainerUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.TileUtils;
import mekanism.ultimate.common.UltimateFluids;
import mekanism.ultimate.common.config.UltimateNutritionConfig;
import mekanism.ultimate.common.nutrition.UltimateNutrition;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.ItemHandlerHelper;

/** Converts one food item into fifty millibuckets of paste per hunger point. */
public final class TileEntityNutritionalLiquifier extends TileEntityMachine implements IFluidHandlerWrapper,
      ISideConfiguration, IConfigCardAccess, ITankManager, IUpgradeInfoHandler, IComparatorSupport {

    public static final int MAX_FLUID = 10_000;
    public static final int BASE_TICKS_REQUIRED = 100;

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int CONTAINER_SLOT = 2;
    public static final int CONTAINER_OUTPUT_SLOT = 3;
    public static final int ENERGY_SLOT = 4;
    public static final int UPGRADE_SLOT = 5;

    public final FluidTank fluidTank = new FluidTank(MAX_FLUID);
    public final TileComponentConfig configComponent;
    public final TileComponentEjector ejectorComponent;

    public int operatingTicks;
    public int ticksRequired = BASE_TICKS_REQUIRED;
    public double clientEnergyUsed;

    private int currentRedstoneLevel;

    public TileEntityNutritionalLiquifier() {
        super("null", "NutritionalLiquifier", getConfiguredStorage(), getConfiguredUsage(), UPGRADE_SLOT);
        inventory = NonNullList.withSize(6, ItemStack.EMPTY);

        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.ENERGY,
              TransmissionType.FLUID);
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Input", EnumColor.DARK_GREEN, new int[]{INPUT_SLOT, CONTAINER_SLOT}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Output", EnumColor.DARK_RED, new int[]{OUTPUT_SLOT, CONTAINER_OUTPUT_SLOT}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Energy", EnumColor.DARK_BLUE, new int[]{ENERGY_SLOT}));
        configComponent.setConfig(TransmissionType.ITEM, new byte[]{0, 3, 0, 2, 1, 0});

        configComponent.addOutput(TransmissionType.FLUID,
              new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.FLUID,
              new SideData("Output", EnumColor.DARK_RED, new int[]{0}));
        configComponent.setConfig(TransmissionType.FLUID, new byte[]{0, 0, 0, 1, 0, 0});
        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM,
              configComponent.getOutputs(TransmissionType.ITEM).get(2));
        ejectorComponent.setOutputData(TransmissionType.FLUID,
              configComponent.getOutputs(TransmissionType.FLUID).get(1));
    }

    private static double getConfiguredStorage() {
        return Math.max(1, UltimateNutritionConfig.nutritionalLiquifierEnergyStorage);
    }

    private static double getConfiguredUsage() {
        return Math.max(0, UltimateNutritionConfig.nutritionalLiquifierEnergyUsage);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            return;
        }

        ChargeUtils.discharge(ENERGY_SLOT, this);
        fillContainer();

        boolean canOperate = canOperate();
        boolean running = canOperate && MekanismUtils.canFunction(this) && getEnergy() >= energyPerTick;
        if (running) {
            setEnergy(getEnergy() - energyPerTick);
            clientEnergyUsed = energyPerTick;
            setActive(true);
            if (++operatingTicks >= ticksRequired) {
                operate();
                operatingTicks = 0;
            }
        } else {
            clientEnergyUsed = 0;
            setActive(false);
            if (!canOperate) {
                operatingTicks = 0;
            }
        }
        prevEnergy = getEnergy();

        int redstoneLevel = getRedstoneLevel();
        if (redstoneLevel != currentRedstoneLevel) {
            currentRedstoneLevel = redstoneLevel;
            world.updateComparatorOutputLevel(pos, getBlockType());
        }
    }

    private void fillContainer() {
        ItemStack input = inventory.get(CONTAINER_SLOT);
        FluidStack stored = fluidTank.getFluid();
        if (input.isEmpty() || input.getCount() != 1 || stored == null || stored.amount <= 0) {
            return;
        }
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(input);
        if (handler == null) {
            return;
        }
        FluidStack offered = stored.copy();
        int filled = handler.fill(offered, true);
        if (filled > 0) {
            fluidTank.drain(filled, true);
            inventory.set(CONTAINER_SLOT, handler.getContainer());
            markDirty();
        }

        ItemStack filledContainer = inventory.get(CONTAINER_SLOT);
        if (filled <= 0 && FluidUtil.getFluidContained(filledContainer) == null) {
            return;
        }
        FluidStack remaining = fluidTank.getFluid();
        boolean cannotFillMore = remaining == null || remaining.amount <= 0;
        if (!cannotFillMore) {
            IFluidHandlerItem remainingHandler = FluidUtil.getFluidHandler(filledContainer);
            cannotFillMore = remainingHandler == null || remainingHandler.fill(remaining.copy(), false) <= 0;
        }
        if (!cannotFillMore || filledContainer.isEmpty()) {
            return;
        }
        ItemStack output = inventory.get(CONTAINER_OUTPUT_SLOT);
        if (output.isEmpty()) {
            inventory.set(CONTAINER_OUTPUT_SLOT, filledContainer);
            inventory.set(CONTAINER_SLOT, ItemStack.EMPTY);
            markDirty();
        } else if (ItemHandlerHelper.canItemStacksStack(output, filledContainer)
              && output.getCount() < output.getMaxStackSize()) {
            output.grow(1);
            inventory.set(CONTAINER_SLOT, ItemStack.EMPTY);
            markDirty();
        }
    }

    public boolean canOperate() {
        ItemStack input = inventory.get(INPUT_SLOT);
        int paste = getPasteOutput(input);
        if (paste <= 0 || paste > fluidTank.getCapacity() - fluidTank.getFluidAmount()) {
            return false;
        }
        ItemStack remainder = UltimateNutrition.getContainerItem(input);
        if (remainder.isEmpty()) {
            return true;
        }
        ItemStack output = inventory.get(OUTPUT_SLOT);
        return output.isEmpty() || ItemStack.areItemsEqual(output, remainder)
              && ItemStack.areItemStackTagsEqual(output, remainder)
              && output.getCount() + remainder.getCount() <= Math.min(output.getMaxStackSize(), getInventoryStackLimit());
    }

    private void operate() {
        if (!canOperate()) {
            return;
        }
        ItemStack input = inventory.get(INPUT_SLOT);
        int paste = getPasteOutput(input);
        ItemStack remainder = UltimateNutrition.getContainerItem(input);
        input.shrink(1);
        if (input.isEmpty()) {
            inventory.set(INPUT_SLOT, ItemStack.EMPTY);
        }
        if (!remainder.isEmpty()) {
            ItemStack output = inventory.get(OUTPUT_SLOT);
            if (output.isEmpty()) {
                inventory.set(OUTPUT_SLOT, remainder);
            } else {
                output.grow(remainder.getCount());
            }
        }
        fluidTank.fill(new FluidStack(UltimateFluids.NutritionalPaste, paste), true);
        markDirty();
    }

    public static int getPasteOutput(ItemStack stack) {
        return UltimateNutrition.getPasteOutput(stack);
    }

    public double getScaledProgress() {
        return ticksRequired <= 0 ? 0 : (double) operatingTicks / ticksRequired;
    }

    @Override
    public void recalculateUpgradables(Upgrade upgrade) {
        super.recalculateUpgradables(upgrade);
        if (upgrade == Upgrade.SPEED) {
            ticksRequired = MekanismUtils.getTicks(this, BASE_TICKS_REQUIRED);
            energyPerTick = MekanismUtils.getEnergyPerTick(this, BASE_ENERGY_PER_TICK);
        } else if (upgrade == Upgrade.ENERGY) {
            energyPerTick = MekanismUtils.getEnergyPerTick(this, BASE_ENERGY_PER_TICK);
        }
    }

    @Override
    public int fill(EnumFacing from, @Nonnull FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public boolean canFill(EnumFacing from, @Nonnull FluidStack fluid) {
        return false;
    }

    @Nullable
    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return canDrain(from, null) ? fluidTank.drain(maxDrain, doDrain) : null;
    }

    @Override
    public boolean canDrain(EnumFacing from, @Nullable FluidStack fluid) {
        if (from == null || !configComponent.getOutput(TransmissionType.FLUID, from, facing).hasSlot(0)) {
            return false;
        }
        FluidStack stored = fluidTank.getFluid();
        return stored != null && (fluid == null || stored.isFluidEqual(fluid));
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return configComponent.getOutput(TransmissionType.FLUID, from, facing).getFluidTankInfo(this);
    }

    @Override
    public FluidTankInfo[] getAllTanks() {
        return new FluidTankInfo[]{fluidTank.getInfo()};
    }

    @Override
    public Object[] getTanks() {
        return new Object[]{fluidTank};
    }

    @Override
    public boolean sideIsConsumer(EnumFacing side) {
        return side == null || configComponent.hasSideForData(TransmissionType.ENERGY, facing, 1, side);
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return configComponent.getOutput(TransmissionType.ITEM, side, facing).availableSlots;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        if (slot == INPUT_SLOT) {
            return getPasteOutput(stack) > 0;
        } else if (slot == CONTAINER_SLOT) {
            return FluidContainerUtils.isFluidContainer(stack);
        } else if (slot == ENERGY_SLOT) {
            return ChargeUtils.canBeDischarged(stack);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side) {
        if (slot == OUTPUT_SLOT || slot == CONTAINER_OUTPUT_SLOT) {
            return true;
        } else if (slot == ENERGY_SLOT) {
            return ChargeUtils.canBeOutputted(stack, false);
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        operatingTicks = Math.max(0, data.getInteger("operatingTicks"));
        fluidTank.readFromNBT(data.getCompoundTag("NutritionalPasteTank"));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("operatingTicks", operatingTicks);
        data.setTag("NutritionalPasteTank", fluidTank.writeToNBT(new NBTTagCompound()));
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        super.handlePacketData(data);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            operatingTicks = data.readInt();
            ticksRequired = data.readInt();
            clientEnergyUsed = data.readDouble();
            TileUtils.readTankData(data, fluidTank);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(operatingTicks);
        data.add(ticksRequired);
        data.add(clientEnergyUsed);
        TileUtils.addTankData(data, fluidTank);
        return data;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing side) {
        if (isCapabilityDisabled(capability, side)) {
            return false;
        }
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
              || capability == Capabilities.CONFIG_CARD_CAPABILITY || super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing side) {
        if (isCapabilityDisabled(capability, side)) {
            return null;
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FluidHandlerWrapper(this, side));
        } else if (capability == Capabilities.CONFIG_CARD_CAPABILITY) {
            return Capabilities.CONFIG_CARD_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public boolean isCapabilityDisabled(@Nonnull Capability<?> capability, EnumFacing side) {
        return configComponent.isCapabilityDisabled(capability, side, facing)
              || super.isCapabilityDisabled(capability, side);
    }

    @Override
    public TileComponentConfig getConfig() {
        return configComponent;
    }

    @Override
    public EnumFacing getOrientation() {
        return facing;
    }

    @Override
    public TileComponentEjector getEjector() {
        return ejectorComponent;
    }

    @Override
    public List<String> getInfo(Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? upgrade.getExpScaledInfo(this) : upgrade.getMultScaledInfo(this);
    }

    @Nonnull
    @Override
    public String getName() {
        return LangUtils.localize("tile.NutritionalLiquifier.name");
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(fluidTank.getFluidAmount(), fluidTank.getCapacity());
    }
}
