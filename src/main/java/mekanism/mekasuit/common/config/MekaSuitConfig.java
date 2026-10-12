package mekanism.mekasuit.common.config;

import io.netty.buffer.ByteBuf;
import java.math.BigDecimal;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** Module-local values copied from the pinned stable gear defaults. */
public final class MekaSuitConfig
{
    public static final long DEFAULT_TOOL_CAPACITY = 16_000_000L;
    public static final long DEFAULT_TOOL_CHARGE_RATE = 100_000L;
    public static final long DEFAULT_SUIT_CAPACITY = 16_000_000L;
    public static final long DEFAULT_SUIT_CHARGE_RATE = 100_000L;
    public static final long DEFAULT_SUIT_POTION_TICK_USAGE = 40_000L;
    public static final long DEFAULT_SUIT_MAGIC_REDUCE_USAGE = 1_000L;
    public static final long DEFAULT_SUIT_DAMAGE_USAGE = 100_000L;
    public static final float DEFAULT_SUIT_MAGIC_DAMAGE_REDUCTION_RATIO = 1F;
    public static final long DEFAULT_SUIT_VISION_ENHANCEMENT_USAGE = 500L;
    public static final long DEFAULT_SUIT_NUTRITIONAL_INJECTION_USAGE = 20_000L;
    public static final int DEFAULT_SUIT_NUTRITIONAL_STORAGE = 128_000;
    public static final int DEFAULT_SUIT_NUTRITIONAL_TRANSFER_RATE = 256;
    public static final int DEFAULT_SUIT_JETPACK_STORAGE = 24_000;
    public static final int DEFAULT_SUIT_JETPACK_TRANSFER_RATE = 256;
    public static final long DEFAULT_SUIT_INVENTORY_CHARGE_RATE = 10_000L;
    public static final long DEFAULT_SUIT_GRAVITATIONAL_MODULATION_USAGE = 1_000L;
    public static final long DEFAULT_SUIT_ELYTRA_ENERGY_USAGE = 32_000L;
    public static final long DEFAULT_SUIT_SPRINT_BOOST_USAGE = 100L;
    public static final long DEFAULT_SUIT_BASE_JUMP_USAGE = 1_000L;
    public static final long DEFAULT_SUIT_HYDROSTATIC_REPULSION_USAGE = 500L;
    public static final long DEFAULT_SUIT_ITEM_ATTRACTION_USAGE = 250L;
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
    public static final int DEFAULT_TOOL_VEIN_MINING_MAX_BLOCKS = 128;
    public static final boolean DEFAULT_TOOL_EXTENDED_MINING = true;
    public static final long DEFAULT_MODIFICATION_STATION_CAPACITY = 40_000L;
    public static final long DEFAULT_MODIFICATION_STATION_USAGE = 400L;
    public static final double DEFAULT_SOLAR_RECHARGING_RATE = 500D;
    public static final double DEFAULT_GEOTHERMAL_CHARGING_RATE = 10D;
    public static final double DEFAULT_HEAT_DAMAGE_REDUCTION_RATIO = 0.8D;

