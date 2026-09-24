package mekanism.nuclear.common.config;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import mekanism.nuclear.common.NuclearOreType;
import net.minecraftforge.common.config.Configuration;

/** Server-authoritative native ore generation configuration. */
public final class NuclearWorldGenConfig {

    private static final String CATEGORY = "worldgen";
    private static final Map<NuclearOreType, OreSettings> SETTINGS = new EnumMap<>(NuclearOreType.class);

    static {
        resetDefaults();
    }

    private NuclearWorldGenConfig() {
    }

    public static synchronized void load(File file) {
        Configuration config = new Configuration(file);
        config.load();
        for (NuclearOreType type : NuclearOreType.values()) {
            String prefix = capitalize(type.getName());
            boolean enabled = config.getBoolean("Enable" + prefix, CATEGORY, true,
                  "Generate native " + type.getName() + " ore in stone dimensions.");
            int veinsPerChunk = config.getInt(prefix + "PerChunk", CATEGORY, type.getVeinsPerChunk(), 0, 128,
                  "Number of " + type.getName() + " ore generation attempts per chunk. Set to zero to disable.");
            int maxVeinSize = config.getInt(prefix + "VeinSize", CATEGORY, type.getMaxVeinSize(), 1, 512,
                  "Maximum number of blocks in a " + type.getName() + " ore vein.");
            int maxHeight = config.getInt(prefix + "MaxHeight", CATEGORY, type.getMaxHeight(), 1, 256,
                  "Exclusive maximum Y level for " + type.getName() + " ore generation.");
            SETTINGS.put(type, new OreSettings(enabled, veinsPerChunk, maxVeinSize, maxHeight));
        }
        if (config.hasChanged()) {
            config.save();
        }
    }

    public static OreSettings get(NuclearOreType type) {
        return SETTINGS.get(type);
    }

    static void resetDefaults() {
        for (NuclearOreType type : NuclearOreType.values()) {
            SETTINGS.put(type, new OreSettings(true, type.getVeinsPerChunk(), type.getMaxVeinSize(), type.getMaxHeight()));
        }
    }

    private static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public static final class OreSettings {

        private final boolean enabled;
        private final int veinsPerChunk;
        private final int maxVeinSize;
        private final int maxHeight;

        OreSettings(boolean enabled, int veinsPerChunk, int maxVeinSize, int maxHeight) {
            this.enabled = enabled;
            this.veinsPerChunk = veinsPerChunk;
            this.maxVeinSize = maxVeinSize;
            this.maxHeight = maxHeight;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getVeinsPerChunk() {
            return veinsPerChunk;
        }

        public int getMaxVeinSize() {
            return maxVeinSize;
        }

        public int getMaxHeight() {
            return maxHeight;
        }
    }
}
