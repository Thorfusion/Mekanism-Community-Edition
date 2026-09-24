package mekanism.mekasuit.common.config;

import java.io.File;
import java.math.BigDecimal;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** Module-local balance values copied from the pinned stable gear defaults. */
public final class MekaSuitConfig {

    public static final long DEFAULT_TOOL_CAPACITY = 16_000_000L;
    public static final long DEFAULT_TOOL_CHARGE_RATE = 100_000L;
    public static final long DEFAULT_SUIT_CAPACITY = 16_000_000L;
    public static final long DEFAULT_SUIT_CHARGE_RATE = 100_000L;
    public static final long DEFAULT_TOOL_MINING_USAGE = 10L;
    public static final float DEFAULT_TOOL_EFFICIENCY = 4F;

    public static long toolCapacity = DEFAULT_TOOL_CAPACITY;
    public static long toolChargeRate = DEFAULT_TOOL_CHARGE_RATE;
    public static long suitCapacity = DEFAULT_SUIT_CAPACITY;
    public static long suitChargeRate = DEFAULT_SUIT_CHARGE_RATE;
    public static long toolMiningUsage = DEFAULT_TOOL_MINING_USAGE;
    public static float toolEfficiency = DEFAULT_TOOL_EFFICIENCY;

    private MekaSuitConfig() {
    }

    public static void load(File file) {
        Configuration config = new Configuration(file);
        config.load();
        toolCapacity = getLong(config, "meka_tool", "baseEnergyCapacity", DEFAULT_TOOL_CAPACITY, 1,
              "Base Meka-Tool energy capacity in Joules. Each Energy Unit doubles this value.");
        toolChargeRate = getLong(config, "meka_tool", "chargeRate", DEFAULT_TOOL_CHARGE_RATE, 1,
              "Base Meka-Tool charge rate in Joules per tick. Each Energy Unit doubles this value.");
        toolMiningUsage = getLong(config, "meka_tool", "baseMiningEnergyUsage", DEFAULT_TOOL_MINING_USAGE, 1,
              "Base mining energy cost, multiplied by the current mining efficiency.");
        toolEfficiency = config.getFloat("baseEfficiency", "meka_tool", DEFAULT_TOOL_EFFICIENCY, 0.1F, 100F,
              "Mining speed while the Meka-Tool has enough energy.");
        suitCapacity = getLong(config, "mekasuit", "baseEnergyCapacity", DEFAULT_SUIT_CAPACITY, 1,
              "Base energy capacity of each MekaSuit piece. Each Energy Unit doubles this value.");
        suitChargeRate = getLong(config, "mekasuit", "chargeRate", DEFAULT_SUIT_CHARGE_RATE, 1,
              "Base charge rate of each MekaSuit piece. Each Energy Unit doubles this value.");
        if (config.hasChanged()) {
            config.save();
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
        if (minimum >= Integer.MIN_VALUE && minimum <= Integer.MAX_VALUE) {
            property.setMinValue((int) minimum);
        }
        return value;
    }
}
