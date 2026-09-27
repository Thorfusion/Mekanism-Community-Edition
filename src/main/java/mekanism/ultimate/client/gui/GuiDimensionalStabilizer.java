package mekanism.ultimate.client.gui;

import java.io.IOException;
import mekanism.api.TileNetworkList;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.ultimate.common.inventory.ContainerDimensionalStabilizer;
import mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiDimensionalStabilizer extends GuiMekanismTile<TileEntityDimensionalStabilizer> {

    public GuiDimensionalStabilizer(InventoryPlayer inventory, TileEntityDimensionalStabilizer tile) {
        super(tile, new ContainerDimensionalStabilizer(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiPowerBar(this, tile, resource, 164, 15));
        addGuiElement(new GuiEnergyInfo(() -> java.util.Arrays.asList(
              LangUtils.localize("gui.using") + ": " + MekanismUtils.getEnergyDisplay(tile.clientEnergyUsed) + "/t",
              LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tile.getMaxEnergy() - tile.getEnergy())),
              this, resource));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 142, 34).with(SlotOverlay.POWER));
    }

    @Override
    public void initGui() {
        super.initGui();
        for (int x = 0; x < TileEntityDimensionalStabilizer.DIAMETER; x++) {
            for (int z = 0; z < TileEntityDimensionalStabilizer.DIAMETER; z++) {
                int id = x * TileEntityDimensionalStabilizer.DIAMETER + z;
                GuiButton button = new GuiButton(id, guiLeft + 61 + x * 11, guiTop + 18 + z * 11,
                      11, 11, tileEntity.isChunkLoadingAt(x, z) ? "+" : "-");
                button.enabled = x != TileEntityDimensionalStabilizer.MAX_LOAD_RADIUS
                      || z != TileEntityDimensionalStabilizer.MAX_LOAD_RADIUS;
                buttonList.add(button);
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        for (GuiButton button : buttonList) {
            if (button.id >= 0 && button.id < TileEntityDimensionalStabilizer.DIAMETER
                  * TileEntityDimensionalStabilizer.DIAMETER) {
                int x = button.id / TileEntityDimensionalStabilizer.DIAMETER;
                int z = button.id % TileEntityDimensionalStabilizer.DIAMETER;
                button.displayString = tileEntity.isChunkLoadingAt(x, z) ? "+" : "-";
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id >= 0 && button.id < TileEntityDimensionalStabilizer.DIAMETER
              * TileEntityDimensionalStabilizer.DIAMETER) {
            int x = button.id / TileEntityDimensionalStabilizer.DIAMETER;
            int z = button.id % TileEntityDimensionalStabilizer.DIAMETER;
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity,
                  TileNetworkList.withContents(0, x, z)));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), 8, 6, 0x404040);
        fontRenderer.drawString("N", 86, 12, 0x404040);
        fontRenderer.drawString(tileEntity.getChunksLoaded() + " chunks", 66, 76, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");
    }
}
