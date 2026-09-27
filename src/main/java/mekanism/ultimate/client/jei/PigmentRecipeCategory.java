package mekanism.ultimate.client.jei;

import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.ultimate.client.gui.element.GuiChemicalGauge;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public final class PigmentRecipeCategory extends BaseRecipeCategory<PigmentRecipeWrapper> {

    private final Mode mode;

    public PigmentRecipeCategory(IGuiHelper helper, Mode mode) {
        super(helper, "mekanism:gui/GuiBlank.png", mode.uid, mode.translationKey,
              null, 3, 4, 170, 80);
        this.mode = mode;
        addModeElements();
    }

    @Override
    protected void addGuiElements() {
        // BaseRecipeCategory invokes this before this class can assign mode.
    }

    private void addModeElements() {
        if (mode == Mode.EXTRACTING) {
            guiElements.add(new GuiSlot(SlotType.INPUT, this, guiLocation, 26, 35));
            guiElements.add(GuiChemicalGauge.getDummy(
                  mekanism.client.gui.element.gauge.GuiGauge.Type.STANDARD,
                  this, guiLocation, 131, 13));
            guiElements.add(progress(ProgressBar.LARGE_RIGHT, 64, 40));
        } else if (mode == Mode.MIXING) {
            guiElements.add(GuiChemicalGauge.getDummy(
                  mekanism.client.gui.element.gauge.GuiGauge.Type.STANDARD,
                  this, guiLocation, 25, 13));
            guiElements.add(GuiChemicalGauge.getDummy(
                  mekanism.client.gui.element.gauge.GuiGauge.Type.STANDARD,
                  this, guiLocation, 79, 4));
            guiElements.add(GuiChemicalGauge.getDummy(
                  mekanism.client.gui.element.gauge.GuiGauge.Type.STANDARD,
                  this, guiLocation, 133, 13));
            guiElements.add(progress(ProgressBar.SMALL_RIGHT, 47, 39));
            guiElements.add(progress(ProgressBar.SMALL_LEFT, 101, 39));
        } else {
            guiElements.add(GuiChemicalGauge.getDummy(
                  mekanism.client.gui.element.gauge.GuiGauge.Type.STANDARD,
                  this, guiLocation, 25, 13));
            guiElements.add(new GuiSlot(SlotType.INPUT, this, guiLocation, 45, 35));
            guiElements.add(new GuiSlot(SlotType.OUTPUT, this, guiLocation, 116, 35));
            guiElements.add(progress(ProgressBar.LARGE_RIGHT, 64, 40));
        }
    }

    private GuiProgress progress(ProgressBar bar, int x, int y) {
        return new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return (double) timer.getValue() / 20F;
            }
        }, bar, this, guiLocation, x, y);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, PigmentRecipeWrapper wrapper,
          IIngredients ingredients) {
        if (wrapper.getMode() != mode) {
            return;
        }
        IGuiItemStackGroup items = layout.getItemStacks();
        IGuiIngredientGroup<ChemicalStackCE> chemicals =
              layout.getIngredientsGroup(UltimateJEI.TYPE_CHEMICAL);
        if (mode == Mode.EXTRACTING) {
            items.init(0, true, 27 - xOffset, 36 - yOffset);
            items.set(0, wrapper.getItemInputs().get(0));
            initChemical(chemicals, 0, false, 132, 14,
                  wrapper.getChemicalOutputs().get(0));
        } else if (mode == Mode.MIXING) {
            initChemical(chemicals, 0, true, 26, 14, wrapper.getChemicalInputs().get(0));
            initChemical(chemicals, 1, true, 134, 14, wrapper.getChemicalInputs().get(1));
            initChemical(chemicals, 2, false, 80, 5, wrapper.getChemicalOutputs().get(0));
        } else {
            initChemical(chemicals, 0, true, 26, 14, wrapper.getChemicalInputs().get(0));
            items.init(0, true, 46 - xOffset, 36 - yOffset);
            items.init(1, false, 117 - xOffset, 36 - yOffset);
            items.set(0, wrapper.getItemInputs().get(0));
            items.set(1, wrapper.getItemOutputs().get(0));
        }
    }

    private void initChemical(IGuiIngredientGroup<ChemicalStackCE> group, int slot,
          boolean input, int x, int y, ChemicalStackCE stack) {
        group.init(slot, input, new ChemicalStackRenderer(stack.getAmount(), true,
                    16, 58, fluidOverlayLarge),
              x - xOffset, y - yOffset, 16, 58, 0, 0);
        group.set(slot, stack);
    }

    @Override
    public String getModName() {
        return MekanismUltimate.MODID;
    }

    public enum Mode {
        EXTRACTING(UltimateJEI.PIGMENT_EXTRACTING, "tile.PigmentExtractor.name"),
        MIXING(UltimateJEI.PIGMENT_MIXING, "tile.PigmentMixer.name"),
        PAINTING(UltimateJEI.PAINTING, "tile.PaintingMachine.name");

        private final String uid;
        private final String translationKey;

        Mode(String uid, String translationKey) {
            this.uid = uid;
            this.translationKey = translationKey;
        }
    }
}
