package mekanism.ultimate.common.gear;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import mekanism.ultimate.common.UltimateItems;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import mekanism.ultimate.common.integration.EtFuturumElytraSwap;
import mekanism.ultimate.common.integration.EtFuturumElytraSwap.Swap;
import mekanism.ultimate.common.item.ItemHDPEElytra;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Adapts the reinforced Elytra to Et Futurum's item-only flight tick. */
public final class HDPEElytraTickHandler
{
    public static final HDPEElytraTickHandler INSTANCE =
          new HDPEElytraTickHandler();

    private final Map<UUID, Swap> serverSwaps =
          new ConcurrentHashMap<UUID, Swap>();
    private final Map<UUID, Swap> clientSwaps =
          new ConcurrentHashMap<UUID, Swap>();

    private HDPEElytraTickHandler()
    {
    }

    /*
     * The Et Futurum flight update must see the temporary vanilla Elytra.
     * Install it before ordinary tick listeners and keep it in the armor slot
     * until every other END listener has finished.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTickStart(PlayerTickEvent event)
    {
        if (event.phase != Phase.START)
        {
            return;
        }
        Map<UUID, Swap> swaps = swaps(event.side);
        finish(event.player, swaps.remove(event.player.getUniqueID()));

        ItemStack chest = event.player.getCurrentArmor(2);
        if (!EtFuturumElytraCompat.isFlying(event.player))
        {
            return;
        }
        /* Another module may own the active Elytra bridge. Only stop flight
         * when the equipped item is ours but can no longer sustain it. */
        if (chest == null || chest.getItem() != UltimateItems.HDPEElytra)
        {
            return;
        }
        if (!ItemHDPEElytra.isUsable(chest) || event.player.onGround
              || event.player.isInWater() || event.player.ridingEntity != null
              || event.player.capabilities.isFlying)
        {
            EtFuturumElytraCompat.setFlying(event.player, false);
            return;
        }
        Swap swap = EtFuturumElytraSwap.begin(event.player, chest, true);
        if (swap != null)
        {
            swaps.put(event.player.getUniqueID(), swap);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerTickEnd(PlayerTickEvent event)
    {
        if (event.phase == Phase.END)
        {
            finish(event.player,
                  swaps(event.side).remove(event.player.getUniqueID()));
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event)
    {
        UUID playerId = event.player.getUniqueID();
        Map<UUID, Swap> active = event.player.worldObj.isRemote
              ? clientSwaps : serverSwaps;
        finish(event.player, active.remove(playerId));
        (event.player.worldObj.isRemote ? serverSwaps : clientSwaps)
              .remove(playerId);
    }

    private static void finish(EntityPlayer player, Swap swap)
    {
        if (swap != null && EtFuturumElytraSwap.restore(player, swap)
              && !player.worldObj.isRemote)
        {
            ItemHDPEElytra.applyProxyDamage(swap.getOriginal(),
                  swap.getProxyDamage());
        }
    }

    private Map<UUID, Swap> swaps(Side side)
    {
        return side == Side.CLIENT ? clientSwaps : serverSwaps;
    }
}
