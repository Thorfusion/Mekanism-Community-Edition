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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

/** Area and connected-block harvesting adapted to Forge 1.7.10. */
public final class MekaToolMiningHelper
{
    private MekaToolMiningHelper()
    {
    }

    public static void mineArea(ItemMekaTool tool, ItemStack stack,
          int originX, int originY, int originZ, EntityPlayer player)
    {
        World world = player.worldObj;
        if (world.isRemote || player.capabilities.isCreativeMode
              || player.isSneaking() || !(player instanceof EntityPlayerMP))
        {
            return;
        }
        Block originBlock = world.getBlock(originX, originY, originZ);
        int originMeta = world.getBlockMetadata(originX, originY, originZ);
        float originHardness = originBlock.getBlockHardness(world, originX,
              originY, originZ);
        if (originHardness < 0)
        {
            return;
        }
        long primaryCost = MekaToolModuleHelper.getMiningEnergyCost(stack,
              originHardness);
        long available = (long)Math.floor(tool.getEnergy(stack));
        if (available - primaryCost <= primaryCost)
        {
            return;
        }

        Coord origin = new Coord(originX, originY, originZ);
        Map<Coord, BlockState> initial = findBlastedBlocks(world, origin,
              new BlockState(originBlock, originMeta), player,
              MekaToolModuleHelper.getBlastingRadius(stack));
        if (initial.isEmpty())
        {
            initial.put(origin, new BlockState(originBlock, originMeta));
        }
        Map<Coord, MiningTarget> targets = findVeinedBlocks(world, initial,
              player, MekaToolModuleHelper.getVeinMiningRange(stack),
              MekaToolModuleHelper.isExtendedVeinMining(stack));
        harvestAdditionalBlocks(tool, stack, origin, (EntityPlayerMP)player,
              targets, available, primaryCost);
    }

    private static Map<Coord, BlockState> findBlastedBlocks(World world,
          Coord origin, BlockState originState, EntityPlayer player, int radius)
    {
        Map<Coord, BlockState> found =
              new LinkedHashMap<Coord, BlockState>();
        if (radius <= 0 || !canBlastBlock(world, origin, originState.block))
        {
            return found;
        }
        int side = getTargetSide(world, origin, originState.block, player);
        if (side < 0)
        {
            return found;
        }
        int minX = 0;
        int maxX = 0;
        int minY = -1;
        int maxY = 2 * radius - 1;
        int minZ = -radius;
        int maxZ = radius;
        if (side == 0 || side == 1)
        {
            minX = -radius;
            maxX = radius;
            minY = 0;
            maxY = 0;
        }
        else if (side == 2 || side == 3)
        {
            minX = -radius;
            maxX = radius;
            minZ = 0;
            maxZ = 0;
        }
        for (int dx = minX; dx <= maxX; dx++)
        {
            for (int dy = minY; dy <= maxY; dy++)
            {
                for (int dz = minZ; dz <= maxZ; dz++)
                {
                    Coord target = new Coord(origin.x + dx, origin.y + dy,
                          origin.z + dz);
                    if (!world.blockExists(target.x, target.y, target.z))
                    {
                        continue;
                    }
                    Block block = world.getBlock(target.x, target.y, target.z);
                    if (canBlastBlock(world, target, block))
                    {
                        found.put(target, new BlockState(block,
                              world.getBlockMetadata(target.x, target.y,
                                    target.z)));
                    }
                }
            }
        }
        return found;
    }

