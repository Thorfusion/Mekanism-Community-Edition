package mekanism.ultimate.api.chemical;

import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;

/**
 * Exposes the chemical tanks available through an optional side.
 */
public interface IChemicalHandlerCE {

    int getChemicalTankCount(@Nullable EnumFacing side);

    IChemicalTankCE getChemicalTank(int tank, @Nullable EnumFacing side);

    boolean canInsertChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side);

    boolean canExtractChemical(int tank, IChemicalTypeCE type, @Nullable EnumFacing side);
}
