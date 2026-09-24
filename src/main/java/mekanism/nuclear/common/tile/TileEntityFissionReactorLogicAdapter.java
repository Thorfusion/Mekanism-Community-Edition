package mekanism.nuclear.common.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.content.fission.FissionReactorLogicMode;
import mekanism.nuclear.common.content.fission.FissionReactorState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/** Server-authoritative redstone control/status surface for a formed reactor. */
public class TileEntityFissionReactorLogicAdapter extends TileEntityBasicBlock {

    private static final String NBT_CONTROLLER = "Controller";
    private static final String NBT_MODE = "LogicMode";

    private BlockPos controllerPos;
    private FissionReactorLogicMode mode = FissionReactorLogicMode.DISABLED;
    private boolean lastOutput;

    @Override
    public void onLoad() {
        super.onLoad();
        if (!world.isRemote) {
            updateLogic();
        }
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            updateLogic();
        }
    }

    public void updateLogic() {
        FissionReactorState state = getControllerState();
        if (mode == FissionReactorLogicMode.ACTIVATION && state != null) {
            boolean shouldRun = world.isBlockPowered(pos);
            if (state.isActive() != shouldRun) {
                TileEntityFissionReactorPort controller = getControllerTile();
                state.setActive(shouldRun);
                if (controller != null) {
                    controller.stateChanged();
                }
            }
        }
        boolean output = mode.shouldOutput(state);
        if (output != lastOutput) {
            lastOutput = output;
            world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
        }
    }

    public int getRedstoneLevel(@Nullable EnumFacing side) {
        FissionReactorState state = getControllerState();
        return side != null && state != null && state.isPositionOutsideBounds(pos.offset(side))
              && mode.shouldOutput(state) ? 15 : 0;
    }

    public FissionReactorLogicMode getMode() {
        return mode;
    }

    public FissionReactorLogicMode cycleMode() {
        mode = mode.next();
        markDirty();
        if (world != null && !world.isRemote) {
            MekanismUtils.saveChunk(this);
            updateLogic();
        }
        return mode;
    }

    public void applyController(BlockPos controller) {
        controllerPos = controller;
        markDirty();
        if (world != null && !world.isRemote) {
            MekanismUtils.saveChunk(this);
        }
    }

    @Nullable
    public BlockPos getControllerPos() {
        return controllerPos;
    }

    @Nullable
    private TileEntityFissionReactorPort getControllerTile() {
        if (world == null || controllerPos == null || !world.isBlockLoaded(controllerPos)) {
            return null;
        }
        TileEntity tile = world.getTileEntity(controllerPos);
        return tile instanceof TileEntityFissionReactorPort ? (TileEntityFissionReactorPort) tile : null;
    }

    @Nullable
    private FissionReactorState getControllerState() {
        TileEntityFissionReactorPort controller = getControllerTile();
        if (controller == null || !controller.getReactorState().isFormed()) {
            return null;
        }
        return controller.getReactorState();
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing facing) {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        controllerPos = tag.hasKey(NBT_CONTROLLER, 10) ? readPos(tag.getCompoundTag(NBT_CONTROLLER)) : null;
        mode = FissionReactorLogicMode.byIndex(tag.getInteger(NBT_MODE));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (controllerPos != null) {
            tag.setTag(NBT_CONTROLLER, writePos(controllerPos));
        }
        tag.setInteger(NBT_MODE, mode.ordinal());
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
