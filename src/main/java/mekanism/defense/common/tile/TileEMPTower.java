package defense.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import mekanism.api.Coord4D;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.integration.IComputerIntegration;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityElectricBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import defense.api.RadarRegistry;
import defense.common.DefenseTech;
import defense.common.Reference;
import defense.common.base.IBlockActivate;
import defense.common.explosive.blast.BlastEMP;

public class TileEMPTower extends TileEntityElectricBlock implements IBoundingBlock, IBlockActivate, IComputerIntegration
{
    // The maximum possible radius for the EMP to strike
    public static final int MAX_RADIUS = 150;

    public float rotation = 0;
    private float rotationDelta = 0;

    // The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
    public byte empMode = 0;

    private int cooldownTicks = 0;

    // The EMP explosion radius
    public int empRadius = 60;
    
    public double energyToUse = 0;
    
    public boolean packet = false;

    public TileEMPTower()
    {
    	super("EMPTower", 10000000);
        RadarRegistry.register(this);
        updateCapacity();
    }
    
    @Override
    public String getInventoryName()
    {
    	return LangUtils.localize("machine.EmpTower.name");
    }

    @Override
    public void invalidate()
    {
        RadarRegistry.unregister(this);
        super.invalidate();
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (!isReady())
        {
            cooldownTicks--;
        }

        if (ticker % 20 == 0 && getEnergy() > 0)
            worldObj.playSoundEffect(xCoord, yCoord, zCoord, Reference.PREFIX + "machinehum", 0.5F, 0.85F * (float)getEnergy() / (float)getMaxEnergy());

        rotationDelta = (float) (Math.pow(getEnergy() / getMaxEnergy(), 2) * 0.5);
        rotation += rotationDelta;
        if (rotation > 360)
            rotation = 0;
        
        if(!worldObj.isRemote && ticker % 20 == 0)
        {
        	Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
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
    			empRadius = dataStream.readInt();
                updateCapacity();
    		}
    		else if(packetType == 2)
    		{
    			empMode = dataStream.readByte();
    		}
	    	
	    	return;
    	}
    	
    	super.handlePacketData(dataStream);
    	
    	if(worldObj.isRemote)
    	{
	    	int prev = empRadius;
	    	
	    	empRadius = dataStream.readInt();
	        empMode = dataStream.readByte();
	        
	        if(dataStream.readBoolean())
	        {
	        	switch (this.empMode)
	            {
	                default:
	                    new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().setEffectEntities().explode();
	                    break;
	                case 1:
	                    new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectEntities().explode();
	                    break;
	                case 2:
	                    new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().explode();
	                    break;
	            }
	        }
	        
	        if(prev != empRadius)
	        {
	        	updateCapacity();
	        }
    	}
    }
    
    @Override
    public ArrayList getNetworkedData(ArrayList data)
    {
    	super.getNetworkedData(data);
    	
    	data.add(empRadius);
    	data.add(empMode);
    	data.add(packet);
    	
    	packet = false;
    	
    	return data;
    }

    //@Callback
    public boolean isReady()
    {
        return getCooldown() <= 0;
    }

    //@Callback
    public int getCooldown()
    {
        return cooldownTicks;
    }

    //@Callback
    public int getMaxCooldown()
    {
        return 120;
    }

    private void updateCapacity()
    {
        maxEnergy = Math.max(3000000 * ((double)this.empRadius / (double)MAX_RADIUS), 10000000);
        energyToUse = getMaxEnergy()/4D;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        this.empRadius = par1NBTTagCompound.getInteger("banJing");
        this.empMode = par1NBTTagCompound.getByte("muoShi");
        
        updateCapacity();
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);

        par1NBTTagCompound.setInteger("banJing", this.empRadius);
        par1NBTTagCompound.setByte("muoShi", this.empMode);
    }

    //@Callback(limit = 1)
    public boolean fire()
    {
    	if(!worldObj.isRemote)
    	{
	        if (getEnergy() >= energyToUse)
	        {
	            if (isReady())
	            {
	                switch (this.empMode)
	                {
	                    default:
	                        new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().setEffectEntities().explode();
	                        break;
	                    case 1:
	                        new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectEntities().explode();
	                        break;
	                    case 2:
	                        new BlastEMP(this.worldObj, null, this.xCoord, this.yCoord, this.zCoord, this.empRadius).setEffectBlocks().explode();
	                        break;
	                }
	                
	                setEnergy(getEnergy()-energyToUse);
	                this.cooldownTicks = getMaxCooldown();
	                packet = true;
	                
	                Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
	                
	                return true;
	            }
	        }
    	}
    	
        return false;
    }
    
    @Override
    public boolean canSetFacing(int facing)
    {
    	return facing != 0 && facing != 1;
    }

    @Override
    public void onPowerChange()
    {
    	if(redstone)
    	{
    		fire();
    	}
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
	public void onPlace()
	{
		Coord4D pos = Coord4D.get(this);
		MekanismUtils.makeBoundingBlock(worldObj, pos.getFromSide(ForgeDirection.UP, 1), pos);
	}

	@Override
	public void onBreak()
	{
		worldObj.setBlockToAir(xCoord, yCoord+1, zCoord);
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

	private static final String[] methods = new String[]{"getEMPMode", "setEMPMode", "targetMissiles", "targetAll", "targetElectronics", "getEMPRadius", "getMaxEMPRadius", "setEMPRadius"};

	@Override
	public String[] getMethods()
	{
		return methods;
	}

	@Override
	public Object[] invoke(int method, Object[] args) throws Exception
	{
		switch(method)
		{
			case 0:
				return new Object[]{getEmpMode()};
			case 1:
				if(args.length < 1 || !(args[0] instanceof Number))
				{
					return new Object[] {false, "first argument needs to be a number between 0 and 2"};
				}
				return new Object[]{setEmpMode(((Number) args[0]).byteValue())};
			case 2:
				empMissiles();
				return new Object[]{};
			case 3:
				empAll();
				return new Object[]{};
			case 4:
				empElectronics();
				return new Object[]{};
			case 5:
				return new Object[]{getEmpRadius()};
			case 6:
				return new Object[]{getMaxEmpRadius()};
			case 7:
				if(args.length < 1 || !(args[0] instanceof Number))
				{
					return new Object[] {false, "first argument needs to be a number"};
				}
				setEmpRadius(((Number) args[0]).intValue());
				return new Object[]{true};
			default:
				throw new NoSuchMethodException();
		}
	}

    public byte getEmpMode()
    {
        return empMode;
    }

    public boolean setEmpMode(byte empMode)
    {
        if (empMode >= 0 && empMode <= 2)
		{
			this.empMode = empMode;
			return true;
		}
		return false;
    }

    public void empMissiles()
    {
        this.empMode = 1;
    }

    public void empAll()
    {
        this.empMode = 0;
    }

    public void empElectronics()
    {
        this.empMode = 2;
    }

    public int getEmpRadius()
    {
        return empRadius;
    }

    public int getMaxEmpRadius()
    {
        return MAX_RADIUS;
    }

	public void setEmpRadius(int empRadius)
    {
        int prev = getEmpRadius();
        this.empRadius = Math.min(Math.max(empRadius, 0), MAX_RADIUS);
        if (prev != getEmpRadius())
            updateCapacity();
    }
}
