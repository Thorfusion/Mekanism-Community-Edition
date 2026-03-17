package defense.common.explosive.blast;

import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import defense.common.DefenseTechBlocks;
import defense.common.DefenseUtils;
import defense.common.explosive.thread.ThreadLargeExplosion;

/** Creates radiation spawning
 * 
 * @author Calclavia */
public class BlastRot extends Blast
{
    private ThreadLargeExplosion thread;
    private float nengLiang;

    public BlastRot(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastRot(World world, Entity entity, double x, double y, double z, float size, float nengLiang)
    {
        this(world, entity, x, y, z, size);
        this.nengLiang = nengLiang;
    }

    @Override
    public void doPreExplode()
    {
        if (!worldObj.isRemote)
        {
            this.thread = new ThreadLargeExplosion(worldObj, this.position, (int) this.getRadius(), this.nengLiang, this.exploder);
            this.thread.start();
        }
    }

    @Override
    public void doExplode()
    {
        if (!worldObj.isRemote)
        {
            if (this.thread.isComplete)
            {
                for (Pos3D targetPosition : this.thread.results)
                {
                    /** Decay the blocks. */
                    Block block = new Coord4D((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos).getBlock(worldObj);

                    if(!DefenseUtils.canBreak(worldObj, block, targetPosition.xPos, targetPosition.yPos, targetPosition.zPos))
                    {
                    	continue;
                    }
                    
                    if (!worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos))
                    {
                        if (block == Blocks.grass || block == Blocks.sand)
                        {
                            if (worldObj.rand.nextFloat() > 0.96)
                            {
                            	worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, DefenseTechBlocks.blockRadioactive);
                            }
                        }

                        if (block == Blocks.stone)
                        {
                            if (worldObj.rand.nextFloat() > 0.99)
                            {
                            	worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, DefenseTechBlocks.blockRadioactive);
                            }
                        }

                        else if (block == Blocks.leaves)
                        {
                        	worldObj.setBlockToAir((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);
                        }
                        else if (block == Blocks.grass)
                        {
                            if (Math.random() * 100 > 50)
                            {
                            	worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, Blocks.cobblestone);
                            }
                            else
                            {
                            	worldObj.setBlockToAir((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);
                            }
                        }
                        else if (block == Blocks.farmland)
                        {
                        	worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, DefenseTechBlocks.blockRadioactive);
                        }
                        else if (block == Blocks.water || block == Blocks.flowing_water)
                        {
                            if (FluidRegistry.getFluid("toxicwaste") != null)
                            {
                            	worldObj.setBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, FluidRegistry.getFluid("toxicwaste").getBlock());
                            }
                        }
                    }
                }

                this.controller.endExplosion();
            }
        }
    }

    @Override
    public int proceduralInterval()
    {
        return 1;
    }

    @Override
    public long getEnergy()
    {
        return 100;
    }
}
