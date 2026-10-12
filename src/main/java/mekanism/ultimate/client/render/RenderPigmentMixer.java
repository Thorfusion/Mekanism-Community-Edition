package mekanism.ultimate.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.ultimate.common.tile.TileEntityPigmentMixer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Renders the active mixer's source-faithful rotating shaft. */
@SideOnly(Side.CLIENT)
public final class RenderPigmentMixer extends TileEntitySpecialRenderer
{
    private static final ResourceLocation DETAILS = new ResourceLocation(
          "mekanismultimate", "textures/blocks/pigment_mixer/details.png");
    private static final double MIN = 0D;
    private static final double MAX = 2D / 16D;
    private static final double BOTTOM = 18D / 16D;
    private static final double TOP = 27D / 16D;
    private static final double MIN_U = 14D / 16D;
    private static final double MAX_U = 15D / 16D;
    private static final double MIN_V = 4D / 16D;
    private static final double MAX_V = 8.5D / 16D;

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y,
          double z, float partialTick)
    {
        if (!(tile instanceof TileEntityPigmentMixer)
              || !((TileEntityPigmentMixer)tile).getActive()
              || tile.getWorldObj() == null)
        {
            return;
        }
        TileEntityPigmentMixer mixer = (TileEntityPigmentMixer)tile;
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glTranslated(x, y, z);
        translateForFacing(mixer.facing);
        GL11.glTranslated(1D / 16D, 0D, 1D / 16D);
        GL11.glRotatef(-((tile.getWorldObj().getTotalWorldTime()
              + partialTick) * 5F % 360F), 0F, 1F, 0F);
        GL11.glTranslated(-1D / 16D, 0D, -1D / 16D);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        bindTexture(DETAILS);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(tile.getBlockType()
              .getMixedBrightnessForBlock(tile.getWorldObj(),
                    tile.xCoord, tile.yCoord + 1, tile.zCoord));
        drawShaft(tessellator);
        tessellator.draw();

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private static void translateForFacing(int facing)
    {
        switch (facing)
        {
            case 3:
                GL11.glTranslated(7D / 16D, 0D, 0.5D);
                break;
            case 4:
                GL11.glTranslated(6D / 16D, 0D, 7D / 16D);
                break;
            case 5:
                GL11.glTranslated(0.5D, 0D, 7D / 16D);
                break;
            default:
                GL11.glTranslated(7D / 16D, 0D, 6D / 16D);
                break;
        }
    }

    private static void drawShaft(Tessellator tessellator)
    {
        tessellator.setNormal(0F, 0F, -1F);
        quad(tessellator, MAX, TOP, MIN, MAX, BOTTOM, MIN,
              MIN, BOTTOM, MIN, MIN, TOP, MIN);
        tessellator.setNormal(1F, 0F, 0F);
        quad(tessellator, MAX, TOP, MAX, MAX, BOTTOM, MAX,
              MAX, BOTTOM, MIN, MAX, TOP, MIN);
        tessellator.setNormal(0F, 0F, 1F);
        quad(tessellator, MIN, TOP, MAX, MIN, BOTTOM, MAX,
              MAX, BOTTOM, MAX, MAX, TOP, MAX);
        tessellator.setNormal(-1F, 0F, 0F);
        quad(tessellator, MIN, TOP, MIN, MIN, BOTTOM, MIN,
              MIN, BOTTOM, MAX, MIN, TOP, MAX);
    }

    private static void quad(Tessellator tessellator, double x1,
          double y1, double z1, double x2, double y2, double z2,
          double x3, double y3, double z3, double x4, double y4,
          double z4)
    {
        tessellator.addVertexWithUV(x1, y1, z1, MIN_U, MIN_V);
        tessellator.addVertexWithUV(x2, y2, z2, MIN_U, MAX_V);
        tessellator.addVertexWithUV(x3, y3, z3, MAX_U, MAX_V);
        tessellator.addVertexWithUV(x4, y4, z4, MAX_U, MIN_V);
    }
}
