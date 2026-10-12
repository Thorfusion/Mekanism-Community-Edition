package mekanism.ultimate.common.recipe;

import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.input.ChemicalChemicalInputCE;
import mekanism.ultimate.api.recipe.input.ItemChemicalInputCE;
import mekanism.ultimate.common.recipe.index.ChemicalChemicalRecipeIndexCE;
import mekanism.ultimate.common.recipe.index.ChemicalRecipeIndexCE;
import mekanism.ultimate.common.recipe.index.ItemChemicalRecipeIndexCE;
import mekanism.ultimate.common.recipe.index.ItemRecipeIndexCE;
import mekanism.ultimate.common.recipe.key.ChemicalIdentityKeyCE;
import mekanism.ultimate.common.recipe.key.ItemIdentityKeyCE;
import mekanism.ultimate.common.recipe.type.ChemicalChemicalToChemicalRecipeCE;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemToChemicalRecipeCE;
import net.minecraft.item.ItemStack;

/** Typed factories for the first Ultimate recipe families. */
public final class RecipeManagersCE
{
    private RecipeManagersCE()
    {
    }

    public static CachedRecipeManagerCE<IChemicalStackCE, ChemicalIdentityKeyCE,
          ChemicalToChemicalRecipeCE> chemicalToChemical()
    {
        return new CachedRecipeManagerCE<IChemicalStackCE, ChemicalIdentityKeyCE,
            ChemicalToChemicalRecipeCE>(new ChemicalRecipeIndexCE<ChemicalToChemicalRecipeCE>());
    }

    public static CachedRecipeManagerCE<ItemStack, ItemIdentityKeyCE, ItemToChemicalRecipeCE> itemToChemical()
    {
        return new CachedRecipeManagerCE<ItemStack, ItemIdentityKeyCE,
            ItemToChemicalRecipeCE>(new ItemRecipeIndexCE<ItemToChemicalRecipeCE>());
    }

    public static CachedRecipeManagerCE<ItemChemicalInputCE, ItemIdentityKeyCE,
          ItemChemicalToItemRecipeCE> itemChemicalToItem()
    {
        return new CachedRecipeManagerCE<ItemChemicalInputCE, ItemIdentityKeyCE,
            ItemChemicalToItemRecipeCE>(new ItemChemicalRecipeIndexCE<ItemChemicalToItemRecipeCE>());
    }

    public static CachedRecipeManagerCE<ChemicalChemicalInputCE, ChemicalIdentityKeyCE,
          ChemicalChemicalToChemicalRecipeCE> chemicalChemicalToChemical()
    {
        return new CachedRecipeManagerCE<ChemicalChemicalInputCE, ChemicalIdentityKeyCE,
            ChemicalChemicalToChemicalRecipeCE>(new ChemicalChemicalRecipeIndexCE());
    }
}
