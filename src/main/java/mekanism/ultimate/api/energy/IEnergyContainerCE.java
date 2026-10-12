package mekanism.ultimate.api.energy;

import mekanism.ultimate.api.Action;

/** Long-backed energy storage used inside new Ultimate systems. */
public interface IEnergyContainerCE
{
    long getEnergy();

    long getCapacity();

    long insertEnergy(long amount, Action action);

    long extractEnergy(long amount, Action action);
}
