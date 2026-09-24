package mekanism.nuclear.common.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

/** Server-authoritative stable Supercritical Phase Shifter values. */
public final class NuclearSPSConfig {

    private static final String CATEGORY = "sps";

    private static int inputPerAntimatter;
    private static long outputTankCapacity;
    private static long energyPerInput;
    private static long portEnergyCapacity;

    static {
        resetDefaults();
    }

    private NuclearSPSConfig() {
    }

    public static synchronized void load(File file) {
        Configuration config = new Configuration(file);
        config.load();
        inputPerAntimatter = config.getInt("InputPerAntimatter", CATEGORY, 1_000,
              1, Integer.MAX_VALUE, "Polonium in mB required to produce one mB of Antimatter.");
        outputTankCapacity = positiveLong(config, "OutputTankCapacity", 1_000L,
              "Antimatter capacity in mB.");
        energyPerInput = positiveLong(config, "EnergyPerInput", 1_000_000L,
              "Energy in joules required to process one mB of Polonium.");
        portEnergyCapacity = positiveLong(config, "PortEnergyCapacity", 1_000_000_000L,
              "Energy in joules stored by each SPS Port.");
        if (config.hasChanged()) {
            config.save();
        }
    }

    private static long positiveLong(Configuration config, String key, long fallback, String comment) {
        return NuclearConfigValues.getLong(config, CATEGORY, key, fallback, 1, comment);
    }

    public static int getInputPerAntimatter() {
        return inputPerAntimatter;
    }

    public static long getInputTankCapacity() {
        return Math.min(Long.MAX_VALUE, inputPerAntimatter * 2L);
    }

    public static long getOutputTankCapacity() {
        return outputTankCapacity;
    }

    public static long getEnergyPerInput() {
        return energyPerInput;
    }

    public static long getPortEnergyCapacity() {
        return portEnergyCapacity;
    }

    static void resetDefaults() {
        inputPerAntimatter = 1_000;
        outputTankCapacity = 1_000L;
        energyPerInput = 1_000_000L;
        portEnergyCapacity = 1_000_000_000L;
    }
}
