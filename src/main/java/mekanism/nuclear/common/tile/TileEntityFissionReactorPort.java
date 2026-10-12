package mekanism.nuclear.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.DoubleSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.Range4D;
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
import mekanism.common.util.PipeUtils;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.config.NuclearFissionConfig;
import mekanism.nuclear.common.content.fission.FissionPortMode;
import mekanism.nuclear.common.content.fission.FissionReactorFormationManager;
import mekanism.nuclear.common.content.fission.FissionReactorPosition;
import mekanism.nuclear.common.content.fission.FissionReactorSimulation;
import mekanism.nuclear.common.content.fission.FissionReactorState;
import mekanism.nuclear.common.content.fission.FissionReactorStatus;
import mekanism.nuclear.common.content.fission.FissionReactorValidator;
import mekanism.nuclear.common.radiation.RadiationManager;
import mekanism.nuclear.common.radiation.RadiationPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/** Physical Fission Reactor port and server-authoritative controller node. */
public class TileEntityFissionReactorPort extends TileEntityBasicBlock
      implements IGasHandler, IGasTankInfoProvider, ITubeConnection,
      IFluidHandler
{
    private static final String NBT_REACTOR = "FissionReactor";
    private static final String NBT_CONTROLLER = "Controller";
    private static final String NBT_MODE = "PortMode";

    private final FissionReactorState reactorState = new FissionReactorState();
    private FissionReactorPosition controllerPos;
    private FissionPortMode mode = FissionPortMode.INPUT;
    private FissionReactorStatus clientStatus = FissionReactorStatus.EMPTY;
    private boolean validationQueued;

    @Override
    public void validate()
    {
        super.validate();
        if (worldObj != null && !worldObj.isRemote)
        {
            FissionReactorFormationManager.INSTANCE.register(this);
            validationQueued = reactorState.hasBounds();
        }
    }

    @Override
    public void onChunkUnload()
    {
        FissionReactorFormationManager.INSTANCE.unregister(this);
        super.onChunkUnload();
    }

    @Override
    public void invalidate()
    {
        FissionReactorFormationManager.INSTANCE.unregister(this);
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
            validateStructure();
        }
        TileEntityFissionReactorPort controller = getControllerTile();
        if (controller == null || !controller.reactorState.isFormed())
        {
            return;
        }
        if (controller == this)
        {
            tickReactor();
        }
        ejectOutput(controller);
    }

    private void tickReactor()
    {
        FissionReactorSimulation.TickResult result =
              FissionReactorSimulation.tick(reactorState,
                    new DoubleSupplier()
                    {
                        @Override
                        public double getAsDouble()
                        {
                            return worldObj.rand.nextDouble();
                        }
                    });
        if (result.getRadioactiveWasteReleased() > 0)
        {
            FissionReactorPosition center = reactorState.getCenter();
            RadiationManager.INSTANCE.release(worldObj,
                  new RadiationPosition(center.getX(), center.getY(),
                        center.getZ()), NuclearChemicals.NuclearWaste,
                  result.getRadioactiveWasteReleased());
        }
        if (result.isMeltdown())
        {
            FissionReactorPosition center = reactorState.getCenter();
            worldObj.createExplosion(null, center.getX() + 0.5D,
                  center.getY() + 0.5D, center.getZ() + 0.5D,
                  NuclearFissionConfig.getMeltdownRadius(), true);
        }
        if (result.isChanged())
        {
            stateChanged();
        }
    }

    private void ejectOutput(TileEntityFissionReactorPort controller)
    {
        ForgeDirection outward = getOutwardSide(controller.reactorState);
        if (outward == ForgeDirection.UNKNOWN)
        {
            return;
        }
        FissionReactorState state = controller.reactorState;
        int emitted = 0;
        if (mode == FissionPortMode.OUTPUT_WASTE
              && state.getNuclearWaste() > 0)
        {
            emitted = GasTransmission.emit(new GasStack(
                  NuclearChemicals.NuclearWaste,
                  saturatingInt(state.getNuclearWaste())), this,
                  Collections.singleton(outward));
            state.extractNuclearWaste(emitted, true);
        }
        else if (mode == FissionPortMode.OUTPUT_COOLANT
              && state.getHeatedCoolant() > 0)
        {
            if (state.getHeatedCoolantType()
                  == FissionReactorState.HeatedCoolantType.SUPERHEATED_SODIUM)
            {
                emitted = GasTransmission.emit(new GasStack(
                      NuclearChemicals.SuperheatedSodium,
                      saturatingInt(state.getHeatedCoolant())), this,
                      Collections.singleton(outward));
            }
            else if (state.getHeatedCoolantType()
                  == FissionReactorState.HeatedCoolantType.STEAM)
            {
                Fluid steam = FluidRegistry.getFluid("steam");
                if (steam != null)
                {
                    emitted = PipeUtils.emit(Collections.singletonList(outward),
                          new FluidStack(steam,
                                saturatingInt(state.getHeatedCoolant())), this);
                }
            }
            state.extractHeatedCoolant(emitted, true);
        }
        if (emitted > 0)
        {
            controller.stateChanged();
        }
    }

    public FissionReactorValidator.Result validateStructure()
    {
        FissionReactorValidator.Result result = FissionReactorValidator.validate(
              worldObj, position());
        if (!result.isFormed())
        {
            invalidateKnownStructure();
            return result;
        }

        FissionReactorState preserved = snapshotExistingController(result);
        FissionReactorPosition controller = result.getPorts().get(0);
        for (FissionReactorPosition portPos : result.getPorts())
        {
            TileEntity tile = tileAt(portPos);
            if (tile instanceof TileEntityFissionReactorPort)
            {
                TileEntityFissionReactorPort port =
                      (TileEntityFissionReactorPort)tile;
                port.applyStructure(result, controller);
                if (portPos.equals(controller))
                {
                    if (preserved != null)
                    {
                        port.reactorState.copyOperationalFrom(preserved);
                    }
                }
                else
                {
                    port.reactorState.clearOperationalState();
                }
                port.stateChanged();
                port.sendUpdatePacket();
            }
        }
        for (FissionReactorPosition adapterPos : result.getLogicAdapters())
        {
            TileEntity tile = tileAt(adapterPos);
            if (tile instanceof TileEntityFissionReactorLogicAdapter)
            {
                ((TileEntityFissionReactorLogicAdapter)tile)
                      .applyController(controller);
            }
        }
        return result;
    }

    @Nullable
    private FissionReactorState snapshotExistingController(
          FissionReactorValidator.Result result)
    {
        TileEntityFissionReactorPort source = null;
        for (FissionReactorPosition portPos : result.getPorts())
        {
            TileEntity tile = tileAt(portPos);
            if (tile instanceof TileEntityFissionReactorPort)
            {
                TileEntityFissionReactorPort port =
                      (TileEntityFissionReactorPort)tile;
                if (port.isController() && port.reactorState.hasBounds())
                {
                    source = port;
                    break;
                }
            }
        }
        if (source == null && isController() && reactorState.hasBounds())
        {
            source = this;
        }
        if (source == null)
        {
            return null;
        }
        FissionReactorState snapshot = new FissionReactorState();
        snapshot.readFromNBT(source.reactorState.writeToNBT(
              new NBTTagCompound()));
        return snapshot;
    }

    private void invalidateKnownStructure()
    {
        if (reactorState.getPorts().isEmpty())
        {
            reactorState.setUnformed();
            stateChanged();
            return;
        }
        for (FissionReactorPosition portPos : reactorState.getPorts())
        {
            TileEntity tile = tileAt(portPos);
            if (tile instanceof TileEntityFissionReactorPort)
            {
                TileEntityFissionReactorPort port =
                      (TileEntityFissionReactorPort)tile;
                port.reactorState.setUnformed();
                port.validationQueued = false;
                port.stateChanged();
                port.sendUpdatePacket();
            }
        }
    }

    private void applyStructure(FissionReactorValidator.Result result,
          FissionReactorPosition controller)
    {
        reactorState.applyStructure(result);
        controllerPos = controller;
        validationQueued = false;
    }

    public void queueValidation()
    {
        if (reactorState.hasBounds())
        {
            validationQueued = true;
        }
    }

    public boolean watches(FissionReactorPosition changedPosition)
    {
        return reactorState.watches(changedPosition);
    }

    public boolean watchesChunk(int chunkX, int chunkZ)
    {
        return reactorState.watchesChunk(chunkX, chunkZ);
    }

    public FissionReactorState getReactorState()
    {
        return reactorState;
    }

    @Nullable
    public FissionReactorPosition getControllerPos()
    {
        return controllerPos;
    }

    @Nullable
    public TileEntityFissionReactorPort getControllerTile()
    {
        if (worldObj == null || controllerPos == null
              || !worldObj.blockExists(controllerPos.getX(),
                    controllerPos.getY(), controllerPos.getZ()))
        {
            return null;
        }
        TileEntity tile = tileAt(controllerPos);
        return tile instanceof TileEntityFissionReactorPort
              ? (TileEntityFissionReactorPort)tile : null;
    }

    @Nullable
    private FissionReactorState getControllerState()
    {
        TileEntityFissionReactorPort controller = getControllerTile();
        return controller != null && controller.reactorState.isFormed()
              ? controller.reactorState : null;
    }

    public boolean isController()
    {
        return controllerPos != null && position().equals(controllerPos);
    }

    public FissionPortMode getMode()
    {
        return mode;
    }

    public FissionReactorStatus getStatus()
    {
        if (worldObj != null && worldObj.isRemote)
        {
            return clientStatus;
        }
        return FissionReactorStatus.from(getControllerState());
    }

    public FissionPortMode cycleMode()
    {
        setMode(mode.next());
        return mode;
    }

    public void setMode(FissionPortMode mode)
    {
        FissionPortMode newMode = mode == null
              ? FissionPortMode.INPUT : mode;
        if (this.mode != newMode)
        {
            this.mode = newMode;
            stateChanged();
            if (worldObj != null && !worldObj.isRemote)
            {
                sendUpdatePacket();
                MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
            }
        }
    }

    public void stateChanged()
    {
        markDirty();
        if (worldObj != null && !worldObj.isRemote)
        {
            MekanismUtils.saveChunk(this);
            if (isController() && reactorState.isFormed())
            {
                for (FissionReactorPosition port : reactorState.getPorts())
                {
                    worldObj.func_147453_f(port.getX(), port.getY(),
                          port.getZ(), getBlockType());
                }
            }
            else
            {
                worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
            }
        }
    }

    @Override
    public int receiveGas(ForgeDirection side, GasStack stack,
          boolean doTransfer)
    {
        FissionReactorState state = getControllerState();
        if (state == null || stack == null || stack.getGas() == null
              || stack.amount <= 0 || !canReceiveGas(side, stack.getGas()))
        {
            return 0;
        }
        long accepted;
        if (sameGas(stack.getGas(), NuclearChemicals.FissileFuel))
        {
            accepted = state.insertFissileFuel(stack.amount, doTransfer);
        }
        else
        {
            accepted = state.insertCoolant(
                  FissionReactorState.CoolantType.SODIUM,
                  stack.amount, doTransfer);
        }
        if (doTransfer && accepted > 0)
        {
            TileEntityFissionReactorPort controller = getControllerTile();
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
        FissionReactorState state = getControllerState();
        if (state == null || amount <= 0)
        {
            return null;
        }
        long extracted;
        Gas gas;
        if (mode == FissionPortMode.OUTPUT_WASTE)
        {
            gas = NuclearChemicals.NuclearWaste;
            extracted = state.extractNuclearWaste(amount, doTransfer);
        }
        else if (mode == FissionPortMode.OUTPUT_COOLANT
              && state.getHeatedCoolantType()
                    == FissionReactorState.HeatedCoolantType.SUPERHEATED_SODIUM)
        {
            gas = NuclearChemicals.SuperheatedSodium;
            extracted = state.extractHeatedCoolant(amount, doTransfer);
        }
        else
        {
            return null;
        }
        if (doTransfer && extracted > 0)
        {
            TileEntityFissionReactorPort controller = getControllerTile();
            if (controller != null)
            {
                controller.stateChanged();
            }
        }
        return extracted <= 0 ? null : new GasStack(gas, (int)extracted);
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
        FissionReactorState state = getControllerState();
        if (mode != FissionPortMode.INPUT || state == null || gas == null)
        {
            return false;
        }
        if (sameGas(gas, NuclearChemicals.FissileFuel))
        {
            return state.getFissileFuel() < state.getFuelCapacity();
        }
        return sameGas(gas, NuclearChemicals.Sodium)
              && (state.getCoolant() == 0
                    || state.getCoolantType()
                          == FissionReactorState.CoolantType.SODIUM)
              && state.getCoolant() < state.getCoolantCapacity();
    }

    @Override
    public boolean canDrawGas(ForgeDirection side, Gas gas)
    {
        FissionReactorState state = getControllerState();
        if (state == null)
        {
            return false;
        }
        if (mode == FissionPortMode.OUTPUT_WASTE)
        {
            return state.getNuclearWaste() > 0 && (gas == null
                  || sameGas(gas, NuclearChemicals.NuclearWaste));
        }
        return mode == FissionPortMode.OUTPUT_COOLANT
              && state.getHeatedCoolant() > 0
              && state.getHeatedCoolantType()
                    == FissionReactorState.HeatedCoolantType.SUPERHEATED_SODIUM
              && (gas == null
                    || sameGas(gas, NuclearChemicals.SuperheatedSodium));
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo()
    {
        FissionReactorState state = getControllerState();
        if (state == null)
        {
            return IGasTankInfoProvider.NONE;
        }
        if (mode == FissionPortMode.INPUT)
        {
            return new GasTankInfo[] {
                  gasInfo(NuclearChemicals.FissileFuel,
                        state.getFissileFuel(), state.getFuelCapacity()),
                  gasInfo(NuclearChemicals.Sodium,
                        state.getCoolantType()
                              == FissionReactorState.CoolantType.SODIUM
                              ? state.getCoolant() : 0,
                        state.getCoolantCapacity())
            };
        }
        if (mode == FissionPortMode.OUTPUT_WASTE)
        {
            return new GasTankInfo[] {gasInfo(NuclearChemicals.NuclearWaste,
                  state.getNuclearWaste(), state.getWasteCapacity())};
        }
        return new GasTankInfo[] {gasInfo(NuclearChemicals.SuperheatedSodium,
              state.getHeatedCoolantType()
                    == FissionReactorState.HeatedCoolantType.SUPERHEATED_SODIUM
                    ? state.getHeatedCoolant() : 0,
              state.getHeatedCoolantCapacity())};
    }

    @Override
    public boolean canTubeConnect(ForgeDirection side)
    {
        return getControllerState() != null;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        FissionReactorState state = getControllerState();
        if (state == null || resource == null
              || !canFill(from, resource.getFluid()))
        {
            return 0;
        }
        long accepted = state.insertCoolant(
              FissionReactorState.CoolantType.WATER, resource.amount, doFill);
        if (doFill && accepted > 0)
        {
            TileEntityFissionReactorPort controller = getControllerTile();
            if (controller != null)
            {
                controller.stateChanged();
            }
        }
        return (int)accepted;
    }

    @Nullable
    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource,
          boolean doDrain)
    {
        if (resource == null || !canDrain(from, resource.getFluid()))
        {
            return null;
        }
        return drain(from, resource.amount, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain,
          boolean doDrain)
    {
        FissionReactorState state = getControllerState();
        Fluid steam = FluidRegistry.getFluid("steam");
        if (state == null || steam == null || maxDrain <= 0
              || !canDrain(from, steam))
        {
            return null;
        }
        long extracted = state.extractHeatedCoolant(maxDrain, doDrain);
        if (doDrain && extracted > 0)
        {
            TileEntityFissionReactorPort controller = getControllerTile();
            if (controller != null)
            {
                controller.stateChanged();
            }
        }
        return extracted <= 0 ? null
              : new FluidStack(steam, (int)extracted);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        FissionReactorState state = getControllerState();
        return mode == FissionPortMode.INPUT && state != null
              && fluid == FluidRegistry.WATER
              && (state.getCoolant() == 0
                    || state.getCoolantType()
                          == FissionReactorState.CoolantType.WATER)
              && state.getCoolant() < state.getCoolantCapacity();
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        FissionReactorState state = getControllerState();
        Fluid steam = FluidRegistry.getFluid("steam");
        return mode == FissionPortMode.OUTPUT_COOLANT && state != null
              && steam != null && state.getHeatedCoolant() > 0
              && state.getHeatedCoolantType()
                    == FissionReactorState.HeatedCoolantType.STEAM
              && (fluid == null || fluid == steam);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        FissionReactorState state = getControllerState();
        if (state == null)
        {
            return PipeUtils.EMPTY;
        }
        if (mode == FissionPortMode.INPUT)
        {
            FluidStack water = state.getCoolantType()
                  == FissionReactorState.CoolantType.WATER
                  && state.getCoolant() > 0
                  ? new FluidStack(FluidRegistry.WATER,
                        saturatingInt(state.getCoolant())) : null;
            return new FluidTankInfo[] {new FluidTankInfo(water,
                  saturatingInt(state.getCoolantCapacity()))};
        }
        if (mode == FissionPortMode.OUTPUT_COOLANT)
        {
            Fluid steam = FluidRegistry.getFluid("steam");
            FluidStack contents = steam != null
                  && state.getHeatedCoolantType()
                        == FissionReactorState.HeatedCoolantType.STEAM
                  && state.getHeatedCoolant() > 0
                  ? new FluidStack(steam,
                        saturatingInt(state.getHeatedCoolant())) : null;
            return new FluidTankInfo[] {new FluidTankInfo(contents,
                  saturatingInt(state.getHeatedCoolantCapacity()))};
        }
        return PipeUtils.EMPTY;
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
            FissionPortMode oldMode = mode;
            mode = FissionPortMode.byIndex(data.readInt());
            if (oldMode != mode)
            {
                MekanismUtils.updateBlock(worldObj, xCoord, yCoord, zCoord);
            }
            clientStatus = FissionReactorStatus.read(data);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArrayList getNetworkedData(ArrayList data)
    {
        super.getNetworkedData(data);
        data.add(mode.ordinal());
        FissionReactorStatus.from(getControllerState()).write(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        if (tag.hasKey(NBT_REACTOR, 10))
        {
            reactorState.readFromNBT(tag.getCompoundTag(NBT_REACTOR));
        }
        controllerPos = tag.hasKey(NBT_CONTROLLER, 10)
              ? readPos(tag.getCompoundTag(NBT_CONTROLLER)) : null;
        mode = FissionPortMode.byIndex(tag.getInteger(NBT_MODE));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setTag(NBT_REACTOR,
              reactorState.writeToNBT(new NBTTagCompound()));
        if (controllerPos != null)
        {
            tag.setTag(NBT_CONTROLLER, writePos(controllerPos));
        }
        tag.setInteger(NBT_MODE, mode.ordinal());
    }

    private ForgeDirection getOutwardSide(FissionReactorState state)
    {
        FissionReactorPosition min = state.getMin();
        FissionReactorPosition max = state.getMax();
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

    private TileEntity tileAt(FissionReactorPosition position)
    {
        return worldObj.getTileEntity(position.getX(), position.getY(),
              position.getZ());
    }

    private FissionReactorPosition position()
    {
        return new FissionReactorPosition(xCoord, yCoord, zCoord);
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

    private static NBTTagCompound writePos(FissionReactorPosition position)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("X", position.getX());
        tag.setInteger("Y", position.getY());
        tag.setInteger("Z", position.getZ());
        return tag;
    }

    private static FissionReactorPosition readPos(NBTTagCompound tag)
    {
        return new FissionReactorPosition(tag.getInteger("X"),
              tag.getInteger("Y"), tag.getInteger("Z"));
    }
}
