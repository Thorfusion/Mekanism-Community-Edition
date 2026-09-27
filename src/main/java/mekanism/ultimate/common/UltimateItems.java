package mekanism.ultimate.common;

import java.util.Arrays;
import java.util.List;
import mekanism.ultimate.common.item.ItemCanteen;
import mekanism.ultimate.common.item.ItemArmoredFreeRunners;
import mekanism.ultimate.common.item.ItemHDPEElytra;
import mekanism.ultimate.common.item.ItemStoneGeneratorUpgrade;
import mekanism.common.Upgrade;
import mekanism.common.item.ItemUpgrade;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

/** Items owned and packaged by the Ultimate module. */
public final class UltimateItems {

    public static final ItemCanteen Canteen = init(new ItemCanteen(), "canteen", "Canteen");
    public static final ItemHDPEElytra HDPEElytra = init(
          new ItemHDPEElytra(), "hdpe_elytra", "HDPEElytra");
    public static final ItemArmoredFreeRunners ArmoredFreeRunners = init(
          new ItemArmoredFreeRunners(), "armored_free_runners", "ArmoredFreeRunners");
    public static final Item StoneGeneratorUpgrade = init(new ItemStoneGeneratorUpgrade(),
          "upgrade_stone_generator", "StoneGeneratorUpgrade");
    /** Modern name and texture over the save-compatible legacy GAS upgrade type. */
    public static final Item ChemicalUpgrade = init(new ItemUpgrade(Upgrade.GAS),
          "upgrade_chemical", "ChemicalUpgrade");

    private static final List<Item> ITEMS = Arrays.asList(Canteen, HDPEElytra, ArmoredFreeRunners,
          StoneGeneratorUpgrade, ChemicalUpgrade);

    private UltimateItems() {
    }

    public static void registerItems(IForgeRegistry<Item> registry) {
        for (Item item : ITEMS) {
            registry.register(item);
        }
    }

    public static List<Item> allRegistered() {
        return ITEMS;
    }

    private static <ITEM extends Item> ITEM init(ITEM item, String registryName, String translationKey) {
        item.setRegistryName(new ResourceLocation(MekanismUltimate.MODID, registryName));
        item.setTranslationKey(translationKey);
        return item;
    }
}
