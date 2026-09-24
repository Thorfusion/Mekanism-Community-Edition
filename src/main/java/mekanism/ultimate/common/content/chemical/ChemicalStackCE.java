package mekanism.ultimate.common.content.chemical;

import java.util.Objects;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;

/**
 * Immutable chemical stack.
 */
public final class ChemicalStackCE implements IChemicalStackCE {

    private final IChemicalTypeCE type;
    private final long amount;

    public ChemicalStackCE(IChemicalTypeCE type, long amount) {
        this.type = Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("Chemical amount cannot be negative: " + amount);
        }
        this.amount = amount;
    }

    @Override
    public IChemicalTypeCE getType() {
        return type;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public ChemicalStackCE copyWithAmount(long newAmount) {
        return new ChemicalStackCE(type, newAmount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IChemicalStackCE)) {
            return false;
        }
        IChemicalStackCE other = (IChemicalStackCE) obj;
        return amount == other.getAmount() && LongChemicalTank.isSameType(type, other.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type.getKind(), type.getRegistryName(), amount);
    }

    @Override
    public String toString() {
        return type.getKind() + ":" + type.getRegistryName() + " x " + amount;
    }
}
