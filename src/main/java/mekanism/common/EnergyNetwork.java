package mekanism.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import mekanism.api.Chunk3D;
import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.mekce;
import mekanism.api.MekanismConfig.mekce_client;
import mekanism.api.energy.EnergyStack;
import mekanism.api.transmitters.DynamicNetwork;
import mekanism.api.transmitters.IGridTransmitter;
import mekanism.common.base.EnergyAcceptorWrapper;
import mekanism.common.multipart.MultipartTransmitter;
import mekanism.common.multipart.PartUniversalCable;
import mekanism.common.util.MekanismUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;

public class EnergyNetwork extends DynamicNetwork<EnergyAcceptorWrapper, EnergyNetwork>
{
	private static final int UNIVERSAL_CABLE_VISUAL_UPDATE_TICKS = 10;
	private static final int SHARE_SAVE_INTERVAL_TICKS = 20;
	private static final Comparator<AcceptorTarget> ACCEPTOR_DEMAND_COMPARATOR = new Comparator<AcceptorTarget>()
	{
		@Override
		public int compare(AcceptorTarget first, AcceptorTarget second)
		{
			return Double.compare(first.demand, second.demand);
		}
	};

	private double lastPowerScale = 0;
	private double joulesTransmitted = 0;
	private double jouleBufferLastTick = 0;
	private int visualUpdateDelay = 0;
	private int shareSaveDelay = 0;

	public double clientEnergyScale = 0;
	public double currentPower = 0;

	public EnergyStack buffer = new EnergyStack(0);

	public EnergyNetwork() {}

	public EnergyNetwork(Collection<EnergyNetwork> networks)
	{
		for(EnergyNetwork net : networks)
		{
			if(net != null)
			{
				currentPower = Math.max(currentPower, net.currentPower);

				if(net.jouleBufferLastTick > jouleBufferLastTick || net.clientEnergyScale > clientEnergyScale)
				{
					clientEnergyScale = net.clientEnergyScale;
					jouleBufferLastTick = net.jouleBufferLastTick;
					joulesTransmitted = net.joulesTransmitted;
					lastPowerScale = net.lastPowerScale;
				}

				buffer.amount += net.buffer.amount;

				adoptTransmittersAndAcceptorsFrom(net);
				net.deregister();
			}
		}

		register();
	}
	
	public static double round(double d)
	{
		return Math.round(d * 10000)/10000;
	}

	@Override
	public void absorbBuffer(IGridTransmitter<EnergyAcceptorWrapper, EnergyNetwork> transmitter)
	{
		EnergyStack energy = (EnergyStack)transmitter.getBuffer();
		buffer.amount += energy.amount;
		energy.amount = 0;
	}

	@Override
	public void clampBuffer()
	{
		if(buffer.amount > getCapacityAsDouble())
		{
			buffer.amount = getCapacityAsDouble();
		}

		if(buffer.amount < 0)
		{
			buffer.amount = 0;
		}
	}

	@Override
	protected void updateMeanCapacity()
	{
        int numCables = transmitters.size();

		if(numCables == 0)
		{
			meanCapacity = 0;
			return;
		}

        double reciprocalSum = 0;
        
        for(IGridTransmitter<EnergyAcceptorWrapper, EnergyNetwork> cable : transmitters)
        {
            reciprocalSum += 1.0/(double)cable.getCapacity();
        }

		meanCapacity = (double)numCables / reciprocalSum;
	}

	@Override
	public synchronized void updateCapacity()
	{
		updateMeanCapacity();
		capacity = (int)Math.min(Integer.MAX_VALUE, getCapacityAsDouble());
	}

	public double getCapacityAsDouble()
	{
		return meanCapacity * transmitters.size();
	}
    
