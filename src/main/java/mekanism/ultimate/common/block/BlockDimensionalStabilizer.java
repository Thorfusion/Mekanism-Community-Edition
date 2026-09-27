package mekanism.ultimate.common.block;

import javax.annotation.Nonnull;
import mekanism.api.IMekWrench;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.common.block.states.BlockStateFacing;
import mekanism.common.block.states.BlockStateMachine;
import mekanism.common.integration.wrenches.Wrenches;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockDimensionalStabilizer extends BlockMekanismContainer {

    public BlockDimensionalStabilizer() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(8F);
        setSoundType(SoundType.METAL);
        setCreativeTab(Mekanism.tabMekanism);
        setDefaultState(blockState.getBaseState()
              .withProperty(BlockStateFacing.facingProperty, EnumFacing.NORTH)
              .withProperty(BlockStateMachine.activeProperty, false));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockStateFacing.facingProperty, BlockStateMachine.activeProperty);
    }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) { return getDefaultState(); }

    @Override
    public int getMetaFromState(IBlockState state) { return 0; }

    @Nonnull
    @Override
    @Deprecated
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityDimensionalStabilizer
              ? state.withProperty(BlockStateFacing.facingProperty, ((TileEntityDimensionalStabilizer) tile).facing)
                    .withProperty(BlockStateMachine.activeProperty, ((TileEntityDimensionalStabilizer) tile).getActive())
              : state;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
          EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBasicBlock) {
            ((TileEntityBasicBlock) tile).setFacing(placer.getHorizontalFacing().getOpposite());
            ((TileEntityBasicBlock) tile).redstone = world.getRedstonePowerFromNeighbors(pos) > 0;
        }
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos from) {
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && tile instanceof TileEntityBasicBlock) {
            ((TileEntityBasicBlock) tile).onNeighborChange(block);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
          EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityDimensionalStabilizer)) return false;
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty()) {
            IMekWrench wrench = Wrenches.getHandler(held);
            if (wrench != null) {
                RayTraceResult ray = new RayTraceResult(new Vec3d(hitX, hitY, hitZ), side, pos);
                if (wrench.canUseWrench(player, hand, held, ray)) {
                    if (!SecurityUtils.canAccess(player, tile)) {
                        SecurityUtils.displayNoAccess(player);
                        return true;
                    }
                    wrench.wrenchUsed(player, hand, held, ray);
                    if (player.isSneaking()) MekanismUtils.dismantleBlock(this, state, world, pos);
                    else ((TileEntityDimensionalStabilizer) tile).setFacing(
                          ((TileEntityDimensionalStabilizer) tile).facing.rotateY());
                    return true;
                }
            }
        }
        if (!player.isSneaking()) {
            if (!world.isRemote) {
                if (SecurityUtils.canAccess(player, tile)) {
                    player.openGui(MekanismUltimate.instance, 7, world, pos.getX(), pos.getY(), pos.getZ());
                } else SecurityUtils.displayNoAccess(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileEntityDimensionalStabilizer();
    }

    @Nonnull
    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world,
          @Nonnull BlockPos pos) {
        return new ItemStack(this);
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityDimensionalStabilizer) {
            InventoryHelper.dropInventoryItems(world, pos, (TileEntityDimensionalStabilizer) tile);
        }
        super.breakBlock(world, pos, state);
    }

    @Nonnull
    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state) { return EnumBlockRenderType.MODEL; }
}
