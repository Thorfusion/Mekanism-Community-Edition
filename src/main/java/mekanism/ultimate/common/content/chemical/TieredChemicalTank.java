package mekanism.ultimate.common.content.chemical;

import java.util.Objects;
import javax.annotation.Nullable;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.IChemicalStackCE;

/** Long-backed storage with the stable Creative Chemical Tank semantics. */
public final class TieredChemicalTank extends LongChemicalTank {

    private boolean creative;

    public TieredChemicalTank(long capacity, boolean creative) {
        super(capacity);
        this.creative = creative;
    }

    public void configure(long capacity, boolean creative) {
        setCapacity(capacity);
        this.creative = creative;
    }

    public boolean isCreative() {
        return creative;
    }

    @Override
    public long insert(IChemicalStackCE stack, Action action) {
        Objects.requireNonNull(stack, "stack");
        Objects.requireNonNull(action, "action");
        if (!creative) {
            return super.insert(stack, action);
        }
        if (stack.getAmount() < 0) {
            throw new IllegalArgumentException("Chemical amount cannot be negative: " + stack.getAmount());
        }
        IChemicalStackCE stored = getStack();
        if (stack.isEmpty() || stored != null && !isSameType(stored.getType(), stack.getType())) {
            return 0;
        }
        if (stored == null && action.execute()) {
            super.insert(new ChemicalStackCE(stack.getType(), Long.MAX_VALUE), Action.EXECUTE);
        }
        return stack.getAmount();
    }

    @Nullable
    @Override
    public ChemicalStackCE extract(long amount, Action action) {
        Objects.requireNonNull(action, "action");
        if (amount < 0) {
            throw new IllegalArgumentException("Chemical amount cannot be negative: " + amount);
        }
        if (!creative) {
            IChemicalStackCE extracted = super.extract(amount, action);
            return extracted == null ? null
                  : new ChemicalStackCE(extracted.getType(), extracted.getAmount());
        }
        IChemicalStackCE stored = getStack();
        if (amount == 0 || stored == null) {
            return null;
        }
        return new ChemicalStackCE(stored.getType(), Math.min(amount, stored.getAmount()));
    }
}
