package mekanism.ultimate.common.content.chemical;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.api.chemical.IChemicalTypeResolverCE;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;

/** Identity registry for facade chemicals not supplied by legacy gases. */
public final class UltimateChemicalRegistry
      implements IChemicalTypeResolverCE
{
    public static final UltimateChemicalRegistry INSTANCE =
          new UltimateChemicalRegistry();

    private final Map<ChemicalKind, Map<String, IChemicalTypeCE>> types =
          new EnumMap<ChemicalKind, Map<String, IChemicalTypeCE>>(
                ChemicalKind.class);

    private UltimateChemicalRegistry()
    {
        for (ChemicalKind kind : ChemicalKind.values())
        {
            types.put(kind, new LinkedHashMap<String, IChemicalTypeCE>());
        }
    }

    public synchronized <TYPE extends IChemicalTypeCE> TYPE register(TYPE type)
    {
        Objects.requireNonNull(type, "type");
        String name = Objects.requireNonNull(type.getRegistryName(),
              "registryName").trim();
        if (name.isEmpty())
        {
            throw new IllegalArgumentException(
                  "Chemical registry name cannot be empty");
        }
        Map<String, IChemicalTypeCE> byName = types.get(type.getKind());
        IChemicalTypeCE existing = byName.get(name);
        if (existing != null && existing != type)
        {
            throw new IllegalArgumentException("Duplicate " + type.getKind()
                  + " chemical: " + name);
        }
        byName.put(name, type);
        return type;
    }

    @Nullable
    @Override
    public synchronized IChemicalTypeCE resolve(ChemicalKind kind,
          String registryName)
    {
        if (kind == null || registryName == null || registryName.isEmpty())
        {
            return null;
        }
        if (kind == ChemicalKind.GAS)
        {
            Gas gas = GasRegistry.getGas(registryName);
            if (gas != null)
            {
                return new MekGasChemicalType(gas);
            }
        }
        Map<String, IChemicalTypeCE> byName = types.get(kind);
        return byName == null ? null : byName.get(registryName);
    }
}
