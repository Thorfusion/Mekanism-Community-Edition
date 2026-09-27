package mekanism.ultimate.common.item;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.config.MekanismConfig;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile.SecurityMode;
import mekanism.common.util.ItemDataUtils;
import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Lossless portable item form of the Personal Barrel. */
public final class ItemBlockPersonalBarrel extends ItemBlock implements ISustainedInventory, ISecurityItem {

    public ItemBlockPersonalBarrel(Block block) {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player,
          World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY,
          float hitZ, @Nonnull IBlockState state) {
        if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state)) {
            return false;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityPersonalBarrel) {
            TileEntityPersonalBarrel barrel = (TileEntityPersonalBarrel) tile;
            barrel.setInventory(getInventory(stack));
            UUID owner = getOwnerUUID(stack);
            barrel.getSecurity().setOwnerUUID(owner == null ? player.getUniqueID() : owner);
            if (hasSecurity(stack)) {
                barrel.getSecurity().setMode(getSecurity(stack));
            }
            barrel.markDirty();
        }
        return true;
    }

    @Override
    public void setInventory(NBTTagList inventory, Object... data) {
        if (data.length > 0 && data[0] instanceof ItemStack) {
            ItemDataUtils.setList((ItemStack) data[0], "Items",
                  inventory == null ? new NBTTagList() : inventory);
        }
    }

    @Override
    public NBTTagList getInventory(Object... data) {
        return data.length > 0 && data[0] instanceof ItemStack
              ? ItemDataUtils.getList((ItemStack) data[0], "Items") : null;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID(ItemStack stack) {
        if (!ItemDataUtils.hasData(stack, "ownerUUID")) {
            return null;
        }
        try {
            return UUID.fromString(ItemDataUtils.getString(stack, "ownerUUID"));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public void setOwnerUUID(ItemStack stack, @Nullable UUID owner) {
        if (owner == null) {
            ItemDataUtils.removeData(stack, "ownerUUID");
        } else {
            ItemDataUtils.setString(stack, "ownerUUID", owner.toString());
        }
    }

    @Override
    public SecurityMode getSecurity(ItemStack stack) {
        if (!MekanismConfig.current().general.allowProtection.val()) {
            return SecurityMode.PUBLIC;
        }
        int index = ItemDataUtils.getInt(stack, "security");
        SecurityMode[] values = SecurityMode.values();
        return index >= 0 && index < values.length ? values[index] : SecurityMode.PUBLIC;
    }

    @Override
    public void setSecurity(ItemStack stack, SecurityMode mode) {
        ItemDataUtils.setInt(stack, "security",
              mode == null ? SecurityMode.PUBLIC.ordinal() : mode.ordinal());
    }

    @Override
    public boolean hasSecurity(ItemStack stack) {
        return true;
    }

    @Override
    public boolean hasOwner(ItemStack stack) {
        return true;
    }
}
