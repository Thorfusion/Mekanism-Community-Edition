package mekanism.ultimate.common.recipe.type;

import java.util.Objects;
import mekanism.ultimate.api.recipe.ingredient.ChemicalIngredientCE;
import mekanism.ultimate.api.recipe.ingredient.ItemIngredientCE;
import mekanism.ultimate.api.recipe.input.ItemChemicalInputCE;
import mekanism.ultimate.api.recipe.type.IItemChemicalInputRecipeCE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/** Immutable item plus chemical to item recipe with a recipe-specific duration. */
public final class ItemChemicalToItemRecipeCE implements IItemChemicalInputRecipeCE {

    private final ResourceLocation id;
    private final ItemIngredientCE itemInput;
    private final ChemicalIngredientCE chemicalInput;
    private final ItemStack output;
    private final int duration;

    public ItemChemicalToItemRecipeCE(ResourceLocation id, ItemIngredientCE itemInput,
          ChemicalIngredientCE chemicalInput, ItemStack output, int duration) {
        this.id = Objects.requireNonNull(id, "id");
        this.itemInput = Objects.requireNonNull(itemInput, "itemInput");
        this.chemicalInput = Objects.requireNonNull(chemicalInput, "chemicalInput");
        Objects.requireNonNull(output, "output");
        if (output.isEmpty()) {
            throw new IllegalArgumentException("Recipe output cannot be empty");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Recipe duration must be positive: " + duration);
        }
        this.output = output.copy();
        this.duration = duration;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public ItemIngredientCE getItemInput() {
        return itemInput;
    }

    @Override
    public ChemicalIngredientCE getChemicalInput() {
        return chemicalInput;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean matches(ItemChemicalInputCE input) {
        return input != null && itemInput.testType(input.getItem())
              && chemicalInput.testType(input.getChemical());
    }
}
