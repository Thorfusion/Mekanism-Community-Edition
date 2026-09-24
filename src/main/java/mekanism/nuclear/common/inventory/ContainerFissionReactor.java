package mekanism.nuclear.common.inventory;

import javax.annotation.Nonnull;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

/** Slotless controller container used for access validation and live tile synchronization. */
public class ContainerFissionReactor extends Container {

    private final TileEntityFissionReactorPort tile;

    public ContainerFissionReactor(EntityPlayer player, TileEntityFissionReactorPort tile) {
        this.tile = tile;
        tile.open(player);
    }

    public TileEntityFissionReactorPort getTile() {
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
