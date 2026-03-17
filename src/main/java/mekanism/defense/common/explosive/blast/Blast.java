package defense.common.explosive.blast;

import java.util.List;

import mekanism.api.Pos3D;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.ExplosionEvent.DoExplosionEvent;
import defense.api.ExplosionEvent.ExplosionConstructionEvent;
import defense.api.ExplosionEvent.PostExplosionEvent;
import defense.api.ExplosionEvent.PreExplosionEvent;
import defense.api.IExplosion;
import defense.client.model.missile.ModelMissileBase;
import defense.common.entity.EntityExplosion;
import defense.common.entity.EntityMissile;

public abstract class Blast extends Explosion implements IExplosion
{
    public Pos3D position;
    
    public World worldObj;
    
    public EntityExplosion controller = null;

    /** The amount of times the explosion has been called */
    protected int callCount = 0;

    public Blast(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
        
        position = new Pos3D(x, y, z);
        worldObj = world;
    }

    public Blast(World world, Pos3D pos, Entity entity, float size)
    {
        super(world, entity, pos.xPos, pos.yPos, pos.zPos, size);
        
        position = pos;
        worldObj = world;
    }

    public Blast(Entity entity, float size)
    {
        super(entity.worldObj, entity, entity.posX, entity.posY, entity.posZ, size);
        
        position = new Pos3D(entity);
        worldObj = entity.worldObj;
    }

    protected void doPreExplode() {}

    /** Called before an explosion happens. */
    public final void preExplode()
    {
        PreExplosionEvent evt = new PreExplosionEvent(worldObj, this);
        MinecraftForge.EVENT_BUS.post(evt);

        if(!evt.isCanceled())
        {
            doPreExplode();
        }
    }

    /** Called every tick when this explosive is being progressed. */
    protected abstract void doExplode();

    public final void onExplode()
    {
        DoExplosionEvent evt = new DoExplosionEvent(worldObj, this);
        MinecraftForge.EVENT_BUS.post(evt);
        
        if(!evt.isCanceled())
        {
            doExplode();
            callCount++;
        }
    }

    protected void doPostExplode() {}

    /** Called after the explosion is completed. */
    public final void postExplode()
    {
        PostExplosionEvent evt = new PostExplosionEvent(worldObj, this);
        MinecraftForge.EVENT_BUS.post(evt);

        if(!evt.isCanceled())
        {
            this.doPostExplode();
        }
    }

    /** Make the default functions useless. */
    @Override
    public void doExplosionA() {}

    @Override
    public void doExplosionB(boolean par1) {}

    /** All outside classes should call this. */
    @Override
    public void explode()
    {
        ExplosionConstructionEvent evt = new ExplosionConstructionEvent(worldObj, this);
        MinecraftForge.EVENT_BUS.post(evt);

        if(!evt.isCanceled())
        {
            if(proceduralInterval() > 0)
            {
                if(!worldObj.isRemote)
                {
                    worldObj.spawnEntityInWorld(new EntityExplosion(this));
                }
            }
            else {
                doPreExplode();
                doExplode();
                doPostExplode();
            }
        }
    }

    public int countIncrement()
    {
        return 1;
    }

    @Override
    public float getRadius()
    {
        return explosionSize;
    }

    @Override
    public long getEnergy()
    {
        return 0;
    }

    /** The interval in ticks before the next procedural call of this explosive
     * 
     * @param return - Return -1 if this explosive does not need procedural calls */
    public int proceduralInterval()
    {
        return -1;
    }

    protected void doDamageEntities(float radius, float power)
    {
        this.doDamageEntities(radius, power, true);
    }

    protected void doDamageEntities(float radius, float power, boolean destroyItem)
    {
        // Step 2: Damage all entities
        Pos3D minCoord = position.clone();
        minCoord.translate(-radius - 1, -radius - 1, -radius - 1);
        Pos3D maxCoord = position.clone();
        maxCoord.translate(radius + 1, radius + 1, radius + 1);
        List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(minCoord.xPos, minCoord.yPos, minCoord.zPos, maxCoord.xPos, maxCoord.yPos, maxCoord.zPos));
        Vec3 var31 = Vec3.createVectorHelper(position.xPos, position.yPos, position.zPos);

        for(int i = 0; i < allEntities.size(); ++i)
        {
            Entity entity = allEntities.get(i);

            if(onDamageEntity(entity))
            {
                continue;
            }

            if(entity instanceof EntityMissile)
            {
                ((EntityMissile)entity).setExplode();
                continue;
            }

            if(entity instanceof EntityItem && !destroyItem)
            {
                continue;
            }

            double distance = entity.getDistance(position.xPos, position.yPos, position.zPos) / radius;

            if(distance <= 1.0D)
            {
                double xDifference = entity.posX - position.xPos;
                double yDifference = entity.posY - position.yPos;
                double zDifference = entity.posZ - position.zPos;
                double var35 = MathHelper.sqrt_double(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
                xDifference /= var35;
                yDifference /= var35;
                zDifference /= var35;
                double var34 = worldObj.getBlockDensity(var31, entity.boundingBox);
                double var36 = (1.0D - distance) * var34;
                int damage = 0;

                damage = (int)((var36 * var36 + var36) / 2.0D * 8.0D * power + 1.0D);

                entity.attackEntityFrom(DamageSource.setExplosionSource(this), damage);

                entity.motionX += xDifference * var36;
                entity.motionY += yDifference * var36;
                entity.motionZ += zDifference * var36;
            }
        }
    }

    /** Called by doDamageEntity on each entity being damaged. This function should be inherited if
     * something special is to happen to a specific entity.
     * 
     * @return True if something special happens to this specific entity. */
    protected boolean onDamageEntity(Entity entity)
    {
        return false;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        this.callCount = nbt.getInteger("callCount");
        this.explosionSize = nbt.getFloat("explosionSize");
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("callCount", this.callCount);
        nbt.setFloat("explosionSize", this.explosionSize);
    }

    public boolean isMovable()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public ModelMissileBase getRenderModel()
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getRenderResource()
    {
        return null;
    }
}
