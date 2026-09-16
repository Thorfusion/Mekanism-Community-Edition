package defense.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.tile.ModelLauncherBaseT1;
import defense.client.model.tile.ModelLauncherRailT1;
import defense.common.Reference;
import defense.common.tile.TileLauncherBase;

@SideOnly(Side.CLIENT)
public class RenderLauncherBase extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE_0 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_0.png");
    public static final ResourceLocation TEXTURE_FILE_1 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_1.png");
    public static final ResourceLocation TEXTURE_FILE_2 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_2.png");

    public static final ModelLauncherBaseT1 MODEL_BASE = new ModelLauncherBaseT1();
    public static final ModelLauncherRailT1 MODEL_RAIL = new ModelLauncherRailT1();

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f)
    {
        TileLauncherBase tileEntity = (TileLauncherBase) tileentity;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        ForgeDirection side = ForgeDirection.getOrientation(tileEntity.facing);
        
        if (side != ForgeDirection.NORTH && side != ForgeDirection.SOUTH)
        {
            GL11.glRotatef(90F, 0F, 180F, 1.0F);
        }

        this.bindTexture(getTexture(tileEntity.getTier()));
        MODEL_BASE.render(0.0625F);
        MODEL_RAIL.render(0.0625F);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    public static ResourceLocation getTexture(int tier)
    {
        switch (tier)
        {
            case 1:
                return TEXTURE_FILE_1;
            case 2:
                return TEXTURE_FILE_2;
            default:
                return TEXTURE_FILE_0;
        }
    }
}
