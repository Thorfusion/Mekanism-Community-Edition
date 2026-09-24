package mekanism.ultimate.common.recipe.type;

import java.util.Objects;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.recipe.ingredient.ItemIngredientCE;
import mekanism.ultimate.api.recipe.type.IItemInputRecipeCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/** Basic item-to-chemical recipe used by oxidizing and pigment extraction. */
public final class ItemToChemicalRecipeCE implements IItemInputRecipeCE {

    private final ResourceLocation id;
    private final ItemIngredientCE input;
    private final ChemicalStackCE output;

    public ItemToChemicalRecipeCE(ResourceLocation id, ItemIngredientCE input, IChemicalStackCE output) {
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
    public ItemIngredientCE getInput() {
        return input;
    }

    public ChemicalStackCE getOutput() {
        return output;
    }

    @Override
    public boolean matches(ItemStack candidate) {
        return input.testType(candidate);
    }
}
