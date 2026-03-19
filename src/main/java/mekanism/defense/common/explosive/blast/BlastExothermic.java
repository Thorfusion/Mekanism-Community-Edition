package defense.common.explosive.blast;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import defense.common.DefenseUtils;
import defense.common.Reference;
import defense.common.Settings;

public class BlastExothermic extends BlastBeam
{
    public BlastExothermic(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
        red = 0.7f;
        green = 0.3f;
        blue = 0;
    }

    @Override
    public void doExplode()
    {
        super.doExplode();
        worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "beamcharging", 4.0F, 0.8F);
    }

    @Override
    public void doPostExplode()
    {
        super.doPostExplode();

        if (!worldObj.isRemote)
        {
            worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "powerdown", 4.0F, 0.8F);

            if (canFocusBeam(worldObj, position) && thread.isComplete)
            {
                for (Pos3D targetPosition : thread.results)
                {
                    double distanceFromCenter = position.distance(targetPosition);

                    if(distanceFromCenter > getRadius())
                    {
                        continue;
                    }

                    /*
                     * Reduce the chance of setting blocks on fire based on distance from center.
                     */
                    double chance = getRadius() - (Math.random() * distanceFromCenter);

                    if(chance > distanceFromCenter * 0.55)
                    {
                        /*
                         * Check to see if the block is an air block and there is a block below it
                         * to support the fire.
                         */
                        Block block = worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                        if(DefenseUtils.canBreak(worldObj, block, targetPosition.xPos, targetPosition.yPos, targetPosition.zPos))
                        {
	                        if(block == Blocks.water || block == Blocks.flowing_water || block == Blocks.ice)
	                        {
	                        	worldObj.setBlockToAir((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);
	                        }
	
	                        if((worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos) || block == Blocks.snow) && worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos - 1, (int)targetPosition.zPos).getMaterial().isSolid())
	                        {
	                            if(worldObj.rand.nextFloat() > 0.999)
	                            {
	                                worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, Blocks.flowing_lava, 0, 2);
	                            }
	                            else {
	                                worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, Blocks.fire, 0, 2);
	
	                                block = worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos - 1, (int)targetPosition.zPos);
	
	                                if (Settings.CREATE_NETHERRACK && (block == Blocks.stone || block == Blocks.grass || block == Blocks.dirt) && worldObj.rand.nextFloat() > 0.75)
	                                {
	                                    worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos - 1, (int)targetPosition.zPos, Blocks.netherrack, 0, 2);
	                                }
	                            }
	                        }
	                        else if(block == Blocks.ice)
	                        {
	                        	if(DefenseUtils.canBreak(worldObj, block, targetPosition.xPos, targetPosition.yPos, targetPosition.zPos))
	                        	{
	                        		worldObj.setBlockToAir((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);
	                        	}
	                        }
                        }
                    }
                }

                worldObj.playSoundEffect(position.xPos + 0.5D, position.yPos + 0.5D, position.zPos + 0.5D, Reference.PREFIX + "explosionfire", 6.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);
            }

            worldObj.setWorldTime(18000);
        }
    }

    @Override
    public boolean canFocusBeam(World worldObj, Pos3D position)
    {
        long worldTime = worldObj.getWorldTime();

        while(worldTime > 23999)
        {
            worldTime -= 23999;
        }

        return worldTime < 12000 && !worldObj.isRaining() && super.canFocusBeam(worldObj, position);
    }

}
