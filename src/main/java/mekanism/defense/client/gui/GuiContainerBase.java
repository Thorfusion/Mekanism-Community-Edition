package defense.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import defense.common.Reference;

public abstract class GuiContainerBase extends GuiContainer
{
    public static final ResourceLocation GUI_EMPTY_TEXTURE = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_container.png");
    public static final ResourceLocation GUI_COMPONENTS = new ResourceLocation(Reference.DOMAIN, Reference.GUI_PATH + "gui_components.png");
    
    public ResourceLocation baseTexture;
    
    protected int containerWidth;
    protected int containerHeight;
    
    public GuiContainerBase(Container container)
    {
        super(container);
        this.baseTexture = GUI_EMPTY_TEXTURE;
    }
    
    protected void drawSlot(int x, int y, ItemStack itemStack)
    {
        this.mc.renderEngine.bindTexture(this.baseTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);

        this.drawItemStack(itemStack, this.containerWidth + x, this.containerHeight + y);
    }
    
    protected void drawSlot(int x, int y, GuiSlotType type, float r, float g, float b)
    {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(r, g, b, 1.0F);

        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 0, 18, 18);

        if (type != GuiSlotType.NONE)
        {
            this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 0, 18 * type.ordinal(), 18, 18);
        }
    }
    
    protected void drawSlot(int x, int y)
    {
        this.drawSlot(x, y, GuiSlotType.NONE);
    }
    
    protected void drawSlot(int x, int y, GuiSlotType type)
    {
        this.drawSlot(x, y, type, 1, 1, 1);
    }
    
    protected void drawItemStack(ItemStack itemStack, int x, int y)
    {
        x += 1;
        y += 1;
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);

        // drawTexturedModelRectFromIcon
        // GL11.glEnable(GL11.GL_BLEND);
        // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, itemStack, x, y);
        // GL11.glDisable(GL11.GL_BLEND);
    }
    
    protected void drawBar(int x, int y, float scale)
    {
        this.mc.renderEngine.bindTexture(GUI_COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        /** Draw background progress bar/ */
        this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 0, 22, 15);

        if (scale > 0)
        {
            /** Draw white color actual progress. */
            this.drawTexturedModalRect(this.containerWidth + x, this.containerHeight + y, 18, 15, 22 - (int) (scale * 22), 15);
        }
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        this.containerWidth = (this.width - this.xSize) / 2;
        this.containerHeight = (this.height - this.ySize) / 2;

        this.mc.renderEngine.bindTexture(this.baseTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, this.xSize, this.ySize);
    }
    
    public enum GuiSlotType
    {
        NONE,
        BATTERY,
        LIQUID,
        ARR_UP,
        ARR_DOWN,
        ARR_LEFT,
        ARR_RIGHT,
        ARR_UP_RIGHT,
        ARR_UP_LEFT,
        ARR_DOWN_LEFT,
        ARR_DOWN_RIGHT
    }
}
