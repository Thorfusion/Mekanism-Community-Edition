package mekanism.common.inventory.container;

import javax.annotation.Nonnull;
import mekanism.common.tile.TileEntityInductionCasing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerInductionMatrixStats extends ContainerInductionMatrix {

    public ContainerInductionMatrixStats(InventoryPlayer inventory, TileEntityInductionCasing tile) {
        super(inventory, tile);
    }

    @Override
    protected void addSlots() {
    }

    @Override
    protected void addInventorySlots(InventoryPlayer inventory) {
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        return ItemStack.EMPTY;
    }
}
