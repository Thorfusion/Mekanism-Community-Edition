package defense.common.explosive.blast;

import mekanism.api.Coord4D;
import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import defense.common.DefenseUtils;
import defense.common.Reference;
import defense.common.entity.EntityExplosion;

public class BlastAntimatter extends Blast
{
    private boolean destroyBedrock;

    public BlastAntimatter(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastAntimatter(World world, Entity entity, double x, double y, double z, float size, boolean destroyBedrock)
    {
        this(world, entity, x, y, z, size);
        destroyBedrock = destroyBedrock;
    }

    /** Called before an explosion happens */
    @Override
    public void doPreExplode()
    {
        super.doPreExplode();
        worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "antimatter", 7F, (float) (worldObj.rand.nextFloat() * 0.1 + 0.9F));
        doDamageEntities(getRadius(), Integer.MAX_VALUE);
    }

    @Override
    public void doExplode()
    {
        if(!worldObj.isRemote)
        {
            for(int x = (int)-getRadius(); x < getRadius(); x++)
            {
                for(int y = (int)-getRadius(); y < getRadius(); y++)
                {
                    for(int z = (int)-getRadius(); z < getRadius(); z++)
                    {
                        Pos3D targetPosition = position.clone().translate(new Pos3D(x, y, z));

                        double dist = position.distance(targetPosition);

                        if(dist < getRadius())
                        {
                        	Coord4D coord = targetPosition.getCoord(worldObj.provider.dimensionId);
                        	
                            Block block = coord.getBlock(worldObj);

                            if(!block.isAir(worldObj, coord.xCoord, coord.yCoord, coord.zCoord))
                            {
                                if(!destroyBedrock && block.getBlockHardness(worldObj, coord.xCoord, coord.yCoord, coord.zCoord) < 0)
                                {
                                    continue;
                                }

                                if(dist < getRadius() - 1 || worldObj.rand.nextFloat() > 0.7)
                                {
                                	if(DefenseUtils.canBreak(worldObj, block, coord.xCoord, coord.yCoord, coord.zCoord))
                                	{
                                		worldObj.setBlockToAir(coord.xCoord, coord.yCoord, coord.zCoord);
                                	}
                                }
                            }
                        }
                    }
                }
            }
        }

        // TODO: Render antimatter shockwave
        /*
         * else if(ZhuYao.proxy.isGaoQing()) { for(int x = -getRadius(); x <
         * getRadius(); x++) { for(int y = -getRadius(); y < getRadius(); y++) { for
         * (int z = -getRadius(); z < getRadius(); z++) { Vector3 targetPosition =
         * Vector3.add(position, new Vector3(x, y, z)); double distance =
         * position.distanceTo(targetPosition);
         * if(targetPosition.getBlockID(worldObj) == 0) { if(distance < getRadius() &&
         * distance > getRadius() - 1 && worldObj.rand.nextFloat() > 0.5) {
         * ParticleSpawner.spawnParticle("antimatter", worldObj, targetPosition); } } } } } }
         */
    }

    @Override
    public void doPostExplode()
    {
        doDamageEntities(getRadius(), Integer.MAX_VALUE);
    }

    @Override
    protected boolean onDamageEntity(Entity entity)
    {
        if(entity instanceof EntityExplosion)
        {
            if(((EntityExplosion)entity).blast instanceof BlastRedmatter)
            {
                entity.setDead();
                return true;
            }
        }

        return false;
    }

    @Override
    public long getEnergy()
    {
        return 30000000;
    }
}
