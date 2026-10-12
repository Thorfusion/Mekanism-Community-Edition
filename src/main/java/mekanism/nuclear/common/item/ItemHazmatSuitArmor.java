package mekanism.nuclear.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.nuclear.common.MekanismNuclear;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

/** Source-faithful zero-armor Hazmat equipment. */
public final class ItemHazmatSuitArmor extends ItemArmor
{
    private static final ArmorMaterial HAZMAT_MATERIAL =
          EnumHelper.addArmorMaterial("MEKANISM_NUCLEAR_HAZMAT", 0,
                new int[] {0, 0, 0, 0}, 0);

    private final String registryName;
    private final double shielding;

    public ItemHazmatSuitArmor(String registryName, String translationKey,
          int armorType, double shielding)
    {
        super(HAZMAT_MATERIAL, 0, armorType);
        if (shielding < 0 || shielding > 1)
        {
            throw new IllegalArgumentException(
                  "Hazmat shielding must be between zero and one");
        }
        this.registryName = registryName;
        this.shielding = shielding;
        setUnlocalizedName(translationKey);
    }

    public String getRegistryName()
    {
        return registryName;
    }

    public double getRadiationShielding()
    {
        return shielding;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.uncommon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(MekanismNuclear.MODID + ":"
              + registryName);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot,
          String type)
    {
        int layer = slot == 2 ? 2 : 1;
        return MekanismNuclear.MODID
              + ":textures/models/armor/hazmat_layer_" + layer + ".png";
    }
}
