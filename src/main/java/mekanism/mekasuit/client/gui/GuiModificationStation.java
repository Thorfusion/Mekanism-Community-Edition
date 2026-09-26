package mekanism.mekasuit.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import mekanism.mekasuit.common.inventory.ContainerModificationStation;
import mekanism.mekasuit.common.network.PacketModificationStationAction;
import mekanism.mekasuit.common.network.PacketModificationStationAction.Action;
import mekanism.mekasuit.common.tile.TileEntityModificationStation;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiModificationStation extends GuiMekanismTile<TileEntityModificationStation> {

    private static final int PREVIOUS = 0;
    private static final int NEXT = 1;
    private static final int REMOVE = 2;
    private static final int TOGGLE = 3;
    private static final int MODE = 4;
    private static final int BOOLEAN_CONFIG = 5;

    private int selectedIndex;
    private int booleanConfigIndex;
    private GuiButton previousButton;
    private GuiButton nextButton;
    private GuiButton removeButton;
    private GuiButton toggleButton;
    private GuiButton modeButton;
    private GuiButton booleanConfigButton;

    public GuiModificationStation(InventoryPlayer inventory, TileEntityModificationStation tile) {
        super(tile, new ContainerModificationStation(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164, 15));
        addGuiElement(new GuiEnergyInfo(() -> java.util.Arrays.asList(
              LangUtils.localize("gui.using") + ": "
                    + MekanismUtils.getEnergyDisplay(tileEntity.clientEnergyUsed) + "/t",
              LangUtils.localize("gui.needed") + ": "
                    + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy())),
              this, resource));
        addGuiElement(new GuiSlot(SlotType.INPUT, this, resource, 24, 34));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 64, 34));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 142, 34).with(SlotOverlay.POWER));
        addGuiElement(new GuiProgress(new GuiProgress.IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getScaledProgress();
            }
        }, ProgressBar.LARGE_RIGHT, this, resource, 96, 39));
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(previousButton = new GuiButton(PREVIOUS, guiLeft + 2, guiTop + 62, 18, 18, "<"));
        buttonList.add(nextButton = new GuiButton(NEXT, guiLeft + 21, guiTop + 62, 18, 18, ">"));
        buttonList.add(removeButton = new GuiButton(REMOVE, guiLeft + 40, guiTop + 62, 42, 18,
              LangUtils.localize("gui.mekasuit.remove")));
        buttonList.add(toggleButton = new GuiButton(TOGGLE, guiLeft + 83, guiTop + 62, 32, 18, ""));
        buttonList.add(modeButton = new GuiButton(MODE, guiLeft + 116, guiTop + 62, 28, 18, ""));
        buttonList.add(booleanConfigButton = new GuiButton(BOOLEAN_CONFIG, guiLeft + 145, guiTop + 62, 29, 18, ""));
        updateButtons();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        updateButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        List<ModuleData> modules = installedModules();
        if (button.id == PREVIOUS && !modules.isEmpty()) {
            selectedIndex = (selectedIndex + modules.size() - 1) % modules.size();
            booleanConfigIndex = 0;
        } else if (button.id == NEXT && !modules.isEmpty()) {
            selectedIndex = (selectedIndex + 1) % modules.size();
            booleanConfigIndex = 0;
        } else {
            ModuleData selected = selected(modules);
            if (selected == null) {
                return;
            }
            if (button.id == REMOVE) {
                MekanismMekaSuit.network.sendToServer(new PacketModificationStationAction(
                      isShiftKeyDown() ? Action.REMOVE_ALL : Action.REMOVE_ONE, selected.getType().getId()));
            } else if (button.id == TOGGLE && selected.getType().canDisable()) {
                MekanismMekaSuit.network.sendToServer(new PacketModificationStationAction(
                      Action.TOGGLE_ENABLED, selected.getType().getId()));
            } else if (button.id == MODE) {
                if (selected.getType().hasModes()) {
                    String mode = selected.getType().cycleMode(selected.getMode(), selected.getInstalledCount(),
                          isShiftKeyDown() ? -1 : 1);
                    MekanismMekaSuit.network.sendToServer(new PacketModificationStationAction(
                          Action.SET_MODE, selected.getType().getId(), mode, false));
                } else if (selected.getType().getBooleanConfigKeys().size() > 1) {
                    int size = selected.getType().getBooleanConfigKeys().size();
                    booleanConfigIndex = Math.floorMod(booleanConfigIndex + (isShiftKeyDown() ? -1 : 1), size);
                }
            } else if (button.id == BOOLEAN_CONFIG && selected.getType().hasBooleanConfigs()) {
                String key = selectedBooleanConfig(selected);
                MekanismMekaSuit.network.sendToServer(new PacketModificationStationAction(
                      Action.SET_BOOLEAN_CONFIG, selected.getType().getId(), key,
                      !selected.getBooleanConfig(key)));
            }
        }
        updateButtons();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        renderScaledText(tileEntity.getName(), 45, 6, 0x404040, 115);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        ModuleData selected = selected(installedModules());
        String label = selected == null ? LangUtils.localize("gui.mekasuit.noModules")
              : LangUtils.localize(selected.getType().getTranslationKey()) + " x" + selected.getInstalledCount();
        if (selected != null && selected.getType().hasModes()) {
            label += " [" + LangUtils.localize("module.mode." + selected.getMode()) + "]";
        }
        if (selected != null && selected.getType().hasBooleanConfigs()) {
            String key = selectedBooleanConfig(selected);
            label += " {" + LangUtils.localize("module.config." + key) + ": "
                  + LangUtils.localize(selected.getBooleanConfig(key) ? "gui.mekasuit.on" : "gui.mekasuit.off") + "}";
        }
        renderScaledText(label, 8, 52, 0x404040, 154);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    private void updateButtons() {
        if (previousButton == null) {
            return;
        }
        List<ModuleData> modules = installedModules();
        if (selectedIndex >= modules.size()) {
            selectedIndex = Math.max(0, modules.size() - 1);
        }
        ModuleData selected = selected(modules);
        previousButton.enabled = modules.size() > 1;
        nextButton.enabled = modules.size() > 1;
        removeButton.enabled = selected != null;
        toggleButton.enabled = selected != null && selected.getType().canDisable();
        toggleButton.displayString = selected != null && selected.isEnabled()
              ? LangUtils.localize("gui.mekasuit.on") : LangUtils.localize("gui.mekasuit.off");
        boolean multipleBooleanConfigs = selected != null
              && selected.getType().getBooleanConfigKeys().size() > 1;
        modeButton.visible = selected != null && (selected.getType().hasModes() || multipleBooleanConfigs);
        modeButton.enabled = modeButton.visible;
        modeButton.displayString = selected != null && selected.getType().hasModes()
              ? LangUtils.localize("gui.mekasuit.mode") : ">";
        booleanConfigButton.visible = selected != null && selected.getType().hasBooleanConfigs();
        booleanConfigButton.enabled = booleanConfigButton.visible;
        if (selected != null && selected.getType().hasBooleanConfigs()) {
            String key = selectedBooleanConfig(selected);
            booleanConfigButton.displayString = LangUtils.localize("module.config.short." + key)
                  + (selected.getBooleanConfig(key) ? "+" : "-");
        } else {
            booleanConfigButton.displayString = "";
        }
    }

    private List<ModuleData> installedModules() {
        ItemStack stack = tileEntity.getContainerStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof IModuleContainerItem)) {
            return java.util.Collections.emptyList();
        }
        IModuleContainerItem item = (IModuleContainerItem) stack.getItem();
        return new ArrayList<>(ModuleContainer.fromStack(stack, item.getModuleTarget()).getModules());
    }

    private ModuleData selected(List<ModuleData> modules) {
        return modules.isEmpty() ? null : modules.get(Math.min(selectedIndex, modules.size() - 1));
    }

    private String selectedBooleanConfig(ModuleData module) {
        List<String> keys = new ArrayList<>(module.getType().getBooleanConfigKeys());
        return keys.get(Math.floorMod(booleanConfigIndex, keys.size()));
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");
    }
}
