package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

/** Runs Charge Distribution after other bodyarmor module work on the server. */
public final class MekaSuitChargeDistributionHandler {

    public static final MekaSuitChargeDistributionHandler INSTANCE = new MekaSuitChargeDistributionHandler();

    private MekaSuitChargeDistributionHandler() {
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != Phase.END || event.side != Side.SERVER) {
            return;
        }
        ItemStack bodyarmor = event.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!bodyarmor.isEmpty() && bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor) {
            MekaSuitChargeDistributionHelper.tick(bodyarmor, event.player);
        }
    }
}
