package mekanism.ultimate.common;

import java.util.Collections;
import java.util.List;
import mekanism.ultimate.common.item.ItemCanteen;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

/** Items owned and packaged by the Ultimate module. */
public final class UltimateItems {

    public static final ItemCanteen Canteen = init(new ItemCanteen(), "canteen", "Canteen");

    private static final List<Item> ITEMS = Collections.singletonList(Canteen);

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
