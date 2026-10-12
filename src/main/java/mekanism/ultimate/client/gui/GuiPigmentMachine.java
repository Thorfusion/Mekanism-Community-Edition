package mekanism.ultimate.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiGauge;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSecurityTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.GuiUpgradeTab;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.ultimate.client.gui.element.GuiChemicalGauge;
import mekanism.ultimate.client.gui.element.GuiChemicalGauge.IChemicalInfoHandler;
import mekanism.ultimate.common.inventory.ContainerPigmentMachine;
import mekanism.ultimate.common.tile.TileEntityPaintingMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentExtractor;
import mekanism.ultimate.common.tile.TileEntityPigmentMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentMixer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Shared source-layout GUI for extracting, mixing, and painting pigments. */
@SideOnly(Side.CLIENT)
public final class GuiPigmentMachine extends GuiMekanism
{
    private static final ResourceLocation RESOURCE =
          MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");

    public final TileEntityPigmentMachine tileEntity;

    public GuiPigmentMachine(InventoryPlayer inventory,
          TileEntityPigmentMachine tile)
    {
        super(tile, new ContainerPigmentMachine(inventory, tile));
        tileEntity = tile;
        guiElements.add(new GuiSecurityTab(this, tile, RESOURCE));
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

        if (tile instanceof TileEntityPigmentExtractor)
        {
            final TileEntityPigmentExtractor extractor =
                  (TileEntityPigmentExtractor)tile;
            guiElements.add(new GuiSlot(SlotType.INPUT, this, RESOURCE,
                  26, 35));
            guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE,
                  152, 55).with(SlotOverlay.PLUS));
            guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE,
                  152, 14).with(SlotOverlay.POWER));
            guiElements.add(new GuiChemicalGauge(
                  new IChemicalInfoHandler()
                  {
                      @Override
                      public mekanism.ultimate.api.chemical.IChemicalTankCE getTank()
                      {
                          return extractor.outputTank;
                      }
                  }, GuiGauge.Type.STANDARD, this, RESOURCE, 131, 13));
            guiElements.add(progress(new ProgressSupplier()
            {
                @Override
                public double get()
                {
                    return extractor.getProgress(
                          TileEntityPigmentExtractor.BASE_TICKS);
                }
            }, ProgressBar.LARGE_RIGHT, 64, 40));
        }
        else if (tile instanceof TileEntityPigmentMixer)
        {
            final TileEntityPigmentMixer mixer =
                  (TileEntityPigmentMixer)tile;
            guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE,
                  6, 56).with(SlotOverlay.MINUS));
            guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE,
                  154, 56).with(SlotOverlay.MINUS));
            guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE,
                  80, 65).with(SlotOverlay.PLUS));
            guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE,
                  154, 14).with(SlotOverlay.POWER));
            guiElements.add(gauge(mixer.leftTank, 25, 13));
            guiElements.add(gauge(mixer.outputTank, 79, 4));
            guiElements.add(gauge(mixer.rightTank, 133, 13));
            guiElements.add(activeProgress(mixer,
                  ProgressBar.SMALL_RIGHT, 47, 39));
            guiElements.add(activeProgress(mixer,
                  ProgressBar.SMALL_LEFT, 101, 39));
        }
        else if (tile instanceof TileEntityPaintingMachine)
        {
            final TileEntityPaintingMachine painting =
                  (TileEntityPaintingMachine)tile;
            guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE,
                  6, 56).with(SlotOverlay.MINUS));
            guiElements.add(new GuiSlot(SlotType.INPUT, this, RESOURCE,
                  45, 35));
            guiElements.add(new GuiSlot(SlotType.OUTPUT, this, RESOURCE,
                  116, 35));
            guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE,
                  144, 35).with(SlotOverlay.POWER));
            guiElements.add(gauge(painting.pigmentTank, 25, 13));
            guiElements.add(progress(new ProgressSupplier()
            {
                @Override
                public double get()
                {
                    return painting.getProgress(
                          TileEntityPaintingMachine.BASE_TICKS);
                }
            }, ProgressBar.LARGE_RIGHT, 64, 40));
        }
    }

    private GuiChemicalGauge gauge(
          final mekanism.ultimate.api.chemical.IChemicalTankCE tank,
          int x, int y)
    {
        return new GuiChemicalGauge(new IChemicalInfoHandler()
        {
            @Override
            public mekanism.ultimate.api.chemical.IChemicalTankCE getTank()
            {
                return tank;
            }
        }, GuiGauge.Type.STANDARD, this, RESOURCE, x, y);
    }

    private GuiProgress progress(final ProgressSupplier supplier,
          ProgressBar bar, int x, int y)
    {
        return new GuiProgress(new IProgressInfoHandler()
        {
            @Override
            public double getProgress()
            {
                return supplier.get();
            }
        }, bar, this, RESOURCE, x, y);
    }

    private GuiProgress activeProgress(final TileEntityPigmentMixer mixer,
          ProgressBar bar, int x, int y)
    {
        return new GuiProgress(new IProgressInfoHandler()
        {
            @Override
            public double getProgress()
            {
                return 1;
            }

            @Override
            public boolean isActive()
            {
                return mixer.getActive();
            }
        }, bar, this, RESOURCE, x, y);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String name = tileEntity.getInventoryName();
        fontRendererObj.drawString(name,
              (xSize - fontRendererObj.getStringWidth(name)) / 2,
              4, 0x404040);
        fontRendererObj.drawString(LangUtils.localize(
              "container.inventory"), 8, ySize - 92, 0x404040);
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

    private interface ProgressSupplier
    {
        double get();
    }
}
