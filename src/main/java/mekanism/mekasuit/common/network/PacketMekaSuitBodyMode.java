package mekanism.mekasuit.common.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.content.gear.MekaSuitGravitationalHandler;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Server validation for wearable bodyarmor mode and boost input. */
public final class PacketMekaSuitBodyMode implements IMessage
{
    private Action action = Action.JETPACK_MODE;
    private String value = "";
    private boolean enabled;

    public PacketMekaSuitBodyMode()
    {
    }

    public PacketMekaSuitBodyMode(Action action, String value,
          boolean enabled)
    {
        this.action = action == null ? Action.JETPACK_MODE : action;
        this.value = value == null ? "" : value;
        this.enabled = enabled;
    }

    @Override
    public void fromBytes(ByteBuf data)
    {
        action = Action.byIndex(data.readUnsignedByte());
        value = ByteBufUtils.readUTF8String(data);
        enabled = data.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf data)
    {
        data.writeByte(action.ordinal());
        ByteBufUtils.writeUTF8String(data,
              value.length() > 32 ? value.substring(0, 32) : value);
        data.writeBoolean(enabled);
    }

    public enum Action
    {
        JETPACK_MODE,
        GRAVITATIONAL_ENABLED,
        BOOSTING;

        private static Action byIndex(int index)
        {
            return index >= 0 && index < values().length
                  ? values()[index] : JETPACK_MODE;
        }
    }

    public static final class Handler implements
          IMessageHandler<PacketMekaSuitBodyMode, IMessage>
    {
        @Override
        public IMessage onMessage(PacketMekaSuitBodyMode message,
              MessageContext context)
        {
            apply(message, PacketHandler.getPlayer(context));
            return null;
        }

        static boolean apply(PacketMekaSuitBodyMode message,
              EntityPlayer player)
        {
            if (message == null || player == null)
            {
                return false;
            }
            ItemStack bodyarmor = player.getCurrentArmor(2);
            if (message.action == Action.BOOSTING)
            {
                // This is input state, not an authority decision. Each
                // consuming handler validates its own equipped unit and cost.
                MekaSuitGravitationalHandler.INSTANCE.setBoosting(player,
                      message.enabled);
                return true;
            }
            if (bodyarmor == null
                  || !(bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor))
            {
                return false;
            }
            ModuleContainer modules = ModuleContainer.fromStack(bodyarmor,
                  ModuleTarget.BODYARMOR);
            boolean changed;
            if (message.action == Action.JETPACK_MODE)
            {
                changed = modules.setMode(MekaSuitModules.JETPACK_UNIT,
                      message.value);
            }
            else
            {
                changed = modules.setEnabled(
                      MekaSuitModules.GRAVITATIONAL_MODULATING_UNIT,
                      message.enabled);
            }
            if (changed)
            {
                modules.save(bodyarmor);
            }
            return changed;
        }
    }
}
