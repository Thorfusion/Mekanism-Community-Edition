package mekanism.ultimate.api.fluid;

import javax.annotation.Nullable;
import mekanism.ultimate.api.Action;
import net.minecraftforge.fluids.FluidStack;

/**
 * Long-backed fluid storage with a Forge-compatible fluid identity boundary.
 *
 * <p>The amount on the value returned by {@link #getFluidType()} is not the
 * authoritative stored amount. Use {@link #getStored()} for quantities.</p>
 */
public interface IFluidTankCE {

    @Nullable
    FluidStack getFluidType();

    long getStored();

    long getCapacity();

    /**
     * @return the non-negative amount accepted
     */
    long insert(FluidStack fluidType, long amount, Action action);

    /**
     * @return the non-negative amount extracted
     */
    long extract(long amount, Action action);
}
