package mekanism.mekasuit.common.content.gear;

import mekanism.common.KeySync;
import mekanism.common.Mekanism;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

/** Server-authoritative MekaSuit jetpack controller, isolated from the legacy ItemJetpack hard casts. */
public final class MekaSuitJetpackHandler {

    public static final MekaSuitJetpackHandler INSTANCE = new MekaSuitJetpackHandler();

    private MekaSuitJetpackHandler() {
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != Phase.END || event.side != Side.SERVER) {
            return;
        }
        EntityPlayer player = event.player;
        ItemStack bodyarmor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (bodyarmor.isEmpty() || !(bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor)) {
            return;
        }
        boolean ascending = Mekanism.keyMap.has(player, KeySync.ASCEND);
        boolean descending = Mekanism.keyMap.has(player, KeySync.DESCEND);
        if (!MekaSuitJetpackHelper.isActive(bodyarmor, player, ascending, descending)) {
            return;
        }
        if (MekaSuitJetpackHelper.applyMotion(player, MekaSuitJetpackHelper.getMode(bodyarmor),
              MekaSuitJetpackHelper.getThrust(bodyarmor), ascending, descending)) {
            player.fallDistance = 0F;
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).connection.floatingTickCount = 0;
            }
        }
        MekaSuitJetpackHelper.useFuel(bodyarmor);
    }
}
