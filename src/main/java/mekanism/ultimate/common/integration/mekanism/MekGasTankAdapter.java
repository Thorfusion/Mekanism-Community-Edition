package mekanism.ultimate.common.integration.mekanism;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/**
 * Presents a long-backed Ultimate tank through the old Mekanism gas API.
 *
 * <p>Every old API call is bounded by its input {@code int}; tank-info values
 * saturate at {@link Integer#MAX_VALUE}. The wrapped tank remains authoritative.</p>
 */
public final class MekGasTankAdapter implements IGasHandler, IChemicalHandlerCE {

    private final LongChemicalTank tank;
    private final Predicate<EnumFacing> inputSides;
    private final Predicate<EnumFacing> outputSides;
    private final GasTankInfo tankInfo = new LongGasTankInfo();

    public MekGasTankAdapter(LongChemicalTank tank) {
        this(tank, side -> true, side -> true);
    }

    public MekGasTankAdapter(LongChemicalTank tank, Predicate<EnumFacing> inputSides, Predicate<EnumFacing> outputSides) {
        this.tank = Objects.requireNonNull(tank, "tank");
        this.inputSides = Objects.requireNonNull(inputSides, "inputSides");
        this.outputSides = Objects.requireNonNull(outputSides, "outputSides");
    }

    public LongChemicalTank getTank() {
        return tank;
    }

    public void writeToNBT(NBTTagCompound data) {
        tank.writeToNBT(data);
    }

    public void readFromNBT(NBTTagCompound data) {
        readFromNBT(data, GasRegistry::getGas);
    }

    /**
     * Resolver overload used by isolated tests and compatibility registries.
     */
    public void readFromNBT(NBTTagCompound data, Function<String, Gas> gasResolver) {
        Objects.requireNonNull(gasResolver, "gasResolver");
        tank.readFromNBT(data, (kind, name) -> {
            Gas gas = kind == ChemicalKind.GAS ? gasResolver.apply(name) : null;
            return gas == null ? null : new MekGasChemicalType(gas);
        });
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
        if (stack == null || stack.getGas() == null || stack.amount <= 0 || !inputSides.test(side)) {
            return 0;
        }
        long accepted = tank.insert(new ChemicalStackCE(new MekGasChemicalType(stack.getGas()), stack.amount),
              doTransfer ? Action.EXECUTE : Action.SIMULATE);
        return (int) accepted;
    }

    @Nullable
    @Override
    public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer) {
        if (amount <= 0 || !outputSides.test(side)) {
            return null;
        }
        IChemicalStackCE stored = tank.getStack();
        if (stored == null || !(stored.getType() instanceof MekGasChemicalType)) {
            return null;
        }

        IChemicalStackCE extracted = tank.extract(amount, doTransfer ? Action.EXECUTE : Action.SIMULATE);
        if (extracted == null) {
            return null;
        }
        Gas gas = ((MekGasChemicalType) extracted.getType()).getGas();
        return new GasStack(gas, (int) extracted.getAmount());
    }

    @Override
    public boolean canReceiveGas(EnumFacing side, Gas type) {
        if (type == null || !inputSides.test(side) || tank.getStored() >= tank.getCapacity()) {
            return false;
        }
        IChemicalStackCE stored = tank.getStack();
        return stored == null || stored.getType() instanceof MekGasChemicalType
              && ((MekGasChemicalType) stored.getType()).getGas() == type;
    }

    @Override
    public boolean canDrawGas(EnumFacing side, Gas type) {
        if (!outputSides.test(side)) {
            return false;
        }
        IChemicalStackCE stored = tank.getStack();
        return stored != null && stored.getType() instanceof MekGasChemicalType
              && (type == null || ((MekGasChemicalType) stored.getType()).getGas() == type);
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo() {
        return new GasTankInfo[]{tankInfo};
    }

    @Override
    public int getChemicalTankCount(@Nullable EnumFacing side) {
        return 1;
    }

    @Override
    public IChemicalTankCE getChemicalTank(int tankIndex, @Nullable EnumFacing side) {
        if (tankIndex != 0) {
            throw new IndexOutOfBoundsException("Chemical tank index: " + tankIndex);
        }
        return tank;
    }

    @Override
    public boolean canInsertChemical(int tankIndex, IChemicalTypeCE type, @Nullable EnumFacing side) {
        return tankIndex == 0 && type instanceof MekGasChemicalType
              && canReceiveGas(side, ((MekGasChemicalType) type).getGas());
    }

    @Override
    public boolean canExtractChemical(int tankIndex, IChemicalTypeCE type, @Nullable EnumFacing side) {
        return tankIndex == 0 && type instanceof MekGasChemicalType
              && canDrawGas(side, ((MekGasChemicalType) type).getGas());
    }

    private final class LongGasTankInfo implements GasTankInfo {

        @Nullable
        @Override
        public GasStack getGas() {
            IChemicalStackCE stack = tank.getStack();
            if (stack == null || !(stack.getType() instanceof MekGasChemicalType)) {
                return null;
            }
            return new GasStack(((MekGasChemicalType) stack.getType()).getGas(), saturatingInt(stack.getAmount()));
        }

        @Override
        public int getStored() {
            IChemicalStackCE stack = tank.getStack();
            return stack != null && stack.getType() instanceof MekGasChemicalType
                  ? saturatingInt(stack.getAmount()) : 0;
        }

        @Override
        public int getMaxGas() {
            return saturatingInt(tank.getCapacity());
        }
    }

    private static int saturatingInt(long value) {
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }
}
