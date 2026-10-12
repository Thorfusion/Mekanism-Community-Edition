package mekanism.mekasuit.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
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
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class GuiModificationStation extends GuiMekanism
{
    private static final ResourceLocation RESOURCE =
          MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");
    private static final int PREVIOUS = 0;
    private static final int NEXT = 1;
    private static final int REMOVE = 2;
    private static final int TOGGLE = 3;
    private static final int MODE = 4;
    private static final int CONFIG = 5;

    private final TileEntityModificationStation tile;
    private int selectedIndex;
    private int booleanConfigIndex;
    private int enumConfigIndex;
    private GuiButton previousButton;
    private GuiButton nextButton;
    private GuiButton removeButton;
    private GuiButton toggleButton;
    private GuiButton modeButton;
    private GuiButton configButton;

    public GuiModificationStation(InventoryPlayer inventory,
          TileEntityModificationStation tile)
    {
        super(tile, new ContainerModificationStation(inventory, tile));
        this.tile = tile;
        guiElements.add(new GuiPowerBar(this, tile, RESOURCE, 164, 15));
        guiElements.add(new GuiEnergyInfo(new IInfoHandler()
        {
            @Override
            public List<String> getInfo()
            {
                return ListUtils.asList(
                      LangUtils.localize("gui.using") + ": "
                            + MekanismUtils.getEnergyDisplay(
                                  GuiModificationStation.this.tile.clientEnergyUsed)
                            + "/t",
                      LangUtils.localize("gui.needed") + ": "
                            + MekanismUtils.getEnergyDisplay(
                                  GuiModificationStation.this.tile.getMaxEnergy()
                                  - GuiModificationStation.this.tile.getEnergy()));
            }
        }, this, RESOURCE));
        guiElements.add(new GuiSlot(SlotType.INPUT, this, RESOURCE, 24, 34));
        guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE, 64, 34));
        guiElements.add(new GuiSlot(SlotType.NORMAL, this, RESOURCE, 142, 34)
              .with(SlotOverlay.POWER));
        guiElements.add(new GuiProgress(new IProgressInfoHandler()
        {
            @Override
            public double getProgress()
            {
                return GuiModificationStation.this.tile.getScaledProgress();
            }
        }, ProgressBar.LARGE_RIGHT, this, RESOURCE, 86, 39));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui()
    {
        super.initGui();
        buttonList.add(previousButton = new GuiButton(PREVIOUS,
              guiLeft + 2, guiTop + 62, 18, 18, "<"));
        buttonList.add(nextButton = new GuiButton(NEXT,
              guiLeft + 21, guiTop + 62, 18, 18, ">"));
        buttonList.add(removeButton = new GuiButton(REMOVE,
              guiLeft + 40, guiTop + 62, 42, 18,
              LangUtils.localize("gui.mekasuit.remove")));
        buttonList.add(toggleButton = new GuiButton(TOGGLE,
              guiLeft + 83, guiTop + 62, 32, 18, ""));
        buttonList.add(modeButton = new GuiButton(MODE,
              guiLeft + 116, guiTop + 62, 28, 18, ""));
        buttonList.add(configButton = new GuiButton(CONFIG,
              guiLeft + 145, guiTop + 62, 29, 18, ""));
        updateButtons();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        updateButtons();
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        List<ModuleData> modules = installedModules();
        if (button.id == PREVIOUS && !modules.isEmpty())
        {
            selectedIndex = Math.floorMod(selectedIndex - 1, modules.size());
            booleanConfigIndex = 0;
            enumConfigIndex = 0;
        }
        else if (button.id == NEXT && !modules.isEmpty())
        {
            selectedIndex = (selectedIndex + 1) % modules.size();
            booleanConfigIndex = 0;
            enumConfigIndex = 0;
        }
        else
        {
            ModuleData selected = selected(modules);
            if (selected == null)
            {
                return;
            }
            if (button.id == REMOVE)
            {
                MekanismMekaSuit.network.sendToServer(
                      new PacketModificationStationAction(isShiftKeyDown()
                            ? Action.REMOVE_ALL : Action.REMOVE_ONE,
                            selected.getType().getId()));
            }
            else if (button.id == TOGGLE && selected.getType().canDisable())
            {
                MekanismMekaSuit.network.sendToServer(
                      new PacketModificationStationAction(
                            Action.TOGGLE_ENABLED, selected.getType().getId()));
            }
            else if (button.id == MODE)
            {
                if (selected.getType().hasModes())
                {
                    String mode = selected.getType().cycleMode(
                          selected.getMode(), selected.getInstalledCount(),
                          isShiftKeyDown() ? -1 : 1);
                    MekanismMekaSuit.network.sendToServer(
                          new PacketModificationStationAction(Action.SET_MODE,
                                selected.getType().getId(), mode, false));
                }
                else if (selected.getType().getBooleanConfigKeys().size() > 1)
                {
                    booleanConfigIndex = Math.floorMod(booleanConfigIndex
                          + (isShiftKeyDown() ? -1 : 1),
                          selected.getType().getBooleanConfigKeys().size());
                }
            }
            else if (button.id == CONFIG)
            {
                if (selected.getType().hasEnumConfigs())
                {
                    if (isShiftKeyDown()
                          && selected.getType().getEnumConfigKeys().size() > 1)
                    {
                        enumConfigIndex = Math.floorMod(enumConfigIndex + 1,
                              selected.getType().getEnumConfigKeys().size());
                    }
                    else
                    {
                        String key = selectedEnumConfig(selected);
                        String value = selected.getType().cycleEnumConfig(key,
                              selected.getEnumConfig(key),
                              selected.getInstalledCount(), 1);
                        MekanismMekaSuit.network.sendToServer(
                              new PacketModificationStationAction(
                                    Action.SET_ENUM_CONFIG,
                                    selected.getType().getId(),
                                    key + "=" + value, false));
                    }
                }
                else if (selected.getType().hasBooleanConfigs())
                {
                    String key = selectedBooleanConfig(selected);
                    MekanismMekaSuit.network.sendToServer(
                          new PacketModificationStationAction(
                                Action.SET_BOOLEAN_CONFIG,
                                selected.getType().getId(), key,
                                !selected.getBooleanConfig(key)));
                }
            }
        }
        updateButtons();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        renderScaledText(tile.getInventoryName(), 45, 6, 0x404040, 115);
        fontRendererObj.drawString(LangUtils.localize("container.inventory"),
              8, ySize - 92, 0x404040);
        ModuleData selected = selected(installedModules());
        String label = selected == null
              ? LangUtils.localize("gui.mekasuit.noModules")
              : LangUtils.localize(selected.getType().getTranslationKey())
                    + " x" + selected.getInstalledCount();
        renderScaledText(label, 8, 52, 0x404040, 154);
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

    private void updateButtons()
    {
        if (previousButton == null)
        {
            return;
        }
        List<ModuleData> modules = installedModules();
        if (selectedIndex >= modules.size())
        {
            selectedIndex = Math.max(0, modules.size() - 1);
        }
        ModuleData selected = selected(modules);
        previousButton.enabled = modules.size() > 1;
        nextButton.enabled = modules.size() > 1;
        removeButton.enabled = selected != null;
        toggleButton.enabled = selected != null
              && selected.getType().canDisable();
        toggleButton.displayString = selected != null && selected.isEnabled()
              ? LangUtils.localize("gui.mekasuit.on")
              : LangUtils.localize("gui.mekasuit.off");
        boolean multipleBooleans = selected != null
              && selected.getType().getBooleanConfigKeys().size() > 1;
        modeButton.visible = selected != null
              && (selected.getType().hasModes() || multipleBooleans);
        modeButton.enabled = modeButton.visible;
        modeButton.displayString = selected != null
              && selected.getType().hasModes()
                    ? LangUtils.localize("gui.mekasuit.mode") : ">";
        configButton.visible = selected != null
              && (selected.getType().hasBooleanConfigs()
                    || selected.getType().hasEnumConfigs());
        configButton.enabled = configButton.visible;
        configButton.displayString = configButton.visible ? "Cfg" : "";
    }

    private List<ModuleData> installedModules()
    {
        ItemStack stack = tile.getContainerStack();
        if (stack == null
              || !(stack.getItem() instanceof IModuleContainerItem))
        {
            return Collections.emptyList();
        }
        IModuleContainerItem item = (IModuleContainerItem)stack.getItem();
        return new ArrayList<ModuleData>(ModuleContainer.fromStack(stack,
              item.getModuleTarget()).getModules());
    }

    private ModuleData selected(List<ModuleData> modules)
    {
        return modules.isEmpty() ? null
              : modules.get(Math.min(selectedIndex, modules.size() - 1));
    }

    private String selectedBooleanConfig(ModuleData module)
    {
        List<String> keys = new ArrayList<String>(
              module.getType().getBooleanConfigKeys());
        return keys.get(Math.floorMod(booleanConfigIndex, keys.size()));
    }

    private String selectedEnumConfig(ModuleData module)
    {
        List<String> keys = new ArrayList<String>(
              module.getType().getEnumConfigKeys());
        return keys.get(Math.floorMod(enumConfigIndex, keys.size()));
    }
}
