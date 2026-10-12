package mekanism.ultimate.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import mekanism.ultimate.common.UltimateItems;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import mekanism.ultimate.common.item.ItemHDPEElytra;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Server-validated request to enter Et Futurum Elytra flight. */
public final class PacketStartHDPEElytra implements IMessage
{
    @Override
    public void fromBytes(ByteBuf data)
    {
    }

    @Override
    public void toBytes(ByteBuf data)
    {
    }

    public static final class Handler implements
          IMessageHandler<PacketStartHDPEElytra, IMessage>
    {
        @Override
        public IMessage onMessage(PacketStartHDPEElytra message,
              MessageContext context)
        {
            startFlight(PacketHandler.getPlayer(context));
            return null;
        }

        static boolean startFlight(EntityPlayer player)
        {
            if (player == null || player.onGround || player.motionY >= 0D
                  || player.isInWater() || player.ridingEntity != null
                  || player.capabilities.isFlying
                  || EtFuturumElytraCompat.isFlying(player))
            {
                return false;
            }
            ItemStack chest = player.getCurrentArmor(2);
            return chest != null
                  && chest.getItem() == UltimateItems.HDPEElytra
                  && ItemHDPEElytra.isUsable(chest)
                  && EtFuturumElytraCompat.setFlying(player, true);
        }
    }
}
