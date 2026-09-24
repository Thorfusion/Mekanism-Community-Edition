package mekanism.nuclear.common.recipe;

import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ChemicalPairInput;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.nuclear.common.NuclearChemicals;

/**
 * Exact-stable recipes that can run in existing 1.12 Mekanism machines.
 * Keeping these registrations in Nuclear avoids duplicating otherwise capable
 * core machines or adding Nuclear branches to core recipe bootstrap code.
 */
public final class NuclearLegacyRecipeRegistry {

    private NuclearLegacyRecipeRegistry() {
    }

    public static synchronized void register() {
        ChemicalPairInput uraniumHexafluorideInput = new ChemicalPairInput(
              new GasStack(NuclearChemicals.HydrofluoricAcid, 1),
              new GasStack(NuclearChemicals.UraniumOxide, 1));
        if (RecipeHandler.getChemicalInfuserRecipe(uraniumHexafluorideInput) == null) {
            RecipeHandler.addChemicalInfuserRecipe(
                  uraniumHexafluorideInput.leftGas,
                  uraniumHexafluorideInput.rightGas,
                  new GasStack(NuclearChemicals.UraniumHexafluoride, 2));
        }

        GasInput poloniumInput = new GasInput(new GasStack(NuclearChemicals.NuclearWaste, 10));
        if (RecipeHandler.getSolarNeutronRecipe(poloniumInput) == null) {
            RecipeHandler.addSolarNeutronRecipe(
                  poloniumInput.ingredient,
                  new GasStack(NuclearChemicals.Polonium, 1));
        }
    }
}
