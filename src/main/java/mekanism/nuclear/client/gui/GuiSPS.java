package mekanism.nuclear.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import java.util.Locale;
import mekanism.client.gui.GuiMekanism;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.nuclear.common.content.sps.SPSStatus;
import mekanism.nuclear.common.inventory.ContainerSPS;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Compact 1.7-native status screen for a Supercritical Phase Shifter. */
@SideOnly(Side.CLIENT)
public class GuiSPS extends GuiMekanism
{
    private static final ResourceLocation RESOURCE =
          MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");

    private final TileEntitySPSPort tile;

    public GuiSPS(EntityPlayer player, TileEntitySPSPort tile)
    {
        super(new ContainerSPS(player, tile));
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick,
          int mouseX, int mouseY)
    {
        mc.renderEngine.bindTexture(RESOURCE);
        GL11.glColor4f(1, 1, 1, 1);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        SPSStatus status = tile.getStatus();
        drawTank(14, 24, status.getPolonium(),
              status.getPoloniumCapacity(), 0xFF1B9E7B);
        drawTank(153, 24, status.getAntimatter(),
              status.getAntimatterCapacity(), 0xFFA464B3);
        drawBar(40, 91, 96, status.getProgress(), 0xFFA464B3);
        double energyLevel = status.getPortEnergyCapacity() <= 0 ? 0
              : status.getPortEnergy()
                    / (double)status.getPortEnergyCapacity();
        drawBar(14, 115, 148, energyLevel, 0xFF3B86C4);
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    }

    private void drawTank(int x, int y, long stored, long capacity, int color)
    {
        x += guiLeft;
        y += guiTop;
        drawRect(x, y, x + 10, y + 54, 0xFF303030);
        drawRect(x + 1, y + 1, x + 9, y + 53, 0xFF101010);
        if (stored > 0 && capacity > 0)
        {
            int height = Math.max(1,
                  (int)Math.min(52, 52D * stored / capacity));
            drawRect(x + 1, y + 53 - height, x + 9, y + 53, color);
        }
    }

    private void drawBar(int x, int y, int width, double level, int color)
    {
        x += guiLeft;
        y += guiTop;
        drawRect(x, y, x + width, y + 8, 0xFF303030);
        drawRect(x + 1, y + 1, x + width - 1, y + 7, 0xFF101010);
        if (level > 0)
        {
            int filled = Math.max(1,
                  (int)Math.min(width - 2, (width - 2) * level));
            drawRect(x + 1, y + 1, x + 1 + filled, y + 7, color);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        SPSStatus status = tile.getStatus();
        centeredText(LangUtils.localize("sps.mekanismnuclear.gui.title"),
              6, 0x404040);
        fontRendererObj.drawString("P", 16, 15, 0x404040);
        fontRendererObj.drawString("A", 155, 15, 0x404040);
        if (!status.isFormed())
        {
            centeredText(LangUtils.localize(
                  "sps.mekanismnuclear.gui.unformed"), 52, 0xAA2020);
        }
        else
        {
            drawLine(34, 26,
                  LangUtils.localize("sps.mekanismnuclear.gui.energy") + ": "
                        + number(status.getEnergyUsage()) + " J/t");
            drawLine(34, 39,
                  LangUtils.localize("sps.mekanismnuclear.gui.rate") + ": "
                        + decimal(status.getProcessRate()) + " mB/t");
            drawLine(34, 52,
                  LangUtils.localize("sps.mekanismnuclear.gui.coils") + ": "
                        + status.getCoils());
            drawLine(34, 65,
                  LangUtils.localize("sps.mekanismnuclear.gui.progress")
                        + ": " + decimal(status.getProgress() * 100D) + "%");
        }
        fontRendererObj.drawString(LangUtils.localize(
              "sps.mekanismnuclear.gui.port_energy"), 14, 104, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    private void centeredText(String text, int y, int color)
    {
        fontRendererObj.drawString(text,
              (xSize - fontRendererObj.getStringWidth(text)) / 2, y, color);
    }

    private void drawLine(int x, int y, String text)
    {
        renderScaledText(text, x, y, 0x404040, 108);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTick)
    {
        super.drawScreen(mouseX, mouseY, partialTick);
        SPSStatus status = tile.getStatus();
        if (inRegion(14, 24, 10, 54, mouseX, mouseY))
        {
            tooltip("gas.polonium", status.getPolonium(),
                  status.getPoloniumCapacity(), " mB", mouseX, mouseY);
        }
        else if (inRegion(153, 24, 10, 54, mouseX, mouseY))
        {
            tooltip("gas.antimatter", status.getAntimatter(),
                  status.getAntimatterCapacity(), " mB", mouseX, mouseY);
        }
        else if (inRegion(14, 115, 148, 8, mouseX, mouseY))
        {
            tooltip("sps.mekanismnuclear.gui.port_energy",
                  status.getPortEnergy(), status.getPortEnergyCapacity(),
                  " J", mouseX, mouseY);
        }
    }

    private boolean inRegion(int x, int y, int width, int height,
          int mouseX, int mouseY)
    {
        return mouseX >= guiLeft + x && mouseX < guiLeft + x + width
              && mouseY >= guiTop + y && mouseY < guiTop + y + height;
    }

    private void tooltip(String key, long stored, long capacity, String unit,
          int mouseX, int mouseY)
    {
        func_146283_a(Arrays.asList(LangUtils.localize(key),
              number(stored) + " / " + number(capacity) + unit),
              mouseX, mouseY);
    }

    private static String decimal(double value)
    {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    private static String number(long value)
    {
        return String.format(Locale.ROOT, "%,d", value);
    }
}
