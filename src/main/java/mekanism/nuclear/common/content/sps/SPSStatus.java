package mekanism.nuclear.common.content.sps;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import mekanism.api.TileNetworkList;
import net.minecraft.util.math.BlockPos;

/** Compact read-only SPS snapshot used by the GUI and tracking-client renderer. */
public final class SPSStatus {

    public static final SPSStatus EMPTY = new SPSStatus(false, false, null, null,
          0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

    private final boolean formed;
    private final boolean controller;
    private final BlockPos min;
    private final BlockPos max;
    private final long polonium;
    private final long poloniumCapacity;
    private final long antimatter;
    private final long antimatterCapacity;
    private final long energyUsage;
    private final double processedThisTick;
    private final double processRate;
    private final double progress;
    private final int coils;
    private final long portEnergy;
    private final long portEnergyCapacity;

    private SPSStatus(boolean formed, boolean controller, @Nullable BlockPos min,
          @Nullable BlockPos max, long polonium, long poloniumCapacity,
          long antimatter, long antimatterCapacity, long energyUsage,
          double processedThisTick, double processRate, double progress, int coils, long portEnergy,
          long portEnergyCapacity) {
        this.formed = formed;
        this.controller = controller;
        this.min = min;
        this.max = max;
        this.polonium = polonium;
        this.poloniumCapacity = poloniumCapacity;
        this.antimatter = antimatter;
        this.antimatterCapacity = antimatterCapacity;
        this.energyUsage = energyUsage;
        this.processedThisTick = processedThisTick;
        this.processRate = processRate;
        this.progress = progress;
        this.coils = coils;
        this.portEnergy = portEnergy;
        this.portEnergyCapacity = portEnergyCapacity;
    }

    public static SPSStatus from(@Nullable SPSState state, boolean controller,
          long portEnergy, long portEnergyCapacity) {
        if (state == null || !state.isFormed()) {
            return new SPSStatus(false, controller, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                  Math.max(0, portEnergy), Math.max(0, portEnergyCapacity));
        }
        return new SPSStatus(true, controller, state.getMin(), state.getMax(),
              state.getPolonium(), state.getPoloniumCapacity(),
              state.getAntimatter(), state.getAntimatterCapacity(), state.getLastReceivedEnergy(),
              state.getLastProcessed(), state.getProcessRate(), state.getScaledProgress(), state.getCoils().size(),
              Math.max(0, portEnergy), Math.max(0, portEnergyCapacity));
    }

    public void write(TileNetworkList data) {
        data.add(formed);
        data.add(controller);
        writePos(data, min);
        writePos(data, max);
        data.add(polonium);
        data.add(poloniumCapacity);
        data.add(antimatter);
        data.add(antimatterCapacity);
        data.add(energyUsage);
        data.add(processedThisTick);
        data.add(processRate);
        data.add(progress);
        data.add(coils);
        data.add(portEnergy);
        data.add(portEnergyCapacity);
    }

    public static SPSStatus read(ByteBuf data) {
        boolean formed = data.readBoolean();
        boolean controller = data.readBoolean();
        BlockPos min = readPos(data);
        BlockPos max = readPos(data);
        return new SPSStatus(formed, controller, formed ? min : null, formed ? max : null,
              data.readLong(), data.readLong(), data.readLong(), data.readLong(), data.readLong(),
              data.readDouble(), data.readDouble(), data.readDouble(), data.readInt(),
              data.readLong(), data.readLong());
    }

    public boolean isFormed() {
        return formed;
    }

    public boolean isController() {
        return controller;
    }

    @Nullable
    public BlockPos getMin() {
        return min;
    }

    @Nullable
    public BlockPos getMax() {
        return max;
    }

    public long getPolonium() {
        return polonium;
    }

    public long getPoloniumCapacity() {
        return poloniumCapacity;
    }

    public long getAntimatter() {
        return antimatter;
    }

    public long getAntimatterCapacity() {
        return antimatterCapacity;
    }

    public long getEnergyUsage() {
        return energyUsage;
    }

    public double getProcessedThisTick() {
        return processedThisTick;
    }

    public double getProcessRate() {
        return processRate;
    }

    public double getProgress() {
        return progress;
    }

    public int getCoils() {
        return coils;
    }

    public long getPortEnergy() {
        return portEnergy;
    }

    public long getPortEnergyCapacity() {
        return portEnergyCapacity;
    }

    private static void writePos(TileNetworkList data, @Nullable BlockPos pos) {
        data.add(pos == null ? 0 : pos.getX());
        data.add(pos == null ? 0 : pos.getY());
        data.add(pos == null ? 0 : pos.getZ());
    }

    private static BlockPos readPos(ByteBuf data) {
        return new BlockPos(data.readInt(), data.readInt(), data.readInt());
    }
}
