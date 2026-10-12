package mekanism.nuclear.common.config;

import java.math.BigDecimal;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** Compatibility readers for values Forge's legacy config API does not model directly. */
final class NuclearConfigValues
{
    private NuclearConfigValues()
    {
    }

    static long getLong(Configuration config, String category, String key,
          long fallback, long minimum, String comment)
    {
        Property property = config.get(category, key, Long.toString(fallback),
              comment, Property.Type.INTEGER);
        long value = parseIntegralLong(property.getString(), fallback);
        value = Math.max(minimum, value);
        String normalized = Long.toString(value);
        if (!normalized.equals(property.getString()))
        {
            property.setValue(normalized);
        }
        if (minimum >= Integer.MIN_VALUE && minimum <= Integer.MAX_VALUE)
        {
            property.setMinValue((int)minimum);
        }
        return value;
    }

    private static long parseIntegralLong(String value, long fallback)
    {
        try
        {
            return new BigDecimal(value.trim()).longValueExact();
        }
        catch (ArithmeticException ignored)
        {
            return fallback;
        }
        catch (NumberFormatException ignored)
        {
            return fallback;
        }
    }
}
