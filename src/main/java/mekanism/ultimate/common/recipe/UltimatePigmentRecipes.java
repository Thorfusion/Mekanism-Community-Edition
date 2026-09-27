package mekanism.ultimate.common.recipe;

import java.util.ArrayList;
import java.util.List;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;
import mekanism.ultimate.api.recipe.ingredient.ItemIngredientCE;
import mekanism.ultimate.api.recipe.input.ChemicalChemicalInputCE;
import mekanism.ultimate.api.recipe.input.ItemChemicalInputCE;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.PigmentTypeCE;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import mekanism.ultimate.common.recipe.key.ChemicalIdentityKeyCE;
import mekanism.ultimate.common.recipe.key.ItemIdentityKeyCE;
import mekanism.ultimate.common.recipe.type.ChemicalChemicalToChemicalRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemToChemicalRecipeCE;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

/** Stable pigment identities and all 1.12-representable base color recipes. */
public final class UltimatePigmentRecipes {

    public static final CachedRecipeManagerCE<ItemStack, ItemIdentityKeyCE, ItemToChemicalRecipeCE> EXTRACTING =
          RecipeManagersCE.itemToChemical();
    public static final CachedRecipeManagerCE<ChemicalChemicalInputCE, ChemicalIdentityKeyCE,
          ChemicalChemicalToChemicalRecipeCE> MIXING = RecipeManagersCE.chemicalChemicalToChemical();
    public static final CachedRecipeManagerCE<ItemChemicalInputCE, ItemIdentityKeyCE,
          ItemChemicalToItemRecipeCE> PAINTING = RecipeManagersCE.itemChemicalToItem();

    private UltimatePigmentRecipes() {
    }

    public static synchronized void registerDefaults() {
        for (PigmentTypeCE pigment : PigmentTypeCE.values()) {
            UltimateChemicalRegistry.INSTANCE.register(pigment);
        }
        if (EXTRACTING.size() != 0 || MIXING.size() != 0 || PAINTING.size() != 0) {
            return;
        }

        List<ItemToChemicalRecipeCE> extracting = new ArrayList<>();
        List<ItemChemicalToItemRecipeCE> painting = new ArrayList<>();
        Block[] colorBlocks = {
              Blocks.WOOL, Blocks.CARPET, Blocks.STAINED_GLASS, Blocks.STAINED_GLASS_PANE,
              Blocks.STAINED_HARDENED_CLAY, Blocks.CONCRETE, Blocks.CONCRETE_POWDER
        };
        String[] blockNames = {
              "wool", "carpet", "glass", "glass_pane", "terracotta", "concrete", "concrete_powder"
        };

        for (EnumDyeColor dye : EnumDyeColor.values()) {
            PigmentTypeCE pigment = PigmentTypeCE.byDye(dye);
            if (pigment == null) {
                continue;
            }
            extracting.add(extract("dye/" + pigment.getPath(),
                  new ItemStack(Items.DYE, 1, dye.getDyeDamage()), pigment, 256));
            painting.add(paint("dye/" + pigment.getPath(),
                  wildcard(Items.DYE), pigment, new ItemStack(Items.DYE, 1, dye.getDyeDamage())));
            painting.add(paint("bed/" + pigment.getPath(),
                  wildcard(Items.BED), pigment, new ItemStack(Items.BED, 1, dye.getMetadata())));

            for (int i = 0; i < colorBlocks.length; i++) {
                ItemStack colored = new ItemStack(colorBlocks[i], 1, dye.getMetadata());
                extracting.add(extract(blockNames[i] + "/" + pigment.getPath(), colored, pigment, 192));
                painting.add(paint(blockNames[i] + "/" + pigment.getPath(),
                      new ItemStack(colorBlocks[i], 1, OreDictionary.WILDCARD_VALUE), pigment, colored));
            }
        }
        EXTRACTING.addAll(extracting);
        PAINTING.addAll(painting);

        List<ChemicalChemicalToChemicalRecipeCE> mixing = new ArrayList<>();
        mix(mixing, "aqua_yellow_to_lime", PigmentTypeCE.AQUA, PigmentTypeCE.YELLOW, PigmentTypeCE.LIME);
        mix(mixing, "black_red_to_dark_red", PigmentTypeCE.BLACK, PigmentTypeCE.RED, PigmentTypeCE.DARK_RED);
        mix(mixing, "black_white_to_gray", PigmentTypeCE.BLACK, PigmentTypeCE.WHITE, PigmentTypeCE.GRAY);
        mix(mixing, "blue_green_to_cyan", PigmentTypeCE.BLUE, PigmentTypeCE.GREEN, PigmentTypeCE.CYAN);
        mix(mixing, "blue_red_to_purple", PigmentTypeCE.BLUE, PigmentTypeCE.RED, PigmentTypeCE.PURPLE);
        mix(mixing, "blue_white_to_light_blue", PigmentTypeCE.BLUE, PigmentTypeCE.WHITE, PigmentTypeCE.LIGHT_BLUE);
        mix(mixing, "blue_yellow_to_green", PigmentTypeCE.BLUE, PigmentTypeCE.YELLOW, PigmentTypeCE.GREEN);
        mix(mixing, "cyan_white_to_aqua", PigmentTypeCE.CYAN, PigmentTypeCE.WHITE, PigmentTypeCE.AQUA);
        mix(mixing, "gray_red_to_dark_red", PigmentTypeCE.GRAY, PigmentTypeCE.RED, PigmentTypeCE.DARK_RED);
        mix(mixing, "gray_white_to_light_gray", PigmentTypeCE.GRAY, PigmentTypeCE.WHITE, PigmentTypeCE.LIGHT_GRAY);
        mix(mixing, "green_white_to_lime", PigmentTypeCE.GREEN, PigmentTypeCE.WHITE, PigmentTypeCE.LIME);
        mix(mixing, "light_blue_lime_to_aqua", PigmentTypeCE.LIGHT_BLUE, PigmentTypeCE.LIME, PigmentTypeCE.AQUA);
        mix(mixing, "light_blue_red_to_magenta", PigmentTypeCE.LIGHT_BLUE, PigmentTypeCE.RED, PigmentTypeCE.MAGENTA);
        mix(mixing, "purple_pink_to_magenta", PigmentTypeCE.PURPLE, PigmentTypeCE.PINK, PigmentTypeCE.MAGENTA);
        mix(mixing, "red_white_to_pink", PigmentTypeCE.RED, PigmentTypeCE.WHITE, PigmentTypeCE.PINK);
        mix(mixing, "red_yellow_to_orange", PigmentTypeCE.RED, PigmentTypeCE.YELLOW, PigmentTypeCE.ORANGE);
        MIXING.addAll(mixing);
    }

