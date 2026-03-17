package defense.common.entity;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import defense.api.ExplosionEvent.ExplosivePreDetonationEvent;
import defense.api.ExplosiveType;
import defense.api.IExplosive;
import defense.api.IExplosiveContainer;
import defense.api.ILauncherContainer;
import defense.api.IMissile;
import defense.api.ITarget;
import defense.api.RadarRegistry;
import defense.common.DamageUtility;
import defense.common.DefenseTech;
import defense.common.DefenseTechItems;
import defense.common.Reference;
import defense.common.Settings;
import defense.common.Vector2;
import defense.common.base.IChunkLoadHandler;
import defense.common.explosion.Explosion;
import defense.common.explosive.ExplosiveRegistry;
import defense.common.tile.TileCruiseLauncher;

/** @Author - Calclavia */
public class EntityMissile extends Entity implements IChunkLoadHandler, IExplosiveContainer, IEntityAdditionalSpawnData, IMissile, ITarget
{
    public enum MissileType
    {
        MISSILE,
        CruiseMissile,
        LAUNCHER
    }

    public static final float SPEED = 0.012F;
    
    public boolean createdSound = false;

    public int explosiveID = 0;
    public int maxHeight = 200;
    public Pos3D targetVector = null;
    public Pos3D startPos = null;
    public Pos3D launcherPos = null;
    public boolean isExpoding = false;

    public int targetHeight = 0;
    public int airborneTicks = -1;
    // Difference
    public double deltaPathX;
    public double deltaPathY;
    public double deltaPathZ;
    // Flat Distance
    public double flatDistance;
    // The flight time in ticks
    public float missileFlightTime;
    // Acceleration
    public float acceleration;
    // Hp
    public float damage = 0;
    public float max_damage = 10;
    // Protection Time
    public int protectionTime = 2;

    private Ticket chunkTicket;

    // For anti-ballistic missile
    public Entity lockedTarget;
    // Has this missile lock it's target before?
    public boolean didTargetLockBefore = false;
    // Tracking
    public int trackingVar = -1;
    // For cluster missile
    public int missileCount = 0;

    public double daoDanGaoDu = 2;

    private boolean setExplode;
    private boolean setNormalExplode;

    // Missile Type
    public MissileType missileType = MissileType.MISSILE;

    public Pos3D xiaoDanMotion = new Pos3D();

    private double lockHeight = 3;

    // Used for the rocket launcher preventing the players from killing themselves.
    private final HashSet<Entity> ignoreEntity = new HashSet<Entity>();

    public NBTTagCompound nbtData = new NBTTagCompound();

    public EntityMissile(World world)
    {
        super(world);
        
        setSize(1F, 1F);
        renderDistanceWeight = 3;
        isImmuneToFire = true;
        ignoreFrustumCheck = true;
    }

    /** Spawns a traditional missile and cruise missiles
     * 
     * @param explosiveId - Explosive ID
     * @param start - Starting Position
     * @param launcher - Missile Launcher Position */
    public EntityMissile(World world, Pos3D start, Pos3D launcher, int explosiveId)
    {
        this(world);
        
        explosiveID = explosiveId;
        startPos = start;
        launcherPos = launcher;

        setPosition(startPos.xPos, startPos.yPos, startPos.zPos);
        setRotation(0, 90);
    }

    /** For rocket launchers
     * 
     * @param id - Explosive ID
     * @param start - Starting Position
     * @param yaw - The yaw of the missle
     * @param pitch - the pitch of the missle */
    public EntityMissile(World world, Pos3D start, int id, float yaw, float pitch)
    {
        this(world);
        
        explosiveID = id;
        launcherPos = startPos = start;
        missileType = MissileType.LAUNCHER;
        protectionTime = 0;

        setPosition(startPos.xPos, startPos.yPos, startPos.zPos);
        setRotation(yaw, pitch);
    }

