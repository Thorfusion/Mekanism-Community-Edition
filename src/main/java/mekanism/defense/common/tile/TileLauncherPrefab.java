package defense.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import mekanism.api.Pos3D;
import mekanism.common.integration.IComputerIntegration;
import mekanism.common.tile.TileEntityElectricBlock;
import net.minecraft.nbt.NBTTagCompound;
import defense.api.FrequencyGrid;
import defense.api.IBlockFrequency;
import defense.api.ILauncherController;

public abstract class TileLauncherPrefab extends TileEntityElectricBlock implements ILauncherController, IComputerIntegration, IBlockFrequency
{
	protected Pos3D targetPos = null;
	protected int frequency = 0;
	
	private boolean initiated = false;
	
    public TileLauncherPrefab(String name, double baseMaxEnergy) 
    {
		super(name, baseMaxEnergy);
	}
    
    @Override
    public void onUpdate()
    {
    	super.onUpdate();
    	
    	if(!initiated)
    	{
    		initiate();
    		initiated = true;
    	}
    }
    
    public void initiate()
    {
    	FrequencyGrid.instance().register(this);
    }
    
    
    @Override
    public void invalidate()
    {
        FrequencyGrid.instance().unregister(this);
        super.invalidate();
    }
    
    @Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);
		
		if(worldObj.isRemote)
		{
			frequency = dataStream.readInt();
			
			if(dataStream.readBoolean())
			{
				targetPos = new Pos3D(dataStream.readDouble(), dataStream.readDouble(), dataStream.readDouble());
			}
		}
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		
		data.add(frequency);
		
		if(targetPos != null)
		{
			data.add(true);
			data.add(targetPos.xPos);
			data.add(targetPos.yPos);
			data.add(targetPos.zPos);
		}
		else {
			data.add(false);
		}
		
		return data;
	}
    
    @Override
    public int getFrequency()
    {
        return this.frequency;
    }

    @Override
    public void setFrequency(int frequency)
    {
        this.frequency = frequency;
    }

    @Override
    public Pos3D getTarget()
    {
        if (this.targetPos == null)
        {
            if (this.getLauncherType() == LauncherType.CRUISE)
            {
                this.targetPos = new Pos3D(this.xCoord, this.yCoord, this.zCoord);
            }
            else
            {
                this.targetPos = new Pos3D(this.xCoord, 0, this.zCoord);
            }
        }

        return this.targetPos;
    }

    @Override
    public void setTarget(Pos3D target)
    {
        this.targetPos = target.floor();
    }

    @Override
    public String[] getMethods()
    {
        return new String[] { "launch", "getTarget", "setTarget", "canLaunch", "getMissile" };
    }

    @Override
    public Object[] invoke(int method, Object[] arguments) throws Exception
    {
        switch (method)
        {
            case 0:
                this.launch();
                return null;
            case 1:
                return new Object[] { this.getTarget().xPos, this.getTarget().yPos, this.getTarget().zPos };
            case 2:
                if (arguments[0] != null && arguments[1] != null && arguments[2] != null)
                {
                    try
                    {
                        this.setTarget(new Pos3D(((Double) arguments[0]).doubleValue(), ((Double) arguments[1]).doubleValue(), ((Double) arguments[2]).doubleValue()));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        throw new Exception("Target Parameter is Invalid.");
                    }
                }
                return null;
            case 3:
                return new Object[] { this.canLaunch() };
            case 4:
                if (this.getMissile() != null)
                {
                    return new Object[] { this.getMissile().getExplosiveType().getMissileName() };
                }
                else
                {
                    return null;
                }
        }

        throw new Exception("Invalid Launcher Function.");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.targetPos = Pos3D.read(nbt.getCompoundTag("target"));
        this.frequency = nbt.getInteger("frequency");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        if (this.targetPos != null)
        {
            nbt.setTag("target", this.targetPos.write(new NBTTagCompound()));
        }
        
        nbt.setInteger("frequency", frequency);
    }
}
