package mekanism.ultimate.common.config;

import net.minecraftforge.common.config.Configuration;

/** Ultimate-owned stable defaults for the nutrition vertical slice. */
public final class UltimateNutritionConfig {

    public static final int DEFAULT_LIQUIFIER_ENERGY_STORAGE = 40_000;
    public static final int DEFAULT_LIQUIFIER_ENERGY_USAGE = 200;
    public static final int DEFAULT_MB_PER_FOOD = 50;
    public static final float DEFAULT_SATURATION = 0.8F;
    public static final int DEFAULT_CANTEEN_CAPACITY = 64_000;
    public static final int DEFAULT_CANTEEN_TRANSFER_RATE = 128;

    public static boolean nutritionalLiquifierEnabled = true;
    public static int nutritionalLiquifierEnergyStorage = DEFAULT_LIQUIFIER_ENERGY_STORAGE;
    public static int nutritionalLiquifierEnergyUsage = DEFAULT_LIQUIFIER_ENERGY_USAGE;
    public static int nutritionalPasteMBPerFood = DEFAULT_MB_PER_FOOD;
    public static float nutritionalPasteSaturation = DEFAULT_SATURATION;
    public static int canteenMaxStorage = DEFAULT_CANTEEN_CAPACITY;
    public static int canteenTransferRate = DEFAULT_CANTEEN_TRANSFER_RATE;

    private UltimateNutritionConfig() {
    }

    public static void load(Configuration config) {
        String category = "nutrition";
        nutritionalLiquifierEnabled = config.getBoolean("NutritionalLiquifierEnabled", category, true,
              "Whether crafting recipes for the Nutritional Liquifier and Canteen are enabled.");
        nutritionalLiquifierEnergyStorage = config.getInt("NutritionalLiquifierEnergyStorage", category,
              DEFAULT_LIQUIFIER_ENERGY_STORAGE, 1, Integer.MAX_VALUE,
              "Nutritional Liquifier energy capacity in Joules.");
        nutritionalLiquifierEnergyUsage = config.getInt("NutritionalLiquifierEnergyUsage", category,
              DEFAULT_LIQUIFIER_ENERGY_USAGE, 0, Integer.MAX_VALUE,
              "Nutritional Liquifier energy usage in Joules per tick.");
        nutritionalPasteMBPerFood = config.getInt("NutritionalPasteMBPerFood", category,
              DEFAULT_MB_PER_FOOD, 1, Integer.MAX_VALUE,
              "Millibuckets of Nutritional Paste produced and consumed per half-drumstick.");
        nutritionalPasteSaturation = config.getFloat("NutritionalPasteSaturation", category,
              DEFAULT_SATURATION, 0F, 100F,
              "Saturation modifier applied when Nutritional Paste restores hunger.");
        canteenMaxStorage = config.getInt("CanteenMaxStorage", category,
              DEFAULT_CANTEEN_CAPACITY, 1, Integer.MAX_VALUE,
              "Maximum Nutritional Paste stored by a Canteen in millibuckets.");
        canteenTransferRate = config.getInt("CanteenTransferRate", category,
              DEFAULT_CANTEEN_TRANSFER_RATE, 1, Integer.MAX_VALUE,
              "Maximum Nutritional Paste transferred to or from a Canteen per operation.");
    }
}
