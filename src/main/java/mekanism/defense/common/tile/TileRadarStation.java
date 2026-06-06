package defense.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.integration.IComputerIntegration;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityElectricBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.ForgeDirection;
import defense.api.FrequencyGrid;
import defense.api.IBlockFrequency;
import defense.api.IItemFrequency;
import defense.api.IRadarDetectable;
import defense.api.RadarRegistry;
import defense.common.DefenseTech;
import defense.common.Vector2;
import defense.common.base.IBlockActivate;
import defense.common.base.IChunkLoadHandler;
import defense.common.base.IRedstoneProvider;
import defense.common.entity.EntityMissile;

public class TileRadarStation extends TileEntityElectricBlock implements IChunkLoadHandler, IRedstoneProvider, IComputerIntegration, IBlockActivate, IBlockFrequency
{
    public final static int MAX_DETECTION_RANGE = 500;

    public float rotation = 0;
    public int alarmRange = 100;
    public int safetyRange = 50;
    public List<Entity> detectedEntities = new ArrayList<Entity>();
    
    public boolean initiated = false;

    public List<TileEntity> detectedTiles = new ArrayList<TileEntity>();
    public boolean emitAll = true;
    /** List of all incoming missiles, in order of distance. */
    private List<EntityMissile> incomingMissiles = new ArrayList<EntityMissile>();
    
    public static final double ENERGY_USAGE = 400;
    
    protected int frequency = 0;

    private Ticket ticket;

    public TileRadarStation()
    {
    	super("RadarStation", 1000000);
        RadarRegistry.register(this);
    }

    public void initiate()
    {
    	FrequencyGrid.instance().register(this);
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        chunkLoaderInit(ForgeChunkManager.requestTicket(DefenseTech.INSTANCE, worldObj, Type.NORMAL));
    }
    
    @Override
    public String getInventoryName()
    {
    	return LangUtils.localize("machine.RadarStation.name");
    }

    @Override
    public void chunkLoaderInit(Ticket t)
    {
        if(!worldObj.isRemote)
        {
            if(ticket == null && t != null)
            {
                ticket = t;
                Coord4D.get(this).write(ticket.getModData());
                ForgeChunkManager.forceChunk(ticket, new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4));
            }
        }
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

