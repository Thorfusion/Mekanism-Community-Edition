package mekanism.ultimate.common.recipe;

import java.util.ArrayList;
import java.util.List;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;
import mekanism.ultimate.api.recipe.ingredient.ItemIngredientCE;
import mekanism.ultimate.api.recipe.input.ChemicalChemicalInputCE;
import mekanism.ultimate.api.recipe.input.ItemChemicalInputCE;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

/** Stable pigments and all recipes representable with vanilla 1.7 colors. */
public final class UltimatePigmentRecipes
{
    static final long DYE_EXTRACTION_AMOUNT = 256;
    static final long BLOCK_EXTRACTION_AMOUNT = 192;
    static final long PAINTING_PIGMENT_AMOUNT = 256;
    static final int PAINTING_DURATION = 200;

    public static final CachedRecipeManagerCE<ItemStack, ItemIdentityKeyCE,
          ItemToChemicalRecipeCE> EXTRACTING =
                RecipeManagersCE.itemToChemical();
    public static final CachedRecipeManagerCE<ChemicalChemicalInputCE,
          ChemicalIdentityKeyCE, ChemicalChemicalToChemicalRecipeCE> MIXING =
                RecipeManagersCE.chemicalChemicalToChemical();
    public static final CachedRecipeManagerCE<ItemChemicalInputCE,
          ItemIdentityKeyCE, ItemChemicalToItemRecipeCE> PAINTING =
                RecipeManagersCE.itemChemicalToItem();

    private UltimatePigmentRecipes()
    {
    }

    public static synchronized void registerDefaults()
    {
        for (PigmentTypeCE pigment : PigmentTypeCE.values())
        {
            UltimateChemicalRegistry.INSTANCE.register(pigment);
        }
        // Plain unit tests do not run below LaunchWrapper and therefore
        // cannot bootstrap Forge's vanilla item registry. The game startup
        // path always has these identities before mod initialization.
        if (Items.dye == null)
        {
            return;
        }

        List<ItemToChemicalRecipeCE> extracting =
              new ArrayList<ItemToChemicalRecipeCE>();
        List<ItemChemicalToItemRecipeCE> painting =
              new ArrayList<ItemChemicalToItemRecipeCE>();

        for (PigmentTypeCE pigment : PigmentTypeCE.values())
        {
            if (!pigment.hasDye())
            {
                continue;
            }
            int dyeDamage = pigment.getDyeDamage();
            addExtractingIfMissing(extracting, extract(
                  "dye/" + pigment.getPath(),
                  new ItemStack(Items.dye, 1, dyeDamage), pigment,
                  DYE_EXTRACTION_AMOUNT));
            addPaintingIfMissing(painting, paint(
                  "dye/" + pigment.getPath(), wildcard(Items.dye), pigment,
                  new ItemStack(Items.dye, 1, dyeDamage)));

            for (LegacyColorTarget target : LegacyColorTarget.values())
            {
                ItemStack colored = new ItemStack(target.getBlock(), 1,
                      pigment.getBlockMetadata());
                addExtractingIfMissing(extracting, extract(
                      target.getPath() + "/" + pigment.getPath(), colored,
                      pigment, BLOCK_EXTRACTION_AMOUNT));
                addPaintingIfMissing(painting, paint(
                      target.getPath() + "/" + pigment.getPath(),
                      wildcard(target.getBlock()), pigment, colored));
            }
        }
        EXTRACTING.addAll(extracting);
        PAINTING.addAll(painting);

        List<ChemicalChemicalToChemicalRecipeCE> mixing =
              new ArrayList<ChemicalChemicalToChemicalRecipeCE>();
        for (PigmentMixingRecipeSpec spec
              : PigmentMixingRecipeSpec.values())
        {
            ChemicalChemicalToChemicalRecipeCE recipe = mix(spec);
            if (MIXING.get(recipe.getId()) == null)
            {
                mixing.add(recipe);
            }
        }
        MIXING.addAll(mixing);
    }

    static int getBaseExtractingRecipeCount()
    {
        return getLegacyDyePigmentCount()
              * (1 + LegacyColorTarget.values().length);
    }

