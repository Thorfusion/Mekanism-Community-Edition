package mekanism.api.gas;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Neutral registry for gas pairs that transfer heat through a Boiler.
 * Optional modules own the gases; Mekanism Core owns only this contract.
 */
public final class GasCoolantRegistry
{
    private static final double MAX_TEMPERATURE = 1_000_000D;
    private static final Map<Gas, Coolant> HEATED_COOLANTS =
          Collections.synchronizedMap(
                new IdentityHashMap<Gas, Coolant>());

    private GasCoolantRegistry()
    {
    }

    public static Coolant register(Gas cooledGas, Gas heatedGas,
          double thermalEnthalpy, double conductivity, double temperature)
    {
        if (cooledGas == null || heatedGas == null)
        {
            throw new IllegalArgumentException("Coolant gases cannot be null.");
        }
        if (cooledGas == heatedGas)
        {
            throw new IllegalArgumentException(
                  "Heated and cooled coolant gases must be different.");
        }
        if (!Double.isFinite(thermalEnthalpy) || thermalEnthalpy <= 0)
        {
            throw new IllegalArgumentException(
                  "Coolant thermal enthalpy must be finite and greater than zero.");
        }
        if (!Double.isFinite(conductivity) || conductivity <= 0
              || conductivity > 1)
        {
            throw new IllegalArgumentException(
                  "Coolant conductivity must be finite, greater than zero, and at most one.");
        }
        if (!Double.isFinite(temperature) || temperature <= 0
              || temperature > MAX_TEMPERATURE)
        {
            throw new IllegalArgumentException(
                  "Coolant temperature must be finite, greater than zero, and at most "
                        + MAX_TEMPERATURE + '.');
        }

        Coolant coolant = new Coolant(cooledGas, heatedGas,
              thermalEnthalpy, conductivity, temperature);
        synchronized (HEATED_COOLANTS)
        {
            Coolant existing = HEATED_COOLANTS.get(heatedGas);
            if (existing != null)
            {
                if (existing.hasSameProperties(coolant))
                {
                    return existing;
                }
                throw new IllegalStateException("Heated coolant gas '"
                      + heatedGas.getName() + "' is already registered.");
            }
            HEATED_COOLANTS.put(heatedGas, coolant);
        }
        return coolant;
    }

    @Nullable
    public static Coolant getByHeatedGas(@Nullable Gas heatedGas)
    {
        return heatedGas == null ? null : HEATED_COOLANTS.get(heatedGas);
    }

    public static boolean isHeatedCoolant(@Nullable Gas gas)
    {
        return getByHeatedGas(gas) != null;
    }

    public static final class Coolant
    {
        private final Gas cooledGas;
        private final Gas heatedGas;
        private final double thermalEnthalpy;
        private final double conductivity;
        private final double temperature;

        private Coolant(Gas cooledGas, Gas heatedGas,
              double thermalEnthalpy, double conductivity,
              double temperature)
        {
            this.cooledGas = cooledGas;
            this.heatedGas = heatedGas;
            this.thermalEnthalpy = thermalEnthalpy;
            this.conductivity = conductivity;
            this.temperature = temperature;
        }

        public Gas getCooledGas()
        {
            return cooledGas;
        }

        public Gas getHeatedGas()
        {
            return heatedGas;
        }

        public double getThermalEnthalpy()
        {
            return thermalEnthalpy;
        }

        public double getConductivity()
        {
            return conductivity;
        }

        public double getTemperature()
        {
            return temperature;
        }

        private boolean hasSameProperties(Coolant other)
        {
            return cooledGas == other.cooledGas
                  && heatedGas == other.heatedGas
                  && Double.compare(thermalEnthalpy,
                        other.thermalEnthalpy) == 0
                  && Double.compare(conductivity, other.conductivity) == 0
                  && Double.compare(temperature, other.temperature) == 0;
        }
    }
}
