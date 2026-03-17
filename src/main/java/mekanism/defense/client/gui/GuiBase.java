package defense.client.gui;

import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.tile.TileEntityContainerBlock;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import defense.common.Reference;

public abstract class GuiBase extends GuiContainer
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_empty.png");

    protected int containerWidth;
    protected int containerHeight;

    private TileEntity tileEntity;

    public GuiBase()
    {
        super(new ContainerNull());
    }

    public GuiBase(TileEntityContainerBlock tile)
    {
        super(new ContainerNull(tile));
        tileEntity = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        containerWidth = (width - xSize) / 2;
        containerHeight = (height - ySize) / 2;
        drawTexturedModalRect(containerWidth, containerHeight, 0, 0, xSize, ySize);
    }
}
