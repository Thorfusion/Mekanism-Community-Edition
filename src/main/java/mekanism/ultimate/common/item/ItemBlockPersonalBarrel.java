package mekanism.ultimate.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.api.EnumColor;
import mekanism.api.MekanismConfig.general;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile.SecurityMode;
import mekanism.common.util.LangUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

/** Lossless portable item form of the Personal Barrel. */
public final class ItemBlockPersonalBarrel extends ItemBlock
      implements ISustainedInventory, ISecurityItem
{
    public ItemBlockPersonalBarrel(Block block)
    {
        super(block);
        setMaxStackSize(1);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player,
          World world, int x, int y, int z, int side, float hitX, float hitY,
          float hitZ, int metadata)
    {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX,
              hitY, hitZ, metadata))
        {
            return false;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityPersonalBarrel)
        {
            TileEntityPersonalBarrel barrel =
                  (TileEntityPersonalBarrel)tile;
            barrel.setInventory(getInventory(stack));
            String owner = getOwner(stack);
            barrel.getSecurity().setOwner(owner == null
                  ? player.getCommandSenderName() : owner);
            barrel.getSecurity().setMode(getSecurity(stack));
            barrel.markDirty();
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    public void addInformation(ItemStack stack, EntityPlayer player,
          List tooltip, boolean advanced)
    {
        tooltip.add(SecurityUtils.getOwnerDisplay(
              player.getCommandSenderName(), getOwner(stack)));
        tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.inventory")
              + ": " + EnumColor.GREY
              + LangUtils.transYesNo(getInventory(stack) != null
                    && getInventory(stack).tagCount() > 0));
    }

    @Override
    public void setInventory(NBTTagList inventory, Object... data)
    {
        if (data.length == 0 || !(data[0] instanceof ItemStack))
        {
            return;
        }
        ItemStack stack = (ItemStack)data[0];
        ensureTag(stack).setTag("Items", inventory == null
              ? new NBTTagList() : inventory.copy());
    }

    @Override
    public NBTTagList getInventory(Object... data)
    {
        if (data.length == 0 || !(data[0] instanceof ItemStack))
        {
            return null;
        }
        ItemStack stack = (ItemStack)data[0];
        return stack.stackTagCompound == null ? null
              : stack.stackTagCompound.getTagList("Items", NBT.TAG_COMPOUND);
    }

    @Override
    public String getOwner(ItemStack stack)
    {
        return stack.stackTagCompound != null
              && stack.stackTagCompound.hasKey("owner")
              ? stack.stackTagCompound.getString("owner") : null;
    }

    @Override
    public void setOwner(ItemStack stack, String owner)
    {
        if (owner == null || owner.isEmpty())
        {
            if (stack.stackTagCompound != null)
            {
                stack.stackTagCompound.removeTag("owner");
            }
        }
        else
        {
            ensureTag(stack).setString("owner", owner);
        }
    }

    @Override
    public SecurityMode getSecurity(ItemStack stack)
    {
        if (!general.allowProtection || stack.stackTagCompound == null)
        {
            return SecurityMode.PUBLIC;
        }
        int ordinal = stack.stackTagCompound.getInteger("security");
        return ordinal >= 0 && ordinal < SecurityMode.values().length
              ? SecurityMode.values()[ordinal] : SecurityMode.PUBLIC;
    }

    @Override
    public void setSecurity(ItemStack stack, SecurityMode mode)
    {
        ensureTag(stack).setInteger("security", (mode == null
              ? SecurityMode.PUBLIC : mode).ordinal());
    }

    @Override
    public boolean hasSecurity(ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean hasOwner(ItemStack stack)
    {
        return true;
    }

    private static NBTTagCompound ensureTag(ItemStack stack)
    {
        if (stack.stackTagCompound == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.stackTagCompound;
    }
}
