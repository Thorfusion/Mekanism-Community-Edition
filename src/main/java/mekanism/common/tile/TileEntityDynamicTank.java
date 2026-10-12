package mekanism.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mekanism.api.Coord4D;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.base.IFluidContainerManager;
import mekanism.common.block.BlockBasic;
import mekanism.common.content.tank.DynamicTankChemicalHooks;
import mekanism.common.content.tank.DynamicTankChemicalStack;
import mekanism.common.content.tank.SynchronizedTankData;
import mekanism.common.content.tank.SynchronizedTankData.ValveData;
import mekanism.common.content.tank.TankCache;
import mekanism.common.content.tank.TankUpdateProtocol;
import mekanism.common.multiblock.MultiblockManager;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.FluidContainerUtils;
import mekanism.common.util.FluidContainerUtils.ContainerEditMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class TileEntityDynamicTank extends TileEntityMultiblock<SynchronizedTankData> implements IFluidContainerManager
{
	/** A client-sided set of valves on this tank's structure that are currently active, used on the client for rendering fluids. */
	public Set<ValveData> valveViewing = new HashSet<ValveData>();

	/** The capacity this tank has on the client-side. */
	public int clientCapacity;

	/** Chemical capacity remains long-backed on the client. */
	public long clientChemicalCapacity;

	public float prevScale;

	public TileEntityDynamicTank()
	{
		super("DynamicTank");
	}

	public TileEntityDynamicTank(String name)
	{
		super(name);
		inventory = new ItemStack[2];
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if(worldObj.isRemote)
		{
			if(structure != null && clientHasStructure && isRendering)
			{
				float targetScale = getActiveScale();
				
				if(Math.abs(prevScale - targetScale) > 0.01)
				{
					prevScale = (9*prevScale + targetScale)/10;
				}
			}

			if(!clientHasStructure || !isRendering)
			{
				for(ValveData data : valveViewing)
				{
					TileEntityDynamicTank tileEntity = (TileEntityDynamicTank)data.location.getTileEntity(worldObj);

					if(tileEntity != null)
					{
						tileEntity.clientHasStructure = false;
					}
				}

				valveViewing.clear();
			}
		}

		if(!worldObj.isRemote)
		{
			if(structure != null)
			{
				if(structure.fluidStored != null && structure.fluidStored.amount <= 0)
				{
					structure.fluidStored = null;
					markDirty();
				}

				if(structure.chemicalStored != null && structure.chemicalStored.amount <= 0)
				{
					structure.chemicalStored = null;
					markDirty();
				}

				if(structure.fluidStored != null && structure.chemicalStored != null)
				{
					structure.chemicalStored = null;
					markDirty();
				}
				
				if(isRendering)
				{
					boolean needsValveUpdate = false;
					
					for(ValveData data : structure.valves)
					{
						if(data.activeTicks > 0)
						{
							data.activeTicks--;
						}
						
						if(data.activeTicks > 0 != data.prevActive)
						{
							needsValveUpdate = true;
						}
						
						data.prevActive = data.activeTicks > 0;
					}
					
					if(needsValveUpdate || structure.needsRenderUpdate())
					{
						sendPacketToRenderer();
					}
					
					structure.prevFluid = structure.fluidStored != null ? structure.fluidStored.copy() : null;
					structure.prevChemical = structure.chemicalStored != null ? structure.chemicalStored.copy() : null;
					
					manageInventory();
				}
			}
		}
	}

	public void manageInventory()
	{
		if(structure.chemicalStored != null || DynamicTankChemicalHooks.isChemicalContainer(structure.inventory[0]))
		{
			if(DynamicTankChemicalHooks.manageInventory(this, structure))
			{
				Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
			}

			if(structure.chemicalStored != null)
			{
				return;
			}
		}

		int needed = (structure.volume*TankUpdateProtocol.FLUID_PER_TANK)-(structure.fluidStored != null ? structure.fluidStored.amount : 0);

		if(structure.inventory[0] != null)
		{
			if(structure.inventory[0].getItem() instanceof IFluidContainerItem)
			{
				if(structure.editMode == ContainerEditMode.FILL && structure.fluidStored != null)
				{
					structure.fluidStored = FluidContainerUtils.handleContainerItemFill(this, structure.inventory, structure.fluidStored, 0, 1);
				}
				else if(structure.editMode == ContainerEditMode.EMPTY)
				{
					structure.fluidStored = FluidContainerUtils.handleContainerItemEmpty(this, structure.inventory, structure.fluidStored, needed, 0, 1, null);
				}
			}
			else if(FluidContainerRegistry.isEmptyContainer(structure.inventory[0]) && (structure.editMode == ContainerEditMode.BOTH || structure.editMode == ContainerEditMode.FILL))
			{
				structure.fluidStored = FluidContainerUtils.handleRegistryItemFill(this, structure.inventory, structure.fluidStored, 0, 1);
				
				Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
			}
			else if(FluidContainerRegistry.isFilledContainer(structure.inventory[0]) && (structure.editMode == ContainerEditMode.BOTH || structure.editMode == ContainerEditMode.EMPTY))
			{
				structure.fluidStored = FluidContainerUtils.handleRegistryItemEmpty(this, structure.inventory, structure.fluidStored, needed, 0, 1, null);
				
				Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
			}
		}
	}
	
	@Override
	public boolean onActivate(EntityPlayer player)
	{
		if(!player.isSneaking() && structure != null)
		{
			ItemStack held = player.getCurrentEquippedItem();
			if(!BlockBasic.manageInventory(player, this) && !DynamicTankChemicalHooks.manageHeldItem(player, this, held))
			{
				Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(this), getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(this)));
				player.openGui(Mekanism.instance, 18, worldObj, xCoord, yCoord, zCoord);
			}
			else {
				player.inventory.markDirty();
				sendPacketToRenderer();
			}

			return true;
		}
		
		return false;
	}
	
	@Override
	protected SynchronizedTankData getNewStructure()
	{
		return new SynchronizedTankData();
	}
	
	@Override
	public TankCache getNewCache()
	{
		return new TankCache();
	}
	
	@Override
	protected TankUpdateProtocol getProtocol()
	{
		return new TankUpdateProtocol(this);
	}
	
	@Override
	public MultiblockManager<SynchronizedTankData> getManager()
	{
		return Mekanism.tankManager;
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		
		if(structure != null)
		{
			data.add(structure.volume*TankUpdateProtocol.FLUID_PER_TANK);
			long chemicalCapacity = DynamicTankChemicalHooks.getCapacity(structure.volume);
			data.add((int)(chemicalCapacity >>> 32));
			data.add((int)chemicalCapacity);
			data.add(structure.editMode.ordinal());
			
			if(structure.fluidStored != null)
			{
				data.add(1);
				data.add(structure.fluidStored.getFluidID());
				data.add(structure.fluidStored.amount);
			}
			else if(structure.chemicalStored != null)
			{
				data.add(2);
				data.add(structure.chemicalStored.kind);
				data.add(structure.chemicalStored.registryName);
				data.add((int)(structure.chemicalStored.amount >>> 32));
				data.add((int)structure.chemicalStored.amount);
			}
			else {
				data.add(0);
			}
			
			if(isRendering)
			{
				Set<ValveData> toSend = new HashSet<ValveData>();

				for(ValveData valveData : structure.valves)
				{
					if(valveData.activeTicks > 0)
					{
						toSend.add(valveData);
					}
				}
				
				data.add(toSend.size());
				
				for(ValveData valveData : toSend)
				{
					valveData.location.write(data);
					data.add(valveData.side.ordinal());
				}
			}
		}

		return data;
	}

	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);
		
		if(worldObj.isRemote)
		{
			if(clientHasStructure)
			{
				clientCapacity = dataStream.readInt();
				clientChemicalCapacity = readLong(dataStream);
				int mode = dataStream.readInt();
				structure.editMode = mode >= 0 && mode < ContainerEditMode.values().length ? ContainerEditMode.values()[mode] : ContainerEditMode.BOTH;
				
				int contentType = dataStream.readInt();
				if(contentType == 1)
				{
					int fluidId = dataStream.readInt();
					int amount = dataStream.readInt();
					structure.fluidStored = FluidRegistry.getFluid(fluidId) != null && amount > 0 ? new FluidStack(FluidRegistry.getFluid(fluidId), Math.min(amount, clientCapacity)) : null;
					structure.chemicalStored = null;
				}
				else if(contentType == 2)
				{
					String kind = PacketHandler.readString(dataStream);
					String name = PacketHandler.readString(dataStream);
					long amount = readLong(dataStream);
					structure.fluidStored = null;
					structure.chemicalStored = DynamicTankChemicalStack.isValidId(kind) && DynamicTankChemicalStack.isValidId(name) && amount > 0 ? new DynamicTankChemicalStack(kind, name, Math.min(amount, clientChemicalCapacity)) : null;
				}
				else {
					structure.fluidStored = null;
					structure.chemicalStored = null;
				}
	
				if(isRendering)
				{
					int size = dataStream.readInt();
					
					valveViewing.clear();
	
					for(int i = 0; i < size; i++)
					{
						ValveData data = new ValveData();
						data.location = Coord4D.read(dataStream);
						data.side = ForgeDirection.getOrientation(dataStream.readInt());
						
						valveViewing.add(data);
	
						TileEntityDynamicTank tileEntity = (TileEntityDynamicTank)data.location.getTileEntity(worldObj);
	
						if(tileEntity != null)
						{
							tileEntity.clientHasStructure = true;
						}
					}
				}
			}
		}
	}

	public int getScaledFluidLevel(long i)
	{
		if(structure == null)
		{
			return 0;
		}

		if(structure.chemicalStored != null)
		{
			return clientChemicalCapacity <= 0 ? 0 : (int)(structure.chemicalStored.amount*i/clientChemicalCapacity);
		}

		return clientCapacity <= 0 || structure.fluidStored == null ? 0 : (int)(structure.fluidStored.amount*i/clientCapacity);
	}

	public float getActiveScale()
	{
		if(structure == null)
		{
			return 0;
		}

		if(structure.chemicalStored != null)
		{
			long capacity = worldObj != null && worldObj.isRemote ? clientChemicalCapacity : DynamicTankChemicalHooks.getCapacity(structure.volume);
			return capacity <= 0 ? 0 : (float)(structure.chemicalStored.amount/(double)capacity);
		}

		int capacity = worldObj != null && worldObj.isRemote ? clientCapacity : structure.volume*TankUpdateProtocol.FLUID_PER_TANK;
		return capacity <= 0 || structure.fluidStored == null ? 0 : (float)structure.fluidStored.amount/capacity;
	}

	private static long readLong(ByteBuf dataStream)
	{
		return ((long)dataStream.readInt() << 32) | (dataStream.readInt() & 0xFFFFFFFFL);
	}

	@Override
	public ContainerEditMode getContainerEditMode() 
	{
		if(structure != null)
		{
			return structure.editMode;
		}
		
		return ContainerEditMode.BOTH;
	}

	@Override
	public void setContainerEditMode(ContainerEditMode mode) 
	{
		if(structure == null)
		{
			return;
		}
		
		structure.editMode = mode;
	}
	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}
}
