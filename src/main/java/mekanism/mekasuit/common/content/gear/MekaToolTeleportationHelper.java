package mekanism.mekasuit.common.content.gear;

import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketPortalFX.PortalFXMessage;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaTool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

/** Stable Meka-Tool short-range teleportation adapted to Forge 1.12. */
public final class MekaToolTeleportationHelper {

    private MekaToolTeleportationHelper() {
    }

    public static EnumActionResult use(ItemMekaTool tool, ItemStack stack, EntityPlayer player, World world) {
        if (!MekaToolModuleHelper.hasTeleportation(stack)) {
            return EnumActionResult.PASS;
        }
        Vec3d start = player.getPositionEyes(1F);
        Vec3d look = player.getLook(1F);
        Vec3d end = start.add(look.x * MekaSuitConfig.toolMaxTeleportReach,
              look.y * MekaSuitConfig.toolMaxTeleportReach, look.z * MekaSuitConfig.toolMaxTeleportReach);
        RayTraceResult hit = world.rayTraceBlocks(start, end, false, true, false);
        boolean missed = hit == null || hit.typeOfHit == RayTraceResult.Type.MISS;
        if (missed && MekaToolModuleHelper.requiresTeleportationTarget(stack)) {
            return EnumActionResult.PASS;
        }
        BlockPos target = missed ? new BlockPos(end) : hit.getBlockPos();
        if (!isSafeTarget(world, target)) {
            return EnumActionResult.PASS;
        }
        double distanceSquared = player.getDistanceSq(target.getX(), target.getY(), target.getZ());
        if (distanceSquared < 5D) {
            return EnumActionResult.PASS;
        }
        long energyNeeded = MekaToolModuleHelper.getTeleportationEnergyCost(distanceSquared);
        if (tool.getEnergy(stack) < energyNeeded) {
            return EnumActionResult.FAIL;
        }
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        double targetX = target.getX() + 0.5D;
        double targetY = target.getY() + 1.5D;
        double targetZ = target.getZ() + 0.5D;
        EnderTeleportEvent event = new EnderTeleportEvent(player, targetX, targetY, targetZ, 0);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return EnumActionResult.FAIL;
        }
        // Modern Mekanism deliberately keeps the ray-traced destination fixed;
        // use Forge's event as a cancellation hook without accepting redirects.
        tool.setEnergy(stack, tool.getEnergy(stack) - energyNeeded);
        if (player.isRiding()) {
            player.dismountRidingEntity();
        }
        player.setPositionAndUpdate(targetX, targetY, targetZ);
        player.fallDistance = 0;
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.floatingTickCount = 0;
        }
        Coord4D effectPos = new Coord4D(target.up(), world);
        Mekanism.packetHandler.sendToAllTracking(new PortalFXMessage(effectPos), effectPos);
        world.playSound(null, targetX, targetY, targetZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
              SoundCategory.PLAYERS, 1F, 1F);
        return EnumActionResult.SUCCESS;
    }

    static boolean isSafeTarget(World world, BlockPos target) {
        BlockPos feet = target.up();
        BlockPos head = target.up(2);
        return head.getY() < world.getHeight() && world.isBlockLoaded(feet) && world.isBlockLoaded(head)
              && isAirOrLiquid(world, feet) && isAirOrLiquid(world, head);
    }

    private static boolean isAirOrLiquid(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return world.isAirBlock(pos) || state.getMaterial().isLiquid();
    }
}