    public static long toolCapacity = DEFAULT_TOOL_CAPACITY;
    public static long toolChargeRate = DEFAULT_TOOL_CHARGE_RATE;
    public static long suitCapacity = DEFAULT_SUIT_CAPACITY;
    public static long suitChargeRate = DEFAULT_SUIT_CHARGE_RATE;
    public static long suitPotionTickUsage = DEFAULT_SUIT_POTION_TICK_USAGE;
    public static long suitMagicReduceUsage = DEFAULT_SUIT_MAGIC_REDUCE_USAGE;
    public static long suitDamageUsage = DEFAULT_SUIT_DAMAGE_USAGE;
    public static float suitMagicDamageReductionRatio =
          DEFAULT_SUIT_MAGIC_DAMAGE_REDUCTION_RATIO;
    public static long suitVisionEnhancementUsage =
          DEFAULT_SUIT_VISION_ENHANCEMENT_USAGE;
    public static long suitNutritionalInjectionUsage =
          DEFAULT_SUIT_NUTRITIONAL_INJECTION_USAGE;
    public static int suitNutritionalStorage = DEFAULT_SUIT_NUTRITIONAL_STORAGE;
    public static int suitNutritionalTransferRate =
          DEFAULT_SUIT_NUTRITIONAL_TRANSFER_RATE;
    public static int suitJetpackStorage = DEFAULT_SUIT_JETPACK_STORAGE;
    public static int suitJetpackTransferRate =
          DEFAULT_SUIT_JETPACK_TRANSFER_RATE;
    public static long suitInventoryChargeRate =
          DEFAULT_SUIT_INVENTORY_CHARGE_RATE;
    public static long suitGravitationalModulationUsage =
          DEFAULT_SUIT_GRAVITATIONAL_MODULATION_USAGE;
    public static long suitElytraEnergyUsage = DEFAULT_SUIT_ELYTRA_ENERGY_USAGE;
    public static long suitSprintBoostUsage = DEFAULT_SUIT_SPRINT_BOOST_USAGE;
    public static long suitBaseJumpUsage = DEFAULT_SUIT_BASE_JUMP_USAGE;
    public static long suitHydrostaticRepulsionUsage =
          DEFAULT_SUIT_HYDROSTATIC_REPULSION_USAGE;
    public static long suitItemAttractionUsage =
          DEFAULT_SUIT_ITEM_ATTRACTION_USAGE;
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
    public static int toolVeinMiningMaxBlocks =
          DEFAULT_TOOL_VEIN_MINING_MAX_BLOCKS;
    public static boolean toolExtendedMining = DEFAULT_TOOL_EXTENDED_MINING;
    public static long modificationStationCapacity =
          DEFAULT_MODIFICATION_STATION_CAPACITY;
    public static long modificationStationUsage =
          DEFAULT_MODIFICATION_STATION_USAGE;
    public static double solarRechargingRate = DEFAULT_SOLAR_RECHARGING_RATE;
    public static double geothermalChargingRate =
          DEFAULT_GEOTHERMAL_CHARGING_RATE;
    public static double heatDamageReductionRatio =
          DEFAULT_HEAT_DAMAGE_REDUCTION_RATIO;

    private MekaSuitConfig()
    {
    }

