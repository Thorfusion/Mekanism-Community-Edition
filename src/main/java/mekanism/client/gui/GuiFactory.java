package mekanism.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.api.Coord4D;
import mekanism.api.util.ListUtils;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.*;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.inventory.container.ContainerFactory;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiFactory extends GuiMekanism
{
	public TileEntityFactory tileEntity;

	public GuiFactory(InventoryPlayer inventory, TileEntityFactory tentity)
	{
		super(tentity, new ContainerFactory(inventory, tentity));
		tileEntity = tentity;

		ySize += 11;
		if(tileEntity.tier == FactoryTier.ULTIMATE)
		{
			ySize += 16;
		}

		guiElements.add(new GuiRedstoneControl(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiSecurityTab(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiUpgradeTab(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiRecipeType(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiSideConfigurationTab(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiTransporterConfigTab(this, 34, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiSortingTab(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiEnergyInfo(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.lastUsage);
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, tileEntity.tier.guiLocation));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(tileEntity.getInventoryName())/2), 4, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 93) + 2, 0x404040);

		int energyX = tileEntity.tier == FactoryTier.ULTIMATE ? 171 : 165;
		if(xAxis >= energyX && xAxis <= energyX + 4 && yAxis >= 17 && yAxis <= 69)
		{
			drawCreativeTabHoveringText(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy()), xAxis, yAxis);
		}

		int gaugeX = tileEntity.tier == FactoryTier.ULTIMATE ? 52 : 8;
		int gaugeY = tileEntity.tier == FactoryTier.ULTIMATE ? 83 : 78;
		int gaugeWidth = tileEntity.tier == FactoryTier.ULTIMATE ? 116 : 160;
		if(xAxis >= gaugeX && xAxis <= gaugeX + gaugeWidth && yAxis >= gaugeY && yAxis <= gaugeY + 5)
		{
			if(tileEntity.recipeType.usesFuel())
			{
				drawCreativeTabHoveringText(tileEntity.gasTank.getGas() != null ? tileEntity.gasTank.getGas().getGas().getLocalizedName() + ": " + tileEntity.gasTank.getStored() : LangUtils.localize("gui.none"), xAxis, yAxis);
			}
			else if(tileEntity.recipeType == RecipeType.INFUSING)
			{
				drawCreativeTabHoveringText(tileEntity.infuseStored.type != null ? tileEntity.infuseStored.type.getLocalizedName() + ": " + tileEntity.infuseStored.amount : LangUtils.localize("gui.empty"), xAxis, yAxis);
			}
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(tileEntity.tier.guiLocation);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int xAxis = mouseX - guiWidth;
		int yAxis = mouseY - guiHeight;

		int displayInt;

		displayInt = tileEntity.getScaledEnergyLevel(52);
		int energyX = tileEntity.tier == FactoryTier.ULTIMATE ? 171 : 165;
		drawTexturedModalRect(guiWidth + energyX, guiHeight + 17 + 52 - displayInt, 176, 52 - displayInt, 4, displayInt);

		int xOffset = tileEntity.tier == FactoryTier.BASIC ? 59 : (tileEntity.tier == FactoryTier.ADVANCED ? 
			39 : (tileEntity.tier == FactoryTier.ELITE ? 33 : 11));
		int xDistance = tileEntity.tier == FactoryTier.BASIC ? 38 : (tileEntity.tier == FactoryTier.ADVANCED ? 
			26 : (tileEntity.tier == FactoryTier.ELITE ? 19 : 18));
		
		for(int i = 0; i < tileEntity.tier.processes; i++)
		{
			int xPos = xOffset + (i*xDistance);

			displayInt = tileEntity.getScaledProgress(20, i);
			drawTexturedModalRect(guiWidth + xPos, guiHeight + 33, 176, 52, 8, displayInt);
		}

		if(tileEntity.recipeType.usesFuel())
		{
			if(tileEntity.getScaledGasLevel(160) > 0)
			{
				int gaugeWidth = tileEntity.tier == FactoryTier.ULTIMATE ? 116 : 160;
				displayGauge(tileEntity.tier == FactoryTier.ULTIMATE ? 52 : 8, tileEntity.tier == FactoryTier.ULTIMATE ? 83 : 78, tileEntity.getScaledGasLevel(gaugeWidth), 5, tileEntity.gasTank.getGas().getGas().getIcon());
			}
		}
		else if(tileEntity.recipeType == RecipeType.INFUSING)
		{
			if(tileEntity.getScaledInfuseLevel(160) > 0)
			{
				int gaugeWidth = tileEntity.tier == FactoryTier.ULTIMATE ? 116 : 160;
				displayGauge(tileEntity.tier == FactoryTier.ULTIMATE ? 52 : 8, tileEntity.tier == FactoryTier.ULTIMATE ? 83 : 78, tileEntity.getScaledInfuseLevel(gaugeWidth), 5, tileEntity.infuseStored.type.icon);
			}
		}
		
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}

	public void displayGauge(int xPos, int yPos, int sizeX, int sizeY, IIcon icon)
	{
		if(icon == null)
		{
			return;
		}

		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;

		mc.renderEngine.bindTexture(MekanismRenderer.getBlocksTexture());
		drawTexturedModelRectFromIcon(guiWidth + xPos, guiHeight + yPos, icon, sizeX, sizeY);
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);

		if(button == 0 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			int xAxis = (x - (width - xSize) / 2);
			int yAxis = (y - (height - ySize) / 2);

			int dumpY = tileEntity.tier == FactoryTier.ULTIMATE ? 99 : 83;
			if(xAxis > 144 && xAxis < 168 && yAxis > dumpY && yAxis < dumpY + 10)
			{
				ArrayList data = new ArrayList();
				data.add(1);

				Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
				SoundHandler.playSound("gui.button.press");
			}
		}
	}
}
