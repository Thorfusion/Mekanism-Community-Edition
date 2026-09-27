package mekanism.ultimate.common.inventory;

import javax.annotation.Nonnull;
import mekanism.common.inventory.container.ContainerMekanism;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.inventory.slot.SlotOutput;
import mekanism.common.util.ChargeUtils;
import mekanism.ultimate.common.tile.TileEntityPaintingMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentExtractor;
import mekanism.ultimate.common.tile.TileEntityPigmentMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentMixer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ContainerPigmentMachine extends ContainerMekanism<TileEntityPigmentMachine> {

    private int machineSlots;

    public ContainerPigmentMachine(InventoryPlayer inventory, TileEntityPigmentMachine tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        if (tileEntity instanceof TileEntityPigmentExtractor) {
            addSlotToContainer(new Slot(tileEntity, 0, 27, 36));
            addSlotToContainer(new Slot(tileEntity, 1, 153, 56));
            addSlotToContainer(new SlotDischarge(tileEntity, 2, 153, 15));
            machineSlots = 3;
        } else if (tileEntity instanceof TileEntityPigmentMixer) {
            addSlotToContainer(new Slot(tileEntity, 0, 7, 57));
            addSlotToContainer(new Slot(tileEntity, 1, 155, 57));
            addSlotToContainer(new Slot(tileEntity, 2, 81, 66));
            addSlotToContainer(new SlotDischarge(tileEntity, 3, 155, 15));
            machineSlots = 4;
        } else {
            addSlotToContainer(new Slot(tileEntity, 0, 46, 36));
            addSlotToContainer(new Slot(tileEntity, 1, 7, 57));
            addSlotToContainer(new SlotOutput(tileEntity, 2, 117, 36));
            addSlotToContainer(new SlotDischarge(tileEntity, 3, 145, 36));
            machineSlots = 4;
        }
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotId);
        if (slot == null || !slot.getHasStack()) return original;
        ItemStack stack = slot.getStack();
        original = stack.copy();
        if (slotId < machineSlots) {
            if (!mergeItemStack(stack, machineSlots, inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else {
            boolean moved = false;
            for (int target = 0; target < machineSlots && !moved; target++) {
                if (tileEntity.isItemValidForSlot(target, stack)) {
                    moved = mergeItemStack(stack, target, target + 1, false);
                }
            }
            if (!moved) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }
}
