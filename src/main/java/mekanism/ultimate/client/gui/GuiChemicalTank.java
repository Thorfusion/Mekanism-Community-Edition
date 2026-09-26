package mekanism.ultimate.client.gui;

import java.io.IOException;
import mekanism.api.TileNetworkList;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiSideConfigurationTab;
import mekanism.client.gui.element.tab.GuiTransporterConfigTab;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.inventory.ContainerChemicalTank;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiChemicalTank extends GuiMekanismTile<TileEntityChemicalTank> {

    public GuiChemicalTank(InventoryPlayer inventory, TileEntityChemicalTank tile) {
        super(tile, new ContainerChemicalTank(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource));
        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiTransporterConfigTab(this, 34, tileEntity, resource));
        addGuiElement(new GuiSlot(SlotType.OUTPUT, this, resource, 7, 7).with(SlotOverlay.PLUS));
        addGuiElement(new GuiSlot(SlotType.INPUT, this, resource, 7, 39).with(SlotOverlay.MINUS));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        IChemicalStackCE stored = tileEntity.chemicalTank.getStack();
        String amount = tileEntity.tier.isCreative() && stored != null
              ? LangUtils.localize("gui.infinite") : Long.toString(tileEntity.chemicalTank.getStored());
        String capacity = tileEntity.tier.isCreative() ? LangUtils.localize("gui.infinite")
              : Long.toString(tileEntity.chemicalTank.getCapacity());
        String chemical = LangUtils.localize("gui.none");
        if (stored != null) {
            chemical = stored.getType() instanceof MekGasChemicalType
                  ? ((MekGasChemicalType) stored.getType()).getGas().getLocalizedName()
                  : stored.getType().getRegistryName();
        }
        fontRenderer.drawString(tileEntity.getName(),
              (xSize - fontRenderer.getStringWidth(tileEntity.getName())) / 2, 6, 0x404040);
        fontRenderer.drawString(amount + " / " + capacity, 45, 40, 0x404040);
        renderScaledText(LangUtils.localize("gui.chemical") + ": " + chemical,
              45, 49, 0x404040, 112);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, ySize - 94, 0x404040);
        String mode = LangUtils.localize(tileEntity.dumping.getLangKey());
        fontRenderer.drawString(mode, 156 - fontRenderer.getStringWidth(mode), 73, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(mouseX, mouseY);
        int display = TileEntityChemicalTank.GasMode.choose(tileEntity.dumping, 10, 26, 18);
        drawTexturedModalRect(guiLeft + 160, guiTop + 73, 176, display, 8, 8);
        if (tileEntity.chemicalTank.getStack() != null) {
            int scale = (int) Math.min(72D, 72D * tileEntity.chemicalTank.getStored()
                  / tileEntity.chemicalTank.getCapacity());
            drawTexturedModalRect(guiLeft + 65, guiTop + 17, 176, 0, scale, 10);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;
        if (x > 160 && x < 169 && y > 73 && y < 82) {
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity,
                  TileNetworkList.withContents(0)));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiGasTank.png");
    }
}
