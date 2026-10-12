package mekanism.ultimate.common.integration;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Optional bridge to Et Futurum Requiem's 1.7.10 Elytra implementation.
 *
 * <p>The flight methods are mixed into the player class at runtime. Reflection
 * keeps the Ultimate and MekaSuit artifacts loadable when Et Futurum is not
 * installed and also avoids linking against its non-API setter interface.</p>
 */
public final class EtFuturumElytraCompat
{
    public static final String MODID = "etfuturum";
    public static final String ELYTRA_REGISTRY_NAME = "elytra";

    private static final String GET_FLYING = "etfu$isElytraFlying";
    private static final String SET_FLYING = "etfu$setElytraFlying";
    private static final FlightAccess UNSUPPORTED = new FlightAccess(null,
          null);
    private static final Map<Class<?>, FlightAccess> ACCESS =
          new ConcurrentHashMap<Class<?>, FlightAccess>();

    private EtFuturumElytraCompat()
    {
    }

    public static boolean isAvailable()
    {
        return getElytraItem() != null
              && access(EntityPlayer.class) != UNSUPPORTED;
    }

    public static Item getElytraItem()
    {
        return Loader.isModLoaded(MODID)
              ? GameRegistry.findItem(MODID, ELYTRA_REGISTRY_NAME) : null;
    }

    public static ItemStack createProxy(ItemStack source, boolean copyTag)
    {
        Item elytra = getElytraItem();
        if (elytra == null)
        {
            return null;
        }
        ItemStack proxy = new ItemStack(elytra);
        if (copyTag && source != null && source.hasTagCompound())
        {
            proxy.setTagCompound(
                  (NBTTagCompound)source.stackTagCompound.copy());
        }
        // Each proxy lives for one player tick. Damage is transferred to the
        // real item (or converted to energy use) before the proxy is discarded.
        proxy.setItemDamage(0);
        return proxy;
    }

    public static boolean supportsFlight(EntityPlayer player)
    {
        return player != null && access(player) != UNSUPPORTED;
    }

    public static boolean isFlying(EntityPlayer player)
    {
        FlightAccess access = player == null ? UNSUPPORTED : access(player);
        if (access == UNSUPPORTED)
        {
            return false;
        }
        try
        {
            return ((Boolean)access.getFlying.invoke(player)).booleanValue();
        }
        catch (Exception ignored)
        {
            return false;
        }
    }

    public static boolean setFlying(EntityPlayer player, boolean flying)
    {
        FlightAccess access = player == null ? UNSUPPORTED : access(player);
        if (access == UNSUPPORTED)
        {
            return false;
        }
        try
        {
            access.setFlying.invoke(player, Boolean.valueOf(flying));
            return true;
        }
        catch (Exception ignored)
        {
            return false;
        }
    }

    private static FlightAccess access(EntityPlayer player)
    {
        return access(player.getClass());
    }

    private static FlightAccess access(Class<?> playerClass)
    {
        FlightAccess existing = ACCESS.get(playerClass);
        if (existing != null)
        {
            return existing;
        }
        FlightAccess discovered;
        try
        {
            discovered = new FlightAccess(
                  playerClass.getMethod(GET_FLYING),
                  playerClass.getMethod(SET_FLYING, Boolean.TYPE));
        }
        catch (Exception ignored)
        {
            discovered = UNSUPPORTED;
        }
        FlightAccess raced = ACCESS.putIfAbsent(playerClass, discovered);
        return raced == null ? discovered : raced;
    }

    private static final class FlightAccess
    {
        private final Method getFlying;
        private final Method setFlying;

        private FlightAccess(Method getFlying, Method setFlying)
        {
            this.getFlying = getFlying;
            this.setFlying = setFlying;
        }
    }
}
