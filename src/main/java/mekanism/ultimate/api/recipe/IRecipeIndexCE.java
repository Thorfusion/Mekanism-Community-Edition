package mekanism.ultimate.api.recipe;

import java.util.Collection;
import javax.annotation.Nullable;

/** Maps inputs and recipes to a coarse immutable identity for cached lookup. */
public interface IRecipeIndexCE<INPUT, KEY, RECIPE extends IRecipeCE<INPUT>>
{
    @Nullable
    KEY getLookupKey(INPUT input);

    /** An empty result places the recipe in the ordered fallback bucket. */
    Collection<KEY> getRegistrationKeys(RECIPE recipe);
}
