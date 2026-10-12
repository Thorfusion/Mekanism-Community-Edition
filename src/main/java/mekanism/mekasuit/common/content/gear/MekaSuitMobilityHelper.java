package mekanism.mekasuit.common.content.gear;

import java.util.List;
import mekanism.api.energy.IEnergizedItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

/** Native 1.7 adapters for stable MekaSuit pants and boots units. */
public final class MekaSuitMobilityHelper
{
    private MekaSuitMobilityHelper()
    {
    }

    public static ModuleData getEnabled(ItemStack stack, ModuleTarget target,
          ModuleType type)
    {
        if (stack == null
              || !(stack.getItem() instanceof ItemMekaSuitArmor))
        {
            return null;
        }
        ModuleData data = ModuleContainer.fromStack(stack, target).get(type);
        return data != null && data.isEnabled() ? data : null;
    }

    public static float getSprintBoost(ItemStack pants)
    {
        ModuleData module = getEnabled(pants, ModuleTarget.PANTS,
              MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT);
        return module == null ? 0 : sprintBoost(module.getMode());
    }

    static float sprintBoost(String mode)
    {
        if ("low".equals(mode))
        {
            return 0.05F;
        }
        if ("med".equals(mode))
        {
            return 0.1F;
        }
        if ("high".equals(mode))
        {
            return 0.25F;
        }
        if ("ultra".equals(mode))
        {
            return 0.5F;
        }
        return 0;
    }

    public static long getSprintEnergyUsage(float boost)
    {
        return scaledEnergy(MekaSuitConfig.suitSprintBoostUsage,
              boost / 0.1D);
    }

    public static boolean canSprintBoost(ItemStack pants,
          EntityPlayer player)
    {
        float boost = getSprintBoost(pants);
        return boost > 0 && player.isSprinting()
              && canUseEnergy(pants, player, getSprintEnergyUsage(boost));
    }

    public static int getGyroscopicLevel(ItemStack pants)
    {
        ModuleData module = getEnabled(pants, ModuleTarget.PANTS,
              MekaSuitModules.GYROSCOPIC_STABILIZATION_UNIT);
        return module == null ? 0 : module.getInstalledCount();
    }

    public static int getHydrostaticLevel(ItemStack pants)
    {
        ModuleData module = getEnabled(pants, ModuleTarget.PANTS,
              MekaSuitModules.HYDROSTATIC_REPULSOR_UNIT);
        return module == null ? 0 : module.getInstalledCount();
    }

    public static boolean hasSwimBoost(ItemStack pants)
    {
        ModuleData module = getEnabled(pants, ModuleTarget.PANTS,
              MekaSuitModules.HYDROSTATIC_REPULSOR_UNIT);
        return module != null && module.getInstalledCount() >= 4
              && module.getBooleanConfig("swim_boost");
    }

    public static int getMotorizedServoLevel(ItemStack pants)
    {
        ModuleData module = getEnabled(pants, ModuleTarget.PANTS,
              MekaSuitModules.MOTORIZED_SERVO_UNIT);
        return module == null ? 0 : module.getInstalledCount();
    }

    public static float getSneakSpeed(ItemStack pants)
    {
        return sneakSpeed(getMotorizedServoLevel(pants));
    }

    static float sneakSpeed(int installed)
    {
        return 0.3F + Math.min(1F, 0.15F * Math.max(0, installed));
    }

    public static float getJumpBoost(ItemStack boots)
    {
        ModuleData module = getEnabled(boots, ModuleTarget.BOOTS,
              MekaSuitModules.HYDRAULIC_PROPULSION_UNIT);
        return module == null ? 0
              : hydraulicSetting(module.getEnumConfig("jump_boost"));
    }

    public static long getJumpEnergyUsage(float boost)
    {
        return scaledEnergy(MekaSuitConfig.suitBaseJumpUsage,
              boost / 0.1D);
    }

    public static float limitJumpBoostForSprint(float boost,
          boolean sprintBoostActive)
    {
        return sprintBoostActive ? (float)Math.sqrt(boost) : boost;
    }

    public static float getStepHeight(ItemStack boots)
    {
        ModuleData module = getEnabled(boots, ModuleTarget.BOOTS,
              MekaSuitModules.HYDRAULIC_PROPULSION_UNIT);
        return module == null ? 0
              : stepSetting(module.getEnumConfig("step_assist"));
    }

    static float hydraulicSetting(String value)
    {
        if ("low".equals(value))
        {
            return 0.5F;
        }
        if ("med".equals(value))
        {
            return 1F;
        }
        if ("high".equals(value))
        {
            return 3F;
        }
        if ("ultra".equals(value))
        {
            return 5F;
        }
        return 0;
    }

    static float stepSetting(String value)
    {
        if ("low".equals(value))
        {
            return 0.5F;
        }
        if ("med".equals(value))
        {
            return 1F;
        }
        if ("high".equals(value))
        {
            return 1.5F;
        }
        if ("ultra".equals(value))
        {
            return 2F;
        }
        return 0;
    }

    public static float getMagneticRange(ItemStack boots)
    {
        ModuleData module = getEnabled(boots, ModuleTarget.BOOTS,
              MekaSuitModules.MAGNETIC_ATTRACTION_UNIT);
        return module == null ? 0
              : magneticRange(module.getEnumConfig("range"));
    }

