package mekanism.nuclear.common.recipe;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.common.MekanismFluids;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.AdvancedMachineInput;
import mekanism.common.recipe.inputs.ChemicalPairInput;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.inputs.PressurizedInput;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.NuclearItems;
import mekanism.nuclear.common.NuclearOreDictionary;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Exact-stable recipes that can run in existing 1.12 Mekanism machines.
 * Keeping these registrations in Nuclear avoids duplicating otherwise capable
 * core machines or adding Nuclear branches to core recipe bootstrap code.
 */
public final class NuclearLegacyRecipeRegistry {

    private NuclearLegacyRecipeRegistry() {
    }

    public static synchronized void register() {
        NuclearOreDictionary.register();
        registerMaterialRecipes();

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

    private static void registerMaterialRecipes() {
        for (ItemStack ore : OreDictionary.getOres(NuclearOreDictionary.ORE_URANIUM, false)) {
            ItemStack input = sized(ore, 1);
            addEnrichmentIfAbsent(input, new ItemStack(NuclearItems.UraniumDust, 2));
            addSmeltingIfAbsent(input, new ItemStack(NuclearItems.UraniumIngot), 0.3F);
        }
        for (ItemStack ingot : OreDictionary.getOres(NuclearOreDictionary.INGOT_URANIUM, false)) {
            ItemStack input = sized(ingot, 1);
            addCrusherIfAbsent(input, new ItemStack(NuclearItems.UraniumDust));
            addEnrichmentIfAbsent(input, new ItemStack(NuclearItems.YellowCakeUranium, 2));
        }
        for (ItemStack dust : OreDictionary.getOres(NuclearOreDictionary.DUST_URANIUM, false)) {
            addSmeltingIfAbsent(sized(dust, 1), new ItemStack(NuclearItems.UraniumIngot), 0.3F);
        }

        for (ItemStack ore : OreDictionary.getOres(NuclearOreDictionary.ORE_FLUORITE, false)) {
            addEnrichmentIfAbsent(sized(ore, 1), new ItemStack(NuclearItems.FluoriteGem, 6));
        }
        for (ItemStack gem : OreDictionary.getOres(NuclearOreDictionary.GEM_FLUORITE, false)) {
            ItemStack input = sized(gem, 1);
            addCrusherIfAbsent(input, new ItemStack(NuclearItems.FluoriteDust));
            addDissolutionIfAbsent(input, new GasStack(NuclearChemicals.HydrofluoricAcid, 1_000));
        }
        for (ItemStack dust : OreDictionary.getOres(NuclearOreDictionary.DUST_FLUORITE, false)) {
            ItemStack input = sized(dust, 1);
            addEnrichmentIfAbsent(input, new ItemStack(NuclearItems.FluoriteGem));
            addPelletReactionIfAbsent(input, NuclearChemicals.Plutonium,
                  new ItemStack(NuclearItems.PlutoniumPellet));
            addPelletReactionIfAbsent(input, NuclearChemicals.Polonium,
                  new ItemStack(NuclearItems.PoloniumPellet));
        }

        ItemStackInput yellowCake = new ItemStackInput(new ItemStack(NuclearItems.YellowCakeUranium));
        if (RecipeHandler.getOxidizerRecipe(yellowCake) == null) {
            RecipeHandler.addChemicalOxidizerRecipe(
                  yellowCake.ingredient,
                  new GasStack(NuclearChemicals.UraniumOxide, 250));
        }

        GasInput antimatter = new GasInput(new GasStack(NuclearChemicals.Antimatter, 1_000));
        if (RecipeHandler.getChemicalCrystallizerRecipe(antimatter) == null) {
            RecipeHandler.addChemicalCrystallizerRecipe(
                  antimatter.ingredient, new ItemStack(NuclearItems.AntimatterPellet));
        }
        for (ItemStack pellet : OreDictionary.getOres(NuclearOreDictionary.PELLET_ANTIMATTER, false)) {
            ItemStackInput input = new ItemStackInput(sized(pellet, 1));
            if (RecipeHandler.getOxidizerRecipe(input) == null) {
                RecipeHandler.addChemicalOxidizerRecipe(
                      input.ingredient, new GasStack(NuclearChemicals.Antimatter, 1_000));
            }
        }
        for (ItemStack pellet : OreDictionary.getOres(NuclearOreDictionary.PELLET_PLUTONIUM, false)) {
            ItemStack input = sized(pellet, 1);
            AdvancedMachineInput recipeInput = new AdvancedMachineInput(input, MekanismFluids.HydrogenChloride);
            if (RecipeHandler.getRecipe(recipeInput, RecipeHandler.Recipe.CHEMICAL_INJECTION_CHAMBER) == null) {
                RecipeHandler.addChemicalInjectionChamberRecipe(input, MekanismFluids.HydrogenChloride,
                      new ItemStack(NuclearItems.ReprocessedFissileFragment, 4));
            }
        }

        ItemStackInput reprocessedFragment = new ItemStackInput(
              new ItemStack(NuclearItems.ReprocessedFissileFragment));
        if (RecipeHandler.getOxidizerRecipe(reprocessedFragment) == null) {
            RecipeHandler.addChemicalOxidizerRecipe(
                  reprocessedFragment.ingredient, new GasStack(NuclearChemicals.FissileFuel, 2_000));
        }
    }

    private static void addPelletReactionIfAbsent(ItemStack fluoriteDust, Gas inputGas,
          ItemStack output) {
        FluidStack water = new FluidStack(FluidRegistry.WATER, 1_000);
        GasStack gas = new GasStack(inputGas, 1_000);
        PressurizedInput input = new PressurizedInput(fluoriteDust, water, gas);
        if (RecipeHandler.getPRCRecipe(input) == null) {
            RecipeHandler.addPRCRecipe(fluoriteDust, water, gas, output,
                  new GasStack(NuclearChemicals.SpentNuclearWaste, 1_000), 0, 100);
        }
    }

    private static void addEnrichmentIfAbsent(ItemStack input, ItemStack output) {
        ItemStackInput recipeInput = new ItemStackInput(input);
        if (RecipeHandler.getRecipe(recipeInput, RecipeHandler.Recipe.ENRICHMENT_CHAMBER) == null) {
            RecipeHandler.addEnrichmentChamberRecipe(input, output);
        }
    }

    private static void addCrusherIfAbsent(ItemStack input, ItemStack output) {
        ItemStackInput recipeInput = new ItemStackInput(input);
        if (RecipeHandler.getRecipe(recipeInput, RecipeHandler.Recipe.CRUSHER) == null) {
            RecipeHandler.addCrusherRecipe(input, output);
        }
    }

    private static void addDissolutionIfAbsent(ItemStack input, GasStack output) {
        ItemStackInput recipeInput = new ItemStackInput(input);
        if (RecipeHandler.getDissolutionRecipe(recipeInput) == null) {
            RecipeHandler.addChemicalDissolutionChamberRecipe(input, output);
        }
    }

    private static void addSmeltingIfAbsent(ItemStack input, ItemStack output, float experience) {
        if (FurnaceRecipes.instance().getSmeltingResult(input).isEmpty()) {
            FurnaceRecipes.instance().addSmeltingRecipe(input, output, experience);
        }
    }

    private static ItemStack sized(ItemStack stack, int amount) {
        ItemStack copy = stack.copy();
        copy.setCount(amount);
        return copy;
    }
}
