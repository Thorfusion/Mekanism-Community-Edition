package mekanism.nuclear.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.TileNetworkList;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.common.Mekanism;
import mekanism.common.base.FluidHandlerWrapper;
import mekanism.common.base.IFluidHandlerWrapper;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.GasUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.PipeUtils;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.config.NuclearFissionConfig;
import mekanism.nuclear.common.content.fission.FissionPortMode;
import mekanism.nuclear.common.content.fission.FissionReactorFormationManager;
import mekanism.nuclear.common.content.fission.FissionReactorSimulation;
import mekanism.nuclear.common.content.fission.FissionReactorState;
import mekanism.nuclear.common.content.fission.FissionReactorValidator;
import mekanism.nuclear.common.radiation.RadiationManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;

/** A physical Fission Reactor port and the server-authoritative controller node. */
public class TileEntityFissionReactorPort extends TileEntityBasicBlock implements IGasHandler, IFluidHandlerWrapper {

    private static final String NBT_REACTOR = "FissionReactor";
    private static final String NBT_CONTROLLER = "Controller";
    private static final String NBT_MODE = "PortMode";

    private final FissionReactorState reactorState = new FissionReactorState();
    private BlockPos controllerPos;
    private FissionPortMode mode = FissionPortMode.INPUT;
    private boolean validationQueued;

    @Override
    public void onLoad() {
        super.onLoad();
        if (!world.isRemote) {
            FissionReactorFormationManager.INSTANCE.register(this);
            validationQueued = reactorState.hasBounds();
        }
    }

    @Override
    public void onChunkUnload() {
        FissionReactorFormationManager.INSTANCE.unregister(this);
        super.onChunkUnload();
    }

    @Override
    public void invalidate() {
        FissionReactorFormationManager.INSTANCE.unregister(this);
        super.invalidate();
    }

    @Override
    public void onUpdate() {
        if (world.isRemote) {
            return;
        }
        if (validationQueued) {
            validationQueued = false;
            validateStructure();
        }
        TileEntityFissionReactorPort controller = getControllerTile();
        if (controller == null || !controller.reactorState.isFormed()) {
            return;
        }
        if (controller == this) {
            tickReactor();
        }
        ejectOutput(controller);
    }

    private void tickReactor() {
        FissionReactorSimulation.TickResult result = FissionReactorSimulation.tick(reactorState,
              world.rand::nextDouble);
        if (result.getRadioactiveWasteReleased() > 0) {
            RadiationManager.INSTANCE.release(world, reactorState.getCenter(), NuclearChemicals.NuclearWaste,
                  result.getRadioactiveWasteReleased());
        }
        if (result.isMeltdown()) {
            BlockPos center = reactorState.getCenter();
            world.createExplosion(null, center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D,
                  NuclearFissionConfig.getMeltdownRadius(), true);
        }
        if (result.isChanged()) {
            stateChanged();
        }
    }

    private void ejectOutput(TileEntityFissionReactorPort controller) {
        EnumFacing outward = getOutwardSide(controller.reactorState);
        if (outward == null) {
            return;
        }
        FissionReactorState state = controller.reactorState;
        int emitted = 0;
        if (mode == FissionPortMode.OUTPUT_WASTE && state.getNuclearWaste() > 0) {
            emitted = GasUtils.emit(new GasStack(NuclearChemicals.NuclearWaste,
                  saturatingInt(state.getNuclearWaste())), this, Collections.singleton(outward));
            state.extractNuclearWaste(emitted, true);
        } else if (mode == FissionPortMode.OUTPUT_COOLANT && state.getHeatedCoolant() > 0) {
            if (state.getHeatedCoolantType() == FissionReactorState.HeatedCoolantType.SUPERHEATED_SODIUM) {
                emitted = GasUtils.emit(new GasStack(NuclearChemicals.SuperheatedSodium,
                      saturatingInt(state.getHeatedCoolant())), this, Collections.singleton(outward));
            } else if (state.getHeatedCoolantType() == FissionReactorState.HeatedCoolantType.STEAM) {
                Fluid steam = FluidRegistry.getFluid("steam");
                if (steam != null) {
                    emitted = PipeUtils.emit(Collections.singleton(outward),
                          new FluidStack(steam, saturatingInt(state.getHeatedCoolant())), this);
                }
            }
            state.extractHeatedCoolant(emitted, true);
        }
        if (emitted > 0) {
            controller.stateChanged();
        }
    }

