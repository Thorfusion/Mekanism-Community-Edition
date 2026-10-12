package mekanism.ultimate.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import java.util.List;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSecurityTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.inventory.ContainerDimensionalStabilizer;
import mekanism.ultimate.common.network.PacketDimensionalStabilizer;
import mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class GuiDimensionalStabilizer extends GuiMekanism
{
    private static final ResourceLocation TEXTURE = MekanismUtils.getResource(
          ResourceType.GUI, "GuiBlank.png");
    private final TileEntityDimensionalStabilizer tile;

    public GuiDimensionalStabilizer(InventoryPlayer inventory,
          TileEntityDimensionalStabilizer tile)
    {
        super(tile, new ContainerDimensionalStabilizer(inventory, tile));
        this.tile = tile;
        guiElements.add(new GuiPowerBar(this, tile, TEXTURE, 164, 15));
        guiElements.add(new GuiSlot(SlotType.POWER, this, TEXTURE,
              142, 34).with(SlotOverlay.POWER));
        guiElements.add(new GuiSlot(SlotType.NORMAL, this, TEXTURE,
              142, 58));
        guiElements.add(new GuiSecurityTab(this, tile, TEXTURE));
        guiElements.add(new GuiRedstoneControl(this, tile, TEXTURE));
        guiElements.add(new GuiEnergyInfo(new IInfoHandler()
        {
            @Override
            public List<String> getInfo()
            {
                return Arrays.asList(
                      LangUtils.localize("gui.using") + ": "
                            + MekanismUtils.getEnergyDisplay(
                                  GuiDimensionalStabilizer.this.tile
                                        .clientEnergyUsed) + "/t",
                      LangUtils.localize("gui.needed") + ": "
                            + MekanismUtils.getEnergyDisplay(
                                  GuiDimensionalStabilizer.this.tile
                                        .getMaxEnergy()
                                        - GuiDimensionalStabilizer.this.tile
                                              .getEnergy()));
            }
        }, this, TEXTURE));
    }

    @Override
    public void initGui()
    {
        super.initGui();
        for (int x = 0; x < TileEntityDimensionalStabilizer.DIAMETER; x++)
        {
            for (int z = 0; z < TileEntityDimensionalStabilizer.DIAMETER;
                  z++)
            {
                int id = x * TileEntityDimensionalStabilizer.DIAMETER + z;
                GuiButton button = new GuiButton(id,
                      guiLeft + 61 + x * 11, guiTop + 18 + z * 11,
                      11, 11, tile.isChunkLoadingAt(x, z) ? "+" : "-");
                button.enabled = x
                      != TileEntityDimensionalStabilizer.MAX_LOAD_RADIUS
                      || z != TileEntityDimensionalStabilizer.MAX_LOAD_RADIUS;
                buttonList.add(button);
            }
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        for (Object entry : buttonList)
        {
            GuiButton button = (GuiButton)entry;
            if (button.id >= 0 && button.id < 25)
            {
                int x = button.id
                      / TileEntityDimensionalStabilizer.DIAMETER;
                int z = button.id
                      % TileEntityDimensionalStabilizer.DIAMETER;
                button.displayString = tile.isChunkLoadingAt(x, z)
                      ? "+" : "-";
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id >= 0 && button.id < 25)
        {
            int x = button.id / TileEntityDimensionalStabilizer.DIAMETER;
            int z = button.id % TileEntityDimensionalStabilizer.DIAMETER;
            MekanismUltimate.network.sendToServer(
                  new PacketDimensionalStabilizer(x, z));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRendererObj.drawString(tile.getInventoryName(), 8, 6,
              0x404040);
        fontRendererObj.drawString("N", 86, 10, 0x404040);
        fontRendererObj.drawString(tile.getChunksLoaded() + " "
              + LangUtils.localize("gui.chunks"), 64, 75, 0x404040);
        fontRendererObj.drawString(LangUtils.localize("container.inventory"),
              8, ySize - 92, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick,
          int mouseX, int mouseY)
    {
        mc.renderEngine.bindTexture(TEXTURE);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
    }
}
