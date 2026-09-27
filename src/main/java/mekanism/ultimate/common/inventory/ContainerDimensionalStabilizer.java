package mekanism.ultimate.common.inventory;

import javax.annotation.Nonnull;
import mekanism.common.inventory.container.ContainerMekanism;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.util.ChargeUtils;
import mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ContainerDimensionalStabilizer
      extends ContainerMekanism<TileEntityDimensionalStabilizer> {

    public ContainerDimensionalStabilizer(InventoryPlayer inventory, TileEntityDimensionalStabilizer tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new SlotDischarge(tileEntity, TileEntityDimensionalStabilizer.ENERGY_SLOT, 143, 35));
        addSlotToContainer(new Slot(tileEntity, TileEntityDimensionalStabilizer.UPGRADE_SLOT, 164, 64));
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = inventorySlots.get(slotId);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        if (slotId < 2) {
            if (!mergeItemStack(stack, 2, inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else if (ChargeUtils.canBeDischarged(stack)) {
            if (!mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (tileEntity.isItemValidForSlot(1, stack)) {
            if (!mergeItemStack(stack, 1, 2, false)) return ItemStack.EMPTY;
        } else return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }
}
