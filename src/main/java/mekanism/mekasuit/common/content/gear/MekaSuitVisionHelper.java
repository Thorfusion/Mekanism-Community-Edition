package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Common energy and installed-count behavior for Vision Enhancement. */
public final class MekaSuitVisionHelper
{
    private MekaSuitVisionHelper()
    {
    }

    public static void tickServer(ItemStack helmet, EntityPlayer player)
    {
        ModuleData module = getModule(helmet);
        if (module == null || !module.isEnabled() || player == null
              || player.capabilities.isCreativeMode)
        {
            return;
        }
        long usage = Math.max(0, MekaSuitConfig.suitVisionEnhancementUsage);
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)helmet.getItem();
        if (usage > 0 && hasEnoughEnergy(armor.getEnergy(helmet), usage))
        {
            armor.setEnergy(helmet,
                  getRemainingEnergy(armor.getEnergy(helmet), usage));
        }
    }

    public static boolean isActive(ItemStack helmet, EntityPlayer player)
    {
        ModuleData module = getModule(helmet);
        if (module == null || !module.isEnabled() || player == null)
        {
            return false;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)helmet.getItem();
        return player.capabilities.isCreativeMode || hasEnoughEnergy(
              armor.getEnergy(helmet),
              Math.max(0, MekaSuitConfig.suitVisionEnhancementUsage));
    }

    public static ModuleData getModule(ItemStack helmet)
    {
        if (helmet == null
              || !(helmet.getItem() instanceof ItemMekaSuitArmor))
        {
            return null;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)helmet.getItem();
        return armor.getModuleTarget() == ModuleTarget.HELMET
              ? ModuleContainer.fromStack(helmet, ModuleTarget.HELMET).get(
                    MekaSuitModules.VISION_ENHANCEMENT_UNIT) : null;
    }

    static boolean hasEnoughEnergy(double storedEnergy, long usage)
    {
        return usage <= 0 || !Double.isNaN(storedEnergy)
              && storedEnergy >= usage;
    }

    static double getRemainingEnergy(double storedEnergy, long usage)
    {
        if (Double.isNaN(storedEnergy) || storedEnergy <= 0)
        {
            return 0;
        }
        return usage <= 0 ? storedEnergy : Math.max(0, storedEnergy - usage);
    }

    public static float getFogDistanceScale(int installedCount)
    {
        int clamped = Math.max(1, Math.min(4, installedCount));
        return (float)Math.pow(clamped, 1.25D) / 4F;
    }
}
