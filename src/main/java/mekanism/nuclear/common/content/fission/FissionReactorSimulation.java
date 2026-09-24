package mekanism.nuclear.common.content.fission;

import java.util.Objects;
import java.util.function.DoubleSupplier;
import mekanism.nuclear.common.config.NuclearFissionConfig;

/** Pure, deterministic server simulation for one formed Fission Reactor. */
public final class FissionReactorSimulation {

    private static final double WATER_CONDUCTIVITY = 0.5D;
    private static final double WATER_THERMAL_ENTHALPY = 100D;
    private static final double STEAM_ENERGY_EFFICIENCY = 0.2D;
    private static final double SODIUM_CONDUCTIVITY = 1D;
    private static final double SODIUM_THERMAL_ENTHALPY = 5D;
    private static final double ENVIRONMENT_INVERSE_COEFFICIENT = 20_010D;

    private FissionReactorSimulation() {
    }

    public static TickResult tick(FissionReactorState state, DoubleSupplier random) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(random, "random");
        if (!state.isFormed()) {
            return TickResult.IDLE;
        }

        double burned = burnFuel(state);
        long heated = heatCoolant(state);
        double heatBeforeEnvironment = state.getHeat();
        double environmentLoss = dissipateHeat(state);
        boolean environmentChanged = heatBeforeEnvironment != state.getHeat();
        boolean meltdown = updateDamage(state, random);
        long radioactiveRelease = state.consumePendingRadiation();
        if (meltdown) {
            radioactiveRelease = saturatedAdd(radioactiveRelease, state.applyMeltdown());
        }
        return new TickResult(burned, heated, environmentLoss, radioactiveRelease, meltdown,
              burned > 0 || heated > 0 || environmentChanged || radioactiveRelease > 0 || meltdown);
    }

    private static double burnFuel(FissionReactorState state) {
        if (!state.isActive()) {
            state.setLastBurnRate(0);
            return 0;
        }
        double storedFuel = state.getFissileFuel() + state.getBurnRemaining();
        double toBurn = Math.min(Math.min(state.getBurnRate(), storedFuel), state.getMaxBurnRate());
        storedFuel -= toBurn;
        state.setFissileFuel((long) storedFuel);
        state.setBurnRemaining(storedFuel % 1D);
        state.addHeat(toBurn * NuclearFissionConfig.getEnergyPerFissionFuel());

        double partialWaste = state.getPartialWaste() + toBurn;
        long producedWaste = floorToLong(partialWaste);
        state.setPartialWaste(partialWaste % 1D);
        if (producedWaste > 0) {
            long accepted = state.insertNuclearWaste(producedWaste, true);
            state.addPendingRadiation(producedWaste - accepted);
        }
        state.setLastBurnRate(toBurn);
        return toBurn;
    }

    private static long heatCoolant(FissionReactorState state) {
        double availableHeat = state.getBoilEfficiency()
              * (state.getHeat() - NuclearFissionConfig.BOIL_TEMPERATURE * state.getHeatCapacity());
        if (availableHeat <= 0 || state.getCoolant() <= 0) {
            state.setLastBoilRate(0);
            return 0;
        }

        double conductivity;
        double enthalpy;
        double efficiency = 1D;
        FissionReactorState.HeatedCoolantType outputType;
        if (state.getCoolantType() == FissionReactorState.CoolantType.WATER) {
            conductivity = WATER_CONDUCTIVITY;
            enthalpy = WATER_THERMAL_ENTHALPY;
            efficiency = STEAM_ENERGY_EFFICIENCY;
            outputType = FissionReactorState.HeatedCoolantType.STEAM;
        } else if (state.getCoolantType() == FissionReactorState.CoolantType.SODIUM) {
            conductivity = SODIUM_CONDUCTIVITY;
            enthalpy = SODIUM_THERMAL_ENTHALPY;
            outputType = FissionReactorState.HeatedCoolantType.SUPERHEATED_SODIUM;
        } else {
            state.setLastBoilRate(0);
            return 0;
        }

        long toHeat = clampToLong(efficiency * availableHeat * conductivity / enthalpy);
        toHeat = Math.min(toHeat, state.getCoolant());
        if (toHeat <= 0) {
            state.setLastBoilRate(0);
            return 0;
        }
        state.extractCoolant(toHeat, true);
        state.insertHeatedCoolant(outputType, toHeat, true);
        state.addHeat(-(toHeat * enthalpy / efficiency));
        state.setLastBoilRate(toHeat);
        return toHeat;
    }

    private static double dissipateHeat(FissionReactorState state) {
        double temperatureDelta = state.getTemperature() - NuclearFissionConfig.AMBIENT_TEMPERATURE;
        double transferredTemperature = temperatureDelta / ENVIRONMENT_INVERSE_COEFFICIENT;
        state.addHeat(-transferredTemperature * state.getHeatCapacity());
        double loss = Math.max(transferredTemperature, 0);
        state.setLastEnvironmentLoss(loss);
        return loss;
    }

    private static boolean updateDamage(FissionReactorState state, DoubleSupplier random) {
        double temperature = state.getTemperature();
        if (temperature > NuclearFissionConfig.MIN_DAMAGE_TEMPERATURE) {
            double rate = Math.min(temperature, NuclearFissionConfig.MAX_DAMAGE_TEMPERATURE)
                  / (NuclearFissionConfig.MIN_DAMAGE_TEMPERATURE * 10D);
            state.setDamage(state.getDamage() + rate);
        } else {
            double repair = (NuclearFissionConfig.MIN_DAMAGE_TEMPERATURE - temperature)
                  / (NuclearFissionConfig.MIN_DAMAGE_TEMPERATURE * 100D);
            state.setDamage(Math.max(0, state.getDamage() - repair));
        }

        if (state.getDamage() >= NuclearFissionConfig.MAX_DAMAGE
              && temperature >= NuclearFissionConfig.MIN_DAMAGE_TEMPERATURE) {
            double chance = Math.min(1D, state.getDamage() / NuclearFissionConfig.MAX_DAMAGE
                  * NuclearFissionConfig.getMeltdownChance());
            if (random.getAsDouble() < chance) {
                if (NuclearFissionConfig.isMeltdownsEnabled()) {
                    return true;
                }
                state.setForceDisabled(true);
            }
        } else if (state.getDamage() < NuclearFissionConfig.MAX_DAMAGE
              && temperature < NuclearFissionConfig.MIN_DAMAGE_TEMPERATURE) {
            state.setForceDisabled(false);
        }
        return false;
    }

    private static long clampToLong(double value) {
        if (!Double.isFinite(value) || value >= Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return value <= 0 ? 0 : (long) value;
    }

    private static long floorToLong(double value) {
        return clampToLong(Math.floor(value));
    }

    private static long saturatedAdd(long first, long second) {
        if (second > 0 && first > Long.MAX_VALUE - second) {
            return Long.MAX_VALUE;
        }
        return first + second;
    }

    public static final class TickResult {

        private static final TickResult IDLE = new TickResult(0, 0, 0, 0, false, false);

        private final double burned;
        private final long heatedCoolant;
        private final double environmentLoss;
        private final long radioactiveWasteReleased;
        private final boolean meltdown;
        private final boolean changed;

        private TickResult(double burned, long heatedCoolant, double environmentLoss,
              long radioactiveWasteReleased, boolean meltdown, boolean changed) {
            this.burned = burned;
            this.heatedCoolant = heatedCoolant;
            this.environmentLoss = environmentLoss;
            this.radioactiveWasteReleased = radioactiveWasteReleased;
            this.meltdown = meltdown;
            this.changed = changed;
        }

        public double getBurned() {
            return burned;
        }

        public long getHeatedCoolant() {
            return heatedCoolant;
        }

        public double getEnvironmentLoss() {
            return environmentLoss;
        }

        public long getRadioactiveWasteReleased() {
            return radioactiveWasteReleased;
        }

        public boolean isMeltdown() {
            return meltdown;
        }

        public boolean isChanged() {
            return changed;
        }
    }
}
