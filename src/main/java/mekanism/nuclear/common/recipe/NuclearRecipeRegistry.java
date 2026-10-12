package mekanism.nuclear.common.recipe;

import java.util.ArrayList;
import java.util.List;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;
import mekanism.ultimate.api.recipe.ingredient.ItemIngredientCE;
import mekanism.ultimate.api.recipe.input.ItemChemicalInputCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.CachedRecipeManagerCE;
import mekanism.ultimate.common.recipe.RecipeManagersCE;
import mekanism.ultimate.common.recipe.key.ChemicalIdentityKeyCE;
import mekanism.ultimate.common.recipe.key.ItemIdentityKeyCE;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/** Stable Nuclear gas-to-gas recipes available to machines and integrations. */
public final class NuclearRecipeRegistry
{
    public static final CachedRecipeManagerCE<IChemicalStackCE, ChemicalIdentityKeyCE,
          ChemicalToChemicalRecipeCE> CENTRIFUGING = RecipeManagersCE.chemicalToChemical();
    public static final CachedRecipeManagerCE<ItemChemicalInputCE, ItemIdentityKeyCE,
          ItemChemicalToItemRecipeCE> NUCLEOSYNTHESIZING =
                RecipeManagersCE.itemChemicalToItem();

    private NuclearRecipeRegistry()
    {
    }

    public static synchronized void registerDefaults()
    {
        if (NuclearChemicals.UraniumHexafluoride == null)
        {
            throw new IllegalStateException("Nuclear chemicals must be registered before recipes");
        }

        List<ChemicalToChemicalRecipeCE> missing = new ArrayList<ChemicalToChemicalRecipeCE>();
        addIfMissing(missing, recipe("fissile_fuel", NuclearChemicals.UraniumHexafluoride, 1,
              NuclearChemicals.FissileFuel, 1));
        addIfMissing(missing, recipe("plutonium", NuclearChemicals.NuclearWaste, 10,
              NuclearChemicals.Plutonium, 1));
        CENTRIFUGING.addAll(missing);
        // Plain 1.7 unit tests cannot bootstrap Forge's registries without a
        // LaunchClassLoader. The dedicated-server path always has vanilla
        // items registered before mod pre-initialization.
        if (Items.coal != null)
        {
            registerNucleosynthesizing(type(NuclearChemicals.Antimatter));
        }
    }

    public static boolean containsInput(String gasName)
    {
        Gas gas = gasName == null ? null : GasRegistry.getGas(gasName);
        return gas != null && CENTRIFUGING.findFirst(new ChemicalStackCE(type(gas), 1)) != null;
    }

    public static ItemChemicalToItemRecipeCE findNucleosynthesizing(
          ItemStack item, IChemicalStackCE chemical)
    {
        return NUCLEOSYNTHESIZING.findFirst(
              new ItemChemicalInputCE(item, chemical));
    }

    public static boolean containsNucleosynthesizingInput(ItemStack item)
    {
        return item != null && item.getItem() != null && item.stackSize > 0
              && NuclearChemicals.Antimatter != null
              && findNucleosynthesizing(item, new ChemicalStackCE(
                    type(NuclearChemicals.Antimatter), 1)) != null;
    }

    private static void addIfMissing(List<ChemicalToChemicalRecipeCE> missing,
          ChemicalToChemicalRecipeCE recipe)
    {
        if (CENTRIFUGING.get(recipe.getId()) == null)
        {
            missing.add(recipe);
        }
    }

    /** Registers every stable recipe with an exact Minecraft 1.7.10 identity. */
    private static void registerNucleosynthesizing(
          MekGasChemicalType antimatter)
    {
        List<ItemChemicalToItemRecipeCE> missing =
              new ArrayList<ItemChemicalToItemRecipeCE>();
        for (NucleosynthesizingRecipeSpec spec
              : NucleosynthesizingRecipeSpec.values())
        {
            addIfMissing(missing, createNucleosynthesizingRecipe(
                  spec, antimatter));
        }
        NUCLEOSYNTHESIZING.addAll(missing);
    }

