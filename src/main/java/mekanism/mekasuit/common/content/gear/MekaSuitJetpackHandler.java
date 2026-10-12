package mekanism.mekasuit.common.content.gear;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import mekanism.common.KeySync;
import mekanism.common.Mekanism;
import mekanism.common.ObfuscatedNames;
import mekanism.common.util.MekanismUtils;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

/** Server-authoritative MekaSuit jetpack controller. */
public final class MekaSuitJetpackHandler
{
    public static final MekaSuitJetpackHandler INSTANCE =
          new MekaSuitJetpackHandler();

    private MekaSuitJetpackHandler()
    {
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase != Phase.END || event.side != Side.SERVER)
        {
            return;
        }
        EntityPlayer player = event.player;
        ItemStack bodyarmor = player.getCurrentArmor(2);
        if (bodyarmor == null
              || !(bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor))
        {
            return;
        }
        boolean ascending = Mekanism.keyMap.has(player, KeySync.ASCEND);
        boolean descending = Mekanism.keyMap.has(player, KeySync.DESCEND);
        if (!MekaSuitJetpackHelper.isActive(bodyarmor, player, ascending,
              descending))
        {
            return;
        }
        if (MekaSuitJetpackHelper.applyMotion(player,
              MekaSuitJetpackHelper.getMode(bodyarmor),
              MekaSuitJetpackHelper.getThrust(bodyarmor), ascending,
              descending))
        {
            player.fallDistance = 0;
            if (player instanceof EntityPlayerMP)
            {
                MekanismUtils.setPrivateValue(
                      ((EntityPlayerMP)player).playerNetServerHandler, 0,
                      NetHandlerPlayServer.class,
                      ObfuscatedNames.NetHandlerPlayServer_floatingTickCount);
            }
        }
        MekaSuitJetpackHelper.useFuel(bodyarmor);
    }
}
