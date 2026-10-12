package mekanism.mekasuit.common.content.gear;

import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketPortalFX.PortalFXMessage;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaTool;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

/** Stable short-range Meka-Tool teleportation on the 1.7 ray-trace API. */
public final class MekaToolTeleportationHelper
{
    private MekaToolTeleportationHelper()
    {
    }

    public static boolean use(ItemMekaTool tool, ItemStack stack,
          EntityPlayer player, World world)
    {
        if (!MekaToolModuleHelper.hasTeleportation(stack))
        {
            return false;
        }
        Vec3 start = Vec3.createVectorHelper(player.posX,
              player.posY + player.getEyeHeight(), player.posZ);
        Vec3 look = player.getLook(1F);
        double reach = MekaSuitConfig.toolMaxTeleportReach;
        Vec3 end = start.addVector(look.xCoord * reach,
              look.yCoord * reach, look.zCoord * reach);
        MovingObjectPosition hit = world.func_147447_a(start, end,
              false, true, false);
        boolean missed = hit == null
              || hit.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
        if (missed && MekaToolModuleHelper.requiresTeleportationTarget(stack))
        {
            return false;
        }
        int x = missed ? MathHelper.floor_double(end.xCoord) : hit.blockX;
        int y = missed ? MathHelper.floor_double(end.yCoord) : hit.blockY;
        int z = missed ? MathHelper.floor_double(end.zCoord) : hit.blockZ;
        if (!isSafeTarget(world, x, y, z))
        {
            return false;
        }
        double distanceSquared = player.getDistanceSq(x, y, z);
        if (distanceSquared < 5D)
        {
            return false;
        }
        long energyNeeded = MekaToolModuleHelper
              .getTeleportationEnergyCost(distanceSquared);
        if (tool.getEnergy(stack) < energyNeeded)
        {
            return false;
        }
        if (world.isRemote)
        {
            return true;
        }

        double targetX = x + 0.5D;
        double targetY = y + 1.5D;
        double targetZ = z + 0.5D;
        EnderTeleportEvent event = new EnderTeleportEvent(player, targetX,
              targetY, targetZ, 0);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return false;
        }
        tool.setEnergy(stack, tool.getEnergy(stack) - energyNeeded);
        if (player.ridingEntity != null)
        {
            player.mountEntity(null);
        }
        if (player instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)player).setPositionAndUpdate(targetX, targetY,
                  targetZ);
        }
        else
        {
            player.setPosition(targetX, targetY, targetZ);
        }
        player.fallDistance = 0;
        Coord4D effect = new Coord4D(x, y + 1, z,
              world.provider.dimensionId);
        Mekanism.packetHandler.sendToAllAround(new PortalFXMessage(effect),
              effect.getTargetPoint(40D));
        world.playSoundEffect(targetX, targetY, targetZ,
              "mob.endermen.portal", 1F, 1F);
        return true;
    }

    static boolean isSafeTarget(World world, int x, int y, int z)
    {
        int feetY = y + 1;
        int headY = y + 2;
        return headY < world.getHeight()
              && world.blockExists(x, feetY, z)
              && world.blockExists(x, headY, z)
              && isAirOrLiquid(world, x, feetY, z)
              && isAirOrLiquid(world, x, headY, z);
    }

    private static boolean isAirOrLiquid(World world, int x, int y, int z)
    {
        Block block = world.getBlock(x, y, z);
        return block.isAir(world, x, y, z)
              || block.getMaterial().isLiquid();
    }
}
