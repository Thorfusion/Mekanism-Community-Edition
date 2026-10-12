package mekanism.ultimate.common.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.common.Mekanism;
import mekanism.common.tile.TileEntityBasicBlock;
import mekanism.ultimate.common.tile.TileEntityIndustrialAlarm;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Six-direction redstone alarm with a narrow model-matching outline. */
public final class BlockIndustrialAlarm extends BlockContainer
      implements ILegacyJsonModelBlock
{
    @SideOnly(Side.CLIENT)
    private IIcon activeIcon;

    private int renderType;

    public BlockIndustrialAlarm()
    {
        super(Material.iron);
        setBlockName("IndustrialAlarm");
        setHardness(2F);
        setResistance(2.4F);
        setStepSound(soundTypeMetal);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        blockIcon = register.registerIcon(
              "mekanismultimate:models/industrial_alarm");
        activeIcon = register.registerIcon(
              "mekanismultimate:models/industrial_alarm_active");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityIndustrialAlarm
              && ((TileEntityIndustrialAlarm)tile).isActive()
              ? activeIcon : blockIcon;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side,
          float hitX, float hitY, float hitZ, int metadata)
    {
        return side >= 0 && side < 6 ? side : 1;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
          EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityBasicBlock)
        {
            TileEntityBasicBlock alarm = (TileEntityBasicBlock)tile;
            alarm.setFacing((short)world.getBlockMetadata(x, y, z));
            alarm.redstone = world.isBlockIndirectlyGettingPowered(x, y, z);
            alarm.onPowerChange();
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileEntityBasicBlock)
        {
            ((TileEntityBasicBlock)tile).onAdded();
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z,
          Block block)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TileEntityBasicBlock)
        {
            ((TileEntityBasicBlock)tile).onNeighborChange(block);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityIndustrialAlarm();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y,
          int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        int facing = tile instanceof TileEntityBasicBlock
              ? ((TileEntityBasicBlock)tile).facing
              : world.getBlockMetadata(x, y, z);
        if (facing < 2)
        {
            setBlockBounds(5F / 16F, 0F, 5F / 16F,
                  11F / 16F, 1F, 11F / 16F);
        }
        else if (facing < 4)
        {
            setBlockBounds(5F / 16F, 5F / 16F, 0F,
                  11F / 16F, 11F / 16F, 1F);
        }
        else
        {
            setBlockBounds(0F, 5F / 16F, 5F / 16F,
                  1F, 11F / 16F, 11F / 16F);
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
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityIndustrialAlarm
              && ((TileEntityIndustrialAlarm)tile).isActive() ? 15 : 0;
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
        return "mekanismultimate:block/industrial_alarm";
    }

    @Override
    public String getLegacyModel(IBlockAccess world, int x, int y, int z,
          int metadata)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityIndustrialAlarm
              && ((TileEntityIndustrialAlarm)tile).isActive()
                    ? "mekanismultimate:block/industrial_alarm_active"
                    : getLegacyModel(metadata);
    }

    @Override
    public String[] getLegacyModels()
    {
        return new String[] {
              "mekanismultimate:block/industrial_alarm",
              "mekanismultimate:block/industrial_alarm_active"
        };
    }

    @Override
    public int getLegacyModelRotationX(IBlockAccess world, int x, int y,
          int z, int metadata)
    {
        int facing = getFacing(world, x, y, z, metadata);
        return facing == 0 ? 180 : facing >= 2 ? 90 : 0;
    }

    @Override
    public int getLegacyModelRotationY(IBlockAccess world, int x, int y,
          int z, int metadata)
    {
        switch (getFacing(world, x, y, z, metadata))
        {
            case 3:
                return 180;
            case 5:
                return 90;
            case 4:
                return -90;
            default:
                return 0;
        }
    }

    private static int getFacing(IBlockAccess world, int x, int y, int z,
          int metadata)
    {
        TileEntity tile = world == null ? null : world.getTileEntity(x, y, z);
        int facing = tile instanceof TileEntityBasicBlock
              ? ((TileEntityBasicBlock)tile).facing : metadata;
        return facing >= 0 && facing < 6 ? facing : 1;
    }

    @Override
    public void setLegacyRenderType(int renderType)
    {
        this.renderType = renderType;
    }
}
