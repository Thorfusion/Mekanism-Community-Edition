package mekanism.mekasuit.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModificationStationOperations;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Server-authoritative armor-key toggle for the Elytra Unit. */
public final class PacketMekaSuitElytraMode implements IMessage
{
    private boolean enabled;

    public PacketMekaSuitElytraMode()
    {
    }

    public PacketMekaSuitElytraMode(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public void fromBytes(ByteBuf data)
    {
        enabled = data.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf data)
    {
        data.writeBoolean(enabled);
    }

    public static final class Handler implements
          IMessageHandler<PacketMekaSuitElytraMode, IMessage>
    {
        @Override
        public IMessage onMessage(PacketMekaSuitElytraMode message,
              MessageContext context)
        {
            setEnabled(PacketHandler.getPlayer(context), message.enabled);
            return null;
        }

        static boolean setEnabled(EntityPlayer player, boolean enabled)
        {
            if (player == null)
            {
                return false;
            }
            ItemStack bodyarmor = player.getCurrentArmor(2);
            return bodyarmor != null
                  && bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor
                  && ModificationStationOperations.setEnabled(bodyarmor,
                        MekaSuitModules.ELYTRA_UNIT.getId(), enabled);
        }
    }
}
