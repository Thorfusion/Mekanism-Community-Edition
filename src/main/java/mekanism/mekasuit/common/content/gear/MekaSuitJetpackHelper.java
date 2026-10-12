package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

/** Stable Jetpack Unit modes, thrust, motion, and Hydrogen accounting. */
public final class MekaSuitJetpackHelper
{
    public static final String NORMAL = "normal";
    public static final String HOVER = "hover";
    public static final String VECTOR = "vector";
    public static final String DISABLED = "disabled";
    public static final String THRUST_CONFIG = "jetpack_mult";
    public static final String HOVER_THRUST_CONFIG = "jetpack_mult.hover";

    private MekaSuitJetpackHelper()
    {
    }

    public static ModuleData getModule(ItemStack stack)
    {
        if (stack == null
              || !(stack.getItem() instanceof ItemMekaSuitBodyarmor))
        {
            return null;
        }
        return ModuleContainer.fromStack(stack, ModuleTarget.BODYARMOR).get(
              MekaSuitModules.JETPACK_UNIT);
    }

    public static boolean hasModule(ItemStack stack)
    {
        return getModule(stack) != null;
    }

    public static String getMode(ItemStack stack)
    {
        ModuleData module = getModule(stack);
        return module != null && module.isEnabled()
              ? module.getMode() : DISABLED;
    }

    public static float getThrustMultiplier(ModuleData module)
    {
        if (module == null)
        {
            return 0;
        }
        String key = HOVER.equals(module.getMode())
              ? HOVER_THRUST_CONFIG : THRUST_CONFIG;
        return multiplier(module.getEnumConfig(key));
    }

    static float multiplier(String value)
    {
        if ("half".equals(value))
        {
            return 0.5F;
        }
        if ("fast".equals(value))
        {
            return 2F;
        }
        if ("faster".equals(value))
        {
            return 3F;
        }
        if ("fastest".equals(value))
        {
            return 4F;
        }
        return 1F;
    }

    public static int getFuelUsage(ModuleData module)
    {
        return MathHelper.ceiling_float_int(getThrustMultiplier(module));
    }

    public static double getThrust(ItemStack stack)
    {
        ModuleData module = getModule(stack);
        if (module == null || !module.isEnabled())
        {
            return 0;
        }
        ItemMekaSuitBodyarmor bodyarmor =
              (ItemMekaSuitBodyarmor)stack.getItem();
        float multiplier = getThrustMultiplier(module);
        int needed = MathHelper.ceiling_float_int(multiplier);
        int stored = bodyarmor.getStoredGas(stack);
        if (stored < needed)
        {
            multiplier = stored;
        }
        return 0.15D * multiplier;
    }

    public static boolean isActive(ItemStack stack, EntityPlayer player,
          boolean ascending, boolean descending)
    {
        if (player == null || player.ridingEntity != null || stack == null
              || !(stack.getItem() instanceof ItemMekaSuitBodyarmor)
              || ((ItemMekaSuitBodyarmor)stack.getItem()).getStoredGas(stack)
                    <= 0)
        {
            return false;
        }
        String mode = getMode(stack);
        if (NORMAL.equals(mode) || VECTOR.equals(mode))
        {
            return ascending;
        }
        return HOVER.equals(mode) && ((ascending && !descending)
              || !mekanism.common.CommonPlayerTickHandler.isOnGround(player));
    }

    /** Applies one tick of motion and reports whether fall distance resets. */
    public static boolean applyMotion(EntityPlayer player, String mode,
          double thrust, boolean ascending, boolean descending)
    {
        if (player == null || thrust <= 0)
        {
            return false;
        }
        if (VECTOR.equals(mode) && player.isSneaking())
        {
            mode = NORMAL;
        }
        if (NORMAL.equals(mode))
        {
            player.motionX += 0.08D * player.motionX;
            player.motionY += thrust * verticalCoefficient(player.motionY);
            player.motionZ += 0.08D * player.motionZ;
        }
        else if (VECTOR.equals(mode))
        {
            double[] vector = upVector(player.rotationPitch,
                  player.rotationYaw);
            player.motionX += vector[0] * thrust;
            player.motionY += vector[1] * thrust
                  * verticalCoefficient(player.motionY);
            player.motionZ += vector[2] * thrust;
        }
        else if (HOVER.equals(mode))
        {
            if (ascending == descending)
            {
                if (player.motionY > 0)
                {
                    player.motionY = Math.max(player.motionY - thrust, 0);
                }
                else if (player.motionY < 0
                      && !mekanism.common.CommonPlayerTickHandler.isOnGround(
                            player))
                {
                    player.motionY = Math.min(player.motionY + thrust, 0);
                }
            }
            else if (ascending)
            {
                player.motionY = Math.min(player.motionY + thrust,
                      2D * thrust);
            }
            else if (!mekanism.common.CommonPlayerTickHandler.isOnGround(
                  player))
            {
                player.motionY = Math.max(player.motionY - thrust,
                      -2D * thrust);
            }
        }
        else
        {
            return false;
        }
        player.velocityChanged = true;
        return true;
    }

    public static void useFuel(ItemStack stack)
    {
        ModuleData module = getModule(stack);
        if (module != null && module.isEnabled()
              && stack.getItem() instanceof ItemMekaSuitBodyarmor)
        {
            ((ItemMekaSuitBodyarmor)stack.getItem()).useGas(stack,
                  getFuelUsage(module));
        }
    }

    static double verticalCoefficient(double currentYVelocity)
    {
        return Math.min(1D, Math.exp(-currentYVelocity));
    }

    static double[] upVector(float pitch, float yaw)
    {
        float pitchRadians = (pitch - 90F) * 0.017453292F;
        float yawRadians = -yaw * 0.017453292F - (float)Math.PI;
        float yawCos = MathHelper.cos(yawRadians);
        float yawSin = MathHelper.sin(yawRadians);
        float pitchCos = -MathHelper.cos(pitchRadians);
        float pitchSin = -MathHelper.sin(pitchRadians);
        return new double[] {yawSin * pitchCos, pitchSin,
              yawCos * pitchCos};
    }
}
