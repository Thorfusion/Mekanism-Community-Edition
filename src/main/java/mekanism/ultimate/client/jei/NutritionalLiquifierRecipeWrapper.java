package mekanism.ultimate.client.jei;

import mekanism.ultimate.common.UltimateFluids;
import mekanism.ultimate.common.nutrition.UltimateNutrition;
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public final class NutritionalLiquifierRecipeWrapper implements IRecipeWrapper {

    private final ItemStack input;
    private final ItemStack containerOutput;
    private final FluidStack pasteOutput;

    public NutritionalLiquifierRecipeWrapper(ItemStack input) {
        this.input = input.copy();
        this.input.setCount(1);
        containerOutput = UltimateNutrition.getContainerItem(this.input);
        pasteOutput = new FluidStack(UltimateFluids.NutritionalPaste,
              TileEntityNutritionalLiquifier.getPasteOutput(this.input));
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getContainerOutput() {
        return containerOutput;
    }

    public FluidStack getPasteOutput() {
        return pasteOutput;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, input);
        ingredients.setOutput(VanillaTypes.FLUID, pasteOutput);
        if (!containerOutput.isEmpty()) {
            ingredients.setOutput(VanillaTypes.ITEM, containerOutput);
        }
    }
}
