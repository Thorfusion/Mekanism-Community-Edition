package mekanism.ultimate.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.common.Upgrade;
import mekanism.common.item.ItemUpgrade;
import mekanism.ultimate.common.MekanismUltimate;
import net.minecraft.client.renderer.texture.IIconRegister;

/** Modern name for the legacy gas-efficiency upgrade type. */
public final class ItemChemicalUpgrade extends ItemUpgrade
{
    public ItemChemicalUpgrade()
    {
        super(Upgrade.GAS);
        setUnlocalizedName("ChemicalUpgrade");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(
              MekanismUltimate.MODID.toLowerCase()
                    + ":upgrade_chemical");
    }
}
