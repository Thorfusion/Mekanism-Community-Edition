package mekanism.ultimate.common.recipe.index;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.IRecipeIndexCE;
import mekanism.ultimate.api.recipe.type.IChemicalInputRecipeCE;
import mekanism.ultimate.common.recipe.key.ChemicalIdentityKeyCE;

public final class ChemicalRecipeIndexCE<RECIPE extends IChemicalInputRecipeCE>
      implements IRecipeIndexCE<IChemicalStackCE, ChemicalIdentityKeyCE, RECIPE> {

    @Nullable
    @Override
    public ChemicalIdentityKeyCE getLookupKey(IChemicalStackCE input) {
        return input == null || input.getType() == null ? null : new ChemicalIdentityKeyCE(input.getType());
    }

    @Override
    public Collection<ChemicalIdentityKeyCE> getRegistrationKeys(RECIPE recipe) {
        return Collections.singleton(new ChemicalIdentityKeyCE(recipe.getInput().getType()));
    }
}
