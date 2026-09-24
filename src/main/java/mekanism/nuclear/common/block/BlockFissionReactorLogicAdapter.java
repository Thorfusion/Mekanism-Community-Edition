package mekanism.nuclear.common.block;

import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.content.fission.FissionReactorLogicMode;
import mekanism.nuclear.common.tile.TileEntityFissionReactorLogicAdapter;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Configurable redstone input/output face for a Fission Reactor. */
public class BlockFissionReactorLogicAdapter extends BlockMekanismContainer {

    public BlockFissionReactorLogicAdapter() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(16F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Nonnull
    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
          EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityFissionReactorLogicAdapter) {
                FissionReactorLogicMode mode = ((TileEntityFissionReactorLogicAdapter) tile).cycleMode();
                player.sendMessage(new TextComponentTranslation("fission.mekanismnuclear.logic_mode",
                      new TextComponentTranslation("fission.mekanismnuclear.logic_mode." + mode.getName())));
            }
        }
        return true;
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityFissionReactorLogicAdapter) {
                ((TileEntityFissionReactorLogicAdapter) tile).updateLogic();
            }
        }
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityFissionReactorLogicAdapter
              ? ((TileEntityFissionReactorLogicAdapter) tile).getRedstoneLevel(side) : 0;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileEntityFissionReactorLogicAdapter();
    }

    @Nonnull
    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return new ItemStack(NuclearBlocks.FissionReactorLogicAdapter);
    }
}
