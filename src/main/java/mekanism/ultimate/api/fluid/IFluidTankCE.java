package mekanism.ultimate.api.fluid;

import javax.annotation.Nullable;
import mekanism.ultimate.api.Action;
import net.minecraftforge.fluids.FluidStack;

/** Long-backed fluid storage with a Forge-compatible fluid identity boundary. */
public interface IFluidTankCE
{
    /** The returned amount is not authoritative; use {@link #getStored()}. */
    @Nullable
    FluidStack getFluidType();

    long getStored();

    long getCapacity();

    long insert(FluidStack fluidType, long amount, Action action);

    long extract(long amount, Action action);
}
