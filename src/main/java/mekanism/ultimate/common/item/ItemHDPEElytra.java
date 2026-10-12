package mekanism.ultimate.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.api.EnumColor;
import mekanism.common.MekanismItems;
import mekanism.common.util.LangUtils;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/** Stable HDPE Reinforced Elytra backed by Et Futurum's flight implementation. */
public final class ItemHDPEElytra extends Item
{
    public static final int MAX_DAMAGE = 648;

    @SideOnly(Side.CLIENT)
    private IIcon brokenIcon;

    public ItemHDPEElytra()
    {
        setUnlocalizedName("HDPEElytra");
        setMaxStackSize(1);
        setMaxDamage(MAX_DAMAGE);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this,
              BlockDispenser.dispenseBehaviorRegistry.getObject(
                    Items.iron_chestplate));
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.rare;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return repair != null && repair.getItem() == MekanismItems.Polyethene
              && repair.getItemDamage() == 2;
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
    {
        return armorType == 1;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world,
          EntityPlayer player)
    {
        if (player.getCurrentArmor(2) == null)
        {
            player.setCurrentItemOrArmor(3, stack.copy());
            if (!player.capabilities.isCreativeMode)
            {
                stack.stackSize = 0;
            }
        }
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(
              "mekanismultimate:hdpe_elytra");
        brokenIcon = register.registerIcon(
              "mekanismultimate:hdpe_broken_elytra");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        return damage >= MAX_DAMAGE - 1 ? brokenIcon : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player,
          List tooltip, boolean advanced)
    {
        if (!EtFuturumElytraCompat.isAvailable())
        {
            tooltip.add(EnumColor.RED + LangUtils.localize(
                  "tooltip.mekanismultimate.requiresEtFuturum"));
        }
    }

    public static boolean isUsable(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof ItemHDPEElytra
              && stack.getItemDamage() < stack.getMaxDamage() - 1;
    }

    /** Transfers one proxy tick's Unbreaking-aware damage without destroying the item. */
    public static int applyProxyDamage(ItemStack stack, int proxyDamage)
    {
        if (stack == null || !(stack.getItem() instanceof ItemHDPEElytra)
              || proxyDamage <= 0)
        {
            return 0;
        }
        int before = stack.getItemDamage();
        int maximumUsableDamage = Math.max(0, stack.getMaxDamage() - 1);
        int after = (int)Math.min(maximumUsableDamage,
              (long)before + proxyDamage);
        if (after <= before)
        {
            return 0;
        }
        stack.setItemDamage(after);
        return stack.getItemDamage() - before;
    }
}
