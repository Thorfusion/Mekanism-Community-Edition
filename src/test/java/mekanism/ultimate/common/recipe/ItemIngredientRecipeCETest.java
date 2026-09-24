package mekanism.ultimate.common.recipe;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.api.recipe.ingredient.ItemIngredientCE;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.recipe.key.ItemIdentityKeyCE;
import mekanism.ultimate.common.recipe.type.ItemToChemicalRecipeCE;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.BeforeClass;
import org.junit.Test;

public class ItemIngredientRecipeCETest {

    private static final IChemicalTypeCE OUTPUT = new TestChemicalType("test:output");

    @BeforeClass
    public static void bootstrapMinecraftRegistries() {
        Bootstrap.register();
    }

    @Test
    public void directIngredientHonorsMetadataCountAndOptionalNbt() {
        Item item = Items.DIAMOND;
        ItemStack template = new ItemStack(item, 3, 4);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("grade", "pure");
        template.setTagCompound(tag);
        ItemIngredientCE sensitive = ItemIngredientCE.direct(template, true);

        ItemStack matching = template.copy();
        matching.setCount(5);
        assertTrue(sensitive.testType(matching));
        assertTrue(sensitive.hasRequiredAmount(matching));

        ItemStack tooSmall = matching.copy();
        tooSmall.setCount(2);
        assertTrue(sensitive.testType(tooSmall));
        assertFalse(sensitive.hasRequiredAmount(tooSmall));

        ItemStack wrongMetadata = matching.copy();
        wrongMetadata.setItemDamage(5);
        assertFalse(sensitive.testType(wrongMetadata));

        ItemStack wrongNbt = new ItemStack(item, 5, 4);
        assertFalse(sensitive.testType(wrongNbt));
        assertTrue(ItemIngredientCE.direct(template, false).testType(wrongNbt));

        ItemStack wildcard = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
        assertTrue(ItemIngredientCE.direct(wildcard, false).testType(wrongMetadata));
    }

    @Test
    public void oreIngredientUsesCacheAndCanBeRebuiltForLateEntries() {
        String oreName = "ingotUltimateTaskFour";
        Item firstItem = Items.IRON_INGOT;
        OreDictionary.registerOre(oreName, new ItemStack(firstItem, 1, OreDictionary.WILDCARD_VALUE));
        ItemIngredientCE ingredient = ItemIngredientCE.ore(oreName, 2);
        ItemToChemicalRecipeCE recipe = new ItemToChemicalRecipeCE(new ResourceLocation("test", "ore_to_chemical"), ingredient,
              new ChemicalStackCE(OUTPUT, 10));
        CachedRecipeManagerCE<ItemStack, ItemIdentityKeyCE, ItemToChemicalRecipeCE> manager = RecipeManagersCE.itemToChemical();
        manager.add(recipe);

        assertSame(recipe, manager.findFirst(new ItemStack(firstItem, 1, 9)));
        assertTrue(ingredient.hasRequiredAmount(new ItemStack(firstItem, 2, 3)));

        Item lateItem = Items.GOLD_INGOT;
        OreDictionary.registerOre(oreName, new ItemStack(lateItem));
        assertNull(manager.findFirst(new ItemStack(lateItem)));
        manager.rebuildCache();
        assertSame(recipe, manager.findFirst(new ItemStack(lateItem)));
    }

    private static final class TestChemicalType implements IChemicalTypeCE {

        private final String name;

        private TestChemicalType(String name) {
            this.name = name;
        }

        @Override
        public String getRegistryName() {
            return name;
        }

        @Override
        public ChemicalKind getKind() {
            return ChemicalKind.GAS;
        }

        @Override
        public boolean isRadioactive() {
            return false;
        }
    }
}
