package mekanism.nuclear.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mekanism.nuclear.common.item.ItemNuclearMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

/** Items owned by the separately packaged Nuclear module. */
public final class NuclearItems {

    public static final ItemNuclearMaterial UraniumIngot =
          new ItemNuclearMaterial("ingot_uranium", "UraniumIngot", 0x46664F);
    public static final ItemNuclearMaterial UraniumDust =
          new ItemNuclearMaterial("dust_uranium", "UraniumDust", 0x46664F);
    public static final ItemNuclearMaterial FluoriteGem =
          new ItemNuclearMaterial("fluorite_gem", "FluoriteGem", 0xE4DFF2);
    public static final ItemNuclearMaterial FluoriteDust =
          new ItemNuclearMaterial("dust_fluorite", "FluoriteDust", 0xE4DFF2);
    public static final ItemNuclearMaterial YellowCakeUranium =
          new ItemNuclearMaterial("yellow_cake_uranium", "YellowCakeUranium", 0xE1F573, EnumRarity.UNCOMMON);

    private static final List<ItemNuclearMaterial> ITEMS = Collections.unmodifiableList(Arrays.asList(
          UraniumIngot, UraniumDust, FluoriteGem, FluoriteDust, YellowCakeUranium));

    private NuclearItems() {
    }

    public static void registerItems(IForgeRegistry<Item> registry) {
        for (Item item : ITEMS) {
            registry.register(item);
        }
    }

    public static List<ItemNuclearMaterial> all() {
        return ITEMS;
    }
}
