package mekanism.ultimate.common.block;

import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.common.block.states.BlockStateFacing;
import mekanism.common.block.states.BlockStateMachine;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.ultimate.common.tile.TileEntityIndustrialAlarm;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Six-direction redstone alarm with a narrow model-matching outline. */
public final class BlockIndustrialAlarm extends BlockMekanismContainer {

    private static final AxisAlignedBB VERTICAL_BOUNDS =
          new AxisAlignedBB(5D / 16D, 0, 5D / 16D, 11D / 16D, 1, 11D / 16D);
    private static final AxisAlignedBB NORTH_SOUTH_BOUNDS =
          new AxisAlignedBB(5D / 16D, 5D / 16D, 0, 11D / 16D, 11D / 16D, 1);
    private static final AxisAlignedBB EAST_WEST_BOUNDS =
          new AxisAlignedBB(0, 5D / 16D, 5D / 16D, 1, 11D / 16D, 11D / 16D);

    public BlockIndustrialAlarm() {
        super(Material.IRON);
        setHardness(2F);
        setResistance(2.4F);
        setSoundType(SoundType.METAL);
        setCreativeTab(Mekanism.tabMekanism);
        setDefaultState(blockState.getBaseState()
              .withProperty(BlockStateFacing.facingProperty, EnumFacing.UP)
              .withProperty(BlockStateMachine.activeProperty, false));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockStateFacing.facingProperty,
              BlockStateMachine.activeProperty);
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
          float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(BlockStateFacing.facingProperty, facing);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
          EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBasicBlock) {
            ((TileEntityBasicBlock) tile).setFacing(state.getValue(BlockStateFacing.facingProperty));
            ((TileEntityBasicBlock) tile).redstone = world.getRedstonePowerFromNeighbors(pos) > 0;
            ((TileEntityBasicBlock) tile).onPowerChange();
        }
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos,
          Block neighborBlock, BlockPos neighborPos) {
        if (!world.isRemote && world.getTileEntity(pos) instanceof TileEntityBasicBlock) {
            ((TileEntityBasicBlock) world.getTileEntity(pos)).onNeighborChange(neighborBlock);
        }
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityIndustrialAlarm) {
            TileEntityIndustrialAlarm alarm = (TileEntityIndustrialAlarm) tile;
            return state.withProperty(BlockStateFacing.facingProperty, alarm.facing)
                  .withProperty(BlockStateMachine.activeProperty, alarm.isActive());
        }
        return state;
    }

    @Nonnull
    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing facing = state.getValue(BlockStateFacing.facingProperty);
        return facing.getAxis() == EnumFacing.Axis.Y ? VERTICAL_BOUNDS
              : facing.getAxis() == EnumFacing.Axis.Z ? NORTH_SOUTH_BOUNDS : EAST_WEST_BOUNDS;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getActualState(state, world, pos).getValue(BlockStateMachine.activeProperty) ? 15 : 0;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileEntityIndustrialAlarm();
    }

    @Nonnull
    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world,
          @Nonnull BlockPos pos) {
        return new ItemStack(this);
    }

    @Nonnull
    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }
}
