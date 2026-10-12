package mekanism.mekasuit.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.mekasuit.common.MekanismMekaSuit;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

/** Simple item with MekaSuit-owned registration and texture identities. */
public class ItemMekaSuit extends Item implements IMekaSuitItem
{
    private final String registryName;

    public ItemMekaSuit(String registryName, String translationKey)
    {
        this.registryName = registryName;
        setUnlocalizedName(translationKey);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(MekanismMekaSuit.MODID + ":"
              + registryName);
    }
}
