package mekanism.ultimate.api.chemical;

import javax.annotation.Nullable;

/** Resolves a serialized chemical identity without coupling storage to a registry implementation. */
public interface IChemicalTypeResolverCE
{
    @Nullable
    IChemicalTypeCE resolve(ChemicalKind kind, String registryName);
}
