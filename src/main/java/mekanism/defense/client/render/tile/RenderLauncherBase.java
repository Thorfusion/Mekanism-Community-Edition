package defense.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.tile.ModelLauncherBaseT1;
import defense.client.model.tile.ModelLauncherBaseT2;
import defense.client.model.tile.ModelLauncherBaseT3;
import defense.client.model.tile.ModelLauncherRailT1;
import defense.client.model.tile.ModelLauncherRailT2;
import defense.client.model.tile.ModelLauncherRailT3;
import defense.common.Reference;
import defense.common.tile.TileLauncherBase;

@SideOnly(Side.CLIENT)
public class RenderLauncherBase extends TileEntitySpecialRenderer
{
    public static final ResourceLocation TEXTURE_FILE_0 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_0.png");
    public static final ResourceLocation TEXTURE_FILE_1 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_1.png");
    public static final ResourceLocation TEXTURE_FILE_2 = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "launcher_2.png");

    public static final ModelLauncherBaseT1 modelBase0 = new ModelLauncherBaseT1();
    public static final ModelLauncherRailT1 modelRail0 = new ModelLauncherRailT1();

    public static final ModelLauncherBaseT2 modelBase1 = new ModelLauncherBaseT2();
    public static final ModelLauncherRailT2 modelRail1 = new ModelLauncherRailT2();

    public static final ModelLauncherBaseT3 modelBase2 = new ModelLauncherBaseT3();
    public static final ModelLauncherRailT3 modelRail2 = new ModelLauncherRailT3();

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

        // The missile launcher screen
        if (tileEntity.getTier() == 0)
        {
            this.bindTexture(TEXTURE_FILE_0);
            modelBase0.render(0.0625F);
            modelRail0.render(0.0625F);
        }
        else if (tileEntity.getTier() == 1)
        {
            this.bindTexture(TEXTURE_FILE_1);
            modelBase1.render(0.0625F);
            modelRail1.render(0.0625F);
            GL11.glRotatef(180F, 0F, 180F, 1.0F);
            modelRail1.render(0.0625F);
        }
        else if (tileEntity.getTier() == 2)
        {
            this.bindTexture(TEXTURE_FILE_2);
            modelBase2.render(0.0625F);
            modelRail2.render(0.0625F);
            GL11.glRotatef(180F, 0F, 180F, 1.0F);
            modelRail2.render(0.0625F);
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
}