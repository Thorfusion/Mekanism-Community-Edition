package mekanism.nuclear.common.content.sps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mekanism.nuclear.common.config.NuclearSPSConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/** Persisted structure identity for a formed Supercritical Phase Shifter. */
public final class SPSState {

    private static final int DATA_VERSION = 2;

    private boolean formed;
    private BlockPos min;
    private BlockPos max;
    private List<BlockPos> ports = Collections.emptyList();
    private List<BlockPos> coils = Collections.emptyList();

    private long polonium;
    private long antimatter;
    private long inputProcessed;
    private double progress;
    private long pendingEnergy;
    private long lastReceivedEnergy;
    private double lastProcessed;

    public void applyStructure(SPSValidator.Result result) {
        if (!result.isFormed()) {
            throw new IllegalArgumentException("Cannot apply an invalid SPS structure");
        }
        formed = true;
        min = result.getMin();
        max = result.getMax();
        ports = immutableCopy(result.getPorts());
        coils = immutableCopy(result.getCoils());
        clampOperationalState();
    }

    /** Copies authoritative processing data after a newly formed structure selects its controller. */
    public void copyOperationalFrom(SPSState other) {
        if (other == null || other == this) {
            return;
        }
        polonium = other.polonium;
        antimatter = other.antimatter;
        inputProcessed = other.inputProcessed;
        progress = other.progress;
        pendingEnergy = other.pendingEnergy;
        lastReceivedEnergy = other.lastReceivedEnergy;
        lastProcessed = other.lastProcessed;
        clampOperationalState();
    }

