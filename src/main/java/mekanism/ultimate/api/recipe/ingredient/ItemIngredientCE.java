package mekanism.ultimate.api.recipe.ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Immutable item ingredient supporting direct stacks, metadata wildcards, Ore
 * Dictionary identities, required counts, and optional NBT-sensitive matching.
 */
public abstract class ItemIngredientCE {

    private final int amount;
    private final boolean nbtSensitive;

    private ItemIngredientCE(int amount, boolean nbtSensitive) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Item ingredient amount must be positive: " + amount);
        }
        this.amount = amount;
        this.nbtSensitive = nbtSensitive;
    }

    public static ItemIngredientCE direct(ItemStack template, boolean nbtSensitive) {
        Objects.requireNonNull(template, "template");
        if (template.isEmpty()) {
            throw new IllegalArgumentException("Direct item ingredient cannot be empty");
        }
        return new Direct(template.copy(), nbtSensitive);
    }

    public static ItemIngredientCE ore(String oreName, int amount) {
        return ore(oreName, amount, false);
    }

    public static ItemIngredientCE ore(String oreName, int amount, boolean nbtSensitive) {
        if (oreName == null || oreName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ore Dictionary name cannot be blank");
        }
        return new Ore(oreName, amount, nbtSensitive);
    }

    /**
     * Creates one ingredient that accepts any of the supplied item identities.
     * This is the 1.12 equivalent of a modern item tag when no matching Ore
     * Dictionary entry is guaranteed to exist.
     */
    public static ItemIngredientCE anyOf(ItemStack... templates) {
        return anyOf(1, false, templates);
    }

    public static ItemIngredientCE anyOf(int amount, boolean nbtSensitive, ItemStack... templates) {
        if (templates == null || templates.length == 0) {
            throw new IllegalArgumentException("Item ingredient alternatives cannot be empty");
        }
        return new Alternatives(amount, nbtSensitive, Arrays.asList(templates));
    }

    public final int getAmount() {
        return amount;
    }

    public final boolean isNbtSensitive() {
        return nbtSensitive;
    }

    public abstract boolean testType(ItemStack stack);

    public final boolean hasRequiredAmount(ItemStack stack) {
        return testType(stack) && stack.getCount() >= amount;
    }

    /**
     * Returns the current item identities used to build coarse lookup buckets.
     * Ore ingredients reflect the Ore Dictionary at call time.
     */
    public abstract Collection<Item> getIndexedItems();

    /**
     * Returns defensive copies suitable for recipe viewers.
     */
    public abstract List<ItemStack> getRepresentations();

    protected final boolean matchesTemplate(ItemStack template, ItemStack stack) {
        if (stack == null || stack.isEmpty() || !OreDictionary.itemMatches(template, stack, false)) {
            return false;
        }
        return !nbtSensitive || ItemStack.areItemStackTagsEqual(template, stack);
    }

    private static Set<Item> newIdentitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }

    private static final class Direct extends ItemIngredientCE {

        private final ItemStack template;
        private final Collection<Item> indexedItems;

        private Direct(ItemStack template, boolean nbtSensitive) {
            super(template.getCount(), nbtSensitive);
            this.template = template;
            this.indexedItems = Collections.singleton(template.getItem());
        }

        @Override
        public boolean testType(ItemStack stack) {
            return matchesTemplate(template, stack);
        }

        @Override
        public Collection<Item> getIndexedItems() {
            return indexedItems;
        }

        @Override
        public List<ItemStack> getRepresentations() {
            return Collections.singletonList(template.copy());
        }
    }

    private static final class Ore extends ItemIngredientCE {

        private final String oreName;

        private Ore(String oreName, int amount, boolean nbtSensitive) {
            super(amount, nbtSensitive);
            this.oreName = oreName;
        }

        @Override
        public boolean testType(ItemStack stack) {
            if (stack == null || stack.isEmpty()) {
                return false;
            }
            for (ItemStack representation : OreDictionary.getOres(oreName, false)) {
                if (matchesTemplate(representation, stack)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Collection<Item> getIndexedItems() {
            Set<Item> items = newIdentitySet();
            for (ItemStack representation : OreDictionary.getOres(oreName, false)) {
                if (!representation.isEmpty()) {
                    items.add(representation.getItem());
                }
            }
            return items;
        }

        @Override
        public List<ItemStack> getRepresentations() {
            List<ItemStack> copies = new ArrayList<>();
            for (ItemStack representation : OreDictionary.getOres(oreName, false)) {
                if (!representation.isEmpty()) {
                    ItemStack copy = representation.copy();
                    copy.setCount(getAmount());
                    copies.add(copy);
                }
            }
            return Collections.unmodifiableList(copies);
        }
    }

    private static final class Alternatives extends ItemIngredientCE {

        private final List<ItemStack> templates;
        private final Collection<Item> indexedItems;

        private Alternatives(int amount, boolean nbtSensitive, Collection<ItemStack> templates) {
            super(amount, nbtSensitive);
            List<ItemStack> copies = new ArrayList<>();
            Set<Item> items = newIdentitySet();
            for (ItemStack template : templates) {
                if (template == null || template.isEmpty()) {
                    throw new IllegalArgumentException("Item ingredient alternative cannot be empty");
                }
                ItemStack copy = template.copy();
                copy.setCount(amount);
                copies.add(copy);
                items.add(copy.getItem());
            }
            this.templates = Collections.unmodifiableList(copies);
            this.indexedItems = Collections.unmodifiableSet(items);
        }

        @Override
        public boolean testType(ItemStack stack) {
            for (ItemStack template : templates) {
                if (matchesTemplate(template, stack)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Collection<Item> getIndexedItems() {
            return indexedItems;
        }

        @Override
        public List<ItemStack> getRepresentations() {
            List<ItemStack> copies = new ArrayList<>(templates.size());
            for (ItemStack template : templates) {
                copies.add(template.copy());
            }
            return Collections.unmodifiableList(copies);
        }
    }
}
