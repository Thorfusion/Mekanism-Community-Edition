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
    public static final long DEFAULT_SUIT_POTION_TICK_USAGE = 40_000L;
    public static final long DEFAULT_SUIT_MAGIC_REDUCE_USAGE = 1_000L;
    public static final float DEFAULT_SUIT_MAGIC_DAMAGE_REDUCTION_RATIO = 1F;
    public static final long DEFAULT_TOOL_MINING_USAGE = 10L;
    public static final long DEFAULT_TOOL_SILK_MINING_USAGE = 100L;
    public static final long DEFAULT_TOOL_WEAPON_USAGE = 2_000L;
    public static final long DEFAULT_TOOL_HOE_USAGE = 10L;
    public static final long DEFAULT_TOOL_SHOVEL_USAGE = 10L;
    public static final long DEFAULT_TOOL_SHEAR_ENTITY_USAGE = 10L;
    public static final long DEFAULT_TOOL_TELEPORT_USAGE = 1_000L;
    public static final int DEFAULT_TOOL_MAX_TELEPORT_REACH = 100;
    public static final float DEFAULT_TOOL_EFFICIENCY = 4F;
    public static final int DEFAULT_TOOL_BASE_DAMAGE = 4;
    public static final double DEFAULT_TOOL_ATTACK_SPEED = -2.4D;
    public static final int DEFAULT_TOOL_VEIN_MINING_MAX_BLOCKS = 128;
    public static final boolean DEFAULT_TOOL_EXTENDED_MINING = true;
    public static final long DEFAULT_MODIFICATION_STATION_CAPACITY = 40_000L;
    public static final long DEFAULT_MODIFICATION_STATION_USAGE = 400L;

    public static long toolCapacity = DEFAULT_TOOL_CAPACITY;
    public static long toolChargeRate = DEFAULT_TOOL_CHARGE_RATE;
    public static long suitCapacity = DEFAULT_SUIT_CAPACITY;
    public static long suitChargeRate = DEFAULT_SUIT_CHARGE_RATE;
    public static long suitPotionTickUsage = DEFAULT_SUIT_POTION_TICK_USAGE;
    public static long suitMagicReduceUsage = DEFAULT_SUIT_MAGIC_REDUCE_USAGE;
    public static float suitMagicDamageReductionRatio = DEFAULT_SUIT_MAGIC_DAMAGE_REDUCTION_RATIO;
    public static long toolMiningUsage = DEFAULT_TOOL_MINING_USAGE;
    public static long toolSilkMiningUsage = DEFAULT_TOOL_SILK_MINING_USAGE;
    public static long toolWeaponUsage = DEFAULT_TOOL_WEAPON_USAGE;
    public static long toolHoeUsage = DEFAULT_TOOL_HOE_USAGE;
    public static long toolShovelUsage = DEFAULT_TOOL_SHOVEL_USAGE;
    public static long toolShearEntityUsage = DEFAULT_TOOL_SHEAR_ENTITY_USAGE;
    public static long toolTeleportUsage = DEFAULT_TOOL_TELEPORT_USAGE;
    public static int toolMaxTeleportReach = DEFAULT_TOOL_MAX_TELEPORT_REACH;
    public static float toolEfficiency = DEFAULT_TOOL_EFFICIENCY;
    public static int toolBaseDamage = DEFAULT_TOOL_BASE_DAMAGE;
    public static double toolAttackSpeed = DEFAULT_TOOL_ATTACK_SPEED;
    public static int toolVeinMiningMaxBlocks = DEFAULT_TOOL_VEIN_MINING_MAX_BLOCKS;
    public static boolean toolExtendedMining = DEFAULT_TOOL_EXTENDED_MINING;
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
        toolTeleportUsage = getLong(config, "meka_tool", "teleportEnergyUsage",
              DEFAULT_TOOL_TELEPORT_USAGE, 1,
              "Base Teleportation Unit energy cost, multiplied by squared distance divided by ten.");
        toolMaxTeleportReach = config.getInt("maxTeleportReach", "meka_tool",
              DEFAULT_TOOL_MAX_TELEPORT_REACH, 3, 1_024,
              "Maximum Teleportation Unit ray-trace distance in blocks.");
        toolEfficiency = config.getFloat("baseEfficiency", "meka_tool", DEFAULT_TOOL_EFFICIENCY, 0.1F, 100F,
              "Mining speed while the Meka-Tool has enough energy.");
        toolBaseDamage = config.getInt("baseDamage", "meka_tool", DEFAULT_TOOL_BASE_DAMAGE, 0, 100_000,
              "Base Meka-Tool attack damage before Attack Amplification.");
        toolAttackSpeed = Math.max(-4D, Math.min(100D,
              config.get("meka_tool", "attackSpeed", DEFAULT_TOOL_ATTACK_SPEED,
                    "Meka-Tool main-hand attack speed modifier.").getDouble(DEFAULT_TOOL_ATTACK_SPEED)));
        toolVeinMiningMaxBlocks = config.getInt("veinMiningMaxBlocks", "meka_tool",
              DEFAULT_TOOL_VEIN_MINING_MAX_BLOCKS, 2, 1_000_000,
              "Maximum connected blocks added for each block type found by the Vein Mining Unit.");
        toolExtendedMining = config.getBoolean("extendedMining", "meka_tool", DEFAULT_TOOL_EXTENDED_MINING,
              "Allows the Vein Mining Unit's Extended mode to include matching non-ore blocks.");
        suitCapacity = getLong(config, "mekasuit", "baseEnergyCapacity", DEFAULT_SUIT_CAPACITY, 1,
              "Base energy capacity of each MekaSuit piece. Each Energy Unit doubles this value.");
        suitChargeRate = getLong(config, "mekasuit", "chargeRate", DEFAULT_SUIT_CHARGE_RATE, 1,
              "Base charge rate of each MekaSuit piece. Each Energy Unit doubles this value.");
        suitPotionTickUsage = getLong(config, "mekasuit", "energyUsagePotionTick",
              DEFAULT_SUIT_POTION_TICK_USAGE, 0,
              "Energy used per selected potion effect accelerated by the Inhalation Purification Unit each tick.");
        suitMagicReduceUsage = getLong(config, "mekasuit", "magicReduce",
              DEFAULT_SUIT_MAGIC_REDUCE_USAGE, 0,
              "Energy cost per half-heart of magic damage reduced by the Inhalation Purification Unit.");
        suitMagicDamageReductionRatio = config.getFloat("magicDamageReductionRatio", "mekasuit",
              DEFAULT_SUIT_MAGIC_DAMAGE_REDUCTION_RATIO, 0F, 1F,
              "Maximum fraction of preventable magic damage absorbed by the Inhalation Purification Unit.");
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
