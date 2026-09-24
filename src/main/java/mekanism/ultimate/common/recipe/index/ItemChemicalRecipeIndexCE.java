package mekanism.ultimate.common.recipe.index;

import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nullable;
import mekanism.ultimate.api.recipe.IRecipeIndexCE;
import mekanism.ultimate.api.recipe.input.ItemChemicalInputCE;
import mekanism.ultimate.api.recipe.type.IItemChemicalInputRecipeCE;
import mekanism.ultimate.common.recipe.key.ItemIdentityKeyCE;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** Coarse item-identity index for item plus chemical recipes. */
public final class ItemChemicalRecipeIndexCE<RECIPE extends IItemChemicalInputRecipeCE>
      implements IRecipeIndexCE<ItemChemicalInputCE, ItemIdentityKeyCE, RECIPE> {

    @Nullable
    @Override
    public ItemIdentityKeyCE getLookupKey(ItemChemicalInputCE input) {
        ItemStack item = input == null ? ItemStack.EMPTY : input.getItem();
        return item.isEmpty() ? null : new ItemIdentityKeyCE(item.getItem());
    }

    @Override
    public Collection<ItemIdentityKeyCE> getRegistrationKeys(RECIPE recipe) {
        Collection<ItemIdentityKeyCE> keys = new ArrayList<>();
        for (Item item : recipe.getItemInput().getIndexedItems()) {
            keys.add(new ItemIdentityKeyCE(item));
        }
        return keys;
    }
}
