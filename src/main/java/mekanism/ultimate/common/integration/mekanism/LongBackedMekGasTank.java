package mekanism.ultimate.common.integration.mekanism;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.util.CheckedLongMath;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Mutable legacy {@link GasTank} view over an authoritative long-backed tank.
 *
 * <p>Legacy calls can move at most an {@code int} amount per operation. Query
 * values saturate instead of truncating, while all mutations immediately apply
 * to the wrapped tank. This makes old GUI widgets and the Gauge Dropper safe to
 * use without maintaining a second, divergent store.</p>
 */
public final class LongBackedMekGasTank extends GasTank {

    private final LongChemicalTank tank;
    private final Function<Gas, MekGasChemicalType> typeFactory;

    public LongBackedMekGasTank(LongChemicalTank tank) {
        this(tank, MekGasChemicalType::new);
    }

    public LongBackedMekGasTank(LongChemicalTank tank, Function<Gas, MekGasChemicalType> typeFactory) {
        super(CheckedLongMath.saturatingInt(Objects.requireNonNull(tank, "tank").getCapacity()));
        this.tank = tank;
        this.typeFactory = Objects.requireNonNull(typeFactory, "typeFactory");
    }

    public LongChemicalTank getLongTank() {
        return tank;
    }

    @Nullable
    @Override
    public GasStack draw(int amount, boolean doDraw) {
        if (amount <= 0) {
            return null;
        }
        IChemicalStackCE stored = tank.getStack();
        Gas gas = toGas(stored);
        if (gas == null) {
            return null;
        }
        IChemicalStackCE extracted = tank.extract(amount, doDraw ? Action.EXECUTE : Action.SIMULATE);
        return extracted == null ? null : new GasStack(gas, CheckedLongMath.saturatingInt(extracted.getAmount()));
    }

    @Override
    public int receive(GasStack stack, boolean doReceive) {
        if (stack == null || stack.getGas() == null || stack.amount <= 0) {
            return 0;
        }
        return CheckedLongMath.saturatingInt(tank.insert(new ChemicalStackCE(typeFactory.apply(stack.getGas()), stack.amount),
              doReceive ? Action.EXECUTE : Action.SIMULATE));
    }

    @Override
    public boolean canReceive(Gas gas) {
        return getNeeded() > 0 && canReceiveType(gas);
    }

    @Override
    public boolean canReceiveType(Gas gas) {
        if (hasNonGasChemical()) {
            return false;
        }
        Gas stored = getGasType();
        return stored == null || gas == null || gas == stored;
    }

    @Override
    public boolean canDraw(Gas gas) {
        Gas stored = getGasType();
        return stored != null && (gas == null || gas == stored);
    }

    @Override
    public int getNeeded() {
        if (hasNonGasChemical()) {
            return 0;
        }
        return CheckedLongMath.saturatingInt(CheckedLongMath.subtract(tank.getCapacity(), tank.getStored()));
    }

    @Override
    public int getMaxGas() {
        return CheckedLongMath.saturatingInt(tank.getCapacity());
    }

    /** The wrapped tank owns capacity; legacy callers cannot resize it. */
    @Override
    public void setMaxGas(int capacity) {
    }

    @Nullable
    @Override
    public GasStack getGas() {
        IChemicalStackCE stack = tank.getStack();
        Gas gas = toGas(stack);
        return stack == null || gas == null ? null : new GasStack(gas, CheckedLongMath.saturatingInt(stack.getAmount()));
    }

    @Override
    public void setGas(GasStack stack) {
        // A legacy gas caller must never be able to clear or replace a facade
        // chemical it cannot represent.
        if (hasNonGasChemical()) {
            return;
        }
        tank.clear();
        if (stack != null && stack.getGas() != null && stack.amount > 0) {
            tank.insert(new ChemicalStackCE(typeFactory.apply(stack.getGas()), stack.amount), Action.EXECUTE);
        }
    }

    @Nullable
    @Override
    public Gas getGasType() {
        return toGas(tank.getStack());
    }

    @Override
    public int getStored() {
        return hasNonGasChemical() ? 0 : CheckedLongMath.saturatingInt(tank.getStored());
    }

    @Override
    public NBTTagCompound write(NBTTagCompound data) {
        tank.writeToNBT(data);
        return data;
    }

    @Override
    public void read(NBTTagCompound data) {
        tank.readFromNBT(data, (kind, name) -> {
            Gas gas = kind == ChemicalKind.GAS ? GasRegistry.getGas(name) : null;
            return gas == null ? null : typeFactory.apply(gas);
        });
    }

    @Nullable
    private static Gas toGas(@Nullable IChemicalStackCE stack) {
        return stack != null && stack.getType() instanceof MekGasChemicalType
              ? ((MekGasChemicalType) stack.getType()).getGas() : null;
    }

    private boolean hasNonGasChemical() {
        IChemicalStackCE stack = tank.getStack();
        return stack != null && !(stack.getType() instanceof MekGasChemicalType);
    }

}
