package mekanism.ultimate.api.inventory;

import javax.annotation.Nullable;
import mekanism.ultimate.api.Action;
import net.minecraft.item.ItemStack;

/** Explicit simulation/execute view over a nullable-stack 1.7 inventory. */
public interface IItemHandlerCE
{
    int getSlots();

    /** Returns a read-only stack view, or {@code null} for an empty slot. */
    @Nullable
    ItemStack getStackInSlot(int slot);

    /** Returns the unaccepted remainder, or {@code null} when fully accepted. */
    @Nullable
    ItemStack insertItem(int slot, ItemStack stack, Action action);

    /** Returns a new extracted stack, or {@code null}. */
    @Nullable
    ItemStack extractItem(int slot, int amount, Action action);

    int getSlotLimit(int slot);
}
