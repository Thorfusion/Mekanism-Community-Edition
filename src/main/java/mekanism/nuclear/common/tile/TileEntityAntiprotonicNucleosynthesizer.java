package mekanism.nuclear.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.EnumColor;
import mekanism.api.IConfigCardAccess;
import mekanism.api.TileNetworkList;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasItem;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.PacketHandler;
import mekanism.common.SideData;
import mekanism.common.Upgrade;
import mekanism.common.Upgrade.IUpgradeInfoHandler;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ITankManager;
import mekanism.common.base.IComparatorSupport;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.prefab.TileEntityMachine;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.GasUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.recipe.NuclearRecipeRegistry;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.integration.mekanism.LongBackedMekGasTank;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;

/** Stable Antiprotonic Nucleosynthesizer adapted to the 1.12 machine APIs. */
public class TileEntityAntiprotonicNucleosynthesizer extends TileEntityMachine implements IGasHandler,
      IChemicalHandlerCE, ISideConfiguration, IConfigCardAccess, ITankManager, IUpgradeInfoHandler,
      IComparatorSupport {

    public static final int MAX_GAS = 10_000;
    public static final int BASE_TICKS_REQUIRED = 400;
    public static final double BASE_ENERGY_USAGE = 100_000D;
    public static final double BASE_ENERGY_STORAGE = 1_000_000_000D;

    private static final int GAS_SLOT = 0;
    private static final int INPUT_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private static final int ENERGY_SLOT = 3;
    private static final int UPGRADE_SLOT = 4;

    private final LongChemicalTank chemicalTank = new LongChemicalTank(MAX_GAS);
    public final LongBackedMekGasTank inputTank = new LongBackedMekGasTank(chemicalTank,
          TileEntityAntiprotonicNucleosynthesizer::type);

    public final TileComponentConfig configComponent;
    public final TileComponentEjector ejectorComponent;
    public int operatingTicks;
    public int ticksRequired = BASE_TICKS_REQUIRED;
    public double clientEnergyUsed;

    @Nullable
    private ResourceLocation activeRecipe;
    private int currentRedstoneLevel;

    public TileEntityAntiprotonicNucleosynthesizer() {
        super("machine.nucleosynthesizer", "AntiprotonicNucleosynthesizer",
              BASE_ENERGY_STORAGE, BASE_ENERGY_USAGE, UPGRADE_SLOT);
        inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        // Stable Mekanism intentionally does not allow this machine to be sped up.
        upgradeComponent.setSupported(Upgrade.SPEED, false);

        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.ENERGY,
              TransmissionType.GAS);
        configComponent.addOutput(TransmissionType.ITEM, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Gas", EnumColor.DARK_AQUA, new int[]{GAS_SLOT}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Input", EnumColor.DARK_GREEN, new int[]{INPUT_SLOT}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Output", EnumColor.DARK_RED, new int[]{OUTPUT_SLOT}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Energy", EnumColor.DARK_BLUE, new int[]{ENERGY_SLOT}));
        configComponent.setConfig(TransmissionType.ITEM, new byte[]{3, 2, 0, 0, 1, 4});

        configComponent.addOutput(TransmissionType.GAS, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.GAS, new SideData("Input", EnumColor.DARK_GREEN, new int[]{0}));
        configComponent.fillConfig(TransmissionType.GAS, 1);
        configComponent.setCanEject(TransmissionType.GAS, false);
        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM,
              configComponent.getOutputs(TransmissionType.ITEM).get(3));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            return;
        }

        ChargeUtils.discharge(ENERGY_SLOT, this);
        transferInputContainer();
        ItemChemicalToItemRecipeCE recipe = getRecipe();
        updateRecipeIdentity(recipe);

        boolean canOperate = canOperate(recipe);
        boolean running = canOperate && getEnergy() >= energyPerTick && MekanismUtils.canFunction(this);
        if (running) {
            setEnergy(getEnergy() - energyPerTick);
            clientEnergyUsed = energyPerTick;
            setActive(true);
            if (++operatingTicks >= ticksRequired) {
                operate(recipe);
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

    private void transferInputContainer() {
        ItemStack stack = inventory.get(GAS_SLOT);
        if (stack.isEmpty() || !(stack.getItem() instanceof IGasItem)) {
            return;
        }
        GasStack contained = ((IGasItem) stack.getItem()).getGas(stack);
        if (contained == null || contained.getGas() != NuclearChemicals.Antimatter) {
            return;
        }
        int needed = (int) Math.min(Integer.MAX_VALUE, chemicalTank.getCapacity() - chemicalTank.getStored());
        if (needed <= 0) {
            return;
        }
        GasStack removed = GasUtils.removeGas(stack, NuclearChemicals.Antimatter, needed);
        if (removed == null) {
            return;
        }
        int accepted = receiveGas(null, removed, true);
        if (accepted < removed.amount) {
            GasUtils.addGas(stack, removed.copy().withAmount(removed.amount - accepted));
        }
    }

    @Nullable
    public ItemChemicalToItemRecipeCE getRecipe() {
        return NuclearRecipeRegistry.findNucleosynthesizing(inventory.get(INPUT_SLOT), chemicalTank.getStack());
    }

    private void updateRecipeIdentity(@Nullable ItemChemicalToItemRecipeCE recipe) {
        ResourceLocation next = recipe == null ? null : recipe.getId();
        if (next == null ? activeRecipe != null : !next.equals(activeRecipe)) {
            operatingTicks = 0;
            activeRecipe = next;
        }
        ticksRequired = recipe == null ? BASE_TICKS_REQUIRED : recipe.getDuration();
    }

    public boolean canOperate(@Nullable ItemChemicalToItemRecipeCE recipe) {
        if (recipe == null || !recipe.getItemInput().hasRequiredAmount(inventory.get(INPUT_SLOT))
              || !recipe.getChemicalInput().hasRequiredAmount(chemicalTank.getStack())) {
            return false;
        }
        ItemStack result = recipe.getOutput();
        ItemStack stored = inventory.get(OUTPUT_SLOT);
        if (stored.isEmpty()) {
            return true;
        }
        return ItemStack.areItemsEqual(stored, result) && ItemStack.areItemStackTagsEqual(stored, result)
              && stored.getCount() + result.getCount() <= Math.min(stored.getMaxStackSize(), getInventoryStackLimit());
    }

    private void operate(ItemChemicalToItemRecipeCE recipe) {
        long chemicalAmount = recipe.getChemicalInput().getAmount();
        IChemicalStackCE extracted = chemicalTank.extract(chemicalAmount, Action.EXECUTE);
        if (extracted == null || extracted.getAmount() != chemicalAmount) {
            throw new IllegalStateException("Nucleosynthesizer chemical commit diverged from validation");
        }
        inventory.get(INPUT_SLOT).shrink(recipe.getItemInput().getAmount());
        ItemStack result = recipe.getOutput();
        ItemStack stored = inventory.get(OUTPUT_SLOT);
        if (stored.isEmpty()) {
            inventory.set(OUTPUT_SLOT, result);
        } else {
            stored.grow(result.getCount());
        }
        markDirty();
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
        if (stack == null || !canReceiveGas(side, stack.getGas())) {
            return 0;
        }
        return (int) chemicalTank.insert(new ChemicalStackCE(type(stack.getGas()), stack.amount),
              doTransfer ? Action.EXECUTE : Action.SIMULATE);
    }

    @Nullable
    @Override
    public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer) {
        return null;
    }

    @Override
    public boolean canReceiveGas(EnumFacing side, Gas gas) {
        return gas == NuclearChemicals.Antimatter && isInputSide(side)
              && chemicalTank.getStored() < chemicalTank.getCapacity();
    }

    @Override
    public boolean canDrawGas(EnumFacing side, Gas gas) {
        return false;
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo() {
        return new GasTankInfo[]{inputTank};
    }

    @Override
    public int getChemicalTankCount(@Nullable EnumFacing side) {
        return 1;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank, @Nullable EnumFacing side) {
        if (tank == 0) {
            return chemicalTank;
        }
        throw new IndexOutOfBoundsException("Chemical tank index: " + tank);
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE chemical, @Nullable EnumFacing side) {
        return tank == 0 && chemical instanceof MekGasChemicalType
              && canReceiveGas(side, ((MekGasChemicalType) chemical).getGas());
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE chemical, @Nullable EnumFacing side) {
        return false;
    }

    private boolean isInputSide(@Nullable EnumFacing side) {
        return side == null || configComponent.getOutput(TransmissionType.GAS, side, facing).hasSlot(0);
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
        if (slot == GAS_SLOT && stack.getItem() instanceof IGasItem) {
            GasStack gas = ((IGasItem) stack.getItem()).getGas(stack);
            return gas != null && gas.getGas() == NuclearChemicals.Antimatter;
        } else if (slot == INPUT_SLOT) {
            return NuclearRecipeRegistry.containsNucleosynthesizingInput(stack);
        } else if (slot == ENERGY_SLOT) {
            return ChargeUtils.canBeDischarged(stack);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side) {
        if (slot == GAS_SLOT) {
            return stack.getItem() instanceof IGasItem && ((IGasItem) stack.getItem()).getGas(stack) == null;
        } else if (slot == OUTPUT_SLOT) {
            return true;
        } else if (slot == ENERGY_SLOT) {
            return ChargeUtils.canBeOutputted(stack, false);
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        operatingTicks = data.getInteger("operatingTicks");
        String recipe = data.getString("activeRecipe");
        activeRecipe = recipe.isEmpty() ? null : new ResourceLocation(recipe);
        chemicalTank.readFromNBT(data.getCompoundTag("AntimatterTank"), (kind, name) -> {
            Gas gas = mekanism.api.gas.GasRegistry.getGas(name);
            return gas == null ? null : type(gas);
        });
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("operatingTicks", operatingTicks);
        if (activeRecipe != null) {
            data.setString("activeRecipe", activeRecipe.toString());
        }
        NBTTagCompound chemicals = new NBTTagCompound();
        chemicalTank.writeToNBT(chemicals);
        data.setTag("AntimatterTank", chemicals);
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        super.handlePacketData(data);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            operatingTicks = data.readInt();
            ticksRequired = data.readInt();
            clientEnergyUsed = data.readDouble();
            String name = PacketHandler.readString(data);
            long amount = data.readLong();
            Gas gas = name.isEmpty() ? null : mekanism.api.gas.GasRegistry.getGas(name);
            inputTank.setGas(gas == null || amount <= 0 ? null
                  : new GasStack(gas, (int) Math.min(Integer.MAX_VALUE, amount)));
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(operatingTicks);
        data.add(ticksRequired);
        data.add(clientEnergyUsed);
        IChemicalStackCE stack = chemicalTank.getStack();
        Gas gas = toGas(stack);
        data.add(gas == null ? "" : gas.getName());
        data.add(stack == null ? 0L : stack.getAmount());
        return data;
    }

    public double getScaledProgress() {
        return ticksRequired <= 0 ? 0 : (double) operatingTicks / ticksRequired;
    }

    @Override
    public int getRedstoneLevel() {
        return Container.calcRedstoneFromInventory(this);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing side) {
        if (isCapabilityDisabled(capability, side)) {
            return false;
        }
        return capability == Capabilities.GAS_HANDLER_CAPABILITY || capability == Capabilities.CONFIG_CARD_CAPABILITY
              || super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing side) {
        if (isCapabilityDisabled(capability, side)) {
            return null;
        }
        if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
            return Capabilities.GAS_HANDLER_CAPABILITY.cast(this);
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
    public Object[] getTanks() {
        return new Object[]{inputTank};
    }

    @Override
    public List<String> getInfo(Upgrade upgrade) {
        return upgrade.getMultScaledInfo(this);
    }

    @Nonnull
    @Override
    public String getName() {
        return LangUtils.localize("tile.AntiprotonicNucleosynthesizer.name");
    }

    @Nullable
    private static Gas toGas(@Nullable IChemicalStackCE stack) {
        return stack != null && stack.getType() instanceof MekGasChemicalType
              ? ((MekGasChemicalType) stack.getType()).getGas() : null;
    }

    private static MekGasChemicalType type(Gas gas) {
        return new MekGasChemicalType(gas, NuclearChemicals.isRadioactive(gas.getName()));
    }
}
