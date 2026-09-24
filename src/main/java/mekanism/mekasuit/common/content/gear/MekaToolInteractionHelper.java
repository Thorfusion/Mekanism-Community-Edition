package mekanism.mekasuit.common.content.gear;

import java.util.List;
import java.util.Random;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.ForgeEventFactory;

/** 1.12-native interaction adapters for functional Meka-Tool units. */
public final class MekaToolInteractionHelper {

    private MekaToolInteractionHelper() {
    }

    public static EnumActionResult useFarming(ItemMekaTool tool, ItemStack stack, EntityPlayer player,
          World world, BlockPos pos, EnumFacing side) {
        if (player.isSneaking()) {
            return EnumActionResult.PASS;
        }
        int diameter = MekaToolModuleHelper.getFarmingDiameter(stack);
        if (diameter <= 0) {
            return EnumActionResult.PASS;
        }
        IBlockState state = world.getBlockState(pos);
        if (getFarmingResult(state, FarmingAction.FLATTEN) != null) {
            return performFarming(tool, stack, player, world, pos, side, diameter,
                  FarmingAction.FLATTEN, MekaSuitConfig.toolShovelUsage);
        }
        return performFarming(tool, stack, player, world, pos, side, diameter,
              FarmingAction.TILL, MekaSuitConfig.toolHoeUsage);
    }

    public static boolean shearEntity(ItemMekaTool tool, ItemStack stack, EntityPlayer player,
          EntityLivingBase entity) {
        if (entity.world.isRemote || !MekaToolModuleHelper.hasShearing(stack)
              || !(entity instanceof IShearable) || tool.getEnergy(stack) < MekaSuitConfig.toolShearEntityUsage) {
            return false;
        }
        MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        IShearable target = (IShearable) entity;
        BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
        if (!target.isShearable(stack, entity.world, pos)) {
            return false;
        }
        List<ItemStack> drops = target.onSheared(stack, entity.world, pos,
              MekaToolModuleHelper.getFortuneLevel(stack));
        Random random = entity.getRNG();
        for (ItemStack drop : drops) {
            EntityItem item = entity.entityDropItem(drop, 1F);
            if (item != null) {
                item.motionY += random.nextFloat() * 0.05F;
                item.motionX += (random.nextFloat() - random.nextFloat()) * 0.1F;
                item.motionZ += (random.nextFloat() - random.nextFloat()) * 0.1F;
            }
        }
        tool.setEnergy(stack, tool.getEnergy(stack) - MekaSuitConfig.toolShearEntityUsage);
        return true;
    }

    public static boolean shearBlock(ItemMekaTool tool, ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (player.world.isRemote || player.capabilities.isCreativeMode || !MekaToolModuleHelper.hasShearing(stack)) {
            return false;
        }
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();
        long cost = MekaToolModuleHelper.getMiningEnergyCost(stack, state.getBlockHardness(player.world, pos));
        if (tool.getEnergy(stack) < cost) {
            return false;
        }
        if (block == Blocks.TRIPWIRE) {
            // Vanilla 1.12 checks for the literal Shears item. Mark the state
            // ourselves so the following normal removal does not trip hooks.
            player.world.setBlockState(pos, state.withProperty(BlockTripWire.DISARMED, true), 4);
            return false;
        }
        if (!(block instanceof IShearable)) {
            return false;
        }
        MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        IShearable target = (IShearable) block;
        if (!target.isShearable(stack, player.world, pos)) {
            return false;
        }
        List<ItemStack> drops = target.onSheared(stack, player.world, pos,
              MekaToolModuleHelper.getFortuneLevel(stack));
        Random random = player.world.rand;
        for (ItemStack drop : drops) {
            float spread = 0.7F;
            double x = random.nextFloat() * spread + (1F - spread) * 0.5D;
            double y = random.nextFloat() * spread + (1F - spread) * 0.5D;
            double z = random.nextFloat() * spread + (1F - spread) * 0.5D;
            EntityItem item = new EntityItem(player.world, pos.getX() + x, pos.getY() + y, pos.getZ() + z, drop);
            item.setDefaultPickupDelay();
            player.world.spawnEntity(item);
        }
        tool.setEnergy(stack, tool.getEnergy(stack) - cost);
        player.addStat(StatList.getBlockStats(block));
        player.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        return true;
    }

    private static EnumActionResult performFarming(ItemMekaTool tool, ItemStack stack, EntityPlayer player,
          World world, BlockPos pos, EnumFacing side, int diameter, FarmingAction action, long energyPerBlock) {
        if (tool.getEnergy(stack) < energyPerBlock || !player.canPlayerEdit(pos.offset(side), side, stack)) {
            return EnumActionResult.FAIL;
        }
        if (action == FarmingAction.TILL) {
            int hook = ForgeEventFactory.onHoeUse(stack, player, world, pos);
            if (hook != 0) {
                if (hook > 0 && !world.isRemote) {
                    tool.setEnergy(stack, tool.getEnergy(stack) - energyPerBlock);
                }
                return hook > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
            }
        }
        IBlockState result = getFarmingResult(world, pos, side, action);
        if (result == null) {
            return EnumActionResult.PASS;
        }
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        changeBlock(world, pos, result, action);
        long energyUsed = energyPerBlock;
        int radius = (diameter - 1) / 2;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }
                long nextEnergyUsed = energyUsed + energyPerBlock;
                if (nextEnergyUsed > tool.getEnergy(stack)) {
                    break;
                }
                BlockPos target = pos.add(x, 0, z);
                if (!player.canPlayerEdit(target.offset(side), side, stack)) {
                    continue;
                }
                if (action == FarmingAction.TILL) {
                    int hook = ForgeEventFactory.onHoeUse(stack, player, world, target);
                    if (hook != 0) {
                        if (hook > 0) {
                            energyUsed = nextEnergyUsed;
                        }
                        continue;
                    }
                }
                IBlockState targetResult = getFarmingResult(world, target, side, action);
                if (result.equals(targetResult)) {
                    changeBlock(world, target, result, action);
                    energyUsed = nextEnergyUsed;
                }
            }
        }
        tool.setEnergy(stack, tool.getEnergy(stack) - energyUsed);
        return EnumActionResult.SUCCESS;
    }

    private static IBlockState getFarmingResult(World world, BlockPos pos, EnumFacing side, FarmingAction action) {
        if (side == EnumFacing.DOWN || !world.isAirBlock(pos.up())) {
            return null;
        }
        return getFarmingResult(world.getBlockState(pos), action);
    }

    static IBlockState getFarmingResult(IBlockState state, FarmingAction action) {
        Block block = state.getBlock();
        if (action == FarmingAction.FLATTEN) {
            return block == Blocks.GRASS ? Blocks.GRASS_PATH.getDefaultState() : null;
        }
        if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
            return Blocks.FARMLAND.getDefaultState();
        }
        if (block == Blocks.DIRT) {
            DirtType type = state.getValue(BlockDirt.VARIANT);
            if (type == DirtType.DIRT) {
                return Blocks.FARMLAND.getDefaultState();
            }
            if (type == DirtType.COARSE_DIRT) {
                return Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT);
            }
        }
        return null;
    }

    private static void changeBlock(World world, BlockPos pos, IBlockState state, FarmingAction action) {
        world.playSound(null, pos, action == FarmingAction.FLATTEN ? SoundEvents.ITEM_SHOVEL_FLATTEN
              : SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1F, 1F);
        world.setBlockState(pos, state, 11);
    }

    enum FarmingAction {
        FLATTEN,
        TILL
    }
}
