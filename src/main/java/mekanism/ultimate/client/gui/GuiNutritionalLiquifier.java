package mekanism.ultimate.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiFluidGauge;
import mekanism.client.gui.element.GuiFluidGauge.IFluidInfoHandler;
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
import mekanism.ultimate.common.inventory.ContainerNutritionalLiquifier;
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class GuiNutritionalLiquifier extends GuiMekanism
{
    private static final ResourceLocation RESOURCE =
          MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");

    public final TileEntityNutritionalLiquifier tileEntity;

    public GuiNutritionalLiquifier(InventoryPlayer inventory,
          TileEntityNutritionalLiquifier tile)
    {
        super(tile, new ContainerNutritionalLiquifier(inventory, tile));
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
                            + MekanismUtils.getEnergyDisplay(
                                  tileEntity.clientEnergyUsed) + "/t",
                      LangUtils.localize("gui.needed") + ": "
                            + MekanismUtils.getEnergyDisplay(
                                  tileEntity.getMaxEnergy()
                                        - tileEntity.getEnergy()));
            }
        }, this, RESOURCE));
        guiElements.add(new GuiFluidGauge(new IFluidInfoHandler()
        {
            @Override
            public FluidTank getTank()
            {
                return tileEntity.fluidTank;
            }
        }, GuiGauge.Type.STANDARD, this, RESOURCE, 78, 13));
        guiElements.add(new GuiSlot(SlotType.INPUT, this, RESOURCE, 26, 34));
        guiElements.add(new GuiSlot(SlotType.OUTPUT, this, RESOURCE, 107, 34));
        guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE, 134, 23)
              .with(SlotOverlay.PLUS));
        guiElements.add(new GuiSlot(SlotType.OUTPUT, this, RESOURCE, 134, 54));
        guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE, 154, 54)
              .with(SlotOverlay.POWER));
        guiElements.add(new GuiProgress(new IProgressInfoHandler()
        {
            @Override
            public double getProgress()
            {
                return tileEntity.getScaledProgress();
            }
        }, ProgressBar.LARGE_RIGHT, this, RESOURCE, 50, 39));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRendererObj.drawString(LangUtils.localize(
              "gui.NutritionalLiquifier.short"), 45, 4, 0x404040);
        fontRendererObj.drawString(LangUtils.localize("container.inventory"),
              8, ySize - 92, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick,
          int mouseX, int mouseY)
    {
        mc.renderEngine.bindTexture(RESOURCE);
        GL11.glColor4f(1, 1, 1, 1);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    }
}
