package defense.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import defense.api.IMissile;
import defense.api.ITier;
import defense.common.DefenseTech;
import defense.common.base.IBlockActivate;

/** This tile entity is for the screen of the missile launcher
 * 
 * @author Calclavia */
public class TileLauncherScreen extends TileLauncherPrefab implements IBlockActivate, ITier
{
    // The tier of this screen
    private int tier = 0;

    // The missile launcher base in which this
    // screen is connected with
    public TileLauncherBase launcherBase = null;

    public short launchHeight = 3;

    private final Set<EntityPlayer> playersUsing = new HashSet<EntityPlayer>();

    public TileLauncherScreen()
    {
    	super("LauncherScreen", 10000000);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if(launcherBase == null)
        {
            for(ForgeDirection side : MekanismUtils.SIDE_DIRS)
            {
                Coord4D position = new Coord4D(xCoord, yCoord, zCoord);
                position.step(side);

                TileEntity tileEntity = position.getTileEntity(worldObj);

                if(tileEntity != null)
                {
                    if(tileEntity instanceof TileLauncherBase)
                    {
                        launcherBase = (TileLauncherBase)tileEntity;
                        setFacing((short)side.getOpposite().ordinal());
                    }
                }
            }
        }
        else {
            if(launcherBase.isInvalid())
            {
                launcherBase = null;
            }
        }

        if(!worldObj.isRemote)
        {
	        if(ticker % 40 == 0 && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
	        {
	            launch();
	        }
        }

        if(!worldObj.isRemote)
        {
            if(ticker % 3 == 0)
            {
                if(targetPos == null)
                {
                    targetPos = new Pos3D(xCoord, 0, zCoord);
                }
            }

            if(ticker % 600 == 0)
            {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }
    
    @Override
	public void handlePacketData(ByteBuf dataStream)
	{
    	if(!worldObj.isRemote)
    	{
    		int packetType = dataStream.readInt();
    		
    		if(packetType == 1)
    		{
    			setFrequency(dataStream.readInt());
    		}
    		else if(packetType == 2)
    		{
    			targetPos = new Pos3D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt());

                if(getTier() < 2)
                {
                    targetPos.yPos = 0;
                }
    		}
    		else if(packetType == 3)
    		{
    			launchHeight = (short)Math.max(Math.min(dataStream.readShort(), Short.MAX_VALUE), 3);
    		}
    		
    		return;
    	}
    	
		super.handlePacketData(dataStream);
		
		if(worldObj.isRemote)
		{
	        tier = dataStream.readInt();
	        launchHeight = dataStream.readShort();
		}
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		
		data.add(tier);
		data.add(launchHeight);
		
		return data;
	}

    @Override
    public void placeMissile(ItemStack itemStack)
    {
        if(launcherBase != null)
        {
            if(!launcherBase.isInvalid())
            {
                launcherBase.setInventorySlotContents(0, itemStack);
            }
        }
    }

    // Checks if the missile is launchable
    @Override
    public boolean canLaunch()
    {
        if(launcherBase != null && launcherBase.missile != null)
        {
            if(getEnergy() >= getLaunchCost())
            {
                return launcherBase.isInRange(targetPos);
            }
        }
        
        return false;
    }

    /** Calls the missile launcher base to launch it's missile towards a targeted location */
    @Override
    public void launch()
    {
        if(canLaunch())
        {
            setEnergy(getEnergy()-getLaunchCost());
            launcherBase.launchMissile(targetPos.clone(), launchHeight);
        }
    }

    /** Gets the display status of the missile launcher
     * 
     * @return The string to be displayed */
    @Override
    public String getStatus()
    {
        String color = "\u00a74";
        String status = LangUtils.localize("gui.misc.idle");

        if(launcherBase == null)
        {
            status = LangUtils.localize("gui.launcherScreen.statusMissing");
        }
        else if(getEnergy() < getLaunchCost())
        {
            status = LangUtils.localize("gui.launcherScreen.statusNoPower");
        }
        else if(launcherBase.missile == null)
        {
            status = LangUtils.localize("gui.launcherScreen.statusEmpty");
        }
        else if(targetPos == null)
        {
            status = LangUtils.localize("gui.launcherScreen.statusInvalid");
        }
        else if(launcherBase.isTargetTooClose(targetPos))
        {
            status = LangUtils.localize("gui.launcherScreen.statusClose");
        }
        else if(launcherBase.isTargetTooFar(targetPos))
        {
            status = LangUtils.localize("gui.launcherScreen.statusFar");
        }
        else {
            color = "\u00a72";
            status = LangUtils.localize("gui.launcherScreen.statusReady");
        }

        return color + status;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        tier = par1NBTTagCompound.getInteger("tier");
        launchHeight = par1NBTTagCompound.getShort("launchHeight");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("tier", tier);
        par1NBTTagCompound.setShort("launchHeight", launchHeight);
    }
    
    @Override
    public boolean canSetFacing(int facing)
    {
    	return facing != 0 && facing != 1;
    }

    @Override
    public int getTier()
    {
        return tier;
    }

    @Override
    public void setTier(int t)
    {
        tier = t;
    }

    public long getLaunchCost()
    {
        switch(getTier())
        {
            case 0:
                return 50000;
            case 1:
                return 80000;
        }

        return 100000;
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
    	if(!worldObj.isRemote)
    	{
    		entityPlayer.openGui(DefenseTech.INSTANCE, 0, worldObj, xCoord, yCoord, zCoord);
    	}
    	
        return true;
    }

    @Override
    public LauncherType getLauncherType()
    {
        return LauncherType.TRADITIONAL;
    }

    @Override
    public IMissile getMissile()
    {
        if(launcherBase != null)
        {
            return launcherBase.missile;
        }

        return null;
    }
}
