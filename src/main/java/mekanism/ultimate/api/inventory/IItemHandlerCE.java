package mekanism.ultimate.api.inventory;

import mekanism.ultimate.api.Action;
import net.minecraft.item.ItemStack;

/**
 * Explicit simulation/execute view of an item inventory.
 *
 * <p>Callers must not mutate stacks returned by {@link #getStackInSlot(int)}.
 * Insert returns the unaccepted remainder and extract returns a new stack.</p>
 */
public interface IItemHandlerCE {

    int getSlots();

    ItemStack getStackInSlot(int slot);

    ItemStack insertItem(int slot, ItemStack stack, Action action);

    ItemStack extractItem(int slot, int amount, Action action);

    int getSlotLimit(int slot);
}
