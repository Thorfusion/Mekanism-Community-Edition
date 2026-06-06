package defense.common.explosive.blast;

import java.util.ArrayList;
import java.util.List;

import mekanism.api.Pos3D;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import defense.common.DefenseUtils;

public class BlastRepulsive extends Blast
{
    private int checkBanJing = 16;
    protected float nengLiang = 10F;

    private List<Pos3D> blownBlocks = new ArrayList<Pos3D>();

    /** 0- No push, 1 - Attract, 2 - Repel */
    private int pushType = 0;
    private boolean destroyItem = false;

    public BlastRepulsive(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    public BlastRepulsive setPushType(int type)
    {
        this.pushType = type;
        return this;
    }

    public BlastRepulsive setDestroyItems()
    {
        this.destroyItem = true;
        return this;
    }

    @Override
    public void doExplode()
    {
        if (!this.worldObj.isRemote)
        {
            for (int x = 0; x < this.checkBanJing; ++x)
            {
                for (int y = 0; y < this.checkBanJing; ++y)
                {
                    for (int z = 0; z < this.checkBanJing; ++z)
                    {
                        if (x == 0 || x == this.checkBanJing - 1 || y == 0 || y == this.checkBanJing - 1 || z == 0 || z == this.checkBanJing - 1)
                        {
                            double xStep = x / (this.checkBanJing - 1.0F) * 2.0F - 1.0F;
                            double yStep = y / (this.checkBanJing - 1.0F) * 2.0F - 1.0F;
                            double zStep = z / (this.checkBanJing - 1.0F) * 2.0F - 1.0F;
                            double diagonalDistance = Math.sqrt(xStep * xStep + yStep * yStep + zStep * zStep);
                            xStep /= diagonalDistance;
                            yStep /= diagonalDistance;
                            zStep /= diagonalDistance;
                            float var14 = this.getRadius() * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                            double var15 = this.position.xPos;
                            double var17 = this.position.yPos;
                            double var19 = this.position.zPos;

                            for (float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F)
                            {
                                int var22 = MathHelper.floor_double(var15);
                                int var23 = MathHelper.floor_double(var17);
                                int var24 = MathHelper.floor_double(var19);
                                Block block = this.worldObj.getBlock(var22, var23, var24);

                                if (!worldObj.isAirBlock(var22, var23, var24))
                                {
                                    var14 -= (block.getExplosionResistance(this.exploder, this.worldObj, var22, var23, var24, (int)this.position.xPos, (int)this.position.yPos, (int)this.position.zPos) + 0.3F) * var21;
                                }

                                if (var14 > 0.0F)
                                {
                                    blownBlocks.add(new Pos3D(var22, var23, var24));
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

        this.worldObj.playSoundEffect(this.position.xPos, this.position.yPos, this.position.zPos, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

        switch (this.pushType)
        {
            case 0:
                this.doDamageEntities(this.getRadius(), nengLiang, this.destroyItem);
                break;
            default:
                this.pushEntities(12, this.getRadius() * 4, this.pushType);
                break;
        }

        if(!this.worldObj.isRemote)
        {
            int var3;
            Pos3D blownPosition;
            int x;
            int y;
            int z;
            Block block;
            int metadata;

            for(var3 = blownBlocks.size() - 1; var3 >= 0; --var3)
            {
                blownPosition = blownBlocks.get(var3);
                x = (int)blownPosition.xPos;
                y = (int)blownPosition.yPos;
                z = (int)blownPosition.zPos;
                block = this.worldObj.getBlock(x, y, z);
                metadata = this.worldObj.getBlockMetadata(x, y, z);

                double var9 = (x + this.worldObj.rand.nextFloat());
                double var11 = (y + this.worldObj.rand.nextFloat());
                double var13 = (z + this.worldObj.rand.nextFloat());
                double var151 = var9 - this.position.yPos;
                double var171 = var11 - this.position.yPos;
                double var191 = var13 - this.position.zPos;
                double var211 = MathHelper.sqrt_double(var151 * var151 + var171 * var171 + var191 * var191);
                var151 /= var211;
                var171 /= var211;
                var191 /= var211;
                double var23 = 0.5D / (var211 / this.getRadius() + 0.1D);
                var23 *= (this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
                var151 *= var23;
                var171 *= var23;
                var191 *= var23;
                this.worldObj.spawnParticle("explode", (var9 + this.position.xPos * 1.0D) / 2.0D, (var11 + this.position.yPos * 1.0D) / 2.0D, (var13 + this.position.zPos * 1.0D) / 2.0D, var151, var171, var191);
                this.worldObj.spawnParticle("smoke", var9, var11, var13, var151, var171, var191);

                if(!worldObj.isAirBlock(x, y, z) && DefenseUtils.canBreak(worldObj, block, x, y, z))
                {
                    try {
                        if(block.canDropFromExplosion(null))
                        {
                            block.dropBlockAsItemWithChance(worldObj, x, y, z, worldObj.getBlockMetadata(x, y, z), 1F, 0);
                        }

                        block.onBlockExploded(worldObj, x, y, z, this);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void pushEntities(float radius, float force, int type)
    {
        // Step 2: Damage all entities
        Pos3D minCoord = position.clone();
        minCoord.translate(-radius - 1, -radius - 1, -radius - 1);
        Pos3D maxCoord = position.clone();
        maxCoord.translate(radius + 1, radius + 1, radius + 1);

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(minCoord.xPos, minCoord.yPos, minCoord.zPos, maxCoord.xPos, maxCoord.yPos, maxCoord.zPos);
        List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, box);

        for (Entity entity : entities)
        {
            double var13 = entity.getDistance(position.xPos, position.yPos, position.zPos) / radius;

            if (var13 <= 1.0D)
            {
                double xDifference = entity.posX - position.xPos;
                double yDifference = entity.posY - position.yPos;
                double zDifference = entity.posZ - position.zPos;
                double distance = MathHelper.sqrt_double(xDifference * xDifference + yDifference * yDifference + zDifference * zDifference);
                xDifference /= distance;
                yDifference /= distance;
                zDifference /= distance;

                if (type == 1)
                {
                    double modifier = var13 * force * (entity instanceof EntityPlayer ? 0.5 : 1);
                    entity.addVelocity(-xDifference * modifier, -yDifference * modifier, -zDifference * modifier);
                }
                else if (type == 2)
                {
                    double modifier = (1.0D - var13) * force * (entity instanceof EntityPlayer ? 0.5 : 1);
                    entity.addVelocity(xDifference * modifier, yDifference * modifier, zDifference * modifier);
                }
            }
        }
    }

    @Override
    public long getEnergy()
    {
        return 418000;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.pushType = nbt.getInteger("pushType");
        this.destroyItem = nbt.getBoolean("destroyItem");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("pushType", this.pushType);
        nbt.setBoolean("destroyItem", this.destroyItem);
    }
}
