package mekanism.ultimate.api.energy;

import mekanism.ultimate.api.Action;

/**
 * Long-backed energy storage used inside new Ultimate systems.
 */
public interface IEnergyContainerCE {

    long getEnergy();

    long getCapacity();

    /**
     * @return the non-negative amount accepted
     */
    long insertEnergy(long amount, Action action);

    /**
     * @return the non-negative amount extracted
     */
    long extractEnergy(long amount, Action action);
}
