package mekanism.nuclear.common.content.fission;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import mekanism.api.TileNetworkList;

/** Compact read-only snapshot synchronized only to clients viewing a reactor port. */
public final class FissionReactorStatus {

    public static final FissionReactorStatus EMPTY = new FissionReactorStatus(false, false, false,
          0, 0, 0, 0, 0, 0, 0, 0, FissionReactorState.CoolantType.NONE,
          FissionReactorState.HeatedCoolantType.NONE, 0, 0, 0, 0, 0, 0, 0,
          0, 0, 0, 0, 0, 0);

    private final boolean formed;
    private final boolean active;
    private final boolean forceDisabled;
    private final long fuel;
    private final long fuelCapacity;
    private final long coolant;
    private final long coolantCapacity;
    private final long heatedCoolant;
    private final long heatedCoolantCapacity;
    private final long waste;
    private final long wasteCapacity;
    private final FissionReactorState.CoolantType coolantType;
    private final FissionReactorState.HeatedCoolantType heatedCoolantType;
    private final double temperature;
    private final double damage;
    private final double burnRate;
    private final double lastBurnRate;
    private final double maxBurnRate;
    private final long lastBoilRate;
    private final double boilEfficiency;
    private final double environmentLoss;
    private final int width;
    private final int height;
    private final int length;
    private final int fuelAssemblies;
    private final int controlRods;

    private FissionReactorStatus(boolean formed, boolean active, boolean forceDisabled,
          long fuel, long fuelCapacity, long coolant, long coolantCapacity,
          long heatedCoolant, long heatedCoolantCapacity, long waste, long wasteCapacity,
          FissionReactorState.CoolantType coolantType,
          FissionReactorState.HeatedCoolantType heatedCoolantType,
          double temperature, double damage, double burnRate, double lastBurnRate,
          double maxBurnRate, long lastBoilRate, double boilEfficiency, double environmentLoss,
          int width, int height, int length, int fuelAssemblies, int controlRods) {
        this.formed = formed;
        this.active = active;
        this.forceDisabled = forceDisabled;
        this.fuel = fuel;
        this.fuelCapacity = fuelCapacity;
        this.coolant = coolant;
        this.coolantCapacity = coolantCapacity;
        this.heatedCoolant = heatedCoolant;
        this.heatedCoolantCapacity = heatedCoolantCapacity;
        this.waste = waste;
        this.wasteCapacity = wasteCapacity;
        this.coolantType = coolantType;
        this.heatedCoolantType = heatedCoolantType;
        this.temperature = temperature;
        this.damage = damage;
        this.burnRate = burnRate;
        this.lastBurnRate = lastBurnRate;
        this.maxBurnRate = maxBurnRate;
        this.lastBoilRate = lastBoilRate;
        this.boilEfficiency = boilEfficiency;
        this.environmentLoss = environmentLoss;
        this.width = width;
        this.height = height;
        this.length = length;
        this.fuelAssemblies = fuelAssemblies;
        this.controlRods = controlRods;
    }

    public static FissionReactorStatus from(@Nullable FissionReactorState state) {
        if (state == null || !state.isFormed()) {
            return EMPTY;
        }
        return new FissionReactorStatus(true, state.isActive(), state.isForceDisabled(),
              state.getFissileFuel(), state.getFuelCapacity(), state.getCoolant(), state.getCoolantCapacity(),
              state.getHeatedCoolant(), state.getHeatedCoolantCapacity(), state.getNuclearWaste(),
              state.getWasteCapacity(), state.getCoolantType(), state.getHeatedCoolantType(),
              state.getTemperature(), state.getDamage(), state.getBurnRate(), state.getLastBurnRate(),
              state.getMaxBurnRate(), state.getLastBoilRate(), state.getBoilEfficiency(),
              state.getLastEnvironmentLoss(), state.getWidth(), state.getHeight(), state.getLength(),
              state.getFuelAssemblies(), state.getControlRods());
    }

    public void write(TileNetworkList data) {
        data.add(formed);
        data.add(active);
        data.add(forceDisabled);
        data.add(fuel);
        data.add(fuelCapacity);
        data.add(coolant);
        data.add(coolantCapacity);
        data.add(heatedCoolant);
        data.add(heatedCoolantCapacity);
        data.add(waste);
        data.add(wasteCapacity);
        data.add(coolantType.ordinal());
        data.add(heatedCoolantType.ordinal());
        data.add(temperature);
        data.add(damage);
        data.add(burnRate);
        data.add(lastBurnRate);
        data.add(maxBurnRate);
        data.add(lastBoilRate);
        data.add(boilEfficiency);
        data.add(environmentLoss);
        data.add(width);
        data.add(height);
        data.add(length);
        data.add(fuelAssemblies);
        data.add(controlRods);
    }

    public static FissionReactorStatus read(ByteBuf data) {
        return new FissionReactorStatus(data.readBoolean(), data.readBoolean(), data.readBoolean(),
              data.readLong(), data.readLong(), data.readLong(), data.readLong(),
              data.readLong(), data.readLong(), data.readLong(), data.readLong(),
              FissionReactorState.CoolantType.values()[boundedIndex(data.readInt(), FissionReactorState.CoolantType.values().length)],
              FissionReactorState.HeatedCoolantType.values()[boundedIndex(data.readInt(), FissionReactorState.HeatedCoolantType.values().length)],
              data.readDouble(), data.readDouble(), data.readDouble(), data.readDouble(), data.readDouble(),
              data.readLong(), data.readDouble(), data.readDouble(), data.readInt(), data.readInt(), data.readInt(),
              data.readInt(), data.readInt());
    }

    private static int boundedIndex(int index, int length) {
        return index >= 0 && index < length ? index : 0;
    }

    public boolean isFormed() {
        return formed;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isForceDisabled() {
        return forceDisabled;
    }

    public long getFuel() {
        return fuel;
    }

    public long getFuelCapacity() {
        return fuelCapacity;
    }

    public long getCoolant() {
        return coolant;
    }

    public long getCoolantCapacity() {
        return coolantCapacity;
    }

    public long getHeatedCoolant() {
        return heatedCoolant;
    }

    public long getHeatedCoolantCapacity() {
        return heatedCoolantCapacity;
    }

    public long getWaste() {
        return waste;
    }

    public long getWasteCapacity() {
        return wasteCapacity;
    }

    public FissionReactorState.CoolantType getCoolantType() {
        return coolantType;
    }

    public FissionReactorState.HeatedCoolantType getHeatedCoolantType() {
        return heatedCoolantType;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getDamage() {
        return damage;
    }

    public double getBurnRate() {
        return burnRate;
    }

    public double getLastBurnRate() {
        return lastBurnRate;
    }

    public double getMaxBurnRate() {
        return maxBurnRate;
    }

    public long getLastBoilRate() {
        return lastBoilRate;
    }

    public double getBoilEfficiency() {
        return boilEfficiency;
    }

    public double getEnvironmentLoss() {
        return environmentLoss;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    public int getFuelAssemblies() {
        return fuelAssemblies;
    }

    public int getControlRods() {
        return controlRods;
    }
}
