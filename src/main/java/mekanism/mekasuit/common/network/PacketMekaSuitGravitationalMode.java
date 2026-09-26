package mekanism.mekasuit.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModificationStationOperations;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Server-validated armor-mode hotkey toggle for the Gravitational Modulating Unit. */
public final class PacketMekaSuitGravitationalMode implements IMessage {

    private boolean enabled;

    public PacketMekaSuitGravitationalMode() {
    }

    public PacketMekaSuitGravitationalMode(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void fromBytes(ByteBuf data) {
        enabled = data.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf data) {
        data.writeBoolean(enabled);
    }

    public static final class Handler implements IMessageHandler<PacketMekaSuitGravitationalMode, IMessage> {

        @Override
        public IMessage onMessage(PacketMekaSuitGravitationalMode message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack bodyarmor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                if (!bodyarmor.isEmpty() && bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor) {
                    ModificationStationOperations.setEnabled(bodyarmor,
                          MekaSuitModules.GRAVITATIONAL_MODULATING_UNIT.getId(), message.enabled);
                }
            });
            return null;
        }
    }
}
