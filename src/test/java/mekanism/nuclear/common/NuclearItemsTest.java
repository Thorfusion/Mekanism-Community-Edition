package mekanism.nuclear.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.HashSet;
import java.util.Set;
import mekanism.nuclear.common.item.ItemNuclearMaterial;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.junit.BeforeClass;
import org.junit.Test;

public class NuclearItemsTest {

    @BeforeClass
    public static void registerOreNames() {
        Bootstrap.register();
        NuclearOreDictionary.register();
    }

    @Test
    public void stableRegistryNamesAreUniqueAndModuleOwned() {
        Set<ResourceLocation> names = new HashSet<>();
        for (ItemNuclearMaterial item : NuclearItems.all()) {
            assertEquals(MekanismNuclear.MODID, item.getRegistryName().getNamespace());
            names.add(item.getRegistryName());
        }
        assertEquals(5, names.size());
        assertEquals(new ResourceLocation(MekanismNuclear.MODID, "yellow_cake_uranium"),
              NuclearItems.YellowCakeUranium.getRegistryName());
        assertEquals(EnumRarity.UNCOMMON,
              NuclearItems.YellowCakeUranium.getRarity(new ItemStack(NuclearItems.YellowCakeUranium)));
    }

    @Test
    public void oreDictionaryRegistrationIsIdempotent() {
        NuclearOreDictionary.register();
        assertSingleOwnedEntry(NuclearOreDictionary.INGOT_URANIUM, NuclearItems.UraniumIngot);
        assertSingleOwnedEntry(NuclearOreDictionary.DUST_URANIUM, NuclearItems.UraniumDust);
        assertSingleOwnedEntry(NuclearOreDictionary.GEM_FLUORITE, NuclearItems.FluoriteGem);
        assertSingleOwnedEntry(NuclearOreDictionary.DUST_FLUORITE, NuclearItems.FluoriteDust);
    }

    private static void assertSingleOwnedEntry(String oreName, ItemNuclearMaterial expected) {
        int matches = 0;
        for (ItemStack stack : OreDictionary.getOres(oreName, false)) {
            if (stack.getItem() == expected) {
                matches++;
                assertSame(expected, stack.getItem());
            }
        }
        assertEquals(1, matches);
    }
}
