package mekanism.ultimate.api.recipe.type;

import mekanism.ultimate.api.recipe.IRecipeCE;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;
import mekanism.ultimate.api.recipe.ingredient.ItemIngredientCE;
import mekanism.ultimate.api.recipe.input.ItemChemicalInputCE;

public interface IItemChemicalInputRecipeCE extends IRecipeCE<ItemChemicalInputCE> {

    ItemIngredientCE getItemInput();

    ChemicalIngredientCE getChemicalInput();
}
