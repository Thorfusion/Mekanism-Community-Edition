package defense.common.explosive.blast;

import java.util.Iterator;
import java.util.List;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import defense.common.DefenseTechBlocks;
import defense.common.DefenseUtils;
import defense.common.Reference;
import defense.common.block.BlockExplosive;
import defense.common.entity.EntityFlyingBlock;
import defense.common.entity.EntityMissile;
import defense.common.explosive.thread.ThreadLargeExplosion;
import defense.common.explosive.thread.ThreadLargeExplosion.IThreadCallBack;
import defense.common.tile.TileExplosive;

public class BlastSonic extends Blast
{
    private float nengLiang;
    private ThreadLargeExplosion thread;
    private boolean hasShockWave = false;;

    public BlastSonic(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastSonic(World world, Entity entity, double x, double y, double z, float size, float nengLiang)
    {
        this(world, entity, x, y, z, size);
        this.nengLiang = nengLiang;
    }

    public Blast setShockWave()
    {
        this.hasShockWave = true;
        return this;
    }

    @Override
    public void doPreExplode()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.hasShockWave)
            {
                for (int x = (int) (-this.getRadius() * 2); x < this.getRadius() * 2; ++x)
                {
                    for (int y = (int) (-this.getRadius() * 2); y < this.getRadius() * 2; ++y)
                    {
                        for (int z = (int) (-this.getRadius() * 2); z < this.getRadius() * 2; ++z)
                        {
                            Pos3D targetPosition = position.clone().translate(new Pos3D(x, y, z));
                            Block block = worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                            if(!DefenseUtils.canBreak(worldObj, block, targetPosition.xPos, targetPosition.yPos, targetPosition.zPos))
                            {
                            	continue;
                            }
                            
                            if (!worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos))
                            {
                                Material material = block.getMaterial();

                                if (block != Blocks.bedrock && !(block instanceof BlockLiquid) && (block.getExplosionResistance(this.exploder, worldObj, (int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, position.xPos, position.yPos, position.zPos) > this.nengLiang || material == Material.glass))
                                {
                                	worldObj.setBlockToAir((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);
                                }
                            }
                        }
                    }
                }
            }

            this.thread = new ThreadLargeExplosion(worldObj, this.position, (int) this.getRadius(), this.nengLiang, this.exploder, new IThreadCallBack()
            {
                @Override
                public float getResistance(World world, Pos3D explosionPosition, Pos3D targetPosition, Entity source, Block block)
                {
                    float resistance = 0;

                    if (block instanceof BlockLiquid || block instanceof IFluidBlock)
                    {
                        resistance = 1f;
                    }
                    else
                    {
                        resistance = block.getExplosionResistance(source, world, (int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, explosionPosition.xPos, explosionPosition.yPos, explosionPosition.zPos);
                    }

                    return resistance;
                }

            });
            this.thread.start();
        }

        if (this.hasShockWave)
        {
            this.worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "hypersonic", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        }
        else
        {
            this.worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "sonicwave", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
        }
    }

    @Override
    public void doExplode()
    {
        int r = callCount;

        if(!worldObj.isRemote)
        {
            if(thread != null && thread.isComplete)
            {
                Iterator<Pos3D> it = thread.results.iterator();

                while(it.hasNext())
                {
                    Pos3D targetPosition = it.next();
                    double distance = targetPosition.clone().distance(position);

                    if (distance > r || distance < r - 3)
                        continue;

                    Block block = this.worldObj.getBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                    if(!DefenseUtils.canBreak(worldObj, block, targetPosition.xPos, targetPosition.yPos, targetPosition.zPos))
                    {
                    	continue;
                    }
                    
                    if(worldObj.isAirBlock((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos) || block == Blocks.bedrock || block == Blocks.obsidian)
                    {
                        continue;
                    }

                    int metadata = this.worldObj.getBlockMetadata((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);

                    if (distance < r - 1 || this.worldObj.rand.nextInt(3) > 0)
                    {
                        if (block == DefenseTechBlocks.blockExplosive)
                        {
                            BlockExplosive.detonate(this.worldObj, (int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos, ((TileExplosive) this.worldObj.getTileEntity((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos)).explosiveID, 1);
                        }
                        else {
                            this.worldObj.setBlockToAir((int)targetPosition.xPos, (int)targetPosition.yPos, (int)targetPosition.zPos);
                        }

                        targetPosition.translate(0.5D, 0.5D, 0.5D);

                        if (this.worldObj.rand.nextFloat() < 0.3 * (this.getRadius() - r))
                        {
                            EntityFlyingBlock entity = new EntityFlyingBlock(this.worldObj, targetPosition, block, metadata);
                            this.worldObj.spawnEntityInWorld(entity);
                            entity.yawChange = 50 * this.worldObj.rand.nextFloat();
                            entity.pitchChange = 100 * this.worldObj.rand.nextFloat();
                        }

                        it.remove();
                    }
                }
            }
        }

        int radius = 2 * this.callCount;
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.xPos - radius, position.yPos - radius, position.zPos - radius, position.xPos + radius, position.yPos + radius, position.zPos + radius);
        List<Entity> allEntities = this.worldObj.getEntitiesWithinAABB(Entity.class, bounds);

        synchronized (allEntities)
        {
            for (Iterator it = allEntities.iterator(); it.hasNext();)
            {
                Entity entity = (Entity) it.next();

                if (entity instanceof EntityMissile)
                {
                    ((EntityMissile) entity).setExplode();
                    break;
                }
                else {
                    double xDifference = entity.posX - position.xPos;
                    double zDifference = entity.posZ - position.zPos;

                    r = (int) this.getRadius();
                    if (xDifference < 0)
                        r = (int) -this.getRadius();

                    entity.motionX += (r - xDifference) * 0.02 * this.worldObj.rand.nextFloat();
                    entity.motionY += 3 * this.worldObj.rand.nextFloat();

                    r = (int) this.getRadius();
                    if (zDifference < 0)
                        r = (int) -this.getRadius();

                    entity.motionZ += (r - zDifference) * 0.02 * this.worldObj.rand.nextFloat();
                }
            }
        }

        if (this.callCount > this.getRadius())
        {
            this.controller.endExplosion();
        }

    }

    /** The interval in ticks before the next procedural call of this explosive
     * 
     * @return - Return -1 if this explosive does not need proceudral calls */
    @Override
    public int proceduralInterval()
    {
        return 4;
    }

    @Override
    public long getEnergy()
    {
        return 3000;
    }
}
