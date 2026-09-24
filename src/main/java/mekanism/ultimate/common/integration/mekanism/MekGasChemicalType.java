package mekanism.ultimate.common.integration.mekanism;

import java.util.Objects;
import mekanism.api.gas.Gas;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;

/**
 * Ultimate chemical identity backed by an old Mekanism gas.
 */
public final class MekGasChemicalType implements IChemicalTypeCE {

    private final Gas gas;
    private final boolean radioactive;

    public MekGasChemicalType(Gas gas) {
        this(gas, false);
    }

    public MekGasChemicalType(Gas gas, boolean radioactive) {
        this.gas = Objects.requireNonNull(gas, "gas");
        this.radioactive = radioactive;
    }

    public Gas getGas() {
        return gas;
    }

    @Override
    public String getRegistryName() {
        return gas.getName();
    }

    @Override
    public ChemicalKind getKind() {
        return ChemicalKind.GAS;
    }

    @Override
    public boolean isRadioactive() {
        return radioactive;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IChemicalTypeCE)) {
            return false;
        }
        IChemicalTypeCE other = (IChemicalTypeCE) obj;
        return other.getKind() == ChemicalKind.GAS && gas.getName().equals(other.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ChemicalKind.GAS, gas.getName());
    }
}
