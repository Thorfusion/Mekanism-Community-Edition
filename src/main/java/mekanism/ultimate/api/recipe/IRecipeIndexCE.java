package mekanism.ultimate.api.recipe;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Maps inputs and recipes to a coarse immutable identity used by a cached
 * recipe manager.
 *
 * <p>The key narrows lookup candidates; {@link IRecipeCE#matches(Object)} is
 * still authoritative. Returning no registration keys places a recipe in the
 * ordered fallback bucket, which is useful for late Ore Dictionary entries.</p>
 */
public interface IRecipeIndexCE<INPUT, KEY, RECIPE extends IRecipeCE<INPUT>> {

    @Nullable
    KEY getLookupKey(INPUT input);

    Collection<KEY> getRegistrationKeys(RECIPE recipe);
}
