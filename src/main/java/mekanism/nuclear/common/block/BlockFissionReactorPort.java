package mekanism.nuclear.common.block;

import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.content.fission.FissionPortMode;
import mekanism.nuclear.common.content.fission.FissionReactorState;
import mekanism.nuclear.common.content.fission.FissionReactorValidator;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
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

/** Formation entry point and persisted controller node for a fission reactor. */
public class BlockFissionReactorPort extends BlockMekanismContainer {

    public static final PropertyEnum<FissionPortMode> MODE = PropertyEnum.create("mode", FissionPortMode.class);

    public BlockFissionReactorPort() {
        super(Material.IRON);
        setDefaultState(blockState.getBaseState().withProperty(MODE, FissionPortMode.INPUT));
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
        if (world.isRemote) {
            return true;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityFissionReactorPort)) {
            return false;
        }
        TileEntityFissionReactorPort port = (TileEntityFissionReactorPort) tile;
        if (player.isSneaking()) {
            FissionPortMode mode = port.cycleMode();
            player.sendMessage(new TextComponentTranslation("fission.mekanismnuclear.port_mode",
                  new TextComponentTranslation("fission.mekanismnuclear.port_mode." + mode.getName())));
            return true;
        }
        TileEntityFissionReactorPort controller = port.getControllerTile();
        if (controller != null && controller.getReactorState().isFormed()) {
            player.openGui(MekanismNuclear.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        FissionReactorValidator.Result result = port.validateStructure();
        if (result.isFormed()) {
            player.sendMessage(new TextComponentTranslation("fission.mekanismnuclear.formed",
                  result.getWidth(), result.getHeight(), result.getLength(), result.getFuelAssemblies()));
            player.openGui(MekanismNuclear.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
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
        return tile instanceof TileEntityFissionReactorPort
              ? state.withProperty(MODE, ((TileEntityFissionReactorPort) tile).getMode()) : state;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MODE);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityFissionReactorPort) {
            TileEntityFissionReactorPort controller = ((TileEntityFissionReactorPort) tile).getControllerTile();
            if (controller != null) {
                FissionReactorState reactor = controller.getReactorState();
                long capacity = reactor.getFuelCapacity();
                if (capacity > 0 && reactor.getFissileFuel() > 0) {
                    return 1 + (int) Math.floor(14D * reactor.getFissileFuel() / capacity);
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
            if (tile instanceof TileEntityFissionReactorPort) {
                ((TileEntityFissionReactorPort) tile).queueValidation();
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
        return new TileEntityFissionReactorPort();
    }

    @Nonnull
    @Override
    protected ItemStack getDropItem(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return new ItemStack(NuclearBlocks.FissionReactorPort);
    }
}
