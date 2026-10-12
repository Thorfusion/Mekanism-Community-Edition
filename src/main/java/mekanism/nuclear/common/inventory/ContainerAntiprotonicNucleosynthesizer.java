package mekanism.nuclear.common.inventory;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.inventory.slot.SlotOutput;
import mekanism.common.inventory.slot.SlotStorageTank;
import mekanism.common.util.ChargeUtils;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.tile.TileEntityAntiprotonicNucleosynthesizer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAntiprotonicNucleosynthesizer extends Container
{
    private final TileEntityAntiprotonicNucleosynthesizer tileEntity;

    public ContainerAntiprotonicNucleosynthesizer(InventoryPlayer inventory,
          TileEntityAntiprotonicNucleosynthesizer tile)
    {
        tileEntity = tile;
        addSlotToContainer(new SlotStorageTank(tile,
              NuclearChemicals.Antimatter, false, 0, 5, 56));
        addSlotToContainer(new Slot(tile, 1, 47, 35));
        addSlotToContainer(new SlotOutput(tile, 2, 133, 35));
        addSlotToContainer(new SlotDischarge(tile, 3, 155, 56));

        for (int row = 0; row < 3; row++)
        {
            for (int column = 0; column < 9; column++)
            {
                addSlotToContainer(new Slot(inventory,
                      column + row * 9 + 9,
                      8 + column * 18, 84 + row * 18));
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
        Slot slot = (Slot)inventorySlots.get(slotId);
        if (slot == null || !slot.getHasStack())
        {
            return null;
        }
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        if (slotId < 4)
        {
            if (!mergeItemStack(stack, 4, inventorySlots.size(), true))
            {
                return null;
            }
        }
        else if (ChargeUtils.canBeDischarged(stack))
        {
            if (!mergeItemStack(stack, 3, 4, false))
            {
                return null;
            }
        }
        else if (isAntimatterContainer(stack))
        {
            if (!mergeItemStack(stack, 0, 1, false))
            {
                return null;
            }
        }
        else if (tileEntity.isItemValidForSlot(1, stack))
        {
            if (!mergeItemStack(stack, 1, 2, false))
            {
                return null;
            }
        }
        else if (slotId >= 4 && slotId < 31)
        {
            if (!mergeItemStack(stack, 31, inventorySlots.size(), false))
            {
                return null;
            }
        }
        else if (slotId >= 31
              && !mergeItemStack(stack, 4, 31, false))
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

    private static boolean isAntimatterContainer(ItemStack stack)
    {
        if (stack == null || !(stack.getItem() instanceof IGasItem))
        {
            return false;
        }
        GasStack gas = ((IGasItem)stack.getItem()).getGas(stack);
        return gas != null && gas.getGas() == NuclearChemicals.Antimatter;
    }
}