    public FissionReactorValidator.Result validateStructure() {
        FissionReactorValidator.Result result = FissionReactorValidator.validate(world, pos);
        if (!result.isFormed()) {
            invalidateKnownStructure();
            return result;
        }

        FissionReactorState preserved = snapshotExistingController(result);
        BlockPos controller = result.getPorts().get(0);
        for (BlockPos portPos : result.getPorts()) {
            TileEntity tile = world.getTileEntity(portPos);
            if (tile instanceof TileEntityFissionReactorPort) {
                TileEntityFissionReactorPort port = (TileEntityFissionReactorPort) tile;
                port.applyStructure(result, controller);
                if (portPos.equals(controller)) {
                    if (preserved != null) {
                        port.reactorState.copyOperationalFrom(preserved);
                    }
                } else {
                    port.reactorState.clearOperationalState();
                }
                port.stateChanged();
            }
        }
        return result;
    }

    @Nullable
    private FissionReactorState snapshotExistingController(FissionReactorValidator.Result result) {
        TileEntityFissionReactorPort source = null;
        for (BlockPos portPos : result.getPorts()) {
            TileEntity tile = world.getTileEntity(portPos);
            if (tile instanceof TileEntityFissionReactorPort) {
                TileEntityFissionReactorPort port = (TileEntityFissionReactorPort) tile;
                if (port.isController() && port.reactorState.hasBounds()) {
                    source = port;
                    break;
                }
            }
        }
        if (source == null && isController() && reactorState.hasBounds()) {
            source = this;
        }
        if (source == null) {
            return null;
        }
        FissionReactorState snapshot = new FissionReactorState();
        snapshot.readFromNBT(source.reactorState.writeToNBT(new NBTTagCompound()));
        return snapshot;
    }

    private void invalidateKnownStructure() {
        if (reactorState.getPorts().isEmpty()) {
            reactorState.setUnformed();
            stateChanged();
            return;
        }
        for (BlockPos portPos : reactorState.getPorts()) {
            TileEntity tile = world.getTileEntity(portPos);
            if (tile instanceof TileEntityFissionReactorPort) {
                TileEntityFissionReactorPort port = (TileEntityFissionReactorPort) tile;
                port.reactorState.setUnformed();
                port.validationQueued = false;
                port.stateChanged();
            }
        }
    }

    private void applyStructure(FissionReactorValidator.Result result, BlockPos controller) {
        reactorState.applyStructure(result);
        controllerPos = controller;
        validationQueued = false;
    }

    public void queueValidation() {
        if (reactorState.hasBounds()) {
            validationQueued = true;
        }
    }

    public boolean watches(BlockPos changedPos) {
        return reactorState.watches(changedPos);
    }

    public boolean watchesChunk(int chunkX, int chunkZ) {
        return reactorState.watchesChunk(chunkX, chunkZ);
    }

    public FissionReactorState getReactorState() {
        return reactorState;
    }

    @Nullable
    public BlockPos getControllerPos() {
        return controllerPos;
    }

    @Nullable
    public TileEntityFissionReactorPort getControllerTile() {
        if (world == null || controllerPos == null || !world.isBlockLoaded(controllerPos)) {
            return null;
        }
        TileEntity tile = world.getTileEntity(controllerPos);
        return tile instanceof TileEntityFissionReactorPort ? (TileEntityFissionReactorPort) tile : null;
    }

    @Nullable
    private FissionReactorState getControllerState() {
        TileEntityFissionReactorPort controller = getControllerTile();
        return controller != null && controller.reactorState.isFormed() ? controller.reactorState : null;
    }

    public boolean isController() {
        return controllerPos != null && pos.equals(controllerPos);
    }

    public FissionPortMode getMode() {
        return mode;
    }

    public FissionPortMode cycleMode() {
        setMode(mode.next());
        return mode;
    }

