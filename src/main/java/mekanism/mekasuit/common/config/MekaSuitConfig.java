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
    public static final long DEFAULT_TOOL_SILK_MINING_USAGE = 100L;
    public static final long DEFAULT_TOOL_WEAPON_USAGE = 2_000L;
    public static final long DEFAULT_TOOL_HOE_USAGE = 10L;
    public static final long DEFAULT_TOOL_SHOVEL_USAGE = 10L;
    public static final long DEFAULT_TOOL_SHEAR_ENTITY_USAGE = 10L;
    public static final float DEFAULT_TOOL_EFFICIENCY = 4F;
    public static final int DEFAULT_TOOL_BASE_DAMAGE = 4;
    public static final double DEFAULT_TOOL_ATTACK_SPEED = -2.4D;
    public static final long DEFAULT_MODIFICATION_STATION_CAPACITY = 40_000L;
    public static final long DEFAULT_MODIFICATION_STATION_USAGE = 400L;

    public static long toolCapacity = DEFAULT_TOOL_CAPACITY;
    public static long toolChargeRate = DEFAULT_TOOL_CHARGE_RATE;
    public static long suitCapacity = DEFAULT_SUIT_CAPACITY;
    public static long suitChargeRate = DEFAULT_SUIT_CHARGE_RATE;
    public static long toolMiningUsage = DEFAULT_TOOL_MINING_USAGE;
    public static long toolSilkMiningUsage = DEFAULT_TOOL_SILK_MINING_USAGE;
    public static long toolWeaponUsage = DEFAULT_TOOL_WEAPON_USAGE;
    public static long toolHoeUsage = DEFAULT_TOOL_HOE_USAGE;
    public static long toolShovelUsage = DEFAULT_TOOL_SHOVEL_USAGE;
    public static long toolShearEntityUsage = DEFAULT_TOOL_SHEAR_ENTITY_USAGE;
    public static float toolEfficiency = DEFAULT_TOOL_EFFICIENCY;
    public static int toolBaseDamage = DEFAULT_TOOL_BASE_DAMAGE;
    public static double toolAttackSpeed = DEFAULT_TOOL_ATTACK_SPEED;
    public static long modificationStationCapacity = DEFAULT_MODIFICATION_STATION_CAPACITY;
    public static long modificationStationUsage = DEFAULT_MODIFICATION_STATION_USAGE;

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
        toolSilkMiningUsage = getLong(config, "meka_tool", "silkMiningEnergyUsage",
              DEFAULT_TOOL_SILK_MINING_USAGE, 1,
              "Silk Touch mining energy cost, multiplied by the current mining efficiency.");
        toolWeaponUsage = getLong(config, "meka_tool", "weaponEnergyUsage",
              DEFAULT_TOOL_WEAPON_USAGE, 1,
              "Weapon energy cost at four points of Attack Amplification damage.");
        toolHoeUsage = getLong(config, "meka_tool", "hoeEnergyUsage", DEFAULT_TOOL_HOE_USAGE, 1,
              "Energy cost for each block tilled by the Farming Unit.");
        toolShovelUsage = getLong(config, "meka_tool", "shovelEnergyUsage", DEFAULT_TOOL_SHOVEL_USAGE, 1,
              "Energy cost for each grass block flattened by the Farming Unit.");
        toolShearEntityUsage = getLong(config, "meka_tool", "shearEntityEnergyUsage",
              DEFAULT_TOOL_SHEAR_ENTITY_USAGE, 1, "Energy cost for shearing an entity with the Shearing Unit.");
        toolEfficiency = config.getFloat("baseEfficiency", "meka_tool", DEFAULT_TOOL_EFFICIENCY, 0.1F, 100F,
              "Mining speed while the Meka-Tool has enough energy.");
        toolBaseDamage = config.getInt("baseDamage", "meka_tool", DEFAULT_TOOL_BASE_DAMAGE, 0, 100_000,
              "Base Meka-Tool attack damage before Attack Amplification.");
        toolAttackSpeed = Math.max(-4D, Math.min(100D,
              config.get("meka_tool", "attackSpeed", DEFAULT_TOOL_ATTACK_SPEED,
                    "Meka-Tool main-hand attack speed modifier.").getDouble(DEFAULT_TOOL_ATTACK_SPEED)));
        suitCapacity = getLong(config, "mekasuit", "baseEnergyCapacity", DEFAULT_SUIT_CAPACITY, 1,
              "Base energy capacity of each MekaSuit piece. Each Energy Unit doubles this value.");
        suitChargeRate = getLong(config, "mekasuit", "chargeRate", DEFAULT_SUIT_CHARGE_RATE, 1,
              "Base charge rate of each MekaSuit piece. Each Energy Unit doubles this value.");
        modificationStationCapacity = getLong(config, "modification_station", "energyCapacity",
              DEFAULT_MODIFICATION_STATION_CAPACITY, 1, "Modification Station energy capacity in Joules.");
        modificationStationUsage = getLong(config, "modification_station", "energyPerTick",
              DEFAULT_MODIFICATION_STATION_USAGE, 1,
              "Modification Station energy usage while installing modules.");
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