    private static Map<Coord, MiningTarget> findVeinedBlocks(World world,
          Map<Coord, BlockState> initial, EntityPlayer player,
          int configuredRange, boolean extended)
    {
        Map<Coord, MiningTarget> found =
              new LinkedHashMap<Coord, MiningTarget>();
        if (configuredRange < 0)
        {
            for (Coord position : initial.keySet())
            {
                found.put(position, new MiningTarget(0, false));
            }
            return found;
        }

        Map<Block, Boolean> oreTracker =
              new IdentityHashMap<Block, Boolean>();
        Set<Block> initialTypes = new HashSet<Block>();
        Queue<SearchNode> frontier = new ArrayDeque<SearchNode>();
        Set<Coord> queued = new HashSet<Coord>();
        for (Map.Entry<Coord, BlockState> entry : initial.entrySet())
        {
            Coord position = entry.getKey();
            Block block = entry.getValue().block;
            boolean ore = isOreOrLog(world, position, block, player);
            oreTracker.put(block, Boolean.valueOf(ore));
            initialTypes.add(normalizeBlock(block));
            frontier.add(new SearchNode(position, block, 0));
            queued.add(position);
        }
        long calculatedLimit = (long)initial.size()
              + (long)MekaSuitConfig.toolVeinMiningMaxBlocks
                    * Math.max(1, initialTypes.size());
        int maxCount = calculatedLimit >= Integer.MAX_VALUE
              ? Integer.MAX_VALUE : (int)calculatedLimit;

        while (!frontier.isEmpty() && found.size() < maxCount)
        {
            SearchNode node = frontier.remove();
            Boolean cachedOre = oreTracker.get(node.block);
            boolean ore = cachedOre != null && cachedOre.booleanValue();
            found.put(node.position, new MiningTarget(node.distance, ore));
            if (!ore && (!extended || configuredRange <= node.distance))
            {
                continue;
            }
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dy = -1; dy <= 1; dy++)
                {
                    for (int dz = -1; dz <= 1; dz++)
                    {
                        if (dx == 0 && dy == 0 && dz == 0)
                        {
                            continue;
                        }
                        Coord next = new Coord(node.position.x + dx,
                              node.position.y + dy, node.position.z + dz);
                        if (queued.contains(next) || !world.blockExists(next.x,
                              next.y, next.z))
                        {
                            continue;
                        }
                        Block nextBlock = world.getBlock(next.x, next.y,
                              next.z);
                        if (sameVeinBlock(node.block, nextBlock))
                        {
                            queued.add(next);
                            if (!oreTracker.containsKey(nextBlock))
                            {
                                oreTracker.put(nextBlock, Boolean.valueOf(
                                      isOreOrLog(world, next, nextBlock,
                                            player)));
                            }
                            frontier.add(new SearchNode(next, nextBlock,
                                  node.distance + 1));
                        }
                    }
                }
            }
        }
        return found;
    }

    private static void harvestAdditionalBlocks(ItemMekaTool tool,
          ItemStack stack, Coord origin, EntityPlayerMP player,
          Map<Coord, MiningTarget> targets, long available, long primaryCost)
    {
        World world = player.worldObj;
        long energyUsed = 0;
        long budget = available - primaryCost;
        MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        for (Map.Entry<Coord, MiningTarget> entry : targets.entrySet())
        {
            Coord position = entry.getKey();
            if (origin.equals(position))
            {
                continue;
            }
            Block block = world.getBlock(position.x, position.y, position.z);
            if (block == Blocks.air)
            {
                continue;
            }
            float hardness = block.getBlockHardness(world, position.x,
                  position.y, position.z);
            if (hardness < 0)
            {
                continue;
            }
            MiningTarget target = entry.getValue();
            long destroyEnergy = target.distance == 0
                  ? MekaToolModuleHelper.getMiningEnergyCost(stack, hardness)
                  : MekaToolModuleHelper.getVeinMiningEnergyCost(stack,
                        hardness, target.distance, target.ore);
            if (destroyEnergy <= 0
                  || destroyEnergy >= budget - energyUsed)
            {
                continue;
            }
            BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(world,
                  player.theItemInWorldManager.getGameType(), player,
                  position.x, position.y, position.z);
            if (event.isCanceled())
            {
                continue;
            }
            int metadata = world.getBlockMetadata(position.x, position.y,
                  position.z);
            boolean canHarvest = block.canHarvestBlock(player, metadata);
            block.onBlockHarvested(world, position.x, position.y, position.z,
                  metadata, player);
            world.playAuxSFXAtEntity(player, 2001, position.x, position.y,
                  position.z, Block.getIdFromBlock(block) + (metadata << 12));
            if (block.removedByPlayer(world, player, position.x, position.y,
                  position.z, canHarvest))
            {
                block.onBlockDestroyedByPlayer(world, position.x, position.y,
                      position.z, metadata);
                if (canHarvest)
                {
                    block.harvestBlock(world, player, position.x, position.y,
                          position.z, metadata);
                }
                if (event.getExpToDrop() > 0)
                {
                    block.dropXpOnBlockBreak(world, position.x, position.y,
                          position.z, event.getExpToDrop());
                }
                energyUsed += destroyEnergy;
            }
        }
        if (energyUsed > 0)
        {
            tool.setEnergy(stack, tool.getEnergy(stack) - energyUsed);
        }
    }

    private static int getTargetSide(World world, Coord position, Block block,
          EntityPlayer player)
    {
        Vec3 start = Vec3.createVectorHelper(player.posX,
              player.posY + player.getEyeHeight(), player.posZ);
        Vec3 look = player.getLook(1F);
        Vec3 end = start.addVector(look.xCoord * 5D, look.yCoord * 5D,
              look.zCoord * 5D);
        MovingObjectPosition result = block.collisionRayTrace(world,
              position.x, position.y, position.z, start, end);
        return result == null ? -1 : result.sideHit;
    }

    private static boolean canBlastBlock(World world, Coord position,
          Block block)
    {
        return block != Blocks.air && !block.getMaterial().isLiquid()
              && block.getBlockHardness(world, position.x, position.y,
                    position.z) > 0;
    }

    private static boolean isOreOrLog(World world, Coord position, Block block,
          EntityPlayer player)
    {
        try
        {
            Vec3 center = Vec3.createVectorHelper(position.x + 0.5D,
                  position.y + 0.5D, position.z + 0.5D);
            ItemStack picked = block.getPickBlock(new MovingObjectPosition(
                  position.x, position.y, position.z, 1, center), world,
                  position.x, position.y, position.z, player);
            for (String name : OreDictCache.getOreDictName(picked))
            {
                if (name.startsWith("ore") || name.equals("logWood"))
                {
                    return true;
                }
            }
        }
        catch (RuntimeException ignored)
        {
        }
        return false;
    }

    private static boolean sameVeinBlock(Block first, Block second)
    {
        return normalizeBlock(first) == normalizeBlock(second);
    }

    private static Block normalizeBlock(Block block)
    {
        return block == Blocks.lit_redstone_ore ? Blocks.redstone_ore : block;
    }

    private static final class Coord
    {
        private final int x;
        private final int y;
        private final int z;

        private Coord(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other)
            {
                return true;
            }
            if (!(other instanceof Coord))
            {
                return false;
            }
            Coord coord = (Coord)other;
            return x == coord.x && y == coord.y && z == coord.z;
        }

        @Override
        public int hashCode()
        {
            int result = x;
            result = 31 * result + y;
            return 31 * result + z;
        }
    }

    private static final class BlockState
    {
        private final Block block;
        @SuppressWarnings("unused")
        private final int metadata;

        private BlockState(Block block, int metadata)
        {
            this.block = block;
            this.metadata = metadata;
        }
    }

    private static final class SearchNode
    {
        private final Coord position;
        private final Block block;
        private final int distance;

        private SearchNode(Coord position, Block block, int distance)
        {
            this.position = position;
            this.block = block;
            this.distance = distance;
        }
    }

    private static final class MiningTarget
    {
        private final int distance;
        private final boolean ore;

        private MiningTarget(int distance, boolean ore)
        {
            this.distance = distance;
            this.ore = ore;
        }
    }
}
