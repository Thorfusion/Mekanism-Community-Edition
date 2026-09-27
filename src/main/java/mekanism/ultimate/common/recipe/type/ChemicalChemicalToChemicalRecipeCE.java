package mekanism.ultimate.common.recipe.type;

import java.util.Objects;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.IRecipeCE;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;
import mekanism.ultimate.api.recipe.input.ChemicalChemicalInputCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import net.minecraft.util.ResourceLocation;

/** Symmetric two-chemical recipe used by the Pigment Mixer. */
public final class ChemicalChemicalToChemicalRecipeCE implements IRecipeCE<ChemicalChemicalInputCE> {

    private final ResourceLocation id;
    private final ChemicalIngredientCE leftInput;
    private final ChemicalIngredientCE rightInput;
    private final ChemicalStackCE output;

    public ChemicalChemicalToChemicalRecipeCE(ResourceLocation id, ChemicalIngredientCE leftInput,
          ChemicalIngredientCE rightInput, IChemicalStackCE output) {
        this.id = Objects.requireNonNull(id, "id");
        this.leftInput = Objects.requireNonNull(leftInput, "leftInput");
        this.rightInput = Objects.requireNonNull(rightInput, "rightInput");
        Objects.requireNonNull(output, "output");
        if (output.getAmount() <= 0) {
            throw new IllegalArgumentException("Recipe output amount must be positive");
        }
        this.output = new ChemicalStackCE(output.getType(), output.getAmount());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public ChemicalIngredientCE getLeftInput() {
        return leftInput;
    }

    public ChemicalIngredientCE getRightInput() {
        return rightInput;
    }

    public ChemicalStackCE getOutput() {
        return output.copyWithAmount(output.getAmount());
    }

    @Override
    public boolean matches(ChemicalChemicalInputCE input) {
        return input != null && (leftInput.testType(input.getLeft()) && rightInput.testType(input.getRight())
              || leftInput.testType(input.getRight()) && rightInput.testType(input.getLeft()));
    }
}
