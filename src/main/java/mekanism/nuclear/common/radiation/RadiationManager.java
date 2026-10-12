package mekanism.nuclear.common.radiation;

import mekanism.api.gas.Gas;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.config.NuclearRadiationConfig;
import net.minecraft.world.World;

/** Entry point for persisted environmental radiation in the Nuclear module. */
public final class RadiationManager
{
    public static final RadiationManager INSTANCE = new RadiationManager();
    public static final double BASELINE = 0.000000100D;
    public static final double MIN_MAGNITUDE = 0.000010D;

    private RadiationManager()
    {
    }

    public void release(World world, RadiationPosition position, Gas gas, long amount)
    {
        if (world == null || world.isRemote || gas == null || amount <= 0)
        {
            return;
        }
        double radioactivity = NuclearChemicals.getRadioactivityPerMb(gas.getName());
        if (radioactivity <= 0)
        {
            return;
        }
        getOrCreateData(world).radiate(position, radioactivity * amount,
              world.getTotalWorldTime(), NuclearRadiationConfig.getSourceDecayRate(),
              MIN_MAGNITUDE);
    }

    public double getRadiationLevel(World world, RadiationPosition position)
    {
        if (world == null || world.isRemote
              || !NuclearRadiationConfig.isRadiationEnabled())
        {
            return BASELINE;
        }
        RadiationLevelData data = getData(world);
        return data == null ? BASELINE : data.getRadiationLevel(position,
              world.getTotalWorldTime(),
              NuclearRadiationConfig.getRadiationChunkCheckRadius(),
              NuclearRadiationConfig.getSourceDecayRate(), MIN_MAGNITUDE, BASELINE);
    }

    public double getSourceMagnitude(World world, RadiationPosition position)
    {
        if (world == null || world.isRemote)
        {
            return 0;
        }
        RadiationLevelData data = getData(world);
        return data == null ? 0 : data.getSourceMagnitude(position,
              world.getTotalWorldTime(), NuclearRadiationConfig.getSourceDecayRate(),
              MIN_MAGNITUDE);
    }

    private static RadiationLevelData getData(World world)
    {
        return (RadiationLevelData)world.perWorldStorage.loadData(
              RadiationLevelData.class, RadiationLevelData.DATA_NAME);
    }

    private static RadiationLevelData getOrCreateData(World world)
    {
        RadiationLevelData data = getData(world);
        if (data == null)
        {
            data = new RadiationLevelData(RadiationLevelData.DATA_NAME);
            world.perWorldStorage.setData(RadiationLevelData.DATA_NAME, data);
        }
        return data;
    }
}
