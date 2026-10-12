package mekanism.nuclear.common.radiation;

import mekanism.nuclear.common.item.ItemHazmatSuitArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/** Calculates protection from equipped Nuclear radiation gear. */
public final class RadiationShielding
{
    private RadiationShielding()
    {
    }

    public static double getResistance(EntityLivingBase entity)
    {
        if (entity == null)
        {
            return 0;
        }
        ItemStack[] armor = new ItemStack[4];
        for (int index = 0; index < armor.length; index++)
        {
            armor[index] = entity.getEquipmentInSlot(index + 1);
        }
        return getResistance(armor);
    }

    static double getResistance(ItemStack[] armor)
    {
        double resistance = 0;
        if (armor == null)
        {
            return resistance;
        }
        for (ItemStack stack : armor)
        {
            if (stack != null && stack.getItem() instanceof ItemHazmatSuitArmor)
            {
                resistance += ((ItemHazmatSuitArmor)stack.getItem())
                      .getRadiationShielding();
            }
            else if (stack != null)
            {
                resistance += MekaSuitRadiationBridge.getShielding(stack);
            }
            if (resistance >= 1)
            {
                return 1;
            }
        }
        return Math.max(0, resistance);
    }
}
