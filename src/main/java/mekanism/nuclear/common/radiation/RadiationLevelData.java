package mekanism.nuclear.common.radiation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

/** Per-dimension persisted and chunk-indexed environmental radiation data. */
public class RadiationLevelData extends WorldSavedData {

    public static final String DATA_NAME = "mekanismnuclear_radiation";
    private static final String NBT_SOURCES = "Sources";

    private final Map<Long, RadiationSource> sources = new HashMap<>();
    private final Map<Long, Set<Long>> sourcesByChunk = new HashMap<>();

    public RadiationLevelData() {
        this(DATA_NAME);
    }

    public RadiationLevelData(String name) {
        super(name);
    }

    public void radiate(BlockPos position, double magnitude, long gameTime, double decayRate,
          double minimumMagnitude) {
        if (position == null || magnitude <= 0 || !Double.isFinite(magnitude)) {
            return;
        }
        long positionKey = position.toLong();
        RadiationSource source = sources.get(positionKey);
        if (source == null || source.decayTo(gameTime, decayRate, minimumMagnitude)) {
            if (source != null) {
                remove(positionKey, source);
            }
            add(new RadiationSource(position, magnitude, gameTime));
        } else {
            source.add(magnitude);
        }
        markDirty();
    }

    public double getRadiationLevel(BlockPos position, long gameTime, int chunkRadius,
          double decayRate, double minimumMagnitude, double baseline) {
        double level = baseline;
        List<Long> expired = new ArrayList<>();
        boolean decayed = false;
        int centerChunkX = position.getX() >> 4;
        int centerChunkZ = position.getZ() >> 4;
        for (int chunkX = centerChunkX - chunkRadius; chunkX <= centerChunkX + chunkRadius; chunkX++) {
            for (int chunkZ = centerChunkZ - chunkRadius; chunkZ <= centerChunkZ + chunkRadius; chunkZ++) {
                Set<Long> indexedSources = sourcesByChunk.get(chunkKey(chunkX, chunkZ));
                if (indexedSources == null) {
                    continue;
                }
                for (Long positionKey : indexedSources) {
                    RadiationSource source = sources.get(positionKey);
                    long previousDecayTick = source == null ? 0 : source.getLastDecayTick();
                    if (source == null || source.decayTo(gameTime, decayRate, minimumMagnitude)) {
                        expired.add(positionKey);
                    } else {
                        decayed |= source.getLastDecayTick() != previousDecayTick;
                        level += source.getMagnitude() / Math.max(1, position.distanceSq(source.getPosition()));
                    }
                }
            }
        }
        if (!expired.isEmpty()) {
            for (Long positionKey : expired) {
                RadiationSource source = sources.get(positionKey);
                if (source != null) {
                    remove(positionKey, source);
                }
            }
            markDirty();
        } else if (decayed) {
            markDirty();
        }
        return level;
    }

    public double getSourceMagnitude(BlockPos position, long gameTime, double decayRate,
          double minimumMagnitude) {
        long positionKey = position.toLong();
        RadiationSource source = sources.get(positionKey);
        if (source == null) {
            return 0;
        }
        long previousDecayTick = source.getLastDecayTick();
        if (source.decayTo(gameTime, decayRate, minimumMagnitude)) {
            remove(positionKey, source);
            markDirty();
            return 0;
        }
        if (source.getLastDecayTick() != previousDecayTick) {
            markDirty();
        }
        return source.getMagnitude();
    }

    public int size() {
        return sources.size();
    }

    private void add(RadiationSource source) {
        long positionKey = source.getPosition().toLong();
        sources.put(positionKey, source);
        long chunkKey = chunkKey(source.getPosition().getX() >> 4, source.getPosition().getZ() >> 4);
        sourcesByChunk.computeIfAbsent(chunkKey, ignored -> new HashSet<>()).add(positionKey);
    }

    private void remove(long positionKey, RadiationSource source) {
        sources.remove(positionKey);
        long chunkKey = chunkKey(source.getPosition().getX() >> 4, source.getPosition().getZ() >> 4);
        Set<Long> indexedSources = sourcesByChunk.get(chunkKey);
        if (indexedSources != null) {
            indexedSources.remove(positionKey);
            if (indexedSources.isEmpty()) {
                sourcesByChunk.remove(chunkKey);
            }
        }
    }

    private static long chunkKey(int chunkX, int chunkZ) {
        return (long) chunkX << 32 | chunkZ & 0xFFFFFFFFL;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        sources.clear();
        sourcesByChunk.clear();
        NBTTagList list = data.getTagList(NBT_SOURCES, NBT.TAG_COMPOUND);
        for (int index = 0; index < list.tagCount(); index++) {
            RadiationSource source = RadiationSource.deserializeNBT(list.getCompoundTagAt(index));
            if (source != null && source.getMagnitude() > 0 && Double.isFinite(source.getMagnitude())) {
                add(source);
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        NBTTagList list = new NBTTagList();
        for (RadiationSource source : sources.values()) {
            list.appendTag(source.serializeNBT());
        }
        data.setTag(NBT_SOURCES, list);
        return data;
    }
}
