package mekanism.nuclear.common.inventory;

import javax.annotation.Nonnull;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

/** Slotless SPS status container used for access validation and live synchronization. */
public class ContainerSPS extends Container {

    private final TileEntitySPSPort tile;

    public ContainerSPS(EntityPlayer player, TileEntitySPSPort tile) {
        this.tile = tile;
        tile.open(player);
    }

    public TileEntitySPSPort getTile() {
        return tile;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player) {
        return !tile.isInvalid() && tile.getWorld() == player.world
              && player.getDistanceSq(tile.getPos()) <= 64D;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        tile.close(player);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return ItemStack.EMPTY;
    }
}
