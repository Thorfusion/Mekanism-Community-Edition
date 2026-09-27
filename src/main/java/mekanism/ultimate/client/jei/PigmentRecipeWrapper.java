package mekanism.ultimate.client.jei;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.recipe.type.ChemicalChemicalToChemicalRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemToChemicalRecipeCE;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public final class PigmentRecipeWrapper implements IRecipeWrapper {

    private final PigmentRecipeCategory.Mode mode;
    private final List<List<ItemStack>> itemInputs;
    private final List<ItemStack> itemOutputs;
    private final List<ChemicalStackCE> chemicalInputs;
    private final List<ChemicalStackCE> chemicalOutputs;

    public PigmentRecipeWrapper(ItemToChemicalRecipeCE recipe) {
        mode = PigmentRecipeCategory.Mode.EXTRACTING;
        itemInputs = Collections.singletonList(recipe.getInput().getRepresentations());
        itemOutputs = Collections.emptyList();
        chemicalInputs = Collections.emptyList();
        chemicalOutputs = Collections.singletonList(recipe.getOutput());
    }

    public PigmentRecipeWrapper(ChemicalChemicalToChemicalRecipeCE recipe) {
        mode = PigmentRecipeCategory.Mode.MIXING;
        itemInputs = Collections.emptyList();
        itemOutputs = Collections.emptyList();
        chemicalInputs = Arrays.asList(asStack(recipe.getLeftInput()), asStack(recipe.getRightInput()));
        chemicalOutputs = Collections.singletonList(recipe.getOutput());
    }

    public PigmentRecipeWrapper(ItemChemicalToItemRecipeCE recipe) {
        mode = PigmentRecipeCategory.Mode.PAINTING;
        itemInputs = Collections.singletonList(recipe.getItemInput().getRepresentations());
        itemOutputs = Collections.singletonList(recipe.getOutput());
        chemicalInputs = Collections.singletonList(asStack(recipe.getChemicalInput()));
        chemicalOutputs = Collections.emptyList();
    }

    private static ChemicalStackCE asStack(ChemicalIngredientCE ingredient) {
        return new ChemicalStackCE(ingredient.getType(), ingredient.getAmount());
    }

    public PigmentRecipeCategory.Mode getMode() {
        return mode;
    }

    public List<List<ItemStack>> getItemInputs() {
        return itemInputs;
    }

    public List<ItemStack> getItemOutputs() {
        return itemOutputs;
    }

    public List<ChemicalStackCE> getChemicalInputs() {
        return chemicalInputs;
    }

    public List<ChemicalStackCE> getChemicalOutputs() {
        return chemicalOutputs;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (!itemInputs.isEmpty()) {
            ingredients.setInputLists(VanillaTypes.ITEM, itemInputs);
        }
        if (!itemOutputs.isEmpty()) {
            ingredients.setOutputs(VanillaTypes.ITEM, itemOutputs);
        }
        if (!chemicalInputs.isEmpty()) {
            ingredients.setInputs(UltimateJEI.TYPE_CHEMICAL, chemicalInputs);
        }
        if (!chemicalOutputs.isEmpty()) {
            ingredients.setOutputs(UltimateJEI.TYPE_CHEMICAL, chemicalOutputs);
        }
    }
}
