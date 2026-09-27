package mekanism.common.network;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import mekanism.api.Coord4D;
import mekanism.api.TileNetworkList;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.frequency.Frequency;
import mekanism.common.frequency.Frequency.AccessMode;
import mekanism.common.frequency.FrequencyManager;
import mekanism.common.frequency.TrustedFrequencyUtils;
import mekanism.common.item.ItemPortableTeleporter;
import mekanism.common.network.PacketPortableTeleporter.PortableTeleporterMessage;
import mekanism.common.network.PacketPortalFX.PortalFXMessage;
import mekanism.common.tile.TileEntityTeleporter;
import mekanism.common.util.SecurityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPortableTeleporter implements IMessageHandler<PortableTeleporterMessage, IMessage> {

    @Override
    public IMessage onMessage(PortableTeleporterMessage message, MessageContext context) {
        EntityPlayer player = PacketHandler.getPlayer(context);
        PacketHandler.handlePacket(() -> {
            ItemStack itemstack = player.getHeldItem(message.currentHand);
            World world = player.world;
            if (!itemstack.isEmpty() && itemstack.getItem() instanceof ItemPortableTeleporter) {
                ItemPortableTeleporter item = (ItemPortableTeleporter) itemstack.getItem();
                switch (message.packetType) {
                    case DATA_REQUEST:
                        sendDataResponse(message.frequency, world, player, item, itemstack, message.currentHand);
                        break;
                    case DATA_RESPONSE:
                        Mekanism.proxy.handleTeleporterUpdate(message);
                        break;
                    case SET_FREQ:
                        FrequencyManager manager1 = getManager(message.frequency, player.getUniqueID(), world);
                        if (manager1 == null) break;
                        Frequency toUse = null;
                        for (Frequency freq : manager1.getFrequencies()) {
                            if (freq.name.equals(message.frequency.name)
                                  && freq.getAccessMode() == message.frequency.getAccessMode()) {
                                toUse = freq;
                                break;
                            }
                        }
                        if (toUse == null) {
                            if (message.frequency.ownerUUID != null
                                  && !message.frequency.ownerUUID.equals(player.getUniqueID())) break;
                            toUse = new Frequency(message.frequency.name, player.getPersistentID())
                                  .setAccessMode(message.frequency.getAccessMode());
                            manager1.addFrequency(toUse);
                        }
                        item.setFrequency(itemstack, toUse);
                        sendDataResponse(toUse, world, player, item, itemstack, message.currentHand);
                        break;
                    case DEL_FREQ:
                        FrequencyManager manager = getManager(message.frequency, player.getUniqueID(), world);
                        if (manager != null && player.getUniqueID().equals(message.frequency.ownerUUID)) {
                            manager.remove(message.frequency.name, player.getUniqueID());
                        }
                        item.setFrequency(itemstack, null);
                        break;
                    case TELEPORT:
                        FrequencyManager manager2 = getManager(message.frequency, player.getUniqueID(), world);
                        if (manager2 == null) break;
                        Frequency found = null;
                        for (Frequency freq : manager2.getFrequencies()) {
                            if (message.frequency.name.equals(freq.name)
                                  && message.frequency.getAccessMode() == freq.getAccessMode()) {
                                found = freq;
                                break;
                            }
                        }
                        if (found == null) {
                            break;
                        }
                        Coord4D coords = found.getClosestCoords(new Coord4D(player));
                        if (coords != null) {
                            World teleWorld = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(coords.dimensionId);
                            TileEntityTeleporter teleporter = (TileEntityTeleporter) coords.getTileEntity(teleWorld);
                            if (teleporter != null) {
                                try {
                                    teleporter.didTeleport.add(player.getPersistentID());
                                    teleporter.teleDelay = 5;
                                    item.setEnergy(itemstack, item.getEnergy(itemstack) - ItemPortableTeleporter.calculateEnergyCost(player, coords));
                                    if (player instanceof EntityPlayerMP) {
                                        ((EntityPlayerMP) player).connection.floatingTickCount = 0;
                                    }
                                    player.closeScreen();
                                    Mekanism.packetHandler.sendToAllTracking(new PortalFXMessage(new Coord4D(player)), coords);
                                    if (player instanceof EntityPlayerMP) {
                                        TileEntityTeleporter.teleportPlayerTo((EntityPlayerMP) player, coords, teleporter);
                                        TileEntityTeleporter.alignPlayer((EntityPlayerMP) player, coords);
                                    }
                                    world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                    Mekanism.packetHandler.sendToAllTracking(new PortalFXMessage(coords), coords);
                                } catch (Exception ignored) {
                                }
                            }
                        }
                        break;
                }
            }
        }, player);
        return null;
    }

    public void sendDataResponse(Frequency given, World world, EntityPlayer player, ItemPortableTeleporter item, ItemStack itemstack, EnumHand hand) {
        List<Frequency> publicFreqs = new ArrayList<>(getManager(null, world).getFrequencies());
        FrequencyManager ownManager = getManager(player.getUniqueID(), world);
        List<Frequency> privateFreqs = new ArrayList<>();
        List<Frequency> trustedFreqs = new ArrayList<>();
        for (Frequency frequency : ownManager.getFrequencies()) {
            if (frequency.isPrivate()) privateFreqs.add(frequency);
            else if (frequency.isTrusted()) trustedFreqs.add(frequency);
        }
        trustedFreqs.addAll(TrustedFrequencyUtils.collect(Mekanism.privateTeleporters,
              Frequency.class, Frequency.TELEPORTER, player.getUniqueID(), world));
        byte status = 3;
        if (given != null) {
            FrequencyManager manager = getManager(given, player.getUniqueID(), world);
            boolean found = false;
            for (Frequency iterFreq : manager == null ? java.util.Collections.<Frequency>emptyList() : manager.getFrequencies()) {
                // Old portable items stored only name + public/private. Resolve
                // those identities in the already permission-checked manager.
                if (given.name.equals(iterFreq.name)
                      && given.getAccessMode() == iterFreq.getAccessMode()) {
                    given = iterFreq;
                    found = true;
                    break;
                }
            }
            if (!found) {
                given = null;
            }
        }

        if (given != null) {
            if (given.activeCoords.size() == 0) {
                status = 3;
            } else {
                Coord4D coords = given.getClosestCoords(new Coord4D(player));
                double energyNeeded = ItemPortableTeleporter.calculateEnergyCost(player, coords);
                if (energyNeeded > item.getEnergy(itemstack)) {
                    status = 4;
                } else {
                    status = 1;
                }
            }
        }
        Mekanism.packetHandler.sendTo(new PortableTeleporterMessage(hand, given, status,
              publicFreqs, privateFreqs, trustedFreqs), (EntityPlayerMP) player);
    }

    public FrequencyManager getManager(UUID owner, World world) {
        if (owner == null) {
            return Mekanism.publicTeleporters;
        } else if (!Mekanism.privateTeleporters.containsKey(owner)) {
            FrequencyManager manager = new FrequencyManager(Frequency.class, Frequency.TELEPORTER, owner);
            Mekanism.privateTeleporters.put(owner, manager);
            manager.createOrLoad(world);
        }
        return Mekanism.privateTeleporters.get(owner);
    }

    private FrequencyManager getManager(Frequency frequency, UUID requester, World world) {
        if (frequency == null) return null;
        if (frequency.isPublic()) return getManager(null, world);
        UUID owner = frequency.isTrusted() && frequency.ownerUUID != null
              ? frequency.ownerUUID : requester;
        if (frequency.isTrusted() && !SecurityUtils.canUseFrequency(frequency, requester)) return null;
        return getManager(owner, world);
    }

    public enum PortableTeleporterPacketType {
        DATA_REQUEST,
        DATA_RESPONSE,
        SET_FREQ,
        DEL_FREQ,
        TELEPORT
    }

    public static class PortableTeleporterMessage implements IMessage {

        public PortableTeleporterPacketType packetType;

        public EnumHand currentHand;
        public Frequency frequency;
        public byte status;

        public List<Frequency> publicCache = new ArrayList<>();
        public List<Frequency> privateCache = new ArrayList<>();
        public List<Frequency> trustedCache = new ArrayList<>();

        public PortableTeleporterMessage() {
        }

        public PortableTeleporterMessage(PortableTeleporterPacketType type, EnumHand hand, Frequency freq) {
            packetType = type;
            currentHand = hand;
            if (type == PortableTeleporterPacketType.DATA_REQUEST) {
                frequency = freq;
            } else if (type == PortableTeleporterPacketType.SET_FREQ) {
                frequency = freq;
            } else if (type == PortableTeleporterPacketType.DEL_FREQ) {
                frequency = freq;
            } else if (type == PortableTeleporterPacketType.TELEPORT) {
                frequency = freq;
            }
        }

        public PortableTeleporterMessage(EnumHand hand, Frequency freq, byte b,
              List<Frequency> publicFreqs, List<Frequency> privateFreqs,
              List<Frequency> trustedFreqs) {
            packetType = PortableTeleporterPacketType.DATA_RESPONSE;

            currentHand = hand;
            frequency = freq;
            status = b;

            publicCache = publicFreqs;
            privateCache = privateFreqs;
            trustedCache = trustedFreqs;
        }

        @Override
        public void toBytes(ByteBuf buffer) {
            buffer.writeInt(packetType.ordinal());

            if (packetType == PortableTeleporterPacketType.DATA_REQUEST) {
                buffer.writeInt(currentHand.ordinal());
                if (frequency != null) {
                    buffer.writeBoolean(true);
                    writeIdentity(buffer, frequency);
                } else {
                    buffer.writeBoolean(false);
                }
            } else if (packetType == PortableTeleporterPacketType.DATA_RESPONSE) {
                buffer.writeInt(currentHand.ordinal());

                if (frequency != null) {
                    buffer.writeBoolean(true);
                    writeIdentity(buffer, frequency);
                } else {
                    buffer.writeBoolean(false);
                }

                buffer.writeByte(status);

                TileNetworkList data = new TileNetworkList();
                data.add(publicCache.size());

                for (Frequency freq : publicCache) {
                    freq.write(data);
                }

                data.add(privateCache.size());

                for (Frequency freq : privateCache) {
                    freq.write(data);
                }

                data.add(trustedCache.size());
                for (Frequency freq : trustedCache) freq.write(data);

                PacketHandler.encode(data.toArray(), buffer);
            } else if (packetType == PortableTeleporterPacketType.SET_FREQ) {
                buffer.writeInt(currentHand.ordinal());
                writeIdentity(buffer, frequency);
            } else if (packetType == PortableTeleporterPacketType.DEL_FREQ) {
                buffer.writeInt(currentHand.ordinal());
                writeIdentity(buffer, frequency);
            } else if (packetType == PortableTeleporterPacketType.TELEPORT) {
                buffer.writeInt(currentHand.ordinal());
                writeIdentity(buffer, frequency);
            }
        }

        @Override
        public void fromBytes(ByteBuf buffer) {
            packetType = PortableTeleporterPacketType.values()[buffer.readInt()];
            if (packetType == PortableTeleporterPacketType.DATA_REQUEST) {
                currentHand = EnumHand.values()[buffer.readInt()];
                if (buffer.readBoolean()) {
                    frequency = readIdentity(buffer);
                }
            } else if (packetType == PortableTeleporterPacketType.DATA_RESPONSE) {
                currentHand = EnumHand.values()[buffer.readInt()];
                if (buffer.readBoolean()) {
                    frequency = readIdentity(buffer);
                }
                status = buffer.readByte();

                int amount = buffer.readInt();
                for (int i = 0; i < amount; i++) {
                    publicCache.add(new Frequency(buffer));
                }
                amount = buffer.readInt();
                for (int i = 0; i < amount; i++) {
                    privateCache.add(new Frequency(buffer));
                }
                amount = buffer.readInt();
                for (int i = 0; i < amount; i++) trustedCache.add(new Frequency(buffer));
            } else if (packetType == PortableTeleporterPacketType.SET_FREQ) {
                currentHand = EnumHand.values()[buffer.readInt()];
                frequency = readIdentity(buffer);
            } else if (packetType == PortableTeleporterPacketType.DEL_FREQ) {
                currentHand = EnumHand.values()[buffer.readInt()];
                frequency = readIdentity(buffer);
            } else if (packetType == PortableTeleporterPacketType.TELEPORT) {
                currentHand = EnumHand.values()[buffer.readInt()];
                frequency = readIdentity(buffer);
            }
        }

        private static void writeIdentity(ByteBuf buffer, Frequency frequency) {
            PacketHandler.writeString(buffer, frequency.name);
            buffer.writeInt(frequency.getAccessMode().ordinal());
            buffer.writeBoolean(frequency.ownerUUID != null);
            if (frequency.ownerUUID != null) {
                buffer.writeLong(frequency.ownerUUID.getMostSignificantBits());
                buffer.writeLong(frequency.ownerUUID.getLeastSignificantBits());
            }
        }

        private static Frequency readIdentity(ByteBuf buffer) {
            String name = PacketHandler.readString(buffer);
            int index = buffer.readInt();
            AccessMode mode = index >= 0 && index < AccessMode.values().length
                  ? AccessMode.values()[index] : AccessMode.PRIVATE;
            UUID owner = buffer.readBoolean() ? new UUID(buffer.readLong(), buffer.readLong()) : null;
            return new Frequency(name, owner).setAccessMode(mode);
        }
    }
}
