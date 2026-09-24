package mekanism.nuclear.common.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ChemicalPairInput;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.common.recipe.machines.ChemicalInfuserRecipe;
import mekanism.common.recipe.machines.SolarNeutronRecipe;
import mekanism.nuclear.common.NuclearChemicals;
import org.junit.BeforeClass;
import org.junit.Test;

public class NuclearLegacyRecipeRegistryTest {

    @BeforeClass
    public static void registerContent() {
        NuclearChemicals.register();
        NuclearLegacyRecipeRegistry.register();
    }

    @Test
    public void chemicalInfuserProducesStableUraniumHexafluorideRatio() {
        ChemicalInfuserRecipe recipe = RecipeHandler.getChemicalInfuserRecipe(new ChemicalPairInput(
              new GasStack(NuclearChemicals.UraniumOxide, 1),
              new GasStack(NuclearChemicals.HydrofluoricAcid, 1)));
        assertNotNull(recipe);
        assertEquals(1, recipe.getInput().leftGas.amount);
        assertEquals(1, recipe.getInput().rightGas.amount);
        assertSame(NuclearChemicals.UraniumHexafluoride, recipe.getOutput().output.getGas());
        assertEquals(2, recipe.getOutput().output.amount);
    }

    @Test
    public void solarActivatorProducesStablePoloniumRatioIdempotently() {
        int infuserRecipes = RecipeHandler.Recipe.CHEMICAL_INFUSER.get().size();
        int activatorRecipes = RecipeHandler.Recipe.SOLAR_NEUTRON_ACTIVATOR.get().size();
        NuclearLegacyRecipeRegistry.register();
        assertEquals(infuserRecipes, RecipeHandler.Recipe.CHEMICAL_INFUSER.get().size());
        assertEquals(activatorRecipes, RecipeHandler.Recipe.SOLAR_NEUTRON_ACTIVATOR.get().size());

        SolarNeutronRecipe recipe = RecipeHandler.getSolarNeutronRecipe(
              new GasInput(new GasStack(NuclearChemicals.NuclearWaste, 10)));
        assertNotNull(recipe);
        assertEquals(10, recipe.getInput().ingredient.amount);
        assertSame(NuclearChemicals.Polonium, recipe.getOutput().output.getGas());
        assertEquals(1, recipe.getOutput().output.amount);
    }
}
