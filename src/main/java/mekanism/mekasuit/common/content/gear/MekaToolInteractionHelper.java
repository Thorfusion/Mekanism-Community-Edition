package mekanism.mekasuit.common.content.gear;

import cpw.mods.fml.common.eventhandler.Event.Result;
import java.util.List;
import java.util.Random;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaTool;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;

/** Farming and shearing adapters for Forge 1.7.10. */
public final class MekaToolInteractionHelper
{
    private MekaToolInteractionHelper()
    {
    }

    public static boolean useFarming(ItemMekaTool tool, ItemStack stack,
          EntityPlayer player, World world, int x, int y, int z, int side)
    {
        if (player.isSneaking())
        {
            return false;
        }
        int diameter = MekaToolModuleHelper.getFarmingDiameter(stack);
        if (diameter <= 0 || side == 0 || !world.isAirBlock(x, y + 1, z)
              || !isTillable(world.getBlock(x, y, z)))
        {
            return false;
        }
        long cost = MekaSuitConfig.toolHoeUsage;
        if (tool.getEnergy(stack) < cost
              || !player.canPlayerEdit(x, y, z, side, stack))
        {
            return false;
        }
        if (world.isRemote)
        {
            return true;
        }

        long energyUsed = till(tool, stack, player, world, x, y, z, side,
              cost) ? cost : 0;
        if (energyUsed == 0)
        {
            return false;
        }
        int radius = (diameter - 1) / 2;
        for (int dx = -radius; dx <= radius; dx++)
        {
            for (int dz = -radius; dz <= radius; dz++)
            {
                if (dx == 0 && dz == 0)
                {
                    continue;
                }
                long next = energyUsed + cost;
                if (next > tool.getEnergy(stack))
                {
                    continue;
                }
                int targetX = x + dx;
                int targetZ = z + dz;
                if (player.canPlayerEdit(targetX, y, targetZ, side, stack)
                      && world.isAirBlock(targetX, y + 1, targetZ)
                      && isTillable(world.getBlock(targetX, y, targetZ))
                      && till(tool, stack, player, world, targetX, y,
                            targetZ, side, cost))
                {
                    energyUsed = next;
                }
            }
        }
        tool.setEnergy(stack, tool.getEnergy(stack) - energyUsed);
        return true;
    }

    public static boolean shearEntity(ItemMekaTool tool, ItemStack stack,
          EntityPlayer player, EntityLivingBase entity)
    {
        if (entity.worldObj.isRemote
              || !MekaToolModuleHelper.hasShearing(stack)
              || !(entity instanceof IShearable)
              || tool.getEnergy(stack) < MekaSuitConfig.toolShearEntityUsage)
        {
            return false;
        }
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.posY);
        int z = MathHelper.floor_double(entity.posZ);
        IShearable target = (IShearable)entity;
        if (!target.isShearable(stack, entity.worldObj, x, y, z))
        {
            return false;
        }
        MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        List<ItemStack> drops = target.onSheared(stack, entity.worldObj,
              x, y, z, MekaToolModuleHelper.getFortuneLevel(stack));
        Random random = entity.getRNG();
        if (drops != null)
        {
            for (ItemStack drop : drops)
            {
                EntityItem item = entity.entityDropItem(drop, 1F);
                if (item != null)
                {
                    item.motionY += random.nextFloat() * 0.05F;
                    item.motionX += (random.nextFloat() - random.nextFloat())
                          * 0.1F;
                    item.motionZ += (random.nextFloat() - random.nextFloat())
                          * 0.1F;
                }
            }
        }
        tool.setEnergy(stack, tool.getEnergy(stack)
              - MekaSuitConfig.toolShearEntityUsage);
        return true;
    }

    public static boolean shearBlock(ItemMekaTool tool, ItemStack stack,
          int x, int y, int z, EntityPlayer player)
    {
        World world = player.worldObj;
        if (world.isRemote || player.capabilities.isCreativeMode
              || !MekaToolModuleHelper.hasShearing(stack))
        {
            return false;
        }
        Block block = world.getBlock(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);
        long cost = MekaToolModuleHelper.getMiningEnergyCost(stack,
              block.getBlockHardness(world, x, y, z));
        if (tool.getEnergy(stack) < cost)
        {
            return false;
        }
        if (block == Blocks.tripwire)
        {
            world.setBlockMetadataWithNotify(x, y, z, metadata | 8, 4);
            return false;
        }
        if (!(block instanceof IShearable))
        {
            return false;
        }
        IShearable target = (IShearable)block;
        if (!target.isShearable(stack, world, x, y, z))
        {
            return false;
        }
        MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        List<ItemStack> drops = target.onSheared(stack, world, x, y, z,
              MekaToolModuleHelper.getFortuneLevel(stack));
        if (drops != null)
        {
            for (ItemStack drop : drops)
            {
                float spread = 0.7F;
                double dropX = world.rand.nextFloat() * spread
                      + (1F - spread) * 0.5D;
                double dropY = world.rand.nextFloat() * spread
                      + (1F - spread) * 0.5D;
                double dropZ = world.rand.nextFloat() * spread
                      + (1F - spread) * 0.5D;
                EntityItem item = new EntityItem(world, x + dropX,
                      y + dropY, z + dropZ, drop);
                item.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(item);
            }
        }
        tool.setEnergy(stack, tool.getEnergy(stack) - cost);
        int blockId = Block.getIdFromBlock(block);
        if (blockId >= 0 && blockId < StatList.mineBlockStatArray.length)
        {
            StatBase stat = StatList.mineBlockStatArray[blockId];
            if (stat != null)
            {
                player.addStat(stat, 1);
            }
        }
        world.setBlockToAir(x, y, z);
        return true;
    }

    static boolean isTillable(Block block)
    {
        return block == Blocks.grass || block == Blocks.dirt;
    }

    private static boolean till(ItemMekaTool tool, ItemStack stack,
          EntityPlayer player, World world, int x, int y, int z, int side,
          long cost)
    {
        UseHoeEvent event = new UseHoeEvent(player, stack, world, x, y, z);
        if (MinecraftForge.EVENT_BUS.post(event)
              || event.getResult() == Result.DENY)
        {
            return false;
        }
        if (event.getResult() == Result.ALLOW)
        {
            return true;
        }
        if (side == 0 || !world.isAirBlock(x, y + 1, z)
              || !isTillable(world.getBlock(x, y, z)))
        {
            return false;
        }
        Block farmland = Blocks.farmland;
        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D,
              farmland.stepSound.getStepResourcePath(),
              (farmland.stepSound.getVolume() + 1F) / 2F,
              farmland.stepSound.getPitch() * 0.8F);
        return world.setBlock(x, y, z, farmland);
    }
}