    @Override
    public String getCommandSenderName()
    {
        return ExplosiveRegistry.get(explosiveID).getMissileName();
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        try {
            data.writeInt(explosiveID);
            data.writeInt(missileType.ordinal());

            data.writeDouble(startPos.xPos);
            data.writeDouble(startPos.yPos);
            data.writeDouble(startPos.zPos);

            data.writeInt((int)launcherPos.xPos);
            data.writeInt((int)launcherPos.yPos);
            data.writeInt((int)launcherPos.zPos);

            data.writeFloat(rotationYaw);
            data.writeFloat(rotationPitch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        try {
            explosiveID = data.readInt();
            missileType = MissileType.values()[data.readInt()];
            startPos = new Pos3D(data.readDouble(), data.readDouble(), data.readDouble());
            launcherPos = new Pos3D(data.readInt(), data.readInt(), data.readInt());

            rotationYaw = data.readFloat();
            rotationPitch = data.readFloat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void launch(Pos3D target)
    {
        startPos = new Pos3D(this);
        targetVector = target;
        targetHeight = (int)targetVector.yPos;
        ((Explosion)ExplosiveRegistry.get(explosiveID)).launch(this);
        airborneTicks = 0;
        recalculatePath();
        worldObj.playSoundAtEntity(this, Reference.PREFIX + "missilelaunch", 4F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        // TODO add an event system here
        RadarRegistry.register(this);
        DefenseTech.LOGGER.info("Launching " + getCommandSenderName() + " (" + getEntityId() + ") from " + (int)startPos.xPos + ", " + (int)startPos.yPos + ", " + (int)startPos.zPos + " to " + (int)targetVector.xPos + ", " + (int)targetVector.yPos + ", " + (int)targetVector.zPos);
    }

    @Override
    public void launch(Pos3D target, int height)
    {
        lockHeight = height;
        launch(target);
    }

    public EntityMissile ignore(Entity entity)
    {
        ignoreEntity.add(entity);
        return this;
    }

    /** Recalculates required parabolic path for the missile Registry */
    public void recalculatePath()
    {
        if(targetVector != null)
        {
            // Calculate the distance difference of the missile
            deltaPathX = targetVector.xPos - startPos.xPos;
            deltaPathY = targetVector.yPos - startPos.yPos;
            deltaPathZ = targetVector.zPos - startPos.zPos;

            // TODO: Calculate parabola and relative out the height.
            // Calculate the power required to reach the target co-ordinates
            // Ground Displacement
            flatDistance = Vector2.distance(new Vector2(startPos), new Vector2(targetVector));
            // Parabolic Height
            maxHeight = 160 + (int)(flatDistance * 3);
            // Flight time
            missileFlightTime = (float)Math.max(100, 2 * flatDistance) - airborneTicks;
            // Acceleration
            acceleration = (float)maxHeight * 2 / (missileFlightTime * missileFlightTime);
        }
    }

    @Override
    public void entityInit()
    {
        dataWatcher.addObject(16, -1);
        dataWatcher.addObject(17, 0);
        chunkLoaderInit(ForgeChunkManager.requestTicket(DefenseTech.INSTANCE, worldObj, Type.ENTITY));
    }

    @Override
    public void chunkLoaderInit(Ticket ticket)
    {
        if(!worldObj.isRemote)
        {
            if(ticket != null)
            {
                if(chunkTicket == null)
                {
                    chunkTicket = ticket;
                    chunkTicket.bindEntity(this);
                    chunkTicket.getModData();
                }

                ForgeChunkManager.forceChunk(chunkTicket, new ChunkCoordIntPair(chunkCoordX, chunkCoordZ));
            }
        }
    }

    final List<ChunkCoordIntPair> loadedChunks = new ArrayList<ChunkCoordIntPair>();

    public void updateLoadChunk(int newChunkX, int newChunkZ)
    {
        if(!worldObj.isRemote && Settings.LOAD_CHUNKS && chunkTicket != null)
        {
            for(ChunkCoordIntPair chunk : loadedChunks)
            {
                ForgeChunkManager.unforceChunk(chunkTicket, chunk);
            }

            loadedChunks.clear();
            loadedChunks.add(new ChunkCoordIntPair(newChunkX, newChunkZ));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX + 1, newChunkZ + 1));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX - 1, newChunkZ - 1));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX + 1, newChunkZ - 1));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX - 1, newChunkZ + 1));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX + 1, newChunkZ));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX, newChunkZ + 1));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX - 1, newChunkZ));
            loadedChunks.add(new ChunkCoordIntPair(newChunkX, newChunkZ - 1));

            for(ChunkCoordIntPair chunk : loadedChunks)
            {
                ForgeChunkManager.forceChunk(chunkTicket, chunk);
            }
        }
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    public void onUpdate()
    {
        if(!worldObj.isRemote)
        {
            ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(worldObj, posX, posY, posZ, ExplosiveType.AIR, ExplosiveRegistry.get(explosiveID));
            MinecraftForge.EVENT_BUS.post(evt);

            if(evt.isCanceled())
            {
                if(airborneTicks >= 0)
                {
                    dropMissileAsItem();
                }

                setDead();
                return;
            }
        }
        else {
        	if(!createdSound)
        	{
        		DefenseTech.proxy.playSound(this);
        		createdSound = true;
        	}
        }

        try {
            if(worldObj.isRemote)
            {
                airborneTicks = dataWatcher.getWatchableObjectInt(16);
                int status = dataWatcher.getWatchableObjectInt(17);

                switch(status)
                {
                    case 1:
                        setNormalExplode = true;
                        break;
                    case 2:
                        setExplode = true;
                        break;
                }
            }
            else {
                dataWatcher.updateObject(16, airborneTicks);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(setNormalExplode)
        {
            normalExplode();
            return;
        }

        if(setExplode)
        {
            explode();
            return;
        }

        if(airborneTicks >= 0)
        {
            RadarRegistry.register(this);

            if(!worldObj.isRemote)
            {
                if(missileType == MissileType.CruiseMissile || missileType == MissileType.LAUNCHER)
                {
                    if(airborneTicks == 0 && xiaoDanMotion != null)
                    {
                        xiaoDanMotion = new Pos3D(deltaPathX / (missileFlightTime * 0.3), deltaPathY / (missileFlightTime * 0.3), deltaPathZ / (missileFlightTime * 0.3));
                        motionX = xiaoDanMotion.xPos;
                        motionY = xiaoDanMotion.yPos;
                        motionZ = xiaoDanMotion.zPos;
                    }

                    rotationPitch = (float)(Math.atan(motionY / (Math.sqrt(motionX * motionX + motionZ * motionZ))) * 180 / Math.PI);

                    // Look at the next point
                    rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180 / Math.PI);

                    ((Explosion)ExplosiveRegistry.get(explosiveID)).update(this);

                    Block block = worldObj.getBlock((int)posX, (int)posY, (int)posZ);

                    if(protectionTime <= 0 && ((!worldObj.isAirBlock((int)posX, (int)posY, (int)posZ) && !(block instanceof BlockLiquid)) || posY > 1000 || isCollided || airborneTicks > 20 * 1000 || (motionX == 0 && motionY == 0 && motionZ == 0)))
                    {
                        setExplode();
                        return;
                    }

                    moveEntity(motionX, motionY, motionZ);
                }
                else {
                    // Start the launch
                    if(lockHeight > 0)
                    {
                        motionY = SPEED * airborneTicks * (airborneTicks / 2);
                        motionX = 0;
                        motionZ = 0;
                        lockHeight -= motionY;
                        moveEntity(motionX, motionY, motionZ);

                        if(lockHeight <= 0)
                        {
                            motionY = acceleration * (missileFlightTime / 2);
                            motionX = deltaPathX / missileFlightTime;
                            motionZ = deltaPathZ / missileFlightTime;
                        }
                    }
                    else {
                        motionY -= acceleration;

                        rotationPitch = (float)(Math.atan(motionY / (Math.sqrt(motionX * motionX + motionZ * motionZ))) * 180 / Math.PI);

                        // Look at the next point
                        rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180 / Math.PI);

                        ((Explosion)ExplosiveRegistry.get(explosiveID)).update(this);

                        moveEntity(motionX, motionY, motionZ);

                        // If the missile contacts anything, it will explode.
                        if(isCollided)
                        {
                            explode();
                        }

                        // If the missile is commanded to explode before impact
                        if(targetHeight > 0 && motionY < 0)
                        {
                            if(!worldObj.isAirBlock((int)posX, (int)posY - targetHeight, (int)posZ))
                            {
                                targetHeight = 0;
                                explode();
                            }
                        }
                    }
                }
            }
            else {
                rotationPitch = (float)(Math.atan(motionY / (Math.sqrt(motionX * motionX + motionZ * motionZ))) * 180 / Math.PI);
                // Look at the next point
                rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180 / Math.PI);
            }

            lastTickPosX = posX;
            lastTickPosY = posY;
            lastTickPosZ = posZ;

            spawnMissileSmoke();
            protectionTime--;
            airborneTicks++;
        }
        else if(missileType != MissileType.LAUNCHER)
        {
            // Check to find the launcher in which this missile belongs in.
            ILauncherContainer launcher = getLauncher();

            if(launcher != null)
            {
                launcher.setContainingMissile(this);

                /** Rotate the missile to the cruise launcher's rotation. */
                if(launcher instanceof TileCruiseLauncher)
                {
                    missileType = MissileType.CruiseMissile;
                    noClip = true;

                    if(worldObj.isRemote)
                    {
                        rotationYaw = -((TileCruiseLauncher)launcher).rotationYaw + 90;
                        rotationPitch = ((TileCruiseLauncher)launcher).rotationPitch;
                    }

                    posY = ((TileCruiseLauncher)launcher).yCoord + 1;
                }
            }
            else {
                setDead();
            }
        }
        
    	List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.5, 0.5, 0.5));
    	
    	for(Entity e : list)
    	{
    		entityBoundCheck(e);
    	}

        super.onUpdate();
    }

    @Override
    public ILauncherContainer getLauncher()
    {
        if(launcherPos != null)
        {
            TileEntity tileEntity = worldObj.getTileEntity((int)launcherPos.xPos, (int)launcherPos.yPos, (int)launcherPos.zPos);

            if(tileEntity != null && tileEntity instanceof ILauncherContainer)
            {
                if(!tileEntity.isInvalid())
                {
                    return (ILauncherContainer) tileEntity;
                }
            }
        }

        return null;
    }

    @Override
    public boolean interactFirst(EntityPlayer entityPlayer)
    {
        if(((Explosion)ExplosiveRegistry.get(explosiveID)) != null)
        {
            if(((Explosion)ExplosiveRegistry.get(explosiveID)).onInteract(this, entityPlayer))
            {
                return true;
            }
        }

        if(!worldObj.isRemote && (riddenByEntity == null || riddenByEntity == entityPlayer))
        {
            entityPlayer.mountEntity(this);
            return true;
        }

        return false;
    }

    @Override
    public double getMountedYOffset()
    {
        if(missileFlightTime <= 0 && missileType == MissileType.MISSILE)
        {
            return height;
        }
        else if(missileType == MissileType.CruiseMissile)
        {
            return height / 10;
        }

        return height / 2 + motionY;
    }

    private void spawnMissileSmoke()
    {
        if(worldObj.isRemote)
        {
            Pos3D position = new Pos3D(this);
            // The distance of the smoke relative
            // to the missile.
            double distance = -daoDanGaoDu - 0.2f;
            Pos3D delta = new Pos3D();
            // The delta Y of the smoke.
            delta.yPos = Math.sin(Math.toRadians(rotationPitch)) * distance;
            // The horizontal distance of the
            // smoke.
            double dH = Math.cos(Math.toRadians(rotationPitch)) * distance;
            // The delta X and Z.
            delta.xPos = Math.sin(Math.toRadians(rotationYaw)) * dH;
            delta.zPos = Math.cos(Math.toRadians(rotationYaw)) * dH;

            position.translate(delta);
            worldObj.spawnParticle("flame", position.xPos, position.yPos, position.zPos, 0, 0, 0);
            DefenseTech.proxy.spawnParticle("missile_smoke", worldObj, position, 4, 2);
            position.rotatePitch(this.rotationPitch);
            position.rotateYaw(this.rotationYaw);
            position.scale(1 - 0.001 * Math.random());
            DefenseTech.proxy.spawnParticle("missile_smoke", worldObj, position, 4, 2);
            position.scale(1 - 0.001 * Math.random());
            DefenseTech.proxy.spawnParticle("missile_smoke", worldObj, position, 4, 2);
            position.scale(1 - 0.001 * Math.random());
            DefenseTech.proxy.spawnParticle("missile_smoke", worldObj, position, 4, 2);
        }
    }

    public void entityBoundCheck(Entity entity)
    {
        if(ignoreEntity.contains(entity))
        {
        	return;
        }

        // Make sure the entity is not an item
        if(!(entity instanceof EntityItem) && entity != riddenByEntity && protectionTime <= 0)
        {
            if(entity instanceof EntityMissile)
            {
                ((EntityMissile)entity).setNormalExplode();
            }

            setExplode();
        }
    }

    @Override
    public Pos3D getPredictedPosition(int t)
    {
        Pos3D guJiDiDian = new Pos3D(this);
        double tempMotionY = motionY;

        if(airborneTicks > 20)
        {
            for(int i = 0; i < t; i++)
            {
                if(missileType == MissileType.CruiseMissile || missileType == MissileType.LAUNCHER)
                {
                    guJiDiDian.xPos += xiaoDanMotion.xPos;
                    guJiDiDian.yPos += xiaoDanMotion.yPos;
                    guJiDiDian.zPos += xiaoDanMotion.zPos;
                }
                else {
                    guJiDiDian.xPos += motionX;
                    guJiDiDian.yPos += tempMotionY;
                    guJiDiDian.zPos += motionZ;

                    tempMotionY -= acceleration;
                }
            }
        }

        return guJiDiDian;
    }

    @Override
    public void setNormalExplode()
    {
        setNormalExplode = true;
        dataWatcher.updateObject(17, 1);
    }

    @Override
    public void setExplode()
    {
        setExplode = true;
        dataWatcher.updateObject(17, 2);
    }

    @Override
    public void setDead()
    {
        RadarRegistry.unregister(this);

        if(chunkTicket != null)
        {
            ForgeChunkManager.releaseTicket(chunkTicket);
        }

        super.setDead();
    }

    @Override
    public void explode()
    {
        try {
            // Make sure the missile is not already exploding
            if(!isExpoding)
            {
                if(explosiveID == 0)
                {
                    if(!worldObj.isRemote)
                    {
                        worldObj.createExplosion(this, posX, posY, posZ, 5F, true);
                    }
                }
                else {
                    ((Explosion)ExplosiveRegistry.get(explosiveID)).createExplosion(worldObj, posX, posY, posZ, this);
                }

                isExpoding = true;

                DefenseTech.LOGGER.info(getCommandSenderName() + " (" + getEntityId() + ") exploded in " + (int)posX + ", " + (int)posY + ", " + (int)posZ);
            }

            setDead();
        } catch(Exception e) {
            DefenseTech.LOGGER.severe("Missile failed to explode properly. Report this to the developers.");
            e.printStackTrace();
            setDead();
        }
    }

    @Override
    public void normalExplode()
    {
        if(!isExpoding)
        {
            isExpoding = true;

            if(!worldObj.isRemote)
            {
                worldObj.createExplosion(this, posX, posY, posZ, 5F, true);
            }

            setDead();
        }
    }

    @Override
    public void dropMissileAsItem()
    {
        if(!isExpoding && !worldObj.isRemote)
        {
            EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(DefenseTechItems.itemMissile, 1, explosiveID));

            float var13 = 0.05F;
            Random random = new Random();
            entityItem.motionX = ((float) random.nextGaussian() * var13);
            entityItem.motionY = ((float) random.nextGaussian() * var13 + 0.2F);
            entityItem.motionZ = ((float) random.nextGaussian() * var13);
            worldObj.spawnEntityInWorld(entityItem);
        }

        setDead();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        startPos = Pos3D.read(nbt.getCompoundTag("startPos"));
        targetVector = Pos3D.read(nbt.getCompoundTag("targetVector"));
        launcherPos = Pos3D.read(nbt.getCompoundTag("launcherPos"));
        acceleration = nbt.getFloat("acceleration");
        targetHeight = nbt.getInteger("targetHeight");
        explosiveID = nbt.getInteger("explosiveID");
        airborneTicks = nbt.getInteger("airborneTicks");
        lockHeight = nbt.getDouble("lockHeight");
        missileType = MissileType.values()[nbt.getInteger("missileType")];
        nbtData = nbt.getCompoundTag("data");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        if(startPos != null)
        {
            nbt.setTag("startPos", startPos.write(new NBTTagCompound()));
        }
        
        if(targetVector != null)
        {
            nbt.setTag("targetVector", targetVector.write(new NBTTagCompound()));
        }

        if(launcherPos != null)
        {
            nbt.setTag("launcherPos", launcherPos.write(new NBTTagCompound()));
        }

        nbt.setFloat("acceleration", acceleration);
        nbt.setInteger("explosiveID", explosiveID);
        nbt.setInteger("targetHeight", targetHeight);
        nbt.setInteger("airborneTicks", airborneTicks);
        nbt.setDouble("lockHeight", lockHeight);
        nbt.setInteger("missileType", missileType.ordinal());
        nbt.setTag("data", nbtData);
    }

    @Override
    public float getShadowSize()
    {
        return 1.0F;
    }

    @Override
    public int getTicksInAir()
    {
        return airborneTicks;
    }

    @Override
    public IExplosive getExplosiveType()
    {
        return ExplosiveRegistry.get(explosiveID);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage)
    {
        if(DamageUtility.canHarm(this, source, damage))
        {
            damage += damage;
            
            if(damage >= max_damage)
            {
                setDead();
            }
            
            return true;
        }
        
        return false;
    }

    @Override
    public boolean canBeTargeted(Object turret)
    {
        return getTicksInAir() > 0;
    }

    @Override
    public TargetType getType()
    {
        return TargetType.MISSILE;
    }

    @Override
    public NBTTagCompound getTagCompound()
    {
        return nbtData;
    }
}
