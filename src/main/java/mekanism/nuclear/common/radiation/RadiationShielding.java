package mekanism.nuclear.common.radiation;

import mekanism.nuclear.common.item.ItemHazmatSuitArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/** Calculates protection from the entity's equipped Nuclear radiation gear. */
public final class RadiationShielding {

    private static final EntityEquipmentSlot[] ARMOR_SLOTS = {
          EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST,
          EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET
    };

    private RadiationShielding() {
    }

    public static double getResistance(EntityLivingBase entity) {
        double resistance = 0;
        for (EntityEquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemHazmatSuitArmor) {
                resistance += ((ItemHazmatSuitArmor) stack.getItem()).getRadiationShielding();
                if (resistance >= 1) {
                    return 1;
                }
            }
        }
        return Math.max(0, resistance);
    }
}
