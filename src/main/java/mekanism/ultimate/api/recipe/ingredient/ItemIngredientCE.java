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
 * Immutable 1.7 item ingredient supporting direct stacks, wildcard metadata,
 * Ore Dictionary identities, counts, alternatives, and optional NBT matching.
 */
public abstract class ItemIngredientCE
{
    interface OreLookup
    {
        List<ItemStack> getOres(String oreName);
    }

    private static final OreLookup FORGE_ORE_LOOKUP = new OreLookup()
    {
        @Override
        public List<ItemStack> getOres(String oreName)
        {
            return OreDictionary.getOres(oreName);
        }
    };

    private final int amount;
    private final boolean nbtSensitive;

    private ItemIngredientCE(int amount, boolean nbtSensitive)
    {
        if (amount <= 0)
        {
            throw new IllegalArgumentException("Item ingredient amount must be positive: " + amount);
        }
        this.amount = amount;
        this.nbtSensitive = nbtSensitive;
    }

    public static ItemIngredientCE direct(ItemStack template, boolean nbtSensitive)
    {
        requireNonEmpty(template, "Direct item ingredient cannot be empty");
        return new Direct(template.copy(), nbtSensitive);
    }

    public static ItemIngredientCE ore(String oreName, int amount)
    {
        return ore(oreName, amount, false);
    }

    public static ItemIngredientCE ore(String oreName, int amount, boolean nbtSensitive)
    {
        return ore(oreName, amount, nbtSensitive, FORGE_ORE_LOOKUP);
    }

    static ItemIngredientCE ore(String oreName, int amount, boolean nbtSensitive, OreLookup lookup)
    {
        if (oreName == null || oreName.trim().isEmpty())
        {
            throw new IllegalArgumentException("Ore Dictionary name cannot be blank");
        }
        return new Ore(oreName, amount, nbtSensitive, Objects.requireNonNull(lookup, "lookup"));
    }

    /** Creates one ingredient accepting any supplied item identity. */
    public static ItemIngredientCE anyOf(ItemStack... templates)
    {
        return anyOf(1, false, templates);
    }

    public static ItemIngredientCE anyOf(int amount, boolean nbtSensitive, ItemStack... templates)
    {
        if (templates == null || templates.length == 0)
        {
            throw new IllegalArgumentException("Item ingredient alternatives cannot be empty");
        }
        return new Alternatives(amount, nbtSensitive, Arrays.asList(templates));
    }

    public final int getAmount()
    {
        return amount;
    }

    public final boolean isNbtSensitive()
    {
        return nbtSensitive;
    }

    public abstract boolean testType(ItemStack stack);

    public final boolean hasRequiredAmount(ItemStack stack)
    {
        return testType(stack) && stack.stackSize >= amount;
    }

    /** Ore ingredients reflect the Ore Dictionary at call time. */
    public abstract Collection<Item> getIndexedItems();

    /** Returns defensive copies suitable for recipe viewers. */
    public abstract List<ItemStack> getRepresentations();

    protected final boolean matchesTemplate(ItemStack template, ItemStack stack)
    {
        if (isEmpty(stack) || template.getItem() != stack.getItem()
            || template.getItemDamage() != OreDictionary.WILDCARD_VALUE
            && template.getItemDamage() != stack.getItemDamage())
        {
            return false;
        }
        return !nbtSensitive || ItemStack.areItemStackTagsEqual(template, stack);
    }

    private static Set<Item> newIdentitySet()
    {
        return Collections.newSetFromMap(new IdentityHashMap<Item, Boolean>());
    }

    private static boolean isEmpty(ItemStack stack)
    {
        return stack == null || stack.getItem() == null || stack.stackSize <= 0;
    }

    private static void requireNonEmpty(ItemStack stack, String message)
    {
        Objects.requireNonNull(stack, "template");
        if (isEmpty(stack))
        {
            throw new IllegalArgumentException(message);
        }
    }

    private static final class Direct extends ItemIngredientCE
    {
        private final ItemStack template;
        private final Collection<Item> indexedItems;

        private Direct(ItemStack template, boolean nbtSensitive)
        {
            super(template.stackSize, nbtSensitive);
            this.template = template;
            this.indexedItems = Collections.singleton(template.getItem());
        }

        @Override
        public boolean testType(ItemStack stack)
        {
            return matchesTemplate(template, stack);
        }

        @Override
        public Collection<Item> getIndexedItems()
        {
            return indexedItems;
        }

        @Override
        public List<ItemStack> getRepresentations()
        {
            return Collections.singletonList(template.copy());
        }
    }

    private static final class Ore extends ItemIngredientCE
    {
        private final String oreName;
        private final OreLookup lookup;

        private Ore(String oreName, int amount, boolean nbtSensitive, OreLookup lookup)
        {
            super(amount, nbtSensitive);
            this.oreName = oreName;
            this.lookup = lookup;
        }

        @Override
        public boolean testType(ItemStack stack)
        {
            if (isEmpty(stack))
            {
                return false;
            }
            for (ItemStack representation : lookup.getOres(oreName))
            {
                if (!isEmpty(representation) && matchesTemplate(representation, stack))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Collection<Item> getIndexedItems()
        {
            Set<Item> items = newIdentitySet();
            for (ItemStack representation : lookup.getOres(oreName))
            {
                if (!isEmpty(representation))
                {
                    items.add(representation.getItem());
                }
            }
            return items;
        }

        @Override
        public List<ItemStack> getRepresentations()
        {
            List<ItemStack> copies = new ArrayList<ItemStack>();
            for (ItemStack representation : lookup.getOres(oreName))
            {
                if (!isEmpty(representation))
                {
                    ItemStack copy = representation.copy();
                    copy.stackSize = getAmount();
                    copies.add(copy);
                }
            }
            return Collections.unmodifiableList(copies);
        }
    }

    private static final class Alternatives extends ItemIngredientCE
    {
        private final List<ItemStack> templates;
        private final Collection<Item> indexedItems;

        private Alternatives(int amount, boolean nbtSensitive, Collection<ItemStack> templates)
        {
            super(amount, nbtSensitive);
            List<ItemStack> copies = new ArrayList<ItemStack>();
            Set<Item> items = newIdentitySet();
            for (ItemStack template : templates)
            {
                requireNonEmpty(template, "Item ingredient alternative cannot be empty");
                ItemStack copy = template.copy();
                copy.stackSize = amount;
                copies.add(copy);
                items.add(copy.getItem());
            }
            this.templates = Collections.unmodifiableList(copies);
            this.indexedItems = Collections.unmodifiableSet(items);
        }

        @Override
        public boolean testType(ItemStack stack)
        {
            for (ItemStack template : templates)
            {
                if (matchesTemplate(template, stack))
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Collection<Item> getIndexedItems()
        {
            return indexedItems;
        }

        @Override
        public List<ItemStack> getRepresentations()
        {
            List<ItemStack> copies = new ArrayList<ItemStack>(templates.size());
            for (ItemStack template : templates)
            {
                copies.add(template.copy());
            }
            return Collections.unmodifiableList(copies);
        }
    }
}
