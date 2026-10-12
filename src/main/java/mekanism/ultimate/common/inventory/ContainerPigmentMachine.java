package mekanism.ultimate.common.inventory;

import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.inventory.slot.SlotOutput;
import mekanism.ultimate.common.tile.TileEntityPaintingMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentExtractor;
import mekanism.ultimate.common.tile.TileEntityPigmentMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentMixer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** Shared container with the source-version pigment-machine layouts. */
public final class ContainerPigmentMachine extends Container
{
    private final TileEntityPigmentMachine tile;
    private int machineSlots;

    public ContainerPigmentMachine(InventoryPlayer inventory,
          TileEntityPigmentMachine tile)
    {
        this.tile = tile;
        if (tile instanceof TileEntityPigmentExtractor)
        {
            addSlotToContainer(new Slot(tile, 0, 27, 36));
            addSlotToContainer(new Slot(tile, 1, 153, 56));
            addSlotToContainer(new SlotDischarge(tile, 2, 153, 15));
            machineSlots = 3;
        }
        else if (tile instanceof TileEntityPigmentMixer)
        {
            addSlotToContainer(new Slot(tile, 0, 7, 57));
            addSlotToContainer(new Slot(tile, 1, 155, 57));
            addSlotToContainer(new Slot(tile, 2, 81, 66));
            addSlotToContainer(new SlotDischarge(tile, 3, 155, 15));
            machineSlots = 4;
        }
        else if (tile instanceof TileEntityPaintingMachine)
        {
            addSlotToContainer(new Slot(tile, 0, 46, 36));
            addSlotToContainer(new Slot(tile, 1, 7, 57));
            addSlotToContainer(new SlotOutput(tile, 2, 117, 36));
            addSlotToContainer(new SlotDischarge(tile, 3, 145, 36));
            machineSlots = 4;
        }
        else
        {
            throw new IllegalArgumentException(
                  "Unsupported pigment machine: " + tile);
        }

        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 9; column++)
            {
                addSlotToContainer(new Slot(inventory,
                      column + row * 9 + 9, 8 + column * 18,
                      84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++)
        {
            addSlotToContainer(new Slot(inventory, column,
                  8 + column * 18, 142));
        }
        tile.open(inventory.player);
        tile.openInventory();
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        tile.close(player);
        tile.closeInventory();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId)
    {
        Slot slot = (Slot)inventorySlots.get(slotId);
        if (slot == null || !slot.getHasStack())
        {
            return null;
        }
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        if (slotId < machineSlots)
        {
            if (!mergeItemStack(stack, machineSlots,
                  inventorySlots.size(), true))
            {
                return null;
            }
        }
        else
        {
            boolean moved = false;
            for (int target = 0; target < machineSlots && !moved; target++)
            {
                if (tile.isItemValidForSlot(target, stack))
                {
                    moved = mergeItemStack(stack, target, target + 1,
                          false);
                }
            }
            if (!moved)
            {
                int playerStart = machineSlots;
                int hotbarStart = playerStart + 27;
                if (slotId >= playerStart && slotId < hotbarStart)
                {
                    moved = mergeItemStack(stack, hotbarStart,
                          inventorySlots.size(), false);
                }
                else if (slotId >= hotbarStart)
                {
                    moved = mergeItemStack(stack, playerStart,
                          hotbarStart, false);
                }
            }
            if (!moved)
            {
                return null;
            }
        }

        if (stack.stackSize == 0)
        {
            slot.putStack(null);
        }
        else
        {
            slot.onSlotChanged();
        }
        if (stack.stackSize == original.stackSize)
        {
            return null;
        }
        slot.onPickupFromSlot(player, stack);
        return original;
    }
}
