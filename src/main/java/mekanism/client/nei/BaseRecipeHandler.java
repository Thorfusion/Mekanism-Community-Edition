package mekanism.client.nei;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import mekanism.api.gas.GasStack;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static codechicken.lib.gui.GuiDraw.*;

public abstract class BaseRecipeHandler extends TemplateRecipeHandler implements IGuiWrapper
{
	public static final String FLUID_LOOKUP_ID = "mekanism.fluid";

	public BaseRecipeHandler()
	{
		addGuiElements();
	}

	public Set<GuiElement> guiElements = new HashSet<GuiElement>();

	public abstract void addGuiElements();

	public static boolean isFluidLookup(String id)
	{
		return "fluid".equals(id) || FLUID_LOOKUP_ID.equals(id);
	}

	public void displayGauge(int length, int xPos, int yPos, int overlayX, int overlayY, int scale, FluidStack fluid, GasStack gas)
	{
		if(fluid == null && gas == null)
		{
			return;
		}

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

			changeTexture(MekanismRenderer.getBlocksTexture());

			if(fluid != null)
			{
				gui.drawTexturedModelRectFromIcon(xPos, yPos + length - renderRemaining - start, fluid.getFluid().getIcon(), 16, 16 - (16 - renderRemaining));
			}
			else if(gas != null)
			{
				gui.drawTexturedModelRectFromIcon(xPos, yPos + length - renderRemaining - start, gas.getGas().getIcon(), 16, 16 - (16 - renderRemaining));
			}

			start+=16;

			if(renderRemaining == 0 || scale == 0)
			{
				break;
			}
		}

		changeTexture(getGuiTexture());
		drawTexturedModalRect(xPos, yPos, overlayX, overlayY, 16, length+1);
	}

	/*
	 * true = usage, false = recipe
	 */
	public boolean doGasLookup(GasStack stack, boolean type)
	{
		if(stack != null && stack.amount > 0)
		{
			if(type)
			{
				if(!GuiUsageRecipe.openRecipeGui("gas", new Object[] {stack}))
				{
					return false;
				}
			}
			else {
				if(!GuiCraftingRecipe.openRecipeGui("gas", new Object[] {stack}))
				{
					return false;
				}
			}

			return true;
		}

		return false;
	}

	public void displayGauge(int xPos, int yPos, int sizeX, int sizeY, GasStack gas)
	{
		if(gas == null)
		{
			return;
		}

		changeTexture(MekanismRenderer.getBlocksTexture());
		gui.drawTexturedModelRectFromIcon(xPos, yPos, gas.getGas().getIcon(), sizeX, sizeY);
	}
	
	public String stripTexture()
	{
		return getGuiTexture().replace("mekanism:gui/", "");
	}

	/*
	 * true = usage, false = recipe
	 */
	public boolean doFluidLookup(FluidStack stack, boolean type)
	{
		if(stack != null && stack.amount > 0 && stack.getFluid() != null)
		{
			FluidStack copy = stack.copy();

			try
			{
				if(type)
				{
					if(!GuiUsageRecipe.openRecipeGui(FLUID_LOOKUP_ID, new Object[] {copy}))
					{
						return false;
					}
				}
				else {
					if(!GuiCraftingRecipe.openRecipeGui(FLUID_LOOKUP_ID, new Object[] {copy}))
					{
						return false;
					}
				}
			}
			catch(Throwable ignored)
			{
				return false;
			}

			return true;
		}

		return false;
	}
	
	@Override
	public void drawTexturedRect(int x, int y, int u, int v, int w, int h)
	{
		drawTexturedModalRect(x, y, u, v, w, h);
	}

	@Override
	public void drawTexturedRectFromIcon(int x, int y, IIcon icon, int w, int h)
	{
		gui.drawTexturedModelRectFromIcon(x, y, icon, w, h);
	}

	@Override
	public void displayTooltip(String s, int xAxis, int yAxis) {}

	@Override
	public void displayTooltips(List<String> list, int xAxis, int yAxis) {}

	@Override
	public FontRenderer getFont() 
	{ 
		return null; 
	}
}
