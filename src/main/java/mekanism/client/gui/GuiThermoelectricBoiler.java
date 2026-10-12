package mekanism.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.api.MekanismConfig.general;
import mekanism.api.gas.GasTank;
import mekanism.api.util.ListUtils;
import mekanism.api.util.UnitDisplayUtils;
import mekanism.api.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.client.gui.element.GuiBoilerTab;
import mekanism.client.gui.element.GuiBoilerTab.BoilerTab;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiGasGauge;
import mekanism.client.gui.element.GuiGasGauge.IGasInfoHandler;
import mekanism.client.gui.element.GuiGauge.Type;
import mekanism.client.gui.element.GuiHeatInfo;
import mekanism.client.gui.element.GuiRateBar;
import mekanism.client.gui.element.GuiRateBar.IRateInfoHandler;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.content.boiler.SynchronizedBoilerData;
import mekanism.common.inventory.container.ContainerFilter;
import mekanism.common.tile.TileEntityBoilerCasing;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiThermoelectricBoiler extends GuiMekanism
{
	public TileEntityBoilerCasing tileEntity;

	public GuiThermoelectricBoiler(InventoryPlayer inventory, TileEntityBoilerCasing tentity)
	{
		super(tentity, new ContainerFilter(inventory, tentity));
		tileEntity = tentity;
		guiElements.add(new GuiBoilerTab(this, tileEntity, BoilerTab.STAT, 6, MekanismUtils.getResource(ResourceType.GUI, "GuiThermoelectricBoiler.png")));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.getSuperheatedCoolantTank();
			}
		}, Type.STANDARD_ORANGE, this, MekanismUtils.getResource(ResourceType.GUI, "GuiThermoelectricBoiler.png"), 25, 13));
		guiElements.add(new GuiGasGauge(new IGasInfoHandler() {
			@Override
			public GasTank getTank()
			{
				return tileEntity.getCooledCoolantTank();
			}
		}, Type.STANDARD_BLUE, this, MekanismUtils.getResource(ResourceType.GUI, "GuiThermoelectricBoiler.png"), 133, 13));
		guiElements.add(new GuiRateBar(this, new IRateInfoHandler()
		{
			@Override
			public String getTooltip()
			{
				return LangUtils.localize("gui.boilRate") + ": " + tileEntity.getLastBoilRate() + " mB/t";
			}
			
			@Override
			public double getLevel()
			{
				return tileEntity.getLastMaxBoil() == 0 ? 0 :
						(double)tileEntity.getLastBoilRate()/(double)tileEntity.getLastMaxBoil();
			}
		}, MekanismUtils.getResource(ResourceType.GUI, "GuiThermoelectricBoiler.png"), 43, 13));
		guiElements.add(new GuiRateBar(this, new IRateInfoHandler()
		{
			@Override
			public String getTooltip()
			{
				return LangUtils.localize("gui.maxBoil") + ": " + tileEntity.getLastMaxBoil() + " mB/t";
			}
			
			@Override
			public double getLevel()
			{
				double cap = (tileEntity.getSuperheatingElements()*general.superheatingHeatTransfer) / SynchronizedBoilerData.getHeatEnthalpy();
				return cap == 0 ? 0 : (double)tileEntity.getLastMaxBoil()/cap;
			}
		}, MekanismUtils.getResource(ResourceType.GUI, "GuiThermoelectricBoiler.png"), 125, 13));
		guiElements.add(new GuiHeatInfo(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				TemperatureUnit unit = TemperatureUnit.values()[general.tempUnit.ordinal()];
				String environment = UnitDisplayUtils.getDisplayShort(tileEntity.getLastEnvironmentLoss()*unit.intervalSize, false, unit);
				return ListUtils.asList(LangUtils.localize("gui.dissipated") + ": " + environment + "/t");
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "GuiThermoelectricBoiler.png")));
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(tileEntity.getInventoryName())/2), 5, 0x404040);
		
		renderScaledText(LangUtils.localize("gui.temp") + ": " + MekanismUtils.getTemperatureDisplay(tileEntity.getTemperature(), TemperatureUnit.AMBIENT), 52, 25, 0x00CD00, 72);
		renderScaledText(LangUtils.localize("gui.coolantRate") + ": " + tileEntity.getLastCoolantRate() + " mB/t", 52, 34, 0x00CD00, 72);
		renderScaledText(LangUtils.localize("gui.boilRate") + ": " + tileEntity.getLastBoilRate() + " mB/t", 52, 43, 0x00CD00, 72);
		renderScaledText(LangUtils.localize("gui.maxBoil") + ": " + tileEntity.getLastMaxBoil() + " mB/t", 52, 52, 0x00CD00, 72);
		
		if(xAxis >= 7 && xAxis <= 23 && yAxis >= 14 && yAxis <= 72)
		{
			FluidStack waterStored = tileEntity.structure != null ? tileEntity.structure.waterStored : null;
			drawCreativeTabHoveringText(waterStored != null ? LangUtils.localizeFluidStack(waterStored) + ": " + waterStored.amount + "mB" : LangUtils.localize("gui.empty"), xAxis, yAxis);
		}
		
		if(xAxis >= 153 && xAxis <= 169 && yAxis >= 14 && yAxis <= 72)
		{
			FluidStack steamStored = tileEntity.structure != null ? tileEntity.structure.steamStored : null;
			drawCreativeTabHoveringText(steamStored != null ? LangUtils.localizeFluidStack(steamStored) + ": " + steamStored.amount + "mB" : LangUtils.localize("gui.empty"), xAxis, yAxis);
		}
		
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GuiThermoelectricBoiler.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
		
		if(tileEntity.getScaledWaterLevel(58) > 0)
		{
			displayGauge(7, 14, tileEntity.getScaledWaterLevel(58), tileEntity.structure.waterStored);
		}
		
		if(tileEntity.getScaledSteamLevel(58) > 0)
		{
			displayGauge(153, 14, tileEntity.getScaledSteamLevel(58), tileEntity.structure.steamStored);
		}
		
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}
	
	public void displayGauge(int xPos, int yPos, int scale, FluidStack fluid)
	{
		if(fluid == null)
		{
			return;
		}

		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;

		int start = 0;

		while(true)
		{
			int renderRemaining = 0;

			if(scale > 16)
			{
				renderRemaining = 16;
				scale -= 16;
			}
			else {
				renderRemaining = scale;
				scale = 0;
			}

			mc.renderEngine.bindTexture(MekanismRenderer.getBlocksTexture());
			drawTexturedModelRectFromIcon(guiWidth + xPos, guiHeight + yPos + 58 - renderRemaining - start, fluid.getFluid().getIcon(), 16, 16 - (16 - renderRemaining));
			start+=16;

			if(renderRemaining == 0 || scale == 0)
			{
				break;
			}
		}

		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GuiIndustrialTurbine.png"));
		drawTexturedModalRect(guiWidth + xPos, guiHeight + yPos, 176, 0, 16, 54);
	}
}
