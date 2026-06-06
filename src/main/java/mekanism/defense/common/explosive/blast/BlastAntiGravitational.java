package defense.common.explosive.blast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import defense.common.DefenseUtils;
import defense.common.Reference;
import defense.common.entity.EntityFlyingBlock;
import defense.common.explosive.thread.ThreadSmallExplosion;

public class BlastAntiGravitational extends Blast
{
    protected ThreadSmallExplosion thread;
    protected Set<EntityFlyingBlock> flyingBlocks = new HashSet<EntityFlyingBlock>();

    public BlastAntiGravitational(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    @Override
    public void doPreExplode()
    {
        if(!worldObj.isRemote)
        {
            thread = new ThreadSmallExplosion(worldObj, this.position, (int) this.getRadius(), this.exploder);
            thread.start();
        }

        worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "antigravity", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
    }

    @Override
    public void doExplode()
    {
        int r = callCount;

        if(!worldObj.isRemote && thread.isComplete)
        {
            int blocksToTake = 20;

            for(Pos3D targetPosition : thread.results)
            {
                double distance = targetPosition.distance(position);

                if(distance > r || distance < r - 2 || blocksToTake <= 0)
                {
                    continue;
                }

                Block block = worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                if(worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos) || block.getBlockHardness(worldObj, (int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos) < 0)
                {
                    continue;
                }

                int metadata = worldObj.getBlockMetadata((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                if(distance < r - 1 || worldObj.rand.nextInt(3) > 0)
                {
                	if(!DefenseUtils.canBreak(worldObj, block, targetPosition.xPos, targetPosition.yPos, targetPosition.zPos))
                	{
                		continue;
                	}
                	
                    worldObj.setBlockToAir((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                    targetPosition.translate(0.5D, 0.5D, 0.5D);

                    if(worldObj.rand.nextFloat() < 0.3 * (this.getRadius() - r))
                    {
                        EntityFlyingBlock entity = new EntityFlyingBlock(worldObj, targetPosition, block, metadata, 0);
                        worldObj.spawnEntityInWorld(entity);
                        flyingBlocks.add(entity);
                        entity.yawChange = 50 * worldObj.rand.nextFloat();
                        entity.pitchChange = 100 * worldObj.rand.nextFloat();
                        entity.motionY += Math.max(0.15 * worldObj.rand.nextFloat(), 0.1);
                    }

                    blocksToTake--;
                }
            }
        }

        int radius = (int)getRadius();
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.xPos - radius, position.yPos - radius, position.zPos - radius, position.xPos + radius, 100, position.zPos + radius);
        List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);

        for(Entity entity : allEntities)
        {
            if(!(entity instanceof EntityFlyingBlock) && entity.posY < 100 + position.yPos)
            {
                if(entity.motionY < 0.4)
                {
                    entity.motionY += 0.15;
                }
            }
        }

        if(callCount > 20 * 120)
        {
            controller.endExplosion();
        }
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
    public float getRadius()
    {
        return 15;
    }

    @Override
    public long getEnergy()
    {
        return 10000;
    }
}
