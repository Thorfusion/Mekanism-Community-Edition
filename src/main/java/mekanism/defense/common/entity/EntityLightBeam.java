package defense.common.entity;

import io.netty.buffer.ByteBuf;
import mekanism.api.Pos3D;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityLightBeam extends Entity implements IEntityAdditionalSpawnData
{
    private int life;
    public float red, green, blue;

    public EntityLightBeam(World world)
    {
        super(world);
        this.setSize(1F, 1F);
        this.preventEntitySpawning = true;
        this.ignoreFrustumCheck = true;
        this.renderDistanceWeight = 3;
    }

    public EntityLightBeam(World world, Pos3D position, int life, float red, float green, float blue)
    {
        super(world);
        this.setPosition(position.xPos, position.yPos, position.zPos);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.life = life;
    }

    @Override
    public String getCommandSenderName()
    {
        return "Light Beam";
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        data.writeInt(this.life);
        data.writeFloat(this.red);
        data.writeFloat(this.green);
        data.writeFloat(this.blue);
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        this.life = data.readInt();
        this.red = data.readFloat();
        this.green = data.readFloat();
        this.blue = data.readFloat();
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void onUpdate()
    {
        if (this.life > 0)
        {
            this.life--;
        }
        else
        {
            this.setDead();
        }
    }

    @Override
    public float getShadowSize()
    {
        return 0F;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {

    }
}