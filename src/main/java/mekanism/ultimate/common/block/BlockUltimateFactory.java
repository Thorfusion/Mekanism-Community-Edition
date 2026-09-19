package mekanism.ultimate.common.block;

import javax.annotation.Nonnull;
import mekanism.api.IMekWrench;
import mekanism.common.base.IFactory;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.block.BlockMachine;
import mekanism.common.block.states.BlockStateFacing;
import mekanism.common.block.states.BlockStateMachine;
import mekanism.common.block.states.BlockStateMachine.MachineBlock;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.config.MekanismConfig;
import mekanism.common.integration.wrenches.Wrenches;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.tile.TileEntityUltimateFactory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockUltimateFactory extends BlockMachine {

    public BlockUltimateFactory() {
        setDefaultState(getDefaultState()
              .withProperty(getTypeProperty(), MachineType.ELITE_FACTORY)
              .withProperty(BlockStateFacing.facingProperty, EnumFacing.NORTH)
              .withProperty(BlockStateMachine.activeProperty, false)
              .withProperty(BlockStateMachine.recipeProperty, RecipeType.SMELTING));
    }

    @Override
    public MachineBlock getMachineBlock() {
        //The type property is only used to reuse the core machine block state container.
        return MachineBlock.MACHINE_BLOCK_1;
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

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (MekanismConfig.current().ultimate == null || !MekanismConfig.current().ultimate.factoryEnabled.val()) {
            return;
        }
        for (RecipeType type : RecipeType.values()) {
            if (type.getType().isEnabled()) {
                ItemStack stack = new ItemStack(this);
                ((IFactory) stack.getItem()).setRecipeType(type.ordinal(), stack);
                list.add(stack);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityUltimateFactory)) {
            return false;
        }
        TileEntityBasicBlock factory = (TileEntityBasicBlock) tile;
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty()) {
            IMekWrench wrenchHandler = Wrenches.getHandler(stack);
            if (wrenchHandler != null) {
                RayTraceResult rayTrace = new RayTraceResult(new Vec3d(hitX, hitY, hitZ), side, pos);
                if (wrenchHandler.canUseWrench(player, hand, stack, rayTrace)) {
                    if (!SecurityUtils.canAccess(player, factory)) {
                        SecurityUtils.displayNoAccess(player);
                        return true;
                    }
                    wrenchHandler.wrenchUsed(player, hand, stack, rayTrace);
                    if (player.isSneaking()) {
                        MekanismUtils.dismantleBlock(this, state, world, pos);
                        return true;
                    }
                    factory.setFacing(factory.facing.rotateY());
                    world.notifyNeighborsOfStateChange(pos, this, true);
                    return true;
                }
            }
        }
        if (!player.isSneaking()) {
            if (SecurityUtils.canAccess(player, factory)) {
                player.openGui(MekanismUltimate.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
            } else {
                SecurityUtils.displayNoAccess(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntityUltimateFactory();
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
        return new TileEntityUltimateFactory();
    }
}
