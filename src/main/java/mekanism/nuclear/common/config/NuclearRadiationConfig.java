package mekanism.nuclear.common.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

/** Server-authoritative radiation and Radioactive Waste Barrel settings. */
public final class NuclearRadiationConfig {

    private static final String CATEGORY = "radiation";

    private static boolean radiationEnabled;
    private static int radiationChunkCheckRadius;
    private static double sourceDecayRate;
    private static double targetDecayRate;
    private static double negativeEffectsMinSeverity;
    private static long wasteBarrelCapacity;
    private static int wasteBarrelProcessTicks;
    private static long wasteBarrelDecayAmount;

    static {
        resetDefaults();
    }

    private NuclearRadiationConfig() {
    }

    public static synchronized void load(File file) {
        Configuration config = new Configuration(file);
        config.load();
        radiationEnabled = config.getBoolean("Enabled", CATEGORY, true,
              "Enable environmental radiation and radioactive exposure.");
        radiationChunkCheckRadius = config.getInt("ChunkCheckRadius", CATEGORY, 5, 1, 100,
              "Chunk radius considered when calculating nearby environmental radiation.");
        sourceDecayRate = config.get(CATEGORY, "SourceDecayRate", 0.9995D,
              "Multiplier applied to environmental radiation sources every 20 ticks.", 0, 1).getDouble();
        targetDecayRate = config.get(CATEGORY, "TargetDecayRate", 0.9995D,
              "Multiplier applied to accumulated player radiation every 20 ticks.", 0, 1).getDouble();
        negativeEffectsMinSeverity = config.get(CATEGORY, "NegativeEffectsMinSeverity", 0.1D,
              "Minimum normalized radiation severity that can cause damage and exhaustion.", 0, 1).getDouble();

        wasteBarrelCapacity = NuclearConfigValues.getLong(config, CATEGORY, "WasteBarrelCapacity", 512_000L, 1,
              "Radioactive Waste Barrel capacity in mB.");
        wasteBarrelProcessTicks = config.getInt("WasteBarrelProcessTicks", CATEGORY, 20, 1,
              Integer.MAX_VALUE, "Ticks required for a Radioactive Waste Barrel decay operation.");
        wasteBarrelDecayAmount = NuclearConfigValues.getLong(config, CATEGORY, "WasteBarrelDecayAmount", 1L, 0,
              "Amount in mB removed by each Radioactive Waste Barrel decay operation. Set to zero to disable decay.");
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static boolean isRadiationEnabled() {
        return radiationEnabled;
    }

    public static int getRadiationChunkCheckRadius() {
        return radiationChunkCheckRadius;
    }

    public static double getSourceDecayRate() {
        return sourceDecayRate;
    }

    public static double getTargetDecayRate() {
        return targetDecayRate;
    }

    public static double getNegativeEffectsMinSeverity() {
        return negativeEffectsMinSeverity;
    }

    public static long getWasteBarrelCapacity() {
        return wasteBarrelCapacity;
    }

    public static int getWasteBarrelProcessTicks() {
        return wasteBarrelProcessTicks;
    }

    public static long getWasteBarrelDecayAmount() {
        return wasteBarrelDecayAmount;
    }

    static void resetDefaults() {
        radiationEnabled = true;
        radiationChunkCheckRadius = 5;
        sourceDecayRate = 0.9995D;
        targetDecayRate = 0.9995D;
        negativeEffectsMinSeverity = 0.1D;
        wasteBarrelCapacity = 512_000L;
        wasteBarrelProcessTicks = 20;
        wasteBarrelDecayAmount = 1;
    }
}
