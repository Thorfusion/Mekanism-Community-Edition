package mekanism.mekasuit.common.content.gear;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import java.util.Map;
import java.util.WeakHashMap;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

/** Common hooks for pants and boots movement behavior. */
public final class MekaSuitMobilityHandler
{
    public static final MekaSuitMobilityHandler INSTANCE =
          new MekaSuitMobilityHandler();

    private final Map<EntityPlayer, Float> previousStepHeights =
          new WeakHashMap<EntityPlayer, Float>();
    private final Map<EntityPlayer, Float> appliedStepHeights =
          new WeakHashMap<EntityPlayer, Float>();

    private MekaSuitMobilityHandler()
    {
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase != Phase.END)
        {
            return;
        }
        EntityPlayer player = event.player;
        ItemStack pants = player.getCurrentArmor(1);
        ItemStack boots = player.getCurrentArmor(0);
        if (!isMekaSuit(pants) && !isMekaSuit(boots))
        {
            releaseStepAssist(player);
            return;
        }
        tickSprint(player, pants);
        tickStepAssist(player, boots);
        tickSwimBoost(player, pants);
        MekaSuitMobilityHelper.applySoulSurfer(player, boots);
        if (!player.worldObj.isRemote)
        {
            MekaSuitMobilityHelper.attractItems(player, boots);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer)
              || !event.entityLiving.worldObj.isRemote)
        {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entityLiving;
        if (!player.isSneaking())
        {
            return;
        }
        float sneakSpeed = MekaSuitMobilityHelper.getSneakSpeed(
              player.getCurrentArmor(1));
        if (sneakSpeed > 0.3F)
        {
            float multiplier = sneakSpeed / 0.3F;
            player.moveForward *= multiplier;
            player.moveStrafing *= multiplier;
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(BreakSpeed event)
    {
        EntityPlayer player = event.entityPlayer;
        if (player.isInsideOfMaterial(Material.water)
              && MekaSuitMobilityHelper.getGyroscopicLevel(
                    player.getCurrentArmor(1)) > 0)
        {
            event.newSpeed *= 5F;
        }
    }

    @SubscribeEvent
    public void onJump(LivingJumpEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer))
        {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entityLiving;
        if (!MekaSuitGravitationalHandler.INSTANCE.isBoosting(player))
        {
            return;
        }
        ItemStack boots = player.getCurrentArmor(0);
        float configuredBoost = MekaSuitMobilityHelper.getJumpBoost(boots);
        long usage = MekaSuitMobilityHelper.getJumpEnergyUsage(
              configuredBoost);
        if (configuredBoost <= 0
              || !MekaSuitMobilityHelper.canUseEnergy(boots, player, usage))
        {
            return;
        }
        ItemStack pants = player.getCurrentArmor(1);
        float appliedBoost =
              MekaSuitMobilityHelper.limitJumpBoostForSprint(configuredBoost,
                    MekaSuitMobilityHelper.canSprintBoost(pants, player));
        player.motionY += appliedBoost;
        player.velocityChanged = true;
        if (!player.worldObj.isRemote)
        {
            MekaSuitMobilityHelper.useEnergy(boots, player, usage);
        }
    }

    private static void tickSprint(EntityPlayer player, ItemStack pants)
    {
        float boost = MekaSuitMobilityHelper.getSprintBoost(pants);
        if (!MekaSuitMobilityHelper.canSprintBoost(pants, player))
        {
            return;
        }
        long usage = MekaSuitMobilityHelper.getSprintEnergyUsage(boost);
        if (!player.onGround)
        {
            boost /= 5F;
        }
        if (player.isInWater())
        {
            boost /= 5F;
        }
        player.moveFlying(0, 1, boost);
        player.velocityChanged = true;
        if (!player.worldObj.isRemote)
        {
            MekaSuitMobilityHelper.useEnergy(pants, player, usage);
        }
    }

    private void tickStepAssist(EntityPlayer player, ItemStack boots)
    {
        float assist = MekaSuitMobilityHelper.getStepHeight(boots);
        if (assist <= 0)
        {
            releaseStepAssist(player);
            return;
        }
        float target = 0.6F + assist;
        Float applied = appliedStepHeights.get(player);
        if (applied != null
              && Float.compare(player.stepHeight, applied.floatValue()) == 0)
        {
            player.stepHeight = target;
            appliedStepHeights.put(player, Float.valueOf(target));
        }
        else if (player.stepHeight < target)
        {
            if (!previousStepHeights.containsKey(player))
            {
                previousStepHeights.put(player,
                      Float.valueOf(player.stepHeight));
            }
            player.stepHeight = target;
            appliedStepHeights.put(player, Float.valueOf(target));
        }
        else if (applied != null)
        {
            previousStepHeights.remove(player);
            appliedStepHeights.remove(player);
        }
    }

    private void releaseStepAssist(EntityPlayer player)
    {
        Float applied = appliedStepHeights.remove(player);
        Float previous = previousStepHeights.remove(player);
        if (applied != null && previous != null
              && Float.compare(player.stepHeight,
                    applied.floatValue()) == 0)
        {
            player.stepHeight = previous.floatValue();
        }
    }

    private static void tickSwimBoost(EntityPlayer player,
          ItemStack pants)
    {
        if (!player.isInWater()
              || !MekaSuitMobilityHelper.hasSwimBoost(pants)
              || !MekaSuitMobilityHelper.canUseEnergy(pants, player,
                    MekaSuitConfig.suitHydrostaticRepulsionUsage))
        {
            return;
        }
        double horizontal = Math.sqrt(player.motionX * player.motionX
              + player.motionZ * player.motionZ);
        if (horizontal > 0.001D && horizontal < 0.8D)
        {
            double multiplier = Math.min(1.1D, 0.8D / horizontal);
            player.motionX *= multiplier;
            player.motionZ *= multiplier;
            player.velocityChanged = true;
        }
        if (!player.worldObj.isRemote)
        {
            MekaSuitMobilityHelper.useEnergy(pants, player,
                  MekaSuitConfig.suitHydrostaticRepulsionUsage);
        }
    }

    private static boolean isMekaSuit(ItemStack stack)
    {
        return stack != null
              && stack.getItem() instanceof ItemMekaSuitArmor;
    }
}
