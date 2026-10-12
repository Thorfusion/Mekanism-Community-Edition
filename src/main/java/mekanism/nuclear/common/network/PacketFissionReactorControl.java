package mekanism.nuclear.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mekanism.common.PacketHandler;
import mekanism.nuclear.common.inventory.ContainerFissionReactor;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import net.minecraft.entity.player.EntityPlayer;

/** Validated client request for the small set of reactor controls. */
public final class PacketFissionReactorControl implements IMessage
{
    private Action action = Action.SCRAM;
    private double value;

    public PacketFissionReactorControl()
    {
    }

    public PacketFissionReactorControl(Action action)
    {
        this(action, 0);
    }

    public PacketFissionReactorControl(Action action, double value)
    {
        this.action = action == null ? Action.SCRAM : action;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf data)
    {
        action = Action.byIndex(data.readByte());
        value = data.readDouble();
    }

    @Override
    public void toBytes(ByteBuf data)
    {
        data.writeByte(action.ordinal());
        data.writeDouble(value);
    }

    Action getAction()
    {
        return action;
    }

    double getValue()
    {
        return value;
    }

    public enum Action
    {
        ACTIVATE,
        SCRAM,
        SET_BURN_RATE;

        static Action byIndex(int index)
        {
            Action[] actions = values();
            return index >= 0 && index < actions.length
                  ? actions[index] : SCRAM;
        }
    }

    public static final class Handler
          implements IMessageHandler<PacketFissionReactorControl, IMessage>
    {
        @Override
        public IMessage onMessage(PacketFissionReactorControl message,
              MessageContext context)
        {
            handle(message, PacketHandler.getPlayer(context));
            return null;
        }

        static void handle(PacketFissionReactorControl message,
              EntityPlayer player)
        {
            if (player == null
                  || !(player.openContainer instanceof ContainerFissionReactor))
            {
                return;
            }
            ContainerFissionReactor container =
                  (ContainerFissionReactor)player.openContainer;
            if (!container.canInteractWith(player))
            {
                return;
            }
            TileEntityFissionReactorPort controller =
                  container.getTile().getControllerTile();
            if (controller == null
                  || !controller.getReactorState().isFormed())
            {
                return;
            }
            switch (message.action)
            {
                case ACTIVATE:
                    controller.getReactorState().setActive(true);
                    break;
                case SCRAM:
                    controller.getReactorState().setActive(false);
                    break;
                case SET_BURN_RATE:
                    if (!Double.isFinite(message.value) || message.value < 0)
                    {
                        return;
                    }
                    controller.getReactorState().setBurnRate(
                          Math.round(message.value * 100D) / 100D);
                    break;
                default:
                    return;
            }
            controller.stateChanged();
        }
    }
}
