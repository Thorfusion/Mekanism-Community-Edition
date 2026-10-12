package mekanism.mekasuit.common.inventory;

import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.util.ChargeUtils;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.common.item.ItemMekaModule;
import mekanism.mekasuit.common.tile.TileEntityModificationStation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ContainerModificationStation extends Container
{
    private final TileEntityModificationStation tile;

    public ContainerModificationStation(InventoryPlayer inventory,
          TileEntityModificationStation tile)
    {
        this.tile = tile;
        addSlotToContainer(new Slot(tile,
              TileEntityModificationStation.MODULE_SLOT, 25, 35));
        addSlotToContainer(new Slot(tile,
              TileEntityModificationStation.CONTAINER_SLOT, 65, 35));
        addSlotToContainer(new SlotDischarge(tile,
              TileEntityModificationStation.ENERGY_SLOT, 143, 35));

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

    public TileEntityModificationStation getTile()
    {
        return tile;
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
        if (slotId < 3)
        {
            if (!mergeItemStack(stack, 3, inventorySlots.size(), true))
            {
                return null;
            }
        }
        else if (stack.getItem() instanceof ItemMekaModule)
        {
            if (!mergeItemStack(stack, 0, 1, false))
            {
                return null;
            }
        }
        else if (stack.getItem() instanceof IModuleContainerItem)
        {
            if (!mergeItemStack(stack, 1, 2, false))
            {
                return null;
            }
        }
        else if (ChargeUtils.canBeDischarged(stack))
        {
            if (!mergeItemStack(stack, 2, 3, false))
            {
                return null;
            }
        }
        else if (slotId < 30)
        {
            if (!mergeItemStack(stack, 30, inventorySlots.size(), false))
            {
                return null;
            }
        }
        else if (!mergeItemStack(stack, 3, 30, false))
        {
            return null;
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
