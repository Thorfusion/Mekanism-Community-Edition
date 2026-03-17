package defense.common.explosive.blast;

import java.util.Iterator;
import java.util.List;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import defense.common.DefenseUtils;
import defense.common.Reference;
import defense.common.potion.CustomPotionEffect;
import defense.common.potion.PoisonFrostBite;

public class BlastEndothermic extends BlastBeam
{
    public BlastEndothermic(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
        this.red = 0f;
        this.green = 0.3f;
        this.blue = 0.7f;
    }

    @Override
    public void doExplode()
    {
        super.doExplode();
        this.worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "redmatter", 4.0F, 0.8F);
    }

    @Override
    public void doPostExplode()
    {
        super.doPostExplode();

        if (!this.worldObj.isRemote)
        {
            if (this.canFocusBeam(this.worldObj, position) && this.thread.isComplete)
            {
                /*
                 * Freeze all nearby entities.
                 */
                List<EntityLiving> livingEntities = worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(position.xPos - getRadius(), position.yPos - getRadius(), position.zPos - getRadius(), position.xPos + getRadius(), position.yPos + getRadius(), position.zPos + getRadius()));

                Iterator<EntityLiving> it = livingEntities.iterator();

                while (it.hasNext())
                {
                    EntityLiving entity = it.next();
                    entity.addPotionEffect(new CustomPotionEffect(PoisonFrostBite.INSTANCE.getId(), 60 * 20, 1, null));
                    entity.addPotionEffect(new PotionEffect(Potion.confusion.id, 10 * 20, 2));
                    entity.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 120 * 20, 2));
                    entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 120 * 20, 4));
                }

                for (Pos3D targetPosition : this.thread.results)
                {
                    double distanceFromCenter = position.distance(targetPosition);

                    if (distanceFromCenter > this.getRadius())
                        continue;

                    /*
                     * Reduce the chance of setting blocks on fire based on distance from center.
                     */
                    double chance = this.getRadius() - (Math.random() * distanceFromCenter);

                    if (chance > distanceFromCenter * 0.55)
                    {
                        /*
                         * Place down ice blocks.
                         */
                        Block block = this.worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                        if(DefenseUtils.canBreak(worldObj, block, targetPosition.xPos, targetPosition.yPos, targetPosition.zPos))
                        {
	                        if (block == Blocks.fire || block == Blocks.flowing_lava || block == Blocks.lava)
	                        {
	                            this.worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, Blocks.snow, 0, 2);
	                        }
	                        else if (worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos) && this.worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos - 1, (int)targetPosition.zPos) != Blocks.ice && !worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos - 1, (int)targetPosition.zPos))
	                        {
	                            this.worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, Blocks.ice, 0, 2);
	                        }
                        }
                    }
                }

                this.worldObj.playSoundEffect(position.xPos + 0.5D, position.yPos + 0.5D, position.zPos + 0.5D, Reference.PREFIX + "redmatter", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);
            }

            this.worldObj.setWorldTime(1200);
        }
    }

    @Override
    public boolean canFocusBeam(World worldObj, Pos3D position)
    {
        long worldTime = worldObj.getWorldTime();

        while (worldTime > 23999)
        {
            worldTime -= 23999;
        }

        return worldTime > 12000 && super.canFocusBeam(worldObj, position);
    }

}
