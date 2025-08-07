package mekanism.common.base;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class GasAcceptor
{
	public int limit;
	public IGasHandler gasHandler;


	public GasAcceptor(IGasHandler gasHandler, int limit)
	{
		this.limit = limit;
		this.gasHandler = gasHandler;
	}

	public Integer receiveGas(EnumFacing side, GasStack resource, boolean doFill)
	{
		GasStack stack = resource.copy();
		stack.amount = Math.min(stack.amount,limit);
		return gasHandler.receiveGas(side,stack,doFill);
	}
}
