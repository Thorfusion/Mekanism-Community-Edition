package mekanism.nuclear.common.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.content.fission.FissionReactorFormationManager;
import mekanism.nuclear.common.content.fission.FissionReactorState;
import mekanism.nuclear.common.content.fission.FissionReactorValidator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityFissionReactorPort extends TileEntityBasicBlock {

    private static final String NBT_REACTOR = "FissionReactor";
    private static final String NBT_CONTROLLER = "Controller";

    private final FissionReactorState reactorState = new FissionReactorState();
    private BlockPos controllerPos;
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
        if (!world.isRemote && validationQueued) {
            validationQueued = false;
            validateStructure();
        }
    }

    public FissionReactorValidator.Result validateStructure() {
        FissionReactorValidator.Result result = FissionReactorValidator.validate(world, pos);
        if (result.isFormed()) {
            BlockPos controller = result.getPorts().get(0);
            for (BlockPos portPos : result.getPorts()) {
                TileEntity tile = world.getTileEntity(portPos);
                if (tile instanceof TileEntityFissionReactorPort) {
                    ((TileEntityFissionReactorPort) tile).applyStructure(result, controller);
                }
            }
        } else {
            reactorState.setUnformed();
            markDirtyAndSave();
        }
        return result;
    }

    private void applyStructure(FissionReactorValidator.Result result, BlockPos controller) {
        reactorState.applyStructure(result);
        controllerPos = controller;
        validationQueued = false;
        markDirtyAndSave();
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

    public boolean isController() {
        return pos.equals(controllerPos);
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing facing) {
        return false;
    }

    private void markDirtyAndSave() {
        markDirty();
        if (world != null && !world.isRemote) {
            MekanismUtils.saveChunk(this);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(NBT_REACTOR, 10)) {
            reactorState.readFromNBT(tag.getCompoundTag(NBT_REACTOR));
        }
        controllerPos = tag.hasKey(NBT_CONTROLLER, 10) ? readPos(tag.getCompoundTag(NBT_CONTROLLER)) : null;
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag(NBT_REACTOR, reactorState.writeToNBT(new NBTTagCompound()));
        if (controllerPos != null) {
            tag.setTag(NBT_CONTROLLER, writePos(controllerPos));
        }
        return tag;
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
