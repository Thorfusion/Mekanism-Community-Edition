package mekanism.nuclear.client.gui;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Compact 1.12-native status screen for a formed Supercritical Phase Shifter. */
@SideOnly(Side.CLIENT)
public class GuiSPS extends GuiMekanism {

    private final TileEntitySPSPort tile;

    public GuiSPS(EntityPlayer player, TileEntitySPSPort tile) {
        super(new ContainerSPS(player, tile));
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int mouseX, int mouseY) {
        SPSStatus status = tile.getStatus();
        drawTank(14, 24, status.getPolonium(), status.getPoloniumCapacity(), 0xFF1B9E7B);
        drawTank(153, 24, status.getAntimatter(), status.getAntimatterCapacity(), 0xFFA464B3);
        drawRect(40, 91, 136, 99, 0xFF303030);
        drawRect(41, 92, 135, 98, 0xFF101010);
        if (status.getProgress() > 0) {
            int width = Math.max(1, (int) Math.min(94, 94D * status.getProgress()));
            drawRect(41, 92, 41 + width, 98, 0xFFA464B3);
        }
        drawRect(14, 115, 162, 123, 0xFF303030);
        drawRect(15, 116, 161, 122, 0xFF101010);
        if (status.getPortEnergy() > 0 && status.getPortEnergyCapacity() > 0) {
            int width = Math.max(1, (int) Math.min(146,
                  146D * status.getPortEnergy() / status.getPortEnergyCapacity()));
            drawRect(15, 116, 15 + width, 122, 0xFF3B86C4);
        }
    }

    private void drawTank(int x, int y, long stored, long capacity, int color) {
        drawRect(x, y, x + 10, y + 54, 0xFF303030);
        drawRect(x + 1, y + 1, x + 9, y + 53, 0xFF101010);
        if (stored > 0 && capacity > 0) {
            int height = Math.max(1, (int) Math.min(52, 52D * stored / capacity));
            drawRect(x + 1, y + 53 - height, x + 9, y + 53, color);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        SPSStatus status = tile.getStatus();
        renderCenteredText(0, xSize, 6, 0x404040, LangUtils.localize("sps.mekanismnuclear.gui.title"));
        fontRenderer.drawString("P", 16, 15, 0x404040);
        fontRenderer.drawString("A", 155, 15, 0x404040);
        if (!status.isFormed()) {
            renderCenteredText(28, 148, 52, 0xAA2020,
                  LangUtils.localize("sps.mekanismnuclear.gui.unformed"));
        } else {
            drawLine(34, 26, LangUtils.localize("sps.mekanismnuclear.gui.energy") + ": "
                  + number(status.getEnergyUsage()) + " J/t");
            drawLine(34, 39, LangUtils.localize("sps.mekanismnuclear.gui.rate") + ": "
                  + decimal(status.getProcessRate()) + " mB/t");
            drawLine(34, 52, LangUtils.localize("sps.mekanismnuclear.gui.coils") + ": "
                  + status.getCoils());
            drawLine(34, 65, LangUtils.localize("sps.mekanismnuclear.gui.progress") + ": "
                  + decimal(status.getProgress() * 100D) + "%");
        }
        fontRenderer.drawString(LangUtils.localize("sps.mekanismnuclear.gui.port_energy"), 14, 104, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    private void drawLine(int x, int y, String text) {
        renderScaledText(text, x, y, 0x404040, 108);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        SPSStatus status = tile.getStatus();
        if (isPointInRegion(14, 24, 10, 54, mouseX, mouseY)) {
            drawHoveringText(Arrays.asList(LangUtils.localize("gas.polonium"),
                  number(status.getPolonium()) + " / " + number(status.getPoloniumCapacity()) + " mB"),
                  mouseX, mouseY);
        } else if (isPointInRegion(153, 24, 10, 54, mouseX, mouseY)) {
            drawHoveringText(Arrays.asList(LangUtils.localize("gas.antimatter"),
                  number(status.getAntimatter()) + " / " + number(status.getAntimatterCapacity()) + " mB"),
                  mouseX, mouseY);
        } else if (isPointInRegion(14, 115, 148, 8, mouseX, mouseY)) {
            drawHoveringText(Arrays.asList(LangUtils.localize("sps.mekanismnuclear.gui.port_energy"),
                  number(status.getPortEnergy()) + " / " + number(status.getPortEnergyCapacity()) + " J"),
                  mouseX, mouseY);
        }
    }

    private static String decimal(double value) {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    private static String number(long value) {
        return String.format(Locale.ROOT, "%,d", value);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");
    }
}
