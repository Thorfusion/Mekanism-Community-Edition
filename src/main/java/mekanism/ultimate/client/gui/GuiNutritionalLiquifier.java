package mekanism.ultimate.client.gui;

import java.util.Arrays;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiSideConfigurationTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.ultimate.common.inventory.ContainerNutritionalLiquifier;
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiNutritionalLiquifier extends GuiMekanismTile<TileEntityNutritionalLiquifier> {

    public GuiNutritionalLiquifier(InventoryPlayer inventory, TileEntityNutritionalLiquifier tile) {
        super(tile, new ContainerNutritionalLiquifier(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource));
        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource));
        addGuiElement(new GuiUpgradeTab(this, tileEntity, resource));
        addGuiElement(new GuiEnergyInfo(() -> Arrays.asList(
              LangUtils.localize("gui.using") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.clientEnergyUsed) + "/t",
              LangUtils.localize("gui.needed") + ": "
                    + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy())), this, resource));
        addGuiElement(new GuiFluidGauge(() -> tileEntity.fluidTank,
              GuiGauge.Type.STANDARD, this, resource, 78, 13));
        addGuiElement(new GuiSlot(SlotType.INPUT, this, resource, 26, 34));
        addGuiElement(new GuiSlot(SlotType.OUTPUT, this, resource, 107, 34));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 134, 23).with(SlotOverlay.PLUS));
        addGuiElement(new GuiSlot(SlotType.OUTPUT, this, resource, 134, 54));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 154, 54).with(SlotOverlay.POWER));
        addGuiElement(new GuiProgress(new GuiProgress.IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getScaledProgress();
            }
        }, ProgressBar.LARGE_RIGHT, this, resource, 50, 39));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(LangUtils.localize("gui.NutritionalLiquifier.short"), 45, 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");
    }
}
