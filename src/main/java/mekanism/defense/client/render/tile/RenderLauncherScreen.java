package defense.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.tile.ModelLauncherScreenT1;
import defense.common.Reference;
import defense.common.tile.TileLauncherScreen;
import mekanism.client.model.ModelSecurityDesk;

@SideOnly(Side.CLIENT)
public class RenderLauncherScreen extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE_0 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_screen_0.png");
    public static final ResourceLocation TEXTURE_FILE_1 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_screen_1.png");
    public static final ResourceLocation TEXTURE_FILE_2 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_screen_2.png");
    public static final ResourceLocation TEXTURE_FILE_2_OVERLAY = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_screen_2_overlay.png");

    public static final ModelLauncherScreenT1 MODEL_T1 = new ModelLauncherScreenT1();
    public static final ModelSecurityDesk MODEL_T3 = new ModelSecurityDesk();

    @Override
    public void renderTileEntityAt(TileEntity var1, double x, double y, double z, float var8)
    {
        TileLauncherScreen tileEntity = (TileLauncherScreen) var1;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        if (tileEntity.getTier() == 2)
        {
            switch (tileEntity.facing)
            {
                case 3:
                    GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
                    break;
                case 4:
                    GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
                    break;
                case 5:
                    GL11.glRotatef(270F, 0.0F, 1.0F, 0.0F);
                    break;
            }

            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
            this.bindTexture(TEXTURE_FILE_2);
            renderT3(field_147501_a.field_147553_e);
        }
        else
        {
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

            switch (tileEntity.facing)
            {
                case 3:
                    GL11.glRotatef(180F, 0.0F, 180F, 1.0F);
                    break;
                case 4:
                    GL11.glRotatef(-90F, 0.0F, 180F, 1.0F);
                    break;
                case 5:
                    GL11.glRotatef(90F, 0.0F, 180F, 1.0F);
                    break;
            }

            this.bindTexture(getTexture(tileEntity.getTier()));
            MODEL_T1.render(0.0625F);
        }

        GL11.glPopMatrix();
    }

    public static ResourceLocation getTexture(int tier)
    {
        return tier == 1 ? TEXTURE_FILE_1 : TEXTURE_FILE_0;
    }

    public static void renderT3(TextureManager textureManager)
    {
        ResourceLocation sharedOverlay = ModelSecurityDesk.OVERLAY;

        try
        {
            // ModelSecurityDesk is shared Core code. Swap its public overlay only
            // for this synchronous render so DefenseTech can supply its own screen
            // without requiring a new method in the installed Core jar.
            ModelSecurityDesk.OVERLAY = TEXTURE_FILE_2_OVERLAY;
            MODEL_T3.render(0.0625F, textureManager);
        }
        finally
        {
            ModelSecurityDesk.OVERLAY = sharedOverlay;
        }
    }

}
