package mekanism.ultimate.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.IConfigCardAccess;
import mekanism.api.Range4D;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.SideData;
import mekanism.common.Upgrade;
import mekanism.common.Upgrade.IUpgradeInfoHandler;
import mekanism.common.base.IActiveState;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ITankManager;
import mekanism.common.base.IUpgradeItem;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.TileEntityNoisyElectricBlock;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.FluidContainerUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.common.UltimateFluids;
import mekanism.ultimate.common.config.UltimateNutritionConfig;
import mekanism.ultimate.common.nutrition.UltimateNutrition;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

/** Converts one food item into fifty millibuckets per hunger point. */
public final class TileEntityNutritionalLiquifier
      extends TileEntityNoisyElectricBlock
      implements IFluidHandler, ISideConfiguration, IConfigCardAccess,
      ITankManager, IUpgradeTile, IUpgradeInfoHandler, IRedstoneControl,
      IActiveState, ISecurityTile
{
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
    public final TileComponentUpgrade upgradeComponent =
          new TileComponentUpgrade(this, UPGRADE_SLOT);
    public final TileComponentSecurity securityComponent =
          new TileComponentSecurity(this);

    public RedstoneControl controlType = RedstoneControl.DISABLED;
    public boolean isActive;
    public boolean clientActive;
    public int updateDelay;
    public int operatingTicks;
    public int ticksRequired = BASE_TICKS_REQUIRED;
    public double energyPerTick = getConfiguredUsage();
    public double clientEnergyUsed;

    private int currentRedstoneLevel;

    public TileEntityNutritionalLiquifier()
    {
        super("null", "NutritionalLiquifier", getConfiguredStorage());
        inventory = new ItemStack[6];
        upgradeComponent.setSupported(Upgrade.MUFFLING);

        configComponent = new TileComponentConfig(this,
              TransmissionType.ITEM, TransmissionType.ENERGY,
              TransmissionType.FLUID);
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Input", EnumColor.DARK_GREEN,
                    new int[] {INPUT_SLOT, CONTAINER_SLOT}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Output", EnumColor.DARK_RED,
                    new int[] {OUTPUT_SLOT, CONTAINER_OUTPUT_SLOT}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Energy", EnumColor.DARK_BLUE,
                    new int[] {ENERGY_SLOT}));
        configComponent.setConfig(TransmissionType.ITEM,
              new byte[] {0, 3, 0, 2, 1, 0});

        configComponent.addOutput(TransmissionType.FLUID,
              new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.FLUID,
              new SideData("Output", EnumColor.DARK_RED, new int[] {0}));
        configComponent.setConfig(TransmissionType.FLUID,
              new byte[] {0, 0, 0, 1, 0, 0});
        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM,
              configComponent.getOutputs(TransmissionType.ITEM).get(2));
        ejectorComponent.setOutputData(TransmissionType.FLUID,
              configComponent.getOutputs(TransmissionType.FLUID).get(1));
    }

    private static double getConfiguredStorage()
    {
        return Math.max(1,
              UltimateNutritionConfig.nutritionalLiquifierEnergyStorage);
    }

    private static double getConfiguredUsage()
    {
        return Math.max(0,
              UltimateNutritionConfig.nutritionalLiquifierEnergyUsage);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (worldObj.isRemote)
        {
            if (updateDelay > 0 && --updateDelay == 0
                  && clientActive != isActive)
            {
                isActive = clientActive;
                MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
            }
            return;
        }

        if (updateDelay > 0 && --updateDelay == 0
              && clientActive != isActive)
        {
            sendUpdatePacket();
        }
        ChargeUtils.discharge(ENERGY_SLOT, this);
        fillContainer();

        boolean canOperate = canOperate();
        boolean running = canOperate && MekanismUtils.canFunction(this)
              && getEnergy() >= energyPerTick;
        if (running)
        {
            setEnergy(getEnergy() - energyPerTick);
            clientEnergyUsed = energyPerTick;
            setActive(true);
            if (++operatingTicks >= ticksRequired)
            {
                operate();
                operatingTicks = 0;
            }
        }
        else
        {
            clientEnergyUsed = 0;
            setActive(false);
            if (!canOperate)
            {
                operatingTicks = 0;
            }
        }

        int redstoneLevel = getRedstoneLevel();
        if (redstoneLevel != currentRedstoneLevel)
        {
            currentRedstoneLevel = redstoneLevel;
            worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
        }
    }

    private void fillContainer()
    {
        ItemStack input = inventory[CONTAINER_SLOT];
        if (input == null || input.stackSize != 1
              || !(input.getItem() instanceof IFluidContainerItem)
              || fluidTank.getFluidAmount() <= 0)
        {
            return;
        }
        FluidContainerUtils.handleContainerItemFill(this, fluidTank,
              CONTAINER_SLOT, CONTAINER_OUTPUT_SLOT);
    }

    public boolean canOperate()
    {
        ItemStack input = inventory[INPUT_SLOT];
        int paste = getPasteOutput(input);
        if (paste <= 0
              || paste > fluidTank.getCapacity() - fluidTank.getFluidAmount())
        {
            return false;
        }
        ItemStack remainder = UltimateNutrition.getContainerItem(input);
        if (remainder == null)
        {
            return true;
        }
        ItemStack output = inventory[OUTPUT_SLOT];
        return output == null || output.isItemEqual(remainder)
              && ItemStack.areItemStackTagsEqual(output, remainder)
              && output.stackSize + remainder.stackSize <= Math.min(
                    output.getMaxStackSize(), getInventoryStackLimit());
    }

    private void operate()
    {
        if (!canOperate())
        {
            return;
        }
        ItemStack input = inventory[INPUT_SLOT];
        int paste = getPasteOutput(input);
        ItemStack remainder = UltimateNutrition.getContainerItem(input);
        if (--input.stackSize <= 0)
        {
            inventory[INPUT_SLOT] = null;
        }
        if (remainder != null)
        {
            if (inventory[OUTPUT_SLOT] == null)
            {
                inventory[OUTPUT_SLOT] = remainder;
            }
            else
            {
                inventory[OUTPUT_SLOT].stackSize += remainder.stackSize;
            }
        }
        fluidTank.fill(new FluidStack(UltimateFluids.NutritionalPaste,
              paste), true);
        markDirty();
    }

    public static int getPasteOutput(ItemStack stack)
    {
        return UltimateNutrition.getPasteOutput(stack);
    }

    public double getScaledProgress()
    {
        return ticksRequired <= 0 ? 0
              : (double)operatingTicks / ticksRequired;
    }

    @Override
    public void recalculateUpgradables(Upgrade upgrade)
    {
        super.recalculateUpgradables(upgrade);
        if (upgrade == Upgrade.SPEED)
        {
            ticksRequired = MekanismUtils.getTicks(this,
                  BASE_TICKS_REQUIRED);
            energyPerTick = MekanismUtils.getEnergyPerTick(this,
                  getConfiguredUsage());
        }
        else if (upgrade == Upgrade.ENERGY)
        {
            energyPerTick = MekanismUtils.getEnergyPerTick(this,
                  getConfiguredUsage());
            maxEnergy = MekanismUtils.getMaxEnergy(this,
                  getConfiguredStorage());
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource,
          boolean doFill)
    {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource,
          boolean doDrain)
    {
        if (resource == null || !canDrain(from, resource.getFluid()))
        {
            return null;
        }
        FluidStack stored = fluidTank.getFluid();
        if (stored == null || !stored.isFluidEqual(resource))
        {
            return null;
        }
        return fluidTank.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain,
          boolean doDrain)
    {
        return canDrain(from, null)
              ? fluidTank.drain(maxDrain, doDrain) : null;
    }

    @Override
    public boolean canFill(ForgeDirection from,
          net.minecraftforge.fluids.Fluid fluid)
    {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from,
          net.minecraftforge.fluids.Fluid fluid)
    {
        if (!isFluidOutputSide(from))
        {
            return false;
        }
        FluidStack stored = fluidTank.getFluid();
        return stored != null
              && (fluid == null || stored.getFluid() == fluid);
    }

    private boolean isFluidOutputSide(ForgeDirection side)
    {
        return side == null || side == ForgeDirection.UNKNOWN
              || configComponent.getOutput(TransmissionType.FLUID,
                    side.ordinal(), facing).hasSlot(0);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return isFluidOutputSide(from)
              ? new FluidTankInfo[] {fluidTank.getInfo()}
              : new FluidTankInfo[0];
    }

    @Override
    public EnumSet<ForgeDirection> getConsumingSides()
    {
        return configComponent.getSidesForData(TransmissionType.ENERGY,
              facing, 1);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return side >= 0 && side < 6
              ? configComponent.getOutput(TransmissionType.ITEM, side,
                    facing).availableSlots : InventoryUtils.EMPTY;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }
        if (slot == INPUT_SLOT)
        {
            return getPasteOutput(stack) > 0;
        }
        if (slot == CONTAINER_SLOT)
        {
            return stack.getItem() instanceof IFluidContainerItem;
        }
        if (slot == ENERGY_SLOT)
        {
            return ChargeUtils.canBeDischarged(stack);
        }
        if (slot == UPGRADE_SLOT
              && stack.getItem() instanceof IUpgradeItem)
        {
            return upgradeComponent.supports(((IUpgradeItem)stack.getItem())
                  .getUpgradeType(stack));
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        if (stack == null)
        {
            return false;
        }
        if (slot == OUTPUT_SLOT || slot == CONTAINER_OUTPUT_SLOT)
        {
            return true;
        }
        return slot == ENERGY_SLOT
              && ChargeUtils.canBeOutputted(stack, false);
    }

    @Override
    public boolean canSetFacing(int side)
    {
        return side != ForgeDirection.DOWN.ordinal()
              && side != ForgeDirection.UP.ordinal();
    }

    @Override
    public RedstoneControl getControlType()
    {
        return controlType;
    }

    @Override
    public void setControlType(RedstoneControl type)
    {
        controlType = type == null ? RedstoneControl.DISABLED : type;
        MekanismUtils.saveChunk(this);
    }

    @Override
    public boolean canPulse()
    {
        return false;
    }

    @Override
    public boolean getActive()
    {
        return isActive;
    }

    @Override
    public void setActive(boolean active)
    {
        isActive = active;
        if (clientActive != active && updateDelay == 0)
        {
            sendUpdatePacket();
            updateDelay = 10;
            clientActive = active;
        }
    }

    private void sendUpdatePacket()
    {
        Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(
              Coord4D.get(this), getNetworkedData(new ArrayList())),
              new Range4D(Coord4D.get(this)));
    }

    @Override
    public boolean renderUpdate()
    {
        return false;
    }

    @Override
    public boolean lightUpdate()
    {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        isActive = data.getBoolean("isActive");
        clientActive = isActive;
        operatingTicks = Math.max(0, data.getInteger("operatingTicks"));
        int control = data.getInteger("controlType");
        controlType = control >= 0
              && control < RedstoneControl.values().length
              ? RedstoneControl.values()[control]
              : RedstoneControl.DISABLED;
        if (data.hasKey("NutritionalPasteTank", 10))
        {
            fluidTank.readFromNBT(data.getCompoundTag(
                  "NutritionalPasteTank"));
            if (fluidTank.getFluid() != null
                  && fluidTank.getFluid().getFluid()
                        != UltimateFluids.NutritionalPaste)
            {
                fluidTank.setFluid(null);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("isActive", isActive);
        data.setInteger("operatingTicks", operatingTicks);
        data.setInteger("controlType", controlType.ordinal());
        data.setTag("NutritionalPasteTank",
              fluidTank.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            isActive = data.readBoolean();
            int control = data.readInt();
            controlType = control >= 0
                  && control < RedstoneControl.values().length
                  ? RedstoneControl.values()[control]
                  : RedstoneControl.DISABLED;
            operatingTicks = data.readInt();
            ticksRequired = data.readInt();
            clientEnergyUsed = data.readDouble();
            if (data.readBoolean())
            {
                int fluidId = data.readInt();
                int amount = data.readInt();
                Fluid fluid = FluidRegistry.getFluid(fluidId);
                fluidTank.setFluid(fluid != null && amount > 0
                      ? new FluidStack(fluid, amount) : null);
            }
            else
            {
                fluidTank.setFluid(null);
            }
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        data.add(isActive);
        data.add(controlType.ordinal());
        data.add(operatingTicks);
        data.add(ticksRequired);
        data.add(clientEnergyUsed);
        FluidStack stored = fluidTank.getFluid();
        data.add(stored != null);
        if (stored != null)
        {
            data.add(stored.getFluidID());
            data.add(stored.amount);
        }
        return data;
    }

    public int getRedstoneLevel()
    {
        double fraction = (double)fluidTank.getFluidAmount()
              / fluidTank.getCapacity();
        return MathHelper.floor_double(fraction * 14D)
              + (fraction > 0 ? 1 : 0);
    }

    @Override
    public TileComponentConfig getConfig()
    {
        return configComponent;
    }

    @Override
    public int getOrientation()
    {
        return facing;
    }

    @Override
    public TileComponentEjector getEjector()
    {
        return ejectorComponent;
    }

    @Override
    public TileComponentUpgrade getComponent()
    {
        return upgradeComponent;
    }

    @Override
    public TileComponentSecurity getSecurity()
    {
        return securityComponent;
    }

    @Override
    public Object[] getTanks()
    {
        return new Object[] {fluidTank};
    }

    @Override
    public List<String> getInfo(Upgrade upgrade)
    {
        return upgrade == Upgrade.SPEED
              ? upgrade.getExpScaledInfo(this)
              : upgrade.getMultScaledInfo(this);
    }

    @Override
    public String getInventoryName()
    {
        return LangUtils.localize("tile.NutritionalLiquifier.name");
    }
}