    private static ItemChemicalToItemRecipeCE createNucleosynthesizingRecipe(
          NucleosynthesizingRecipeSpec spec, MekGasChemicalType antimatter)
    {
        ItemIngredientCE input;
        ItemStack output;
        switch (spec)
        {
            case WITHER_SKELETON_SKULL:
                input = direct(Items.skull, spec.getInputMetadata());
                output = new ItemStack(Items.skull, 1,
                      spec.getOutputMetadata());
                break;
            case DIAMOND:
                input = direct(Items.coal, spec.getInputMetadata());
                output = new ItemStack(Items.diamond);
                break;
            case EMERALD:
                input = direct(Items.diamond, spec.getInputMetadata());
                output = new ItemStack(Items.emerald);
                break;
            case DRAGON_EGG:
                input = direct(Items.egg, spec.getInputMetadata());
                output = new ItemStack(Blocks.dragon_egg);
                break;
            case ENCHANTED_GOLDEN_APPLE:
                input = direct(Items.golden_apple,
                      spec.getInputMetadata());
                output = new ItemStack(Items.golden_apple, 1,
                      spec.getOutputMetadata());
                break;
            case REDSTONE_BLOCK:
                input = direct(Blocks.wool, spec.getInputMetadata());
                output = new ItemStack(Blocks.redstone_block);
                break;
            case GLOWSTONE_BLOCK:
                input = direct(Blocks.wool, spec.getInputMetadata());
                output = new ItemStack(Blocks.glowstone);
                break;
            case LAPIS_BLOCK:
                input = direct(Blocks.wool, spec.getInputMetadata());
                output = new ItemStack(Blocks.lapis_block);
                break;
            case QUARTZ_BLOCK:
                input = direct(Blocks.wool, spec.getInputMetadata());
                output = new ItemStack(Blocks.quartz_block);
                break;
            case ENDER_CHEST:
                input = ItemIngredientCE.ore("chestWood", 1);
                output = new ItemStack(Blocks.ender_chest);
                break;
            case IRON:
                input = ItemIngredientCE.ore("ingotTin", 1);
                output = new ItemStack(Items.iron_ingot);
                break;
            default:
                throw new IllegalArgumentException("Unknown recipe: " + spec);
        }
        return nucleosynthRecipe(spec.getPath(), input, antimatter,
              spec.getChemicalAmount(), output, spec.getDuration());
    }

    private static ItemIngredientCE direct(net.minecraft.item.Item item,
          int metadata)
    {
        return ItemIngredientCE.direct(new ItemStack(item, 1, metadata),
              false);
    }

    private static ItemIngredientCE direct(net.minecraft.block.Block block,
          int metadata)
    {
        return ItemIngredientCE.direct(new ItemStack(block, 1, metadata),
              false);
    }

    private static void addIfMissing(
          List<ItemChemicalToItemRecipeCE> missing,
          ItemChemicalToItemRecipeCE recipe)
    {
        if (NUCLEOSYNTHESIZING.get(recipe.getId()) == null)
        {
            missing.add(recipe);
        }
    }

    private static ItemChemicalToItemRecipeCE nucleosynthRecipe(String path,
          ItemIngredientCE item, MekGasChemicalType chemical,
          long chemicalAmount, ItemStack output, int duration)
    {
        return new ItemChemicalToItemRecipeCE(new ResourceLocation(
              MekanismNuclear.MODID, "nucleosynthesizing/" + path), item,
              new ChemicalIngredientCE(chemical, chemicalAmount), output,
              duration);
    }

    private static ChemicalToChemicalRecipeCE recipe(String path, Gas input, long inputAmount,
          Gas output, long outputAmount)
    {
        return new ChemicalToChemicalRecipeCE(
              new ResourceLocation(MekanismNuclear.MODID, "centrifuging/" + path),
              new ChemicalIngredientCE(type(input), inputAmount),
              new ChemicalStackCE(type(output), outputAmount));
    }

    public static MekGasChemicalType type(Gas gas)
    {
        return new MekGasChemicalType(gas, NuclearChemicals.isRadioactive(gas.getName()));
    }

    /** Pure recipe contracts kept visible to tests that cannot boot Forge. */
    enum NucleosynthesizingRecipeSpec
    {
        WITHER_SKELETON_SKULL("wither_skeleton_skull", 5, 1250, 0, 1),
        DIAMOND("diamond", 4, 1000, 0, 0),
        EMERALD("emerald", 4, 1000, 0, 0),
        DRAGON_EGG("dragon_egg", 4, 1000, 0, 0),
        ENCHANTED_GOLDEN_APPLE("enchanted_golden_apple", 3, 750, 0, 1),
        REDSTONE_BLOCK("redstone_block", 2, 500, 14, 0),
        GLOWSTONE_BLOCK("glowstone_block", 2, 500, 4, 0),
        LAPIS_BLOCK("lapis_block", 2, 500, 11, 0),
        QUARTZ_BLOCK("quartz_block", 2, 500, 8, 0),
        ENDER_CHEST("ender_chest", 2, 500, 0, 0),
        IRON("iron", 1, 200, 0, 0);

        private final String path;
        private final long chemicalAmount;
        private final int duration;
        private final int inputMetadata;
        private final int outputMetadata;

        NucleosynthesizingRecipeSpec(String path, long chemicalAmount,
              int duration, int inputMetadata, int outputMetadata)
        {
            this.path = path;
            this.chemicalAmount = chemicalAmount;
            this.duration = duration;
            this.inputMetadata = inputMetadata;
            this.outputMetadata = outputMetadata;
        }

        String getPath()
        {
            return path;
        }

        long getChemicalAmount()
        {
            return chemicalAmount;
        }

        int getDuration()
        {
            return duration;
        }

        int getInputMetadata()
        {
            return inputMetadata;
        }

        int getOutputMetadata()
        {
            return outputMetadata;
        }
    }
}
