package mekanism.nuclear.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.EnumColor;
import mekanism.api.IConfigCardAccess;
import mekanism.api.TileNetworkList;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
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
import mekanism.ultimate.common.content.machine.ChemicalToChemicalRecipeProcessorCE;
import mekanism.ultimate.common.content.machine.RecipeProcessResultCE;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * 1.12 implementation of the modern Isotopic Centrifuge. The Ultimate
 * long-backed tanks are authoritative; the public GasTanks are display-only
 * snapshots used by the legacy GUI widgets.
 */
public class TileEntityIsotopicCentrifuge extends TileEntityMachine implements IGasHandler, IChemicalHandlerCE,
      ISideConfiguration, IConfigCardAccess, ITankManager, IUpgradeInfoHandler {

    public static final int MAX_GAS = 10_000;
    public static final int GAS_OUTPUT = 256;
    private static final int INPUT_TANK = 0;
    private static final int OUTPUT_TANK = 1;

    private final ChemicalToChemicalRecipeProcessorCE processor = new ChemicalToChemicalRecipeProcessorCE(
          NuclearRecipeRegistry.CENTRIFUGING, MAX_GAS, MAX_GAS);

    /** Client-side views for the legacy gauge API. */
    public final GasTank inputTank = new GasTank(MAX_GAS);
    public final GasTank outputTank = new GasTank(MAX_GAS);

    public final TileComponentConfig configComponent;
    public final TileComponentEjector ejectorComponent;
    public double clientEnergyUsed;
    private int currentRedstoneLevel;

    public TileEntityIsotopicCentrifuge() {
        super("machine.isotopiccentrifuge", "IsotopicCentrifuge", 80_000, 200, 3);
        inventory = NonNullList.withSize(4, ItemStack.EMPTY);

        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.ENERGY, TransmissionType.GAS);
        configComponent.addOutput(TransmissionType.ITEM, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Input", EnumColor.DARK_GREEN, new int[]{0}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Output", EnumColor.DARK_RED, new int[]{1}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Energy", EnumColor.DARK_BLUE, new int[]{2}));
        configComponent.setConfig(TransmissionType.ITEM, new byte[]{0, 3, 0, 0, 1, 2});

        configComponent.addOutput(TransmissionType.GAS, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.GAS, new SideData("Input", EnumColor.DARK_GREEN, new int[]{INPUT_TANK}));
        configComponent.addOutput(TransmissionType.GAS, new SideData("Output", EnumColor.DARK_RED, new int[]{OUTPUT_TANK}));
        configComponent.setConfig(TransmissionType.GAS, new byte[]{0, 0, 0, 0, 1, 2});
        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM, configComponent.getOutputs(TransmissionType.ITEM).get(2));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            return;
        }

        ChargeUtils.discharge(2, this);
        transferInputContainer();
        transferOutputContainer();

        int completed = 0;
        int maxOperations = 1 << upgradeComponent.getUpgrades(Upgrade.SPEED);
        while (completed < maxOperations && getEnergy() >= energyPerTick && MekanismUtils.canFunction(this)) {
            if (processor.processOnce() != RecipeProcessResultCE.PROCESSED) {
                break;
            }
            setEnergy(getEnergy() - energyPerTick);
            completed++;
        }
        clientEnergyUsed = completed * energyPerTick;
        setActive(completed > 0);
        if (completed > 0) {
            markDirty();
        }

        ejectOutputGas();
        prevEnergy = getEnergy();
        int redstoneLevel = getRedstoneLevel();
        if (redstoneLevel != currentRedstoneLevel) {
            currentRedstoneLevel = redstoneLevel;
            world.updateComparatorOutputLevel(pos, getBlockType());
        }
    }

    private void transferInputContainer() {
        ItemStack stack = inventory.get(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof IGasItem)) {
            return;
        }
        GasStack contained = ((IGasItem) stack.getItem()).getGas(stack);
        if (contained == null || !NuclearRecipeRegistry.containsInput(contained.getGas().getName())) {
            return;
        }
        IChemicalStackCE stored = processor.getInput();
        Gas current = toGas(stored);
        if (current != null && current != contained.getGas()) {
            return;
        }
        int needed = (int) Math.min(Integer.MAX_VALUE, processor.getInputTank().getCapacity() - processor.getInputStored());
        GasStack removed = GasUtils.removeGas(stack, current, needed);
        if (removed == null) {
            return;
        }
        int accepted = receiveGas(null, removed, true);
        if (accepted < removed.amount) {
            GasUtils.addGas(stack, removed.copy().withAmount(removed.amount - accepted));
        }
    }

    private void transferOutputContainer() {
        ItemStack stack = inventory.get(1);
        IChemicalStackCE stored = processor.getOutput();
        Gas gas = toGas(stored);
        if (stack.isEmpty() || gas == null) {
            return;
        }
        int offered = (int) Math.min(Integer.MAX_VALUE, stored.getAmount());
        int accepted = GasUtils.addGas(stack, new GasStack(gas, offered));
        if (accepted > 0) {
            processor.extractOutput(accepted, Action.EXECUTE);
            markDirty();
        }
    }

    private void ejectOutputGas() {
        if (!configComponent.isEjecting(TransmissionType.GAS)) {
            return;
        }
        IChemicalStackCE stored = processor.getOutput();
        Gas gas = toGas(stored);
        if (gas == null) {
            return;
        }
        int amount = (int) Math.min(GAS_OUTPUT, stored.getAmount());
        int emitted = GasUtils.emit(new GasStack(gas, amount), this,
              configComponent.getSidesForData(TransmissionType.GAS, facing, 2));
        if (emitted > 0) {
            processor.extractOutput(emitted, Action.EXECUTE);
            markDirty();
        }
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
        if (stack == null || !canReceiveGas(side, stack.getGas())) {
            return 0;
        }
        return (int) processor.insertInput(new ChemicalStackCE(type(stack.getGas()), stack.amount),
              doTransfer ? Action.EXECUTE : Action.SIMULATE);
    }

    @Nullable
    @Override
    public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer) {
        if (amount <= 0 || !canDrawGas(side, null)) {
            return null;
        }
        IChemicalStackCE extracted = processor.extractOutput(amount, doTransfer ? Action.EXECUTE : Action.SIMULATE);
        Gas gas = toGas(extracted);
        return extracted == null || gas == null ? null : new GasStack(gas, (int) extracted.getAmount());
    }

    @Override
    public boolean canReceiveGas(EnumFacing side, Gas gas) {
        return gas != null && isInputSide(side) && NuclearRecipeRegistry.containsInput(gas.getName())
              && canStore(processor.getInput(), gas) && processor.getInputStored() < processor.getInputTank().getCapacity();
    }

    @Override
    public boolean canDrawGas(EnumFacing side, Gas gas) {
        IChemicalStackCE stored = processor.getOutput();
        Gas storedGas = toGas(stored);
        return storedGas != null && isOutputSide(side) && (gas == null || gas == storedGas);
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo() {
        syncDisplayTanks();
        return new GasTankInfo[]{inputTank, outputTank};
    }

    @Override
    public int getChemicalTankCount(@Nullable EnumFacing side) {
        return 2;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tank, @Nullable EnumFacing side) {
        if (tank == INPUT_TANK) {
            return processor.getInputTank();
        } else if (tank == OUTPUT_TANK) {
            return processor.getOutputTank();
        }
        throw new IndexOutOfBoundsException("Chemical tank index: " + tank);
    }

    @Override
    public boolean canInsertChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side) {
        return tank == INPUT_TANK && type instanceof MekGasChemicalType
              && canReceiveGas(side, ((MekGasChemicalType) type).getGas());
    }

    @Override
    public boolean canExtractChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side) {
        return tank == OUTPUT_TANK && type instanceof MekGasChemicalType
              && canDrawGas(side, ((MekGasChemicalType) type).getGas());
    }

    private boolean isInputSide(@Nullable EnumFacing side) {
        return side == null || configComponent.getOutput(TransmissionType.GAS, side, facing).hasSlot(INPUT_TANK);
    }

    private boolean isOutputSide(@Nullable EnumFacing side) {
        return side == null || configComponent.getOutput(TransmissionType.GAS, side, facing).hasSlot(OUTPUT_TANK);
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
        if (slot == 0) {
            if (!(stack.getItem() instanceof IGasItem)) {
                return false;
            }
            GasStack gas = ((IGasItem) stack.getItem()).getGas(stack);
            return gas != null && NuclearRecipeRegistry.containsInput(gas.getGas().getName());
        } else if (slot == 1) {
            return stack.getItem() instanceof IGasItem && ((IGasItem) stack.getItem()).canReceiveGas(stack, toGas(processor.getOutput()));
        } else if (slot == 2) {
            return ChargeUtils.canBeDischarged(stack);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing side) {
        if (slot == 0) {
            return stack.getItem() instanceof IGasItem && ((IGasItem) stack.getItem()).getGas(stack) == null;
        } else if (slot == 1) {
            return stack.getItem() instanceof IGasItem && !((IGasItem) stack.getItem()).canReceiveGas(stack, toGas(processor.getOutput()));
        } else if (slot == 2) {
            return ChargeUtils.canBeOutputted(stack, false);
        }
        return false;
    }

    @Override
    public void recalculateUpgradables(Upgrade upgrade) {
        super.recalculateUpgradables(upgrade);
        if (upgrade == Upgrade.SPEED || upgrade == Upgrade.ENERGY) {
            energyPerTick = MekanismUtils.getEnergyPerTick(this, BASE_ENERGY_PER_TICK);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        processor.readFromNBT(data.getCompoundTag("CentrifugeChemicals"), (kind, name) -> {
            Gas gas = GasRegistry.getGas(name);
            return gas == null ? null : new MekGasChemicalType(gas, NuclearChemicals.isRadioactive(name));
        });
        syncDisplayTanks();
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagCompound chemicals = new NBTTagCompound();
        processor.writeToNBT(chemicals);
        data.setTag("CentrifugeChemicals", chemicals);
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        super.handlePacketData(data);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            clientEnergyUsed = data.readDouble();
            readDisplayTank(data, inputTank);
            readDisplayTank(data, outputTank);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(clientEnergyUsed);
        addTankSnapshot(data, processor.getInput());
        addTankSnapshot(data, processor.getOutput());
        return data;
    }

    private static void addTankSnapshot(TileNetworkList data, @Nullable IChemicalStackCE stack) {
        Gas gas = toGas(stack);
        data.add(gas == null ? "" : gas.getName());
        data.add(stack == null ? 0L : stack.getAmount());
    }

    private static void readDisplayTank(ByteBuf data, GasTank tank) {
        String name = PacketHandler.readString(data);
        long amount = data.readLong();
        Gas gas = name.isEmpty() ? null : GasRegistry.getGas(name);
        tank.setGas(gas == null || amount <= 0 ? null : new GasStack(gas, (int) Math.min(Integer.MAX_VALUE, amount)));
    }

    private void syncDisplayTanks() {
        setDisplayTank(inputTank, processor.getInput());
        setDisplayTank(outputTank, processor.getOutput());
    }

    private static void setDisplayTank(GasTank tank, @Nullable IChemicalStackCE stack) {
        Gas gas = toGas(stack);
        tank.setGas(gas == null || stack == null ? null : new GasStack(gas, (int) Math.min(Integer.MAX_VALUE, stack.getAmount())));
    }

    @Nullable
    private static Gas toGas(@Nullable IChemicalStackCE stack) {
        return stack != null && stack.getType() instanceof MekGasChemicalType
              ? ((MekGasChemicalType) stack.getType()).getGas() : null;
    }

    private static MekGasChemicalType type(Gas gas) {
        return new MekGasChemicalType(gas, NuclearChemicals.isRadioactive(gas.getName()));
    }

    private static boolean canStore(@Nullable IChemicalStackCE stored, Gas gas) {
        return stored == null || gas == toGas(stored);
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
        return configComponent.isCapabilityDisabled(capability, side, facing) || super.isCapabilityDisabled(capability, side);
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
        // Legacy gauge-droppers only understand mutable int-backed GasTanks.
        // Returning null disables that unsafe path; containers and pipes use
        // the authoritative long-backed handlers above.
        return null;
    }

    @Override
    public List<String> getInfo(Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? upgrade.getExpScaledInfo(this) : upgrade.getMultScaledInfo(this);
    }

    @Nonnull
    @Override
    public String getName() {
        return LangUtils.localize("tile.IsotopicCentrifuge.name");
    }

    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(processor.getInputStored(), processor.getInputTank().getCapacity());
    }

    public double getProgress() {
        return getActive() ? .16 * (1 + world.getTotalWorldTime() % 6) : 0;
    }
}
