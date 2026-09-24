package mekanism.nuclear.common.block;

import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.content.sps.SPSValidator;
import mekanism.nuclear.common.content.sps.SPSState;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Formation entry point and persisted controller node for an SPS. */
public class BlockSPSPort extends BlockMekanismContainer {

    public static final PropertyBool OUTPUT = PropertyBool.create("output");

    public BlockSPSPort() {
        super(Material.IRON);
        setDefaultState(blockState.getBaseState().withProperty(OUTPUT, false));
        setHardness(3.5F);
        setResistance(16F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
          EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntitySPSPort)) {
            return false;
        }
        TileEntitySPSPort port = (TileEntitySPSPort) tile;
        if (player.isSneaking()) {
            boolean output = port.toggleOutput();
            player.sendMessage(new TextComponentTranslation("sps.mekanismnuclear.port_mode",
                  new TextComponentTranslation(output ? "sps.mekanismnuclear.port_mode.output"
                        : "sps.mekanismnuclear.port_mode.input")));
            return true;
        }

        TileEntitySPSPort controller = port.getControllerTile();
        if (controller != null && controller.getSPSState().isFormed()) {
            player.openGui(MekanismNuclear.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        SPSValidator.Result result = port.validateStructure();
        if (result.isFormed()) {
            player.sendMessage(new TextComponentTranslation("sps.mekanismnuclear.formed",
                  result.getPorts().size(), result.getCoils().size()));
            player.openGui(MekanismNuclear.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
        } else if (result.getFailurePos() == null) {
            player.sendMessage(new TextComponentTranslation(result.getFailure().getTranslationKey()));
        } else {
            BlockPos failure = result.getFailurePos();
            player.sendMessage(new TextComponentTranslation(result.getFailure().getTranslationKey(),
                  "(" + failure.getX() + ", " + failure.getY() + ", " + failure.getZ() + ")"));
        }
        return true;
    }

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
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntitySPSPort
              ? state.withProperty(OUTPUT, ((TileEntitySPSPort) tile).isOutput()) : state;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, OUTPUT);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySPSPort) {
            TileEntitySPSPort controller = ((TileEntitySPSPort) tile).getControllerTile();
            if (controller != null) {
                SPSState sps = controller.getSPSState();
                long capacity = sps.getPoloniumCapacity();
                if (capacity > 0 && sps.getPolonium() > 0) {
                    return 1 + (int) Math.floor(14D * sps.getPolonium() / capacity);
                }
            }
        }
        return 0;
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntitySPSPort) {
                ((TileEntitySPSPort) tile).queueValidation();
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileEntitySPSPort();
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && tile instanceof TileEntitySPSPort) {
            ((TileEntitySPSPort) tile).prepareForRemoval();
        }
        super.breakBlock(world, pos, state);
    }

    @Nonnull
    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return new ItemStack(NuclearBlocks.SPSPort);
    }
}
