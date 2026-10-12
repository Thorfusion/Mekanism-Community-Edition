package mekanism.ultimate.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.UltimateItems;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import mekanism.ultimate.common.item.ItemHDPEElytra;
import mekanism.ultimate.common.network.PacketStartHDPEElytra;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Sends a start-flight request for the custom reinforced Elytra item. */
@SideOnly(Side.CLIENT)
public final class HDPEElytraClientHandler
{
    public static final HDPEElytraClientHandler INSTANCE =
          new HDPEElytraClientHandler();

    private boolean jumpKeyDown;

    private HDPEElytraClientHandler()
    {
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase != Phase.END)
        {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (player == null)
        {
            jumpKeyDown = false;
            return;
        }
        ItemStack chest = player.getCurrentArmor(2);
        boolean currentJumpKey = minecraft.currentScreen == null
              && minecraft.gameSettings.keyBindJump.getIsKeyPressed();
        boolean request = currentJumpKey && !jumpKeyDown
              && EtFuturumElytraCompat.isAvailable()
              && !player.onGround && player.motionY < 0D
              && !EtFuturumElytraCompat.isFlying(player)
              && !player.isInWater() && player.ridingEntity == null
              && !player.capabilities.isFlying && chest != null
              && chest.getItem() == UltimateItems.HDPEElytra
              && ItemHDPEElytra.isUsable(chest);
        jumpKeyDown = currentJumpKey;
        if (request)
        {
            MekanismUltimate.network.sendToServer(
                  new PacketStartHDPEElytra());
        }
    }
}
