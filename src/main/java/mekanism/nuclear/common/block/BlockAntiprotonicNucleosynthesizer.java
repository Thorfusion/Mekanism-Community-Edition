package mekanism.nuclear.common.block;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.IMekWrench;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.common.block.states.BlockStateFacing;
import mekanism.common.block.states.BlockStateMachine;
import mekanism.common.integration.wrenches.Wrenches;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.tile.TileEntityAntiprotonicNucleosynthesizer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAntiprotonicNucleosynthesizer extends BlockMekanismContainer {

    public BlockAntiprotonicNucleosynthesizer() {
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockStateFacing.facingProperty, BlockStateMachine.activeProperty);
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
    @Deprecated
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityAntiprotonicNucleosynthesizer) {
            TileEntityAntiprotonicNucleosynthesizer machine = (TileEntityAntiprotonicNucleosynthesizer) tile;
            return state.withProperty(BlockStateFacing.facingProperty, machine.facing)
                  .withProperty(BlockStateMachine.activeProperty, machine.getActive());
        }
        return state;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBasicBlock) {
            ((TileEntityBasicBlock) tile).setFacing(placer.getHorizontalFacing().getOpposite());
            ((TileEntityBasicBlock) tile).redstone = world.getRedstonePowerFromNeighbors(pos) > 0;
        }
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityBasicBlock) {
                ((TileEntityBasicBlock) tile).onNeighborChange(neighborBlock);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
          EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityAntiprotonicNucleosynthesizer)) {
            return false;
        }
        TileEntityAntiprotonicNucleosynthesizer machine = (TileEntityAntiprotonicNucleosynthesizer) tile;
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty()) {
            IMekWrench wrench = Wrenches.getHandler(held);
            if (wrench != null) {
                RayTraceResult ray = new RayTraceResult(new Vec3d(hitX, hitY, hitZ), side, pos);
                if (wrench.canUseWrench(player, hand, held, ray)) {
                    if (!SecurityUtils.canAccess(player, machine)) {
                        SecurityUtils.displayNoAccess(player);
                        return true;
                    }
                    wrench.wrenchUsed(player, hand, held, ray);
                    if (player.isSneaking()) {
                        MekanismUtils.dismantleBlock(this, state, world, pos);
                    } else {
                        machine.setFacing(machine.facing.rotateY());
                        world.notifyNeighborsOfStateChange(pos, this, true);
                    }
                    return true;
                }
            }
        }
        if (!player.isSneaking()) {
            if (SecurityUtils.canAccess(player, machine)) {
                player.openGui(MekanismNuclear.instance, 3, world, pos.getX(), pos.getY(), pos.getZ());
            } else {
                SecurityUtils.displayNoAccess(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileEntityAntiprotonicNucleosynthesizer();
    }

    @Nonnull
    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return new ItemStack(NuclearBlocks.AntiprotonicNucleosynthesizer);
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityAntiprotonicNucleosynthesizer) {
            InventoryHelper.dropInventoryItems(world, pos, (TileEntityAntiprotonicNucleosynthesizer) tile);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(new TextComponentTranslation("tooltip.AntiprotonicNucleosynthesizer").getFormattedText());
    }

    @Override
    @Deprecated
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    @Deprecated
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityAntiprotonicNucleosynthesizer
              ? ((TileEntityAntiprotonicNucleosynthesizer) tile).getRedstoneLevel() : 0;
    }
}
