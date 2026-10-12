package mekanism.mekasuit.common.content.gear;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.item.ItemStack;

/** Runs Charge Distribution after other bodyarmor work on the server. */
public final class MekaSuitChargeDistributionHandler
{
    public static final MekaSuitChargeDistributionHandler INSTANCE =
          new MekaSuitChargeDistributionHandler();

    private MekaSuitChargeDistributionHandler()
    {
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase != Phase.END || event.side != Side.SERVER)
        {
            return;
        }
        ItemStack bodyarmor = event.player.getCurrentArmor(2);
        if (bodyarmor != null
              && bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor)
        {
            MekaSuitChargeDistributionHelper.tick(bodyarmor, event.player);
        }
    }
}