        if(!worldObj.isRemote)
        {
            //Update client every 2 seconds
            if(ticker % 40 == 0)
            {
            	Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));;;
            }
        }

        //If we have energy
        if(getEnergy() >= ENERGY_USAGE)
        {
            rotation += 0.08f;

            if(rotation > 360)
            {
                rotation = 0;
            }

            if(!worldObj.isRemote)
            {
            	setEnergy(getEnergy() - ENERGY_USAGE);
            }

            int prevDetectedEntities = detectedEntities.size();

            // Do a radar scan
            doScan();

            if(prevDetectedEntities != detectedEntities.size())
            {
                worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            }
            //Check for incoming and launch anti-missiles if
            if(ticker % 20 == 0 && incomingMissiles.size() > 0)
            {
                for(IBlockFrequency blockFrequency : FrequencyGrid.instance().get())
                {
                    if(blockFrequency instanceof TileLauncherPrefab)
                    {
                        TileLauncherPrefab launcher = (TileLauncherPrefab)blockFrequency;

                        if(new Pos3D(this).distance(new Pos3D(launcher)) < alarmRange && launcher.getFrequency() == getFrequency())
                        {
                            if(launcher instanceof TileLauncherScreen)
                            {
                                double height = launcher.getTarget() != null ? launcher.getTarget().yPos : 0;
                                launcher.setTarget(new Pos3D(incomingMissiles.get(0).posX, height, incomingMissiles.get(0).posZ));
                            }
                            else {
                                launcher.setTarget(new Pos3D(incomingMissiles.get(0)));
                            }
                        }
                    }
                }
            }
        }
        else
        {
            if(detectedEntities.size() > 0)
            {
                worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            }

            incomingMissiles.clear();
            detectedEntities.clear();
            detectedTiles.clear();
        }

        if(ticker % 40 == 0)
        {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
        }
    }
    
    @Override
    public int getFrequency()
    {
        return frequency;
    }

    @Override
    public void setFrequency(int freq)
    {
        frequency = freq;
    }

    private void doScan()
    {
        incomingMissiles.clear();
        detectedEntities.clear();
        detectedTiles.clear();

        List<Entity> entities = RadarRegistry.getEntitiesWithinRadius(new Vector2(new Pos3D(this)), MAX_DETECTION_RANGE);

        for(Entity entity : entities)
        {
            if(entity instanceof EntityMissile)
            {
                if(((EntityMissile) entity).airborneTicks > -1)
                {
                    if(!detectedEntities.contains(entity))
                    {
                        detectedEntities.add(entity);
                    }

                    if(isMissileGoingToHit((EntityMissile)entity))
                    {
                        if(incomingMissiles.size() > 0)
                        {
                            /** Sort in order of distance */
                            double dist = new Pos3D(this).distance(new Pos3D(entity));

                            for(int i = 0; i < incomingMissiles.size(); i++)
                            {
                                EntityMissile daoDan = incomingMissiles.get(i);

                                if(dist < new Pos3D(this).distance(new Pos3D(daoDan)))
                                {
                                    incomingMissiles.add(i, (EntityMissile) entity);
                                    break;
                                }
                                else if(i == incomingMissiles.size() - 1)
                                {
                                    incomingMissiles.add((EntityMissile) entity);
                                    break;
                                }
                            }
                        }
                        else {
                            incomingMissiles.add((EntityMissile) entity);
                        }
                    }
                }
            }
            else
            {
                detectedEntities.add(entity);
            }
        }

        List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(xCoord - MAX_DETECTION_RANGE, yCoord - MAX_DETECTION_RANGE, zCoord - MAX_DETECTION_RANGE, xCoord + MAX_DETECTION_RANGE, yCoord + MAX_DETECTION_RANGE, zCoord + MAX_DETECTION_RANGE));

        for(EntityPlayer player : players)
        {
            if(player != null)
            {
                boolean youHuoLuan = false;

                for(int i = 0; i < player.inventory.getSizeInventory(); i++)
                {
                    ItemStack itemStack = player.inventory.getStackInSlot(i);

                    if(itemStack != null)
                    {
                        if(itemStack.getItem() instanceof IItemFrequency)
                        {
                            youHuoLuan = true;
                            break;
                        }
                    }
                }

                if(!youHuoLuan)
                {
                    detectedEntities.add(player);
                }
            }
        }

        for(TileEntity jiQi : RadarRegistry.getTileEntitiesInArea(new Vector2(xCoord - TileRadarStation.MAX_DETECTION_RANGE, zCoord - TileRadarStation.MAX_DETECTION_RANGE), new Vector2(xCoord + TileRadarStation.MAX_DETECTION_RANGE, zCoord + TileRadarStation.MAX_DETECTION_RANGE)))
        {
            if(jiQi instanceof TileRadarStation)
            {
                if(((TileRadarStation) jiQi).getEnergy() > 0)
                {
                    detectedTiles.add(jiQi);
                }
            }
            else
            {
                if(detectedTiles instanceof IRadarDetectable)
                {
                    if(((IRadarDetectable) detectedTiles).canDetect(this))
                    {
                        detectedTiles.add(jiQi);
                    }
                }
                else
                {
                    detectedTiles.add(jiQi);
                }
            }
        }
    }

    /** Checks to see if the missile will hit within the range of the radar station
     * 
     * @param missile - missile being checked
     * @return true if it will */
    public boolean isMissileGoingToHit(EntityMissile missile)
    {
        if(missile == null || missile.targetVector == null)
        {
            return false;
        }
        return (Vector2.distance(new Vector2(new Pos3D(missile)), new Vector2(xCoord, zCoord)) < alarmRange && Vector2.distance(new Vector2(missile.targetVector), new Vector2(xCoord, zCoord)) < safetyRange);
    }
    
    @Override
    public void handlePacketData(ByteBuf dataStream)
    {
    	if(!worldObj.isRemote)
    	{
    		int packetType = dataStream.readInt();
    		
    		if(packetType == 2)
            {
                safetyRange = dataStream.readInt();
            }
            else if(packetType == 3)
            {
                alarmRange = dataStream.readInt();
            }
            else if(packetType == 4)
            {
                frequency = dataStream.readInt();
            }
	    	
	    	return;
    	}
    	
    	super.handlePacketData(dataStream);
    	
    	if(worldObj.isRemote)
    	{
			alarmRange = dataStream.readInt();
			safetyRange = dataStream.readInt();
			frequency = dataStream.readInt();
    	}
    }
    
    @Override
    public ArrayList getNetworkedData(ArrayList data)
    {
    	super.getNetworkedData(data);
    	
    	data.add(alarmRange);
    	data.add(safetyRange);
    	data.add(frequency);
    	
    	return data;
    }
    
    @Override
    public boolean canSetFacing(int facing)
    {
    	return facing != 0 && facing != 1;
    }

    @Override
    public boolean isPoweringTo(ForgeDirection side)
    {
        if(incomingMissiles.size() > 0)
        {
            if(emitAll)
            {
                return true;
            }

            for(EntityMissile incomingMissile : incomingMissiles)
            {
                Vector2 position = new Vector2(new Pos3D(incomingMissile));
                ForgeDirection missileTravelDirection = ForgeDirection.UNKNOWN;
                double closest = -1;

                for(int i = 2; i < 6; i++)
                {
                    double dist = Vector2.distance(position, new Vector2(xCoord + ForgeDirection.getOrientation(i).offsetX, zCoord + ForgeDirection.getOrientation(i).offsetZ));

                    if(dist < closest || closest < 0)
                    {
                        missileTravelDirection = ForgeDirection.getOrientation(i);
                        closest = dist;
                    }
                }

                if(missileTravelDirection.getOpposite() == side)
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isIndirectlyPoweringTo(ForgeDirection side)
    {
        return isPoweringTo(side);
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        safetyRange = nbt.getInteger("safetyBanJing");
        alarmRange = nbt.getInteger("alarmBanJing");
        emitAll = nbt.getBoolean("emitAll");
        frequency = nbt.getInteger("frequency");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("safetyBanJing", safetyRange);
        nbt.setInteger("alarmBanJing", alarmRange);
        nbt.setBoolean("emitAll", emitAll);
        nbt.setInteger("frequency", frequency);
    }

    @Override
    public boolean onActivated(EntityPlayer entityPlayer)
    {
        if(entityPlayer.inventory.getCurrentItem() != null)
        {
            if(MekanismUtils.hasUsableWrench(entityPlayer, xCoord, yCoord, zCoord))
            {
                if(!worldObj.isRemote)
                {
                    emitAll = !emitAll;
                    entityPlayer.addChatMessage(new ChatComponentText(LangUtils.localize("message.radar.redstone") + " " + emitAll));
                }

                return true;
            }
        }

        entityPlayer.openGui(DefenseTech.INSTANCE, 0, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public String[] getMethods()
    {
        return new String[] { "getEntities", "getBlocks" };
    }

    @Override
    public Object[] invoke(int method, Object[] arguments) throws Exception
    {
        if(getEnergy() < ENERGY_USAGE)
        {
            throw new Exception("Radar has insufficient electricity!");
        }

        HashMap<String, Double> returnArray = new HashMap<String, Double>();
        int count = 0;

        switch (method)
        {
            case 0:
                List<Entity> entities = RadarRegistry.getEntitiesWithinRadius(new Vector2(new Pos3D(this)), alarmRange);

                for(Entity entity : entities)
                {
                    returnArray.put("x_" + count, entity.posX);
                    returnArray.put("y_" + count, entity.posY);
                    returnArray.put("z_" + count, entity.posZ);
                    count++;
                }

                return new Object[] { returnArray };
            case 1:
                for(TileEntity jiQi : RadarRegistry.getTileEntitiesInArea(new Vector2(xCoord - TileRadarStation.MAX_DETECTION_RANGE, zCoord - TileRadarStation.MAX_DETECTION_RANGE), new Vector2(xCoord + TileRadarStation.MAX_DETECTION_RANGE, zCoord + TileRadarStation.MAX_DETECTION_RANGE)))
                {
                    returnArray.put("x_" + count, (double) jiQi.xCoord);
                    returnArray.put("y_" + count, (double) jiQi.yCoord);
                    returnArray.put("z_" + count, (double) jiQi.zCoord);
                    count++;
                }
                return new Object[] { returnArray };
        }

        throw new Exception("Invalid Radar Function.");
    }

    @Override
    public void invalidate()
    {
    	FrequencyGrid.instance().unregister(this);
        ForgeChunkManager.releaseTicket(ticket);
        RadarRegistry.unregister(this);
        
        super.invalidate();
    }
}
