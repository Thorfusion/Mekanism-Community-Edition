package mekanism.mekasuit.common.content.gear;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import mekanism.ultimate.common.integration.EtFuturumElytraSwap;
import mekanism.ultimate.common.integration.EtFuturumElytraSwap.Swap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Adapts enabled MekaSuit Elytra Units to Et Futurum's item-only flight tick. */
public final class MekaSuitElytraTickHandler
{
    public static final MekaSuitElytraTickHandler INSTANCE =
          new MekaSuitElytraTickHandler();

    private final Map<UUID, Swap> serverSwaps =
          new ConcurrentHashMap<UUID, Swap>();
    private final Map<UUID, Swap> clientSwaps =
          new ConcurrentHashMap<UUID, Swap>();

    private MekaSuitElytraTickHandler()
    {
    }

    /* Keep the proxy installed while Et Futurum performs its tick update. */
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
        /* Leave a reinforced Elytra (or another compatible addon) to its own
         * bridge handler; otherwise the two Mekanism handlers cancel each
         * other's active flight state at the same event priority. */
        if (chest == null
              || !(chest.getItem() instanceof ItemMekaSuitBodyarmor))
        {
            return;
        }
        if (!MekaSuitElytraHelper.canUse(chest, event.player)
              || event.player.onGround || event.player.isInWater()
              || event.player.ridingEntity != null
              || event.player.capabilities.isFlying)
        {
            EtFuturumElytraCompat.setFlying(event.player, false);
            return;
        }
        Swap swap = EtFuturumElytraSwap.begin(event.player, chest, false);
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
              && !player.worldObj.isRemote && swap.getProxyDamage() > 0
              && !player.capabilities.isCreativeMode)
        {
            MekaSuitElytraHelper.useEnergy(swap.getOriginal(),
                  MekaSuitElytraHelper.getEnergyUsage());
        }
    }

    private Map<UUID, Swap> swaps(Side side)
    {
        return side == Side.CLIENT ? clientSwaps : serverSwaps;
    }
}
