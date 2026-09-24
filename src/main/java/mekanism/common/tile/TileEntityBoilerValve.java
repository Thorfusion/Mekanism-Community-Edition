package mekanism.common.tile;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasCoolantRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.common.base.FluidHandlerWrapper;
import mekanism.common.base.IComparatorSupport;
import mekanism.common.base.IFluidHandlerWrapper;
import mekanism.common.content.boiler.BoilerSteamTank;
import mekanism.common.content.boiler.BoilerTank;
import mekanism.common.content.boiler.BoilerWaterTank;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.tank.SynchronizedTankData.ValveData;
import mekanism.common.integration.computer.IComputerIntegration;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.EmitUtils;
import mekanism.common.util.FluidContainerUtils;
import mekanism.common.util.GasUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.PipeUtils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityBoilerValve extends TileEntityBoilerCasing implements IFluidHandlerWrapper, IGasHandler, IComputerIntegration, IComparatorSupport {

    private static final String[] methods = new String[]{"isFormed", "getSteam", "getWater", "getBoilRate", "getMaxBoilRate", "getTemp",
          "getHeatedCoolant", "getCooledCoolant", "getHeatedCoolantCapacity", "getCooledCoolantCapacity", "getCoolantRate"};
    public BoilerTank waterTank;
    public BoilerTank steamTank;
    private int currentRedstoneLevel;

    public TileEntityBoilerValve() {
        super("BoilerValve");
        waterTank = new BoilerWaterTank(this);
        steamTank = new BoilerSteamTank(this);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            if (structure != null && structure.upperRenderLocation != null && getPos().getY() >= structure.upperRenderLocation.y - 1) {
                if (structure.steamStored != null && structure.steamStored.amount > 0) {
                    EmitUtils.forEachSide(getWorld(), getPos(), EnumSet.allOf(EnumFacing.class), (tile, side) -> {
                        if (!(tile instanceof TileEntityBoilerValve)) {
                            IFluidHandler handler = CapabilityUtils.getCapability(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
                            if (handler != null && PipeUtils.canFill(handler, structure.steamStored)) {
                                structure.steamStored.amount -= handler.fill(structure.steamStored, true);
                                if (structure.steamStored.amount <= 0) {
                                    structure.steamStored = null;
                                }
                            }
                        }
                    });
                }
                GasStack cooledCoolant = structure.cooledCoolantTank.getGas();
                if (cooledCoolant != null) {
                    int emitted = GasUtils.emit(cooledCoolant, this, EnumSet.allOf(EnumFacing.class));
                    structure.cooledCoolantTank.draw(emitted, true);
                    if (emitted > 0) {
                        markValveTransfer();
                    }
                }
                int newRedstoneLevel = getRedstoneLevel();
                if (newRedstoneLevel != currentRedstoneLevel) {
                    world.updateComparatorOutputLevel(pos, getBlockType());
                    currentRedstoneLevel = newRedstoneLevel;
                }
            }
        }
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        if ((!world.isRemote && structure != null) || (world.isRemote && clientHasStructure)) {
            if (structure.upperRenderLocation != null && getPos().getY() >= structure.upperRenderLocation.y - 1) {
                return new FluidTankInfo[]{steamTank.getInfo()};
            }
            return new FluidTankInfo[]{waterTank.getInfo()};
        }
        return PipeUtils.EMPTY;
    }

    @Override
    public FluidTankInfo[] getAllTanks() {
        return new FluidTankInfo[]{steamTank.getInfo(), waterTank.getInfo()};
    }

    @Override
    public int fill(EnumFacing from, @Nonnull FluidStack resource, boolean doFill) {
        return waterTank.fill(resource, doFill);
    }

    @Override
    @Nullable
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return steamTank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(EnumFacing from, @Nonnull FluidStack fluid) {
        if ((!world.isRemote && structure != null) || (world.isRemote && clientHasStructure)) {
            return structure.upperRenderLocation != null && getPos().getY() < structure.upperRenderLocation.y - 1 && fluid.getFluid() == FluidRegistry.WATER;
        }
        return false;
    }

    @Override
    public boolean canDrain(EnumFacing from, @Nullable FluidStack fluid) {
        if ((!world.isRemote && structure != null) || (world.isRemote && clientHasStructure)) {
            return structure.upperRenderLocation != null && getPos().getY() >= structure.upperRenderLocation.y - 1 && FluidContainerUtils.canDrain(structure.steamStored, fluid);
        }
        return false;
    }

    @Override
    public String[] getMethods() {
        return methods;
    }

    @Override
    public Object[] invoke(int method, Object[] arguments) throws NoSuchMethodException {
        if (method == 0) {
            return new Object[]{structure != null};
        } else {
            if (structure == null) {
                return new Object[]{"Unformed"};
            }
            switch (method) {
                case 1:
                    return new Object[]{structure.steamStored != null ? structure.steamStored.amount : 0};
                case 2:
                    return new Object[]{structure.waterStored != null ? structure.waterStored.amount : 0};
                case 3:
                    return new Object[]{structure.lastBoilRate};
                case 4:
                    return new Object[]{structure.lastMaxBoil};
                case 5:
                    return new Object[]{structure.temperature};
                case 6:
                    return new Object[]{structure.superheatedCoolantTank.getStored()};
                case 7:
                    return new Object[]{structure.cooledCoolantTank.getStored()};
                case 8:
                    return new Object[]{structure.superheatedCoolantTank.getMaxGas()};
                case 9:
                    return new Object[]{structure.cooledCoolantTank.getMaxGas()};
                case 10:
                    return new Object[]{structure.lastCoolantRate};
            }
        }
        throw new NoSuchMethodException();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing side) {
        if ((!world.isRemote && structure != null) || (world.isRemote && clientHasStructure)) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == Capabilities.GAS_HANDLER_CAPABILITY) {
                return true;
            }
        }
        return super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing side) {
        if ((!world.isRemote && structure != null) || (world.isRemote && clientHasStructure)) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FluidHandlerWrapper(this, side));
            } else if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
                return Capabilities.GAS_HANDLER_CAPABILITY.cast(this);
            }
        }
        return super.getCapability(capability, side);
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(waterTank.getFluidAmount(), waterTank.getCapacity());
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
        if (stack == null || !canReceiveGas(side, stack.getGas())) {
            return 0;
        }
        int received = structure.superheatedCoolantTank.receive(stack, doTransfer);
        if (doTransfer && received > 0) {
            markValveTransfer();
        }
        return received;
    }

    @Override
    public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer) {
        GasStack drawn = isUpperValve() ? structure.cooledCoolantTank.draw(amount, doTransfer) : null;
        if (doTransfer && drawn != null && drawn.amount > 0) {
            markValveTransfer();
        }
        return drawn;
    }

    @Override
    public boolean canReceiveGas(EnumFacing side, Gas type) {
        return isLowerValve() && GasCoolantRegistry.isHeatedCoolant(type)
              && structure.superheatedCoolantTank.canReceive(type);
    }

    @Override
    public boolean canDrawGas(EnumFacing side, Gas type) {
        return isUpperValve() && structure.cooledCoolantTank.canDraw(type);
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo() {
        if (isUpperValve()) {
            return new GasTankInfo[]{structure.cooledCoolantTank};
        } else if (isLowerValve()) {
            return new GasTankInfo[]{structure.superheatedCoolantTank};
        }
        return IGasHandler.NONE;
    }

    private boolean isUpperValve() {
        return structure != null && structure.upperRenderLocation != null && getPos().getY() >= structure.upperRenderLocation.y - 1;
    }

    private boolean isLowerValve() {
        return structure != null && structure.upperRenderLocation != null && getPos().getY() < structure.upperRenderLocation.y - 1;
    }

    private void markValveTransfer() {
        Coord4D position = Coord4D.get(this);
        for (ValveData data : structure.valves) {
            if (position.equals(data.location)) {
                data.onTransfer();
                return;
            }
        }
    }
}
