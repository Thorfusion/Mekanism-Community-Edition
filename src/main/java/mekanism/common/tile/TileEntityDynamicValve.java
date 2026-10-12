package mekanism.common.tile;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasTankInfoProvider;
import mekanism.api.gas.ITubeConnection;
import mekanism.common.content.tank.DynamicTankChemicalHooks;
import mekanism.common.content.tank.DynamicFluidTank;
import mekanism.common.util.LangUtils;
import mekanism.common.util.PipeUtils;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityDynamicValve extends TileEntityDynamicTank implements IFluidHandler, IGasHandler, IGasTankInfoProvider, ITubeConnection
{
	public DynamicFluidTank fluidTank;

	public TileEntityDynamicValve()
	{
		super("Dynamic Valve");
		fluidTank = new DynamicFluidTank(this);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		return ((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure)) ? new FluidTankInfo[] {fluidTank.getInfo()} : PipeUtils.EMPTY;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return fluidTank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if(resource != null && structure != null && structure.fluidStored != null)
		{
			if(resource.getFluid() == structure.fluidStored.getFluid())
			{
				return fluidTank.drain(resource.amount, doDrain);
			}
		}

		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if(structure != null)
		{
			return fluidTank.drain(maxDrain, doDrain);
		}

		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return ((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure)) && (structure == null || structure.chemicalStored == null);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return ((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure)) && structure != null && structure.fluidStored != null;
	}

	@Override
	public int receiveGas(ForgeDirection side, GasStack stack, boolean doTransfer)
	{
		return DynamicTankChemicalHooks.receiveGas(this, side, stack, doTransfer);
	}

	@Override
	@Deprecated
	public int receiveGas(ForgeDirection side, GasStack stack)
	{
		return receiveGas(side, stack, true);
	}

	@Override
	public GasStack drawGas(ForgeDirection side, int amount, boolean doTransfer)
	{
		return DynamicTankChemicalHooks.drawGas(this, side, amount, doTransfer);
	}

	@Override
	@Deprecated
	public GasStack drawGas(ForgeDirection side, int amount)
	{
		return drawGas(side, amount, true);
	}

	@Override
	public boolean canReceiveGas(ForgeDirection side, Gas type)
	{
		return DynamicTankChemicalHooks.canReceiveGas(this, side, type);
	}

	@Override
	public boolean canDrawGas(ForgeDirection side, Gas type)
	{
		return DynamicTankChemicalHooks.canDrawGas(this, side, type);
	}

	@Override
	public GasTankInfo[] getTankInfo()
	{
		return DynamicTankChemicalHooks.getGasTankInfo(this);
	}

	@Override
	public boolean canTubeConnect(ForgeDirection side)
	{
		return side != null && side != ForgeDirection.UNKNOWN && getTankInfo().length > 0;
	}
	
	@Override
	public String getInventoryName()
	{
		return LangUtils.localize("gui.dynamicTank");
	}
}
