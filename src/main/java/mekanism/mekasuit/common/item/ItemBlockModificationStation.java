package mekanism.mekasuit.common.item;

import mekanism.mekasuit.common.tile.TileEntityModificationStation;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/** Prevents the station's invisible interaction cells replacing other blocks. */
public final class ItemBlockModificationStation extends ItemBlock
{
    public ItemBlockModificationStation(Block block)
    {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
          World world, int x, int y, int z, int side, float hitX, float hitY,
          float hitZ, int metadata)
    {
        int quadrant = MathHelper.floor_double(
              player.rotationYaw * 4F / 360F + 0.5D) & 3;
        int facing = quadrant == 0 ? 2 : quadrant == 1 ? 5
              : quadrant == 2 ? 3 : 4;
        return TileEntityModificationStation.canFormBoundingBlocks(world,
              x, y, z, facing, false)
              && super.placeBlockAt(stack, player, world, x, y, z, side,
                    hitX, hitY, hitZ, metadata);
    }
}
