package mekanism.nuclear.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.nuclear.common.inventory.ContainerFissionReactor;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Validated client request for the small set of Fission Reactor controls. */
public final class PacketFissionReactorControl implements IMessage {

    private Action action = Action.SCRAM;
    private double value;

    public PacketFissionReactorControl() {
    }

    public PacketFissionReactorControl(Action action) {
        this(action, 0);
    }

    public PacketFissionReactorControl(Action action, double value) {
        this.action = action;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf data) {
        action = Action.byIndex(data.readByte());
        value = data.readDouble();
    }

    @Override
    public void toBytes(ByteBuf data) {
        data.writeByte(action.ordinal());
        data.writeDouble(value);
    }

    public enum Action {
        ACTIVATE,
        SCRAM,
        SET_BURN_RATE;

        private static Action byIndex(int index) {
            Action[] values = values();
            return index >= 0 && index < values.length ? values[index] : SCRAM;
        }
    }

    public static final class Handler implements IMessageHandler<PacketFissionReactorControl, IMessage> {

        @Override
        public IMessage onMessage(PacketFissionReactorControl message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private static void handle(PacketFissionReactorControl message, EntityPlayerMP player) {
            if (!(player.openContainer instanceof ContainerFissionReactor)) {
                return;
            }
            TileEntityFissionReactorPort port = ((ContainerFissionReactor) player.openContainer).getTile();
            if (!player.openContainer.canInteractWith(player)) {
                return;
            }
            TileEntityFissionReactorPort controller = port.getControllerTile();
            if (controller == null || !controller.getReactorState().isFormed()) {
                return;
            }
            switch (message.action) {
                case ACTIVATE:
                    controller.getReactorState().setActive(true);
                    break;
                case SCRAM:
                    controller.getReactorState().setActive(false);
                    break;
                case SET_BURN_RATE:
                    if (!Double.isFinite(message.value) || message.value < 0) {
                        return;
                    }
                    double rounded = Math.round(message.value * 100D) / 100D;
                    controller.getReactorState().setBurnRate(rounded);
                    break;
                default:
                    return;
            }
            controller.stateChanged();
        }
    }
}
