package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Common energy and installed-count behavior for the Vision Enhancement Unit. */
public final class MekaSuitVisionHelper {

    private MekaSuitVisionHelper() {
    }

    public static void tickServer(ItemStack helmet, EntityPlayer player) {
        ModuleData module = getModule(helmet);
        if (module == null || !module.isEnabled() || player == null || player.capabilities.isCreativeMode) {
            return;
        }
        long usage = Math.max(0, MekaSuitConfig.suitVisionEnhancementUsage);
        if (usage > 0) {
            ItemMekaSuitArmor armor = (ItemMekaSuitArmor) helmet.getItem();
            armor.setEnergy(helmet, getRemainingEnergy(armor.getEnergy(helmet), usage));
        }
    }

    public static boolean isActive(ItemStack helmet, EntityPlayer player) {
        ModuleData module = getModule(helmet);
        if (module == null || !module.isEnabled() || player == null
              || player.getCooldownTracker().hasCooldown(helmet.getItem())) {
            return false;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor) helmet.getItem();
        return hasEnoughEnergy(armor.getEnergy(helmet), Math.max(0, MekaSuitConfig.suitVisionEnhancementUsage));
    }

    public static ModuleData getModule(ItemStack helmet) {
        if (helmet == null || helmet.isEmpty() || !(helmet.getItem() instanceof ItemMekaSuitArmor)) {
            return null;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor) helmet.getItem();
        if (armor.getModuleTarget() != ModuleTarget.HELMET) {
            return null;
        }
        return ModuleContainer.fromStack(helmet, ModuleTarget.HELMET)
              .get(MekaSuitModules.VISION_ENHANCEMENT_UNIT);
    }

    static boolean hasEnoughEnergy(double storedEnergy, long usage) {
        return usage <= 0 || !Double.isNaN(storedEnergy) && storedEnergy >= usage;
    }

    static double getRemainingEnergy(double storedEnergy, long usage) {
        if (Double.isNaN(storedEnergy) || storedEnergy <= 0) {
            return 0;
        }
        if (usage <= 0) {
            return storedEnergy;
        }
        return Math.max(0, storedEnergy - usage);
    }

    /** Matches stable's installed-count fog-distance curve. */
    public static float getFogDistanceScale(int installedCount) {
        int clamped = Math.max(1, Math.min(4, installedCount));
        return (float) Math.pow(clamped, 1.25D) / 4F;
    }
}
