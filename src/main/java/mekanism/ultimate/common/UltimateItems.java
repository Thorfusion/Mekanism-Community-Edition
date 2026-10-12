package mekanism.ultimate.common;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mekanism.common.Mekanism;
import mekanism.ultimate.common.item.ItemArmoredFreeRunners;
import mekanism.ultimate.common.item.ItemCanteen;
import mekanism.ultimate.common.item.ItemHDPEElytra;
import mekanism.ultimate.common.item.ItemChemicalUpgrade;
import mekanism.ultimate.common.item.ItemStoneGeneratorUpgrade;
import net.minecraft.item.Item;

/** Items owned and packaged by the Ultimate module. */
public final class UltimateItems
{
    public static final ItemCanteen Canteen = new ItemCanteen();
    public static final ItemHDPEElytra HDPEElytra = new ItemHDPEElytra();
    public static final ItemArmoredFreeRunners ArmoredFreeRunners =
          new ItemArmoredFreeRunners();
    public static final ItemStoneGeneratorUpgrade StoneGeneratorUpgrade =
          new ItemStoneGeneratorUpgrade();
    public static final ItemChemicalUpgrade ChemicalUpgrade =
          new ItemChemicalUpgrade();

    private static final List<Item> ITEMS = Collections.unmodifiableList(
          Arrays.<Item>asList(Canteen, HDPEElytra, ArmoredFreeRunners,
                StoneGeneratorUpgrade, ChemicalUpgrade));

    private UltimateItems()
    {
    }

    public static void register()
    {
        for (Item item : ITEMS)
        {
            item.setCreativeTab(Mekanism.tabMekanism);
        }
        GameRegistry.registerItem(Canteen, "canteen");
        GameRegistry.registerItem(HDPEElytra, "hdpe_elytra");
        GameRegistry.registerItem(ArmoredFreeRunners,
              "armored_free_runners");
        GameRegistry.registerItem(StoneGeneratorUpgrade,
              "upgrade_stone_generator");
        GameRegistry.registerItem(ChemicalUpgrade, "upgrade_chemical");
    }

    public static List<Item> allRegistered()
    {
        return ITEMS;
    }
}
