package mekanism.ultimate.common.recipe;

import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.common.recipe.index.ChemicalRecipeIndexCE;
import mekanism.ultimate.common.recipe.index.ItemRecipeIndexCE;
import mekanism.ultimate.common.recipe.key.ChemicalIdentityKeyCE;
import mekanism.ultimate.common.recipe.key.ItemIdentityKeyCE;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemToChemicalRecipeCE;
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
}
