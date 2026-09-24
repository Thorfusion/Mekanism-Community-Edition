package mekanism.ultimate.api.chemical;

/**
 * Stable identity and transport semantics for an Ultimate chemical.
 */
public interface IChemicalTypeCE {

    /**
     * Returns the stable serialized name. Old Mek gases retain their exact gas
     * registry name; new Ultimate chemicals use namespaced names.
     */
    String getRegistryName();

    ChemicalKind getKind();

    boolean isRadioactive();
}
