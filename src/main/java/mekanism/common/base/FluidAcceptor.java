package mekanism.common.base;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidAcceptor
{
	public int limit;
	public IFluidHandler fluidHandler;


	public FluidAcceptor(IFluidHandler fluidHandler, int limit)
	{
		this.limit = limit;
		this.fluidHandler = fluidHandler;
	}

	public Integer fill(FluidStack resource, boolean doFill)
	{
		FluidStack stack = resource.copy();
		stack.amount= Math.min(stack.amount,limit);
		return fluidHandler.fill(stack,doFill);
	}
}
