package defense.common.explosive.blast;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import defense.common.DefenseUtils;
import defense.common.Reference;
import defense.common.Settings;
import defense.common.entity.EntityExplosion;

public class BlastAntimatter extends Blast
{
    private boolean destroyBedrock;
    private boolean scanInitialized;
    private boolean scanComplete;
    private int scanShell;
    private int scanX;
    private int scanY;
    private int scanZ;
    private int scanMaxY;
    private int scanMaxZ;
    private int scanMinAbsZ;

    public BlastAntimatter(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastAntimatter(World world, Entity entity, double x, double y, double z, float size, boolean destroyBedrock)
    {
        this(world, entity, x, y, z, size);
        this.destroyBedrock = destroyBedrock;
    }

    /** Called before an explosion happens */
    @Override
    public void doPreExplode()
    {
        super.doPreExplode();

        if(!worldObj.isRemote)
        {
            worldObj.playSoundEffect(position.xPos, position.yPos, position.zPos, Reference.PREFIX + "antimatter", 7F, (float)(worldObj.rand.nextFloat() * 0.1 + 0.9F));
            doDamageEntities(getRadius(), Integer.MAX_VALUE);
        }
    }

    @Override
    public void doExplode()
    {
        if(worldObj.isRemote)
        {
            return;
        }

        if(!scanInitialized)
        {
            initializeScan();
        }

        int radius = getScanRadius();
        int checksRemaining = Math.max(1, Settings.ANTIMATTER_BLOCKS_PER_TICK);

        while(!scanComplete && checksRemaining-- > 0)
        {
            removeCurrentBlock(radius);
            advanceScan(radius);
        }

        if(scanComplete && controller != null)
        {
            controller.endExplosion();
        }
    }

    @Override
    public void doPostExplode()
    {
        if(!worldObj.isRemote)
        {
            doDamageEntities(getRadius(), Integer.MAX_VALUE);
        }
    }

    private int getScanRadius()
    {
        return Math.max(0, (int)getRadius());
    }

    private void initializeScan()
    {
        scanInitialized = true;

        int radius = getScanRadius();
        if(radius <= 0)
        {
            scanComplete = true;
            return;
        }

        scanShell = 1;
        resetXAndYAndZ();
    }

    private void resetXAndYAndZ()
    {
        // The planes at +/- shell radius cannot be inside a strict-radius sphere.
        scanX = -scanShell + 1;
        resetYAndZ();
    }

    private void resetYAndZ()
    {
        scanMaxY = getMaxOffset(scanShell, scanX, 0);
        scanY = -scanMaxY;
        resetZ();
    }

    private void resetZ()
    {
        // Shell N contains points where (N - 1)^2 <= distanceSquared < N^2.
        scanMaxZ = getMaxOffset(scanShell, scanX, scanY);
        scanMinAbsZ = getMinAbsOffset(scanShell - 1, scanX, scanY);
        scanZ = -scanMaxZ;
    }

    private int getMaxOffset(int radius, int x, int y)
    {
        long remaining = (long)radius * radius - 1L - (long)x * x - (long)y * y;
        return remaining <= 0 ? 0 : (int)Math.sqrt(remaining);
    }

    private int getMinAbsOffset(int radius, int x, int y)
    {
        long remaining = (long)radius * radius - (long)x * x - (long)y * y;
        if(remaining <= 0)
        {
            return 0;
        }

        int result = (int)Math.sqrt(remaining);
        return (long)result * result == remaining ? result : result + 1;
    }

    private void advanceScan(int maxRadius)
    {
        scanZ++;
        if(scanMinAbsZ > 0 && scanZ > -scanMinAbsZ && scanZ < scanMinAbsZ)
        {
            scanZ = scanMinAbsZ;
        }

        if(scanZ <= scanMaxZ)
        {
            return;
        }

        if(++scanY <= scanMaxY)
        {
            resetZ();
            return;
        }

        if(++scanX < scanShell)
        {
            resetYAndZ();
            return;
        }

        if(++scanShell <= maxRadius)
        {
            resetXAndYAndZ();
        }
        else
        {
            scanComplete = true;
        }
    }

    private void removeCurrentBlock(int radius)
    {
        // Keep the original Pos3D-to-block truncation behavior, including near coordinate zero.
        int x = (int)(position.xPos + scanX);
        int y = (int)(position.yPos + scanY);
        int z = (int)(position.zPos + scanZ);

        if(y < 0 || y >= worldObj.getHeight())
        {
            return;
        }

        Block block = worldObj.getBlock(x, y, z);
        if(block.isAir(worldObj, x, y, z))
        {
            return;
        }

        if(!destroyBedrock && block.getBlockHardness(worldObj, x, y, z) < 0)
        {
            return;
        }

        long distanceSquared = (long)scanX * scanX + (long)scanY * scanY + (long)scanZ * scanZ;
        int innerRadius = Math.max(0, radius - 1);
        boolean isInnerSphere = distanceSquared < (long)innerRadius * innerRadius;

        if(!isInnerSphere && worldObj.rand.nextFloat() <= 0.7F)
        {
            return;
        }

        if(DefenseUtils.canBreak(worldObj, block, x, y, z))
        {
            // Interior removals only need client synchronization. Preserve normal neighbor updates
            // around the crater boundary so surviving blocks, fluids, and falling blocks react.
            int neighborUpdateRadius = Math.max(0, radius - 2);
            int updateFlags = distanceSquared >= (long)neighborUpdateRadius * neighborUpdateRadius ? 3 : 2;
            worldObj.setBlock(x, y, z, Blocks.air, 0, updateFlags);
        }
    }

    @Override
    protected boolean onDamageEntity(Entity entity)
    {
        if(entity == controller)
        {
            return true;
        }

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

    @Override
    public int proceduralInterval()
    {
        return 1;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        if(nbt.hasKey("destroyBedrock"))
        {
            destroyBedrock = nbt.getBoolean("destroyBedrock");
        }

        scanInitialized = nbt.getBoolean("antimatterScanInitialized");
        scanComplete = nbt.getBoolean("antimatterScanComplete");
        scanShell = nbt.getInteger("antimatterScanShell");
        scanX = nbt.getInteger("antimatterScanX");
        scanY = nbt.getInteger("antimatterScanY");
        scanZ = nbt.getInteger("antimatterScanZ");

        if(scanInitialized && !scanComplete)
        {
            if(!nbt.hasKey("antimatterScanShell") || scanShell < 1 || scanShell > getScanRadius())
            {
                // Safely restart procedural explosions saved by the older plane-scan format.
                scanInitialized = false;
            }
            else
            {
                scanMaxY = getMaxOffset(scanShell, scanX, 0);
                scanMaxZ = getMaxOffset(scanShell, scanX, scanY);
                scanMinAbsZ = getMinAbsOffset(scanShell - 1, scanX, scanY);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setBoolean("destroyBedrock", destroyBedrock);
        nbt.setBoolean("antimatterScanInitialized", scanInitialized);
        nbt.setBoolean("antimatterScanComplete", scanComplete);
        nbt.setInteger("antimatterScanShell", scanShell);
        nbt.setInteger("antimatterScanX", scanX);
        nbt.setInteger("antimatterScanY", scanY);
        nbt.setInteger("antimatterScanZ", scanZ);
    }
}
