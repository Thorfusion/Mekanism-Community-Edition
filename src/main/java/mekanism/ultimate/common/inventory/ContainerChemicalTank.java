package mekanism.ultimate.common.inventory;

import mekanism.api.gas.IGasItem;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import mekanism.ultimate.common.util.ChemicalTransferUtilsCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ContainerChemicalTank extends Container
{
    private final TileEntityChemicalTank tileEntity;

    public ContainerChemicalTank(InventoryPlayer inventory,
          TileEntityChemicalTank tileEntity)
    {
        this.tileEntity = tileEntity;
        addSlotToContainer(new ChemicalContainerSlot(tileEntity, 0, 8, 8));
        addSlotToContainer(new ChemicalContainerSlot(tileEntity, 1, 8, 40));

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

        tileEntity.open(inventory.player);
        tileEntity.openInventory();
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

        if (isChemicalContainer(stack))
        {
            if (slotId >= 2)
            {
                int target = containsChemical(stack) ? 1 : 0;
                if (!mergeItemStack(stack, target, target + 1, false))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(stack, 2, inventorySlots.size(), true))
            {
                return null;
            }
        }
        else if (slotId >= 2 && slotId < 29)
        {
            if (!mergeItemStack(stack, 29, inventorySlots.size(), false))
            {
                return null;
            }
        }
        else if (slotId >= 29)
        {
            if (!mergeItemStack(stack, 2, 29, false))
            {
                return null;
            }
        }
        else if (!mergeItemStack(stack, 2, inventorySlots.size(), true))
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

    private static boolean isChemicalContainer(ItemStack stack)
    {
        return stack != null && (stack.getItem() instanceof IGasItem
              || ChemicalTransferUtilsCE.itemHandler(stack) != null);
    }

    private static boolean containsChemical(ItemStack stack)
    {
        if (stack.getItem() instanceof IGasItem
              && ((IGasItem)stack.getItem()).getGas(stack) != null)
        {
            return true;
        }
        IChemicalHandlerCE handler = ChemicalTransferUtilsCE
              .itemHandler(stack);
        if (handler == null)
        {
            return false;
        }
        for (int tank = 0; tank < handler.getChemicalTankCount(null); tank++)
        {
            IChemicalTankCE chemicalTank = handler.getChemicalTank(tank,
                  null);
            if (chemicalTank.getStack() != null)
            {
                return true;
            }
        }
        return false;
    }

    private static final class ChemicalContainerSlot extends Slot
    {
        private ChemicalContainerSlot(IInventory inventory, int index,
              int x, int y)
        {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return isChemicalContainer(stack);
        }
    }
}
