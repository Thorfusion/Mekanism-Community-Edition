package mekanism.ultimate.common.block;

import javax.annotation.Nonnull;
import mekanism.api.IMekWrench;
import mekanism.common.Mekanism;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.common.block.states.BlockStateFacing;
import mekanism.common.integration.wrenches.Wrenches;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.item.ItemBlockChemicalTank;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import mekanism.ultimate.common.tier.ChemicalTankTier;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** One fixed-tier modern Chemical Tank block identity. */
public final class BlockChemicalTank extends BlockMekanismContainer {

    private static final AxisAlignedBB TANK_BOUNDS = new AxisAlignedBB(
          3D / 16D, 0D, 3D / 16D, 13D / 16D, 1D, 13D / 16D);

    private final ChemicalTankTier tier;

    public BlockChemicalTank(ChemicalTankTier tier) {
        super(Material.IRON);
        this.tier = tier;
        setHardness(3.5F);
        setResistance(8F);
        setSoundType(SoundType.METAL);
        setCreativeTab(Mekanism.tabMekanism);
        setDefaultState(blockState.getBaseState()
              .withProperty(BlockStateFacing.facingProperty, EnumFacing.NORTH));
    }

    public ChemicalTankTier getTier() {
        return tier;
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateFacing(this);
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
        return tile instanceof TileEntityChemicalTank
              ? state.withProperty(BlockStateFacing.facingProperty,
                    ((TileEntityChemicalTank) tile).facing)
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
    public void neighborChanged(IBlockState state, World world, BlockPos pos,
          Block neighborBlock, BlockPos neighborPos) {
        if (!world.isRemote && world.getTileEntity(pos) instanceof TileEntityBasicBlock) {
            ((TileEntityBasicBlock) world.getTileEntity(pos)).onNeighborChange(neighborBlock);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
          EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
          float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityChemicalTank)) {
            return false;
        }
        TileEntityChemicalTank tank = (TileEntityChemicalTank) tile;
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty()) {
            IMekWrench wrench = Wrenches.getHandler(held);
            if (wrench != null) {
                RayTraceResult ray = new RayTraceResult(new Vec3d(hitX, hitY, hitZ), side, pos);
                if (wrench.canUseWrench(player, hand, held, ray)) {
                    if (!SecurityUtils.canAccess(player, tank)) {
                        SecurityUtils.displayNoAccess(player);
                        return true;
                    }
                    wrench.wrenchUsed(player, hand, held, ray);
                    if (player.isSneaking()) {
                        MekanismUtils.dismantleBlock(this, state, world, pos);
                    } else {
                        tank.setFacing(tank.facing.rotateY());
                        world.notifyNeighborsOfStateChange(pos, this, true);
                    }
                    return true;
                }
            }
        }
        if (!player.isSneaking()) {
            if (SecurityUtils.canAccess(player, tank)) {
                player.openGui(MekanismUltimate.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
            } else {
                SecurityUtils.displayNoAccess(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileEntityChemicalTank(tier);
    }

    @Nonnull
    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world,
          @Nonnull BlockPos pos) {
        ItemStack drop = new ItemStack(this);
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityChemicalTank)
              || !(drop.getItem() instanceof ItemBlockChemicalTank)) {
            return drop;
        }
        TileEntityChemicalTank tank = (TileEntityChemicalTank) tile;
        ItemBlockChemicalTank item = (ItemBlockChemicalTank) drop.getItem();
        item.setStoredChemical(drop, tank.chemicalTank.getStack());
        item.setDumpMode(drop, tank.dumping);

        ISecurityItem securityItem = item;
        if (securityItem.hasSecurity(drop)) {
            securityItem.setOwnerUUID(drop, ((ISecurityTile) tank).getSecurity().getOwnerUUID());
            securityItem.setSecurity(drop, ((ISecurityTile) tank).getSecurity().getMode());
        }
        NBTTagCompound dataMap = ItemDataUtils.getDataMap(drop);
        ((ISideConfiguration) tank).getConfig().write(dataMap);
        ((ISideConfiguration) tank).getEjector().write(dataMap);
        item.setInventory(((ISustainedInventory) tank).getInventory(), drop);
        return drop;
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
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return TANK_BOUNDS;
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

    @Override
    @Deprecated
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    @Deprecated
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityChemicalTank
              ? ((TileEntityChemicalTank) tile).getRedstoneLevel() : 0;
    }

    @Override
    public EnumFacing[] getValidRotations(World world, @Nonnull BlockPos pos) {
        EnumFacing[] valid = new EnumFacing[6];
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBasicBlock) {
            for (EnumFacing side : EnumFacing.VALUES) {
                if (((TileEntityBasicBlock) tile).canSetFacing(side)) {
                    valid[side.ordinal()] = side;
                }
            }
        }
        return valid;
    }

    @Override
    public boolean rotateBlock(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBasicBlock && ((TileEntityBasicBlock) tile).canSetFacing(axis)) {
            ((TileEntityBasicBlock) tile).setFacing(axis);
            return true;
        }
        return false;
    }
}
