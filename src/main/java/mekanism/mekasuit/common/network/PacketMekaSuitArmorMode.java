package mekanism.mekasuit.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleRegistry;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.content.gear.ModificationStationOperations;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Server validation for the dedicated pants/boots unit-mode key. */
public final class PacketMekaSuitArmorMode implements IMessage {

    private int slot;
    private ResourceLocation moduleId;
    private String value = "";
    private boolean toggle;

    public PacketMekaSuitArmorMode() {
    }

    public PacketMekaSuitArmorMode(EntityEquipmentSlot slot, ResourceLocation moduleId,
          String value, boolean toggle) {
        this.slot = slot == null ? -1 : slot.ordinal();
        this.moduleId = moduleId;
        this.value = value == null ? "" : value;
        this.toggle = toggle;
    }

    @Override
    public void fromBytes(ByteBuf data) {
        slot = data.readByte();
        try {
            moduleId = new ResourceLocation(ByteBufUtils.readUTF8String(data));
        } catch (RuntimeException ignored) {
            moduleId = null;
        }
        value = ByteBufUtils.readUTF8String(data);
        toggle = data.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf data) {
        data.writeByte(slot);
        ByteBufUtils.writeUTF8String(data, moduleId == null ? "" : moduleId.toString());
        ByteBufUtils.writeUTF8String(data, value.length() > 64 ? value.substring(0, 64) : value);
        data.writeBoolean(toggle);
    }

    public static final class Handler implements IMessageHandler<PacketMekaSuitArmorMode, IMessage> {

        @Override
        public IMessage onMessage(PacketMekaSuitArmorMode message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private static void handle(PacketMekaSuitArmorMode message, EntityPlayerMP player) {
            EntityEquipmentSlot[] slots = EntityEquipmentSlot.values();
            if (message.slot < 0 || message.slot >= slots.length || message.moduleId == null) {
                return;
            }
            EntityEquipmentSlot slot = slots[message.slot];
            if (slot != EntityEquipmentSlot.LEGS && slot != EntityEquipmentSlot.FEET) {
                return;
            }
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof IModuleContainerItem)) {
                return;
            }
            ModuleType type = ModuleRegistry.getInstance().get(message.moduleId);
            IModuleContainerItem item = (IModuleContainerItem) stack.getItem();
            if (type == null || !type.supports(item.getModuleTarget()) || !type.handlesModeChange()) {
                return;
            }
            if (message.toggle) {
                ModificationStationOperations.toggleEnabled(stack, message.moduleId);
            } else {
                ModificationStationOperations.setMode(stack, message.moduleId, message.value);
            }
        }
    }
}
