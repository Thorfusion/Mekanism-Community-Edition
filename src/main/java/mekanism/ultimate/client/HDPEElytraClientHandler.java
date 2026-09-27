package mekanism.ultimate.client;

import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.UltimateItems;
import mekanism.ultimate.common.network.PacketStartHDPEElytra;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Sends the custom start-flight request that vanilla 1.12 refuses for non-vanilla Elytra items. */
@SideOnly(Side.CLIENT)
public final class HDPEElytraClientHandler {

    public static final HDPEElytraClientHandler INSTANCE = new HDPEElytraClientHandler();
    private boolean startRequested;

    private HDPEElytraClientHandler() {
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        if (player == null) {
            startRequested = false;
            return;
        }
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        boolean request = minecraft.currentScreen == null && minecraft.gameSettings.keyBindJump.isKeyDown()
              && !player.onGround && player.motionY < 0D && !player.isElytraFlying()
              && !player.isInWater() && !player.capabilities.isFlying
              && chest.getItem() == UltimateItems.HDPEElytra && ItemElytra.isUsable(chest);
        if (request && !startRequested) {
            MekanismUltimate.network.sendToServer(new PacketStartHDPEElytra());
        }
        startRequested = request;
    }
}
