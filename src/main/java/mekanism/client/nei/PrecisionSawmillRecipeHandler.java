package mekanism.client.nei;

import mekanism.client.gui.GuiPrecisionSawmill;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.machines.SawmillRecipe;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.RecipeUtils;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public class PrecisionSawmillRecipeHandler extends ChanceMachineRecipeHandler
{	
	@Override
	public String getRecipeName()
	{
		return LangUtils.localize("tile.MachineBlock2.PrecisionSawmill.name");
	}

	@Override
	public String getRecipeId()
	{
		return "mekanism.precisionsawmill";
	}

	@Override
	public String getOverlayIdentifier()
	{
		return "precisionsawmill";
	}

	@Override
	public Collection<SawmillRecipe> getRecipes()
	{
		return Recipe.PRECISION_SAWMILL.get().values();
	}

	@Override
	public void loadCraftingRecipes(ItemStack result)
	{
		if(isSawingFactory(result))
		{
			loadCraftingRecipes(getRecipeId());
		}
		else {
			super.loadCraftingRecipes(result);
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient)
	{
		if(isSawingFactory(ingredient))
		{
			loadCraftingRecipes(getRecipeId());
		}
		else {
			super.loadUsageRecipes(ingredient);
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients)
	{
		if(inputId.equals(getRecipeId()))
		{
			loadCraftingRecipes(getRecipeId());
		}
		else {
			super.loadUsageRecipes(inputId, ingredients);
		}
	}

	private boolean isSawingFactory(ItemStack stack)
	{
		if(stack == null)
		{
			return false;
		}

		for(FactoryTier tier : FactoryTier.values())
		{
			ItemStack factory = MekanismUtils.getFactory(tier, RecipeType.SAWING);
			if(factory != null && RecipeUtils.areItemsEqualForCrafting(factory, stack))
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public ProgressBar getProgressType()
	{
		return ProgressBar.PURPLE;
	}

	@Override
	public Class getGuiClass()
	{
		return GuiPrecisionSawmill.class;
	}
}
