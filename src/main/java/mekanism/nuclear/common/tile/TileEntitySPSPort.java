package mekanism.nuclear.common.tile;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.TileNetworkList;
import mekanism.common.Mekanism;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.content.sps.SPSFormationManager;
import mekanism.nuclear.common.content.sps.SPSState;
import mekanism.nuclear.common.content.sps.SPSValidator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

/** A physical SPS port and the server-authoritative structure controller node. */
public class TileEntitySPSPort extends TileEntityBasicBlock {

    private static final String NBT_SPS = "SPS";
    private static final String NBT_CONTROLLER = "Controller";
    private static final String NBT_OUTPUT = "Output";

    private final SPSState spsState = new SPSState();
    private BlockPos controllerPos;
    private boolean output;
    private boolean validationQueued;

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
        if (!world.isRemote && validationQueued) {
            validationQueued = false;
            validateKnownStructure();
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

        BlockPos controller = result.getPorts().get(0);
        for (BlockPos portPos : result.getPorts()) {
            TileEntity tile = world.getTileEntity(portPos);
            if (tile instanceof TileEntitySPSPort) {
                TileEntitySPSPort port = (TileEntitySPSPort) tile;
                port.spsState.applyStructure(result);
                port.controllerPos = controller;
                port.validationQueued = false;
                port.stateChanged();
            }
        }
        return result;
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

    public boolean isController() {
        return controllerPos != null && pos.equals(controllerPos);
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
        }
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
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(output);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(NBT_SPS, 10)) {
            spsState.readFromNBT(tag.getCompoundTag(NBT_SPS));
        }
        controllerPos = tag.hasKey(NBT_CONTROLLER, 10) ? readPos(tag.getCompoundTag(NBT_CONTROLLER)) : null;
        output = tag.getBoolean(NBT_OUTPUT);
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
