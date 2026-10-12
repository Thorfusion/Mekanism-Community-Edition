package mekanism.ultimate.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.common.Mekanism;
import mekanism.ultimate.common.MekanismUltimate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

/** Compact storage block for nine units of legacy Bio Fuel. */
public final class BlockBioFuel extends Block
{
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;

    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;

    public BlockBioFuel()
    {
        super(Material.grass);
        setBlockName("BioFuelBlock");
        setHardness(0.5F);
        setStepSound(soundTypeGrass);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        String prefix = MekanismUltimate.MODID.toLowerCase()
              + ":bio_fuel_block_";
        blockIcon = register.registerIcon(prefix + "side");
        topIcon = register.registerIcon(prefix + "top");
        bottomIcon = register.registerIcon(prefix + "bottom");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        return side == 0 ? bottomIcon : side == 1 ? topIcon : blockIcon;
    }
}
