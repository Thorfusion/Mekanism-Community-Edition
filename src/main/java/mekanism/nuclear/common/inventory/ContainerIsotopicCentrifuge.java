package mekanism.nuclear.common.inventory;

import java.util.Arrays;
import mekanism.api.gas.IGasItem;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.inventory.slot.SlotStorageTank;
import mekanism.common.util.ChargeUtils;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.tile.TileEntityIsotopicCentrifuge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerIsotopicCentrifuge extends Container
{
    private final TileEntityIsotopicCentrifuge tileEntity;

    public ContainerIsotopicCentrifuge(InventoryPlayer inventory,
          TileEntityIsotopicCentrifuge tile)
    {
        tileEntity = tile;
        addSlotToContainer(new SlotStorageTank(tile,
              Arrays.asList(NuclearChemicals.UraniumHexafluoride, NuclearChemicals.NuclearWaste),
              false, 0, 5, 56));
        addSlotToContainer(new SlotStorageTank(tile, 1, 155, 56));
        addSlotToContainer(new SlotDischarge(tile, 2, 155, 14));

        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 9; column++)
            {
                addSlotToContainer(new Slot(inventory, column + row * 9 + 9,
                      8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++)
        {
            addSlotToContainer(new Slot(inventory, column, 8 + column * 18, 142));
        }
        tile.open(inventory.player);
        tile.openInventory();
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        tileEntity.close(player);
        tileEntity.closeInventory();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId)
    {
        ItemStack original = null;
        Slot slot = (Slot)inventorySlots.get(slotId);
        if (slot == null || !slot.getHasStack())
        {
            return null;
        }

        ItemStack stack = slot.getStack();
        original = stack.copy();
        if (slotId < 3)
        {
            if (!mergeItemStack(stack, 3, inventorySlots.size(), true))
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
        else if (stack.getItem() instanceof IGasItem)
        {
            IGasItem gasItem = (IGasItem)stack.getItem();
            if (gasItem.getGas(stack) != null
                  && tileEntity.isItemValidForSlot(0, stack))
            {
                if (!mergeItemStack(stack, 0, 1, false))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(stack, 1, 2, false))
            {
                return null;
            }
        }
        else if (slotId >= 3 && slotId < 30)
        {
            if (!mergeItemStack(stack, 30, inventorySlots.size(), false))
            {
                return null;
            }
        }
        else if (slotId >= 30 && !mergeItemStack(stack, 3, 30, false))
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
