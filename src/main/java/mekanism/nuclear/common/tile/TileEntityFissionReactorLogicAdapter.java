package mekanism.nuclear.common.tile;

import javax.annotation.Nullable;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.content.fission.FissionReactorLogicMode;
import mekanism.nuclear.common.content.fission.FissionReactorPosition;
import mekanism.nuclear.common.content.fission.FissionReactorState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/** Server-authoritative redstone control/status surface for a formed reactor. */
public class TileEntityFissionReactorLogicAdapter extends TileEntityBasicBlock
{
    private static final String NBT_CONTROLLER = "Controller";
    private static final String NBT_MODE = "LogicMode";

    private FissionReactorPosition controllerPos;
    private FissionReactorLogicMode mode = FissionReactorLogicMode.DISABLED;
    private boolean lastOutput;

    @Override
    public void validate()
    {
        super.validate();
        if (worldObj != null && !worldObj.isRemote)
        {
            updateLogic();
        }
    }

    @Override
    public void onUpdate()
    {
        if (!worldObj.isRemote)
        {
            updateLogic();
        }
    }

    public void updateLogic()
    {
        FissionReactorState state = getControllerState();
        if (mode == FissionReactorLogicMode.ACTIVATION && state != null)
        {
            boolean shouldRun = worldObj.isBlockIndirectlyGettingPowered(
                  xCoord, yCoord, zCoord);
            if (state.isActive() != shouldRun)
            {
                TileEntityFissionReactorPort controller = getControllerTile();
                state.setActive(shouldRun);
                if (controller != null)
                {
                    controller.stateChanged();
                }
            }
        }
        boolean output = mode.shouldOutput(state);
        if (output != lastOutput)
        {
            lastOutput = output;
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,
                  getBlockType());
        }
    }

    public int getRedstoneLevel(@Nullable ForgeDirection side)
    {
        FissionReactorState state = getControllerState();
        return side != null && side != ForgeDirection.UNKNOWN && state != null
              && state.isPositionOutsideBounds(position().offset(side))
              && mode.shouldOutput(state) ? 15 : 0;
    }

    public FissionReactorLogicMode getMode()
    {
        return mode;
    }

    public FissionReactorLogicMode cycleMode()
    {
        mode = mode.next();
        markDirty();
        if (worldObj != null && !worldObj.isRemote)
        {
            MekanismUtils.saveChunk(this);
            updateLogic();
        }
        return mode;
    }

    public void applyController(FissionReactorPosition controller)
    {
        controllerPos = controller;
        markDirty();
        if (worldObj != null && !worldObj.isRemote)
        {
            MekanismUtils.saveChunk(this);
        }
    }

    @Nullable
    public FissionReactorPosition getControllerPos()
    {
        return controllerPos;
    }

    @Nullable
    private TileEntityFissionReactorPort getControllerTile()
    {
        if (worldObj == null || controllerPos == null
              || !worldObj.blockExists(controllerPos.getX(),
                    controllerPos.getY(), controllerPos.getZ()))
        {
            return null;
        }
        TileEntity tile = worldObj.getTileEntity(controllerPos.getX(),
              controllerPos.getY(), controllerPos.getZ());
        return tile instanceof TileEntityFissionReactorPort
              ? (TileEntityFissionReactorPort)tile : null;
    }

    @Nullable
    private FissionReactorState getControllerState()
    {
        TileEntityFissionReactorPort controller = getControllerTile();
        return controller == null || !controller.getReactorState().isFormed()
              ? null : controller.getReactorState();
    }

    @Override
    public boolean canSetFacing(int side)
    {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        controllerPos = tag.hasKey(NBT_CONTROLLER, 10)
              ? readPos(tag.getCompoundTag(NBT_CONTROLLER)) : null;
        mode = FissionReactorLogicMode.byIndex(tag.getInteger(NBT_MODE));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        if (controllerPos != null)
        {
            tag.setTag(NBT_CONTROLLER, writePos(controllerPos));
        }
        tag.setInteger(NBT_MODE, mode.ordinal());
    }

    private FissionReactorPosition position()
    {
        return new FissionReactorPosition(xCoord, yCoord, zCoord);
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