    public static void load(Configuration config)
    {
        toolCapacity = getLong(config, "meka_tool", "baseEnergyCapacity",
              DEFAULT_TOOL_CAPACITY, 1,
              "Base Meka-Tool energy capacity in Joules. Each Energy Unit doubles this value.");
        toolChargeRate = getLong(config, "meka_tool", "chargeRate",
              DEFAULT_TOOL_CHARGE_RATE, 1,
              "Base Meka-Tool charge rate in Joules per tick. Each Energy Unit doubles this value.");
        toolMiningUsage = getLong(config, "meka_tool",
              "baseMiningEnergyUsage", DEFAULT_TOOL_MINING_USAGE, 1,
              "Base mining energy cost, multiplied by the current mining efficiency.");
        toolSilkMiningUsage = getLong(config, "meka_tool",
              "silkMiningEnergyUsage", DEFAULT_TOOL_SILK_MINING_USAGE, 1,
              "Silk Touch mining energy cost, multiplied by the current mining efficiency.");
        toolWeaponUsage = getLong(config, "meka_tool", "weaponEnergyUsage",
              DEFAULT_TOOL_WEAPON_USAGE, 1,
              "Weapon energy cost at four points of Attack Amplification damage.");
        toolHoeUsage = getLong(config, "meka_tool", "hoeEnergyUsage",
              DEFAULT_TOOL_HOE_USAGE, 1,
              "Energy cost for each block tilled by the Farming Unit.");
        toolShovelUsage = getLong(config, "meka_tool", "shovelEnergyUsage",
              DEFAULT_TOOL_SHOVEL_USAGE, 1,
              "Energy cost for each grass block flattened by the Farming Unit.");
        toolShearEntityUsage = getLong(config, "meka_tool",
              "shearEntityEnergyUsage", DEFAULT_TOOL_SHEAR_ENTITY_USAGE, 1,
              "Energy cost for shearing an entity with the Shearing Unit.");
        toolTeleportUsage = getLong(config, "meka_tool",
              "teleportEnergyUsage", DEFAULT_TOOL_TELEPORT_USAGE, 1,
              "Base Teleportation Unit energy cost, multiplied by squared distance divided by ten.");
        toolMaxTeleportReach = config.get("meka_tool", "maxTeleportReach",
              DEFAULT_TOOL_MAX_TELEPORT_REACH,
              "Maximum Teleportation Unit ray-trace distance in blocks.",
              3, 1_024).getInt(DEFAULT_TOOL_MAX_TELEPORT_REACH);
        toolEfficiency = (float)Math.max(0.1D, Math.min(100D,
              config.get("meka_tool", "baseEfficiency",
                    DEFAULT_TOOL_EFFICIENCY,
                    "Mining speed while the Meka-Tool has enough energy.")
                    .getDouble(DEFAULT_TOOL_EFFICIENCY)));
        toolBaseDamage = config.get("meka_tool", "baseDamage",
              DEFAULT_TOOL_BASE_DAMAGE,
              "Base Meka-Tool attack damage before Attack Amplification.",
              0, 100_000).getInt(DEFAULT_TOOL_BASE_DAMAGE);
        toolVeinMiningMaxBlocks = config.get("meka_tool",
              "veinMiningMaxBlocks", DEFAULT_TOOL_VEIN_MINING_MAX_BLOCKS,
              "Maximum connected blocks added for each block type found by the Vein Mining Unit.",
              2, 1_000_000).getInt(DEFAULT_TOOL_VEIN_MINING_MAX_BLOCKS);
        toolExtendedMining = config.get("meka_tool", "extendedMining",
              DEFAULT_TOOL_EXTENDED_MINING,
              "Allows the Vein Mining Unit's Extended mode to include matching non-ore blocks.")
              .getBoolean(DEFAULT_TOOL_EXTENDED_MINING);
        suitCapacity = getLong(config, "mekasuit", "baseEnergyCapacity",
              DEFAULT_SUIT_CAPACITY, 1,
              "Base energy capacity of each MekaSuit piece. Each Energy Unit doubles this value.");
        suitChargeRate = getLong(config, "mekasuit", "chargeRate",
              DEFAULT_SUIT_CHARGE_RATE, 1,
              "Base charge rate of each MekaSuit piece. Each Energy Unit doubles this value.");
        suitPotionTickUsage = getLong(config, "mekasuit",
              "energyUsagePotionTick", DEFAULT_SUIT_POTION_TICK_USAGE, 0,
              "Energy used per selected potion effect accelerated by the Inhalation Purification Unit each tick.");
        suitMagicReduceUsage = getLong(config, "mekasuit", "magicReduce",
              DEFAULT_SUIT_MAGIC_REDUCE_USAGE, 0,
              "Energy cost per half-heart of magic damage reduced by the Inhalation Purification Unit.");
        suitDamageUsage = getLong(config, "mekasuit", "damage",
              DEFAULT_SUIT_DAMAGE_USAGE, 0,
              "Energy used per point of damage absorbed by the base MekaSuit armor.");
        suitMagicDamageReductionRatio = (float)Math.max(0D, Math.min(1D,
              config.get("mekasuit", "magicDamageReductionRatio",
                    DEFAULT_SUIT_MAGIC_DAMAGE_REDUCTION_RATIO,
                    "Maximum fraction of preventable magic damage absorbed by the Inhalation Purification Unit.")
                    .getDouble(DEFAULT_SUIT_MAGIC_DAMAGE_REDUCTION_RATIO)));
        suitVisionEnhancementUsage = getLong(config, "mekasuit",
              "visionEnhancement", DEFAULT_SUIT_VISION_ENHANCEMENT_USAGE, 0,
              "Energy used per tick while the Vision Enhancement Unit is active.");
        suitNutritionalInjectionUsage = getLong(config, "mekasuit",
              "nutritionalInjection", DEFAULT_SUIT_NUTRITIONAL_INJECTION_USAGE,
              0, "Energy used for each half-drumstick restored by the Nutritional Injection Unit.");
        suitNutritionalStorage = config.get("mekasuit",
              "nutritionalMaxStorage", DEFAULT_SUIT_NUTRITIONAL_STORAGE,
              "Maximum Nutritional Paste stored by a MekaSuit Helmet in millibuckets.",
              1, Integer.MAX_VALUE).getInt(DEFAULT_SUIT_NUTRITIONAL_STORAGE);
        suitNutritionalTransferRate = config.get("mekasuit",
              "nutritionalTransferRate", DEFAULT_SUIT_NUTRITIONAL_TRANSFER_RATE,
              "Maximum Nutritional Paste transferred into a MekaSuit Helmet per operation.",
              1, Integer.MAX_VALUE).getInt(DEFAULT_SUIT_NUTRITIONAL_TRANSFER_RATE);
        suitJetpackStorage = config.get("mekasuit", "jetpackMaxStorage",
              DEFAULT_SUIT_JETPACK_STORAGE,
              "Hydrogen storage added to MekaSuit Bodyarmor by each installed Jetpack Unit, in millibuckets.",
              1, Integer.MAX_VALUE).getInt(DEFAULT_SUIT_JETPACK_STORAGE);
        suitJetpackTransferRate = config.get("mekasuit", "jetpackTransferRate",
              DEFAULT_SUIT_JETPACK_TRANSFER_RATE,
              "Maximum Hydrogen transferred into Jetpack Unit storage per operation.",
              1, Integer.MAX_VALUE).getInt(DEFAULT_SUIT_JETPACK_TRANSFER_RATE);
        suitInventoryChargeRate = getLong(config, "mekasuit",
              "inventoryChargeRate", DEFAULT_SUIT_INVENTORY_CHARGE_RATE, 1,
              "Maximum energy transferred from the Charge Distribution Unit into inventory items per tick.");
        suitGravitationalModulationUsage = getLong(config, "mekasuit",
              "gravitationalModulation",
              DEFAULT_SUIT_GRAVITATIONAL_MODULATION_USAGE, 0,
              "Energy used per tick while flying with the Gravitational Modulating Unit.");
        suitElytraEnergyUsage = getLong(config, "mekasuit", "elytra",
              DEFAULT_SUIT_ELYTRA_ENERGY_USAGE, 0,
              "Energy used per second while gliding with the MekaSuit Elytra Unit.");
        suitSprintBoostUsage = getLong(config, "mekasuit", "sprintBoost",
              DEFAULT_SUIT_SPRINT_BOOST_USAGE, 0,
              "Energy used per tick at the Medium Locomotive Boosting setting.");
        suitBaseJumpUsage = getLong(config, "mekasuit", "jump",
              DEFAULT_SUIT_BASE_JUMP_USAGE, 0,
              "Base Hydraulic Propulsion jump energy usage.");
        suitHydrostaticRepulsionUsage = getLong(config, "mekasuit",
              "hydrostaticRepulsion", DEFAULT_SUIT_HYDROSTATIC_REPULSION_USAGE,
              0, "Energy used per tick by the four-unit Hydrostatic Repulsor swim boost.");
        suitItemAttractionUsage = getLong(config, "mekasuit",
              "itemAttraction", DEFAULT_SUIT_ITEM_ATTRACTION_USAGE, 0,
              "Energy used per attracted item and range block by the Magnetic Attraction Unit.");
        modificationStationCapacity = getLong(config, "modification_station",
              "energyCapacity", DEFAULT_MODIFICATION_STATION_CAPACITY, 1,
              "Modification Station energy capacity in Joules.");
        modificationStationUsage = getLong(config, "modification_station",
              "energyPerTick", DEFAULT_MODIFICATION_STATION_USAGE, 1,
              "Modification Station energy usage while installing modules.");
        solarRechargingRate = config.get("generators",
              "solarRechargingRate", DEFAULT_SOLAR_RECHARGING_RATE,
              "Peak Joules per tick generated by each Solar Recharging Unit.",
              0D, Double.MAX_VALUE).getDouble(DEFAULT_SOLAR_RECHARGING_RATE);
        geothermalChargingRate = config.get("generators",
              "geothermalChargingRate", DEFAULT_GEOTHERMAL_CHARGING_RATE,
              "Joules generated per tick, degree, and Geothermal Generator Unit.",
              0D, Double.MAX_VALUE).getDouble(DEFAULT_GEOTHERMAL_CHARGING_RATE);
        heatDamageReductionRatio = clamp(config.get("generators",
              "heatDamageReductionRatio",
              DEFAULT_HEAT_DAMAGE_REDUCTION_RATIO,
              "Maximum fire-damage fraction absorbed by eight Geothermal Generator Units.",
              0D, 1D).getDouble(DEFAULT_HEAT_DAMAGE_REDUCTION_RATIO), 0D, 1D);
    }

