package mekanism.ultimate.client.jei;

import java.util.ArrayList;
import java.util.List;
import mekanism.ultimate.client.gui.GuiNutritionalLiquifier;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.UltimateBlocks;
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public final class UltimateJEI implements IModPlugin {

    public static final String UID = MekanismUltimate.MODID + ".nutritional_liquification";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new NutritionalLiquifierRecipeCategory(helper));
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
    }
}
