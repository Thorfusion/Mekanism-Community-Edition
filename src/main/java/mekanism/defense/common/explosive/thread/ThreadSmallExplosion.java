package defense.common.explosive.thread;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/** Used for small explosions.
 * 
 * @author Calclavia */
public class ThreadSmallExplosion extends ThreadExplosion
{
    public ThreadSmallExplosion(World world, Pos3D position, int banJing, Entity source)
    {
        super(world, position, banJing, 0, source);
    }

    @Override
    public void run()
    {
        if (!worldObj.isRemote)
        {
            for (int x = 0; x < this.explosionRadius; ++x)
            {
                for (int y = 0; y < this.explosionRadius; ++y)
                {
                    for (int z = 0; z < this.explosionRadius; ++z)
                    {
                        if (x == 0 || x == this.explosionRadius - 1 || y == 0 || y == this.explosionRadius - 1 || z == 0 || z == this.explosionRadius - 1)
                        {
                            double xStep = x / (this.explosionRadius - 1.0F) * 2.0F - 1.0F;
                            double yStep = y / (this.explosionRadius - 1.0F) * 2.0F - 1.0F;
                            double zStep = z / (this.explosionRadius - 1.0F) * 2.0F - 1.0F;
                            double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
                            xStep /= diagonalDistance;
                            yStep /= diagonalDistance;
                            zStep /= diagonalDistance;
                            float power = this.explosionRadius * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                            double var15 = position.xPos;
                            double var17 = position.yPos;
                            double var19 = position.zPos;

                            for (float var21 = 0.3F; power > 0.0F; power -= var21 * 0.75F)
                            {
                                Pos3D targetPosition = new Pos3D(var15, var17, var19);
                                double distanceFromCenter = position.distance(targetPosition);
                                Block block = this.worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                                if (!worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos))
                                {
                                    float resistance = 0;

                                    if (block == Blocks.bedrock)
                                    {
                                        break;
                                    }
                                    else
                                    {
                                        resistance = block.getExplosionResistance(this.explosionSource, this.worldObj, (int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, position.xPos, position.yPos, position.zPos);
                                    }
                                    // TODO rather than remove power divert a percentage to the
                                    // sides, and then calculate how much is absorbed by the block
                                    power -= resistance;
                                }

                                if (power > 0.0F)
                                {
                                    this.results.add(targetPosition.clone());
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

        super.run();
    }
}
