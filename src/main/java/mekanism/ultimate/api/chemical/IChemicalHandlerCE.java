package mekanism.ultimate.api.chemical;

import javax.annotation.Nullable;
import net.minecraftforge.common.util.ForgeDirection;

/** Exposes the chemical tanks available through an optional side. */
public interface IChemicalHandlerCE
{
    int getChemicalTankCount(@Nullable ForgeDirection side);

    IChemicalTankCE getChemicalTank(int tank, @Nullable ForgeDirection side);

    boolean canInsertChemical(int tank, IChemicalTypeCE type, @Nullable ForgeDirection side);

    boolean canExtractChemical(int tank, IChemicalTypeCE type, @Nullable ForgeDirection side);
}
