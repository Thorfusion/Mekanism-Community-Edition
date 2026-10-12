package defense.common.integration.flans;

import com.flansmod.common.guns.BulletType;
import com.flansmod.common.guns.EntityBullet;
import com.flansmod.common.types.InfoType;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import defense.api.ExplosionEvent.ExplosivePreDetonationEvent;
import defense.api.ExplosiveType;
import defense.common.explosive.Explosive;
import defense.common.explosive.ExplosiveRegistry;

/** Flan projectile physics with DefenseTech detonation behavior. */
public class EntityDefenseTechBomb extends EntityBullet
{
    private static final String NBT_EXPLOSIVE_ID = "DefenseTechExplosiveId";

    private int explosiveId = -1;
    private boolean payloadDispatched;

    public EntityDefenseTechBomb(World world)
    {
        super(world);
    }

    public EntityDefenseTechBomb(World world, Vec3 origin, float yaw, float pitch, double motionX, double motionY, double motionZ,
          EntityLivingBase shooter, float gunDamage, BulletType type, InfoType shotFrom, int explosiveId)
    {
        super(world, origin, yaw, pitch, motionX, motionY, motionZ, shooter, gunDamage, type, shotFrom);
        this.explosiveId = explosiveId;
    }

    public int getExplosiveId()
    {
        return explosiveId;
    }

    @Override
    public void setDead()
    {
        if(isDead)
        {
            return;
        }

        World world = worldObj;
        double x = posX;
        double y = posY;
        double z = posZ;
        boolean dispatch = !payloadDispatched && world != null && !world.isRemote && explosiveId >= 0;
        Explosive explosive = dispatch ? ExplosiveRegistry.get(explosiveId) : null;

        if(explosive == null || !explosive.hasBlockForm())
        {
            dispatch = false;
        }
        else
        {
            dispatch = !MinecraftForge.EVENT_BUS.post(new ExplosivePreDetonationEvent(world, this, ExplosiveType.AIR, explosive));
        }

        payloadDispatched = true;

        super.setDead();

        if(dispatch)
        {
            explosive.createExplosion(world, x, y, z, this);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag)
    {
        super.writeEntityToNBT(tag);
        tag.setInteger(NBT_EXPLOSIVE_ID, explosiveId);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag)
    {
        super.readEntityFromNBT(tag);
        explosiveId = tag.getInteger(NBT_EXPLOSIVE_ID);
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        super.writeSpawnData(data);
        data.writeInt(explosiveId);
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        super.readSpawnData(data);
        explosiveId = data.readInt();
    }
}
