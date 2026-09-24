package mekanism.nuclear.common.block;

import mekanism.common.Mekanism;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Directional internal energy coil used by the SPS structure. */
public class BlockSuperchargedCoil extends Block {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    private static final AxisAlignedBB VERTICAL = new AxisAlignedBB(0.25D, 0, 0.25D, 0.75D, 1, 0.75D);
    private static final AxisAlignedBB NORTH_SOUTH = new AxisAlignedBB(0.25D, 0.25D, 0, 0.75D, 0.75D, 1);
    private static final AxisAlignedBB EAST_WEST = new AxisAlignedBB(0, 0.25D, 0.25D, 1, 0.75D, 0.75D);

    public BlockSuperchargedCoil() {
        super(Material.IRON);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        setHardness(3.5F);
        setResistance(16F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
          float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta);
        return getDefaultState().withProperty(FACING, facing == null ? EnumFacing.UP : facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rotation) {
        return state.withProperty(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(FACING).getAxis()) {
            case X:
                return EAST_WEST;
            case Z:
                return NORTH_SOUTH;
            default:
                return VERTICAL;
        }
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
