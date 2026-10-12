package mekanism.ultimate.api.chemical;

/** Read-only chemical stack view with a long-backed amount. */
public interface IChemicalStackCE
{
    IChemicalTypeCE getType();

    long getAmount();

    IChemicalStackCE copyWithAmount(long amount);

    default boolean isEmpty()
    {
        return getAmount() == 0;
    }
}
