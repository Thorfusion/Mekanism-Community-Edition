package mekanism.ultimate.common.block;

import javax.annotation.Nonnull;
import mekanism.api.IMekWrench;
import mekanism.common.Mekanism;
import mekanism.common.base.ISustainedInventory;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.common.integration.wrenches.Wrenches;
import mekanism.common.security.ISecurityItem;
import mekanism.common.security.ISecurityTile;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.item.ItemBlockPersonalBarrel;
import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
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

/** Secure, portable 54-slot personal storage with barrel presentation. */
public final class BlockPersonalBarrel extends BlockMekanismContainer {

    public BlockPersonalBarrel() {
        super(Material.WOOD);
        setHardness(2.5F);
        setResistance(6_000_000F);
        setSoundType(SoundType.WOOD);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
          EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
          float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityPersonalBarrel)) {
            return false;
        }
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
                    if (!world.isRemote && player.isSneaking()) {
                        MekanismUtils.dismantleBlock(this, state, world, pos);
                    }
                    return true;
                }
            }
        }
        if (!player.isSneaking()) {
            if (!world.isRemote) {
                if (SecurityUtils.canAccess(player, tile)) {
                    player.openGui(MekanismUltimate.instance, 3, world, pos.getX(), pos.getY(), pos.getZ());
                } else {
                    SecurityUtils.displayNoAccess(player);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileEntityPersonalBarrel();
    }

    @Nonnull
    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world,
          @Nonnull BlockPos pos) {
        ItemStack drop = new ItemStack(this);
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityPersonalBarrel)
              || !(drop.getItem() instanceof ItemBlockPersonalBarrel)) {
            return drop;
        }
        TileEntityPersonalBarrel barrel = (TileEntityPersonalBarrel) tile;
        ItemBlockPersonalBarrel item = (ItemBlockPersonalBarrel) drop.getItem();
        item.setInventory(((ISustainedInventory) barrel).getInventory(), drop);
        item.setOwnerUUID(drop, ((ISecurityTile) barrel).getSecurity().getOwnerUUID());
        item.setSecurity(drop, ((ISecurityTile) barrel).getSecurity().getMode());
        return drop;
    }

    @Nonnull
    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
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
        return tile instanceof TileEntityPersonalBarrel
              ? Container.calcRedstoneFromInventory((TileEntityPersonalBarrel) tile) : 0;
    }

    @Override
    @Deprecated
    public float getPlayerRelativeBlockHardness(IBlockState state, @Nonnull EntityPlayer player,
          @Nonnull World world, @Nonnull BlockPos pos) {
        return SecurityUtils.canAccess(player, world.getTileEntity(pos))
              ? super.getPlayerRelativeBlockHardness(state, player, world, pos) : 0;
    }
}
