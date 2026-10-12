package mekanism.common.tile;

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasCoolantRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.GasTransmission;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasTankInfoProvider;
import mekanism.api.gas.ITubeConnection;
import mekanism.common.content.boiler.BoilerSteamTank;
import mekanism.common.content.boiler.BoilerTank;
import mekanism.common.content.boiler.BoilerWaterTank;
import mekanism.common.content.tank.SynchronizedTankData.ValveData;
import mekanism.common.integration.IComputerIntegration;
import mekanism.common.util.PipeUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityBoilerValve extends TileEntityBoilerCasing implements IFluidHandler, IGasHandler, IGasTankInfoProvider, ITubeConnection, IComputerIntegration
{
	private static final String[] METHODS = new String[] {
			"isFormed", "getSteam", "getWater", "getBoilRate",
			"getMaxBoilRate", "getTemp", "getHeatedCoolant",
			"getCooledCoolant", "getHeatedCoolantCapacity",
			"getCooledCoolantCapacity", "getCoolantRate"
	};

	public BoilerTank waterTank;
	public BoilerTank steamTank;

	public TileEntityBoilerValve()
	{
		super("BoilerValve");
		
		waterTank = new BoilerWaterTank(this);
		steamTank = new BoilerSteamTank(this);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if(!worldObj.isRemote)
		{
			if(structure != null && structure.upperRenderLocation != null && yCoord >= structure.upperRenderLocation.yCoord-1)
			{
				if(structure.steamStored != null && structure.steamStored.amount > 0)
				{
					for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
					{
						TileEntity tile = Coord4D.get(this).getFromSide(side).getTileEntity(worldObj);
						
						if(tile instanceof IFluidHandler && !(tile instanceof TileEntityBoilerValve))
						{
							if(((IFluidHandler)tile).canFill(side.getOpposite(), structure.steamStored.getFluid()))
							{
								structure.steamStored.amount -= ((IFluidHandler)tile).fill(side.getOpposite(), structure.steamStored, true);
								
								if(structure.steamStored.amount <= 0)
								{
									structure.steamStored = null;
									break;
								}
							}
						}
					}
				}

				GasStack cooled = structure.cooledCoolantTank.getGas();
				if(cooled != null)
				{
					int emitted = GasTransmission.emit(cooled, this,
							Arrays.asList(ForgeDirection.VALID_DIRECTIONS));
					structure.cooledCoolantTank.draw(emitted, true);
					if(emitted > 0)
					{
						markValveTransfer();
					}
				}
			}
		}
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from)
	{
		if((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure))
		{
			if(structure.upperRenderLocation != null && yCoord >= structure.upperRenderLocation.yCoord-1)
			{
				return new FluidTankInfo[] {steamTank.getInfo()};
			}
			else {
				return new FluidTankInfo[] {waterTank.getInfo()};
			}
		}
		
		return PipeUtils.EMPTY;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(structure != null && structure.upperRenderLocation != null && yCoord < structure.upperRenderLocation.yCoord-1)
		{
			return waterTank.fill(resource, doFill);
		}
		
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
	{
		if(structure != null && structure.upperRenderLocation != null && yCoord >= structure.upperRenderLocation.yCoord-1)
		{
			if(structure.steamStored != null)
			{
				if(resource.getFluid() == structure.steamStored.getFluid())
				{
					return steamTank.drain(resource.amount, doDrain);
				}
			}
		}

		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		if(structure != null && structure.upperRenderLocation != null && yCoord >= structure.upperRenderLocation.yCoord-1)
		{
			return steamTank.drain(maxDrain, doDrain);
		}

		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		if((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure))
		{
			return structure.upperRenderLocation != null && yCoord < structure.upperRenderLocation.yCoord-1;
		}
		
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		if((!worldObj.isRemote && structure != null) || (worldObj.isRemote && clientHasStructure))
		{
			return structure.upperRenderLocation != null && yCoord >= structure.upperRenderLocation.yCoord-1;
		}
		
		return false;
	}

	@Override
	public int receiveGas(ForgeDirection side, GasStack stack, boolean doTransfer)
	{
		if(stack == null || stack.getGas() == null || !canReceiveGas(side, stack.getGas()))
		{
			return 0;
		}
		int received = structure.superheatedCoolantTank.receive(stack, doTransfer);
		if(doTransfer && received > 0)
		{
			markValveTransfer();
		}
		return received;
	}

	@Deprecated
	@Override
	public int receiveGas(ForgeDirection side, GasStack stack)
	{
		return receiveGas(side, stack, true);
	}

	@Nullable
	@Override
	public GasStack drawGas(ForgeDirection side, int amount, boolean doTransfer)
	{
		GasStack drawn = isUpperValve()
				? structure.cooledCoolantTank.draw(amount, doTransfer) : null;
		if(doTransfer && drawn != null && drawn.amount > 0)
		{
			markValveTransfer();
		}
		return drawn;
	}

	@Deprecated
	@Nullable
	@Override
	public GasStack drawGas(ForgeDirection side, int amount)
	{
		return drawGas(side, amount, true);
	}

	@Override
	public boolean canReceiveGas(ForgeDirection side, Gas type)
	{
		return isLowerValve() && GasCoolantRegistry.isHeatedCoolant(type)
				&& structure.superheatedCoolantTank.canReceive(type);
	}

	@Override
	public boolean canDrawGas(ForgeDirection side, Gas type)
	{
		return isUpperValve() && structure.cooledCoolantTank.canDraw(type);
	}

	@Nonnull
	@Override
	public GasTankInfo[] getTankInfo()
	{
		if(isUpperValve())
		{
			return new GasTankInfo[] {structure.cooledCoolantTank};
		}
		if(isLowerValve())
		{
			return new GasTankInfo[] {structure.superheatedCoolantTank};
		}
		return IGasTankInfoProvider.NONE;
	}

	@Override
	public boolean canTubeConnect(ForgeDirection side)
	{
		return isUpperValve() || isLowerValve();
	}

	@Override
	public String[] getMethods()
	{
		return METHODS;
	}

	@Override
	public Object[] invoke(int method, Object[] arguments) throws Exception
	{
		if(method == 0)
		{
			return new Object[] {structure != null};
		}
		if(structure == null)
		{
			return new Object[] {"Unformed"};
		}
		switch(method)
		{
			case 1:
				return new Object[] {structure.steamStored == null ? 0 : structure.steamStored.amount};
			case 2:
				return new Object[] {structure.waterStored == null ? 0 : structure.waterStored.amount};
			case 3:
				return new Object[] {structure.lastBoilRate};
			case 4:
				return new Object[] {structure.lastMaxBoil};
			case 5:
				return new Object[] {structure.temperature};
			case 6:
				return new Object[] {structure.superheatedCoolantTank.getStored()};
			case 7:
				return new Object[] {structure.cooledCoolantTank.getStored()};
			case 8:
				return new Object[] {structure.superheatedCoolantTank.getMaxGas()};
			case 9:
				return new Object[] {structure.cooledCoolantTank.getMaxGas()};
			case 10:
				return new Object[] {structure.lastCoolantRate};
			default:
				throw new NoSuchMethodException();
		}
	}

	private boolean isUpperValve()
	{
		return structure != null && structure.upperRenderLocation != null
				&& yCoord >= structure.upperRenderLocation.yCoord-1;
	}

	private boolean isLowerValve()
	{
		return structure != null && structure.upperRenderLocation != null
				&& yCoord < structure.upperRenderLocation.yCoord-1;
	}

	private void markValveTransfer()
	{
		Coord4D position = Coord4D.get(this);
		for(ValveData data : structure.valves)
		{
			if(position.equals(data.location))
			{
				data.onTransfer();
				return;
			}
		}
	}
}
