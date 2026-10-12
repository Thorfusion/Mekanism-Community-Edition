package mekanism.nuclear.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.api.gas.GasTank;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiGasGauge;
import mekanism.client.gui.element.GuiGasGauge.IGasInfoHandler;
import mekanism.client.gui.element.GuiGauge;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSecurityTab;
import mekanism.client.gui.element.GuiSideConfigurationTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.GuiUpgradeTab;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.nuclear.common.inventory.ContainerIsotopicCentrifuge;
import mekanism.nuclear.common.tile.TileEntityIsotopicCentrifuge;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiIsotopicCentrifuge extends GuiMekanism
{
    private static final ResourceLocation RESOURCE =
          MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");

    public final TileEntityIsotopicCentrifuge tileEntity;

    public GuiIsotopicCentrifuge(InventoryPlayer inventory,
          TileEntityIsotopicCentrifuge tile)
    {
        super(tile, new ContainerIsotopicCentrifuge(inventory, tile));
        tileEntity = tile;
        guiElements.add(new GuiSecurityTab(this, tile, RESOURCE));
        guiElements.add(new GuiSideConfigurationTab(this, tile, RESOURCE));
        guiElements.add(new GuiRedstoneControl(this, tile, RESOURCE));
        guiElements.add(new GuiUpgradeTab(this, tile, RESOURCE));
        guiElements.add(new GuiEnergyInfo(new IInfoHandler()
        {
            @Override
            public List<String> getInfo()
            {
                return ListUtils.asList(
                      LangUtils.localize("gui.using") + ": "
                            + MekanismUtils.getEnergyDisplay(tileEntity.clientEnergyUsed) + "/t",
                      LangUtils.localize("gui.needed") + ": "
                            + MekanismUtils.getEnergyDisplay(
                                  tileEntity.getMaxEnergy() - tileEntity.getEnergy()));
            }
        }, this, RESOURCE));
        guiElements.add(new GuiGasGauge(new IGasInfoHandler()
        {
            @Override
            public GasTank getTank()
            {
                return tileEntity.inputTank;
            }
        }, GuiGauge.Type.STANDARD, this, RESOURCE, 25, 13));
        guiElements.add(new GuiGasGauge(new IGasInfoHandler()
        {
            @Override
            public GasTank getTank()
            {
                return tileEntity.outputTank;
            }
        }, GuiGauge.Type.STANDARD, this, RESOURCE, 133, 13));
        guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE, 4, 55)
              .with(SlotOverlay.MINUS));
        guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE, 154, 55)
              .with(SlotOverlay.PLUS));
        guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE, 154, 13)
              .with(SlotOverlay.POWER));
        guiElements.add(new GuiProgress(new IProgressInfoHandler()
        {
            @Override
            public double getProgress()
            {
                return tileEntity.getProgress();
            }
        }, ProgressBar.LARGE_RIGHT, this, RESOURCE, 62, 38));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRendererObj.drawString(tileEntity.getInventoryName(), 26, 4, 0x404040);
        fontRendererObj.drawString(LangUtils.localize("container.inventory"),
              8, ySize - 92, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
    {
        mc.renderEngine.bindTexture(RESOURCE);
        GL11.glColor4f(1, 1, 1, 1);
        int guiWidth = (width - xSize) / 2;
        int guiHeight = (height - ySize) / 2;
        drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    }
}
