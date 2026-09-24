package mekanism.nuclear.common.content.sps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

/** Persisted structure identity for a formed Supercritical Phase Shifter. */
public final class SPSState {

    private static final int DATA_VERSION = 1;

    private boolean formed;
    private BlockPos min;
    private BlockPos max;
    private List<BlockPos> ports = Collections.emptyList();
    private List<BlockPos> coils = Collections.emptyList();

    public void applyStructure(SPSValidator.Result result) {
        if (!result.isFormed()) {
            throw new IllegalArgumentException("Cannot apply an invalid SPS structure");
        }
        formed = true;
        min = result.getMin();
        max = result.getMax();
        ports = immutableCopy(result.getPorts());
        coils = immutableCopy(result.getCoils());
    }

    public void setUnformed() {
        formed = false;
    }

    public boolean isFormed() {
        return formed;
    }

    public boolean hasBounds() {
        return min != null && max != null;
    }

    public boolean watches(BlockPos changedPos) {
        return hasBounds() && changedPos.getX() >= min.getX() - 1 && changedPos.getX() <= max.getX() + 1
              && changedPos.getY() >= min.getY() - 1 && changedPos.getY() <= max.getY() + 1
              && changedPos.getZ() >= min.getZ() - 1 && changedPos.getZ() <= max.getZ() + 1;
    }

    public boolean watchesChunk(int chunkX, int chunkZ) {
        return hasBounds() && chunkX >= (min.getX() >> 4) && chunkX <= (max.getX() >> 4)
              && chunkZ >= (min.getZ() >> 4) && chunkZ <= (max.getZ() >> 4);
    }

    @Nullable
    public BlockPos getMin() {
        return min;
    }

    @Nullable
    public BlockPos getMax() {
        return max;
    }

    public List<BlockPos> getPorts() {
        return ports;
    }

    public List<BlockPos> getCoils() {
        return coils;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("Version", DATA_VERSION);
        tag.setBoolean("Formed", formed);
        if (hasBounds()) {
            tag.setTag("Min", writePos(min));
            tag.setTag("Max", writePos(max));
        }
        tag.setTag("Ports", writePositions(ports));
        tag.setTag("Coils", writePositions(coils));
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        min = tag.hasKey("Min", 10) ? readPos(tag.getCompoundTag("Min")) : null;
        max = tag.hasKey("Max", 10) ? readPos(tag.getCompoundTag("Max")) : null;
        formed = tag.getBoolean("Formed") && hasBounds();
        ports = readPositions(tag.getTagList("Ports", 10));
        coils = readPositions(tag.getTagList("Coils", 10));
    }

    private static List<BlockPos> immutableCopy(List<BlockPos> positions) {
        return Collections.unmodifiableList(new ArrayList<>(positions));
    }

    private static NBTTagList writePositions(List<BlockPos> positions) {
        NBTTagList list = new NBTTagList();
        for (BlockPos position : positions) {
            list.appendTag(writePos(position));
        }
        return list;
    }

    private static List<BlockPos> readPositions(NBTTagList tags) {
        List<BlockPos> positions = new ArrayList<>(tags.tagCount());
        for (int i = 0; i < tags.tagCount(); i++) {
            positions.add(readPos(tags.getCompoundTagAt(i)));
        }
        return immutableCopy(positions);
    }

    private static NBTTagCompound writePos(BlockPos pos) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("X", pos.getX());
        tag.setInteger("Y", pos.getY());
        tag.setInteger("Z", pos.getZ());
        return tag;
    }

    private static BlockPos readPos(NBTTagCompound tag) {
        return new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
    }
}
