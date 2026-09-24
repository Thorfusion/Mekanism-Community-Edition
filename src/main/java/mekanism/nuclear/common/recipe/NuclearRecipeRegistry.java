package mekanism.nuclear.common.recipe;

import java.util.ArrayList;
import java.util.List;
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
import net.minecraftforge.oredict.OreDictionary;

/** Stable-modern Nuclear machine recipes that have valid 1.12 representations. */
public final class NuclearRecipeRegistry {

    public static final CachedRecipeManagerCE<IChemicalStackCE, ChemicalIdentityKeyCE, ChemicalToChemicalRecipeCE> CENTRIFUGING =
          RecipeManagersCE.chemicalToChemical();
    public static final CachedRecipeManagerCE<ItemChemicalInputCE, ItemIdentityKeyCE, ItemChemicalToItemRecipeCE> NUCLEOSYNTHESIZING =
          RecipeManagersCE.itemChemicalToItem();

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
        registerNucleosynthesizing(type(NuclearChemicals.Antimatter.getName()));
    }

    public static boolean containsInput(String gasName) {
        if (gasName == null || mekanism.api.gas.GasRegistry.getGas(gasName) == null) {
            return false;
        }
        return CENTRIFUGING.findFirst(new ChemicalStackCE(type(gasName), 1)) != null;
    }

    public static ItemChemicalToItemRecipeCE findNucleosynthesizing(ItemStack item, IChemicalStackCE chemical) {
        return NUCLEOSYNTHESIZING.findFirst(new ItemChemicalInputCE(item, chemical));
    }

    public static boolean containsNucleosynthesizingInput(ItemStack item) {
        if (item == null || item.isEmpty() || NuclearChemicals.Antimatter == null) {
            return false;
        }
        return findNucleosynthesizing(item,
              new ChemicalStackCE(type(NuclearChemicals.Antimatter.getName()), 1)) != null;
    }

    private static void registerNucleosynthesizing(MekGasChemicalType antimatter) {
        List<ItemChemicalToItemRecipeCE> missing = new ArrayList<>();
        addIfMissing(missing, nucleosynthRecipe("wither_skeleton_skull",
              ItemIngredientCE.direct(new ItemStack(Items.SKULL, 1, 0), false), antimatter, 5,
              new ItemStack(Items.SKULL, 1, 1), 1_250));
        addIfMissing(missing, nucleosynthRecipe("diamond",
              ItemIngredientCE.direct(new ItemStack(Items.COAL, 1, 0), false), antimatter, 4,
              new ItemStack(Items.DIAMOND), 1_000));
        addIfMissing(missing, nucleosynthRecipe("emerald",
              ItemIngredientCE.direct(new ItemStack(Items.DIAMOND), false), antimatter, 4,
              new ItemStack(Items.EMERALD), 1_000));
        addIfMissing(missing, nucleosynthRecipe("dragon_egg",
              ItemIngredientCE.direct(new ItemStack(Items.EGG), false), antimatter, 4,
              new ItemStack(Blocks.DRAGON_EGG), 1_000));
        addIfMissing(missing, nucleosynthRecipe("end_crystal",
              ItemIngredientCE.direct(new ItemStack(Blocks.BEACON), false), antimatter, 3,
              new ItemStack(Items.END_CRYSTAL), 750));
        addIfMissing(missing, nucleosynthRecipe("enchanted_golden_apple",
              ItemIngredientCE.direct(new ItemStack(Items.GOLDEN_APPLE, 1, 0), false), antimatter, 3,
              new ItemStack(Items.GOLDEN_APPLE, 1, 1), 750));
        addIfMissing(missing, nucleosynthRecipe("redstone_block",
              ItemIngredientCE.direct(new ItemStack(Blocks.WOOL, 1, 14), false), antimatter, 2,
              new ItemStack(Blocks.REDSTONE_BLOCK), 500));
        addIfMissing(missing, nucleosynthRecipe("glowstone_block",
              ItemIngredientCE.direct(new ItemStack(Blocks.WOOL, 1, 4), false), antimatter, 2,
              new ItemStack(Blocks.GLOWSTONE), 500));
        addIfMissing(missing, nucleosynthRecipe("lapis_block",
              ItemIngredientCE.direct(new ItemStack(Blocks.WOOL, 1, 11), false), antimatter, 2,
              new ItemStack(Blocks.LAPIS_BLOCK), 500));
        addIfMissing(missing, nucleosynthRecipe("quartz_block",
              ItemIngredientCE.direct(new ItemStack(Blocks.WOOL, 1, 8), false), antimatter, 2,
              new ItemStack(Blocks.QUARTZ_BLOCK), 500));
        addIfMissing(missing, nucleosynthRecipe("chorus_flower",
              ItemIngredientCE.anyOf(
                    new ItemStack(Blocks.YELLOW_FLOWER, 1, OreDictionary.WILDCARD_VALUE),
                    new ItemStack(Blocks.RED_FLOWER, 1, OreDictionary.WILDCARD_VALUE)), antimatter, 2,
              new ItemStack(Blocks.CHORUS_FLOWER), 500));
        addIfMissing(missing, nucleosynthRecipe("ender_chest",
              ItemIngredientCE.ore("chestWood", 1), antimatter, 2,
              new ItemStack(Blocks.ENDER_CHEST), 500));
        addIfMissing(missing, nucleosynthRecipe("iron",
              ItemIngredientCE.ore("ingotTin", 1), antimatter, 1,
              new ItemStack(Items.IRON_INGOT), 200));
        NUCLEOSYNTHESIZING.addAll(missing);
    }

    private static void addIfMissing(List<ItemChemicalToItemRecipeCE> missing, ItemChemicalToItemRecipeCE recipe) {
        if (NUCLEOSYNTHESIZING.get(recipe.getId()) == null) {
            missing.add(recipe);
        }
    }

    private static ItemChemicalToItemRecipeCE nucleosynthRecipe(String path, ItemIngredientCE item,
          MekGasChemicalType chemical, long chemicalAmount, ItemStack output, int duration) {
        return new ItemChemicalToItemRecipeCE(
              new ResourceLocation(MekanismNuclear.MODID, "nucleosynthesizing/" + path), item,
              new ChemicalIngredientCE(chemical, chemicalAmount), output, duration);
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
