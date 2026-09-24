package mekanism.ultimate.api.recipe.input;

import java.util.Objects;
import javax.annotation.Nullable;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import net.minecraft.item.ItemStack;

/** Immutable lookup view for a recipe with one item and one chemical input. */
public final class ItemChemicalInputCE {

    private final ItemStack item;
    private final IChemicalStackCE chemical;

    public ItemChemicalInputCE(ItemStack item, @Nullable IChemicalStackCE chemical) {
        this.item = Objects.requireNonNull(item, "item");
        this.chemical = chemical;
    }

    public ItemStack getItem() {
        return item;
    }

    @Nullable
    public IChemicalStackCE getChemical() {
        return chemical;
    }
}
