package mekanism.nuclear.common.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** Server-authoritative radiation and Radioactive Waste Barrel settings. */
public final class NuclearRadiationConfig {

    private static final String CATEGORY = "radiation";

    private static boolean radiationEnabled;
    private static int radiationChunkCheckRadius;
    private static double sourceDecayRate;
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

        Property capacity = config.get(CATEGORY, "WasteBarrelCapacity", 512_000L,
              "Radioactive Waste Barrel capacity in mB.");
        capacity.setMinValue(1);
        wasteBarrelCapacity = Math.max(1, capacity.getLong());
        wasteBarrelProcessTicks = config.getInt("WasteBarrelProcessTicks", CATEGORY, 20, 1,
              Integer.MAX_VALUE, "Ticks required for a Radioactive Waste Barrel decay operation.");
        Property decayAmount = config.get(CATEGORY, "WasteBarrelDecayAmount", 1L,
              "Amount in mB removed by each Radioactive Waste Barrel decay operation. Set to zero to disable decay.");
        decayAmount.setMinValue(0);
        wasteBarrelDecayAmount = Math.max(0, decayAmount.getLong());
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
        wasteBarrelCapacity = 512_000L;
        wasteBarrelProcessTicks = 20;
        wasteBarrelDecayAmount = 1;
    }
}
