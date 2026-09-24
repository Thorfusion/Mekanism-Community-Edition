package mekanism.ultimate.api.recipe.type;

import mekanism.ultimate.api.recipe.IRecipeCE;
import mekanism.ultimate.api.recipe.ingredient.ItemIngredientCE;
import net.minecraft.item.ItemStack;

public interface IItemInputRecipeCE extends IRecipeCE<ItemStack> {

    ItemIngredientCE getInput();
}