    static int getBasePaintingRecipeCount()
    {
        return getBaseExtractingRecipeCount();
    }

    private static int getLegacyDyePigmentCount()
    {
        int count = 0;
        for (PigmentTypeCE pigment : PigmentTypeCE.values())
        {
            if (pigment.hasDye())
            {
                count++;
            }
        }
        return count;
    }

    private static void addExtractingIfMissing(
          List<ItemToChemicalRecipeCE> recipes,
          ItemToChemicalRecipeCE recipe)
    {
        if (EXTRACTING.get(recipe.getId()) == null)
        {
            recipes.add(recipe);
        }
    }

    private static void addPaintingIfMissing(
          List<ItemChemicalToItemRecipeCE> recipes,
          ItemChemicalToItemRecipeCE recipe)
    {
        if (PAINTING.get(recipe.getId()) == null)
        {
            recipes.add(recipe);
        }
    }

    static ItemToChemicalRecipeCE extract(String path, ItemStack input,
          PigmentTypeCE output, long amount)
    {
        return new ItemToChemicalRecipeCE(id("pigment_extracting/" + path),
              ItemIngredientCE.direct(input, false),
              new ChemicalStackCE(output, amount));
    }

    static ItemChemicalToItemRecipeCE paint(String path,
          ItemStack input, PigmentTypeCE pigment, ItemStack output)
    {
        return new ItemChemicalToItemRecipeCE(id("painting/" + path),
              ItemIngredientCE.direct(input, false),
              new ChemicalIngredientCE(pigment, PAINTING_PIGMENT_AMOUNT),
              output, PAINTING_DURATION);
    }

    static ChemicalChemicalToChemicalRecipeCE mix(
          PigmentMixingRecipeSpec spec)
    {
        return new ChemicalChemicalToChemicalRecipeCE(
              id("pigment_mixing/" + spec.getPath()),
              new ChemicalIngredientCE(spec.getLeft(), 1),
              new ChemicalIngredientCE(spec.getRight(), 1),
              new ChemicalStackCE(spec.getOutput(), 2));
    }

    private static ItemStack wildcard(Item item)
    {
        return new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
    }

