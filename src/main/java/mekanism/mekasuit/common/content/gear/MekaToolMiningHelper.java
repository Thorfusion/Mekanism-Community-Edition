package mekanism.mekasuit.common.content.gear;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import mekanism.common.OreDictCache;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaTool;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants.WorldEvents;

/** Stable-style area and connected-block harvesting adapted to Forge 1.12. */
public final class MekaToolMiningHelper {

    private MekaToolMiningHelper() {
    }

    public static void mineArea(ItemMekaTool tool, ItemStack stack, BlockPos origin, EntityPlayer player) {
        World world = player.world;
        if (world.isRemote || player.capabilities.isCreativeMode || player.isSneaking()
              || !(player instanceof EntityPlayerMP)) {
            return;
        }
        IBlockState originState = world.getBlockState(origin);
        float originHardness = originState.getBlockHardness(world, origin);
        if (originHardness < 0) {
            return;
        }
        long primaryCost = MekaToolModuleHelper.getMiningEnergyCost(stack, originHardness);
        long available = (long) Math.floor(tool.getEnergy(stack));
        // Match stable Mekanism: reserve the primary break and require enough
        // charge for at least one more normal break before starting AOE work.
        if (available - primaryCost <= primaryCost) {
            return;
        }

        Map<BlockPos, IBlockState> initial = findBlastedBlocks(world, origin, originState, player,
              MekaToolModuleHelper.getBlastingRadius(stack));
        if (initial.isEmpty()) {
            initial.put(origin, originState);
        }
        Map<BlockPos, MiningTarget> targets = findVeinedBlocks(world, initial, player,
              MekaToolModuleHelper.getVeinMiningRange(stack), MekaToolModuleHelper.isExtendedVeinMining(stack));
        harvestAdditionalBlocks(tool, stack, origin, player, targets, available, primaryCost);
    }

