package mekanism.nuclear.common.inventory;

import javax.annotation.Nonnull;
import mekanism.api.gas.IGasItem;
import mekanism.common.inventory.container.ContainerMekanism;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.inventory.slot.SlotStorageTank;
import mekanism.common.util.ChargeUtils;
import mekanism.nuclear.common.tile.TileEntityIsotopicCentrifuge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerIsotopicCentrifuge extends ContainerMekanism<TileEntityIsotopicCentrifuge> {

    public ContainerIsotopicCentrifuge(InventoryPlayer inventory, TileEntityIsotopicCentrifuge tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new SlotStorageTank(tileEntity, 0, 5, 56));
        addSlotToContainer(new SlotStorageTank(tileEntity, 1, 155, 56));
        addSlotToContainer(new SlotDischarge(tileEntity, 2, 155, 14));
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotID);
        if (slot == null || !slot.getHasStack()) {
            return original;
        }

        ItemStack stack = slot.getStack();
        original = stack.copy();
        if (ChargeUtils.canBeDischarged(stack)) {
            if (slotID != 2 && !mergeItemStack(stack, 2, 3, false)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof IGasItem) {
            if (slotID >= 3) {
                IGasItem gasItem = (IGasItem) stack.getItem();
                if (gasItem.canProvideGas(stack, null)) {
                    if (!mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!mergeItemStack(stack, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 3, inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (slotID >= 3 && slotID < 30) {
            if (!mergeItemStack(stack, 30, inventorySlots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (slotID >= 30) {
            if (!mergeItemStack(stack, 3, 30, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!mergeItemStack(stack, 3, inventorySlots.size(), true)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }
}
