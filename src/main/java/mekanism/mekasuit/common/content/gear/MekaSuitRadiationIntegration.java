package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.item.ItemStack;

/** Optional-safe entry points used reflectively by the Nuclear module. */
public final class MekaSuitRadiationIntegration {

    private MekaSuitRadiationIntegration() {
    }

    public static double getShielding(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemMekaSuitArmor)) {
            return 0;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor) stack.getItem();
        if (!armor.hasEnabledModule(stack, MekaSuitModules.RADIATION_SHIELDING_UNIT)) {
            return 0;
        }
        switch (armor.getModuleTarget()) {
            case HELMET: return 0.25D;
            case BODYARMOR: return 0.40D;
            case PANTS: return 0.20D;
            case BOOTS: return 0.15D;
            default: return 0;
        }
    }

    public static boolean hasDosimeter(ItemStack stack) {
        return hasBodyModule(stack, MekaSuitModules.DOSIMETER_UNIT);
    }

    public static boolean hasGeiger(ItemStack stack) {
        return hasBodyModule(stack, MekaSuitModules.GEIGER_UNIT);
    }

    private static boolean hasBodyModule(ItemStack stack, mekanism.mekasuit.api.gear.ModuleType type) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemMekaSuitArmor
              && ((ItemMekaSuitArmor) stack.getItem()).getModuleTarget() == ModuleTarget.BODYARMOR
              && ((ItemMekaSuitArmor) stack.getItem()).hasEnabledModule(stack, type);
    }
}
