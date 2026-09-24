package mekanism.nuclear.common.block;

import javax.annotation.Nonnull;
import mekanism.api.IMekWrench;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.common.integration.wrenches.Wrenches;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.tile.TileEntityRadioactiveWasteBarrel;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRadioactiveWasteBarrel extends BlockMekanismContainer {

    public BlockRadioactiveWasteBarrel() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(8F);
        setSoundType(SoundType.METAL);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
          EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityRadioactiveWasteBarrel)) {
            return false;
        }
        TileEntityRadioactiveWasteBarrel barrel = (TileEntityRadioactiveWasteBarrel) tile;
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty()) {
            IMekWrench wrench = Wrenches.getHandler(held);
            if (wrench != null) {
                RayTraceResult ray = new RayTraceResult(new Vec3d(hitX, hitY, hitZ), side, pos);
                if (wrench.canUseWrench(player, hand, held, ray)) {
                    wrench.wrenchUsed(player, hand, held, ray);
                    if (!world.isRemote) {
                        MekanismUtils.dismantleBlock(this, state, world, pos);
                    }
                    return true;
                }
            }
        }
        if (player.isSneaking()) {
            if (!world.isRemote) {
                if (barrel.getStoredGas() == null) {
                    player.sendMessage(new TextComponentTranslation("gui.empty"));
                } else {
                    player.sendMessage(new TextComponentTranslation("tooltip.RadioactiveWasteBarrel.stored",
                          barrel.getStoredGas().getLocalizedName(), barrel.getStored(), barrel.getCapacity()));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileEntityRadioactiveWasteBarrel();
    }

    @Nonnull
    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world,
          @Nonnull BlockPos pos) {
        return new ItemStack(NuclearBlocks.RadioactiveWasteBarrel);
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && tile instanceof TileEntityRadioactiveWasteBarrel) {
            ((TileEntityRadioactiveWasteBarrel) tile).releaseContentsToRadiation();
        }
        super.breakBlock(world, pos, state);
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
        return tile instanceof TileEntityRadioactiveWasteBarrel
              ? ((TileEntityRadioactiveWasteBarrel) tile).getRedstoneLevel() : 0;
    }
}
