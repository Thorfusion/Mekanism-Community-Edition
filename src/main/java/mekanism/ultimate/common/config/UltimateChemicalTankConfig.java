package mekanism.ultimate.common.config;

import java.math.BigDecimal;
import mekanism.ultimate.common.tier.ChemicalTankTier;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** Stable tier balance and dumping behavior for Ultimate-owned Chemical Tanks. */
public final class UltimateChemicalTankConfig {

    private static final long[] STORAGE = new long[ChemicalTankTier.values().length];
    private static final long[] OUTPUT = new long[ChemicalTankTier.values().length];

    public static double dumpExcessKeepRatio = 0.85D;

    static {
        resetDefaults();
    }

    private UltimateChemicalTankConfig() {
    }

    public static void load(Configuration config) {
        for (ChemicalTankTier tier : ChemicalTankTier.values()) {
            if (tier.isCreative()) {
                STORAGE[tier.ordinal()] = Long.MAX_VALUE;
                OUTPUT[tier.ordinal()] = Long.MAX_VALUE / 2;
            } else {
                String prefix = tier.getName() + ".";
                STORAGE[tier.ordinal()] = getLong(config, "chemical_tanks", prefix + "storage",
                      tier.getBaseStorage(), 1L, "Chemical Tank capacity in millibuckets.");
                OUTPUT[tier.ordinal()] = getLong(config, "chemical_tanks", prefix + "output",
                      tier.getBaseOutput(), 1L, "Maximum automatic output per tick in millibuckets.");
            }
        }
        dumpExcessKeepRatio = config.getFloat("dumpExcessKeepRatio", "chemical_tanks", 0.85F,
              0.001F, 1F, "Fraction retained by Dumping Excess mode.");
    }

    public static long getStorage(ChemicalTankTier tier) {
        return STORAGE[tier.ordinal()];
    }

    public static long getOutput(ChemicalTankTier tier) {
        return OUTPUT[tier.ordinal()];
    }

    private static void resetDefaults() {
        for (ChemicalTankTier tier : ChemicalTankTier.values()) {
            STORAGE[tier.ordinal()] = tier.getBaseStorage();
            OUTPUT[tier.ordinal()] = tier.getBaseOutput();
        }
    }

    private static long getLong(Configuration config, String category, String key, long fallback,
          long minimum, String comment) {
        Property property = config.get(category, key, Long.toString(fallback), comment, Property.Type.INTEGER);
        long value;
        try {
            value = new BigDecimal(property.getString().trim()).longValueExact();
        } catch (ArithmeticException | NumberFormatException ignored) {
            value = fallback;
        }
        value = Math.max(minimum, value);
        String normalized = Long.toString(value);
        if (!normalized.equals(property.getString())) {
            property.setValue(normalized);
        }
        return value;
    }
}
