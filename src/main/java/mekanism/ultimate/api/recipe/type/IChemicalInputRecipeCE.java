package mekanism.ultimate.api.recipe.type;

import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.IRecipeCE;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;

public interface IChemicalInputRecipeCE extends IRecipeCE<IChemicalStackCE> {

    ChemicalIngredientCE getInput();
}
