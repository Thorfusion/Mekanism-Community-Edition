package mekanism.nuclear.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.nuclear.common.MekanismNuclear;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/** A Nuclear material item with a stable registration and texture identity. */
public class ItemNuclearMaterial extends Item
{
    private final String registryName;
    private final int tint;
    private final EnumRarity rarity;

    public ItemNuclearMaterial(String registryName, String translationKey, int tint)
    {
        this(registryName, translationKey, tint, EnumRarity.common);
    }

    public ItemNuclearMaterial(String registryName, String translationKey, int tint,
          EnumRarity rarity)
    {
        this.registryName = registryName;
        this.tint = tint;
        this.rarity = rarity;
        setUnlocalizedName(translationKey);
    }

    public String getRegistryName()
    {
        return registryName;
    }

    public int getTint()
    {
        return tint;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return rarity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(MekanismNuclear.MODID + ":" + registryName);
    }
}
