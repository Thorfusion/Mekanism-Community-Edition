package defense.client.render.entity;

import java.util.HashMap;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.IExplosive;
import defense.client.model.missile.ModelMissileBase;
import defense.common.entity.EntityMissile;
import defense.common.entity.EntityMissile.MissileType;
import defense.common.explosion.Explosion;

@SideOnly(Side.CLIENT)
/** @author Calclavia */
public class RenderMissile extends Render
{
    public static final HashMap<Explosion, ModelMissileBase> cache = new HashMap<Explosion, ModelMissileBase>();

    public RenderMissile(float f)
    {
        this.shadowSize = f;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float f, float partial)
    {
        EntityMissile entityMissile = (EntityMissile) entity;
        IExplosive e = entityMissile.getExplosiveType();
        
        if (e instanceof Explosion)
        {
            Explosion missile = (Explosion) e;

            GL11.glPushMatrix();
            GL11.glTranslated(x + (entityMissile.motionX * partial), y + (entityMissile.motionY * partial), z + (entityMissile.motionZ * partial));
            GL11.glRotatef(entityMissile.prevRotationYaw + (entityMissile.rotationYaw - entityMissile.prevRotationYaw) * partial - 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(entityMissile.prevRotationPitch + (entityMissile.rotationPitch - entityMissile.prevRotationPitch) * partial + 90.0F, 0.0F, 0.0F, 1.0F);

            if (entityMissile.missileType == MissileType.CruiseMissile)
            {
                GL11.glScalef(0.5f, 0.5f, 0.5f);
            }

            FMLClientHandler.instance().getClient().renderEngine.bindTexture(missile.getMissileResource());
            
            synchronized (cache)
            {
                if (!RenderMissile.cache.containsKey(missile))
                {
                    RenderMissile.cache.put(missile, missile.getMissileModel());
                }
                
                RenderMissile.cache.get(missile).render(0.0625F);
            }

            GL11.glPopMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return null;
    }
}