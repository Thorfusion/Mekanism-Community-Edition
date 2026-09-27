package mekanism.ultimate.common.config;

import net.minecraftforge.common.config.Configuration;

/** Ultimate-owned balance values for standalone modern equipment. */
public final class UltimateGearConfig {

    public static final int DEFAULT_ARMORED_FREE_RUNNER_ARMOR = 3;
    public static final float DEFAULT_ARMORED_FREE_RUNNER_TOUGHNESS = 2F;
    public static final float DEFAULT_ARMORED_FREE_RUNNER_KNOCKBACK_RESISTANCE = 0F;

    public static int armoredFreeRunnerArmor = DEFAULT_ARMORED_FREE_RUNNER_ARMOR;
    public static float armoredFreeRunnerToughness = DEFAULT_ARMORED_FREE_RUNNER_TOUGHNESS;
    public static float armoredFreeRunnerKnockbackResistance = DEFAULT_ARMORED_FREE_RUNNER_KNOCKBACK_RESISTANCE;

    private UltimateGearConfig() {
    }

    public static void load(Configuration config) {
        armoredFreeRunnerArmor = config.getInt("armor", "armored_free_runners",
              DEFAULT_ARMORED_FREE_RUNNER_ARMOR, 0, 100,
              "Armor points provided by Armored Free Runners.");
        armoredFreeRunnerToughness = config.getFloat("toughness", "armored_free_runners",
              DEFAULT_ARMORED_FREE_RUNNER_TOUGHNESS, 0F, 100F,
              "Armor toughness provided by Armored Free Runners.");
        armoredFreeRunnerKnockbackResistance = config.getFloat("knockbackResistance", "armored_free_runners",
              DEFAULT_ARMORED_FREE_RUNNER_KNOCKBACK_RESISTANCE, 0F, 1F,
              "Knockback resistance provided by Armored Free Runners.");
    }
}
