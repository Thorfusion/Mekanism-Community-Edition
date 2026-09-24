package mekanism.nuclear.client.jei;

import java.util.stream.Collectors;
import mekanism.nuclear.client.gui.GuiIsotopicCentrifuge;
import mekanism.nuclear.client.gui.GuiAntiprotonicNucleosynthesizer;
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
    public static final String NUCLEOSYNTHESIZING_UID = MekanismNuclear.MODID + ".nucleosynthesizing";

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new CentrifugingRecipeCategory(helper),
              new NucleosynthesizingRecipeCategory(helper));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(NuclearRecipeRegistry.CENTRIFUGING.getRecipes().stream()
              .map(CentrifugingRecipeWrapper::new).collect(Collectors.toList()), UID);
        registry.addRecipeCatalyst(new ItemStack(NuclearBlocks.IsotopicCentrifuge), UID);
        registry.addRecipeClickArea(GuiIsotopicCentrifuge.class, 64, 39, 48, 8, UID);
        registry.addRecipes(NuclearRecipeRegistry.NUCLEOSYNTHESIZING.getRecipes().stream()
              .map(NucleosynthesizingRecipeWrapper::new).collect(Collectors.toList()), NUCLEOSYNTHESIZING_UID);
        registry.addRecipeCatalyst(new ItemStack(NuclearBlocks.AntiprotonicNucleosynthesizer),
              NUCLEOSYNTHESIZING_UID);
        registry.addRecipeClickArea(GuiAntiprotonicNucleosynthesizer.class, 74, 39, 48, 8,
              NUCLEOSYNTHESIZING_UID);
    }
}
