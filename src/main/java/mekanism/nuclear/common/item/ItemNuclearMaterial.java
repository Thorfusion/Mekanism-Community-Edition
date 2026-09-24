package mekanism.nuclear.common.item;

import javax.annotation.Nonnull;
import mekanism.common.item.ItemMekanism;
import mekanism.nuclear.common.MekanismNuclear;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/** A simple Nuclear material item with a client tint and stable registry identity. */
public class ItemNuclearMaterial extends ItemMekanism {

    private final int tint;
    private final EnumRarity rarity;

    public ItemNuclearMaterial(String registryName, String translationKey, int tint) {
        this(registryName, translationKey, tint, EnumRarity.COMMON);
    }

    public ItemNuclearMaterial(String registryName, String translationKey, int tint, EnumRarity rarity) {
        this.tint = tint;
        this.rarity = rarity;
        setRegistryName(new ResourceLocation(MekanismNuclear.MODID, registryName));
        setTranslationKey(translationKey);
    }

    public int getTint() {
        return tint;
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return rarity;
    }
}
