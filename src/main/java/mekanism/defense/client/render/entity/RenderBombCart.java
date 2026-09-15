package defense.client.render.entity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderTntMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.common.entity.EntityBombCart;
import defense.common.explosive.Explosive;
import defense.common.explosive.ExplosiveRegistry;

/** Renders custom explosive payload models in bomb carts while retaining TNT fuse effects. */
@SideOnly(Side.CLIENT)
public class RenderBombCart extends RenderTntMinecart
{
    @Override
    protected void func_147910_a(EntityMinecartTNT entity, float partialTick, Block block, int metadata)
    {
        if(!(entity instanceof EntityBombCart))
        {
            super.func_147910_a(entity, partialTick, block, metadata);
            return;
        }

        Explosive explosive = ExplosiveRegistry.get(((EntityBombCart)entity).explosiveID);
        if(explosive == null || explosive.getBlockModel() == null || explosive.getBlockResource() == null)
        {
            super.func_147910_a(entity, partialTick, block, metadata);
            return;
        }

        int fuse = entity.func_94104_d();
        if(fuse > -1 && (float)fuse - partialTick + 1.0F < 10.0F)
        {
            float pulse = 1.0F - ((float)fuse - partialTick + 1.0F) / 10.0F;
            pulse = Math.max(0.0F, Math.min(1.0F, pulse));
            pulse *= pulse;
            pulse *= pulse;
            float scale = 1.0F + pulse * 0.3F;
            GL11.glScalef(scale, scale, scale);
        }

        renderModel(explosive, true);

        if(fuse > -1 && fuse / 5 % 2 == 0)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, (1.0F - ((float)fuse - partialTick + 1.0F) / 100.0F) * 0.8F);
            renderModel(explosive, false);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    private void renderModel(Explosive explosive, boolean bindTexture)
    {
        GL11.glPushMatrix();
        // RenderMinecart already applies its 0.75 display-payload scale.
        // Center the shared model around the same local origin as a block.
        GL11.glTranslatef(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        if(bindTexture)
        {
            bindTexture(explosive.getBlockResource());
        }
        explosive.getBlockModel().render(0.0625F);
        GL11.glPopMatrix();
    }
}
