package mekanism.nuclear.common.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ChemicalPairInput;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.ChemicalInfuserRecipe;
import mekanism.common.recipe.machines.CrusherRecipe;
import mekanism.common.recipe.machines.EnrichmentRecipe;
import mekanism.common.recipe.machines.OxidationRecipe;
import mekanism.common.recipe.machines.SolarNeutronRecipe;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.NuclearItems;
import mekanism.nuclear.common.NuclearOreDictionary;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.BeforeClass;
import org.junit.Test;

public class NuclearLegacyRecipeRegistryTest {

    @BeforeClass
    public static void registerContent() {
        Bootstrap.register();
        NuclearChemicals.register();
        NuclearOreDictionary.register();
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
        int enrichmentRecipes = RecipeHandler.Recipe.ENRICHMENT_CHAMBER.get().size();
        int crusherRecipes = RecipeHandler.Recipe.CRUSHER.get().size();
        int oxidizerRecipes = RecipeHandler.Recipe.CHEMICAL_OXIDIZER.get().size();
        int infuserRecipes = RecipeHandler.Recipe.CHEMICAL_INFUSER.get().size();
        int activatorRecipes = RecipeHandler.Recipe.SOLAR_NEUTRON_ACTIVATOR.get().size();
        NuclearLegacyRecipeRegistry.register();
        assertEquals(enrichmentRecipes, RecipeHandler.Recipe.ENRICHMENT_CHAMBER.get().size());
        assertEquals(crusherRecipes, RecipeHandler.Recipe.CRUSHER.get().size());
        assertEquals(oxidizerRecipes, RecipeHandler.Recipe.CHEMICAL_OXIDIZER.get().size());
        assertEquals(infuserRecipes, RecipeHandler.Recipe.CHEMICAL_INFUSER.get().size());
        assertEquals(activatorRecipes, RecipeHandler.Recipe.SOLAR_NEUTRON_ACTIVATOR.get().size());

        SolarNeutronRecipe recipe = RecipeHandler.getSolarNeutronRecipe(
              new GasInput(new GasStack(NuclearChemicals.NuclearWaste, 10)));
        assertNotNull(recipe);
        assertEquals(10, recipe.getInput().ingredient.amount);
        assertSame(NuclearChemicals.Polonium, recipe.getOutput().output.getGas());
        assertEquals(1, recipe.getOutput().output.amount);
    }

    @Test
    public void uraniumMaterialChainMatchesStableRatios() {
        EnrichmentRecipe yellowCake = RecipeHandler.getRecipe(
              new ItemStackInput(new ItemStack(NuclearItems.UraniumIngot)),
              RecipeHandler.Recipe.ENRICHMENT_CHAMBER);
        assertNotNull(yellowCake);
        assertSame(NuclearItems.YellowCakeUranium, yellowCake.getOutput().output.getItem());
        assertEquals(2, yellowCake.getOutput().output.getCount());

        OxidationRecipe uraniumOxide = RecipeHandler.getOxidizerRecipe(
              new ItemStackInput(new ItemStack(NuclearItems.YellowCakeUranium)));
        assertNotNull(uraniumOxide);
        assertSame(NuclearChemicals.UraniumOxide, uraniumOxide.getOutput().output.getGas());
        assertEquals(250, uraniumOxide.getOutput().output.amount);

        ItemStack smelted = FurnaceRecipes.instance().getSmeltingResult(new ItemStack(NuclearItems.UraniumDust));
        assertSame(NuclearItems.UraniumIngot, smelted.getItem());
        assertEquals(1, smelted.getCount());
    }

    @Test
    public void fluoriteGemAndDustConversionsAreReversible() {
        CrusherRecipe crushing = RecipeHandler.getRecipe(
              new ItemStackInput(new ItemStack(NuclearItems.FluoriteGem)),
              RecipeHandler.Recipe.CRUSHER);
        assertNotNull(crushing);
        assertSame(NuclearItems.FluoriteDust, crushing.getOutput().output.getItem());
        assertEquals(1, crushing.getOutput().output.getCount());

        EnrichmentRecipe enriching = RecipeHandler.getRecipe(
              new ItemStackInput(new ItemStack(NuclearItems.FluoriteDust)),
              RecipeHandler.Recipe.ENRICHMENT_CHAMBER);
        assertNotNull(enriching);
        assertSame(NuclearItems.FluoriteGem, enriching.getOutput().output.getItem());
        assertEquals(1, enriching.getOutput().output.getCount());
    }

    @Test
    public void externalOreDictionaryOresEnterTheStableChain() {
        Item uraniumOre = new Item().setRegistryName(
              new ResourceLocation(MekanismNuclear.MODID, "test_uranium_ore"));
        Item fluoriteOre = new Item().setRegistryName(
              new ResourceLocation(MekanismNuclear.MODID, "test_fluorite_ore"));
        OreDictionary.registerOre(NuclearOreDictionary.ORE_URANIUM, new ItemStack(uraniumOre));
        OreDictionary.registerOre(NuclearOreDictionary.ORE_FLUORITE, new ItemStack(fluoriteOre));
        NuclearLegacyRecipeRegistry.register();

        EnrichmentRecipe uranium = RecipeHandler.getRecipe(
              new ItemStackInput(new ItemStack(uraniumOre)), RecipeHandler.Recipe.ENRICHMENT_CHAMBER);
        assertNotNull(uranium);
        assertSame(NuclearItems.UraniumDust, uranium.getOutput().output.getItem());
        assertEquals(2, uranium.getOutput().output.getCount());

        EnrichmentRecipe fluorite = RecipeHandler.getRecipe(
              new ItemStackInput(new ItemStack(fluoriteOre)), RecipeHandler.Recipe.ENRICHMENT_CHAMBER);
        assertNotNull(fluorite);
        assertSame(NuclearItems.FluoriteGem, fluorite.getOutput().output.getItem());
        assertEquals(6, fluorite.getOutput().output.getCount());
    }
}
