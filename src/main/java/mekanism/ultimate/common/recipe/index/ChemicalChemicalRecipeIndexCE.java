package mekanism.ultimate.common.recipe.index;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.IRecipeIndexCE;
import mekanism.ultimate.api.recipe.input.ChemicalChemicalInputCE;
import mekanism.ultimate.common.recipe.key.ChemicalIdentityKeyCE;
import mekanism.ultimate.common.recipe.type.ChemicalChemicalToChemicalRecipeCE;

public final class ChemicalChemicalRecipeIndexCE implements IRecipeIndexCE<ChemicalChemicalInputCE,
      ChemicalIdentityKeyCE, ChemicalChemicalToChemicalRecipeCE> {

    @Nullable
    @Override
    public ChemicalIdentityKeyCE getLookupKey(ChemicalChemicalInputCE input) {
        IChemicalStackCE left = input == null ? null : input.getLeft();
        return left == null || left.isEmpty() ? null : new ChemicalIdentityKeyCE(left.getType());
    }

    @Override
    public Collection<ChemicalIdentityKeyCE> getRegistrationKeys(
          ChemicalChemicalToChemicalRecipeCE recipe) {
        return Arrays.asList(new ChemicalIdentityKeyCE(recipe.getLeftInput().getType()),
              new ChemicalIdentityKeyCE(recipe.getRightInput().getType()));
    }
}
