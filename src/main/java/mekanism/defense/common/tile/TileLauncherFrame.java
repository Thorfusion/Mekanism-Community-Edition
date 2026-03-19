package defense.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import defense.api.ITier;
import mekanism.api.Coord4D;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

/** This tile entity is for the screen of the missile launcher
 * 
 * @author Calclavia */
public class TileLauncherFrame extends TileEntityBasicBlock implements ITier, IBoundingBlock
{
    // The tier of this screen
    private int tier = 0;

    @Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);
		
		if(worldObj.isRemote)
		{
			tier = dataStream.readInt();
		}
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		
		data.add(tier);
		
		return data;
	}

    /** Gets the inaccuracy of the missile based on the launcher support frame's tier */
    public int getInaccuracy()
    {
        switch (this.tier)
        {
            default:
                return 15;
            case 1:
                return 7;
            case 2:
                return 0;
        }
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        this.tier = par1NBTTagCompound.getInteger("tier");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("tier", this.tier);
    }

    @Override
    public int getTier()
    {
        return this.tier;
    }

    @Override
    public void setTier(int tier)
    {
        this.tier = tier;
    }
    
    @Override
    public boolean canSetFacing(int facing)
    {
    	return facing != 0 && facing != 1;
    }

    @Override
	public void onPlace()
	{
		Coord4D pos = Coord4D.get(this);
		MekanismUtils.makeBoundingBlock(worldObj, pos.getFromSide(ForgeDirection.UP, 1), pos);
		MekanismUtils.makeBoundingBlock(worldObj, pos.getFromSide(ForgeDirection.UP, 2), pos);
	}

	@Override
	public void onBreak()
	{
		worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
		worldObj.setBlockToAir(xCoord, yCoord+2, zCoord);

		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

	@Override
	public void onUpdate() {}
}
