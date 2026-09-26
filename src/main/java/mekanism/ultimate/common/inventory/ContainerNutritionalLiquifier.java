package mekanism.ultimate.common.inventory;

import javax.annotation.Nonnull;
import mekanism.common.inventory.container.ContainerMekanism;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.inventory.slot.SlotOutput;
import mekanism.common.util.ChargeUtils;
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ContainerNutritionalLiquifier
      extends ContainerMekanism<TileEntityNutritionalLiquifier> {

    public ContainerNutritionalLiquifier(InventoryPlayer inventory, TileEntityNutritionalLiquifier tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new Slot(tileEntity, TileEntityNutritionalLiquifier.INPUT_SLOT, 27, 35));
        addSlotToContainer(new SlotOutput(tileEntity, TileEntityNutritionalLiquifier.OUTPUT_SLOT, 108, 35));
        addSlotToContainer(new Slot(tileEntity, TileEntityNutritionalLiquifier.CONTAINER_SLOT, 135, 24));
        addSlotToContainer(new SlotOutput(tileEntity, TileEntityNutritionalLiquifier.CONTAINER_OUTPUT_SLOT, 135, 55));
        addSlotToContainer(new SlotDischarge(tileEntity, TileEntityNutritionalLiquifier.ENERGY_SLOT, 155, 55));
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
        if (slotID < 5) {
            if (!mergeItemStack(stack, 5, inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (ChargeUtils.canBeDischarged(stack)) {
            if (!mergeItemStack(stack, 4, 5, false)) {
                return ItemStack.EMPTY;
            }
        } else if (tileEntity.isItemValidForSlot(TileEntityNutritionalLiquifier.CONTAINER_SLOT, stack)) {
            if (!mergeItemStack(stack, 2, 3, false)) {
                return ItemStack.EMPTY;
            }
        } else if (tileEntity.isItemValidForSlot(TileEntityNutritionalLiquifier.INPUT_SLOT, stack)) {
            if (!mergeItemStack(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (slotID < 32) {
            if (!mergeItemStack(stack, 32, inventorySlots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (!mergeItemStack(stack, 5, 32, false)) {
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