	public double getEnergyNeeded()
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return 0;
		}

		return Math.max(0, getCapacityAsDouble()-buffer.amount);
	}

	public double tickEmit(double energyToSend)
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return 0;
		}

		double sent = doEmit(energyToSend);
		joulesTransmitted = sent;
		
		return sent;
	}

	public double emit(double energyToSend, boolean doEmit)
	{
		double toUse = Math.min(getEnergyNeeded(), energyToSend);
		
		if(doEmit)
		{
			buffer.amount += toUse;
		}
		
		return energyToSend-toUse;
	}

	/**
	 * @return sent
	 */
	public double doEmit(double energyToSend)
	{
		if(energyToSend <= 0)
		{
			return 0;
		}

		List<AcceptorTarget> targets = new ArrayList<>(possibleAcceptors.size());

		for(Entry<Coord4D, EnergyAcceptorWrapper> entry : possibleAcceptors.entrySet())
		{
			EnergyAcceptorWrapper acceptor = entry.getValue();
			EnumSet<ForgeDirection> sides = acceptorDirections.get(entry.getKey());

			if(acceptor == null || sides == null || sides.isEmpty())
			{
				continue;
			}

			for(ForgeDirection side : sides)
			{
				if(!acceptor.canReceiveEnergy(side))
				{
					continue;
				}

				double demand = acceptor.simulateEnergyToAcceptor(side, energyToSend);

				if(demand > 0 && !Double.isNaN(demand))
				{
					targets.add(new AcceptorTarget(acceptor, side, Math.min(demand, energyToSend)));
					break;
				}
			}
		}

		Collections.sort(targets, ACCEPTOR_DEMAND_COMPARATOR);

		double remaining = energyToSend;
		int targetsRemaining = targets.size();

		for(AcceptorTarget target : targets)
		{
			double offer = Math.min(target.demand, remaining / targetsRemaining);
			double accepted = target.acceptor.transferEnergyToAcceptor(target.side, offer);

			if(!Double.isNaN(accepted) && accepted > 0)
			{
				remaining -= Math.min(offer, accepted);
			}

			targetsRemaining--;
		}

		return energyToSend - remaining;
	}

	@Override
	public Set<EnergyAcceptorWrapper> getAcceptors(Object data)
	{
		Set<EnergyAcceptorWrapper> toReturn = new HashSet<>();

		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return toReturn;
		}

		for(Entry<Coord4D, EnergyAcceptorWrapper> entry : possibleAcceptors.entrySet())
		{
			Coord4D coord = entry.getKey();
			EnumSet<ForgeDirection> sides = acceptorDirections.get(coord);

			if(sides == null || sides.isEmpty())
			{
				continue;
			}

			EnergyAcceptorWrapper acceptor = entry.getValue();

			if(acceptor != null)
			{
				for(ForgeDirection side : sides)
				{
					if(acceptor.canReceiveEnergy(side) && acceptor.needsEnergy(side))
					{
						toReturn.add(acceptor);
						break;
					}
				}
			}
		}

		return toReturn;
	}

	public static class EnergyTransferEvent extends Event
	{
		public final EnergyNetwork energyNetwork;

		public final double power;

		public EnergyTransferEvent(EnergyNetwork network, double currentPower)
		{
			energyNetwork = network;
			power = currentPower;
		}
	}

	@Override
	public String toString()
	{
		return "[EnergyNetwork] " + transmitters.size() + " transmitters, " + possibleAcceptors.size() + " acceptors.";
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			updateClientPower();
			return;
		}

		clearJoulesTransmitted();

		if(!mekce.disableUniversalCableServerVisualUpdates)
		{
			double currentPowerScale = getPowerScale();

			if(Math.abs(currentPowerScale-lastPowerScale) > 0.01 || (currentPowerScale != lastPowerScale && (currentPowerScale == 0 || currentPowerScale == 1)))
			{
				needsUpdate = true;
			}

			if(needsUpdate)
			{
				visualUpdateDelay++;

				if(visualUpdateDelay >= UNIVERSAL_CABLE_VISUAL_UPDATE_TICKS)
				{
					MinecraftForge.EVENT_BUS.post(new EnergyTransferEvent(this, currentPowerScale));
					lastPowerScale = currentPowerScale;
					needsUpdate = false;
					visualUpdateDelay = 0;
				}
			}
			else {
				visualUpdateDelay = 0;
			}
		}
		else {
			needsUpdate = false;
			visualUpdateDelay = 0;
		}

		if(buffer.amount > 0)
		{
			buffer.amount -= tickEmit(buffer.amount);
		}

		shareSaveDelay++;

		if(shareSaveDelay >= SHARE_SAVE_INTERVAL_TICKS)
		{
			updateAndSaveShares();
			shareSaveDelay = 0;
		}
	}

	private void updateClientPower()
	{
		if(mekce_client.opaqueTransmitters || mekce_client.opaqueUniversalCable)
		{
			currentPower = 0;
			return;
		}

		double targetPower = mekce.disableUniversalCableServerVisualUpdates ? 1 : clientEnergyScale;

		if(Math.abs(currentPower - targetPower) > 0.01)
		{
			currentPower = (9 * currentPower + targetPower) / 10;
		}
		else {
			currentPower = targetPower;
		}
	}

	@Override
	public void clientTick()
	{
		updateClientPower();
	}

	private void updateAndSaveShares()
	{
		for(IGridTransmitter<EnergyAcceptorWrapper, EnergyNetwork> transmitter : transmitters)
		{
			transmitter.updateShare();
		}

		onSharesUpdated();
	}

	@Override
	protected void onSharesUpdated()
	{
		Set<Chunk3D> dirtyChunks = new HashSet<>();

		for(IGridTransmitter<EnergyAcceptorWrapper, EnergyNetwork> transmitter : transmitters)
		{
			if(transmitter instanceof MultipartTransmitter && ((MultipartTransmitter)transmitter).getPart() instanceof PartUniversalCable)
			{
				PartUniversalCable cable = (PartUniversalCable)((MultipartTransmitter)transmitter).getPart();

				if(cable.flushShareSave() && dirtyChunks.add(transmitter.coord().getChunk3D()))
				{
					MekanismUtils.saveChunk(cable.tile());
				}
			}
		}
	}

	public double getPowerScale()
	{
		double networkCapacity = getCapacityAsDouble();
		return Math.max(jouleBufferLastTick == 0 ? 0 : Math.min(Math.ceil(Math.log10(getPower())*2)/10, 1), networkCapacity == 0 ? 0 : buffer.amount/networkCapacity);
	}

	public void clearJoulesTransmitted()
	{
		jouleBufferLastTick = buffer.amount;
		joulesTransmitted = 0;
	}

	public double getPower()
	{
		return jouleBufferLastTick * 20;
	}

	@Override
	public String getNeededInfo()
	{
		return MekanismUtils.getEnergyDisplay(getEnergyNeeded());
	}

	@Override
	public String getStoredInfo()
	{
		return MekanismUtils.getEnergyDisplay(buffer.amount);
	}

	@Override
	public String getFlowInfo()
	{
		return MekanismUtils.getEnergyDisplay(joulesTransmitted) + "/t";
	}

	private static class AcceptorTarget
	{
		private final EnergyAcceptorWrapper acceptor;
		private final ForgeDirection side;
		private final double demand;

		private AcceptorTarget(EnergyAcceptorWrapper acceptor, ForgeDirection side, double demand)
		{
			this.acceptor = acceptor;
			this.side = side;
			this.demand = demand;
		}
	}
}
