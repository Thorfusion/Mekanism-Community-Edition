package mekanism.ultimate.api.heat;

import mekanism.ultimate.api.Action;

/** Simulation-aware heat boundary for Ultimate systems. */
public interface IHeatHandlerCE
{
    double getTemperature();

    double getHeatCapacity();

    double insertHeat(double amount, Action action);

    double extractHeat(double amount, Action action);
}
