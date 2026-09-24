package mekanism.ultimate.common.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import mekanism.ultimate.api.recipe.IRecipeCE;
import mekanism.ultimate.api.recipe.IRecipeIndexCE;
import net.minecraft.util.ResourceLocation;
import org.junit.Test;

public class CachedRecipeManagerCETest {

    @Test
    public void indexedLookupSkipsUnrelatedRecipesAndPreservesFallbackOrder() {
        CachedRecipeManagerCE<String, Character, TestRecipe> manager = new CachedRecipeManagerCE<>(new TestIndex());
        AtomicInteger fallbackChecks = new AtomicInteger();
        AtomicInteger appleChecks = new AtomicInteger();
        AtomicInteger bananaChecks = new AtomicInteger();
        TestRecipe fallback = new TestRecipe("fallback", "", null, fallbackChecks);
        TestRecipe apple = new TestRecipe("apple", "app", 'a', appleChecks);
        TestRecipe banana = new TestRecipe("banana", "ban", 'b', bananaChecks);

        manager.add(fallback);
        manager.add(apple);
        manager.add(banana);

        assertSame(fallback, manager.findFirst("apple"));
        assertEquals(2, manager.getCandidateCount("apple"));
        assertEquals(1, fallbackChecks.get());
        assertEquals(0, appleChecks.get());
        assertEquals(0, bananaChecks.get());

        manager.remove(fallback.getId());
        assertSame(apple, manager.findFirst("apple"));
        assertEquals(1, manager.getCandidateCount("apple"));
        assertEquals(1, appleChecks.get());
        assertEquals(0, bananaChecks.get());
    }

    @Test
    public void mutationRebuildsIndexAndReplacementKeepsRecipePriority() {
        CachedRecipeManagerCE<String, Character, TestRecipe> manager = new CachedRecipeManagerCE<>(new TestIndex());
        TestRecipe first = new TestRecipe("first", "a", 'a', new AtomicInteger());
        TestRecipe second = new TestRecipe("second", "a", 'a', new AtomicInteger());
        manager.add(first);
        manager.add(second);
        long revision = manager.getRevision();

        TestRecipe replacement = new TestRecipe("first", "a", 'a', new AtomicInteger());
        assertSame(first, manager.replace(replacement));
        assertSame(replacement, manager.findFirst("anything"));
        assertEquals(revision + 1, manager.getRevision());
        assertEquals(2, manager.size());

        manager.clear();
        assertEquals(0, manager.size());
        assertNull(manager.findFirst("anything"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicateIdsAreRejected() {
        CachedRecipeManagerCE<String, Character, TestRecipe> manager = new CachedRecipeManagerCE<>(new TestIndex());
        manager.add(new TestRecipe("duplicate", "a", 'a', new AtomicInteger()));
        manager.add(new TestRecipe("duplicate", "b", 'b', new AtomicInteger()));
    }

    private static final class TestIndex implements IRecipeIndexCE<String, Character, TestRecipe> {

        @Override
        public Character getLookupKey(String input) {
            return input.isEmpty() ? null : input.charAt(0);
        }

        @Override
        public Collection<Character> getRegistrationKeys(TestRecipe recipe) {
            return recipe.key == null ? Collections.emptyList() : Collections.singleton(recipe.key);
        }
    }

    private static final class TestRecipe implements IRecipeCE<String> {

        private final ResourceLocation id;
        private final String prefix;
        private final Character key;
        private final AtomicInteger checks;

        private TestRecipe(String id, String prefix, Character key, AtomicInteger checks) {
            this.id = new ResourceLocation("test", id);
            this.prefix = prefix;
            this.key = key;
            this.checks = checks;
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public boolean matches(String input) {
            checks.incrementAndGet();
            return input.startsWith(prefix);
        }
    }
}
