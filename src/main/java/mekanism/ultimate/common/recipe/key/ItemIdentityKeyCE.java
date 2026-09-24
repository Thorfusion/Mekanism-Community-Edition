package mekanism.ultimate.common.recipe.key;

import java.util.Objects;
import net.minecraft.item.Item;

/** Runtime item identity for coarse recipe indexing. */
public final class ItemIdentityKeyCE {

    private final Item item;

    public ItemIdentityKeyCE(Item item) {
        this.item = Objects.requireNonNull(item, "item");
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof ItemIdentityKeyCE && item == ((ItemIdentityKeyCE) obj).item;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(item);
    }
}
