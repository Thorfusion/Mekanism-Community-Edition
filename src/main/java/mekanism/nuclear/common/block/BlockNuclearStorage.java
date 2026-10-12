package mekanism.nuclear.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.nuclear.common.MekanismNuclear;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

/** Dense Fluorite storage block with the stable material properties. */
public class BlockNuclearStorage extends Block
{
    public BlockNuclearStorage()
    {
        super(Material.rock);
        setHardness(5F);
        setResistance(9F);
        setStepSound(soundTypeStone);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        blockIcon = register.registerIcon(MekanismNuclear.MODID + ":block_fluorite");
    }
}
