package mekanism.ultimate.api.heat;

import mekanism.ultimate.api.Action;

/**
 * Simulation-aware heat boundary for Ultimate systems.
 *
 * <p>Amounts are non-negative heat quantities. Implementations must reject NaN,
 * infinity, and negative inputs.</p>
 */
public interface IHeatHandlerCE {

    double getTemperature();

    double getHeatCapacity();

    double insertHeat(double amount, Action action);

    double extractHeat(double amount, Action action);
}
