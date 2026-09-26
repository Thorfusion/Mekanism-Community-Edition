package mekanism.mekasuit.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.mekasuit.common.content.gear.MekaSuitElytraHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Starts validated custom-item gliding where 1.12's vanilla packet only accepts Items.ELYTRA. */
public final class PacketMekaSuitStartElytra implements IMessage {

    @Override
    public void fromBytes(ByteBuf data) {
    }

    @Override
    public void toBytes(ByteBuf data) {
    }

    public static final class Handler implements IMessageHandler<PacketMekaSuitStartElytra, IMessage> {

        @Override
        public IMessage onMessage(PacketMekaSuitStartElytra message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                if (!player.onGround && player.motionY < 0D && !player.isElytraFlying()
                      && !player.isInWater() && !player.capabilities.isFlying
                      && MekaSuitElytraHelper.canUse(chest, player)) {
                    player.setElytraFlying();
                }
            });
            return null;
        }
    }
}
