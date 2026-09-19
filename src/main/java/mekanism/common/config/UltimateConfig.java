package mekanism.common.config;

import mekanism.common.config.options.BooleanOption;
import mekanism.common.config.options.DoubleOption;
import mekanism.common.config.options.IntOption;

public class UltimateConfig extends BaseConfig {

    public final BooleanOption factoryEnabled = new BooleanOption(this, "ultimate", "FactoryEnabled", true,
          "Whether Ultimate Factories are enabled.").setRequiresGameRestart(true);

    public final BooleanOption allowTierInstallerUpgrade = new BooleanOption(this, "ultimate", "AllowTierInstallerUpgrade", true,
          "Whether an Ultimate Tier Installer can upgrade an Elite Factory.");

    public final DoubleOption factoryUsageMultiplier = new DoubleOption(this, "ultimate", "FactoryUsageMultiplier", 1D,
          "Multiplier applied to the base machine energy usage for Ultimate Factories.", 0D, Double.MAX_VALUE);

    public final IntOption factoryEnergyStorageTicks = new IntOption(this, "ultimate", "FactoryEnergyStorageTicks", 400,
          "Number of operation ticks worth of energy stored per Ultimate Factory process.", 1, Integer.MAX_VALUE);

    public final BooleanOption enableFactoryRecipes = new BooleanOption(this, "ultimate", "EnableFactoryRecipes", true,
          "Whether crafting recipes for Ultimate Factories are enabled.").setRequiresGameRestart(true);
}