    static Map<BlockPos, IBlockState> findBlastedBlocks(World world, BlockPos origin, IBlockState originState,
          EntityPlayer player, int radius) {
        Map<BlockPos, IBlockState> found = new LinkedHashMap<>();
        if (radius <= 0 || !canBlastBlock(world, origin, originState)) {
            return found;
        }
        EnumFacing side = getTargetSide(world, origin, originState, player);
        if (side == null) {
            return found;
        }
        int minX = 0;
        int maxX = 0;
        int minY = -1;
        int maxY = 2 * radius - 1;
        int minZ = -radius;
        int maxZ = radius;
        if (side.getAxis() == EnumFacing.Axis.Y) {
            minX = -radius;
            maxX = radius;
            minY = 0;
            maxY = 0;
        } else if (side.getAxis() == EnumFacing.Axis.Z) {
            minX = -radius;
            maxX = radius;
            minZ = 0;
            maxZ = 0;
        }
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos target = origin.add(x, y, z);
                    if (target.getY() < 0 || target.getY() >= world.getHeight() || !world.isBlockLoaded(target)) {
                        continue;
                    }
                    IBlockState state = world.getBlockState(target);
                    if (canBlastBlock(world, target, state)) {
                        found.put(target.toImmutable(), state);
                    }
                }
            }
        }
        return found;
    }

    private static Map<BlockPos, MiningTarget> findVeinedBlocks(World world,
          Map<BlockPos, IBlockState> initial, EntityPlayer player, int configuredRange, boolean extended) {
        Map<BlockPos, MiningTarget> found = new LinkedHashMap<>();
        if (configuredRange < 0) {
            for (Map.Entry<BlockPos, IBlockState> entry : initial.entrySet()) {
                found.put(entry.getKey(), new MiningTarget(0, false));
            }
            return found;
        }

        Map<Block, Boolean> oreTracker = new IdentityHashMap<>();
        Set<Block> initialTypes = new HashSet<>();
        Queue<SearchNode> frontier = new ArrayDeque<>();
        for (Map.Entry<BlockPos, IBlockState> entry : initial.entrySet()) {
            Block block = entry.getValue().getBlock();
            boolean ore = isOreOrLog(world, entry.getKey(), entry.getValue(), player);
            oreTracker.put(block, ore);
            initialTypes.add(normalizeBlock(block));
            frontier.add(new SearchNode(entry.getKey(), block, 0));
        }
        long calculatedLimit = (long) initial.size()
              + (long) MekaSuitConfig.toolVeinMiningMaxBlocks * Math.max(1, initialTypes.size());
        int maxCount = calculatedLimit >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) calculatedLimit;
        Set<BlockPos> queued = new HashSet<>(initial.keySet());

        while (!frontier.isEmpty() && found.size() < maxCount) {
            SearchNode node = frontier.remove();
            Boolean cachedOre = oreTracker.get(node.block);
            boolean ore = cachedOre != null && cachedOre;
            found.put(node.pos, new MiningTarget(node.distance, ore));
            if (!ore && (!extended || configuredRange <= node.distance)) {
                continue;
            }
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }
                        BlockPos next = node.pos.add(x, y, z);
                        if (next.getY() < 0 || next.getY() >= world.getHeight()
                              || queued.contains(next) || !world.isBlockLoaded(next)) {
                            continue;
                        }
                        IBlockState nextState = world.getBlockState(next);
                        if (sameVeinBlock(node.block, nextState.getBlock())) {
                            BlockPos immutable = next.toImmutable();
                            queued.add(immutable);
                            Block nextBlock = nextState.getBlock();
                            if (!oreTracker.containsKey(nextBlock)) {
                                oreTracker.put(nextBlock, isOreOrLog(world, immutable, nextState, player));
                            }
                            frontier.add(new SearchNode(immutable, nextBlock, node.distance + 1));
                        }
                    }
                }
            }
        }
        return found;
    }

    private static void harvestAdditionalBlocks(ItemMekaTool tool, ItemStack stack, BlockPos origin,
          EntityPlayer player, Map<BlockPos, MiningTarget> targets, long available, long primaryCost) {
        World world = player.world;
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        long energyUsed = 0;
        long budget = available - primaryCost;
        MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        for (Map.Entry<BlockPos, MiningTarget> entry : targets.entrySet()) {
            BlockPos pos = entry.getKey();
            if (origin.equals(pos)) {
                continue;
            }
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == Blocks.AIR) {
                continue;
            }
            float hardness = state.getBlockHardness(world, pos);
            if (hardness < 0) {
                continue;
            }
            MiningTarget target = entry.getValue();
            long destroyEnergy = target.distance == 0
                  ? MekaToolModuleHelper.getMiningEnergyCost(stack, hardness)
                  : MekaToolModuleHelper.getVeinMiningEnergyCost(stack, hardness, target.distance, target.ore);
            if (destroyEnergy <= 0 || destroyEnergy >= budget - energyUsed) {
                continue;
            }
            int experience = ForgeHooks.onBlockBreakEvent(world, playerMP.interactionManager.getGameType(), playerMP, pos);
            if (experience == -1) {
                continue;
            }
            Block block = state.getBlock();
            TileEntity tile = world.getTileEntity(pos);
            boolean canHarvest = block.canHarvestBlock(world, pos, player);
            ItemStack harvestTool = stack.copy();
            world.playEvent(WorldEvents.BREAK_BLOCK_EFFECTS, pos, Block.getStateId(state));
            if (block.removedByPlayer(state, world, pos, player, canHarvest)) {
                block.onPlayerDestroy(world, pos, state);
                if (canHarvest) {
                    block.harvestBlock(world, player, pos, state, tile, harvestTool);
                }
                if (experience > 0) {
                    block.dropXpOnBlockBreak(world, pos, experience);
                }
                energyUsed += destroyEnergy;
            }
        }
        if (energyUsed > 0) {
            tool.setEnergy(stack, tool.getEnergy(stack) - energyUsed);
        }
    }

    private static EnumFacing getTargetSide(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        Vec3d start = player.getPositionEyes(1F);
        Vec3d look = player.getLook(1F);
        double reach = player.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d end = start.add(look.x * reach, look.y * reach, look.z * reach);
        RayTraceResult result = state.collisionRayTrace(world, pos, start, end);
        return result == null ? null : result.sideHit;
    }

    private static boolean canBlastBlock(World world, BlockPos pos, IBlockState state) {
        return state.getBlock() != Blocks.AIR && !state.getMaterial().isLiquid()
              && state.getBlockHardness(world, pos) > 0;
    }

    private static boolean isOreOrLog(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        try {
            Vec3d center = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            ItemStack picked = state.getBlock().getPickBlock(state,
                  new RayTraceResult(center, EnumFacing.UP, pos), world, pos, player);
            for (String name : OreDictCache.getOreDictName(picked)) {
                if (name.startsWith("ore") || name.equals("logWood")) {
                    return true;
                }
            }
        } catch (RuntimeException ignored) {
            // A third-party pick-block implementation must not abort mining.
        }
        return false;
    }

    private static boolean sameVeinBlock(Block first, Block second) {
        return normalizeBlock(first) == normalizeBlock(second);
    }

    private static Block normalizeBlock(Block block) {
        return block == Blocks.LIT_REDSTONE_ORE ? Blocks.REDSTONE_ORE : block;
    }

    private static final class SearchNode {

        private final BlockPos pos;
        private final Block block;
        private final int distance;

        private SearchNode(BlockPos pos, Block block, int distance) {
            this.pos = pos;
            this.block = block;
            this.distance = distance;
        }
    }

    private static final class MiningTarget {

        private final int distance;
        private final boolean ore;

        private MiningTarget(int distance, boolean ore) {
            this.distance = distance;
            this.ore = ore;
        }
    }
}
