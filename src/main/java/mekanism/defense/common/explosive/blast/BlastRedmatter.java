package defense.common.explosive.blast;

import java.util.List;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import defense.api.IExplosiveIgnore;
import defense.common.DefenseTech;
import defense.common.DefenseUtils;
import defense.common.Reference;
import defense.common.Settings;
import defense.common.entity.EntityExplosion;
import defense.common.entity.EntityExplosive;
import defense.common.entity.EntityFlyingBlock;

public class BlastRedmatter extends Blast
{
    private int maxTakeBlocks = 5;

    public BlastRedmatter(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    @Override
    public void doPreExplode()
    {
        if (!this.worldObj.isRemote)
        {
            this.worldObj.createExplosion(this.exploder, position.xPos, position.yPos, position.zPos, 5.0F, true);
        }
    }

    @Override
    protected void doPostExplode()
    {
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.explosionX - this.explosionSize, this.explosionY - this.explosionSize, this.explosionZ - this.explosionSize, this.explosionX + this.explosionSize, this.explosionY + this.explosionSize, this.explosionZ + this.explosionSize);
        List<?> list = this.worldObj.getEntitiesWithinAABB(EntityExplosion.class, bounds);

        for (Object obj : list)
        {
            if (obj instanceof EntityExplosion)
            {
                EntityExplosion explosion = (EntityExplosion) obj;

                if (explosion.blast instanceof BlastRedmatter)
                {
                    explosion.setDead();
                }
            }
        }

    }

    @Override
    public void doExplode()
    {
        if (Settings.DO_REDMATTER_DESPAWN && callCount >= Settings.MAX_REDMATTER_LIFESPAN)
        {
            this.postExplode();
        }

        // Try to find and grab some blocks to orbit
        if (!this.worldObj.isRemote)
        {
            Pos3D currentPos = new Pos3D();
            int blockID = -1;
            int metadata = -1;
            double dist = -1;
            int takenBlocks = 0;
            Block block = null;

            /** Block removal loop */
            loop:
            for (int radius = 1; radius < this.getRadius(); radius++)
            {
                for (int xCoord = -radius; xCoord < radius; xCoord++)
                {
                    for (int yCoord = -radius; yCoord < radius; yCoord++)
                    {
                        for (int zCoord = -radius; zCoord < radius; zCoord++)
                        {
                            currentPos.xPos = position.xPos + xCoord;
                            currentPos.yPos = position.yPos + yCoord;
                            currentPos.zPos = position.zPos + zCoord;

                            dist = MathHelper.sqrt_double((xCoord * xCoord + yCoord * yCoord + zCoord * zCoord));

                            if (dist > radius || dist < radius - 2)
                                continue;

                            block = currentPos.getCoord(worldObj.provider.dimensionId).getBlock(this.worldObj);
                            metadata = currentPos.getCoord(worldObj.provider.dimensionId).getMetadata(this.worldObj);

                            if(!worldObj.isAirBlock((int)currentPos.xPos, (int)currentPos.yPos, (int)currentPos.zPos) && block.getBlockHardness(this.worldObj, (int)currentPos.xPos, (int)currentPos.yPos, (int)currentPos.zPos) >= 0)
                            {
                            	if(DefenseUtils.canBreak(worldObj, block, currentPos.xPos, currentPos.yPos, currentPos.zPos))
                            	{
	                                worldObj.setBlock((int)currentPos.xPos, (int)currentPos.yPos, (int)currentPos.zPos, Blocks.air, 0, block instanceof BlockLiquid ? 0 : 2);
	                                //TODO: render fluid streams
	                                if(block instanceof BlockLiquid || block instanceof IFluidBlock)
	                                {
	                                    continue;
	                                }
	
	                                currentPos.translate(0.5D, 0.5D, 0.5D);
	
	                                if(worldObj.rand.nextFloat() > 0.8)
	                                {
	                                    EntityFlyingBlock entity = new EntityFlyingBlock(worldObj, currentPos, block, metadata);
	                                    worldObj.spawnEntityInWorld(entity);
	                                    entity.yawChange = 50 * this.worldObj.rand.nextFloat();
	                                    entity.pitchChange = 50 * this.worldObj.rand.nextFloat();
	                                }
	
	                                takenBlocks++;
	                                
	                                if(takenBlocks > maxTakeBlocks)
	                                {
	                                    break loop;
	                                }
                            	}
                            }
                        }
                    }
                }
            }
        }

        /** Entity orbital removal & movement loop */
        float radius = this.getRadius() + this.getRadius() / 2;
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.xPos - radius, position.yPos - radius, position.zPos - radius, position.xPos + radius, position.yPos + radius, position.zPos + radius);
        List<Entity> allEntities = this.worldObj.getEntitiesWithinAABB(Entity.class, bounds);
        boolean doExplosion = true;

