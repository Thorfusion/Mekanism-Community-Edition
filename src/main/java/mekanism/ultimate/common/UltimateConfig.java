package mekanism.ultimate.common;

import mekanism.api.MekanismConfig.ultimate;
import mekanism.api.MekanismConfig.usage;
import mekanism.common.Tier.FactoryTier;

public final class UltimateConfig
{
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
}
