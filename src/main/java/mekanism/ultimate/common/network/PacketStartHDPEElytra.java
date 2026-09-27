package mekanism.ultimate.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.ultimate.common.UltimateItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Starts server-validated flight for the custom reinforced Elytra item. */
public final class PacketStartHDPEElytra implements IMessage {

    @Override
    public void fromBytes(ByteBuf data) {
    }

    @Override
    public void toBytes(ByteBuf data) {
    }

    public static final class Handler implements IMessageHandler<PacketStartHDPEElytra, IMessage> {

        @Override
        public IMessage onMessage(PacketStartHDPEElytra message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                if (!player.onGround && player.motionY < 0D && !player.isElytraFlying()
                      && !player.isInWater() && !player.capabilities.isFlying
                      && chest.getItem() == UltimateItems.HDPEElytra && ItemElytra.isUsable(chest)) {
                    player.setElytraFlying();
                }
            });
            return null;
        }
    }
}
