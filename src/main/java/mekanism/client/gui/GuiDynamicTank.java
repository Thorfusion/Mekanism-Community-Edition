package mekanism.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.client.gui.element.GuiContainerEditMode;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.content.tank.DynamicTankChemicalHooks;
import mekanism.common.content.tank.DynamicTankChemicalStack;
import mekanism.common.content.tank.TankUpdateProtocol;
import mekanism.common.inventory.container.ContainerDynamicTank;
import mekanism.common.tile.TileEntityDynamicTank;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiDynamicTank extends GuiMekanism
{
	public TileEntityDynamicTank tileEntity;

	public GuiDynamicTank(InventoryPlayer inventory, TileEntityDynamicTank tentity)
	{
		super(tentity, new ContainerDynamicTank(inventory, tentity));
		tileEntity = tentity;
		guiElements.add(new GuiContainerEditMode(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiDynamicTank.png")));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize/2)-(fontRendererObj.getStringWidth(tileEntity.getInventoryName())/2), 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 94) + 2, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("gui.volume") + ": " + tileEntity.clientCapacity/TankUpdateProtocol.FLUID_PER_TANK, 53, 26, 0x00CD00);
		FluidStack fluid = tileEntity.structure != null ? tileEntity.structure.fluidStored : null;
		DynamicTankChemicalStack chemical = tileEntity.structure != null ? tileEntity.structure.chemicalStored : null;
		String contentsName = chemical != null ? DynamicTankChemicalHooks.getDisplayName(chemical) : fluid != null ? LangUtils.localizeFluidStack(fluid) : LangUtils.localize("gui.noFluid");
		renderScaledText(contentsName + (chemical != null || fluid != null ? ":" : ""), 53, 44, 0x00CD00, 74);

		if(chemical != null || fluid != null)
		{
			fontRendererObj.drawString((chemical != null ? chemical.amount : fluid.amount) + "mB", 53, 53, 0x00CD00);
		}

		if(xAxis >= 7 && xAxis <= 39 && yAxis >= 14 && yAxis <= 72)
		{
			drawCreativeTabHoveringText(chemical != null ? DynamicTankChemicalHooks.getDisplayName(chemical) + ": " + chemical.amount + "mB" : fluid != null ? LangUtils.localizeFluidStack(fluid) + ": " + fluid.amount + "mB" : LangUtils.localize("gui.empty"), xAxis, yAxis);
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);

		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GuiDynamicTank.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
		
		FluidStack renderStack = tileEntity.structure == null ? null : tileEntity.structure.chemicalStored == null ? tileEntity.structure.fluidStored : DynamicTankChemicalHooks.getRenderStack(tileEntity.structure.chemicalStored);
		if(tileEntity.getScaledFluidLevel(58) > 0 && renderStack != null)
		{
			displayGauge(7, 14, tileEntity.getScaledFluidLevel(58), renderStack, 0);
			displayGauge(23, 14, tileEntity.getScaledFluidLevel(58), renderStack, 1);
		}
	}

	public void displayGauge(int xPos, int yPos, int scale, FluidStack fluid, int side /*0-left, 1-right*/)
	{
		if(fluid == null)
		{
			return;
		}

		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;

		int start = 0;
		MekanismRenderer.colorFluid(fluid);

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
		MekanismRenderer.resetColor();

		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GuiDynamicTank.png"));
		drawTexturedModalRect(guiWidth + xPos, guiHeight + yPos, 176, side == 0 ? 0 : 54, 16, 54);
	}
}
