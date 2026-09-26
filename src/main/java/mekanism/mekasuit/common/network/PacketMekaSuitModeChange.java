package mekanism.mekasuit.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModificationStationOperations;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Server-validated armor-mode hotkey update for the MekaSuit Jetpack Unit. */
public final class PacketMekaSuitModeChange implements IMessage {

    private String mode = "normal";

    public PacketMekaSuitModeChange() {
    }

    public PacketMekaSuitModeChange(String mode) {
        this.mode = mode == null ? "normal" : mode;
    }

    @Override
    public void fromBytes(ByteBuf data) {
        mode = ByteBufUtils.readUTF8String(data);
    }

    @Override
    public void toBytes(ByteBuf data) {
        ByteBufUtils.writeUTF8String(data, mode.length() > 64 ? mode.substring(0, 64) : mode);
    }

    public static final class Handler implements IMessageHandler<PacketMekaSuitModeChange, IMessage> {

        @Override
        public IMessage onMessage(PacketMekaSuitModeChange message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack bodyarmor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                if (!bodyarmor.isEmpty() && bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor) {
                    ModificationStationOperations.setMode(bodyarmor, MekaSuitModules.JETPACK_UNIT.getId(), message.mode);
                }
            });
            return null;
        }
    }
}
