package mekanism.ultimate.common.inventory;

import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.inventory.slot.SlotOutput;
import mekanism.common.util.ChargeUtils;
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public final class ContainerNutritionalLiquifier extends Container
{
    private final TileEntityNutritionalLiquifier tile;

    public ContainerNutritionalLiquifier(InventoryPlayer inventory,
          TileEntityNutritionalLiquifier tile)
    {
        this.tile = tile;
        addSlotToContainer(new Slot(tile,
              TileEntityNutritionalLiquifier.INPUT_SLOT, 27, 35));
        addSlotToContainer(new SlotOutput(tile,
              TileEntityNutritionalLiquifier.OUTPUT_SLOT, 108, 35));
        addSlotToContainer(new Slot(tile,
              TileEntityNutritionalLiquifier.CONTAINER_SLOT, 135, 24));
        addSlotToContainer(new SlotOutput(tile,
              TileEntityNutritionalLiquifier.CONTAINER_OUTPUT_SLOT,
              135, 55));
        addSlotToContainer(new SlotDischarge(tile,
              TileEntityNutritionalLiquifier.ENERGY_SLOT, 155, 55));

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
        if (slotId < 5)
        {
            if (!mergeItemStack(stack, 5, inventorySlots.size(), true))
            {
                return null;
            }
        }
        else if (ChargeUtils.canBeDischarged(stack))
        {
            if (!mergeItemStack(stack, 4, 5, false))
            {
                return null;
            }
        }
        else if (stack.getItem() instanceof IFluidContainerItem)
        {
            if (!mergeItemStack(stack, 2, 3, false))
            {
                return null;
            }
        }
        else if (tile.isItemValidForSlot(
              TileEntityNutritionalLiquifier.INPUT_SLOT, stack))
        {
            if (!mergeItemStack(stack, 0, 1, false))
            {
                return null;
            }
        }
        else if (slotId < 32)
        {
            if (!mergeItemStack(stack, 32, inventorySlots.size(), false))
            {
                return null;
            }
        }
        else if (!mergeItemStack(stack, 5, 32, false))
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
