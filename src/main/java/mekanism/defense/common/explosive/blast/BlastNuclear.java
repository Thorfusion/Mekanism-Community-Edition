package defense.common.explosive.blast;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import defense.common.DefenseTech;
import defense.common.DefenseUtils;
import defense.common.Reference;
import defense.common.explosive.thread.ThreadLargeExplosion;

public class BlastNuclear extends Blast
{
    private ThreadLargeExplosion thread;
    private float explosionEnergy;
    private boolean spawnMoreParticles = false;
    private boolean isRadioactive = false;

    public BlastNuclear(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastNuclear(World world, Entity entity, double x, double y, double z, float size, float energy)
    {
        this(world, entity, x, y, z, size);
        explosionEnergy = energy;
    }

    public BlastNuclear setNuclear()
    {
        spawnMoreParticles = true;
        isRadioactive = true;
        
        return this;
    }

    @Override
    public void doPreExplode()
    {
        if(!worldObj.isRemote)
        {
            thread = new ThreadLargeExplosion(worldObj, position, (int)getRadius(), explosionEnergy, exploder);

            thread.start();

        }
        else if(spawnMoreParticles && DefenseTech.proxy.isFancyGraphicsEnabled())
        {
            // Spawn nuclear cloud.
            for(int y = 0; y < 26; y++)
            {
                int r = 4;

                if(y < 8)
                {
                    r = Math.max(Math.min((8 - y) * 2, 10), 4);
                }
                else if(y > 15)
                {
                    r = Math.max(Math.min((y - 15) * 2, 15), 5);
                }

                for(int x = -r; x < r; x++)
                {
                    for (int z = -r; z < r; z++)
                    {
                        double distance = MathHelper.sqrt_double(x * x + z * z);

                        if(r > distance && r - 3 < distance)
                        {
                            Pos3D spawnPosition = position.clone().translate(new Pos3D(x * 2, (y - 2) * 2, z * 2));
                            float xDiff = (float) (spawnPosition.xPos - position.xPos);
                            float zDiff = (float) (spawnPosition.zPos - position.zPos);
                            DefenseTech.proxy.spawnParticle("smoke", worldObj, spawnPosition, xDiff * 0.3 * worldObj.rand.nextFloat(), -worldObj.rand.nextFloat(), zDiff * 0.3 * worldObj.rand.nextFloat(), (float) (distance / getRadius()) * worldObj.rand.nextFloat(), 0, 0, 8F, 1.2F);
                        }
                    }
                }
            }
        }

        doDamageEntities(getRadius(), explosionEnergy * 1000);

        worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "explosion", 7.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
    }

    @Override
    public void doExplode()
    {
        int r = callCount;

        if(worldObj.isRemote)
        {
            if(DefenseTech.proxy.isFancyGraphicsEnabled())
            {
                for(int x = -r; x < r; x++)
                {
                    for(int z = -r; z < r; z++)
                    {
                        double distance = MathHelper.sqrt_double(x * x + z * z);

                        if(distance < r && distance > r - 1)
                        {
                            Pos3D targetPosition = position.clone().translate(new Pos3D(x, 0, z));

                            if(worldObj.rand.nextFloat() < Math.max(0.001 * r, 0.05))
                            {
                                DefenseTech.proxy.spawnParticle("smoke", worldObj, targetPosition, 5F, 1F);
                            }
                        }
                    }
                }
            }

        }
        else {
            if(thread != null)
            {
                if(thread.isComplete)
                {
                    controller.endExplosion();
                }
            }
            else {
                controller.endExplosion();
                DefenseTech.LOGGER.severe("Something went wrong with multi-threading while detonating the nuclear explosive.");
            }
        }
    }

    @Override
    public void doPostExplode()
    {
        try {
            if(!worldObj.isRemote && thread.isComplete)
            {
                for(Pos3D p : thread.results)
                {
                    Block block = worldObj.getBlock((int)p.xPos, (int)p.yPos, (int)p.zPos);
                    
                    if(!worldObj.isAirBlock((int)p.xPos, (int)p.yPos, (int)p.zPos))
                    {
                        if(DefenseUtils.canBreak(worldObj, block, p.xPos, p.yPos, p.zPos))
                        {
                        	block.onBlockExploded(worldObj, (int)p.xPos, (int)p.yPos, (int)p.zPos, this);
                        }
                    }
                }
            }
        } catch(Exception e) {
            DefenseTech.LOGGER.severe("Nuclear-type detonation failed!");
            e.printStackTrace();
        }

        doDamageEntities(getRadius(), explosionEnergy * 1000);

        if(isRadioactive)
        {
            new BlastRot(worldObj, exploder, position.xPos, position.yPos, position.zPos, getRadius(), explosionEnergy).explode();
            new BlastMutation(worldObj, exploder, position.xPos, position.yPos, position.zPos, getRadius()).explode();

            if(worldObj.rand.nextInt(3) == 0)
            {
            	worldObj.getWorldInfo().setRaining(true);
            }
        }

        worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "explosion", 10.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
    }

    /** The interval in ticks before the next procedural call of this explosive
     * 
     * @param return - Return -1 if this explosive does not need procedural calls */
    @Override
    public int proceduralInterval()
    {
        return 1;
    }

    @Override
    public long getEnergy()
    {
        return (long)(41840000 * explosionEnergy);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        spawnMoreParticles = nbt.getBoolean("spawnMoreParticles");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("spawnMoreParticles", spawnMoreParticles);
    }
}