    public static void write(ByteBuf data)
    {
        data.writeLong(toolCapacity);
        data.writeLong(toolChargeRate);
        data.writeLong(toolMiningUsage);
        data.writeLong(toolSilkMiningUsage);
        data.writeLong(toolWeaponUsage);
        data.writeLong(toolHoeUsage);
        data.writeLong(toolShovelUsage);
        data.writeLong(toolShearEntityUsage);
        data.writeLong(toolTeleportUsage);
        data.writeInt(toolMaxTeleportReach);
        data.writeFloat(toolEfficiency);
        data.writeInt(toolBaseDamage);
        data.writeInt(toolVeinMiningMaxBlocks);
        data.writeBoolean(toolExtendedMining);

        data.writeLong(suitCapacity);
        data.writeLong(suitChargeRate);
        data.writeLong(suitPotionTickUsage);
        data.writeLong(suitMagicReduceUsage);
        data.writeLong(suitDamageUsage);
        data.writeFloat(suitMagicDamageReductionRatio);
        data.writeLong(suitVisionEnhancementUsage);
        data.writeLong(suitNutritionalInjectionUsage);
        data.writeInt(suitNutritionalStorage);
        data.writeInt(suitNutritionalTransferRate);
        data.writeInt(suitJetpackStorage);
        data.writeInt(suitJetpackTransferRate);
        data.writeLong(suitInventoryChargeRate);
        data.writeLong(suitGravitationalModulationUsage);
        data.writeLong(suitElytraEnergyUsage);
        data.writeLong(suitSprintBoostUsage);
        data.writeLong(suitBaseJumpUsage);
        data.writeLong(suitHydrostaticRepulsionUsage);
        data.writeLong(suitItemAttractionUsage);

        data.writeLong(modificationStationCapacity);
        data.writeLong(modificationStationUsage);
        data.writeDouble(solarRechargingRate);
        data.writeDouble(geothermalChargingRate);
        data.writeDouble(heatDamageReductionRatio);
    }

