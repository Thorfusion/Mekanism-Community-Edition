package mekanism.ultimate.api.recipe.input;

import javax.annotation.Nullable;
import mekanism.ultimate.api.chemical.IChemicalStackCE;

public final class ChemicalChemicalInputCE {

    private final IChemicalStackCE left;
    private final IChemicalStackCE right;

    public ChemicalChemicalInputCE(@Nullable IChemicalStackCE left,
          @Nullable IChemicalStackCE right) {
        this.left = left;
        this.right = right;
    }

    @Nullable
    public IChemicalStackCE getLeft() {
        return left;
    }

    @Nullable
    public IChemicalStackCE getRight() {
        return right;
    }
}
