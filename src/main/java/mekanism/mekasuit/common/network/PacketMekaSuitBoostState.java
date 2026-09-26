package mekanism.mekasuit.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.mekasuit.common.content.gear.MekaSuitGravitationalHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Synchronizes the module-local boost key without extending Core's legacy key map. */
public final class PacketMekaSuitBoostState implements IMessage {

    private boolean boosting;

    public PacketMekaSuitBoostState() {
    }

    public PacketMekaSuitBoostState(boolean boosting) {
        this.boosting = boosting;
    }

    @Override
    public void fromBytes(ByteBuf data) {
        boosting = data.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf data) {
        data.writeBoolean(boosting);
    }

    public static final class Handler implements IMessageHandler<PacketMekaSuitBoostState, IMessage> {

        @Override
        public IMessage onMessage(PacketMekaSuitBoostState message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() ->
                  MekaSuitGravitationalHandler.INSTANCE.setBoosting(player, message.boosting));
            return null;
        }
    }
}