    public static void read(ByteBuf data)
    {
        toolCapacity = atLeast(data.readLong(), 1L);
        toolChargeRate = atLeast(data.readLong(), 1L);
        toolMiningUsage = atLeast(data.readLong(), 1L);
        toolSilkMiningUsage = atLeast(data.readLong(), 1L);
        toolWeaponUsage = atLeast(data.readLong(), 1L);
        toolHoeUsage = atLeast(data.readLong(), 1L);
        toolShovelUsage = atLeast(data.readLong(), 1L);
        toolShearEntityUsage = atLeast(data.readLong(), 1L);
        toolTeleportUsage = atLeast(data.readLong(), 1L);
        toolMaxTeleportReach = clamp(data.readInt(), 3, 1_024);
        toolEfficiency = (float)clamp(data.readFloat(), 0.1D, 100D);
        toolBaseDamage = clamp(data.readInt(), 0, 100_000);
        toolVeinMiningMaxBlocks = clamp(data.readInt(), 2, 1_000_000);
        toolExtendedMining = data.readBoolean();

        suitCapacity = atLeast(data.readLong(), 1L);
        suitChargeRate = atLeast(data.readLong(), 1L);
        suitPotionTickUsage = atLeast(data.readLong(), 0L);
        suitMagicReduceUsage = atLeast(data.readLong(), 0L);
        suitDamageUsage = atLeast(data.readLong(), 0L);
        suitMagicDamageReductionRatio = (float)clamp(data.readFloat(), 0D, 1D);
        suitVisionEnhancementUsage = atLeast(data.readLong(), 0L);
        suitNutritionalInjectionUsage = atLeast(data.readLong(), 0L);
        suitNutritionalStorage = atLeast(data.readInt(), 1);
        suitNutritionalTransferRate = atLeast(data.readInt(), 1);
        suitJetpackStorage = atLeast(data.readInt(), 1);
        suitJetpackTransferRate = atLeast(data.readInt(), 1);
        suitInventoryChargeRate = atLeast(data.readLong(), 1L);
        suitGravitationalModulationUsage = atLeast(data.readLong(), 0L);
        suitElytraEnergyUsage = atLeast(data.readLong(), 0L);
        suitSprintBoostUsage = atLeast(data.readLong(), 0L);
        suitBaseJumpUsage = atLeast(data.readLong(), 0L);
        suitHydrostaticRepulsionUsage = atLeast(data.readLong(), 0L);
        suitItemAttractionUsage = atLeast(data.readLong(), 0L);

        modificationStationCapacity = atLeast(data.readLong(), 1L);
        modificationStationUsage = atLeast(data.readLong(), 1L);
        solarRechargingRate = Math.max(0D, data.readDouble());
        geothermalChargingRate = Math.max(0D, data.readDouble());
        heatDamageReductionRatio = clamp(data.readDouble(), 0D, 1D);
    }

    private static long getLong(Configuration config, String category,
          String key, long fallback, long minimum, String comment)
    {
        Property property = config.get(category, key, Long.toString(fallback),
              comment, Property.Type.INTEGER);
        long value;
        try
        {
            value = new BigDecimal(property.getString().trim()).longValueExact();
        }
        catch (ArithmeticException ex)
        {
            value = fallback;
        }
        catch (NumberFormatException ex)
        {
            value = fallback;
        }
        value = Math.max(minimum, value);
        String normalized = Long.toString(value);
        if (!normalized.equals(property.getString()))
        {
            property.setValue(normalized);
        }
        return value;
    }

    private static long atLeast(long value, long minimum)
    {
        return Math.max(minimum, value);
    }

    private static int atLeast(int value, int minimum)
    {
        return Math.max(minimum, value);
    }

    private static int clamp(int value, int minimum, int maximum)
    {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static double clamp(double value, double minimum, double maximum)
    {
        return Double.isNaN(value) ? minimum
              : Math.max(minimum, Math.min(maximum, value));
    }
}
