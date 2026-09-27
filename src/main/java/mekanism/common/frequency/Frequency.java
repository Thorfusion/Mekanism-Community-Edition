package mekanism.common.frequency;

import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.TileNetworkList;
import mekanism.common.PacketHandler;
import mekanism.common.util.MekanismUtils;
import net.minecraft.nbt.NBTTagCompound;

public class Frequency {

    public static final String TELEPORTER = "Teleporter";

    public String name;
    public UUID ownerUUID;
    public String clientOwner;

    public boolean valid = true;

    public boolean publicFreq;

    /** Versioned access mode; publicFreq remains for old saves and addons. */
    private AccessMode accessMode = AccessMode.PRIVATE;

    public Set<Coord4D> activeCoords = new HashSet<>();

    public Frequency(String n, UUID uuid) {
        name = n;
        ownerUUID = uuid;
    }

    public Frequency(NBTTagCompound nbtTags) {
        read(nbtTags);
    }

    public Frequency(ByteBuf dataStream) {
        read(dataStream);
    }

    public boolean isPublic() {
        return accessMode == AccessMode.PUBLIC;
    }

    public Frequency setPublic(boolean isPublic) {
        setAccessMode(isPublic ? AccessMode.PUBLIC : AccessMode.PRIVATE);
        return this;
    }

    public Frequency setAccessMode(AccessMode mode) {
        accessMode = mode == null ? AccessMode.PRIVATE : mode;
        publicFreq = accessMode == AccessMode.PUBLIC;
        return this;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public boolean isTrusted() {
        return accessMode == AccessMode.TRUSTED;
    }

    public boolean isPrivate() {
        return accessMode == AccessMode.PRIVATE;
    }

    public Coord4D getClosestCoords(Coord4D coord) {
        Coord4D closest = null;
        for (Coord4D iterCoord : activeCoords) {
            if (iterCoord.equals(coord)) {
                continue;
            }
            if (closest == null) {
                closest = iterCoord;
                continue;
            }

            if (coord.dimensionId != closest.dimensionId && coord.dimensionId == iterCoord.dimensionId) {
                closest = iterCoord;
            } else if (coord.dimensionId != closest.dimensionId || coord.dimensionId == iterCoord.dimensionId) {
                if (coord.distanceTo(closest) > coord.distanceTo(iterCoord)) {
                    closest = iterCoord;
                }
            }
        }
        return closest;
    }

    public void write(NBTTagCompound nbtTags) {
        nbtTags.setString("name", name);
        nbtTags.setString("ownerUUID", ownerUUID.toString());
        nbtTags.setBoolean("publicFreq", publicFreq);
        nbtTags.setInteger("accessMode", accessMode.ordinal());
    }

    protected void read(NBTTagCompound nbtTags) {
        name = nbtTags.getString("name");
        ownerUUID = UUID.fromString(nbtTags.getString("ownerUUID"));
        if (nbtTags.hasKey("accessMode")) {
            int mode = nbtTags.getInteger("accessMode");
            setAccessMode(mode >= 0 && mode < AccessMode.values().length
                  ? AccessMode.values()[mode] : AccessMode.PRIVATE);
        } else {
            setPublic(nbtTags.getBoolean("publicFreq"));
        }
    }

    public void write(TileNetworkList data) {
        data.add(name);
        data.add(ownerUUID.toString());
        data.add(MekanismUtils.getLastKnownUsername(ownerUUID));
        data.add(publicFreq);
        data.add(accessMode.ordinal());
    }

    protected void read(ByteBuf dataStream) {
        name = PacketHandler.readString(dataStream);
        ownerUUID = UUID.fromString(PacketHandler.readString(dataStream));
        clientOwner = PacketHandler.readString(dataStream);
        dataStream.readBoolean(); // legacy projection retained on the wire
        int mode = dataStream.readInt();
        setAccessMode(mode >= 0 && mode < AccessMode.values().length
              ? AccessMode.values()[mode] : AccessMode.PRIVATE);
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + name.hashCode();
        code = 31 * code + ownerUUID.hashCode();
        code = 31 * code + accessMode.ordinal();
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Frequency && ((Frequency) obj).name.equals(name)
              && ((Frequency) obj).ownerUUID.equals(ownerUUID)
              && ((Frequency) obj).accessMode == accessMode;
    }

    public Identity getIdentity() {
        return new Identity(name, accessMode, ownerUUID);
    }

    public static class Identity {

        public String name;
        public boolean publicFreq;
        public AccessMode accessMode;
        public UUID ownerUUID;

        private Identity(String name, AccessMode accessMode, UUID ownerUUID) {
            this.name = name;
            this.accessMode = accessMode == null ? AccessMode.PRIVATE : accessMode;
            this.publicFreq = this.accessMode == AccessMode.PUBLIC;
            this.ownerUUID = ownerUUID;
        }

        @Nullable
        public static Identity load(NBTTagCompound data) {
            if (!data.getString("name").isEmpty()) {
                int mode = data.hasKey("accessMode") ? data.getInteger("accessMode")
                      : data.getBoolean("publicFreq") ? AccessMode.PUBLIC.ordinal() : AccessMode.PRIVATE.ordinal();
                AccessMode access = mode >= 0 && mode < AccessMode.values().length
                      ? AccessMode.values()[mode] : AccessMode.PRIVATE;
                UUID owner = null;
                if (data.hasKey("ownerUUID")) {
                    try {
                        owner = UUID.fromString(data.getString("ownerUUID"));
                    } catch (IllegalArgumentException ignored) {
                        return null;
                    }
                }
                return new Identity(data.getString("name"), access, owner);
            }
            return null;
        }

        public NBTTagCompound serialize() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("name", name);
            tag.setBoolean("publicFreq", publicFreq);
            tag.setInteger("accessMode", accessMode.ordinal());
            if (ownerUUID != null) tag.setString("ownerUUID", ownerUUID.toString());
            return tag;
        }
    }

    public enum AccessMode {
        PUBLIC,
        PRIVATE,
        TRUSTED
    }
}
