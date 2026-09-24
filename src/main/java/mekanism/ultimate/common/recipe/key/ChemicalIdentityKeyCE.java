package mekanism.ultimate.common.recipe.key;

import java.util.Objects;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;

/** Stable immutable chemical identity for recipe indexes. */
public final class ChemicalIdentityKeyCE {

    private final ChemicalKind kind;
    private final String registryName;

    public ChemicalIdentityKeyCE(IChemicalTypeCE type) {
        Objects.requireNonNull(type, "type");
        this.kind = Objects.requireNonNull(type.getKind(), "chemical kind");
        this.registryName = Objects.requireNonNull(type.getRegistryName(), "chemical registry name");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChemicalIdentityKeyCE)) {
            return false;
        }
        ChemicalIdentityKeyCE other = (ChemicalIdentityKeyCE) obj;
        return kind == other.kind && registryName.equals(other.registryName);
    }

    @Override
    public int hashCode() {
        return 31 * kind.hashCode() + registryName.hashCode();
    }

    @Override
    public String toString() {
        return kind + ":" + registryName;
    }
}
