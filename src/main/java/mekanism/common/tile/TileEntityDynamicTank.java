package mekanism.common.tile;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

import mekanism.api.Coord4D;
import mekanism.api.TileNetworkList;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.base.IFluidContainerManager;
import mekanism.common.block.BlockBasic;
import mekanism.common.content.tank.SynchronizedTankData;
import mekanism.common.content.tank.SynchronizedTankData.ValveData;
import mekanism.common.content.tank.DynamicTankChemicalHooks;
import mekanism.common.content.tank.DynamicTankChemicalStack;
import mekanism.common.content.tank.TankCache;
import mekanism.common.content.tank.TankUpdateProtocol;
import mekanism.common.integration.computer.IComputerIntegration;
import mekanism.common.multiblock.MultiblockManager;
import mekanism.common.util.FluidContainerUtils;
import mekanism.common.util.FluidContainerUtils.ContainerEditMode;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.TileUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityDynamicTank extends TileEntityMultiblock<SynchronizedTankData> implements IComputerIntegration, IFluidContainerManager {

    protected static final int[] SLOTS = {0, 1};

    public static final String[] methods = new String[]{"getAmount", "getCapacity", "getLiquidType"};

    /**
     * A client-sided set of valves on this tank's structure that are currently active, used on the client for rendering fluids.
     */
    public Set<ValveData> valveViewing = new HashSet<>();

    /**
     * The capacity this tank has on the client-side.
     */
    public int clientCapacity;

    public long clientChemicalCapacity;

    public float prevScale;

    public TileEntityDynamicTank() {
        super("DynamicTank");
    }

    public TileEntityDynamicTank(String name) {
        super(name);
        inventory = NonNullList.withSize(SLOTS.length, ItemStack.EMPTY);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            if (clientHasStructure && isRendering) {
                if (structure != null) {
                    float targetScale = getActiveScale();
                    if (Math.abs(prevScale - targetScale) > 0.01) {
                        prevScale = (9 * prevScale + targetScale) / 10;
                    }
                }
            } else {
                for (ValveData data : valveViewing) {
                    TileEntityDynamicTank tileEntity = (TileEntityDynamicTank) data.location.getTileEntity(world);
                    if (tileEntity != null) {
                        tileEntity.clientHasStructure = false;
                    }
                }
                valveViewing.clear();
            }
        } else if (structure != null) {
            if (structure.fluidStored != null && structure.fluidStored.amount <= 0) {
                structure.fluidStored = null;
                markDirty();
            }
            if (structure.chemicalStored != null && structure.chemicalStored.amount <= 0) {
                structure.chemicalStored = null;
                markDirty();
            }
            if (structure.fluidStored != null && structure.chemicalStored != null) {
                structure.chemicalStored = null;
                markDirty();
            }
            if (isRendering) {
                boolean needsValveUpdate = false;
                for (ValveData data : structure.valves) {
                    if (data.activeTicks > 0) {
                        data.activeTicks--;
                    }
                    if (data.activeTicks > 0 != data.prevActive) {
                        needsValveUpdate = true;
                    }
                    data.prevActive = data.activeTicks > 0;
                }
                if (needsValveUpdate || structure.needsRenderUpdate()) {
                    sendPacketToRenderer();
                }
                structure.prevFluid = structure.fluidStored != null ? structure.fluidStored.copy() : null;
                structure.prevChemical = structure.chemicalStored != null ? structure.chemicalStored.copy() : null;
                manageInventory();
            }
        }
    }

    public void manageInventory() {
        if (structure.chemicalStored != null || !FluidContainerUtils.isFluidContainer(structure.inventory.get(0))) {
            if (DynamicTankChemicalHooks.manageInventory(this, structure)) {
                Mekanism.packetHandler.sendUpdatePacket(this);
            }
            if (structure.chemicalStored != null) {
                return;
            }
        }
        int needed = (structure.volume * TankUpdateProtocol.FLUID_PER_TANK) - (structure.fluidStored != null ? structure.fluidStored.amount : 0);
        if (FluidContainerUtils.isFluidContainer(structure.inventory.get(0))) {
            structure.fluidStored = FluidContainerUtils.handleContainerItem(this, structure.inventory, structure.editMode, structure.fluidStored, needed, 0, 1, null);
            Mekanism.packetHandler.sendUpdatePacket(this);
        }
    }

    @Override
    public boolean onActivate(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (!player.isSneaking() && structure != null) {
            if (!BlockBasic.manageInventory(player, this, hand, stack)
                  && !DynamicTankChemicalHooks.manageHeldItem(player, this, hand, stack)) {
                Mekanism.packetHandler.sendUpdatePacket(this);
                player.openGui(Mekanism.instance, 18, world, getPos().getX(), getPos().getY(), getPos().getZ());
            } else {
                player.inventory.markDirty();
                sendPacketToRenderer();
            }
            return true;
        }
        return false;
    }

    @Override
    protected SynchronizedTankData getNewStructure() {
        return new SynchronizedTankData();
    }

    @Override
    public TankCache getNewCache() {
        return new TankCache();
    }

    @Override
    protected TankUpdateProtocol getProtocol() {
        return new TankUpdateProtocol(this);
    }

    @Override
    public MultiblockManager<SynchronizedTankData> getManager() {
        return Mekanism.tankManager;
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        if (structure != null) {
            data.add(structure.volume * TankUpdateProtocol.FLUID_PER_TANK);
            data.add(DynamicTankChemicalHooks.getCapacity(structure.volume));
            data.add(structure.editMode.ordinal());
            TileUtils.addFluidStack(data, structure.fluidStored);
            data.add(structure.chemicalStored == null ? "" : structure.chemicalStored.kind);
            data.add(structure.chemicalStored == null ? "" : structure.chemicalStored.registryName);
            data.add(structure.chemicalStored == null ? 0L : structure.chemicalStored.amount);

            if (isRendering) {
                Set<ValveData> toSend = new HashSet<>();

                for (ValveData valveData : structure.valves) {
                    if (valveData.activeTicks > 0) {
                        toSend.add(valveData);
                    }
                }
                data.add(toSend.size());
                for (ValveData valveData : toSend) {
                    valveData.location.write(data);
                    data.add(valveData.side);
                }
            }
        }
        return data;
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            if (clientHasStructure) {
                clientCapacity = dataStream.readInt();
                clientChemicalCapacity = dataStream.readLong();
                int mode = dataStream.readInt();
                structure.editMode = mode >= 0 && mode < ContainerEditMode.values().length
                      ? ContainerEditMode.values()[mode] : ContainerEditMode.BOTH;
                structure.fluidStored = TileUtils.readFluidStack(dataStream);
                String chemicalKind = PacketHandler.readString(dataStream);
                String chemicalName = PacketHandler.readString(dataStream);
                long chemicalAmount = dataStream.readLong();
                structure.chemicalStored = structure.fluidStored == null
                      && DynamicTankChemicalStack.isValidId(chemicalKind)
                      && DynamicTankChemicalStack.isValidId(chemicalName) && chemicalAmount > 0
                      ? new DynamicTankChemicalStack(chemicalKind, chemicalName,
                            Math.min(chemicalAmount, clientChemicalCapacity)) : null;

                if (isRendering) {
                    int size = dataStream.readInt();
                    valveViewing.clear();
                    for (int i = 0; i < size; i++) {
                        ValveData data = new ValveData();
                        data.location = Coord4D.read(dataStream);
                        data.side = EnumFacing.byIndex(dataStream.readInt());
                        valveViewing.add(data);
                        TileEntityDynamicTank tileEntity = (TileEntityDynamicTank) data.location.getTileEntity(world);
                        if (tileEntity != null) {
                            tileEntity.clientHasStructure = true;
                        }
                    }
                }
            }
        }
    }

    public int getScaledFluidLevel(long i) {
        if (structure == null) {
            return 0;
        }
        if (structure.chemicalStored != null) {
            return clientChemicalCapacity <= 0 ? 0
                  : (int) (structure.chemicalStored.amount * i / clientChemicalCapacity);
        }
        return clientCapacity <= 0 || structure.fluidStored == null ? 0
              : (int) (structure.fluidStored.amount * i / clientCapacity);
    }

    public float getActiveScale() {
        if (structure == null) {
            return 0;
        }
        if (structure.chemicalStored != null) {
            long capacity = world != null && world.isRemote ? clientChemicalCapacity
                  : DynamicTankChemicalHooks.getCapacity(structure.volume);
            return capacity <= 0 ? 0 : (float) (structure.chemicalStored.amount / (double) capacity);
        }
        int capacity = world != null && world.isRemote ? clientCapacity
              : structure.volume * TankUpdateProtocol.FLUID_PER_TANK;
        return capacity <= 0 || structure.fluidStored == null ? 0
              : (float) structure.fluidStored.amount / capacity;
    }

    @Override
    public ContainerEditMode getContainerEditMode() {
        if (structure != null) {
            return structure.editMode;
        }
        return ContainerEditMode.BOTH;
    }

    @Override
    public void setContainerEditMode(ContainerEditMode mode) {
        if (structure == null) {
            return;
        }
        structure.editMode = mode;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return InventoryUtils.EMPTY;
    }

    @Override
    public boolean isCapabilityDisabled(@Nonnull Capability<?> capability, EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.isCapabilityDisabled(capability, side);
    }

    @Override
    public String[] getMethods() {
        return methods;
    }

    @Override
    public Object[] invoke(int method, Object[] args) throws NoSuchMethodException {
        switch (method) {
            case 0:
                return new Object[]{structure != null ? structure.chemicalStored != null
                      ? structure.chemicalStored.amount
                      : structure.fluidStored != null ? structure.fluidStored.amount : 0 : 0};
            case 1:
                return new Object[]{structure != null ? structure.volume : 0};
            case 2:
                return new Object[]{structure != null ? structure.chemicalStored != null
                      ? DynamicTankChemicalHooks.getDisplayName(structure.chemicalStored)
                      : structure.fluidStored != null ? structure.fluidStored.getLocalizedName() : null : null};
            default:
                throw new NoSuchMethodException();
        }
    }
}
