package mekanism.ultimate.common.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import mekanism.ultimate.api.recipe.IRecipeCE;
import mekanism.ultimate.api.recipe.IRecipeIndexCE;
import mekanism.ultimate.api.recipe.IRecipeManagerCE;
import net.minecraft.util.ResourceLocation;

/**
 * Deterministic recipe manager backed by coarse input-identity buckets.
 *
 * <p>Recipe mutation is rare and rebuilds the index. Lookup examines only the
 * matching bucket and the usually-empty fallback bucket while preserving global
 * registration order. Public methods are synchronized so recipe scripting and
 * reload code cannot expose a partially rebuilt cache.</p>
 */
public final class CachedRecipeManagerCE<INPUT, KEY, RECIPE extends IRecipeCE<INPUT>>
      implements IRecipeManagerCE<INPUT, RECIPE> {

    private final IRecipeIndexCE<INPUT, KEY, RECIPE> indexStrategy;
    private final LinkedHashMap<ResourceLocation, Entry<RECIPE>> recipes = new LinkedHashMap<>();
    private final Map<KEY, List<Entry<RECIPE>>> index = new HashMap<>();
    private final List<Entry<RECIPE>> fallback = new ArrayList<>();

    private long nextOrder;
    private long revision;

    public CachedRecipeManagerCE(IRecipeIndexCE<INPUT, KEY, RECIPE> indexStrategy) {
        this.indexStrategy = Objects.requireNonNull(indexStrategy, "indexStrategy");
    }

    @Override
    public synchronized void add(RECIPE recipe) {
        Objects.requireNonNull(recipe, "recipe");
        ResourceLocation id = requireId(recipe);
        if (recipes.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate recipe id: " + id);
        }
        recipes.put(id, new Entry<>(recipe, nextOrder++));
        rebuildCacheInternal();
    }

    @Override
    public synchronized void addAll(Collection<? extends RECIPE> newRecipes) {
        Objects.requireNonNull(newRecipes, "recipes");
        LinkedHashMap<ResourceLocation, RECIPE> validated = new LinkedHashMap<>();
        for (RECIPE recipe : newRecipes) {
            Objects.requireNonNull(recipe, "recipe");
            ResourceLocation id = requireId(recipe);
            if (recipes.containsKey(id) || validated.put(id, recipe) != null) {
                throw new IllegalArgumentException("Duplicate recipe id: " + id);
            }
        }
        for (Map.Entry<ResourceLocation, RECIPE> entry : validated.entrySet()) {
            recipes.put(entry.getKey(), new Entry<>(entry.getValue(), nextOrder++));
        }
        if (!validated.isEmpty()) {
            rebuildCacheInternal();
        }
    }

    @Nullable
    @Override
    public synchronized RECIPE replace(RECIPE recipe) {
        Objects.requireNonNull(recipe, "recipe");
        ResourceLocation id = requireId(recipe);
        Entry<RECIPE> previous = recipes.get(id);
        long order = previous == null ? nextOrder++ : previous.order;
        recipes.put(id, new Entry<>(recipe, order));
        rebuildCacheInternal();
        return previous == null ? null : previous.recipe;
    }

    @Nullable
    @Override
    public synchronized RECIPE remove(ResourceLocation id) {
        Objects.requireNonNull(id, "id");
        Entry<RECIPE> removed = recipes.remove(id);
        if (removed != null) {
            rebuildCacheInternal();
            return removed.recipe;
        }
        return null;
    }

    @Override
    public synchronized void clear() {
        if (!recipes.isEmpty()) {
            recipes.clear();
            rebuildCacheInternal();
        }
    }

    @Override
    public synchronized void rebuildCache() {
        rebuildCacheInternal();
    }

    @Nullable
    @Override
    public synchronized RECIPE findFirst(INPUT input) {
        Objects.requireNonNull(input, "input");
        KEY key = indexStrategy.getLookupKey(input);
        List<Entry<RECIPE>> keyed = key == null ? null : index.get(key);
        int keyedIndex = 0;
        int fallbackIndex = 0;

        while ((keyed != null && keyedIndex < keyed.size()) || fallbackIndex < fallback.size()) {
            Entry<RECIPE> candidate;
            if (keyed == null || keyedIndex >= keyed.size()) {
                candidate = fallback.get(fallbackIndex++);
            } else if (fallbackIndex >= fallback.size()) {
                candidate = keyed.get(keyedIndex++);
            } else if (keyed.get(keyedIndex).order < fallback.get(fallbackIndex).order) {
                candidate = keyed.get(keyedIndex++);
            } else {
                candidate = fallback.get(fallbackIndex++);
            }
            if (candidate.recipe.matches(input)) {
                return candidate.recipe;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public synchronized RECIPE get(ResourceLocation id) {
        Objects.requireNonNull(id, "id");
        Entry<RECIPE> entry = recipes.get(id);
        return entry == null ? null : entry.recipe;
    }

    @Override
    public synchronized Collection<RECIPE> getRecipes() {
        List<RECIPE> snapshot = new ArrayList<>(recipes.size());
        for (Entry<RECIPE> entry : recipes.values()) {
            snapshot.add(entry.recipe);
        }
        return Collections.unmodifiableList(snapshot);
    }

    @Override
    public synchronized int size() {
        return recipes.size();
    }

    @Override
    public synchronized long getRevision() {
        return revision;
    }

    /**
     * Diagnostic candidate count used by tests and profiling. It excludes
     * recipes in unrelated identity buckets.
     */
    public synchronized int getCandidateCount(INPUT input) {
        Objects.requireNonNull(input, "input");
        KEY key = indexStrategy.getLookupKey(input);
        List<Entry<RECIPE>> keyed = key == null ? null : index.get(key);
        return (keyed == null ? 0 : keyed.size()) + fallback.size();
    }

    private void rebuildCacheInternal() {
        index.clear();
        fallback.clear();
        for (Entry<RECIPE> entry : recipes.values()) {
            Collection<KEY> keys = indexStrategy.getRegistrationKeys(entry.recipe);
            Set<KEY> uniqueKeys = new LinkedHashSet<>();
            if (keys != null) {
                for (KEY key : keys) {
                    if (key != null) {
                        uniqueKeys.add(key);
                    }
                }
            }
            if (uniqueKeys.isEmpty()) {
                fallback.add(entry);
            } else {
                for (KEY key : uniqueKeys) {
                    index.computeIfAbsent(key, ignored -> new ArrayList<>()).add(entry);
                }
            }
        }
        revision++;
    }

    private static ResourceLocation requireId(IRecipeCE<?> recipe) {
        return Objects.requireNonNull(recipe.getId(), "recipe id");
    }

    private static final class Entry<RECIPE> {

        private final RECIPE recipe;
        private final long order;

        private Entry(RECIPE recipe, long order) {
            this.recipe = recipe;
            this.order = order;
        }
    }
}
