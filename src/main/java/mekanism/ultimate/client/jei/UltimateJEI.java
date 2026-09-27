package mekanism.ultimate.client.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import mekanism.ultimate.client.gui.GuiPigmentMachine;
import mekanism.ultimate.client.gui.GuiNutritionalLiquifier;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.UltimateBlocks;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.PigmentTypeCE;
import mekanism.ultimate.common.recipe.UltimatePigmentRecipes;
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public final class UltimateJEI implements IModPlugin {

    public static final String UID = MekanismUltimate.MODID + ".nutritional_liquification";
    public static final String PIGMENT_EXTRACTING = MekanismUltimate.MODID + ".pigment_extracting";
    public static final String PIGMENT_MIXING = MekanismUltimate.MODID + ".pigment_mixing";
    public static final String PAINTING = MekanismUltimate.MODID + ".painting";
    public static final IIngredientType<ChemicalStackCE> TYPE_CHEMICAL = () -> ChemicalStackCE.class;

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
        List<ChemicalStackCE> pigments = Arrays.stream(PigmentTypeCE.values())
              .map(pigment -> new ChemicalStackCE(pigment, 1_000))
              .collect(Collectors.toList());
        registry.register(TYPE_CHEMICAL, pigments, new ChemicalStackHelper(),
              new ChemicalStackRenderer());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new NutritionalLiquifierRecipeCategory(helper),
              new PigmentRecipeCategory(helper, PigmentRecipeCategory.Mode.EXTRACTING),
              new PigmentRecipeCategory(helper, PigmentRecipeCategory.Mode.MIXING),
              new PigmentRecipeCategory(helper, PigmentRecipeCategory.Mode.PAINTING));
    }

    @Override
    public void register(IModRegistry registry) {
        List<NutritionalLiquifierRecipeWrapper> recipes = new ArrayList<>();
        for (ItemStack stack : registry.getIngredientRegistry().getAllIngredients(VanillaTypes.ITEM)) {
            if (TileEntityNutritionalLiquifier.getPasteOutput(stack) > 0) {
                recipes.add(new NutritionalLiquifierRecipeWrapper(stack));
            }
        }
        registry.addRecipes(recipes, UID);
        registry.addRecipeCatalyst(new ItemStack(UltimateBlocks.NutritionalLiquifier), UID);
        registry.addRecipeClickArea(GuiNutritionalLiquifier.class, 50, 39, 52, 10, UID);

        registry.addRecipes(UltimatePigmentRecipes.EXTRACTING.getRecipes().stream()
              .map(PigmentRecipeWrapper::new).collect(Collectors.toList()), PIGMENT_EXTRACTING);
        registry.addRecipes(UltimatePigmentRecipes.MIXING.getRecipes().stream()
              .map(PigmentRecipeWrapper::new).collect(Collectors.toList()), PIGMENT_MIXING);
        registry.addRecipes(UltimatePigmentRecipes.PAINTING.getRecipes().stream()
              .map(PigmentRecipeWrapper::new).collect(Collectors.toList()), PAINTING);
        registry.addRecipeCatalyst(new ItemStack(UltimateBlocks.PigmentExtractor), PIGMENT_EXTRACTING);
        registry.addRecipeCatalyst(new ItemStack(UltimateBlocks.PigmentMixer), PIGMENT_MIXING);
        registry.addRecipeCatalyst(new ItemStack(UltimateBlocks.PaintingMachine), PAINTING);
        registry.addRecipeClickArea(GuiPigmentMachine.class, 47, 39, 82, 10,
              PIGMENT_EXTRACTING, PIGMENT_MIXING, PAINTING);
    }
}
