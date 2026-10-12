package mekanism.ultimate.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.client.renderer.entity.elytra.ModelElytra;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/** Shared worn-wing renderer using Et Futurum's animated Elytra model. */
@SideOnly(Side.CLIENT)
public final class EtFuturumElytraRenderer
{
    private static final ResourceLocation WING_TEXTURE =
          new ResourceLocation("mekanismultimate",
                "textures/entity/hdpe_elytra.png");
    private static final ResourceLocation ENCHANTED_GLINT =
          new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final ModelElytra MODEL = new ModelElytra();
    private static final float SCALE = 0.0625F;

    private EtFuturumElytraRenderer()
    {
    }

    public static void render(EntityPlayer player, ItemStack chest,
          float partialTicks)
    {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        Minecraft.getMinecraft().renderEngine.bindTexture(WING_TEXTURE);
        GL11.glTranslatef(0F, 0F, 0.125F);
        float age = player.ticksExisted + partialTicks;
        MODEL.setLivingAnimations(player, 0F, 0F, partialTicks);
        MODEL.setRotationAngles(0F, 0F, age, 0F, 0F, SCALE, player);
        MODEL.render(player, 0F, 0F, age, 0F, 0F, SCALE);
        if (chest != null && chest.isItemEnchanted())
        {
            renderGlint(player, age, partialTicks);
        }
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static void renderGlint(EntityPlayer player, float age,
          float partialTicks)
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(ENCHANTED_GLINT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDepthMask(false);
        GL11.glColor4f(0.38F, 0.19F, 0.608F, 1F);
        for (int pass = 0; pass < 2; pass++)
        {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            GL11.glScalef(1F / 3F, 1F / 3F, 1F / 3F);
            GL11.glRotatef(30F - pass * 60F, 0F, 0F, 1F);
            GL11.glTranslatef(0F,
                  age * (0.001F + pass * 0.003F) * 20F, 0F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            MODEL.render(player, 0F, 0F, age, 0F, 0F, SCALE);
        }
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