    public void setMode(FissionPortMode mode) {
        FissionPortMode newMode = mode == null ? FissionPortMode.INPUT : mode;
        if (this.mode != newMode) {
            this.mode = newMode;
            stateChanged();
            if (world != null && !world.isRemote) {
                Mekanism.packetHandler.sendUpdatePacket(this);
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    }

    public void stateChanged() {
        markDirty();
        if (world != null && !world.isRemote) {
            MekanismUtils.saveChunk(this);
            if (isController() && reactorState.isFormed()) {
                for (BlockPos port : reactorState.getPorts()) {
                    world.updateComparatorOutputLevel(port, getBlockType());
                }
            } else {
                world.updateComparatorOutputLevel(pos, getBlockType());
            }
        }
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
        FissionReactorState state = getControllerState();
        if (state == null || stack == null || stack.getGas() == null || stack.amount <= 0
              || !canReceiveGas(side, stack.getGas())) {
            return 0;
        }
        long accepted;
        if (sameGas(stack.getGas(), NuclearChemicals.FissileFuel)) {
            accepted = state.insertFissileFuel(stack.amount, doTransfer);
        } else {
            accepted = state.insertCoolant(FissionReactorState.CoolantType.SODIUM, stack.amount, doTransfer);
        }
        if (doTransfer && accepted > 0) {
            getControllerTile().stateChanged();
        }
        return (int) accepted;
    }

    @Nullable
    @Override
    public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer) {
        FissionReactorState state = getControllerState();
        if (state == null || amount <= 0) {
            return null;
        }
        long extracted;
        Gas gas;
        if (mode == FissionPortMode.OUTPUT_WASTE) {
            gas = NuclearChemicals.NuclearWaste;
            extracted = state.extractNuclearWaste(amount, doTransfer);
        } else if (mode == FissionPortMode.OUTPUT_COOLANT
              && state.getHeatedCoolantType() == FissionReactorState.HeatedCoolantType.SUPERHEATED_SODIUM) {
            gas = NuclearChemicals.SuperheatedSodium;
            extracted = state.extractHeatedCoolant(amount, doTransfer);
        } else {
            return null;
        }
        if (doTransfer && extracted > 0) {
            getControllerTile().stateChanged();
        }
        return extracted <= 0 ? null : new GasStack(gas, (int) extracted);
    }

    @Override
    public boolean canReceiveGas(EnumFacing side, Gas gas) {
        FissionReactorState state = getControllerState();
        if (mode != FissionPortMode.INPUT || state == null || gas == null) {
            return false;
        }
        if (sameGas(gas, NuclearChemicals.FissileFuel)) {
            return state.getFissileFuel() < state.getFuelCapacity();
        }
        return sameGas(gas, NuclearChemicals.Sodium)
              && (state.getCoolant() == 0 || state.getCoolantType() == FissionReactorState.CoolantType.SODIUM)
              && state.getCoolant() < state.getCoolantCapacity();
    }

    @Override
    public boolean canDrawGas(EnumFacing side, Gas gas) {
        FissionReactorState state = getControllerState();
        if (state == null) {
            return false;
        }
        if (mode == FissionPortMode.OUTPUT_WASTE) {
            return state.getNuclearWaste() > 0 && (gas == null || sameGas(gas, NuclearChemicals.NuclearWaste));
        }
        return mode == FissionPortMode.OUTPUT_COOLANT && state.getHeatedCoolant() > 0
              && state.getHeatedCoolantType() == FissionReactorState.HeatedCoolantType.SUPERHEATED_SODIUM
              && (gas == null || sameGas(gas, NuclearChemicals.SuperheatedSodium));
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo() {
        FissionReactorState state = getControllerState();
        if (state == null) {
            return IGasHandler.NONE;
        }
        if (mode == FissionPortMode.INPUT) {
            return new GasTankInfo[]{gasInfo(NuclearChemicals.FissileFuel, state.getFissileFuel(), state.getFuelCapacity()),
                  gasInfo(NuclearChemicals.Sodium,
                        state.getCoolantType() == FissionReactorState.CoolantType.SODIUM ? state.getCoolant() : 0,
                        state.getCoolantCapacity())};
        }
        if (mode == FissionPortMode.OUTPUT_WASTE) {
            return new GasTankInfo[]{gasInfo(NuclearChemicals.NuclearWaste, state.getNuclearWaste(),
                  state.getWasteCapacity())};
        }
        return new GasTankInfo[]{gasInfo(NuclearChemicals.SuperheatedSodium,
              state.getHeatedCoolantType() == FissionReactorState.HeatedCoolantType.SUPERHEATED_SODIUM
                    ? state.getHeatedCoolant() : 0, state.getHeatedCoolantCapacity())};
    }

    @Override
    public int fill(EnumFacing from, @Nonnull FluidStack resource, boolean doFill) {
        FissionReactorState state = getControllerState();
        if (state == null || !canFill(from, resource)) {
            return 0;
        }
        long accepted = state.insertCoolant(FissionReactorState.CoolantType.WATER, resource.amount, doFill);
        if (doFill && accepted > 0) {
            getControllerTile().stateChanged();
        }
        return (int) accepted;
    }

    @Nullable
    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        FissionReactorState state = getControllerState();
        Fluid steam = FluidRegistry.getFluid("steam");
        if (state == null || steam == null || maxDrain <= 0 || !canDrain(from, null)) {
            return null;
        }
        long extracted = state.extractHeatedCoolant(maxDrain, doDrain);
        if (doDrain && extracted > 0) {
            getControllerTile().stateChanged();
        }
        return extracted <= 0 ? null : new FluidStack(steam, (int) extracted);
    }

    @Override
    public boolean canFill(EnumFacing from, @Nonnull FluidStack fluid) {
        FissionReactorState state = getControllerState();
        return mode == FissionPortMode.INPUT && state != null && fluid.getFluid() == FluidRegistry.WATER
              && (state.getCoolant() == 0 || state.getCoolantType() == FissionReactorState.CoolantType.WATER)
              && state.getCoolant() < state.getCoolantCapacity();
    }

    @Override
    public boolean canDrain(EnumFacing from, @Nullable FluidStack fluid) {
        FissionReactorState state = getControllerState();
        Fluid steam = FluidRegistry.getFluid("steam");
        return mode == FissionPortMode.OUTPUT_COOLANT && state != null && steam != null
              && state.getHeatedCoolant() > 0
              && state.getHeatedCoolantType() == FissionReactorState.HeatedCoolantType.STEAM
              && (fluid == null || fluid.getFluid() == steam);
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        FissionReactorState state = getControllerState();
        if (state == null) {
            return PipeUtils.EMPTY;
        }
        if (mode == FissionPortMode.INPUT) {
            FluidStack water = state.getCoolantType() == FissionReactorState.CoolantType.WATER
                  && state.getCoolant() > 0 ? new FluidStack(FluidRegistry.WATER, saturatingInt(state.getCoolant())) : null;
            return new FluidTankInfo[]{new FluidTankInfo(water, saturatingInt(state.getCoolantCapacity()))};
        }
        if (mode == FissionPortMode.OUTPUT_COOLANT) {
            Fluid steam = FluidRegistry.getFluid("steam");
            FluidStack contents = steam != null
                  && state.getHeatedCoolantType() == FissionReactorState.HeatedCoolantType.STEAM
                  && state.getHeatedCoolant() > 0
                  ? new FluidStack(steam, saturatingInt(state.getHeatedCoolant())) : null;
            return new FluidTankInfo[]{new FluidTankInfo(contents, saturatingInt(state.getHeatedCoolantCapacity()))};
        }
        return PipeUtils.EMPTY;
    }

    @Override
    public FluidTankInfo[] getAllTanks() {
        return getTankInfo(null);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing side) {
        if (getControllerState() != null && (capability == Capabilities.GAS_HANDLER_CAPABILITY
              || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
            return true;
        }
        return super.hasCapability(capability, side);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing side) {
        if (getControllerState() != null) {
            if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
                return (T) this;
            }
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FluidHandlerWrapper(this, side));
            }
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
            FissionPortMode oldMode = mode;
            mode = FissionPortMode.byIndex(data.readInt());
            if (oldMode != mode) {
                MekanismUtils.updateBlock(world, pos);
            }
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(mode.ordinal());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(NBT_REACTOR, 10)) {
            reactorState.readFromNBT(tag.getCompoundTag(NBT_REACTOR));
        }
        controllerPos = tag.hasKey(NBT_CONTROLLER, 10) ? readPos(tag.getCompoundTag(NBT_CONTROLLER)) : null;
        mode = FissionPortMode.byIndex(tag.getInteger(NBT_MODE));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag(NBT_REACTOR, reactorState.writeToNBT(new NBTTagCompound()));
        if (controllerPos != null) {
            tag.setTag(NBT_CONTROLLER, writePos(controllerPos));
        }
        tag.setInteger(NBT_MODE, mode.ordinal());
        return tag;
    }

    @Nullable
    private EnumFacing getOutwardSide(FissionReactorState state) {
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
