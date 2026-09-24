package mekanism.nuclear.client.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import mekanism.client.gui.GuiMekanism;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.content.fission.FissionReactorStatus;
import mekanism.nuclear.common.inventory.ContainerFissionReactor;
import mekanism.nuclear.common.network.PacketFissionReactorControl;
import mekanism.nuclear.common.network.PacketFissionReactorControl.Action;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Compact 1.12-native controller and stats screen for a formed Fission Reactor. */
@SideOnly(Side.CLIENT)
public class GuiFissionReactor extends GuiMekanism {

    private static final int ACTIVATE = 0;
    private static final int SCRAM = 1;
    private static final int RATE_MINUS_ONE = 2;
    private static final int RATE_MINUS_TENTH = 3;
    private static final int RATE_PLUS_TENTH = 4;
    private static final int RATE_PLUS_ONE = 5;
    private static final int TOGGLE_STATS = 6;

    private final TileEntityFissionReactorPort tile;
    private GuiButton activateButton;
    private GuiButton scramButton;
    private boolean stats;

    public GuiFissionReactor(EntityPlayer player, TileEntityFissionReactorPort tile) {
        super(new ContainerFissionReactor(player, tile));
        this.tile = tile;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(activateButton = new GuiButton(ACTIVATE, guiLeft + 8, guiTop + 80, 78, 20,
              LangUtils.localize("fission.mekanismnuclear.gui.activate")));
        buttonList.add(scramButton = new GuiButton(SCRAM, guiLeft + 90, guiTop + 80, 78, 20,
              LangUtils.localize("fission.mekanismnuclear.gui.scram")));
        buttonList.add(new GuiButton(RATE_MINUS_ONE, guiLeft + 8, guiTop + 104, 38, 20, "-1"));
        buttonList.add(new GuiButton(RATE_MINUS_TENTH, guiLeft + 48, guiTop + 104, 38, 20, "-0.1"));
        buttonList.add(new GuiButton(RATE_PLUS_TENTH, guiLeft + 90, guiTop + 104, 38, 20, "+0.1"));
        buttonList.add(new GuiButton(RATE_PLUS_ONE, guiLeft + 130, guiTop + 104, 38, 20, "+1"));
        buttonList.add(new GuiButton(TOGGLE_STATS, guiLeft + 48, guiTop + 130, 80, 20,
              LangUtils.localize("fission.mekanismnuclear.gui.stats")));
        updateButtons();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        updateButtons();
    }

