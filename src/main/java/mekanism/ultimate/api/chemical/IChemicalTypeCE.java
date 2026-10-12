package mekanism.ultimate.api.chemical;

/** Stable identity and transport semantics for an Ultimate chemical. */
public interface IChemicalTypeCE
{
    /** Returns the stable serialized name. */
    String getRegistryName();

    ChemicalKind getKind();

    boolean isRadioactive();
}
