package mekanism.nuclear.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.TileNetworkList;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.integration.forgeenergy.ForgeEnergyIntegration;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.GasUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.config.NuclearSPSConfig;
import mekanism.nuclear.common.content.sps.SPSFormationManager;
import mekanism.nuclear.common.content.sps.SPSState;
import mekanism.nuclear.common.content.sps.SPSStatus;
import mekanism.nuclear.common.content.sps.SPSValidator;
import mekanism.nuclear.common.radiation.RadiationManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;

/** A physical SPS port and the server-authoritative structure controller node. */
public class TileEntitySPSPort extends TileEntityBasicBlock implements IGasHandler,
      IStrictEnergyAcceptor, IStrictEnergyStorage, IEnergyStorage {

    private static final String NBT_SPS = "SPS";
    private static final String NBT_CONTROLLER = "Controller";
    private static final String NBT_OUTPUT = "Output";
    private static final String NBT_PORT_ENERGY = "PortEnergy";

    private final SPSState spsState = new SPSState();
    private BlockPos controllerPos;
    private boolean output;
    private boolean validationQueued;
    private long portEnergy;
    private SPSStatus clientStatus = SPSStatus.EMPTY;
    private boolean presentationDirty;

    @Override
    public void onLoad() {
        super.onLoad();
        if (!world.isRemote) {
            SPSFormationManager.INSTANCE.register(this);
            queueValidation();
        }
    }

    @Override
    public void onChunkUnload() {
        SPSFormationManager.INSTANCE.unregister(this);
        super.onChunkUnload();
    }

    @Override
    public void invalidate() {
        SPSFormationManager.INSTANCE.unregister(this);
        super.invalidate();
    }

    @Override
    public void onUpdate() {
        if (world.isRemote) {
            return;
        }
        if (validationQueued) {
            validationQueued = false;
            validateKnownStructure();
        }
        TileEntitySPSPort controller = getControllerTile();
        if (controller == null || !controller.spsState.isFormed()) {
            syncPresentationIfNeeded();
            return;
        }
        supplyCoilEnergy(controller);
        if (controller == this) {
            SPSState.TickResult tick = spsState.tick();
            if (tick.isChanged()) {
                stateChanged();
            }
        }
        ejectAntimatter(controller);
        syncPresentationIfNeeded();
    }

    private void supplyCoilEnergy(TileEntitySPSPort controller) {
        if (portEnergy <= 0 || !controller.spsState.canOperate()
              || !controller.spsState.hasCoilForPort(pos)) {
            return;
        }
        long accepted = controller.spsState.addPendingEnergy(portEnergy);
        if (accepted > 0) {
            portEnergy -= accepted;
            portStateChanged();
            if (controller != this) {
                controller.stateChanged();
            }
        }
    }

    private void ejectAntimatter(TileEntitySPSPort controller) {
        if (!output || controller.spsState.getAntimatter() <= 0) {
            return;
        }
        EnumFacing outward = getOutwardSide(controller.spsState);
        if (outward == null) {
            return;
        }
        int emitted = GasUtils.emit(new GasStack(NuclearChemicals.Antimatter,
              saturatingInt(controller.spsState.getAntimatter())), this, Collections.singleton(outward));
        if (emitted > 0) {
            controller.spsState.extractAntimatter(emitted, true);
            controller.stateChanged();
        }
    }

    public SPSValidator.Result validateStructure() {
        return applyValidation(SPSValidator.validate(world, pos));
    }

    private void validateKnownStructure() {
        BlockPos min = spsState.getMin();
        if (min != null) {
            applyValidation(SPSValidator.validateAtMin(world, min));
        }
    }

    private SPSValidator.Result applyValidation(SPSValidator.Result result) {
        if (!result.isFormed()) {
            invalidateKnownStructure();
            return result;
        }

        SPSState preserved = snapshotExistingController(result);
        BlockPos controller = result.getPorts().get(0);
        for (BlockPos portPos : result.getPorts()) {
            TileEntity tile = world.getTileEntity(portPos);
            if (tile instanceof TileEntitySPSPort) {
                TileEntitySPSPort port = (TileEntitySPSPort) tile;
                port.spsState.applyStructure(result);
                port.controllerPos = controller;
                if (portPos.equals(controller)) {
                    if (preserved != null) {
                        port.spsState.copyOperationalFrom(preserved);
                    }
                } else {
                    port.spsState.clearOperationalState();
                }
                port.validationQueued = false;
                port.stateChanged();
            }
        }
        return result;
    }

    @Nullable
    private SPSState snapshotExistingController(SPSValidator.Result result) {
        TileEntitySPSPort source = null;
        for (BlockPos portPos : result.getPorts()) {
            TileEntity tile = world.getTileEntity(portPos);
            if (tile instanceof TileEntitySPSPort) {
                TileEntitySPSPort port = (TileEntitySPSPort) tile;
                if (port.isController() && port.spsState.hasBounds()) {
                    source = port;
                    break;
                }
            }
        }
        if (source == null && isController() && spsState.hasBounds()) {
            source = this;
        }
        if (source == null) {
            return null;
        }
        SPSState snapshot = new SPSState();
        snapshot.readFromNBT(source.spsState.writeToNBT(new NBTTagCompound()));
        return snapshot;
    }

    private void invalidateKnownStructure() {
        spsState.setUnformed();
        if (spsState.getPorts().isEmpty()) {
            stateChanged();
            return;
        }
        for (BlockPos portPos : spsState.getPorts()) {
            TileEntity tile = world.getTileEntity(portPos);
            if (tile instanceof TileEntitySPSPort) {
                TileEntitySPSPort port = (TileEntitySPSPort) tile;
                port.spsState.setUnformed();
                port.validationQueued = false;
                port.stateChanged();
            }
        }
    }

    /**
     * Moves authoritative contents before the selected controller port is
     * removed. If no linked port survives, radioactive input is released to
     * the persisted radiation system instead of being silently deleted.
     */
    public void prepareForRemoval() {
        if (world == null || world.isRemote || !isController() || !spsState.hasBounds()) {
            return;
        }
        TileEntitySPSPort successor = null;
        for (BlockPos portPos : spsState.getPorts()) {
            if (portPos.equals(pos) || !world.isBlockLoaded(portPos)) {
                continue;
            }
            TileEntity tile = world.getTileEntity(portPos);
            if (tile instanceof TileEntitySPSPort) {
                successor = (TileEntitySPSPort) tile;
                break;
            }
        }
        if (successor == null) {
            long released = spsState.extractPolonium(Long.MAX_VALUE, true);
            RadiationManager.INSTANCE.release(world, pos, NuclearChemicals.Polonium, released);
            spsState.clearOperationalState();
            stateChanged();
            return;
        }

        SPSState snapshot = new SPSState();
        snapshot.readFromNBT(spsState.writeToNBT(new NBTTagCompound()));
        BlockPos successorPos = successor.getPos();
        for (BlockPos portPos : spsState.getPorts()) {
            if (portPos.equals(pos) || !world.isBlockLoaded(portPos)) {
                continue;
            }
            TileEntity tile = world.getTileEntity(portPos);
            if (tile instanceof TileEntitySPSPort) {
                TileEntitySPSPort port = (TileEntitySPSPort) tile;
                port.controllerPos = successorPos;
                port.spsState.setUnformed();
                if (port == successor) {
                    port.spsState.copyOperationalFrom(snapshot);
                    port.validationQueued = true;
                } else {
                    port.spsState.clearOperationalState();
                    port.validationQueued = false;
                }
                port.stateChanged();
            }
        }
        spsState.clearOperationalState();
    }

    public void queueValidation() {
        if (spsState.hasBounds()) {
            TileEntitySPSPort controller = getControllerTile();
            if (controller != null) {
                controller.validationQueued = true;
            } else {
                validationQueued = true;
            }
        }
    }

    public boolean watches(BlockPos changedPos) {
        return spsState.watches(changedPos);
    }

    public boolean watchesChunk(int chunkX, int chunkZ) {
        return spsState.watchesChunk(chunkX, chunkZ);
    }

    public SPSState getSPSState() {
        return spsState;
    }

    @Nullable
    public BlockPos getControllerPos() {
        return controllerPos;
    }

    @Nullable
    public TileEntitySPSPort getControllerTile() {
        if (world == null || controllerPos == null || !world.isBlockLoaded(controllerPos)) {
            return null;
        }
        TileEntity tile = world.getTileEntity(controllerPos);
        return tile instanceof TileEntitySPSPort ? (TileEntitySPSPort) tile : null;
    }

    @Nullable
    private SPSState getControllerState() {
        TileEntitySPSPort controller = getControllerTile();
        return controller != null && controller.spsState.isFormed() ? controller.spsState : null;
    }

    public boolean isController() {
        return controllerPos != null && pos.equals(controllerPos);
    }

    public SPSStatus getStatus() {
        if (world != null && world.isRemote) {
            return clientStatus;
        }
        return SPSStatus.from(getControllerState(), isController(), portEnergy, getPortEnergyCapacity());
    }

    public boolean isOutput() {
        return output;
    }

    public boolean toggleOutput() {
        setOutput(!output);
        return output;
    }

    public void setOutput(boolean output) {
        if (this.output != output) {
            this.output = output;
            stateChanged();
            if (world != null && !world.isRemote) {
                Mekanism.packetHandler.sendUpdatePacket(this);
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    }

    private void stateChanged() {
        markDirty();
        if (world != null && !world.isRemote) {
            MekanismUtils.saveChunk(this);
            if (isController()) {
                presentationDirty = true;
            }
            if (isController() && spsState.isFormed()) {
                for (BlockPos port : spsState.getPorts()) {
                    world.updateComparatorOutputLevel(port, getBlockType());
                }
            }
        }
    }

    private void syncPresentationIfNeeded() {
        if (presentationDirty && isController() && (ticker & 1) == 0) {
            presentationDirty = false;
            Mekanism.packetHandler.sendUpdatePacket(this);
        }
    }

    private void portStateChanged() {
        markDirty();
        if (world != null && !world.isRemote) {
            MekanismUtils.saveChunk(this);
        }
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
        SPSState state = getControllerState();
        if (state == null || stack == null || stack.getGas() == null || stack.amount <= 0
              || !canReceiveGas(side, stack.getGas())) {
            return 0;
        }
        long accepted = state.insertPolonium(stack.amount, doTransfer);
        if (doTransfer && accepted > 0) {
            getControllerTile().stateChanged();
        }
        return (int) accepted;
    }

    @Nullable
    @Override
    public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer) {
        SPSState state = getControllerState();
        if (state == null || amount <= 0 || !output) {
            return null;
        }
        long extracted = state.extractAntimatter(amount, doTransfer);
        if (doTransfer && extracted > 0) {
            getControllerTile().stateChanged();
        }
        return extracted <= 0 ? null : new GasStack(NuclearChemicals.Antimatter, (int) extracted);
    }

    @Override
    public boolean canReceiveGas(EnumFacing side, Gas gas) {
        SPSState state = getControllerState();
        return !output && state != null && sameGas(gas, NuclearChemicals.Polonium)
              && state.getPolonium() < state.getPoloniumCapacity();
    }

    @Override
    public boolean canDrawGas(EnumFacing side, Gas gas) {
        SPSState state = getControllerState();
        return output && state != null && state.getAntimatter() > 0
              && (gas == null || sameGas(gas, NuclearChemicals.Antimatter));
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo() {
        SPSState state = getControllerState();
        if (state == null) {
            return IGasHandler.NONE;
        }
        return output
              ? new GasTankInfo[]{gasInfo(NuclearChemicals.Antimatter, state.getAntimatter(),
                    state.getAntimatterCapacity())}
              : new GasTankInfo[]{gasInfo(NuclearChemicals.Polonium, state.getPolonium(),
                    state.getPoloniumCapacity())};
    }

    public long getPortEnergy() {
        return portEnergy;
    }

    public long getPortEnergyCapacity() {
        return NuclearSPSConfig.getPortEnergyCapacity();
    }

    public long insertPortEnergy(long amount, boolean execute) {
        long requested = Math.max(0, amount);
        long accepted = Math.min(requested, Math.max(0, getPortEnergyCapacity() - portEnergy));
        if (execute && accepted > 0) {
            portEnergy += accepted;
            portStateChanged();
        }
        return accepted;
    }

    @Override
    public double acceptEnergy(EnumFacing side, double amount, boolean simulate) {
        if (!Double.isFinite(amount) || amount < 1) {
            return 0;
        }
        long requested = amount >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) Math.floor(amount);
        return insertPortEnergy(requested, !simulate);
    }

    @Override
    public boolean canReceiveEnergy(EnumFacing side) {
        return portEnergy < getPortEnergyCapacity();
    }

    @Override
    public double getEnergy() {
        return portEnergy;
    }

    @Override
    public void setEnergy(double energy) {
        long clamped = !Double.isFinite(energy) || energy <= 0 ? 0
              : energy >= getPortEnergyCapacity() ? getPortEnergyCapacity() : (long) Math.floor(energy);
        if (portEnergy != clamped) {
            portEnergy = clamped;
            portStateChanged();
        }
    }

    @Override
    public double getMaxEnergy() {
        return getPortEnergyCapacity();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0;
        }
        double accepted = acceptEnergy(null, ForgeEnergyIntegration.fromForge(maxReceive), simulate);
        return ForgeEnergyIntegration.toForge(accepted);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return ForgeEnergyIntegration.toForge(portEnergy);
    }

    @Override
    public int getMaxEnergyStored() {
        return ForgeEnergyIntegration.toForge(getPortEnergyCapacity());
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return canReceiveEnergy(null);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing side) {
        if (capability == Capabilities.ENERGY_STORAGE_CAPABILITY
              || capability == Capabilities.ENERGY_ACCEPTOR_CAPABILITY
              || capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        if (capability == Capabilities.GAS_HANDLER_CAPABILITY && getControllerState() != null) {
            return true;
        }
        return super.hasCapability(capability, side);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing side) {
        if (capability == Capabilities.ENERGY_STORAGE_CAPABILITY
              || capability == Capabilities.ENERGY_ACCEPTOR_CAPABILITY) {
            return (T) this;
        }
        if (capability == Capabilities.GAS_HANDLER_CAPABILITY && getControllerState() != null) {
            return (T) this;
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing facing) {
        return false;
    }

    @Override
    public void handlePacketData(ByteBuf data) {
        super.handlePacketData(data);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            boolean oldOutput = output;
            output = data.readBoolean();
            if (oldOutput != output) {
                MekanismUtils.updateBlock(world, pos);
            }
            clientStatus = SPSStatus.read(data);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(output);
        SPSStatus.from(getControllerState(), isController(), portEnergy, getPortEnergyCapacity()).write(data);
        return data;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        SPSStatus status = getStatus();
        BlockPos min = status.getMin();
        BlockPos max = status.getMax();
        return status.isFormed() && min != null && max != null
              ? new AxisAlignedBB(min, max.add(1, 1, 1)) : super.getRenderBoundingBox();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(NBT_SPS, 10)) {
            spsState.readFromNBT(tag.getCompoundTag(NBT_SPS));
        }
        controllerPos = tag.hasKey(NBT_CONTROLLER, 10) ? readPos(tag.getCompoundTag(NBT_CONTROLLER)) : null;
        output = tag.getBoolean(NBT_OUTPUT);
        portEnergy = Math.min(Math.max(0, tag.getLong(NBT_PORT_ENERGY)), getPortEnergyCapacity());
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag(NBT_SPS, spsState.writeToNBT(new NBTTagCompound()));
        if (controllerPos != null) {
            tag.setTag(NBT_CONTROLLER, writePos(controllerPos));
        }
        tag.setBoolean(NBT_OUTPUT, output);
        tag.setLong(NBT_PORT_ENERGY, portEnergy);
        return tag;
    }

    @Nullable
    private EnumFacing getOutwardSide(SPSState state) {
        BlockPos min = state.getMin();
        BlockPos max = state.getMax();
        if (min == null || max == null) {
            return null;
        }
        if (pos.getX() == min.getX()) {
            return EnumFacing.WEST;
        } else if (pos.getX() == max.getX()) {
            return EnumFacing.EAST;
        } else if (pos.getY() == min.getY()) {
            return EnumFacing.DOWN;
        } else if (pos.getY() == max.getY()) {
            return EnumFacing.UP;
        } else if (pos.getZ() == min.getZ()) {
            return EnumFacing.NORTH;
        } else if (pos.getZ() == max.getZ()) {
            return EnumFacing.SOUTH;
        }
        return null;
    }

    private static GasTankInfo gasInfo(@Nullable Gas gas, long stored, long capacity) {
        final int visibleStored = saturatingInt(stored);
        final int visibleCapacity = saturatingInt(capacity);
        return new GasTankInfo() {
            @Nullable
            @Override
            public GasStack getGas() {
                return gas == null || visibleStored <= 0 ? null : new GasStack(gas, visibleStored);
            }

            @Override
            public int getStored() {
                return visibleStored;
            }

            @Override
            public int getMaxGas() {
                return visibleCapacity;
            }
        };
    }

    private static boolean sameGas(@Nullable Gas first, @Nullable Gas second) {
        return first != null && second != null && first.getName().equals(second.getName());
    }

    private static int saturatingInt(long amount) {
        return amount >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.max(0, amount);
    }

    private static NBTTagCompound writePos(BlockPos pos) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("X", pos.getX());
        tag.setInteger("Y", pos.getY());
        tag.setInteger("Z", pos.getZ());
        return tag;
    }

    private static BlockPos readPos(NBTTagCompound tag) {
        return new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
    }
}
