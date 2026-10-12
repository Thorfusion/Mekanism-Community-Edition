package mekanism.nuclear.common.recipe;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
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
import java.util.List;

/** Exact-stable recipes that run in existing 1.7.10 Mekanism machines. */
public final class NuclearLegacyRecipeRegistry
{
    private NuclearLegacyRecipeRegistry()
    {
    }

    public static synchronized void register()
    {
        NuclearOreDictionary.register();
        register(new MaterialResolver()
        {
            @Override
            public List<ItemStack> get(String oreName)
            {
                return OreDictionary.getOres(oreName, false);
            }
        }, new SmeltingRegistry()
        {
            @Override
            public ItemStack getResult(ItemStack input)
            {
                return FurnaceRecipes.smelting().getSmeltingResult(input);
            }

            @Override
            public void add(ItemStack input, ItemStack output, float experience)
            {
                FurnaceRecipes.smelting().func_151394_a(input, output, experience);
            }
        }, new ReactionRegistry()
        {
            @Override
            public void addIfAbsent(ItemStack inputSolid, int waterAmount,
                  Gas inputGas, int gasAmount, ItemStack outputSolid,
                  GasStack outputGas, double extraEnergy, int ticks)
            {
                FluidStack water = new FluidStack(FluidRegistry.WATER, waterAmount);
                GasStack gas = new GasStack(inputGas, gasAmount);
                PressurizedInput input = new PressurizedInput(inputSolid, water, gas);
                if (RecipeHandler.getPRCRecipe(input) == null)
                {
                    RecipeHandler.addPRCRecipe(inputSolid, water, gas, outputSolid,
                          outputGas, extraEnergy, ticks);
                }
            }
        });
    }

    static synchronized void register(MaterialResolver materials,
          SmeltingRegistry smelting, ReactionRegistry reactions)
    {
        registerMaterialRecipes(materials, smelting, reactions);

        ChemicalPairInput uraniumHexafluorideInput = new ChemicalPairInput(
              new GasStack(NuclearChemicals.HydrofluoricAcid, 1),
              new GasStack(NuclearChemicals.UraniumOxide, 1));
        if (RecipeHandler.getChemicalInfuserRecipe(uraniumHexafluorideInput) == null)
        {
            RecipeHandler.addChemicalInfuserRecipe(
                  uraniumHexafluorideInput.leftGas,
                  uraniumHexafluorideInput.rightGas,
                  new GasStack(NuclearChemicals.UraniumHexafluoride, 2));
        }

        GasInput poloniumInput = new GasInput(
              new GasStack(NuclearChemicals.NuclearWaste, 10));
        if (RecipeHandler.getSolarNeutronRecipe(poloniumInput) == null)
        {
            RecipeHandler.addSolarNeutronRecipe(poloniumInput.ingredient,
                  new GasStack(NuclearChemicals.Polonium, 1));
        }
    }

