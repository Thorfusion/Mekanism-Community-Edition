package mekanism.nuclear.common.config;

import java.math.BigDecimal;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** Compatibility readers for value types Forge 1.12's config API does not model directly. */
final class NuclearConfigValues {

    private NuclearConfigValues() {
    }

    static long getLong(Configuration config, String category, String key, long fallback,
          long minimum, String comment) {
        Property property = config.get(category, key, Long.toString(fallback), comment, Property.Type.INTEGER);
        long value = parseIntegralLong(property.getString(), fallback);
        value = Math.max(minimum, value);
        String normalized = Long.toString(value);
        if (!normalized.equals(property.getString())) {
            // Older code selected Configuration#get(double), which wrote values such as
            // "512000.0". Normalize them so all later loads are unambiguous.
            property.setValue(normalized);
        }
        if (minimum >= Integer.MIN_VALUE && minimum <= Integer.MAX_VALUE) {
            property.setMinValue((int) minimum);
        }
        return value;
    }

    private static long parseIntegralLong(String value, long fallback) {
        try {
            // BigDecimal accepts both the intended integer spelling and legacy exact
            // decimal spellings, but rejects fractional and out-of-range values.
            return new BigDecimal(value.trim()).longValueExact();
        } catch (ArithmeticException | NumberFormatException ignored) {
            return fallback;
        }
    }
}
