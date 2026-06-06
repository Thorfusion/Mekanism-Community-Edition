package defense.client.render;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_POLYGON_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glTranslatef;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.client.FMLClientHandler;

public class RenderUtils 
{
	public static final ResourceLocation PARTICLE_RESOURCE = new ResourceLocation("textures/particle/particles.png");
	
	public static void setTerrainTexture()
    {
        setSpriteTexture(0);
    }

    public static void setSpriteTexture(ItemStack itemStack)
    {
        setSpriteTexture(itemStack.getItemSpriteNumber());
    }

    public static void setSpriteTexture(int sprite)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(getSpriteTexture(sprite));
    }
    
    public static ResourceLocation getSpriteTexture(int sprite)
    {
        return FMLClientHandler.instance().getClient().renderEngine.getResourceLocation(sprite);
    }
    
    /** Enables blending. */
    public static void enableBlending()
    {
        glShadeModel(GL_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    /** Disables blending. */
    public static void disableBlending()
    {
        glShadeModel(GL_FLAT);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
        glDisable(GL_BLEND);
    }

    public static void enableLighting()
    {
        RenderHelper.enableStandardItemLighting();
    }

    /** Disables lighting and turns glow on. */
    public static void disableLighting()
    {
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    }
    
    public static void renderNormalBlockAsItem(Block block, int metadata, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;

        block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(block);
        glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        tessellator.draw();
        glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
