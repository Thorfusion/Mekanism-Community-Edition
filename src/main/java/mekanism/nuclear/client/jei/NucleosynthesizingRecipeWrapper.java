package mekanism.nuclear.client.jei;

import mekanism.api.gas.GasStack;
import mekanism.client.jei.MekanismJEI;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

public class NucleosynthesizingRecipeWrapper implements IRecipeWrapper {

    private final ItemChemicalToItemRecipeCE recipe;

    public NucleosynthesizingRecipeWrapper(ItemChemicalToItemRecipeCE recipe) {
        this.recipe = recipe;
    }

    public ItemChemicalToItemRecipeCE getRecipe() {
        return recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, recipe.getItemInput().getRepresentations());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
        MekGasChemicalType chemical = (MekGasChemicalType) recipe.getChemicalInput().getType();
        ingredients.setInput(MekanismJEI.TYPE_GAS,
              new GasStack(chemical.getGas(), (int) Math.min(Integer.MAX_VALUE,
                    recipe.getChemicalInput().getAmount())));
    }
}
