package mekanism.ultimate.common.integration;

import cpw.mods.fml.relauncher.ReflectionHelper;
import java.lang.reflect.Field;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Keeps Et Futurum's temporary Elytra proxy out of equipment synchronization. */
public final class EtFuturumElytraSwap
{
    private static final int ARMOR_CHEST_INDEX = 2;
    private static final int EQUIPMENT_CHEST_INDEX = 3;
    private static final Field PREVIOUS_EQUIPMENT = ReflectionHelper.findField(
          EntityLivingBase.class, "previousEquipment", "field_82180_bT");

    private EtFuturumElytraSwap()
    {
    }

    public static Swap begin(EntityPlayer player, ItemStack original,
          boolean copyTag)
    {
        if (player == null || original == null)
        {
            return null;
        }
        ItemStack proxy = EtFuturumElytraCompat.createProxy(original,
              copyTag);
        if (proxy == null)
        {
            return null;
        }
        ItemStack cachedOriginal = null;
        if (!player.worldObj.isRemote)
        {
            ItemStack cached = equipmentCache(player)[EQUIPMENT_CHEST_INDEX];
            cachedOriginal = cached == null ? null : cached.copy();
        }
        Swap swap = new Swap(original, proxy, cachedOriginal);
        setChestSilently(player, proxy);
        if (!player.worldObj.isRemote)
        {
            // EntityLivingBase compares this cache during the player tick. By
            // matching the proxy here, neither it nor armor attributes leak.
            equipmentCache(player)[EQUIPMENT_CHEST_INDEX] = proxy.copy();
        }
        return swap;
    }

    /**
     * Restores the real chest item. Returns true only when the proxy still
     * owned the slot, so callers know it is safe to transfer its usage.
     */
    public static boolean restore(EntityPlayer player, Swap swap)
    {
        if (player == null || swap == null)
        {
            return false;
        }
        ItemStack current = player.getCurrentArmor(ARMOR_CHEST_INDEX);
        if (current == swap.proxy)
        {
            setChestSilently(player, swap.original);
            if (!player.worldObj.isRemote)
            {
                equipmentCache(player)[EQUIPMENT_CHEST_INDEX] =
                      swap.cachedOriginal;
            }
            return true;
        }

        // Another mod replaced the chest slot during the tick. Preserve both
        // items rather than overwriting the new owner with our saved stack.
        if (current != swap.original && swap.original.stackSize > 0)
        {
            if (!player.inventory.addItemStackToInventory(swap.original)
                  || swap.original.stackSize > 0)
            {
                player.dropPlayerItemWithRandomChoice(swap.original, false);
            }
        }
        if (!player.worldObj.isRemote)
        {
            equipmentCache(player)[EQUIPMENT_CHEST_INDEX] = current == null
                  ? null : current.copy();
        }
        return false;
    }

    private static void setChestSilently(EntityPlayer player, ItemStack stack)
    {
        player.inventory.armorInventory[ARMOR_CHEST_INDEX] = stack;
    }

    private static ItemStack[] equipmentCache(EntityPlayer player)
    {
        try
        {
            return (ItemStack[])PREVIOUS_EQUIPMENT.get(player);
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException(
                  "Unable to access the 1.7.10 living-equipment cache", e);
        }
    }

    public static final class Swap
    {
        private final ItemStack original;
        private final ItemStack proxy;
        private final ItemStack cachedOriginal;

        private Swap(ItemStack original, ItemStack proxy,
              ItemStack cachedOriginal)
        {
            this.original = original;
            this.proxy = proxy;
            this.cachedOriginal = cachedOriginal;
        }

        public ItemStack getOriginal()
        {
            return original;
        }

        public int getProxyDamage()
        {
            return proxy.getItemDamage();
        }
    }
}
