package mekanism.ultimate.common.nutrition;

import mekanism.ultimate.common.config.UltimateNutritionConfig;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;

/** Centralized stable nutrition values and food conversion rules. */
public final class UltimateNutrition
{
    private UltimateNutrition()
    {
    }

    public static int getPasteMBPerFood()
    {
        return Math.max(1,
              UltimateNutritionConfig.nutritionalPasteMBPerFood);
    }

    public static float getSaturation()
    {
        return Math.max(0,
              UltimateNutritionConfig.nutritionalPasteSaturation);
    }

    public static int getCanteenCapacity()
    {
        return Math.max(1, UltimateNutritionConfig.canteenMaxStorage);
    }

    public static int getCanteenTransferRate()
    {
        return Math.max(1, UltimateNutritionConfig.canteenTransferRate);
    }

    public static int getPasteOutput(ItemStack stack)
    {
        if (stack == null || !(stack.getItem() instanceof ItemFood))
        {
            return 0;
        }
        int nutrition = ((ItemFood)stack.getItem()).func_150905_g(stack);
        if (nutrition <= 0)
        {
            return 0;
        }
        long amount = (long)nutrition * getPasteMBPerFood();
        return (int)Math.min(Integer.MAX_VALUE, amount);
    }

    public static ItemStack getContainerItem(ItemStack input)
    {
        if (input == null)
        {
            return null;
        }
        if (input.getItem() instanceof ItemSoup)
        {
            return new ItemStack(Items.bowl);
        }
        if (!input.getItem().hasContainerItem(input))
        {
            return null;
        }
        ItemStack remainder = input.getItem().getContainerItem(input);
        return remainder == null ? null : remainder.copy();
    }
}