    private static ItemStack wildcard(Block block)
    {
        return new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE);
    }

    private static ResourceLocation id(String path)
    {
        return new ResourceLocation("mekanismultimate:" + path);
    }

    public static ItemToChemicalRecipeCE findExtracting(ItemStack input)
    {
        return input == null || input.getItem() == null || input.stackSize <= 0
              ? null : EXTRACTING.findFirst(input);
    }

    public static ChemicalChemicalToChemicalRecipeCE findMixing(
          IChemicalStackCE left, IChemicalStackCE right)
    {
        return left == null || right == null ? null
              : MIXING.findFirst(new ChemicalChemicalInputCE(left, right));
    }

    public static ItemChemicalToItemRecipeCE findPainting(ItemStack item,
          IChemicalStackCE pigment)
    {
        return item == null || item.getItem() == null || item.stackSize <= 0
              || pigment == null ? null
                    : PAINTING.findFirst(
                          new ItemChemicalInputCE(item, pigment));
    }

    public static boolean containsPaintingInput(ItemStack item)
    {
        if (item == null || item.getItem() == null || item.stackSize <= 0)
        {
            return false;
        }
        for (ItemChemicalToItemRecipeCE recipe : PAINTING.getRecipes())
        {
            if (recipe.getItemInput().testType(item))
            {
                return true;
            }
        }
        return false;
    }

    enum LegacyColorTarget
    {
        WOOL("wool"),
        CARPET("carpet"),
        GLASS("glass"),
        GLASS_PANE("glass_pane"),
        TERRACOTTA("terracotta");

        private final String path;

        LegacyColorTarget(String path)
        {
            this.path = path;
        }

        String getPath()
        {
            return path;
        }

        Block getBlock()
        {
            switch (this)
            {
                case WOOL:
                    return Blocks.wool;
                case CARPET:
                    return Blocks.carpet;
                case GLASS:
                    return Blocks.stained_glass;
                case GLASS_PANE:
                    return Blocks.stained_glass_pane;
                case TERRACOTTA:
                    return Blocks.stained_hardened_clay;
                default:
                    throw new IllegalStateException("Unknown target: " + this);
            }
        }
    }

    enum PigmentMixingRecipeSpec
    {
        AQUA_YELLOW_TO_LIME("aqua_yellow_to_lime", PigmentTypeCE.AQUA,
              PigmentTypeCE.YELLOW, PigmentTypeCE.LIME),
        BLACK_RED_TO_DARK_RED("black_red_to_dark_red", PigmentTypeCE.BLACK,
              PigmentTypeCE.RED, PigmentTypeCE.DARK_RED),
        BLACK_WHITE_TO_GRAY("black_white_to_gray", PigmentTypeCE.BLACK,
              PigmentTypeCE.WHITE, PigmentTypeCE.GRAY),
        BLUE_GREEN_TO_CYAN("blue_green_to_cyan", PigmentTypeCE.BLUE,
              PigmentTypeCE.GREEN, PigmentTypeCE.CYAN),
        BLUE_RED_TO_PURPLE("blue_red_to_purple", PigmentTypeCE.BLUE,
              PigmentTypeCE.RED, PigmentTypeCE.PURPLE),
        BLUE_WHITE_TO_LIGHT_BLUE("blue_white_to_light_blue",
              PigmentTypeCE.BLUE, PigmentTypeCE.WHITE,
              PigmentTypeCE.LIGHT_BLUE),
        BLUE_YELLOW_TO_GREEN("blue_yellow_to_green", PigmentTypeCE.BLUE,
              PigmentTypeCE.YELLOW, PigmentTypeCE.GREEN),
        CYAN_WHITE_TO_AQUA("cyan_white_to_aqua", PigmentTypeCE.CYAN,
              PigmentTypeCE.WHITE, PigmentTypeCE.AQUA),
        GRAY_RED_TO_DARK_RED("gray_red_to_dark_red", PigmentTypeCE.GRAY,
              PigmentTypeCE.RED, PigmentTypeCE.DARK_RED),
        GRAY_WHITE_TO_LIGHT_GRAY("gray_white_to_light_gray",
              PigmentTypeCE.GRAY, PigmentTypeCE.WHITE,
              PigmentTypeCE.LIGHT_GRAY),
        GREEN_WHITE_TO_LIME("green_white_to_lime", PigmentTypeCE.GREEN,
              PigmentTypeCE.WHITE, PigmentTypeCE.LIME),
        LIGHT_BLUE_LIME_TO_AQUA("light_blue_lime_to_aqua",
              PigmentTypeCE.LIGHT_BLUE, PigmentTypeCE.LIME,
              PigmentTypeCE.AQUA),
        LIGHT_BLUE_RED_TO_MAGENTA("light_blue_red_to_magenta",
              PigmentTypeCE.LIGHT_BLUE, PigmentTypeCE.RED,
              PigmentTypeCE.MAGENTA),
        PURPLE_PINK_TO_MAGENTA("purple_pink_to_magenta",
              PigmentTypeCE.PURPLE, PigmentTypeCE.PINK,
              PigmentTypeCE.MAGENTA),
        RED_WHITE_TO_PINK("red_white_to_pink", PigmentTypeCE.RED,
              PigmentTypeCE.WHITE, PigmentTypeCE.PINK),
        RED_YELLOW_TO_ORANGE("red_yellow_to_orange", PigmentTypeCE.RED,
              PigmentTypeCE.YELLOW, PigmentTypeCE.ORANGE);

        private final String path;
        private final PigmentTypeCE left;
        private final PigmentTypeCE right;
        private final PigmentTypeCE output;

        PigmentMixingRecipeSpec(String path, PigmentTypeCE left,
              PigmentTypeCE right, PigmentTypeCE output)
        {
            this.path = path;
            this.left = left;
            this.right = right;
            this.output = output;
        }

        String getPath()
        {
            return path;
        }

        PigmentTypeCE getLeft()
        {
            return left;
        }

        PigmentTypeCE getRight()
        {
            return right;
        }

        PigmentTypeCE getOutput()
        {
            return output;
        }
    }
}
