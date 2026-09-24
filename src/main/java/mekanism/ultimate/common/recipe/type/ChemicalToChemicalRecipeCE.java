package mekanism.ultimate.common.recipe.type;

import java.util.Objects;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;
import mekanism.ultimate.api.recipe.type.IChemicalInputRecipeCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import net.minecraft.util.ResourceLocation;

/** Basic chemical-to-chemical recipe used by centrifuging and conversion machines. */
public final class ChemicalToChemicalRecipeCE implements IChemicalInputRecipeCE {

    private final ResourceLocation id;
    private final ChemicalIngredientCE input;
    private final ChemicalStackCE output;

    public ChemicalToChemicalRecipeCE(ResourceLocation id, ChemicalIngredientCE input, IChemicalStackCE output) {
        this.id = Objects.requireNonNull(id, "id");
        this.input = Objects.requireNonNull(input, "input");
        Objects.requireNonNull(output, "output");
        if (output.getAmount() <= 0) {
            throw new IllegalArgumentException("Recipe output amount must be positive: " + output.getAmount());
        }
        this.output = new ChemicalStackCE(output.getType(), output.getAmount());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public ChemicalIngredientCE getInput() {
        return input;
    }

    public ChemicalStackCE getOutput() {
        return output;
    }

    @Override
    public boolean matches(IChemicalStackCE candidate) {
        return input.testType(candidate);
    }
}
