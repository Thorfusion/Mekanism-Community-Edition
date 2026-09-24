package mekanism.nuclear.common.block;

import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.common.block.BlockMekanismContainer;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.content.fission.FissionReactorValidator;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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

/** Formation entry point and persisted controller node for a fission reactor. */
public class BlockFissionReactorPort extends BlockMekanismContainer {

    public BlockFissionReactorPort() {
        super(Material.IRON);
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
        if (!(tile instanceof TileEntityFissionReactorPort)) {
            return false;
        }
        FissionReactorValidator.Result result = ((TileEntityFissionReactorPort) tile).validateStructure();
        if (result.isFormed()) {
            player.sendMessage(new TextComponentTranslation("fission.mekanismnuclear.formed",
                  result.getWidth(), result.getHeight(), result.getLength(), result.getFuelAssemblies()));
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
