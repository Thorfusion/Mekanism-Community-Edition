package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Stable Jetpack Unit mode, thrust, motion, and Hydrogen accounting adapted to 1.12 movement APIs. */
public final class MekaSuitJetpackHelper {

    public static final String NORMAL = "normal";
    public static final String HOVER = "hover";
    public static final String VECTOR = "vector";
    public static final String DISABLED = "disabled";
    public static final String THRUST_CONFIG = "jetpack_mult";
    public static final String HOVER_THRUST_CONFIG = "jetpack_mult.hover";

    private MekaSuitJetpackHelper() {
    }

    public static ModuleData getModule(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemMekaSuitBodyarmor)) {
            return null;
        }
        return ModuleContainer.fromStack(stack, ModuleTarget.BODYARMOR).get(MekaSuitModules.JETPACK_UNIT);
    }

    public static boolean hasModule(ItemStack stack) {
        return getModule(stack) != null;
    }

    public static String getMode(ItemStack stack) {
        ModuleData module = getModule(stack);
        return module != null && module.isEnabled() ? module.getMode() : DISABLED;
    }

    public static float getThrustMultiplier(ModuleData module) {
        if (module == null) {
            return 0F;
        }
        String key = HOVER.equals(module.getMode()) ? HOVER_THRUST_CONFIG : THRUST_CONFIG;
        return multiplier(module.getEnumConfig(key));
    }

    public static float multiplier(String value) {
        if ("half".equals(value)) {
            return 0.5F;
        } else if ("fast".equals(value)) {
            return 2F;
        } else if ("faster".equals(value)) {
            return 3F;
        } else if ("fastest".equals(value)) {
            return 4F;
        }
        return 1F;
    }

    public static int getFuelUsage(ModuleData module) {
        return MathHelper.ceil(getThrustMultiplier(module));
    }

    public static double getThrust(ItemStack stack) {
        ModuleData module = getModule(stack);
        if (module == null || !module.isEnabled()) {
            return 0D;
        }
        ItemMekaSuitBodyarmor bodyarmor = (ItemMekaSuitBodyarmor) stack.getItem();
        float multiplier = getThrustMultiplier(module);
        int needed = MathHelper.ceil(multiplier);
        int stored = bodyarmor.getStoredGas(stack);
        if (stored < needed) {
            multiplier = stored;
        }
        return 0.15D * multiplier;
    }

    public static boolean isActive(ItemStack stack, EntityPlayer player, boolean ascending, boolean descending) {
        if (player == null || player.isSpectator() || player.isRiding()
              || !(stack.getItem() instanceof ItemMekaSuitBodyarmor)
              || ((ItemMekaSuitBodyarmor) stack.getItem()).getStoredGas(stack) <= 0) {
            return false;
        }
        String mode = getMode(stack);
        if (NORMAL.equals(mode) || VECTOR.equals(mode)) {
            return ascending;
        }
        return HOVER.equals(mode) && ((ascending && !descending) || !mekanism.common.CommonPlayerTickHandler.isOnGround(player));
    }

    /** Applies one tick of motion and returns whether fall distance should be reset. */
    public static boolean applyMotion(EntityPlayer player, String mode, double thrust,
          boolean ascending, boolean descending) {
        if (thrust <= 0D) {
            return false;
        }
        if (VECTOR.equals(mode) && player.isSneaking()) {
            mode = NORMAL;
        }
        if (NORMAL.equals(mode)) {
            player.motionX += 0.08D * player.motionX;
            player.motionY += thrust * verticalCoefficient(player.motionY);
            player.motionZ += 0.08D * player.motionZ;
        } else if (VECTOR.equals(mode)) {
            Vec3d up = upVector(player.rotationPitch, player.rotationYaw).scale(thrust);
            player.motionX += up.x;
            player.motionY += up.y * verticalCoefficient(player.motionY);
            player.motionZ += up.z;
        } else if (HOVER.equals(mode)) {
            if (ascending == descending) {
                if (player.motionY > 0D) {
                    player.motionY = Math.max(player.motionY - thrust, 0D);
                } else if (player.motionY < 0D && !mekanism.common.CommonPlayerTickHandler.isOnGround(player)) {
                    player.motionY = Math.min(player.motionY + thrust, 0D);
                }
            } else if (ascending) {
                player.motionY = Math.min(player.motionY + thrust, 2D * thrust);
            } else if (!mekanism.common.CommonPlayerTickHandler.isOnGround(player)) {
                player.motionY = Math.max(player.motionY - thrust, -2D * thrust);
            }
        } else {
            return false;
        }
        return true;
    }

    public static void useFuel(ItemStack stack) {
        ModuleData module = getModule(stack);
        if (module != null && module.isEnabled() && stack.getItem() instanceof ItemMekaSuitBodyarmor) {
            ((ItemMekaSuitBodyarmor) stack.getItem()).useGas(stack, getFuelUsage(module));
        }
    }

    static double verticalCoefficient(double currentYVelocity) {
        return Math.min(1D, Math.exp(-currentYVelocity));
    }

    static Vec3d upVector(float pitch, float yaw) {
        float pitchRadians = (pitch - 90F) * 0.017453292F;
        float yawRadians = -yaw * 0.017453292F - (float) Math.PI;
        float yawCos = MathHelper.cos(yawRadians);
        float yawSin = MathHelper.sin(yawRadians);
        float pitchCos = -MathHelper.cos(pitchRadians);
        float pitchSin = -MathHelper.sin(pitchRadians);
        return new Vec3d(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }
}