    private void updateButtons() {
        FissionReactorStatus status = tile.getStatus();
        if (activateButton != null) {
            activateButton.enabled = status.isFormed() && !status.isActive() && !status.isForceDisabled();
            scramButton.enabled = status.isFormed() && status.isActive();
            for (GuiButton button : buttonList) {
                if (button.id >= RATE_MINUS_ONE && button.id <= RATE_PLUS_ONE) {
                    button.enabled = status.isFormed();
                } else if (button.id == TOGGLE_STATS) {
                    button.displayString = LangUtils.localize(stats
                          ? "fission.mekanismnuclear.gui.status" : "fission.mekanismnuclear.gui.stats");
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        FissionReactorStatus status = tile.getStatus();
        switch (button.id) {
            case ACTIVATE:
                MekanismNuclear.network.sendToServer(new PacketFissionReactorControl(Action.ACTIVATE));
                break;
            case SCRAM:
                MekanismNuclear.network.sendToServer(new PacketFissionReactorControl(Action.SCRAM));
                break;
            case RATE_MINUS_ONE:
                setBurnRate(status.getBurnRate() - 1D);
                break;
            case RATE_MINUS_TENTH:
                setBurnRate(status.getBurnRate() - 0.1D);
                break;
            case RATE_PLUS_TENTH:
                setBurnRate(status.getBurnRate() + 0.1D);
                break;
            case RATE_PLUS_ONE:
                setBurnRate(status.getBurnRate() + 1D);
                break;
            case TOGGLE_STATS:
                stats = !stats;
                updateButtons();
                break;
            default:
                break;
        }
    }

    private void setBurnRate(double rate) {
        MekanismNuclear.network.sendToServer(new PacketFissionReactorControl(Action.SET_BURN_RATE,
              Math.max(0, rate)));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int mouseX, int mouseY) {
        FissionReactorStatus status = tile.getStatus();
        drawTank(8, 18, status.getFuel(), status.getFuelCapacity(), 0xFF459B45);
        drawTank(27, 18, status.getCoolant(), status.getCoolantCapacity(), 0xFF45AFC7);
        drawTank(141, 18, status.getHeatedCoolant(), status.getHeatedCoolantCapacity(), 0xFFD18446);
        drawTank(160, 18, status.getWaste(), status.getWasteCapacity(), 0xFF574632);
    }

    private void drawTank(int x, int y, long stored, long capacity, int color) {
        drawRect(x, y, x + 9, y + 53, 0xFF303030);
        drawRect(x + 1, y + 1, x + 8, y + 52, 0xFF101010);
        if (stored > 0 && capacity > 0) {
            int height = Math.max(1, (int) Math.min(51, 51D * stored / capacity));
            drawRect(x + 1, y + 52 - height, x + 8, y + 52, color);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        FissionReactorStatus status = tile.getStatus();
        renderCenteredText(0, xSize, 5, 0x404040,
              LangUtils.localize("tile.FissionReactorPort.name"));
        fontRenderer.drawString("F", 9, 10, 0x404040);
        fontRenderer.drawString("C", 28, 10, 0x404040);
        fontRenderer.drawString("H", 142, 10, 0x404040);
        fontRenderer.drawString("W", 161, 10, 0x404040);
        if (!status.isFormed()) {
            renderCenteredText(38, 100, 38, 0xAA2020,
                  LangUtils.localize("fission.mekanismnuclear.gui.unformed"));
        } else if (stats) {
            drawStats(status);
        } else {
            drawStatus(status);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    private void drawStatus(FissionReactorStatus status) {
        int x = 42;
        int y = 18;
        drawLine(x, y, LangUtils.localize("fission.mekanismnuclear.gui.state") + ": "
              + LangUtils.localize(status.isActive() ? "fission.mekanismnuclear.gui.active"
              : "fission.mekanismnuclear.gui.inactive"));
        drawLine(x, y + 10, LangUtils.localize("fission.mekanismnuclear.gui.burn") + ": "
              + decimal(status.getLastBurnRate()) + " / " + decimal(status.getBurnRate()));
        drawLine(x, y + 20, LangUtils.localize("fission.mekanismnuclear.gui.heating") + ": "
              + number(status.getLastBoilRate()) + " mB/t");
        drawLine(x, y + 30, LangUtils.localize("fission.mekanismnuclear.gui.temperature") + ": "
              + decimal(status.getTemperature()) + " K");
        drawLine(x, y + 40, LangUtils.localize("fission.mekanismnuclear.gui.damage") + ": "
              + decimal(status.getDamage()) + "%");
        drawLine(x, y + 50, LangUtils.localize("fission.mekanismnuclear.gui.rate") + ": 0-"
              + decimal(status.getMaxBurnRate()) + " mB/t");
    }

    private void drawStats(FissionReactorStatus status) {
        int x = 42;
        int y = 18;
        drawLine(x, y, status.getWidth() + " x " + status.getHeight() + " x " + status.getLength());
        drawLine(x, y + 10, LangUtils.localize("fission.mekanismnuclear.gui.assemblies") + ": "
              + status.getFuelAssemblies());
        drawLine(x, y + 20, LangUtils.localize("fission.mekanismnuclear.gui.rods") + ": "
              + status.getControlRods());
        drawLine(x, y + 30, LangUtils.localize("fission.mekanismnuclear.gui.efficiency") + ": "
              + decimal(status.getBoilEfficiency() * 100D) + "%");
        drawLine(x, y + 40, LangUtils.localize("fission.mekanismnuclear.gui.dissipation") + ": "
              + decimal(status.getEnvironmentLoss()) + " K/t");
        drawLine(x, y + 50, LangUtils.localize("fission.mekanismnuclear.gui.coolant") + ": "
              + coolantName(status));
    }

    private void drawLine(int x, int y, String text) {
        renderScaledText(text, x, y, 0x404040, 94);
    }

    private String coolantName(FissionReactorStatus status) {
        if (status.getCoolant() > 0) {
            return status.getCoolantType().name();
        }
        return status.getHeatedCoolantType().name();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        FissionReactorStatus status = tile.getStatus();
        if (isPointInRegion(8, 18, 9, 53, mouseX, mouseY)) {
            drawHoveringText(Arrays.asList(LangUtils.localize("gas.fissile_fuel"),
                  number(status.getFuel()) + " / " + number(status.getFuelCapacity()) + " mB"), mouseX, mouseY);
        } else if (isPointInRegion(27, 18, 9, 53, mouseX, mouseY)) {
            drawHoveringText(Arrays.asList(LangUtils.localize("fission.mekanismnuclear.gui.coolant"),
                  number(status.getCoolant()) + " / " + number(status.getCoolantCapacity()) + " mB"), mouseX, mouseY);
        } else if (isPointInRegion(141, 18, 9, 53, mouseX, mouseY)) {
            drawHoveringText(Arrays.asList(LangUtils.localize("fission.mekanismnuclear.gui.heated_coolant"),
                  number(status.getHeatedCoolant()) + " / " + number(status.getHeatedCoolantCapacity()) + " mB"), mouseX, mouseY);
        } else if (isPointInRegion(160, 18, 9, 53, mouseX, mouseY)) {
            drawHoveringText(Arrays.asList(LangUtils.localize("gas.nuclear_waste"),
                  number(status.getWaste()) + " / " + number(status.getWasteCapacity()) + " mB"), mouseX, mouseY);
        }
    }

    private static String decimal(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static String number(long value) {
        return String.format(Locale.ROOT, "%,d", value);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");
    }
}
