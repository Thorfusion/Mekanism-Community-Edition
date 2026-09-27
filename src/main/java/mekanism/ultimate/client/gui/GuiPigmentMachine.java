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
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.ultimate.common.inventory.ContainerPigmentMachine;
import mekanism.ultimate.client.gui.element.GuiChemicalGauge;
import mekanism.ultimate.common.tile.TileEntityPaintingMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentExtractor;
import mekanism.ultimate.common.tile.TileEntityPigmentMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentMixer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiPigmentMachine extends GuiMekanismTile<TileEntityPigmentMachine> {

    public GuiPigmentMachine(InventoryPlayer inventory, TileEntityPigmentMachine tile) {
        super(tile, new ContainerPigmentMachine(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource));
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource));
        addGuiElement(new GuiUpgradeTab(this, tileEntity, resource));
        addGuiElement(new GuiEnergyInfo(() -> Arrays.asList(
              LangUtils.localize("gui.using") + ": "
                    + MekanismUtils.getEnergyDisplay(tileEntity.clientEnergyUsed) + "/t",
              LangUtils.localize("gui.needed") + ": "
                    + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy())),
              this, resource));
        if (tile instanceof TileEntityPigmentExtractor) {
            TileEntityPigmentExtractor extractor = (TileEntityPigmentExtractor) tile;
            addGuiElement(new GuiSlot(SlotType.INPUT, this, resource, 26, 35));
            addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 152, 55).with(SlotOverlay.PLUS));
            addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 152, 14).with(SlotOverlay.POWER));
            addGuiElement(new GuiChemicalGauge(() -> extractor.outputTank,
                  mekanism.client.gui.element.gauge.GuiGauge.Type.STANDARD,
                  this, resource, 131, 13));
            addProgress(resource, () -> ((TileEntityPigmentExtractor) tile).getProgress(
                  TileEntityPigmentExtractor.BASE_TICKS));
        } else if (tile instanceof TileEntityPigmentMixer) {
            TileEntityPigmentMixer mixer = (TileEntityPigmentMixer) tile;
            addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 6, 56).with(SlotOverlay.MINUS));
            addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 154, 56).with(SlotOverlay.MINUS));
            addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 80, 65).with(SlotOverlay.PLUS));
            addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 154, 14).with(SlotOverlay.POWER));
            addGuiElement(new GuiChemicalGauge(() -> mixer.leftTank,
                  mekanism.client.gui.element.gauge.GuiGauge.Type.STANDARD,
                  this, resource, 25, 13));
            addGuiElement(new GuiChemicalGauge(() -> mixer.outputTank,
                  mekanism.client.gui.element.gauge.GuiGauge.Type.STANDARD,
                  this, resource, 79, 4));
            addGuiElement(new GuiChemicalGauge(() -> mixer.rightTank,
                  mekanism.client.gui.element.gauge.GuiGauge.Type.STANDARD,
                  this, resource, 133, 13));
            addMixerProgress(resource, mixer, GuiProgress.ProgressBar.SMALL_RIGHT, 47);
            addMixerProgress(resource, mixer, GuiProgress.ProgressBar.SMALL_LEFT, 101);
        } else if (tile instanceof TileEntityPaintingMachine) {
            TileEntityPaintingMachine painting = (TileEntityPaintingMachine) tile;
            addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 6, 56).with(SlotOverlay.MINUS));
            addGuiElement(new GuiSlot(SlotType.INPUT, this, resource, 45, 35));
            addGuiElement(new GuiSlot(SlotType.OUTPUT, this, resource, 116, 35));
            addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 144, 35).with(SlotOverlay.POWER));
            addGuiElement(new GuiChemicalGauge(() -> painting.pigmentTank,
                  mekanism.client.gui.element.gauge.GuiGauge.Type.STANDARD,
                  this, resource, 25, 13));
            addProgress(resource, () -> ((TileEntityPaintingMachine) tile).getProgress(
                  TileEntityPaintingMachine.BASE_TICKS));
        }
    }

    private void addProgress(ResourceLocation resource, ProgressSupplier supplier) {
        addGuiElement(new GuiProgress(new GuiProgress.IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return supplier.get();
            }
        }, ProgressBar.LARGE_RIGHT, this, resource, 64, 40));
    }

    private void addMixerProgress(ResourceLocation resource, TileEntityPigmentMixer mixer,
          ProgressBar bar, int x) {
        addGuiElement(new GuiProgress(new GuiProgress.IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return 1;
            }

            @Override
            public boolean isActive() {
                return mixer.getActive();
            }
        }, bar, this, resource, x, 39));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), 28, 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");
    }

    private interface ProgressSupplier {
        double get();
    }
}