    static float magneticRange(String value)
    {
        if ("low".equals(value))
        {
            return 1F;
        }
        if ("med".equals(value))
        {
            return 3F;
        }
        if ("high".equals(value))
        {
            return 5F;
        }
        if ("ultra".equals(value))
        {
            return 10F;
        }
        return 0;
    }

    public static int getSoulSurferLevel(ItemStack boots)
    {
        ModuleData module = getEnabled(boots, ModuleTarget.BOOTS,
              MekaSuitModules.SOUL_SURFER_UNIT);
        return module == null ? 0 : module.getInstalledCount();
    }

    @SuppressWarnings("unchecked")
    public static void attractItems(EntityPlayer player, ItemStack boots)
    {
        float range = getMagneticRange(boots);
        if (range <= 0)
        {
            return;
        }
        long usage = scaledEnergy(MekaSuitConfig.suitItemAttractionUsage,
              range);
        double reach = 4D + range;
        AxisAlignedBB box = player.boundingBox.expand(reach, reach, reach);
        List<EntityItem> items = player.worldObj.getEntitiesWithinAABB(
              EntityItem.class, box);
        for (EntityItem item : items)
        {
            if (item == null || item.isDead
                  || item.delayBeforeCanPickup == Short.MAX_VALUE
                  || item.getDistanceSqToEntity(player) <= 0.001D
                  || !canUseEnergy(boots, player, usage))
            {
                continue;
            }
            double dx = clamp(player.posX - item.posX, -1, 1);
            double dy = clamp(player.posY + 0.2D - item.posY, -1, 1);
            double dz = clamp(player.posZ - item.posZ, -1, 1);
            item.motionX += (dx - item.motionX) * 0.2D;
            item.motionY += (dy - item.motionY) * 0.2D;
            item.motionZ += (dz - item.motionZ) * 0.2D;
            item.velocityChanged = true;
            useEnergy(boots, player, usage);
        }
    }

    public static void applySoulSurfer(EntityPlayer player, ItemStack boots)
    {
        int level = getSoulSurferLevel(boots);
        if (level <= 0 || !player.onGround)
        {
            return;
        }
        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.boundingBox.minY - 0.2D);
        int z = MathHelper.floor_double(player.posZ);
        if (player.worldObj.getBlock(x, y, z) != Blocks.soul_sand)
        {
            return;
        }
        double efficiency = Math.min(1D, level / 3D);
        double correction = (0.4D + 0.6D * efficiency) / 0.4D;
        player.motionX *= correction;
        player.motionZ *= correction;
        player.velocityChanged = true;
    }

    public static boolean canUseEnergy(ItemStack stack, EntityPlayer player,
          long amount)
    {
        if (amount <= 0 || player.capabilities.isCreativeMode)
        {
            return true;
        }
        return stack != null && stack.getItem() instanceof IEnergizedItem
              && ((IEnergizedItem)stack.getItem()).getEnergy(stack) >= amount;
    }

    public static boolean useEnergy(ItemStack stack, EntityPlayer player,
          long amount)
    {
        if (!canUseEnergy(stack, player, amount))
        {
            return false;
        }
        if (amount > 0 && !player.capabilities.isCreativeMode)
        {
            IEnergizedItem energized = (IEnergizedItem)stack.getItem();
            energized.setEnergy(stack,
                  energized.getEnergy(stack) - amount);
        }
        return true;
    }

    public static int getColor(ItemStack armor)
    {
        if (armor == null
              || !(armor.getItem() instanceof ItemMekaSuitArmor))
        {
            return 0xFFFFFF;
        }
        ItemMekaSuitArmor item = (ItemMekaSuitArmor)armor.getItem();
        ModuleData module = ModuleContainer.fromStack(armor,
              item.getModuleTarget()).get(
                    MekaSuitModules.COLOR_MODULATION_UNIT);
        return module == null ? 0xFFFFFF
              : color(module.getEnumConfig("color"));
    }

    static int color(String name)
    {
        if ("orange".equals(name)) return 0xF9801D;
        if ("magenta".equals(name)) return 0xC74EBD;
        if ("light_blue".equals(name)) return 0x3AB3DA;
        if ("yellow".equals(name)) return 0xFED83D;
        if ("lime".equals(name)) return 0x80C71F;
        if ("pink".equals(name)) return 0xF38BAA;
        if ("gray".equals(name)) return 0x474F52;
        if ("light_gray".equals(name)) return 0x9D9D97;
        if ("cyan".equals(name)) return 0x169C9C;
        if ("purple".equals(name)) return 0x8932B8;
        if ("blue".equals(name)) return 0x3C44AA;
        if ("brown".equals(name)) return 0x835432;
        if ("green".equals(name)) return 0x5E7C16;
        if ("red".equals(name)) return 0xB02E26;
        if ("black".equals(name)) return 0x1D1D21;
        return 0xFFFFFF;
    }

    private static long scaledEnergy(long base, double multiplier)
    {
        if (base <= 0 || multiplier <= 0)
        {
            return 0;
        }
        double value = base * multiplier;
        return Double.isNaN(value) || Double.isInfinite(value)
              || value >= Long.MAX_VALUE ? Long.MAX_VALUE
              : Math.max(1, (long)Math.ceil(value));
    }

    private static double clamp(double value, double minimum,
          double maximum)
    {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
