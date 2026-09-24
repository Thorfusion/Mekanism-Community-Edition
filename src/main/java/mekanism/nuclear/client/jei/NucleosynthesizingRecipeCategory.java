package mekanism.nuclear.client.jei;

import mekanism.api.gas.GasStack;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

public class NucleosynthesizingRecipeCategory extends BaseRecipeCategory<NucleosynthesizingRecipeWrapper> {

    public NucleosynthesizingRecipeCategory(IGuiHelper helper) {
        super(helper, "mekanism:gui/GuiAdvancedMachine.png", NuclearJEI.NUCLEOSYNTHESIZING_UID,
              "gui.jei.category.nucleosynthesizing", null, 28, 16, 144, 54);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        super.drawExtras(minecraft);
        drawTexturedRect(77 - xOffset, 37 - yOffset, 176, 0, 28, 8);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, NucleosynthesizingRecipeWrapper wrapper, IIngredients ingredients) {
        ItemChemicalToItemRecipeCE recipe = wrapper.getRecipe();
        IGuiItemStackGroup items = layout.getItemStacks();
        items.init(0, true, 27, 0);
        items.init(1, false, 87, 18);
        items.set(0, recipe.getItemInput().getRepresentations());
        items.set(1, recipe.getOutput());

        MekGasChemicalType chemical = (MekGasChemicalType) recipe.getChemicalInput().getType();
        IGuiIngredientGroup<GasStack> gases = layout.getIngredientsGroup(MekanismJEI.TYPE_GAS);
        initGas(gases, 0, true, 33, 21, 6, 12,
              new GasStack(chemical.getGas(), (int) Math.min(Integer.MAX_VALUE,
                    recipe.getChemicalInput().getAmount())), false);
    }
}
