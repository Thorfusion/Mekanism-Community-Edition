package mekanism.ultimate.common.recipe;

import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.common.recipe.index.ChemicalRecipeIndexCE;
import mekanism.ultimate.common.recipe.index.ItemRecipeIndexCE;
import mekanism.ultimate.common.recipe.index.ItemChemicalRecipeIndexCE;
import mekanism.ultimate.common.recipe.key.ChemicalIdentityKeyCE;
import mekanism.ultimate.common.recipe.key.ItemIdentityKeyCE;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemToChemicalRecipeCE;
import mekanism.ultimate.api.recipe.input.ItemChemicalInputCE;
import net.minecraft.item.ItemStack;

/** Typed factories for the first Ultimate recipe families. */
public final class RecipeManagersCE {

    private RecipeManagersCE() {
    }

    public static CachedRecipeManagerCE<IChemicalStackCE, ChemicalIdentityKeyCE, ChemicalToChemicalRecipeCE> chemicalToChemical() {
        return new CachedRecipeManagerCE<>(new ChemicalRecipeIndexCE<>());
    }

    public static CachedRecipeManagerCE<ItemStack, ItemIdentityKeyCE, ItemToChemicalRecipeCE> itemToChemical() {
        return new CachedRecipeManagerCE<>(new ItemRecipeIndexCE<>());
    }

    public static CachedRecipeManagerCE<ItemChemicalInputCE, ItemIdentityKeyCE, ItemChemicalToItemRecipeCE> itemChemicalToItem() {
        return new CachedRecipeManagerCE<>(new ItemChemicalRecipeIndexCE<>());
    }
}
