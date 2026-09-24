package mekanism.mekasuit.common.inventory;

import javax.annotation.Nonnull;
import mekanism.common.inventory.container.ContainerMekanism;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.util.ChargeUtils;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.common.item.ItemMekaModule;
import mekanism.mekasuit.common.tile.TileEntityModificationStation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ContainerModificationStation extends ContainerMekanism<TileEntityModificationStation> {

    public ContainerModificationStation(InventoryPlayer inventory, TileEntityModificationStation tile) {
        super(tile, inventory);
    }

    public TileEntityModificationStation getTile() {
        return tileEntity;
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new Slot(tileEntity, TileEntityModificationStation.MODULE_SLOT, 25, 35));
        addSlotToContainer(new Slot(tileEntity, TileEntityModificationStation.CONTAINER_SLOT, 65, 35));
        addSlotToContainer(new SlotDischarge(tileEntity, TileEntityModificationStation.ENERGY_SLOT, 143, 35));
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = inventorySlots.get(slotId);
        if (slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        if (slotId < 3) {
            if (!mergeItemStack(stack, 3, inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof ItemMekaModule) {
            if (!mergeItemStack(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof IModuleContainerItem) {
            if (!mergeItemStack(stack, 1, 2, false)) {
                return ItemStack.EMPTY;
            }
        } else if (ChargeUtils.canBeDischarged(stack)) {
            if (!mergeItemStack(stack, 2, 3, false)) {
                return ItemStack.EMPTY;
            }
        } else if (slotId < 30) {
            if (!mergeItemStack(stack, 30, inventorySlots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (!mergeItemStack(stack, 3, 30, false)) {
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
