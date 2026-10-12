package mekanism.ultimate.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiSecurityTab;
import mekanism.common.inventory.container.ContainerPersonalChest;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class GuiPersonalBarrel extends GuiMekanism
{
    private final TileEntityPersonalBarrel tile;

    public GuiPersonalBarrel(InventoryPlayer inventory,
          TileEntityPersonalBarrel tile)
    {
        super(tile, new ContainerPersonalChest(inventory, tile, null,
              true, -1));
        this.tile = tile;
        xSize += 26;
        ySize += 64;
        guiElements.add(new GuiSecurityTab(this, tile,
              MekanismUtils.getResource(ResourceType.GUI,
                    "GuiPersonalChest.png")));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRendererObj.drawString(tile.getInventoryName(), 8, 6,
              0x404040);
        fontRendererObj.drawString(LangUtils.localize("container.inventory"),
              8, ySize - 94, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick,
          int mouseX, int mouseY)
    {
        mc.renderEngine.bindTexture(MekanismUtils.getResource(
              ResourceType.GUI, "GuiPersonalChest.png"));
        GL11.glColor4f(1F, 1F, 1F, 1F);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    }
}
