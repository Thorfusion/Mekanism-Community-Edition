package mekanism.nuclear.client.jei;

import mekanism.api.gas.GasStack;
import mekanism.client.jei.MekanismJEI;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CentrifugingRecipeWrapper implements IRecipeWrapper {

    private final ChemicalToChemicalRecipeCE recipe;

    public CentrifugingRecipeWrapper(ChemicalToChemicalRecipeCE recipe) {
        this.recipe = recipe;
    }

    public ChemicalToChemicalRecipeCE getRecipe() {
        return recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        MekGasChemicalType input = (MekGasChemicalType) recipe.getInput().getType();
        MekGasChemicalType output = (MekGasChemicalType) recipe.getOutput().getType();
        ingredients.setInput(MekanismJEI.TYPE_GAS,
              new GasStack(input.getGas(), safeInt(recipe.getInput().getAmount())));
        ingredients.setOutput(MekanismJEI.TYPE_GAS,
              new GasStack(output.getGas(), safeInt(recipe.getOutput().getAmount())));
    }

    private static int safeInt(long amount) {
        return (int) Math.min(Integer.MAX_VALUE, amount);
    }
}
