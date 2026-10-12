package mekanism.nuclear.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.ultimate.common.block.ILegacyJsonModelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/** Directional internal energy coil used by the SPS structure. */
public class BlockSuperchargedCoil extends Block
      implements ILegacyJsonModelBlock
{
    private int renderType;

    public BlockSuperchargedCoil()
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
        blockIcon = register.registerIcon(MekanismNuclear.MODID
              + ":model/sps/supercharged_coil");
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side,
          float hitX, float hitY, float hitZ, int metadata)
    {
        return side >= 0 && side < 6 ? side : ForgeDirection.UP.ordinal();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y,
          int z)
    {
        ForgeDirection side = ForgeDirection.getOrientation(
              world.getBlockMetadata(x, y, z));
        if (side == ForgeDirection.EAST || side == ForgeDirection.WEST)
        {
            setBlockBounds(0, 0.25F, 0.25F, 1, 0.75F, 0.75F);
        }
        else if (side == ForgeDirection.NORTH || side == ForgeDirection.SOUTH)
        {
            setBlockBounds(0.25F, 0.25F, 0, 0.75F, 0.75F, 1);
        }
        else
        {
            setBlockBounds(0.25F, 0, 0.25F, 0.75F, 1, 0.75F);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
          int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x,
          int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return renderType;
    }

    @Override
    public String getLegacyModel(int metadata)
    {
        return MekanismNuclear.MODID + ":block/sps/supercharged_coil";
    }

    @Override
    public int getLegacyModelRotationX(IBlockAccess world, int x, int y,
          int z, int metadata)
    {
        return metadata == ForgeDirection.DOWN.ordinal() ? 180
              : metadata >= ForgeDirection.NORTH.ordinal() ? 270 : 0;
    }

    @Override
    public int getLegacyModelRotationY(IBlockAccess world, int x, int y,
          int z, int metadata)
    {
        return metadata == ForgeDirection.NORTH.ordinal() ? 180
              : metadata == ForgeDirection.EAST.ordinal() ? 270
              : metadata == ForgeDirection.WEST.ordinal() ? 90 : 0;
    }

    @Override
    public void setLegacyRenderType(int renderType)
    {
        this.renderType = renderType;
    }
}
