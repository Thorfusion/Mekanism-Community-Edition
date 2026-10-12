package mekanism.nuclear.common.tile;

import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.general;
import mekanism.api.Range4D;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.GasTransmission;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasTankInfoProvider;
import mekanism.api.gas.ITubeConnection;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.config.NuclearSPSConfig;
import mekanism.nuclear.common.content.sps.SPSFormationManager;
import mekanism.nuclear.common.content.sps.SPSPosition;
import mekanism.nuclear.common.content.sps.SPSState;
import mekanism.nuclear.common.content.sps.SPSStatus;
import mekanism.nuclear.common.content.sps.SPSValidator;
import mekanism.nuclear.common.radiation.RadiationManager;
import mekanism.nuclear.common.radiation.RadiationPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

/** Physical SPS port and server-authoritative structure controller node. */
public class TileEntitySPSPort extends TileEntityBasicBlock
      implements IGasHandler, IGasTankInfoProvider, ITubeConnection,
      IStrictEnergyAcceptor, IStrictEnergyStorage, IEnergyReceiver
{
    private static final String NBT_SPS = "SPS";
    private static final String NBT_CONTROLLER = "Controller";
    private static final String NBT_OUTPUT = "Output";
    private static final String NBT_PORT_ENERGY = "PortEnergy";

    private final SPSState spsState = new SPSState();
    private SPSPosition controllerPos;
    private boolean output;
    private boolean validationQueued;
    private long portEnergy;
    private SPSStatus clientStatus = SPSStatus.EMPTY;
    private boolean presentationDirty;

    @Override
    public void validate()
    {
        super.validate();
        if (worldObj != null && !worldObj.isRemote)
        {
            SPSFormationManager.INSTANCE.register(this);
            validationQueued = spsState.hasBounds();
        }
    }

    @Override
    public void onChunkUnload()
    {
        SPSFormationManager.INSTANCE.unregister(this);
        super.onChunkUnload();
    }

    @Override
    public void invalidate()
    {
        SPSFormationManager.INSTANCE.unregister(this);
        super.invalidate();
    }

    @Override
    public void onUpdate()
    {
        if (worldObj.isRemote)
        {
            return;
        }
        if (validationQueued)
        {
            validationQueued = false;
            validateKnownStructure();
        }
        TileEntitySPSPort controller = getControllerTile();
        if (controller == null || !controller.spsState.isFormed())
        {
            syncPresentationIfNeeded();
            return;
        }
        supplyCoilEnergy(controller);
        if (controller == this)
        {
            SPSState.TickResult tick = spsState.tick();
            if (tick.isChanged())
            {
                stateChanged();
            }
        }
        ejectAntimatter(controller);
        syncPresentationIfNeeded();
    }

    private void supplyCoilEnergy(TileEntitySPSPort controller)
    {
        if (portEnergy <= 0 || !controller.spsState.canOperate()
              || !controller.spsState.hasCoilForPort(position()))
        {
            return;
        }
        long accepted = controller.spsState.addPendingEnergy(portEnergy);
        if (accepted > 0)
        {
            portEnergy -= accepted;
            portStateChanged();
            if (controller != this)
            {
                controller.stateChanged();
            }
        }
    }

    private void ejectAntimatter(TileEntitySPSPort controller)
    {
        if (!output || controller.spsState.getAntimatter() <= 0)
        {
            return;
        }
        ForgeDirection outward = getOutwardSide(controller.spsState);
        if (outward == ForgeDirection.UNKNOWN)
        {
            return;
        }
        int emitted = GasTransmission.emit(new GasStack(
              NuclearChemicals.Antimatter,
              saturatingInt(controller.spsState.getAntimatter())), this,
              Collections.singleton(outward));
        if (emitted > 0)
        {
            controller.spsState.extractAntimatter(emitted, true);
            controller.stateChanged();
        }
    }

    public SPSValidator.Result validateStructure()
    {
        return applyValidation(SPSValidator.validate(worldObj, position()));
    }

    private void validateKnownStructure()
    {
        SPSPosition min = spsState.getMin();
        if (min != null)
        {
            applyValidation(SPSValidator.validateAtMin(worldObj, min));
        }
    }

    private SPSValidator.Result applyValidation(SPSValidator.Result result)
    {
        if (!result.isFormed())
        {
            invalidateKnownStructure();
            return result;
        }

        SPSState preserved = snapshotExistingController(result);
        SPSPosition controller = result.getPorts().get(0);
        for (SPSPosition portPos : result.getPorts())
        {
            TileEntity tile = tileAt(portPos);
            if (tile instanceof TileEntitySPSPort)
            {
                TileEntitySPSPort port = (TileEntitySPSPort)tile;
                port.spsState.applyStructure(result);
                port.controllerPos = controller;
                if (portPos.equals(controller))
                {
                    if (preserved != null)
                    {
                        port.spsState.copyOperationalFrom(preserved);
                    }
                }
                else
                {
                    port.spsState.clearOperationalState();
                }
                port.validationQueued = false;
                port.stateChanged();
                port.sendUpdatePacket();
            }
        }
        return result;
    }

    @Nullable
    private SPSState snapshotExistingController(SPSValidator.Result result)
    {
        TileEntitySPSPort source = null;
        for (SPSPosition portPos : result.getPorts())
        {
            TileEntity tile = tileAt(portPos);
            if (tile instanceof TileEntitySPSPort)
            {
                TileEntitySPSPort port = (TileEntitySPSPort)tile;
                if (port.isController() && port.spsState.hasBounds())
                {
                    source = port;
                    break;
                }
            }
        }
        if (source == null && isController() && spsState.hasBounds())
        {
            source = this;
        }
        if (source == null)
        {
            return null;
        }
        SPSState snapshot = new SPSState();
        snapshot.readFromNBT(source.spsState.writeToNBT(
              new NBTTagCompound()));
        return snapshot;
    }

    private void invalidateKnownStructure()
    {
        spsState.setUnformed();
        if (spsState.getPorts().isEmpty())
        {
            stateChanged();
            return;
        }
        for (SPSPosition portPos : spsState.getPorts())
        {
            TileEntity tile = tileAt(portPos);
            if (tile instanceof TileEntitySPSPort)
            {
                TileEntitySPSPort port = (TileEntitySPSPort)tile;
                port.spsState.setUnformed();
                port.validationQueued = false;
                port.stateChanged();
                port.sendUpdatePacket();
            }
        }
    }

    public void prepareForRemoval()
    {
        if (worldObj == null || worldObj.isRemote || !isController()
              || !spsState.hasBounds())
        {
            return;
        }
        TileEntitySPSPort successor = null;
        for (SPSPosition portPos : spsState.getPorts())
        {
            if (portPos.equals(position()) || !blockExists(portPos))
            {
                continue;
            }
            TileEntity tile = tileAt(portPos);
            if (tile instanceof TileEntitySPSPort)
            {
                successor = (TileEntitySPSPort)tile;
                break;
            }
        }
        if (successor == null)
        {
            long released = spsState.extractPolonium(Long.MAX_VALUE, true);
            RadiationManager.INSTANCE.release(worldObj,
                  new RadiationPosition(xCoord, yCoord, zCoord),
                  NuclearChemicals.Polonium, released);
            spsState.clearOperationalState();
            stateChanged();
            return;
        }

        SPSState snapshot = new SPSState();
        snapshot.readFromNBT(spsState.writeToNBT(new NBTTagCompound()));
        SPSPosition successorPos = successor.position();
        for (SPSPosition portPos : spsState.getPorts())
        {
            if (portPos.equals(position()) || !blockExists(portPos))
            {
                continue;
            }
            TileEntity tile = tileAt(portPos);
            if (tile instanceof TileEntitySPSPort)
            {
                TileEntitySPSPort port = (TileEntitySPSPort)tile;
                port.controllerPos = successorPos;
                port.spsState.setUnformed();
                if (port == successor)
                {
                    port.spsState.copyOperationalFrom(snapshot);
                    port.validationQueued = true;
                }
                else
                {
                    port.spsState.clearOperationalState();
                    port.validationQueued = false;
                }
                port.stateChanged();
                port.sendUpdatePacket();
            }
        }
        spsState.clearOperationalState();
    }

    public void queueValidation()
    {
        if (spsState.hasBounds())
        {
            TileEntitySPSPort controller = getControllerTile();
            if (controller != null)
            {
                controller.validationQueued = true;
            }
            else
            {
                validationQueued = true;
            }
        }
    }

    public boolean watches(SPSPosition changedPosition)
    {
        return spsState.watches(changedPosition);
    }

    public boolean watchesChunk(int chunkX, int chunkZ)
    {
        return spsState.watchesChunk(chunkX, chunkZ);
    }

    public SPSState getSPSState()
    {
        return spsState;
    }

    @Nullable
    public SPSPosition getControllerPos()
    {
        return controllerPos;
    }

    @Nullable
    public TileEntitySPSPort getControllerTile()
    {
        if (worldObj == null || controllerPos == null
              || !blockExists(controllerPos))
        {
            return null;
        }
        TileEntity tile = tileAt(controllerPos);
        return tile instanceof TileEntitySPSPort
              ? (TileEntitySPSPort)tile : null;
    }

    @Nullable
    private SPSState getControllerState()
    {
        TileEntitySPSPort controller = getControllerTile();
        return controller != null && controller.spsState.isFormed()
              ? controller.spsState : null;
    }

    public boolean isController()
    {
        return controllerPos != null && position().equals(controllerPos);
    }

    public SPSStatus getStatus()
    {
        if (worldObj != null && worldObj.isRemote)
        {
            return clientStatus;
        }
        return SPSStatus.from(getControllerState(), isController(),
              portEnergy, getPortEnergyCapacity());
    }

    public boolean isOutput()
    {
        return output;
    }

    public boolean toggleOutput()
    {
        setOutput(!output);
        return output;
    }

    public void setOutput(boolean output)
    {
        if (this.output != output)
        {
            this.output = output;
            stateChanged();
            if (worldObj != null && !worldObj.isRemote)
            {
                sendUpdatePacket();
                MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
            }
        }
    }

    private void stateChanged()
    {
        markDirty();
        if (worldObj != null && !worldObj.isRemote)
        {
            MekanismUtils.saveChunk(this);
            if (isController())
            {
                presentationDirty = true;
            }
            if (isController() && spsState.isFormed())
            {
                for (SPSPosition port : spsState.getPorts())
                {
                    worldObj.func_147453_f(port.getX(), port.getY(),
                          port.getZ(), getBlockType());
                }
            }
            else
            {
                worldObj.func_147453_f(xCoord, yCoord, zCoord,
                      getBlockType());
            }
        }
    }

    private void syncPresentationIfNeeded()
    {
        if (presentationDirty && isController() && (ticker & 1) == 0)
        {
            presentationDirty = false;
            sendUpdatePacket();
        }
    }

    private void portStateChanged()
    {
        markDirty();
        if (worldObj != null && !worldObj.isRemote)
        {
            MekanismUtils.saveChunk(this);
        }
    }

    @Override
    public int receiveGas(ForgeDirection side, GasStack stack,
          boolean doTransfer)
    {
        SPSState state = getControllerState();
        if (state == null || stack == null || stack.getGas() == null
              || stack.amount <= 0 || !canReceiveGas(side, stack.getGas()))
        {
            return 0;
        }
        long accepted = state.insertPolonium(stack.amount, doTransfer);
        if (doTransfer && accepted > 0)
        {
            TileEntitySPSPort controller = getControllerTile();
            if (controller != null)
            {
                controller.stateChanged();
            }
        }
        return (int)accepted;
    }

    @Deprecated
    @Override
    public int receiveGas(ForgeDirection side, GasStack stack)
    {
        return receiveGas(side, stack, true);
    }

    @Nullable
    @Override
    public GasStack drawGas(ForgeDirection side, int amount,
          boolean doTransfer)
    {
        SPSState state = getControllerState();
        if (state == null || amount <= 0 || !output)
        {
            return null;
        }
        long extracted = state.extractAntimatter(amount, doTransfer);
        if (doTransfer && extracted > 0)
        {
            TileEntitySPSPort controller = getControllerTile();
            if (controller != null)
            {
                controller.stateChanged();
            }
        }
        return extracted <= 0 ? null
              : new GasStack(NuclearChemicals.Antimatter, (int)extracted);
    }

    @Deprecated
    @Nullable
    @Override
    public GasStack drawGas(ForgeDirection side, int amount)
    {
        return drawGas(side, amount, true);
    }

    @Override
    public boolean canReceiveGas(ForgeDirection side, Gas gas)
    {
        SPSState state = getControllerState();
        return !output && state != null
              && sameGas(gas, NuclearChemicals.Polonium)
              && state.getPolonium() < state.getPoloniumCapacity();
    }

    @Override
    public boolean canDrawGas(ForgeDirection side, Gas gas)
    {
        SPSState state = getControllerState();
        return output && state != null && state.getAntimatter() > 0
              && (gas == null || sameGas(gas, NuclearChemicals.Antimatter));
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo()
    {
        SPSState state = getControllerState();
        if (state == null)
        {
            return IGasTankInfoProvider.NONE;
        }
        return output
              ? new GasTankInfo[] {gasInfo(NuclearChemicals.Antimatter,
                    state.getAntimatter(), state.getAntimatterCapacity())}
              : new GasTankInfo[] {gasInfo(NuclearChemicals.Polonium,
                    state.getPolonium(), state.getPoloniumCapacity())};
    }

    @Override
    public boolean canTubeConnect(ForgeDirection side)
    {
        return getControllerState() != null;
    }

    public long getPortEnergy()
    {
        return portEnergy;
    }

    public long getPortEnergyCapacity()
    {
        return NuclearSPSConfig.getPortEnergyCapacity();
    }

    public long insertPortEnergy(long amount, boolean execute)
    {
        long requested = Math.max(0, amount);
        long accepted = Math.min(requested,
              Math.max(0, getPortEnergyCapacity() - portEnergy));
        if (execute && accepted > 0)
        {
            portEnergy += accepted;
            portStateChanged();
        }
        return accepted;
    }

    public double acceptEnergy(ForgeDirection side, double amount,
          boolean simulate)
    {
        if (!Double.isFinite(amount) || amount < 1)
        {
            return 0;
        }
        long requested = amount >= Long.MAX_VALUE
              ? Long.MAX_VALUE : (long)Math.floor(amount);
        return insertPortEnergy(requested, !simulate);
    }

    @Override
    public double transferEnergyToAcceptor(ForgeDirection side, double amount)
    {
        return acceptEnergy(side, amount, false);
    }

    @Override
    public boolean canReceiveEnergy(ForgeDirection side)
    {
        return portEnergy < getPortEnergyCapacity();
    }

    @Override
    public double getEnergy()
    {
        return portEnergy;
    }

    @Override
    public void setEnergy(double energy)
    {
        long clamped = !Double.isFinite(energy) || energy <= 0 ? 0
              : energy >= getPortEnergyCapacity()
                    ? getPortEnergyCapacity() : (long)Math.floor(energy);
        if (portEnergy != clamped)
        {
            portEnergy = clamped;
            portStateChanged();
        }
    }

    @Override
    public double getMaxEnergy()
    {
        return getPortEnergyCapacity();
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive,
          boolean simulate)
    {
        if (maxReceive <= 0)
        {
            return 0;
        }
        double accepted = acceptEnergy(from, maxReceive * general.FROM_TE,
              simulate);
        return (int)Math.min(maxReceive,
              Math.round(accepted * general.TO_TE));
    }

    @Override
    public int getEnergyStored(ForgeDirection from)
    {
        return saturatingInt(Math.round(portEnergy * general.TO_TE));
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from)
    {
        return saturatingInt(Math.round(
              getPortEnergyCapacity() * general.TO_TE));
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return true;
    }

    @Override
    public boolean canSetFacing(int side)
    {
        return false;
    }

    @Override
    public void handlePacketData(ByteBuf data)
    {
        super.handlePacketData(data);
        if (worldObj.isRemote)
        {
            boolean oldOutput = output;
            output = data.readBoolean();
            if (oldOutput != output)
            {
                MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
            }
            clientStatus = SPSStatus.read(data);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        data.add(output);
        SPSStatus.from(getControllerState(), isController(), portEnergy,
              getPortEnergyCapacity()).write(data);
        return data;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        SPSStatus status = getStatus();
        SPSPosition min = status.getMin();
        SPSPosition max = status.getMax();
        return status.isFormed() && min != null && max != null
              ? AxisAlignedBB.getBoundingBox(min.getX(), min.getY(), min.getZ(),
                    max.getX() + 1, max.getY() + 1, max.getZ() + 1)
              : super.getRenderBoundingBox();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        if (tag.hasKey(NBT_SPS, 10))
        {
            spsState.readFromNBT(tag.getCompoundTag(NBT_SPS));
        }
        controllerPos = tag.hasKey(NBT_CONTROLLER, 10)
              ? readPos(tag.getCompoundTag(NBT_CONTROLLER)) : null;
        output = tag.getBoolean(NBT_OUTPUT);
        portEnergy = Math.min(Math.max(0, tag.getLong(NBT_PORT_ENERGY)),
              getPortEnergyCapacity());
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setTag(NBT_SPS, spsState.writeToNBT(new NBTTagCompound()));
        if (controllerPos != null)
        {
            tag.setTag(NBT_CONTROLLER, writePos(controllerPos));
        }
        tag.setBoolean(NBT_OUTPUT, output);
        tag.setLong(NBT_PORT_ENERGY, portEnergy);
    }

    private ForgeDirection getOutwardSide(SPSState state)
    {
        SPSPosition min = state.getMin();
        SPSPosition max = state.getMax();
        if (min == null || max == null)
        {
            return ForgeDirection.UNKNOWN;
        }
        if (xCoord == min.getX())
        {
            return ForgeDirection.WEST;
        }
        if (xCoord == max.getX())
        {
            return ForgeDirection.EAST;
        }
        if (yCoord == min.getY())
        {
            return ForgeDirection.DOWN;
        }
        if (yCoord == max.getY())
        {
            return ForgeDirection.UP;
        }
        if (zCoord == min.getZ())
        {
            return ForgeDirection.NORTH;
        }
        if (zCoord == max.getZ())
        {
            return ForgeDirection.SOUTH;
        }
        return ForgeDirection.UNKNOWN;
    }

    private TileEntity tileAt(SPSPosition position)
    {
        return worldObj.getTileEntity(position.getX(), position.getY(),
              position.getZ());
    }

    private boolean blockExists(SPSPosition position)
    {
        return worldObj.blockExists(position.getX(), position.getY(),
              position.getZ());
    }

    private SPSPosition position()
    {
        return new SPSPosition(xCoord, yCoord, zCoord);
    }

    private void sendUpdatePacket()
    {
        Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(
              Coord4D.get(this), getNetworkedData(new ArrayList())),
              new Range4D(Coord4D.get(this)));
    }

    private static GasTankInfo gasInfo(@Nullable final Gas gas,
          long stored, long capacity)
    {
        final int visibleStored = saturatingInt(stored);
        final int visibleCapacity = saturatingInt(capacity);
        return new GasTankInfo()
        {
            @Nullable
            @Override
            public GasStack getGas()
            {
                return gas == null || visibleStored <= 0
                      ? null : new GasStack(gas, visibleStored);
            }

            @Override
            public int getStored()
            {
                return visibleStored;
            }

            @Override
            public int getMaxGas()
            {
                return visibleCapacity;
            }
        };
    }

    private static boolean sameGas(@Nullable Gas first, @Nullable Gas second)
    {
        return first != null && second != null
              && first.getName().equals(second.getName());
    }

    private static int saturatingInt(long amount)
    {
        return amount >= Integer.MAX_VALUE
              ? Integer.MAX_VALUE : (int)Math.max(0, amount);
    }

    private static NBTTagCompound writePos(SPSPosition position)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("X", position.getX());
        tag.setInteger("Y", position.getY());
        tag.setInteger("Z", position.getZ());
        return tag;
    }

    private static SPSPosition readPos(NBTTagCompound tag)
    {
        return new SPSPosition(tag.getInteger("X"), tag.getInteger("Y"),
              tag.getInteger("Z"));
    }
}