    private static void registerMaterialRecipes(MaterialResolver materials,
          SmeltingRegistry smelting, ReactionRegistry reactions)
    {
        for (ItemStack ore : materials.get(NuclearOreDictionary.ORE_URANIUM))
        {
            ItemStack input = sized(ore, 1);
            addEnrichmentIfAbsent(input, new ItemStack(NuclearItems.UraniumDust, 2));
            addSmeltingIfAbsent(input, new ItemStack(NuclearItems.UraniumIngot),
                  0.3F, smelting);
        }
        for (ItemStack ingot : materials.get(NuclearOreDictionary.INGOT_URANIUM))
        {
            ItemStack input = sized(ingot, 1);
            addCrusherIfAbsent(input, new ItemStack(NuclearItems.UraniumDust));
            addEnrichmentIfAbsent(input, new ItemStack(NuclearItems.YellowCakeUranium, 2));
        }
        for (ItemStack dust : materials.get(NuclearOreDictionary.DUST_URANIUM))
        {
            addSmeltingIfAbsent(sized(dust, 1),
                  new ItemStack(NuclearItems.UraniumIngot), 0.3F, smelting);
        }

        for (ItemStack ore : materials.get(NuclearOreDictionary.ORE_FLUORITE))
        {
            addEnrichmentIfAbsent(sized(ore, 1),
                  new ItemStack(NuclearItems.FluoriteGem, 6));
        }
        for (ItemStack gem : materials.get(NuclearOreDictionary.GEM_FLUORITE))
        {
            ItemStack input = sized(gem, 1);
            addCrusherIfAbsent(input, new ItemStack(NuclearItems.FluoriteDust));
            addDissolutionIfAbsent(input,
                  new GasStack(NuclearChemicals.HydrofluoricAcid, 1000));
        }
        for (ItemStack block : materials.get(NuclearOreDictionary.BLOCK_FLUORITE))
        {
            addDissolutionIfAbsent(sized(block, 1),
                  new GasStack(NuclearChemicals.HydrofluoricAcid, 9000), 9);
        }
        for (ItemStack dust : materials.get(NuclearOreDictionary.DUST_FLUORITE))
        {
            ItemStack input = sized(dust, 1);
            addEnrichmentIfAbsent(input, new ItemStack(NuclearItems.FluoriteGem));
            addPelletReactionIfAbsent(input, NuclearChemicals.Plutonium,
                  new ItemStack(NuclearItems.PlutoniumPellet), reactions);
            addPelletReactionIfAbsent(input, NuclearChemicals.Polonium,
                  new ItemStack(NuclearItems.PoloniumPellet), reactions);
        }

        ItemStackInput yellowCake = new ItemStackInput(
              new ItemStack(NuclearItems.YellowCakeUranium));
        if (RecipeHandler.getOxidizerRecipe(yellowCake) == null)
        {
            RecipeHandler.addChemicalOxidizerRecipe(yellowCake.ingredient,
                  new GasStack(NuclearChemicals.UraniumOxide, 250));
        }

        GasInput antimatter = new GasInput(new GasStack(NuclearChemicals.Antimatter, 1000));
        if (RecipeHandler.getChemicalCrystallizerRecipe(antimatter) == null)
        {
            RecipeHandler.addChemicalCrystallizerRecipe(antimatter.ingredient,
                  new ItemStack(NuclearItems.AntimatterPellet));
        }
        for (ItemStack pellet : materials.get(NuclearOreDictionary.PELLET_ANTIMATTER))
        {
            ItemStackInput input = new ItemStackInput(sized(pellet, 1));
            if (RecipeHandler.getOxidizerRecipe(input) == null)
            {
                RecipeHandler.addChemicalOxidizerRecipe(input.ingredient,
                      new GasStack(NuclearChemicals.Antimatter, 1000));
            }
        }

        Gas hydrogenChloride = GasRegistry.getGas("hydrogenChloride");
        if (hydrogenChloride != null)
        {
            for (ItemStack pellet : materials.get(NuclearOreDictionary.PELLET_PLUTONIUM))
            {
                ItemStack input = sized(pellet, 1);
                AdvancedMachineInput recipeInput =
                      new AdvancedMachineInput(input, hydrogenChloride);
                if (RecipeHandler.getRecipe(recipeInput,
                      RecipeHandler.Recipe.CHEMICAL_INJECTION_CHAMBER.get()) == null)
                {
                    RecipeHandler.addChemicalInjectionChamberRecipe(input,
                          hydrogenChloride.getName(),
                          new ItemStack(NuclearItems.ReprocessedFissileFragment, 4));
                }
            }
        }

        ItemStackInput reprocessedFragment = new ItemStackInput(
              new ItemStack(NuclearItems.ReprocessedFissileFragment));
        if (RecipeHandler.getOxidizerRecipe(reprocessedFragment) == null)
        {
            RecipeHandler.addChemicalOxidizerRecipe(reprocessedFragment.ingredient,
                  new GasStack(NuclearChemicals.FissileFuel, 2000));
        }
    }

    private static void addPelletReactionIfAbsent(ItemStack fluoriteDust,
          Gas inputGas, ItemStack output, ReactionRegistry reactions)
    {
        reactions.addIfAbsent(fluoriteDust, 1000, inputGas, 1000, output,
              new GasStack(NuclearChemicals.SpentNuclearWaste, 1000), 0, 100);
    }

    private static void addEnrichmentIfAbsent(ItemStack input, ItemStack output)
    {
        ItemStackInput recipeInput = new ItemStackInput(input);
        if (RecipeHandler.getRecipe(recipeInput,
              RecipeHandler.Recipe.ENRICHMENT_CHAMBER.get()) == null)
        {
            RecipeHandler.addEnrichmentChamberRecipe(input, output);
        }
    }

    private static void addCrusherIfAbsent(ItemStack input, ItemStack output)
    {
        ItemStackInput recipeInput = new ItemStackInput(input);
        if (RecipeHandler.getRecipe(recipeInput,
              RecipeHandler.Recipe.CRUSHER.get()) == null)
        {
            RecipeHandler.addCrusherRecipe(input, output);
        }
    }

    private static void addDissolutionIfAbsent(ItemStack input, GasStack output)
    {
        addDissolutionIfAbsent(input, output, 1);
    }

    private static void addDissolutionIfAbsent(ItemStack input, GasStack output,
          int baseChemicalUsage)
    {
        ItemStackInput recipeInput = new ItemStackInput(input);
        if (RecipeHandler.getDissolutionRecipe(recipeInput) == null)
        {
            RecipeHandler.addChemicalDissolutionChamberRecipe(
                  input, output, baseChemicalUsage);
        }
    }

    private static void addSmeltingIfAbsent(ItemStack input, ItemStack output,
          float experience, SmeltingRegistry smelting)
    {
        if (smelting.getResult(input) == null)
        {
            smelting.add(input, output, experience);
        }
    }

    interface MaterialResolver
    {
        List<ItemStack> get(String oreName);
    }

    interface SmeltingRegistry
    {
        ItemStack getResult(ItemStack input);

        void add(ItemStack input, ItemStack output, float experience);
    }

    interface ReactionRegistry
    {
        void addIfAbsent(ItemStack inputSolid, int waterAmount, Gas inputGas,
              int gasAmount, ItemStack outputSolid, GasStack outputGas,
              double extraEnergy, int ticks);
    }

    private static ItemStack sized(ItemStack stack, int amount)
    {
        ItemStack copy = stack.copy();
        copy.stackSize = amount;
        return copy;
    }
}
