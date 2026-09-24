package mekanism.nuclear.common.content.sps;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import mekanism.api.TileNetworkList;

/** Compact read-only SPS snapshot synchronized only to players viewing a port. */
public final class SPSStatus {

    public static final SPSStatus EMPTY = new SPSStatus(false, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0);

    private final boolean formed;
    private final long polonium;
    private final long poloniumCapacity;
    private final long antimatter;
    private final long antimatterCapacity;
    private final long energyUsage;
    private final double processRate;
    private final double progress;
    private final int coils;
    private final long portEnergy;
    private final long portEnergyCapacity;

    private SPSStatus(boolean formed, long polonium, long poloniumCapacity,
          long antimatter, long antimatterCapacity, long energyUsage,
          double processRate, double progress, int coils, long portEnergy,
          long portEnergyCapacity) {
        this.formed = formed;
        this.polonium = polonium;
        this.poloniumCapacity = poloniumCapacity;
        this.antimatter = antimatter;
        this.antimatterCapacity = antimatterCapacity;
        this.energyUsage = energyUsage;
        this.processRate = processRate;
        this.progress = progress;
        this.coils = coils;
        this.portEnergy = portEnergy;
        this.portEnergyCapacity = portEnergyCapacity;
    }

    public static SPSStatus from(@Nullable SPSState state, long portEnergy, long portEnergyCapacity) {
        if (state == null || !state.isFormed()) {
            return new SPSStatus(false, 0, 0, 0, 0, 0, 0, 0, 0,
                  Math.max(0, portEnergy), Math.max(0, portEnergyCapacity));
        }
        return new SPSStatus(true, state.getPolonium(), state.getPoloniumCapacity(),
              state.getAntimatter(), state.getAntimatterCapacity(), state.getLastReceivedEnergy(),
              state.getProcessRate(), state.getScaledProgress(), state.getCoils().size(),
              Math.max(0, portEnergy), Math.max(0, portEnergyCapacity));
    }

    public void write(TileNetworkList data) {
        data.add(formed);
        data.add(polonium);
        data.add(poloniumCapacity);
        data.add(antimatter);
        data.add(antimatterCapacity);
        data.add(energyUsage);
        data.add(processRate);
        data.add(progress);
        data.add(coils);
        data.add(portEnergy);
        data.add(portEnergyCapacity);
    }

    public static SPSStatus read(ByteBuf data) {
        return new SPSStatus(data.readBoolean(), data.readLong(), data.readLong(),
              data.readLong(), data.readLong(), data.readLong(), data.readDouble(),
              data.readDouble(), data.readInt(), data.readLong(), data.readLong());
    }

    public boolean isFormed() {
        return formed;
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
}
