package mekanism.ultimate.api.chemical;

import javax.annotation.Nullable;
import mekanism.ultimate.api.Action;

/**
 * Long-backed single-chemical storage.
 */
public interface IChemicalTankCE {

    @Nullable
    IChemicalStackCE getStack();

    long getStored();

    long getCapacity();

    /**
     * @return the non-negative amount accepted
     */
    long insert(IChemicalStackCE stack, Action action);

    /**
     * @return the extracted stack, or {@code null} when nothing can be extracted
     */
    @Nullable
    IChemicalStackCE extract(long amount, Action action);
}
