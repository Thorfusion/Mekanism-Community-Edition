package mekanism.nuclear.common.inventory;

import javax.annotation.Nonnull;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

/** Slotless SPS status container used for access and live synchronization. */
public class ContainerSPS extends Container
{
    private final TileEntitySPSPort tile;

    public ContainerSPS(EntityPlayer player, TileEntitySPSPort tile)
    {
        this.tile = tile;
        tile.open(player);
    }

    public TileEntitySPSPort getTile()
    {
        return tile;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player)
    {
        return !tile.isInvalid() && tile.getWorldObj() == player.worldObj
              && player.getDistanceSq(tile.xCoord + 0.5D,
                    tile.yCoord + 0.5D, tile.zCoord + 0.5D) <= 64D;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        tile.close(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        return null;
    }
}