    /** Removes duplicated contents from non-controller port state. */
    public void clearOperationalState() {
        polonium = antimatter = inputProcessed = pendingEnergy = lastReceivedEnergy = 0;
        progress = lastProcessed = 0;
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

    public boolean hasCoilForPort(BlockPos port) {
        if (!formed || port == null) {
            return false;
        }
        EnumFacing outward = outwardSide(port);
        return outward != null && coils.contains(port.offset(outward.getOpposite()));
    }

    public long getPolonium() {
        return polonium;
    }

    public long getPoloniumCapacity() {
        return NuclearSPSConfig.getInputTankCapacity();
    }

    public long insertPolonium(long amount, boolean execute) {
        long accepted = accepted(amount, polonium, getPoloniumCapacity());
        if (execute) {
            polonium += accepted;
        }
        return accepted;
    }

    public long extractPolonium(long amount, boolean execute) {
        long extracted = Math.min(nonNegative(amount), polonium);
        if (execute) {
            polonium -= extracted;
        }
        return extracted;
    }

    public long getAntimatter() {
        return antimatter;
    }

    public long getAntimatterCapacity() {
        return NuclearSPSConfig.getOutputTankCapacity();
    }

    public long insertAntimatter(long amount, boolean execute) {
        long accepted = accepted(amount, antimatter, getAntimatterCapacity());
        if (execute) {
            antimatter += accepted;
        }
        return accepted;
    }

    public long extractAntimatter(long amount, boolean execute) {
        long extracted = Math.min(nonNegative(amount), antimatter);
        if (execute) {
            antimatter -= extracted;
        }
        return extracted;
    }

    public long getInputProcessed() {
        return inputProcessed;
    }

    public double getProgress() {
        return progress;
    }

    public double getScaledProgress() {
        return (inputProcessed + progress) / NuclearSPSConfig.getInputPerAntimatter();
    }

    public long getPendingEnergy() {
        return pendingEnergy;
    }

    public long getLastReceivedEnergy() {
        return lastReceivedEnergy;
    }

    public double getLastProcessed() {
        return lastProcessed;
    }

    public double getProcessRate() {
        return Math.round((lastProcessed / NuclearSPSConfig.getInputPerAntimatter()) * 1_000D) / 1_000D;
    }

    public boolean canOperate() {
        return formed && polonium > 0 && antimatter < getAntimatterCapacity();
    }

    /** Adds energy delivered by a connected coil without allowing long overflow. */
    public long addPendingEnergy(long amount) {
        long requested = nonNegative(amount);
        long accepted = Math.min(requested, Long.MAX_VALUE - pendingEnergy);
        pendingEnergy += accepted;
        return accepted;
    }

    /** Executes one stable SPS processing tick and clears the received-energy accumulator. */
    public TickResult tick() {
        long received = pendingEnergy;
        pendingEnergy = 0;
        double processed = 0;
        double oldProgress = progress;
        long oldInputProcessed = inputProcessed;
        long oldPolonium = polonium;
        long oldAntimatter = antimatter;

        if (canOperate() && received > 0) {
            long inputLimit = getProcessInputLimit();
            double processable = received / (double) NuclearSPSConfig.getEnergyPerInput();
            if (processable + progress >= inputLimit) {
                processed = process(inputLimit);
                progress = 0;
            } else {
                processed = processable;
                progress += processable;
                long requested = clampToLong(progress);
                long actual = process(requested);
                if (actual < requested) {
                    long difference = requested - actual;
                    progress -= difference;
                    processed -= difference;
                }
                progress %= 1D;
            }
        }

        boolean changed = received != lastReceivedEnergy || Double.compare(processed, lastProcessed) != 0
              || Double.compare(oldProgress, progress) != 0 || oldInputProcessed != inputProcessed
              || oldPolonium != polonium || oldAntimatter != antimatter;
        lastReceivedEnergy = received;
        lastProcessed = finiteNonNegative(processed);
        return new TickResult(changed, received, lastProcessed);
    }

    private long getProcessInputLimit() {
        long outputNeeded = Math.max(0, getAntimatterCapacity() - antimatter);
        if (outputNeeded == 0 || polonium == 0) {
            return 0;
        }
        long inputPerAntimatter = NuclearSPSConfig.getInputPerAntimatter();
        long firstOutput = inputPerAntimatter - inputProcessed;
        long laterOutputs = saturatedMultiply(outputNeeded - 1, inputPerAntimatter);
        return Math.min(polonium, saturatedAdd(firstOutput, laterOutputs));
    }

    private long process(long amount) {
        long processed = Math.min(nonNegative(amount), polonium);
        if (processed == 0) {
            return 0;
        }
        polonium -= processed;
        long total = saturatedAdd(inputProcessed, processed);
        long inputPerAntimatter = NuclearSPSConfig.getInputPerAntimatter();
        long produced = total / inputPerAntimatter;
        long acceptedOutput = Math.min(produced, Math.max(0, getAntimatterCapacity() - antimatter));
        antimatter += acceptedOutput;
        inputProcessed = total % inputPerAntimatter;
        return processed;
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
        tag.setLong("Polonium", polonium);
        tag.setLong("Antimatter", antimatter);
        tag.setLong("InputProcessed", inputProcessed);
        tag.setDouble("Progress", progress);
        tag.setLong("PendingEnergy", pendingEnergy);
        tag.setLong("LastReceivedEnergy", lastReceivedEnergy);
        tag.setDouble("LastProcessed", lastProcessed);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        min = tag.hasKey("Min", 10) ? readPos(tag.getCompoundTag("Min")) : null;
        max = tag.hasKey("Max", 10) ? readPos(tag.getCompoundTag("Max")) : null;
        formed = tag.getBoolean("Formed") && hasBounds();
        ports = readPositions(tag.getTagList("Ports", 10));
        coils = readPositions(tag.getTagList("Coils", 10));
        polonium = nonNegative(tag.getLong("Polonium"));
        antimatter = nonNegative(tag.getLong("Antimatter"));
        inputProcessed = nonNegative(tag.getLong("InputProcessed"));
        progress = finiteFraction(tag.getDouble("Progress"));
        pendingEnergy = nonNegative(tag.getLong("PendingEnergy"));
        lastReceivedEnergy = nonNegative(tag.getLong("LastReceivedEnergy"));
        lastProcessed = finiteNonNegative(tag.getDouble("LastProcessed"));
        clampOperationalState();
    }

    private void clampOperationalState() {
        polonium = Math.min(nonNegative(polonium), getPoloniumCapacity());
        antimatter = Math.min(nonNegative(antimatter), getAntimatterCapacity());
        inputProcessed = nonNegative(inputProcessed) % NuclearSPSConfig.getInputPerAntimatter();
        progress = finiteFraction(progress);
        pendingEnergy = nonNegative(pendingEnergy);
        lastReceivedEnergy = nonNegative(lastReceivedEnergy);
        lastProcessed = finiteNonNegative(lastProcessed);
    }

    @Nullable
    private EnumFacing outwardSide(BlockPos position) {
        if (!hasBounds()) {
            return null;
        }
        if (position.getX() == min.getX()) {
            return EnumFacing.WEST;
        } else if (position.getX() == max.getX()) {
            return EnumFacing.EAST;
        } else if (position.getY() == min.getY()) {
            return EnumFacing.DOWN;
        } else if (position.getY() == max.getY()) {
            return EnumFacing.UP;
        } else if (position.getZ() == min.getZ()) {
            return EnumFacing.NORTH;
        } else if (position.getZ() == max.getZ()) {
            return EnumFacing.SOUTH;
        }
        return null;
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

    private static long accepted(long requested, long stored, long capacity) {
        return Math.min(nonNegative(requested), Math.max(0, capacity - stored));
    }

    private static long nonNegative(long value) {
        return Math.max(0, value);
    }

    private static double finiteNonNegative(double value) {
        return Double.isFinite(value) ? Math.max(0, value) : 0;
    }

    private static double finiteFraction(double value) {
        return Double.isFinite(value) ? Math.max(0, Math.min(Math.nextDown(1D), value % 1D)) : 0;
    }

    private static long clampToLong(double value) {
        return !Double.isFinite(value) || value >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) Math.max(0, value);
    }

    private static long saturatedAdd(long first, long second) {
        return second > 0 && first > Long.MAX_VALUE - second ? Long.MAX_VALUE : first + second;
    }

    private static long saturatedMultiply(long first, long second) {
        if (first <= 0 || second <= 0) {
            return 0;
        }
        return first > Long.MAX_VALUE / second ? Long.MAX_VALUE : first * second;
    }

    public static final class TickResult {

        private final boolean changed;
        private final long energyUsed;
        private final double poloniumProcessed;

        private TickResult(boolean changed, long energyUsed, double poloniumProcessed) {
            this.changed = changed;
            this.energyUsed = energyUsed;
            this.poloniumProcessed = poloniumProcessed;
        }

        public boolean isChanged() {
            return changed;
        }

        public long getEnergyUsed() {
            return energyUsed;
        }

        public double getPoloniumProcessed() {
            return poloniumProcessed;
        }
    }
}
