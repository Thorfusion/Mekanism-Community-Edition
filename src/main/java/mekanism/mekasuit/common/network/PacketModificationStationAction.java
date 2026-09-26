package mekanism.mekasuit.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.common.util.SecurityUtils;
import mekanism.mekasuit.common.content.gear.ModificationStationOperations;
import mekanism.mekasuit.common.inventory.ContainerModificationStation;
import mekanism.mekasuit.common.tile.TileEntityModificationStation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Server-validated controls for installed modules in the station's gear slot. */
public final class PacketModificationStationAction implements IMessage {

    private Action action = Action.REMOVE_ONE;
    private ResourceLocation moduleId;
    private String value = "";
    private boolean booleanValue;

    public PacketModificationStationAction() {
    }

    public PacketModificationStationAction(Action action, ResourceLocation moduleId) {
        this(action, moduleId, "", false);
    }

    public PacketModificationStationAction(Action action, ResourceLocation moduleId, String value, boolean booleanValue) {
        this.action = action == null ? Action.REMOVE_ONE : action;
        this.moduleId = moduleId;
        this.value = value == null ? "" : value;
        this.booleanValue = booleanValue;
    }

    @Override
    public void fromBytes(ByteBuf data) {
        action = Action.byIndex(data.readUnsignedByte());
        String id = ByteBufUtils.readUTF8String(data);
        try {
            moduleId = id.isEmpty() ? null : new ResourceLocation(id);
        } catch (RuntimeException ignored) {
            moduleId = null;
        }
        value = ByteBufUtils.readUTF8String(data);
        booleanValue = data.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf data) {
        data.writeByte(action.ordinal());
        ByteBufUtils.writeUTF8String(data, moduleId == null ? "" : moduleId.toString());
        ByteBufUtils.writeUTF8String(data, value.length() > 64 ? value.substring(0, 64) : value);
        data.writeBoolean(booleanValue);
    }

    public enum Action {
        REMOVE_ONE,
        REMOVE_ALL,
        TOGGLE_ENABLED,
        SET_MODE,
        SET_BOOLEAN_CONFIG,
        SET_ENUM_CONFIG;

        private static Action byIndex(int index) {
            Action[] values = values();
            return index >= 0 && index < values.length ? values[index] : REMOVE_ONE;
        }
    }

    public static final class Handler implements IMessageHandler<PacketModificationStationAction, IMessage> {

        @Override
        public IMessage onMessage(PacketModificationStationAction message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> handle(message, player));
            return null;
        }

        private static void handle(PacketModificationStationAction message, EntityPlayerMP player) {
            if (message.moduleId == null || !(player.openContainer instanceof ContainerModificationStation)
                  || !player.openContainer.canInteractWith(player)) {
                return;
            }
            ContainerModificationStation container = (ContainerModificationStation) player.openContainer;
            TileEntityModificationStation tile = container.getTile();
            if (!SecurityUtils.canAccess(player, tile)) {
                return;
            }
            ItemStack host = tile.getContainerStack();
            boolean changed = false;
            switch (message.action) {
                case REMOVE_ONE:
                case REMOVE_ALL:
                    ItemStack removed = ModificationStationOperations.remove(host, message.moduleId,
                          message.action == Action.REMOVE_ALL ? Integer.MAX_VALUE : 1);
                    if (!removed.isEmpty()) {
                        changed = true;
                        player.inventory.addItemStackToInventory(removed);
                        if (!removed.isEmpty()) {
                            player.dropItem(removed, false);
                        }
                    }
                    break;
                case TOGGLE_ENABLED:
                    changed = ModificationStationOperations.toggleEnabled(host, message.moduleId);
                    break;
                case SET_MODE:
                    changed = ModificationStationOperations.setMode(host, message.moduleId, message.value);
                    break;
                case SET_BOOLEAN_CONFIG:
                    changed = ModificationStationOperations.setBooleanConfig(host, message.moduleId,
                          message.value, message.booleanValue);
                    break;
                case SET_ENUM_CONFIG:
                    int separator = message.value.indexOf('=');
                    if (separator > 0) {
                        changed = ModificationStationOperations.setEnumConfig(host, message.moduleId,
                              message.value.substring(0, separator), message.value.substring(separator + 1));
                    }
                    break;
                default:
                    return;
            }
            if (changed) {
                tile.markDirty();
                container.detectAndSendChanges();
            }
        }
    }
}
