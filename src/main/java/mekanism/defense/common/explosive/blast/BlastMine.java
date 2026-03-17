package defense.common.explosive.blast;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.missile.ModelMissileBase;
import defense.client.model.tile.ModelSMine;

public class BlastMine extends Blast
{
    public BlastMine(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    @Override
    public void doPreExplode()
    {
        if (!this.worldObj.isRemote)
        {
            this.worldObj.createExplosion(this.controller, position.xPos, position.yPos, position.zPos, 1.5f, true);
        }

        this.controller.motionX = -0.125 + 0.25 * this.worldObj.rand.nextFloat();
        this.controller.motionY = 0.7 + 0.4 * this.worldObj.rand.nextFloat();
        this.controller.motionZ = -0.125 + 0.25 * this.worldObj.rand.nextFloat();
    }

    @Override
    public void doExplode()
    {
        this.controller.motionY -= 0.045;
        this.controller.rotationPitch += 1.5 * this.worldObj.rand.nextFloat();

        if (!this.worldObj.isRemote)
        {
            if (this.callCount < 20 * 2 && !this.controller.isCollided)
            {
                return;
            }

            if (this.callCount >= 20 * 2 && this.callCount % 2 == 0)
            {
                new BlastShrapnel(this.worldObj, this.exploder, this.position.xPos, this.position.yPos, this.position.zPos, this.getRadius(), true, true, false).doExplode();
            }

            if (this.callCount >= 20 * 2 + 20)
            {
                this.controller.endExplosion();
            }
        }
    }

    @Override
    public boolean isMovable()
    {
        return true;
    }

    @Override
    public int proceduralInterval()
    {
        return 1;
    }

    @Override
    public long getEnergy()
    {
        return 8000;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelMissileBase getRenderModel()
    {
        return ModelSMine.INSTANCE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getRenderResource()
    {
        return ModelSMine.TEXTURE;
    }

}
