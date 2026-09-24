package mekanism.nuclear.common.recipe;

import java.util.ArrayList;
import java.util.List;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.CachedRecipeManagerCE;
import mekanism.ultimate.common.recipe.RecipeManagersCE;
import mekanism.ultimate.common.recipe.key.ChemicalIdentityKeyCE;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import net.minecraft.util.ResourceLocation;

/** Stable-modern Isotopic Centrifuge recipes. */
public final class NuclearRecipeRegistry {

    public static final CachedRecipeManagerCE<IChemicalStackCE, ChemicalIdentityKeyCE, ChemicalToChemicalRecipeCE> CENTRIFUGING =
          RecipeManagersCE.chemicalToChemical();

    private NuclearRecipeRegistry() {
    }

    public static synchronized void registerDefaults() {
        if (NuclearChemicals.UraniumHexafluoride == null) {
            throw new IllegalStateException("Nuclear chemicals must be registered before recipes");
        }
        MekGasChemicalType uraniumHexafluoride = type(NuclearChemicals.UraniumHexafluoride.getName());
        MekGasChemicalType fissileFuel = type(NuclearChemicals.FissileFuel.getName());
        MekGasChemicalType nuclearWaste = type(NuclearChemicals.NuclearWaste.getName());
        MekGasChemicalType plutonium = type(NuclearChemicals.Plutonium.getName());
        List<ChemicalToChemicalRecipeCE> missing = new ArrayList<>();
        ResourceLocation fissileId = id("fissile_fuel");
        ResourceLocation plutoniumId = id("plutonium");
        if (CENTRIFUGING.get(fissileId) == null) {
            missing.add(recipe(fissileId, uraniumHexafluoride, 1, fissileFuel, 1));
        }
        if (CENTRIFUGING.get(plutoniumId) == null) {
            missing.add(recipe(plutoniumId, nuclearWaste, 10, plutonium, 1));
        }
        CENTRIFUGING.addAll(missing);
    }

    public static boolean containsInput(String gasName) {
        if (gasName == null || mekanism.api.gas.GasRegistry.getGas(gasName) == null) {
            return false;
        }
        return CENTRIFUGING.findFirst(new ChemicalStackCE(type(gasName), 1)) != null;
    }

    private static ChemicalToChemicalRecipeCE recipe(ResourceLocation id, MekGasChemicalType input, long inputAmount,
          MekGasChemicalType output, long outputAmount) {
        return new ChemicalToChemicalRecipeCE(
              id,
              new ChemicalIngredientCE(input, inputAmount),
              new ChemicalStackCE(output, outputAmount));
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(MekanismNuclear.MODID, "centrifuging/" + path);
    }

    private static MekGasChemicalType type(String gasName) {
        return new MekGasChemicalType(mekanism.api.gas.GasRegistry.getGas(gasName), NuclearChemicals.isRadioactive(gasName));
    }
}
