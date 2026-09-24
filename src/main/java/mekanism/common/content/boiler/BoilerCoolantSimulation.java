package mekanism.common.content.boiler;

import mekanism.api.gas.GasCoolantRegistry.Coolant;
import mekanism.api.gas.GasTank;

/** Pure calculations used by the Boiler's heated-coolant exchange. */
public final class BoilerCoolantSimulation {

    private BoilerCoolantSimulation() {
    }

    /**
     * Calculates how much heated coolant can be cooled this tick.
     * The result is bounded by input, output capacity, output type, and source temperature.
     */
    public static int getAmountToCool(Coolant coolant, GasTank heatedTank, GasTank cooledTank, double boilerTemperature) {
        if (coolant == null || heatedTank == null || cooledTank == null || heatedTank.getStored() <= 0
              || cooledTank.getNeeded() <= 0 || heatedTank.getGasType() != coolant.getHeatedGas()
              || !cooledTank.canReceiveType(coolant.getCooledGas()) || boilerTemperature >= coolant.getTemperature()) {
            return 0;
        }
        double temperatureEfficiency = 1D - boilerTemperature / coolant.getTemperature();
        long requested = Math.round(coolant.getConductivity() * heatedTank.getStored() * temperatureEfficiency);
        if (requested <= 0) {
            return 0;
        }
        return (int) Math.min(requested, Math.min(heatedTank.getStored(), cooledTank.getNeeded()));
    }

    public static int capacityForVolume(int volume, int capacityPerTank) {
        if (volume <= 0 || capacityPerTank <= 0) {
            return 0;
        }
        return (int) Math.min((long) volume * capacityPerTank, Integer.MAX_VALUE);
    }

    /** Converts registered heat energy into the legacy Boiler's temperature scale. */
    public static double getTemperatureIncrease(Coolant coolant, int amount, double energyPerHeat, int heatCapacity) {
        if (coolant == null || amount <= 0 || !Double.isFinite(energyPerHeat) || energyPerHeat <= 0 || heatCapacity <= 0) {
            return 0;
        }
        return amount * coolant.getThermalEnthalpy() / energyPerHeat / heatCapacity;
    }
}