    private static ItemToChemicalRecipeCE extract(String path, ItemStack input,
          PigmentTypeCE output, long amount) {
        return new ItemToChemicalRecipeCE(id("pigment_extracting/" + path),
              ItemIngredientCE.direct(input, false), new ChemicalStackCE(output, amount));
    }

    private static ItemChemicalToItemRecipeCE paint(String path, ItemStack input,
          PigmentTypeCE pigment, ItemStack output) {
        return new ItemChemicalToItemRecipeCE(id("painting/" + path),
              ItemIngredientCE.direct(input, false), new ChemicalIngredientCE(pigment, 256),
              output, 200);
    }

    private static void mix(List<ChemicalChemicalToChemicalRecipeCE> recipes, String path,
          PigmentTypeCE left, PigmentTypeCE right, PigmentTypeCE output) {
        recipes.add(new ChemicalChemicalToChemicalRecipeCE(id("pigment_mixing/" + path),
              new ChemicalIngredientCE(left, 1), new ChemicalIngredientCE(right, 1),
              new ChemicalStackCE(output, 2)));
    }

    private static ItemStack wildcard(Item item) {
        return new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(MekanismUltimate.MODID, path);
    }

    public static ItemToChemicalRecipeCE findExtracting(ItemStack input) {
        return input == null || input.isEmpty() ? null : EXTRACTING.findFirst(input);
    }

    public static ChemicalChemicalToChemicalRecipeCE findMixing(IChemicalStackCE left,
          IChemicalStackCE right) {
        return left == null || right == null ? null
              : MIXING.findFirst(new ChemicalChemicalInputCE(left, right));
    }

    public static ItemChemicalToItemRecipeCE findPainting(ItemStack item, IChemicalStackCE pigment) {
        return item == null || item.isEmpty() || pigment == null ? null
              : PAINTING.findFirst(new ItemChemicalInputCE(item, pigment));
    }

    /**
     * Checks only the item half of the painting recipes. This keeps hopper and
     * shift-click insertion useful before the machine has received pigment.
     */
    public static boolean containsPaintingInput(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        for (ItemChemicalToItemRecipeCE recipe : PAINTING.getRecipes()) {
            if (recipe.getItemInput().testType(item)) {
                return true;
            }
        }
        return false;
    }
}
