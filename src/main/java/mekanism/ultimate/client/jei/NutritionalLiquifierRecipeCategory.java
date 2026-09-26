package mekanism.ultimate.client.jei;

import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.ultimate.common.MekanismUltimate;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraftforge.fluids.FluidStack;

public final class NutritionalLiquifierRecipeCategory
      extends BaseRecipeCategory<NutritionalLiquifierRecipeWrapper> {

    public NutritionalLiquifierRecipeCategory(IGuiHelper helper) {
        super(helper, "mekanism:gui/GuiBlank.png", UltimateJEI.UID,
              "tile.NutritionalLiquifier.name", null, 3, 12, 170, 70);
    }

    @Override
    protected void addGuiElements() {
        guiElements.add(new GuiSlot(SlotType.INPUT, this, guiLocation, 26, 34));
        guiElements.add(new GuiSlot(SlotType.OUTPUT, this, guiLocation, 107, 34));
        guiElements.add(GuiFluidGauge.getDummy(GuiGauge.Type.STANDARD, this, guiLocation, 78, 13));
        guiElements.add(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return (double) timer.getValue() / 20F;
            }
        }, ProgressBar.LARGE_RIGHT, this, guiLocation, 50, 39));
    }

    @Override
    public void setRecipe(IRecipeLayout layout, NutritionalLiquifierRecipeWrapper wrapper,
          IIngredients ingredients) {
        IGuiItemStackGroup items = layout.getItemStacks();
        items.init(0, true, 27 - xOffset, 35 - yOffset);
        items.init(1, false, 108 - xOffset, 35 - yOffset);
        items.set(0, wrapper.getInput());
        if (!wrapper.getContainerOutput().isEmpty()) {
            items.set(1, wrapper.getContainerOutput());
        }

        IGuiFluidStackGroup fluids = layout.getFluidStacks();
        FluidStack output = wrapper.getPasteOutput();
        fluids.init(0, false, 79 - xOffset, 14 - yOffset, 16, 58,
              output.amount, false, fluidOverlayLarge);
        fluids.set(0, output);
    }

    @Override
    public String getModName() {
        return MekanismUltimate.MODID;
    }
}
