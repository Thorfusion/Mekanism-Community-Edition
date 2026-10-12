package mekanism.ultimate.client.gui.element;

import codechicken.lib.gui.GuiDraw;
import codechicken.lib.vec.Rectangle4i;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.GuiGauge;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.common.content.chemical.PigmentTypeCE;
import mekanism.ultimate.common.tile.TileEntityPigmentMachine;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Solid-color gauge for facade chemicals that have no legacy block icon. */
@SideOnly(Side.CLIENT)
public final class GuiChemicalGauge extends GuiElement
{
    private final IChemicalInfoHandler handler;
    private final int xLocation;
    private final int yLocation;
    private final int width;
    private final int height;
    private final int textureY;

    private IChemicalStackCE dummyStack;
    private long dummyCapacity = 1;

    public GuiChemicalGauge(IChemicalInfoHandler handler,
          GuiGauge.Type type, IGuiWrapper gui, ResourceLocation def,
          int x, int y)
    {
        super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT,
              type.textureLocation), gui, def);
        this.handler = handler;
        xLocation = x;
        yLocation = y;
        width = type.width;
        height = type.height;
        textureY = type.texY;
    }

    public static GuiChemicalGauge getDummy(GuiGauge.Type type,
          IGuiWrapper gui, ResourceLocation def, int x, int y)
    {
        return new GuiChemicalGauge(null, type, gui, def, x, y);
    }

    public void setDummy(IChemicalStackCE stack, long capacity)
    {
        dummyStack = stack;
        dummyCapacity = Math.max(1, capacity);
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight)
    {
        return new Rectangle4i(guiWidth + xLocation,
              guiHeight + yLocation, width, height);
    }

    @Override
    public void renderBackground(int mouseX, int mouseY,
          int guiWidth, int guiHeight)
    {
        mc.renderEngine.bindTexture(RESOURCE);
        guiObj.drawTexturedRect(guiWidth + xLocation,
              guiHeight + yLocation, 0, textureY, width, height);

        IChemicalStackCE stack = getStack();
        int scale = getScaledLevel();
        if (stack != null && scale > 0)
        {
            int color = stack.getType() instanceof PigmentTypeCE
                  ? ((PigmentTypeCE)stack.getType()).getColor()
                  : 0x48D1CC;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GuiDraw.drawRect(guiWidth + xLocation + 1,
                  guiHeight + yLocation + height - scale - 1,
                  guiWidth + xLocation + width - 1,
                  guiHeight + yLocation + height - 1,
                  0xFF000000 | color);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1);
        }

        mc.renderEngine.bindTexture(RESOURCE);
        guiObj.drawTexturedRect(guiWidth + xLocation,
              guiHeight + yLocation, width, textureY, width, height);
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY)
    {
        if (mouseX < xLocation + 1 || mouseX > xLocation + width - 1
              || mouseY < yLocation + 1
              || mouseY > yLocation + height - 1)
        {
            return;
        }
        IChemicalStackCE stack = getStack();
        if (stack == null)
        {
            displayTooltip(LangUtils.localize("gui.empty"), mouseX,
                  mouseY);
            return;
        }
        String amount = String.format(LangUtils.localize(
              "gui.chemical.amount"), stack.getAmount());
        displayTooltip(TileEntityPigmentMachine.getChemicalName(
              stack.getType()) + ": " + amount, mouseX, mouseY);
    }

    public int getScaledLevel()
    {
        IChemicalStackCE stack = getStack();
        long capacity = getCapacity();
        if (stack == null || stack.getAmount() <= 0 || capacity <= 0)
        {
            return 0;
        }
        return Math.min(height - 2, Math.max(1,
              (int)Math.ceil(stack.getAmount() * (height - 2D)
                    / capacity)));
    }

    private IChemicalStackCE getStack()
    {
        IChemicalTankCE tank = handler == null ? null : handler.getTank();
        return tank == null ? dummyStack : tank.getStack();
    }

    private long getCapacity()
    {
        IChemicalTankCE tank = handler == null ? null : handler.getTank();
        return tank == null ? dummyCapacity : tank.getCapacity();
    }

    @Override
    public void preMouseClicked(int mouseX, int mouseY, int button)
    {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button)
    {
    }

    public interface IChemicalInfoHandler
    {
        IChemicalTankCE getTank();
    }
}
