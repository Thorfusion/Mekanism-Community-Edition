package mekanism.ultimate.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.inventory.ContainerDimensionalStabilizer;
import mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer;
import net.minecraft.entity.player.EntityPlayer;

/** Server-authoritative chunk-selection request. */
public final class PacketDimensionalStabilizer implements IMessage
{
    private int x;
    private int z;

    public PacketDimensionalStabilizer()
    {
    }

    public PacketDimensionalStabilizer(int x, int z)
    {
        this.x = x;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf data)
    {
        x = data.readUnsignedByte();
        z = data.readUnsignedByte();
    }

    @Override
    public void toBytes(ByteBuf data)
    {
        data.writeByte(x);
        data.writeByte(z);
    }

    public static final class Handler implements
          IMessageHandler<PacketDimensionalStabilizer, IMessage>
    {
        @Override
        public IMessage onMessage(PacketDimensionalStabilizer message,
              MessageContext context)
        {
            EntityPlayer player = PacketHandler.getPlayer(context);
            if (player == null || !(player.openContainer
                  instanceof ContainerDimensionalStabilizer)
                  || !player.openContainer.canInteractWith(player))
            {
                return null;
            }
            TileEntityDimensionalStabilizer tile =
                  ((ContainerDimensionalStabilizer)player.openContainer)
                        .getTile();
            if (SecurityUtils.canAccess(player, tile))
            {
                tile.toggleChunkLoadingAt(message.x, message.z);
                player.openContainer.detectAndSendChanges();
            }
            return null;
        }
    }
}
