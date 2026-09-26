package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/** Stable Gravitational Modulating Unit energy and movement rules adapted to 1.12 flight capabilities. */
public final class MekaSuitGravitationalHelper {

    public static final String SPEED_BOOST_CONFIG = "speed_boost";
    public static final int BOOST_ENERGY_MULTIPLIER = 4;

    private MekaSuitGravitationalHelper() {
    }

    public static ModuleData getModule(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemMekaSuitBodyarmor)) {
            return null;
        }
        return ModuleContainer.fromStack(stack, ModuleTarget.BODYARMOR)
              .get(MekaSuitModules.GRAVITATIONAL_MODULATING_UNIT);
    }

    public static boolean canProvideFlight(ItemStack stack) {
        ModuleData module = getModule(stack);
        return module != null && module.isEnabled() && hasEnergy(stack, getBaseEnergyUsage());
    }

    public static boolean canBoost(ItemStack stack, ModuleData module) {
        return module != null && module.isEnabled() && getSpeedBoost(module) > 0F
              && hasEnergy(stack, getBoostEnergyUsage());
    }

    public static float getSpeedBoost(ModuleData module) {
        if (module == null) {
            return 0F;
        }
        String value = module.getEnumConfig(SPEED_BOOST_CONFIG);
        if ("low".equals(value)) {
            return 0.05F;
        } else if ("med".equals(value)) {
            return 0.1F;
        } else if ("high".equals(value)) {
            return 0.25F;
        } else if ("ultra".equals(value)) {
            return 0.5F;
        }
        return 0F;
    }

    public static long getBaseEnergyUsage() {
        return Math.max(0L, MekaSuitConfig.suitGravitationalModulationUsage);
    }

    public static long getBoostEnergyUsage() {
        long base = getBaseEnergyUsage();
        return base > Long.MAX_VALUE / BOOST_ENERGY_MULTIPLIER
              ? Long.MAX_VALUE : base * BOOST_ENERGY_MULTIPLIER;
    }

    public static boolean hasEnergy(ItemStack stack, long amount) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemMekaSuitBodyarmor
              && ((ItemMekaSuitBodyarmor) stack.getItem()).getEnergy(stack) >= Math.max(0L, amount);
    }

    public static void useEnergy(ItemStack stack, long amount) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemMekaSuitBodyarmor) {
            ItemMekaSuitBodyarmor armor = (ItemMekaSuitBodyarmor) stack.getItem();
            armor.setEnergy(stack, armor.getEnergy(stack) - Math.max(0L, amount));
        }
    }

    /** Matches modern moveRelative(0, 0, 1) boost direction without changing vertical velocity. */
    public static void applyBoost(EntityPlayer player, float boost) {
        if (player == null || boost <= 0F) {
            return;
        }
        float yaw = player.rotationYaw * 0.017453292F;
        player.motionX -= MathHelper.sin(yaw) * boost;
        player.motionZ += MathHelper.cos(yaw) * boost;
    }
}
