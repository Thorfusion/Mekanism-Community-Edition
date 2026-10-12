package mekanism.ultimate.common.config;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.config.Configuration;

/** Ultimate-owned stable defaults for the nutrition vertical slice. */
public final class UltimateNutritionConfig
{
    public static final int DEFAULT_LIQUIFIER_ENERGY_STORAGE = 40_000;
    public static final int DEFAULT_LIQUIFIER_ENERGY_USAGE = 200;
    public static final int DEFAULT_MB_PER_FOOD = 50;
    public static final float DEFAULT_SATURATION = 0.8F;
    public static final int DEFAULT_CANTEEN_CAPACITY = 64_000;
    public static final int DEFAULT_CANTEEN_TRANSFER_RATE = 128;

    public static boolean nutritionalLiquifierEnabled = true;
    public static int nutritionalLiquifierEnergyStorage =
          DEFAULT_LIQUIFIER_ENERGY_STORAGE;
    public static int nutritionalLiquifierEnergyUsage =
          DEFAULT_LIQUIFIER_ENERGY_USAGE;
    public static int nutritionalPasteMBPerFood = DEFAULT_MB_PER_FOOD;
    public static float nutritionalPasteSaturation = DEFAULT_SATURATION;
    public static int canteenMaxStorage = DEFAULT_CANTEEN_CAPACITY;
    public static int canteenTransferRate = DEFAULT_CANTEEN_TRANSFER_RATE;

    private UltimateNutritionConfig()
    {
    }

    public static void load(Configuration config)
    {
        String category = "nutrition";
        nutritionalLiquifierEnabled = config.get(category,
              "NutritionalLiquifierEnabled", true,
              "Whether crafting recipes for the Nutritional Liquifier and Canteen are enabled.")
              .setRequiresMcRestart(true)
              .getBoolean(true);
        nutritionalLiquifierEnergyStorage = config.get(category,
              "NutritionalLiquifierEnergyStorage",
              DEFAULT_LIQUIFIER_ENERGY_STORAGE,
              "Nutritional Liquifier energy capacity in Joules.",
              1, Integer.MAX_VALUE).getInt(DEFAULT_LIQUIFIER_ENERGY_STORAGE);
        nutritionalLiquifierEnergyUsage = config.get(category,
              "NutritionalLiquifierEnergyUsage",
              DEFAULT_LIQUIFIER_ENERGY_USAGE,
              "Nutritional Liquifier energy usage in Joules per tick.",
              0, Integer.MAX_VALUE).getInt(DEFAULT_LIQUIFIER_ENERGY_USAGE);
        nutritionalPasteMBPerFood = config.get(category,
              "NutritionalPasteMBPerFood", DEFAULT_MB_PER_FOOD,
              "Millibuckets of Nutritional Paste produced and consumed per half-drumstick.",
              1, Integer.MAX_VALUE).getInt(DEFAULT_MB_PER_FOOD);
        nutritionalPasteSaturation = (float)Math.max(0D, Math.min(100D,
              config.get(category, "NutritionalPasteSaturation",
                    DEFAULT_SATURATION,
                    "Saturation modifier applied when Nutritional Paste restores hunger.",
                    0D, 100D).getDouble(DEFAULT_SATURATION)));
        canteenMaxStorage = config.get(category, "CanteenMaxStorage",
              DEFAULT_CANTEEN_CAPACITY,
              "Maximum Nutritional Paste stored by a Canteen in millibuckets.",
              1, Integer.MAX_VALUE).getInt(DEFAULT_CANTEEN_CAPACITY);
        canteenTransferRate = config.get(category, "CanteenTransferRate",
              DEFAULT_CANTEEN_TRANSFER_RATE,
              "Maximum Nutritional Paste transferred to or from a Canteen per operation.",
              1, Integer.MAX_VALUE).getInt(DEFAULT_CANTEEN_TRANSFER_RATE);
    }

    public static void write(ByteBuf data)
    {
        data.writeBoolean(nutritionalLiquifierEnabled);
        data.writeInt(nutritionalLiquifierEnergyStorage);
        data.writeInt(nutritionalLiquifierEnergyUsage);
        data.writeInt(nutritionalPasteMBPerFood);
        data.writeFloat(nutritionalPasteSaturation);
        data.writeInt(canteenMaxStorage);
        data.writeInt(canteenTransferRate);
    }

    public static void read(ByteBuf data)
    {
        nutritionalLiquifierEnabled = data.readBoolean();
        nutritionalLiquifierEnergyStorage = Math.max(1, data.readInt());
        nutritionalLiquifierEnergyUsage = Math.max(0, data.readInt());
        nutritionalPasteMBPerFood = Math.max(1, data.readInt());
        float saturation = data.readFloat();
        nutritionalPasteSaturation = Float.isNaN(saturation) ? 0F
              : Math.max(0F, Math.min(100F, saturation));
        canteenMaxStorage = Math.max(1, data.readInt());
        canteenTransferRate = Math.max(1, data.readInt());
    }
}
