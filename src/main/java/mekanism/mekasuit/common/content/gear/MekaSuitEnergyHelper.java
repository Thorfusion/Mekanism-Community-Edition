package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleTarget;
import net.minecraft.item.ItemStack;

/** Stable Energy Unit scaling shared by the Meka-Tool and all suit pieces. */
public final class MekaSuitEnergyHelper {

    private MekaSuitEnergyHelper() {
    }

    public static double getScaledValue(ItemStack stack, ModuleTarget target, long baseValue) {
        ModuleContainer container = ModuleContainer.fromStack(stack, target);
        return scale(baseValue, container.getInstalledCount(MekaSuitModules.ENERGY_UNIT));
    }

    public static double scale(long baseValue, int installedEnergyUnits) {
        if (baseValue < 0 || installedEnergyUnits < 0 || installedEnergyUnits > MekaSuitModules.ENERGY_UNIT.getMaxInstallCount()) {
            throw new IllegalArgumentException("Invalid MekaSuit energy scaling input");
        }
        return Math.scalb((double) baseValue, installedEnergyUnits);
    }
}
