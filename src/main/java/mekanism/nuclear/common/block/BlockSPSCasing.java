package mekanism.nuclear.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import mekanism.common.CTMData;
import mekanism.common.Mekanism;
import mekanism.common.base.IBlockCTM;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.IBlockAccess;

/** Reinforced frame and face block for the Supercritical Phase Shifter. */
public class BlockSPSCasing extends Block implements IBlockCTM
{
    private CTMData ctm;

    public BlockSPSCasing()
    {
        super(Material.iron);
        setHardness(3.5F);
        setResistance(16F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        ctm = new CTMData(MekanismNuclear.MODID + ":sps_casing",
              this, Arrays.asList(0)).addOtherBlockConnectivities(
                    NuclearBlocks.SPSPort, Arrays.asList(0))
              .registerIcons(register);
        blockIcon = ctm.getIcon(0);
    }

    @Override
    public int getRenderType()
    {
        return Mekanism.proxy.CTM_RENDER_ID;
    }

    @Override
    public CTMData getCTMData(IBlockAccess world, int x, int y, int z,
          int metadata)
    {
        return ctm;
    }

    @Override
    public boolean shouldRenderBlock(IBlockAccess world, int x, int y,
          int z, int metadata)
    {
        return true;
    }
}
