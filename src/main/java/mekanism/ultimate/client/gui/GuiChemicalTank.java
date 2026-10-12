package mekanism.ultimate.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import mekanism.api.Coord4D;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSecurityTab;
import mekanism.client.gui.element.GuiSideConfigurationTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.GuiTransporterConfigTab;
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
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class GuiChemicalTank extends GuiMekanism
{
    private final TileEntityChemicalTank tileEntity;

    public GuiChemicalTank(InventoryPlayer inventory,
          TileEntityChemicalTank tileEntity)
    {
        super(tileEntity, new ContainerChemicalTank(inventory, tileEntity));
        this.tileEntity = tileEntity;
        guiElements.add(new GuiRedstoneControl(this, tileEntity,
              MekanismUtils.getResource(ResourceType.GUI,
                    "GuiGasTank.png")));
        guiElements.add(new GuiSecurityTab(this, tileEntity,
              MekanismUtils.getResource(ResourceType.GUI,
                    "GuiGasTank.png")));
        guiElements.add(new GuiSideConfigurationTab(this, tileEntity,
              MekanismUtils.getResource(ResourceType.GUI,
                    "GuiGasTank.png")));
        guiElements.add(new GuiTransporterConfigTab(this, 34, tileEntity,
              MekanismUtils.getResource(ResourceType.GUI,
                    "GuiGasTank.png")));
        guiElements.add(new GuiSlot(SlotType.OUTPUT, this,
              MekanismUtils.getResource(ResourceType.GUI,
                    "GuiGasTank.png"), 7, 7).with(SlotOverlay.PLUS));
        guiElements.add(new GuiSlot(SlotType.INPUT, this,
              MekanismUtils.getResource(ResourceType.GUI,
                    "GuiGasTank.png"), 7, 39).with(SlotOverlay.MINUS));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        IChemicalStackCE stored = tileEntity.chemicalTank.getStack();
        String amount = tileEntity.tier.isCreative() && stored != null
              ? LangUtils.localize("gui.infinite")
              : Long.toString(tileEntity.chemicalTank.getStored());
        String capacity = tileEntity.tier.isCreative()
              ? LangUtils.localize("gui.infinite")
              : Long.toString(tileEntity.chemicalTank.getCapacity());
        String chemical = stored == null ? LangUtils.localize("gui.none")
              : getChemicalName(stored);

        fontRendererObj.drawString(tileEntity.getInventoryName(),
              (xSize - fontRendererObj.getStringWidth(
                    tileEntity.getInventoryName())) / 2, 6, 0x404040);
        fontRendererObj.drawString(amount + " / " + capacity,
              45, 40, 0x404040);
        renderScaledText(LangUtils.localize("gui.chemical") + ": "
              + chemical, 45, 49, 0x404040, 112);
        fontRendererObj.drawString(LangUtils.localize("container.inventory"),
              8, ySize - 94, 0x404040);

        String mode = tileEntity.dumping == TileEntityChemicalTank.GasMode.DUMPING
              ? LangUtils.localize("gui.dumping")
              : tileEntity.dumping == TileEntityChemicalTank.GasMode.DUMPING_EXCESS
                    ? LangUtils.localize("gui.dumping_excess")
                    : LangUtils.localize("gui.idle");
        fontRendererObj.drawString(mode,
              156 - fontRendererObj.getStringWidth(mode), 73, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick,
          int mouseX, int mouseY)
    {
        mc.renderEngine.bindTexture(MekanismUtils.getResource(
              ResourceType.GUI, "GuiGasTank.png"));
        GL11.glColor4f(1F, 1F, 1F, 1F);
        int guiWidth = (width - xSize) / 2;
        int guiHeight = (height - ySize) / 2;
        drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

        int display = tileEntity.dumping == TileEntityChemicalTank.GasMode.DUMPING
              ? 18 : tileEntity.dumping
                    == TileEntityChemicalTank.GasMode.DUMPING_EXCESS
                          ? 26 : 10;
        drawTexturedModalRect(guiWidth + 160, guiHeight + 73,
              176, display, 8, 8);
        if (tileEntity.chemicalTank.getStack() != null)
        {
            int scale = (int)Math.min(72D,
                  72D * tileEntity.chemicalTank.getStored()
                        / tileEntity.chemicalTank.getCapacity());
            drawTexturedModalRect(guiWidth + 65, guiHeight + 17,
                  176, 0, scale, 10);
        }
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        super.mouseClicked(x, y, button);
        int xAxis = x - (width - xSize) / 2;
        int yAxis = y - (height - ySize) / 2;
        if (xAxis > 160 && xAxis < 169 && yAxis > 73 && yAxis < 82)
        {
            ArrayList data = new ArrayList();
            data.add(0);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(
                  Coord4D.get(tileEntity), data));
            SoundHandler.playSound("gui.button.press");
        }
    }

    private static String getChemicalName(IChemicalStackCE stored)
    {
        if (stored.getType() instanceof MekGasChemicalType)
        {
            return ((MekGasChemicalType)stored.getType()).getGas()
                  .getLocalizedName();
        }
        String name = stored.getType().getRegistryName();
        int separator = name.indexOf(':');
        String path = separator >= 0 ? name.substring(separator + 1) : name;
        String key = "chemical."
              + stored.getType().getKind().name().toLowerCase() + "." + path;
        String localized = LangUtils.localize(key);
        return localized.equals(key) ? name : localized;
    }
}
