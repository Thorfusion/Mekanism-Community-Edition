package mekanism.ultimate.common.inventory;

import mekanism.common.base.IUpgradeItem;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.util.ChargeUtils;
import mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ContainerDimensionalStabilizer extends Container
{
    private final TileEntityDimensionalStabilizer tile;

    public ContainerDimensionalStabilizer(InventoryPlayer inventory,
          TileEntityDimensionalStabilizer tile)
    {
        this.tile = tile;
        addSlotToContainer(new SlotDischarge(tile,
              TileEntityDimensionalStabilizer.ENERGY_SLOT, 143, 35));
        addSlotToContainer(new Slot(tile,
              TileEntityDimensionalStabilizer.UPGRADE_SLOT, 143, 59));
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

    public TileEntityDimensionalStabilizer getTile()
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
        if (slotId < 2)
        {
            if (!mergeItemStack(stack, 2, inventorySlots.size(), true))
            {
                return null;
            }
        }
        else if (ChargeUtils.canBeDischarged(stack))
        {
            if (!mergeItemStack(stack, 0, 1, false))
            {
                return null;
            }
        }
        else if (stack.getItem() instanceof IUpgradeItem
              && tile.isItemValidForSlot(1, stack))
        {
            if (!mergeItemStack(stack, 1, 2, false))
            {
                return null;
            }
        }
        else if (slotId < 29)
        {
            if (!mergeItemStack(stack, 29, inventorySlots.size(), false))
            {
                return null;
            }
        }
        else if (!mergeItemStack(stack, 2, 29, false))
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
