package mekanism.ultimate.common.recipe.index;

import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nullable;
import mekanism.ultimate.api.recipe.IRecipeIndexCE;
import mekanism.ultimate.api.recipe.type.IItemInputRecipeCE;
import mekanism.ultimate.common.recipe.key.ItemIdentityKeyCE;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ItemRecipeIndexCE<RECIPE extends IItemInputRecipeCE>
      implements IRecipeIndexCE<ItemStack, ItemIdentityKeyCE, RECIPE> {

    @Nullable
    @Override
    public ItemIdentityKeyCE getLookupKey(ItemStack input) {
        return input == null || input.isEmpty() ? null : new ItemIdentityKeyCE(input.getItem());
    }

    @Override
    public Collection<ItemIdentityKeyCE> getRegistrationKeys(RECIPE recipe) {
        Collection<ItemIdentityKeyCE> keys = new ArrayList<>();
        for (Item item : recipe.getInput().getIndexedItems()) {
            keys.add(new ItemIdentityKeyCE(item));
        }
        return keys;
    }
}
