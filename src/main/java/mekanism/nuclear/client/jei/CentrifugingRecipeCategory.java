package mekanism.nuclear.client.jei;

import mekanism.api.gas.GasStack;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

public class CentrifugingRecipeCategory extends BaseRecipeCategory<CentrifugingRecipeWrapper> {

    public CentrifugingRecipeCategory(IGuiHelper helper) {
        super(helper, "mekanism:gui/nei/GuiSolarNeutronActivator.png", NuclearJEI.UID,
              "gui.jei.category.centrifuging", null, 3, 12, 170, 70);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        super.drawExtras(minecraft);
        drawTexturedRect(64 - xOffset, 39 - yOffset, 176, 58, 55, 8);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, CentrifugingRecipeWrapper wrapper, IIngredients ingredients) {
        ChemicalToChemicalRecipeCE recipe = wrapper.getRecipe();
        MekGasChemicalType input = (MekGasChemicalType) recipe.getInput().getType();
        MekGasChemicalType output = (MekGasChemicalType) recipe.getOutput().getType();
        IGuiIngredientGroup<GasStack> gases = layout.getIngredientsGroup(MekanismJEI.TYPE_GAS);
        initGas(gases, 0, true, 26 - xOffset, 14 - yOffset, 16, 58,
              new GasStack(input.getGas(), safeInt(recipe.getInput().getAmount())), true);
        initGas(gases, 1, false, 134 - xOffset, 14 - yOffset, 16, 58,
              new GasStack(output.getGas(), safeInt(recipe.getOutput().getAmount())), true);
    }

    private static int safeInt(long amount) {
        return (int) Math.min(Integer.MAX_VALUE, amount);
    }
}
