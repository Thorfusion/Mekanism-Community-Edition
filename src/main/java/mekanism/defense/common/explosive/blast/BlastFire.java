package defense.common.explosive.blast;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import defense.common.DefenseUtils;
import defense.common.Reference;

public class BlastFire extends Blast
{
    public BlastFire(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    @Override
    public void doExplode()
    {
        if (!this.worldObj.isRemote)
        {
            int radius = (int) this.getRadius();

            for (int x = 0; x < radius; ++x)
            {
                for (int y = 0; y < radius; ++y)
                {
                    for (int z = 0; z < radius; ++z)
                    {
                        if (x == 0 || x == radius - 1 || y == 0 || y == radius - 1 || z == 0 || z == radius - 1)
                        {
                            double xStep = x / (radius - 1.0F) * 2.0F - 1.0F;
                            double yStep = y / (radius - 1.0F) * 2.0F - 1.0F;
                            double zStep = z / (radius - 1.0F) * 2.0F - 1.0F;
                            double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
                            xStep /= diagonalDistance;
                            yStep /= diagonalDistance;
                            zStep /= diagonalDistance;
                            float var14 = radius * (0.7F + worldObj.rand.nextFloat() * 0.6F);
                            double var15 = position.xPos;
                            double var17 = position.yPos;
                            double var19 = position.zPos;

                            for (float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F)
                            {
                                Pos3D targetPosition = new Pos3D(var15, var17, var19);
                                double distanceFromCenter = position.distance(targetPosition);
                                Block block = worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                                if(!worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos))
                                {
                                    var14 -= (block.getExplosionResistance(this.exploder, worldObj, (int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, position.xPos, position.yPos, position.zPos) + 0.3F) * var21;
                                }

                                if(var14 > 0.0F)
                                {
                                    // Set fire by chance and distance
                                    double chance = radius - (Math.random() * distanceFromCenter);

                                    if(chance > distanceFromCenter * 0.55)
                                    {
                                        /*
                                         * Check to see if the block is an air block and there is a
                                         * block below it to support the fire.
                                         */
                                    	
                                        if((worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos) || block == Blocks.snow) && worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos - 1, (int)targetPosition.zPos).getMaterial().isSolid())
                                        {
                                            worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, Blocks.fire, 0, 2);
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

                                var15 += xStep * var21;
                                var17 += yStep * var21;
                                var19 += zStep * var21;
                            }
                        }
                    }
                }
            }
        }

        worldObj.playSoundEffect(position.xPos + 0.5D, position.yPos + 0.5D, position.zPos + 0.5D, Reference.PREFIX + "explosionfire", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);
    }

    @Override
    public long getEnergy()
    {
        return 3000;
    }
}
