package mekanism.ultimate.common;

import mekanism.api.MekanismConfig.ultimate;
import mekanism.api.MekanismConfig.usage;
import mekanism.common.Tier.FactoryTier;

public final class UltimateConfig
{
    public static boolean enableSawmillFactoryRecipes = true;
    public static boolean allowDimensionalStabilizerChunkloading = true;
    public static boolean enableDimensionalStabilizerRecipe = true;
    public static double dimensionalStabilizerEnergyPerChunk = 5000D;
    public static int dimensionalStabilizerEnergyStorageTicks = 400;

    private UltimateConfig()
    {
    }

    public static double getFactoryUsage()
    {
        return usage.factoryUsage * ultimate.factoryUsageMultiplier;
    }

    public static double getFactoryMaxEnergy()
    {
        return getFactoryUsage() * FactoryTier.ULTIMATE.processes * ultimate.factoryEnergyStorageTicks;
    }

    public static double getDimensionalStabilizerMaxEnergy()
    {
        return Math.max(1D, dimensionalStabilizerEnergyPerChunk)
              * 25D * Math.max(1, dimensionalStabilizerEnergyStorageTicks);
    }
}
