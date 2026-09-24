package mekanism.ultimate.api.recipe.ingredient;

import java.util.Objects;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;

/**
 * Immutable chemical type and required amount.
 */
public final class ChemicalIngredientCE {

    private final IChemicalTypeCE type;
    private final long amount;

    public ChemicalIngredientCE(IChemicalTypeCE type, long amount) {
        this.type = Objects.requireNonNull(type, "type");
        if (amount <= 0) {
            throw new IllegalArgumentException("Chemical ingredient amount must be positive: " + amount);
        }
        this.amount = amount;
    }

    public IChemicalTypeCE getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }

    public boolean testType(IChemicalStackCE stack) {
        return stack != null && isSameType(type, stack.getType());
    }

    public boolean hasRequiredAmount(IChemicalStackCE stack) {
        return testType(stack) && stack.getAmount() >= amount;
    }

    private static boolean isSameType(IChemicalTypeCE first, IChemicalTypeCE second) {
        return first == second || first != null && second != null && first.getKind() == second.getKind()
              && first.getRegistryName().equals(second.getRegistryName());
    }
}
