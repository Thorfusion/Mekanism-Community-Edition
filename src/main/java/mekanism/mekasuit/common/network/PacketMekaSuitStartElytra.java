package mekanism.mekasuit.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import mekanism.mekasuit.common.content.gear.MekaSuitElytraHelper;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Server-validated request to start gliding with a MekaSuit Elytra Unit. */
public final class PacketMekaSuitStartElytra implements IMessage
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
          IMessageHandler<PacketMekaSuitStartElytra, IMessage>
    {
        @Override
        public IMessage onMessage(PacketMekaSuitStartElytra message,
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
            return MekaSuitElytraHelper.canUse(chest, player)
                  && EtFuturumElytraCompat.setFlying(player, true);
        }
    }
}
