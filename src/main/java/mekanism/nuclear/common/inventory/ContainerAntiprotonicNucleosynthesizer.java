package mekanism.nuclear.common.inventory;

import javax.annotation.Nonnull;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import mekanism.common.inventory.container.ContainerMekanism;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.inventory.slot.SlotOutput;
import mekanism.common.inventory.slot.SlotStorageTank;
import mekanism.common.util.ChargeUtils;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.tile.TileEntityAntiprotonicNucleosynthesizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAntiprotonicNucleosynthesizer
      extends ContainerMekanism<TileEntityAntiprotonicNucleosynthesizer> {

    public ContainerAntiprotonicNucleosynthesizer(InventoryPlayer inventory,
          TileEntityAntiprotonicNucleosynthesizer tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new SlotStorageTank(tileEntity, NuclearChemicals.Antimatter, false, 0, 5, 56));
        addSlotToContainer(new Slot(tileEntity, 1, 47, 35));
        addSlotToContainer(new SlotOutput(tileEntity, 2, 133, 35));
        addSlotToContainer(new SlotDischarge(tileEntity, 3, 155, 56));
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
        if (slotID < 4) {
            if (!mergeItemStack(stack, 4, inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (ChargeUtils.canBeDischarged(stack)) {
            if (!mergeItemStack(stack, 3, 4, false)) {
                return ItemStack.EMPTY;
            }
        } else if (isAntimatterContainer(stack)) {
            if (!mergeItemStack(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (tileEntity.isItemValidForSlot(1, stack)) {
            if (!mergeItemStack(stack, 1, 2, false)) {
                return ItemStack.EMPTY;
            }
        } else if (slotID < 31) {
            if (!mergeItemStack(stack, 31, inventorySlots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else if (!mergeItemStack(stack, 4, 31, false)) {
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

    private static boolean isAntimatterContainer(ItemStack stack) {
        if (!(stack.getItem() instanceof IGasItem)) {
            return false;
        }
        GasStack gas = ((IGasItem) stack.getItem()).getGas(stack);
        return gas != null && gas.getGas() == NuclearChemicals.Antimatter;
    }
}
