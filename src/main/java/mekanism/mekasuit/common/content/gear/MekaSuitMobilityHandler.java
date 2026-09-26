package mekanism.mekasuit.common.content.gear;

import java.util.Map;
import java.util.WeakHashMap;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/** Common player hooks for MekaSuit pants and boots movement behavior. */
public final class MekaSuitMobilityHandler {

    public static final MekaSuitMobilityHandler INSTANCE = new MekaSuitMobilityHandler();

    /** Only restore step height when the value is still the one this handler applied. */
    private final Map<EntityPlayer, Float> previousStepHeights = new WeakHashMap<>();
    private final Map<EntityPlayer, Float> appliedStepHeights = new WeakHashMap<>();

    private MekaSuitMobilityHandler() {
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        EntityPlayer player = event.player;
        ItemStack pants = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!isMekaSuit(pants) && !isMekaSuit(boots)) {
            releaseStepAssist(player);
            return;
        }

        MekaSuitMobilityHelper.synchronizeMovementEnchantments(pants, boots);
        tickSprint(player, pants);
        tickStepAssist(player, boots);
        tickSwimBoost(player, pants);
        MekaSuitMobilityHelper.applySoulSurfer(player, boots);
        if (!player.world.isRemote) {
            MekaSuitMobilityHelper.attractItems(player, boots);
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.isInsideOfMaterial(net.minecraft.block.material.Material.WATER)) {
            ItemStack pants = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            if (MekaSuitMobilityHelper.getGyroscopicLevel(pants) > 0) {
                event.setNewSpeed(event.getNewSpeed() * 5F);
            }
        }
    }

    @SubscribeEvent
    public void onJump(LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (!MekaSuitGravitationalHandler.INSTANCE.isBoosting(player)) {
            return;
        }
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        float configuredBoost = MekaSuitMobilityHelper.getJumpBoost(boots);
        long usage = MekaSuitMobilityHelper.getJumpEnergyUsage(configuredBoost);
        if (configuredBoost <= 0 || !MekaSuitMobilityHelper.canUseEnergy(boots, player, usage)) {
            return;
        }
        ItemStack pants = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        float appliedBoost = MekaSuitMobilityHelper.limitJumpBoostForSprint(configuredBoost,
              MekaSuitMobilityHelper.canSprintBoost(pants, player));
        player.motionY += appliedBoost;
        player.velocityChanged = true;
        if (!player.world.isRemote) {
            MekaSuitMobilityHelper.useEnergy(boots, player, usage);
        }
    }

    private static void tickSprint(EntityPlayer player, ItemStack pants) {
        float boost = MekaSuitMobilityHelper.getSprintBoost(pants);
        if (!MekaSuitMobilityHelper.canSprintBoost(pants, player)) {
            return;
        }
        long usage = MekaSuitMobilityHelper.getSprintEnergyUsage(boost);
        if (!MekaSuitMobilityHelper.canUseEnergy(pants, player, usage)) {
            return;
        }
        if (!player.onGround) {
            boost /= 5F;
        }
        if (player.isInWater()) {
            boost /= 5F;
        }
        player.moveRelative(0F, 0F, 1F, boost);
        player.velocityChanged = true;
        if (!player.world.isRemote) {
            MekaSuitMobilityHelper.useEnergy(pants, player, usage);
        }
    }

    private void tickStepAssist(EntityPlayer player, ItemStack boots) {
        float assist = MekaSuitMobilityHelper.getStepHeight(boots);
        if (assist <= 0) {
            releaseStepAssist(player);
            return;
        }
        float target = 0.6F + assist;
        Float applied = appliedStepHeights.get(player);
        if (applied != null && Float.compare(player.stepHeight, applied) == 0) {
            player.stepHeight = target;
            appliedStepHeights.put(player, target);
        } else if (player.stepHeight < target) {
            previousStepHeights.putIfAbsent(player, player.stepHeight);
            player.stepHeight = target;
            appliedStepHeights.put(player, target);
        } else if (applied != null) {
            // Another mod deliberately replaced our value. Stop owning it.
            previousStepHeights.remove(player);
            appliedStepHeights.remove(player);
        }
    }

    private void releaseStepAssist(EntityPlayer player) {
        Float applied = appliedStepHeights.remove(player);
        Float previous = previousStepHeights.remove(player);
        if (applied != null && previous != null && Float.compare(player.stepHeight, applied) == 0) {
            player.stepHeight = previous;
        }
    }

    private static void tickSwimBoost(EntityPlayer player, ItemStack pants) {
        if (!player.isInWater() || !MekaSuitMobilityHelper.hasSwimBoost(pants)
              || !MekaSuitMobilityHelper.canUseEnergy(pants, player,
              mekanism.mekasuit.common.config.MekaSuitConfig.suitHydrostaticRepulsionUsage)) {
            return;
        }
        double horizontal = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
        if (horizontal > 0.001D && horizontal < 0.8D) {
            double multiplier = Math.min(1.1D, 0.8D / horizontal);
            player.motionX *= multiplier;
            player.motionZ *= multiplier;
            player.velocityChanged = true;
        }
        if (!player.world.isRemote) {
            MekaSuitMobilityHelper.useEnergy(pants, player,
                  mekanism.mekasuit.common.config.MekaSuitConfig.suitHydrostaticRepulsionUsage);
        }
    }

    private static boolean isMekaSuit(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemMekaSuitArmor;
    }
}
