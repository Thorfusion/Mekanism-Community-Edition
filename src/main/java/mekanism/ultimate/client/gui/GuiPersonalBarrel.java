package mekanism.ultimate.client.gui;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.common.inventory.container.ContainerPersonalChest;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiPersonalBarrel extends GuiMekanismTile<TileEntityPersonalBarrel> {

    public GuiPersonalBarrel(InventoryPlayer inventory, TileEntityPersonalBarrel tile) {
        super(tile, new ContainerPersonalChest(inventory, tile));
        xSize += 26;
        ySize += 64;
        addGuiElement(new GuiSecurityTab(this, tileEntity, getGuiLocation()));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(LangUtils.localize("tile.PersonalBarrel.name"), 8, 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiPersonalChest.png");
    }
}
