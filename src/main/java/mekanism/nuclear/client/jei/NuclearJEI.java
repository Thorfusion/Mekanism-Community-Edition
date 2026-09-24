package mekanism.nuclear.client.jei;

import java.util.stream.Collectors;
import mekanism.nuclear.client.gui.GuiIsotopicCentrifuge;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.recipe.NuclearRecipeRegistry;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class NuclearJEI implements IModPlugin {

    public static final String UID = MekanismNuclear.MODID + ".centrifuging";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new CentrifugingRecipeCategory(helper));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(NuclearRecipeRegistry.CENTRIFUGING.getRecipes().stream()
              .map(CentrifugingRecipeWrapper::new).collect(Collectors.toList()), UID);
        registry.addRecipeCatalyst(new ItemStack(NuclearBlocks.IsotopicCentrifuge), UID);
        registry.addRecipeClickArea(GuiIsotopicCentrifuge.class, 64, 39, 48, 8, UID);
    }
}