        for(Entity entity : allEntities)
        {
            doExplosion = !this.affectEntity(radius, entity, doExplosion);
        }
        /*
         * if (this.worldObj.isRemote) { for (int i = 0; i < 10 * (2 -
         * ZhuYaoZhaPin.proxy.getParticleSetting()); i++) { Pos3D randomVector = new
         * Pos3D(this.worldObj.rand.nextInt((int) this.getRadius()) - this.getRadius(),
         * this.worldObj.rand.nextInt((int) this.getRadius()) - this.getRadius(),
         * this.worldObj.rand.nextInt((int) this.getRadius()) - this.getRadius());
         * ZhuYaoZhaPin.proxy.spawnParticle("smoke", this.worldObj, Pos3D.add(this.position,
         * randomVector), 0, 0, 0, 1, 1, 1, 7.0F, 8); }
         * List<Entity> list = ZhuYaoZhaPin.proxy.getEntityFXs();
         * if (list != null) { for (Entity entity : list) { if (this.position.distanceTo(new
         * Pos3D(entity)) <= radius) { this.affectEntity(radius, entity, false); } } } }
         */

        if (this.worldObj.rand.nextInt(8) == 0)
        {
            this.worldObj.playSoundEffect(position.xPos + (Math.random() - 0.5) * radius, position.yPos + (Math.random() - 0.5) * radius, position.zPos + (Math.random() - 0.5) * radius, Reference.PREFIX + "collapse", 6.0F - this.worldObj.rand.nextFloat(), 1.0F - this.worldObj.rand.nextFloat() * 0.4F);
        }

        this.worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "redmatter", 3.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 1F);
    }

    /** Makes an entity get affected by Red Matter.
     * 
     * @Return True if explosion happened */
    public boolean affectEntity(float radius, Entity entity, boolean doExplosion)
    {
        boolean explosionCreated = false;

        if(entity == controller)
        {
            return false;
        }

        if(entity instanceof IExplosiveIgnore)
        {
            if(((IExplosiveIgnore)entity).canIgnore(this))
            {
                return false;
            }
        }

        if(entity instanceof EntityPlayer)
        {
            if (((EntityPlayer)entity).capabilities.isCreativeMode)
            {
                return false;
            }
        }

        double xDifference = entity.posX - position.xPos;
        double yDifference = entity.posY - position.yPos;
        double zDifference = entity.posZ - position.zPos;

        /** The percentage of the closeness of the entity. */
        double xPercentage = 1 - (xDifference / radius);
        double yPercentage = 1 - (yDifference / radius);
        double zPercentage = 1 - (zDifference / radius);
        double distancePercentage = (this.position.distance(new Pos3D(entity)) / radius);

        Pos3D entityPosition = new Pos3D(entity);
        Pos3D centeredPosition = entityPosition.clone().diff(this.position);
        centeredPosition.rotate(1.5 * distancePercentage * Math.random(), 1.5 * distancePercentage * Math.random(), 1.5 * distancePercentage * Math.random());
        Pos3D newPosition = this.position.clone().translate(centeredPosition);
        // Orbit Velocity
        entity.addVelocity(newPosition.xPos - entityPosition.xPos, 0, newPosition.zPos - entityPosition.zPos);
        // Gravity Velocity
        entity.addVelocity(-xDifference * 0.015 * xPercentage, -yDifference * 0.015 * yPercentage, -zDifference * 0.015 * zPercentage);

        if(worldObj.isRemote)
        {
            if(entity instanceof EntityFlyingBlock)
            {
                if (DefenseTech.proxy.getParticleSetting() == 0)
                {
                    if (this.worldObj.rand.nextInt(5) == 0)
                    {
                        DefenseTech.proxy.spawnParticle("digging", this.worldObj, new Pos3D(entity), -xDifference, -yDifference + 10, -zDifference, Block.getIdFromBlock(((EntityFlyingBlock) entity).block), 0, ((EntityFlyingBlock) entity).metadata, 2, 1);
                    }
                }
            }
        }

        if(new Pos3D(entity.posX, entity.posY, entity.posZ).distance(position) < 4)
        {
            if(doExplosion && !explosionCreated && callCount % 5 == 0)
            {
                /** Inject velocities to prevent this explosion to move RedMatter. */
                Pos3D tempMotion = new Pos3D(this.controller.motionX, this.controller.motionY, this.controller.motionZ);
                this.worldObj.createExplosion(this.exploder, entity.posX, entity.posY, entity.posZ, 3.0F, true);
                this.controller.motionX = tempMotion.xPos;
                this.controller.motionY = tempMotion.yPos;
                this.controller.motionZ = tempMotion.zPos;
                explosionCreated = true;
            }

            if (entity instanceof EntityLivingBase)
            {
                entity.fallDistance = 0;
                entity.attackEntityFrom(DamageSource.setExplosionSource(this), 4F);
            }
            else {
                if(entity instanceof EntityExplosion)
                {
                    if(((EntityExplosion)entity).blast instanceof BlastAntimatter || ((EntityExplosion)entity).blast instanceof BlastRedmatter)
                    {
                        worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "explosion", 7.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

                        if(worldObj.rand.nextFloat() > 0.85 && !worldObj.isRemote)
                        {
                            entity.setDead();
                            return explosionCreated;
                        }
                    }
                }
                else if(entity instanceof EntityExplosive)
                {
                    ((EntityExplosive)entity).explode();
                }
                else {
                    entity.setDead();
                }
            }
        }

        return explosionCreated;
    }

    /** The interval in ticks before the next procedural call of this explosive
     * 
     * @return - Return -1 if this explosive does not need proceudral calls */
    @Override
    public int proceduralInterval()
    {
        return 1;
    }

    @Override
    public long getEnergy()
    {
        return -3000;
    }

    @Override
    public boolean isMovable()
    {
        return this.callCount > 1;
    }
}
