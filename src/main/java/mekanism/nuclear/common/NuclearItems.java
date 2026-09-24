package mekanism.nuclear.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mekanism.nuclear.common.item.ItemDosimeter;
import mekanism.nuclear.common.item.ItemGeigerCounter;
import mekanism.nuclear.common.item.ItemHazmatSuitArmor;
import mekanism.nuclear.common.item.ItemNuclearMaterial;
import net.minecraft.inventory.EntityEquipmentSlot;
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
    public static final ItemNuclearMaterial PlutoniumPellet =
          new ItemNuclearMaterial("pellet_plutonium", "PlutoniumPellet", 0xAAAAAA);
    public static final ItemNuclearMaterial PoloniumPellet =
          new ItemNuclearMaterial("pellet_polonium", "PoloniumPellet", 0x5555FF);
    public static final ItemNuclearMaterial AntimatterPellet =
          new ItemNuclearMaterial("pellet_antimatter", "AntimatterPellet", 0xAA00AA);
    public static final ItemNuclearMaterial ReprocessedFissileFragment =
          new ItemNuclearMaterial("reprocessed_fissile_fragment", "ReprocessedFissileFragment", 0x8AA34A, EnumRarity.RARE);
    public static final ItemGeigerCounter GeigerCounter = new ItemGeigerCounter();
    public static final ItemDosimeter Dosimeter = new ItemDosimeter();
    public static final ItemHazmatSuitArmor HazmatMask = new ItemHazmatSuitArmor(
          "hazmat_mask", "HazmatMask", EntityEquipmentSlot.HEAD, 0.25D);
    public static final ItemHazmatSuitArmor HazmatGown = new ItemHazmatSuitArmor(
          "hazmat_gown", "HazmatGown", EntityEquipmentSlot.CHEST, 0.40D);
    public static final ItemHazmatSuitArmor HazmatPants = new ItemHazmatSuitArmor(
          "hazmat_pants", "HazmatPants", EntityEquipmentSlot.LEGS, 0.20D);
    public static final ItemHazmatSuitArmor HazmatBoots = new ItemHazmatSuitArmor(
          "hazmat_boots", "HazmatBoots", EntityEquipmentSlot.FEET, 0.15D);

    private static final List<ItemNuclearMaterial> ITEMS = Collections.unmodifiableList(Arrays.asList(
          UraniumIngot, UraniumDust, FluoriteGem, FluoriteDust, YellowCakeUranium,
          PlutoniumPellet, PoloniumPellet, AntimatterPellet, ReprocessedFissileFragment));
    private static final List<Item> REGISTERED_ITEMS = Collections.unmodifiableList(Arrays.asList(
          UraniumIngot, UraniumDust, FluoriteGem, FluoriteDust, YellowCakeUranium,
          PlutoniumPellet, PoloniumPellet, AntimatterPellet, ReprocessedFissileFragment,
          GeigerCounter, Dosimeter, HazmatMask, HazmatGown, HazmatPants, HazmatBoots));

    private NuclearItems() {
    }

    public static void registerItems(IForgeRegistry<Item> registry) {
        for (Item item : REGISTERED_ITEMS) {
            registry.register(item);
        }
    }

    public static List<ItemNuclearMaterial> all() {
        return ITEMS;
    }

    public static List<Item> allRegistered() {
        return REGISTERED_ITEMS;
    }
}
