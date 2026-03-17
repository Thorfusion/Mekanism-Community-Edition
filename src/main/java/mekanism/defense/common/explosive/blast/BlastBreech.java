package defense.common.explosive.blast;

import defense.common.entity.EntityExplosive;
import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlastBreech extends BlastRepulsive
{
    private int depth;

    public BlastBreech(World world, Entity entity, double x, double y, double z, float size, int depth)
    {
        this(world, entity, x, y, z, size);
        this.depth = depth;
    }

    public BlastBreech(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
        this.nengLiang = 13;
    }

    @Override
    public void doExplode()
    {
        if (!this.worldObj.isRemote)
        {
            final Pos3D difference = new Pos3D();

            if (this.exploder instanceof EntityExplosive)
            {
                difference.translate(((EntityExplosive)this.exploder).getDirection(), 1);
            }
            else
            {
                difference.translate(ForgeDirection.DOWN, 1);
            }

            this.worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, "random.explode", 5.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

            for (int i = 0; i < this.depth; i++)
            {
            	Block block = position.getCoord(worldObj.provider.dimensionId).getBlock(worldObj);
            	
                if (!worldObj.isAirBlock((int)position.xPos, (int)position.yPos, (int)position.zPos))
                {
                    if (block.getExplosionResistance(this.exploder, worldObj, (int)position.xPos, (int)position.yPos, (int)position.zPos, position.xPos, position.yPos, position.zPos) > Blocks.obsidian.getExplosionResistance(this.exploder))
                    {
                        break;
                    }
                }

                super.doExplode();
                this.position.translate(difference);
            }
        }
    }

    @Override
    public long getEnergy()
    {
        return (super.getEnergy() * this.depth) / 2;
    }
}
