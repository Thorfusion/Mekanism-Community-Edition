package mekanism.nuclear.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.EnumColor;
import mekanism.api.IConfigCardAccess;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.GasTransmission;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasItem;
import mekanism.api.gas.IGasTankInfoProvider;
import mekanism.api.gas.ITubeConnection;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.SideData;
import mekanism.common.Upgrade;
import mekanism.common.Upgrade.IUpgradeInfoHandler;
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
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.api.Coord4D;
import mekanism.api.Range4D;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.recipe.NuclearRecipeRegistry;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.machine.ChemicalToChemicalRecipeProcessorCE;
import mekanism.ultimate.common.content.machine.RecipeProcessResultCE;
import mekanism.ultimate.common.integration.mekanism.LongBackedMekGasTank;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityIsotopicCentrifuge extends TileEntityNoisyElectricBlock
      implements IGasHandler, IGasTankInfoProvider, ITubeConnection, IChemicalHandlerCE,
      IRedstoneControl, ISideConfiguration, IConfigCardAccess, ITankManager,
      IUpgradeTile, IUpgradeInfoHandler, ISecurityTile
{
    public static final int MAX_GAS = 10000;
    public static final int GAS_OUTPUT = 256;
    public static final double BASE_MAX_ENERGY = 80000D;
    public static final double BASE_ENERGY_USAGE = 200D;

    private static final int INPUT_TANK = 0;
    private static final int OUTPUT_TANK = 1;

    private final ChemicalToChemicalRecipeProcessorCE processor =
          new ChemicalToChemicalRecipeProcessorCE(NuclearRecipeRegistry.CENTRIFUGING,
                MAX_GAS, MAX_GAS);

    public final LongBackedMekGasTank inputTank =
          new LongBackedMekGasTank(processor.getInputTank(), NuclearRecipeRegistry::type);
    public final LongBackedMekGasTank outputTank =
          new LongBackedMekGasTank(processor.getOutputTank(), NuclearRecipeRegistry::type);

    public final TileComponentConfig configComponent;
    public final TileComponentEjector ejectorComponent;
    public final TileComponentUpgrade upgradeComponent = new TileComponentUpgrade(this, 3);
    public final TileComponentSecurity securityComponent = new TileComponentSecurity(this);

    public RedstoneControl controlType = RedstoneControl.DISABLED;
    public boolean isActive;
    public boolean clientActive;
    public int updateDelay;
    public double energyPerTick = BASE_ENERGY_USAGE;
    public double clientEnergyUsed;
    public double prevEnergy;
    private int currentRedstoneLevel;

    public TileEntityIsotopicCentrifuge()
    {
        super("machine.cheminfuser", "IsotopicCentrifuge", BASE_MAX_ENERGY);
        inventory = new ItemStack[4];
        upgradeComponent.setSupported(Upgrade.MUFFLING);

        configComponent = new TileComponentConfig(this,
              TransmissionType.ITEM, TransmissionType.ENERGY, TransmissionType.GAS);
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Input", EnumColor.DARK_GREEN, new int[] {0}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Output", EnumColor.DARK_RED, new int[] {1}));
        configComponent.addOutput(TransmissionType.ITEM,
              new SideData("Energy", EnumColor.DARK_BLUE, new int[] {2}));
        configComponent.setConfig(TransmissionType.ITEM, new byte[] {0, 3, 0, 0, 1, 2});

        configComponent.addOutput(TransmissionType.GAS,
              new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.GAS,
              new SideData("Input", EnumColor.DARK_GREEN, new int[] {INPUT_TANK}));
        configComponent.addOutput(TransmissionType.GAS,
              new SideData("Output", EnumColor.DARK_RED, new int[] {OUTPUT_TANK}));
        configComponent.setConfig(TransmissionType.GAS, new byte[] {0, 0, 0, 0, 1, 2});
        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM,
              configComponent.getOutputs(TransmissionType.ITEM).get(2));
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (worldObj.isRemote)
        {
            if (updateDelay > 0 && --updateDelay == 0 && clientActive != isActive)
            {
                isActive = clientActive;
                MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
            }
            return;
        }

        if (updateDelay > 0 && --updateDelay == 0 && clientActive != isActive)
        {
            sendUpdatePacket();
        }

        ChargeUtils.discharge(2, this);
        transferInputContainer();
        transferOutputContainer();

        int completed = 0;
        int maxOperations = 1 << upgradeComponent.getUpgrades(Upgrade.SPEED);
        while (completed < maxOperations && getEnergy() >= energyPerTick
              && MekanismUtils.canFunction(this))
        {
            if (processor.processOnce() != RecipeProcessResultCE.PROCESSED)
            {
                break;
            }
            setEnergy(getEnergy() - energyPerTick);
            completed++;
        }
        clientEnergyUsed = completed * energyPerTick;
        setActive(completed > 0);
        if (completed > 0)
        {
            markDirty();
        }

        ejectOutputGas();
        prevEnergy = getEnergy();
        int redstoneLevel = getRedstoneLevel();
        if (redstoneLevel != currentRedstoneLevel)
        {
            currentRedstoneLevel = redstoneLevel;
            markDirty();
        }
    }

    private void transferInputContainer()
    {
        ItemStack stack = inventory[0];
        if (stack == null || !(stack.getItem() instanceof IGasItem))
        {
            return;
        }
        GasStack contained = ((IGasItem)stack.getItem()).getGas(stack);
        if (contained == null || !NuclearRecipeRegistry.containsInput(contained.getGas().getName()))
        {
            return;
        }
        Gas stored = toGas(processor.getInput());
        if (stored != null && stored != contained.getGas())
        {
            return;
        }
        GasStack removed = GasTransmission.removeGas(stack, stored, inputTank.getNeeded());
        if (removed != null && inputTank.receive(removed, true) > 0)
        {
            markDirty();
        }
    }

    private void transferOutputContainer()
    {
        ItemStack stack = inventory[1];
        GasStack stored = outputTank.getGas();
        if (stack == null || stored == null)
        {
            return;
        }
        int accepted = GasTransmission.addGas(stack, stored);
        if (accepted > 0)
        {
            outputTank.draw(accepted, true);
            markDirty();
        }
    }

    private void ejectOutputGas()
    {
        if (!configComponent.isEjecting(TransmissionType.GAS) || outputTank.getGas() == null)
        {
            return;
        }
        GasStack offered = outputTank.getGas().copy().withAmount(
              Math.min(GAS_OUTPUT, outputTank.getStored()));
        int emitted = GasTransmission.emit(offered, this,
              configComponent.getSidesForData(TransmissionType.GAS, facing, 2));
        if (emitted > 0)
        {
            outputTank.draw(emitted, true);
            markDirty();
        }
    }

    @Override
    public int receiveGas(ForgeDirection side, GasStack stack, boolean doTransfer)
    {
        if (stack == null || !canReceiveGas(side, stack.getGas()))
        {
            return 0;
        }
        int accepted = (int)processor.insertInput(
              new ChemicalStackCE(NuclearRecipeRegistry.type(stack.getGas()), stack.amount),
              doTransfer ? Action.EXECUTE : Action.SIMULATE);
        if (doTransfer && accepted > 0)
        {
            markDirty();
        }
        return accepted;
    }

    @Override
    @Deprecated
    public int receiveGas(ForgeDirection side, GasStack stack)
    {
        return receiveGas(side, stack, true);
    }

    @Override
    @Nullable
    public GasStack drawGas(ForgeDirection side, int amount, boolean doTransfer)
    {
        if (amount <= 0 || !canDrawGas(side, null))
        {
            return null;
        }
        IChemicalStackCE extracted = processor.extractOutput(amount,
              doTransfer ? Action.EXECUTE : Action.SIMULATE);
        Gas gas = toGas(extracted);
        if (doTransfer && extracted != null)
        {
            markDirty();
        }
        return extracted == null || gas == null ? null
              : new GasStack(gas, (int)extracted.getAmount());
    }

    @Override
    @Deprecated
    public GasStack drawGas(ForgeDirection side, int amount)
    {
        return drawGas(side, amount, true);
    }

    @Override
    public boolean canReceiveGas(ForgeDirection side, Gas gas)
    {
        Gas stored = toGas(processor.getInput());
        return gas != null && isInputSide(side)
              && NuclearRecipeRegistry.containsInput(gas.getName())
              && (stored == null || stored == gas) && inputTank.getNeeded() > 0;
    }

    @Override
    public boolean canDrawGas(ForgeDirection side, Gas gas)
    {
        Gas stored = toGas(processor.getOutput());
        return stored != null && isOutputSide(side) && (gas == null || gas == stored);
    }

    private boolean isInputSide(@Nullable ForgeDirection side)
    {
        return side == null || side == ForgeDirection.UNKNOWN
              || configComponent.getOutput(TransmissionType.GAS, side.ordinal(), facing)
                    .hasSlot(INPUT_TANK);
    }

    private boolean isOutputSide(@Nullable ForgeDirection side)
    {
        return side == null || side == ForgeDirection.UNKNOWN
              || configComponent.getOutput(TransmissionType.GAS, side.ordinal(), facing)
                    .hasSlot(OUTPUT_TANK);
    }

    @Override
    public boolean canTubeConnect(ForgeDirection side)
    {
        return isInputSide(side) || isOutputSide(side);
    }

    @Override
    @Nonnull
    public GasTankInfo[] getTankInfo()
    {
        return new GasTankInfo[] {inputTank, outputTank};
    }

    @Override
    public int getChemicalTankCount(@Nullable ForgeDirection side)
    {
        return 2;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank, @Nullable ForgeDirection side)
    {
        if (tank == INPUT_TANK)
        {
            return processor.getInputTank();
        }
        if (tank == OUTPUT_TANK)
        {
            return processor.getOutputTank();
        }
        throw new IndexOutOfBoundsException("Chemical tank index: " + tank);
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type, @Nullable ForgeDirection side)
    {
        return tank == INPUT_TANK && type instanceof MekGasChemicalType
              && canReceiveGas(side, ((MekGasChemicalType)type).getGas());
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type, @Nullable ForgeDirection side)
    {
        return tank == OUTPUT_TANK && type instanceof MekGasChemicalType
              && canDrawGas(side, ((MekGasChemicalType)type).getGas());
    }

    @Override
    public EnumSet<ForgeDirection> getConsumingSides()
    {
        return configComponent.getSidesForData(TransmissionType.ENERGY, facing, 1);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return side >= 0 && side < 6
              ? configComponent.getOutput(TransmissionType.ITEM, side, facing).availableSlots
              : InventoryUtils.EMPTY;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack == null)
        {
            return false;
        }
        if (slot == 0 && stack.getItem() instanceof IGasItem)
        {
            GasStack gas = ((IGasItem)stack.getItem()).getGas(stack);
            return gas != null && NuclearRecipeRegistry.containsInput(gas.getGas().getName());
        }
        if (slot == 1)
        {
            return stack.getItem() instanceof IGasItem
                  && ((IGasItem)stack.getItem()).canReceiveGas(stack, outputTank.getGasType());
        }
        if (slot == 2)
        {
            return ChargeUtils.canBeDischarged(stack);
        }
        if (slot == 3 && stack.getItem() instanceof IUpgradeItem)
        {
            return upgradeComponent.supports(((IUpgradeItem)stack.getItem()).getUpgradeType(stack));
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
        if (slot == 0 && stack.getItem() instanceof IGasItem)
        {
            return ((IGasItem)stack.getItem()).getGas(stack) == null;
        }
        if (slot == 1 && stack.getItem() instanceof IGasItem)
        {
            return !((IGasItem)stack.getItem()).canReceiveGas(stack, outputTank.getGasType());
        }
        return slot == 2 && ChargeUtils.canBeOutputted(stack, false);
    }

    @Override
    public boolean canSetFacing(int side)
    {
        return side != 0 && side != 1;
    }

    @Override
    public RedstoneControl getControlType()
    {
        return controlType;
    }

    @Override
    public void setControlType(RedstoneControl type)
    {
        controlType = type;
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
        Mekanism.packetHandler.sendToReceivers(
              new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())),
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
    public void recalculateUpgradables(Upgrade upgrade)
    {
        super.recalculateUpgradables(upgrade);
        if (upgrade == Upgrade.SPEED || upgrade == Upgrade.ENERGY)
        {
            energyPerTick = MekanismUtils.getEnergyPerTick(this, BASE_ENERGY_USAGE);
        }
        if (upgrade == Upgrade.ENERGY)
        {
            maxEnergy = MekanismUtils.getMaxEnergy(this, BASE_MAX_ENERGY);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        isActive = data.getBoolean("isActive");
        int control = data.getInteger("controlType");
        controlType = control >= 0 && control < RedstoneControl.values().length
              ? RedstoneControl.values()[control] : RedstoneControl.DISABLED;
        processor.readFromNBT(data.getCompoundTag("CentrifugeChemicals"), (kind, name) -> {
            Gas gas = GasRegistry.getGas(name);
            return gas == null ? null : NuclearRecipeRegistry.type(gas);
        });
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        data.setBoolean("isActive", isActive);
        data.setInteger("controlType", controlType.ordinal());
        NBTTagCompound chemicals = new NBTTagCompound();
        processor.writeToNBT(chemicals);
        data.setTag("CentrifugeChemicals", chemicals);
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            isActive = data.readBoolean();
            controlType = RedstoneControl.values()[data.readInt()];
            clientEnergyUsed = data.readDouble();
            readTankSnapshot(data, inputTank);
            readTankSnapshot(data, outputTank);
            MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        data.add(isActive);
        data.add(controlType.ordinal());
        data.add(clientEnergyUsed);
        addTankSnapshot(data, inputTank);
        addTankSnapshot(data, outputTank);
        return data;
    }

    private static void addTankSnapshot(ArrayList data, LongBackedMekGasTank tank)
    {
        GasStack gas = tank.getGas();
        data.add(gas != null);
        if (gas != null)
        {
            data.add(gas.getGas().getID());
            data.add(gas.amount);
        }
    }

    private static void readTankSnapshot(ByteBuf data, LongBackedMekGasTank tank)
    {
        if (data.readBoolean())
        {
            tank.setGas(new GasStack(GasRegistry.getGas(data.readInt()), data.readInt()));
        }
        else
        {
            tank.setGas(null);
        }
    }

    @Nullable
    private static Gas toGas(@Nullable IChemicalStackCE stack)
    {
        return stack != null && stack.getType() instanceof MekGasChemicalType
              ? ((MekGasChemicalType)stack.getType()).getGas() : null;
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
        return new Object[] {inputTank, outputTank};
    }

    @Override
    public List<String> getInfo(Upgrade upgrade)
    {
        return upgrade == Upgrade.SPEED
              ? upgrade.getExpScaledInfo(this) : upgrade.getMultScaledInfo(this);
    }

    public int getRedstoneLevel()
    {
        double fraction = (double)processor.getInputStored() / processor.getInputTank().getCapacity();
        return MathHelper.floor_float((float)(fraction * 14F)) + (fraction > 0 ? 1 : 0);
    }

    public double getProgress()
    {
        return isActive && worldObj != null ? 0.16D * (1 + worldObj.getTotalWorldTime() % 6) : 0;
    }

    @Override
    public String getInventoryName()
    {
        return LangUtils.localize("tile.IsotopicCentrifuge.name");
    }
}
