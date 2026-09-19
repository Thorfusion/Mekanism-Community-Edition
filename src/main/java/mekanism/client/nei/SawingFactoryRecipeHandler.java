package mekanism.client.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.guihook.IContainerTooltipHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import mekanism.client.gui.GuiFactory;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.base.IFactory.RecipeType;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import java.awt.Point;
import java.util.List;

/**
 * Routes NEI recipe lookups from the progress indicators in a Sawing Factory
 * to the Precision Sawmill recipe handler.
 */
public class SawingFactoryRecipeHandler implements IContainerInputHandler, IContainerTooltipHandler
{
	private static final String RECIPE_ID = "mekanism.precisionsawmill";

	public static void register()
	{
		SawingFactoryRecipeHandler handler = new SawingFactoryRecipeHandler();
		GuiContainerManager.addInputHandler(handler);
		GuiContainerManager.addTooltipHandler(handler);
	}

	private boolean handles(GuiContainer gui, int mouseX, int mouseY)
	{
		if(!(gui instanceof GuiFactory))
		{
			return false;
		}

		GuiFactory factoryGui = (GuiFactory)gui;
		if(factoryGui.tileEntity.recipeType != RecipeType.SAWING)
		{
			return false;
		}

		int relativeX = mouseX - factoryGui.getXPos();
		int relativeY = mouseY - factoryGui.getYPos();
		if(relativeY < 33 || relativeY >= 53)
		{
			return false;
		}

		FactoryTier tier = factoryGui.tileEntity.tier;
		int xOffset = tier == FactoryTier.BASIC ? 59 : tier == FactoryTier.ADVANCED ? 39 : tier == FactoryTier.ELITE ? 33 : 11;
		int xDistance = tier == FactoryTier.BASIC ? 38 : tier == FactoryTier.ADVANCED ? 26 : tier == FactoryTier.ELITE ? 19 : 18;

		for(int process = 0; process < tier.processes; process++)
		{
			int progressX = xOffset + process*xDistance;
			if(relativeX >= progressX && relativeX < progressX + 8)
			{
				return true;
			}
		}

		return false;
	}

	private boolean openRecipes(GuiContainer gui, int mouseX, int mouseY, boolean usage)
	{
		if(!handles(gui, mouseX, mouseY))
		{
			return false;
		}

		return usage ? GuiUsageRecipe.openRecipeGui(RECIPE_ID) : GuiCraftingRecipe.openRecipeGui(RECIPE_ID);
	}

	@Override
	public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyCode)
	{
		Point mouse = GuiDraw.getMousePosition();
		if(keyCode == NEIClientConfig.getKeyBinding("gui.recipe"))
		{
			return openRecipes(gui, mouse.x, mouse.y, false);
		}
		else if(keyCode == NEIClientConfig.getKeyBinding("gui.usage"))
		{
			return openRecipes(gui, mouse.x, mouse.y, true);
		}

		return false;
	}

	@Override
	public boolean mouseClicked(GuiContainer gui, int mouseX, int mouseY, int button)
	{
		if(button == 0 || button == 1)
		{
			return openRecipes(gui, mouseX, mouseY, button == 1);
		}

		return false;
	}

	@Override
	public List<String> handleTooltip(GuiContainer gui, int mouseX, int mouseY, List<String> currentTip)
	{
		if(handles(gui, mouseX, mouseY))
		{
			currentTip.add(NEIClientUtils.translate("recipe.tooltip"));
		}

		return currentTip;
	}

	@Override
	public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode)
	{
		return false;
	}

	@Override
	public void onKeyTyped(GuiContainer gui, char keyChar, int keyCode)
	{
	}

	@Override
	public void onMouseClicked(GuiContainer gui, int mouseX, int mouseY, int button)
	{
	}

	@Override
	public void onMouseUp(GuiContainer gui, int mouseX, int mouseY, int button)
	{
	}

	@Override
	public boolean mouseScrolled(GuiContainer gui, int mouseX, int mouseY, int scrolled)
	{
		return false;
	}

	@Override
	public void onMouseScrolled(GuiContainer gui, int mouseX, int mouseY, int scrolled)
	{
	}

	@Override
	public void onMouseDragged(GuiContainer gui, int mouseX, int mouseY, int button, long heldTime)
	{
	}

	@Override
	public List<String> handleItemDisplayName(GuiContainer gui, ItemStack stack, List<String> currentTip)
	{
		return currentTip;
	}

	@Override
	public List<String> handleItemTooltip(GuiContainer gui, ItemStack stack, int mouseX, int mouseY, List<String> currentTip)
	{
		return currentTip;
	}
}
