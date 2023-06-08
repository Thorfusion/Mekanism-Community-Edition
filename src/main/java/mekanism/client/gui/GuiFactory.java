package mekanism.client.gui;

import java.io.IOException;
import java.util.Arrays;
import mekanism.api.TileNetworkList;
import mekanism.api.gas.GasStack;
import mekanism.api.infuse.InfuseType;
import mekanism.client.gui.element.*;
import mekanism.client.gui.element.tab.*;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.base.IFactory.MachineFuelType;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.inventory.container.ContainerFactory;
import mekanism.common.item.ItemGaugeDropper;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiFactory extends GuiMekanismTile<TileEntityFactory> {

    private GuiButton infuserDumpButton = null;

    public GuiFactory(InventoryPlayer inventory, TileEntityFactory tile) {
        super(tile, new ContainerFactory(inventory, tile));
        ySize += 11;
        if (tile.tier == FactoryTier.ULTIMATE) {
            xSize += 34;
        }
        ResourceLocation resource = tileEntity.tier.guiLocation;
        if (tile.tier == FactoryTier.ULTIMATE) {
            addGuiElement(new GuiSecurityTab2(this, tileEntity, resource));
            addGuiElement(new GuiUpgradeTab2(this, tileEntity, resource));
            addGuiElement(new GuiRedstoneControl2(this, tileEntity, resource));
        }else {
            addGuiElement(new GuiSecurityTab(this, tileEntity, resource));
            addGuiElement(new GuiUpgradeTab(this, tileEntity, resource));
            addGuiElement(new GuiRedstoneControl(this, tileEntity, resource));
        }
        addGuiElement(new GuiRecipeType(this, tileEntity, resource));
        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiTransporterConfigTab(this, 34, tileEntity, resource));
        addGuiElement(new GuiSortingTab(this, tileEntity, resource));
        addGuiElement(new GuiEnergyInfo(() -> {
            String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.lastUsage);
            return Arrays.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t",
                  LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy()));
        }, this, resource));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(this.infuserDumpButton = new GuiButtonImage(1, this.guiLeft+6, this.guiTop+44, 21, 10, 147, 72, 0, MekanismUtils.getResource(ResourceType.GUI, "GuiMetallurgicInfuser.png")){
            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                if (GuiFactory.this.tileEntity.getRecipeType() == RecipeType.INFUSING) {
                    super.drawButton(mc, mouseX, mouseY, partialTicks);
                }
            }

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                return GuiFactory.this.tileEntity.getRecipeType() == RecipeType.INFUSING && super.mousePressed(mc, mouseX, mouseY);
            }
        });
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        if (tileEntity.tier == FactoryTier.ULTIMATE){
            fontRenderer.drawString(LangUtils.localize("container.inventory"), 27, (ySize - 93) + 2, 0x404040);
        }else {
            fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 93) + 2, 0x404040);
        }


        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;

        if (tileEntity.tier == FactoryTier.ULTIMATE) {
            if (xAxis >= 199 && xAxis <= 203 && yAxis >= 17 && yAxis <= 69) {
                displayTooltip(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy(), tileEntity.getMaxEnergy()), xAxis, yAxis);
            } else if (xAxis >= 8 && xAxis <= 202 && yAxis >= 78 && yAxis <= 83) {
                if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
                    GasStack gasStack = tileEntity.gasTank.getGas();
                    displayTooltip(gasStack != null ? gasStack.getGas().getLocalizedName() + ": " + tileEntity.gasTank.getStored() : LangUtils.localize("gui.none"), xAxis, yAxis);
                } else if (tileEntity.getRecipeType() == RecipeType.INFUSING) {
                    InfuseType type = tileEntity.infuseStored.getType();
                    displayTooltip(type != null ? type.getLocalizedName() + ": " + tileEntity.infuseStored.getAmount() : LangUtils.localize("gui.empty"), xAxis, yAxis);
                }
            }
            super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        } else {
            if (xAxis >= 165 && xAxis <= 169 && yAxis >= 17 && yAxis <= 69) {
                displayTooltip(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy(), tileEntity.getMaxEnergy()), xAxis, yAxis);
            } else if (xAxis >= 8 && xAxis <= 168 && yAxis >= 78 && yAxis <= 83) {
                if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
                    GasStack gasStack = tileEntity.gasTank.getGas();
                    displayTooltip(gasStack != null ? gasStack.getGas().getLocalizedName() + ": " + tileEntity.gasTank.getStored() : LangUtils.localize("gui.none"), xAxis, yAxis);
                } else if (tileEntity.getRecipeType() == RecipeType.INFUSING) {
                    InfuseType type = tileEntity.infuseStored.getType();
                    displayTooltip(type != null ? type.getLocalizedName() + ": " + tileEntity.infuseStored.getAmount() : LangUtils.localize("gui.empty"), xAxis, yAxis);
                }
            }
            super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        int displayInt = tileEntity.getScaledEnergyLevel(52);
        if (tileEntity.tier == FactoryTier.ULTIMATE) {
            drawTexturedModalRect(guiLeft + 199, guiTop + 17 + 52 - displayInt, 210, 52 - displayInt, 4, displayInt);
        }else {
            drawTexturedModalRect(guiLeft + 165, guiTop + 17 + 52 - displayInt, 176, 52 - displayInt, 4, displayInt);
        }
        int xOffset = tileEntity.tier == FactoryTier.BASIC ? 59 : tileEntity.tier == FactoryTier.ADVANCED ? 39 : tileEntity.tier == FactoryTier.ELITE ? 33 : 31;
        int xDistance = tileEntity.tier == FactoryTier.BASIC ? 38 : tileEntity.tier == FactoryTier.ADVANCED ? 26 : tileEntity.tier == FactoryTier.ELITE ? 19 : 19;

        for (int i = 0; i < tileEntity.tier.processes; i++) {
            int xPos = xOffset + (i * xDistance);
            displayInt = tileEntity.getScaledProgress(20, i);
            if (tileEntity.tier == FactoryTier.ULTIMATE) {
                drawTexturedModalRect(guiLeft + xPos, guiTop + 33, 210, 52, 8, displayInt);
            }else {
                drawTexturedModalRect(guiLeft + xPos, guiTop + 33, 176, 52, 8, displayInt);
            }
        }

        if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
            if (tileEntity.getScaledGasLevel(160) > 0) {
                GasStack gas = tileEntity.gasTank.getGas();
                if (gas != null) {
                    MekanismRenderer.color(gas);
                    if (tileEntity.tier == FactoryTier.ULTIMATE) {
                        displayGauge(8, 78, tileEntity.getScaledGasLevel(194), 5, gas.getGas().getSprite());
                    }else {
                        displayGauge(8, 78, tileEntity.getScaledGasLevel(160), 5, gas.getGas().getSprite());
                    }
                    MekanismRenderer.resetColor();
                }
            }
        } else if (tileEntity.getRecipeType() == RecipeType.INFUSING) {
            if (tileEntity.getScaledInfuseLevel(160) > 0) {
                if (tileEntity.tier == FactoryTier.ULTIMATE){
                    displayGauge(8, 78, tileEntity.getScaledInfuseLevel(194), 5, tileEntity.infuseStored.getType().sprite);
                }else {
                    displayGauge(8, 78, tileEntity.getScaledInfuseLevel(160), 5, tileEntity.infuseStored.getType().sprite);
                }
            }
        }
    }

    public void displayGauge(int xPos, int yPos, int sizeX, int sizeY, TextureAtlasSprite icon) {
        if (icon != null) {
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            drawTexturedModalRect(guiLeft + xPos, guiTop + yPos, icon, sizeX, sizeY);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        if (button == 0 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            int xAxis = x - guiLeft;
            int yAxis = y - guiTop;
            if (xAxis > 8 && xAxis < 168 && yAxis > 78 && yAxis < 83) {
                ItemStack stack = mc.player.inventory.getItemStack();
                if (!stack.isEmpty() && stack.getItem() instanceof ItemGaugeDropper) {
                    TileNetworkList data = TileNetworkList.withContents(1);
                    Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
                    SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                }
            }
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return tileEntity.tier.guiLocation;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button == this.infuserDumpButton) {
            TileNetworkList data = TileNetworkList.withContents(1);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }
}