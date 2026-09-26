package mekanism.ultimate.common.inventory;

import javax.annotation.Nonnull;
import mekanism.api.gas.IGasItem;
import mekanism.common.inventory.container.ContainerMekanism;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.common.capability.UltimateChemicalCapabilities;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ContainerChemicalTank extends ContainerMekanism<TileEntityChemicalTank> {

    public ContainerChemicalTank(InventoryPlayer inventory, TileEntityChemicalTank tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new ChemicalContainerSlot(tileEntity, 0, 8, 8));
        addSlotToContainer(new ChemicalContainerSlot(tileEntity, 1, 8, 40));
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
        if (isChemicalContainer(stack)) {
            if (slotId >= 2) {
                int target = containsChemical(stack) ? 1 : 0;
                if (!mergeItemStack(stack, target, target + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 2, inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (slotId >= 2 && slotId < 29) {
            if (!mergeItemStack(stack, 29, inventorySlots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (slotId >= 29) {
            if (!mergeItemStack(stack, 2, 29, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!mergeItemStack(stack, 2, inventorySlots.size(), true)) {
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

    private static boolean isChemicalContainer(ItemStack stack) {
        return stack.getItem() instanceof IGasItem
              || UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY != null
              && stack.hasCapability(UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null);
    }

    private static boolean containsChemical(ItemStack stack) {
        if (stack.getItem() instanceof IGasItem && ((IGasItem) stack.getItem()).getGas(stack) != null) {
            return true;
        }
        if (UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY == null) {
            return false;
        }
        IChemicalHandlerCE handler = stack.getCapability(
              UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null);
        if (handler == null) {
            return false;
        }
        for (int tank = 0; tank < handler.getChemicalTankCount(null); tank++) {
            if (handler.getChemicalTank(tank, null).getStack() != null) {
                return true;
            }
        }
        return false;
    }

    private static final class ChemicalContainerSlot extends Slot {

        private ChemicalContainerSlot(IInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return isChemicalContainer(stack);
        }
    }
}
