package mekanism.ultimate.api.recipe;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

/**
 * Mutable recipe registry with deterministic, cached lookup.
 */
public interface IRecipeManagerCE<INPUT, RECIPE extends IRecipeCE<INPUT>> {

    void add(RECIPE recipe);

    void addAll(Collection<? extends RECIPE> recipes);

    @Nullable
    RECIPE replace(RECIPE recipe);

    @Nullable
    RECIPE remove(ResourceLocation id);

    void clear();

    /**
     * Rebuilds lookup buckets after an external identity source such as the Ore
     * Dictionary changes. This does not add or remove recipes.
     */
    void rebuildCache();

    @Nullable
    RECIPE findFirst(INPUT input);

    @Nullable
    RECIPE get(ResourceLocation id);

    Collection<RECIPE> getRecipes();

    int size();

    long getRevision();
}
