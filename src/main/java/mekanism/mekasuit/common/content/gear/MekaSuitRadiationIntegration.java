package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.item.ItemStack;

/** Optional-safe entry points used reflectively by the Nuclear module. */
public final class MekaSuitRadiationIntegration
{
    private MekaSuitRadiationIntegration()
    {
    }

    public static double getShielding(ItemStack stack)
    {
        if (stack == null || !(stack.getItem() instanceof ItemMekaSuitArmor))
        {
            return 0;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)stack.getItem();
        return MekaSuitProtectionHelper.getRadiationShielding(
              armor.getModuleTarget(), armor.hasEnabledModule(stack,
                    MekaSuitModules.RADIATION_SHIELDING_UNIT));
    }

    public static boolean hasDosimeter(ItemStack stack)
    {
        return hasBodyModule(stack, MekaSuitModules.DOSIMETER_UNIT);
    }

    public static boolean hasGeiger(ItemStack stack)
    {
        return hasBodyModule(stack, MekaSuitModules.GEIGER_UNIT);
    }

    private static boolean hasBodyModule(ItemStack stack, ModuleType type)
    {
        return stack != null
              && stack.getItem() instanceof ItemMekaSuitArmor
              && ((ItemMekaSuitArmor)stack.getItem()).getModuleTarget()
                    == ModuleTarget.BODYARMOR
              && ((ItemMekaSuitArmor)stack.getItem()).hasEnabledModule(
                    stack, type);
    }
}
