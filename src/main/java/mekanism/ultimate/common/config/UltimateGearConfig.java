package mekanism.ultimate.common.config;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.config.Configuration;

/** Ultimate-owned balance values for standalone modern equipment. */
public final class UltimateGearConfig
{
    public static final int DEFAULT_ARMORED_FREE_RUNNER_ARMOR = 3;
    public static final float DEFAULT_ARMORED_FREE_RUNNER_TOUGHNESS = 2F;
    public static final float DEFAULT_ARMORED_FREE_RUNNER_KNOCKBACK_RESISTANCE =
          0F;

    public static int armoredFreeRunnerArmor =
          DEFAULT_ARMORED_FREE_RUNNER_ARMOR;
    public static float armoredFreeRunnerToughness =
          DEFAULT_ARMORED_FREE_RUNNER_TOUGHNESS;
    public static float armoredFreeRunnerKnockbackResistance =
          DEFAULT_ARMORED_FREE_RUNNER_KNOCKBACK_RESISTANCE;

    private UltimateGearConfig()
    {
    }

    public static void load(Configuration config)
    {
        String category = "armored_free_runners";
        armoredFreeRunnerArmor = config.get(category, "Armor",
              DEFAULT_ARMORED_FREE_RUNNER_ARMOR,
              "Armor points provided by Armored Free Runners.", 0, 100)
              .getInt(DEFAULT_ARMORED_FREE_RUNNER_ARMOR);
        armoredFreeRunnerToughness = (float)config.get(category,
              "Toughness", DEFAULT_ARMORED_FREE_RUNNER_TOUGHNESS,
              "Armor toughness used by the 1.7 damage adapter.", 0D, 100D)
              .getDouble(DEFAULT_ARMORED_FREE_RUNNER_TOUGHNESS);
        armoredFreeRunnerKnockbackResistance = (float)config.get(category,
              "KnockbackResistance",
              DEFAULT_ARMORED_FREE_RUNNER_KNOCKBACK_RESISTANCE,
              "Knockback resistance provided by Armored Free Runners.",
              0D, 1D).getDouble(
                    DEFAULT_ARMORED_FREE_RUNNER_KNOCKBACK_RESISTANCE);
    }

    public static void write(ByteBuf data)
    {
        data.writeInt(armoredFreeRunnerArmor);
        data.writeFloat(armoredFreeRunnerToughness);
        data.writeFloat(armoredFreeRunnerKnockbackResistance);
    }

    public static void read(ByteBuf data)
    {
        armoredFreeRunnerArmor = clamp(data.readInt(), 0, 100);
        armoredFreeRunnerToughness = (float)clamp(data.readFloat(), 0D,
              100D);
        armoredFreeRunnerKnockbackResistance = (float)clamp(
              data.readFloat(), 0D, 1D);
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
