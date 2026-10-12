package mekanism.mekasuit.common.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.api.gear.ModuleRegistry;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.content.gear.ModificationStationOperations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/** Server validation for the dedicated pants/boots module-mode key. */
public final class PacketMekaSuitArmorMode implements IMessage
{
    private int armorSlot = -1;
    private ResourceLocation moduleId;
    private String value = "";
    private boolean toggle;

    public PacketMekaSuitArmorMode()
    {
    }

    public PacketMekaSuitArmorMode(int armorSlot,
          ResourceLocation moduleId, String value, boolean toggle)
    {
        this.armorSlot = armorSlot;
        this.moduleId = moduleId;
        this.value = value == null ? "" : value;
        this.toggle = toggle;
    }

    @Override
    public void fromBytes(ByteBuf data)
    {
        armorSlot = data.readByte();
        try
        {
            moduleId = new ResourceLocation(
                  ByteBufUtils.readUTF8String(data));
        }
        catch (RuntimeException ignored)
        {
            moduleId = null;
        }
        value = ByteBufUtils.readUTF8String(data);
        toggle = data.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf data)
    {
        data.writeByte(armorSlot);
        ByteBufUtils.writeUTF8String(data,
              moduleId == null ? "" : moduleId.toString());
        ByteBufUtils.writeUTF8String(data,
              value.length() > 64 ? value.substring(0, 64) : value);
        data.writeBoolean(toggle);
    }

    public static final class Handler implements
          IMessageHandler<PacketMekaSuitArmorMode, IMessage>
    {
        @Override
        public IMessage onMessage(PacketMekaSuitArmorMode message,
              MessageContext context)
        {
            apply(message, PacketHandler.getPlayer(context));
            return null;
        }

        static boolean apply(PacketMekaSuitArmorMode message,
              EntityPlayer player)
        {
            if (message == null || player == null
                  || message.moduleId == null
                  || message.armorSlot < 0 || message.armorSlot > 1)
            {
                return false;
            }
            ItemStack stack = player.getCurrentArmor(message.armorSlot);
            if (stack == null
                  || !(stack.getItem() instanceof IModuleContainerItem))
            {
                return false;
            }
            ModuleType type = ModuleRegistry.getInstance().get(
                  message.moduleId);
            IModuleContainerItem item =
                  (IModuleContainerItem)stack.getItem();
            if (type == null || !type.supports(item.getModuleTarget())
                  || !type.handlesModeChange())
            {
                return false;
            }
            return message.toggle
                  ? ModificationStationOperations.toggleEnabled(stack,
                        message.moduleId)
                  : ModificationStationOperations.setMode(stack,
                        message.moduleId, message.value);
        }
    }
}
