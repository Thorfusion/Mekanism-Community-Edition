package mekanism.ultimate.common.nutrition;

import mekanism.ultimate.common.config.UltimateNutritionConfig;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;

/** Centralized stable nutrition defaults with safe startup fallbacks. */
public final class UltimateNutrition {

    public static final int DEFAULT_MB_PER_FOOD = UltimateNutritionConfig.DEFAULT_MB_PER_FOOD;
    public static final float DEFAULT_SATURATION = UltimateNutritionConfig.DEFAULT_SATURATION;
    public static final int DEFAULT_CANTEEN_CAPACITY = UltimateNutritionConfig.DEFAULT_CANTEEN_CAPACITY;
    public static final int DEFAULT_CANTEEN_TRANSFER_RATE = UltimateNutritionConfig.DEFAULT_CANTEEN_TRANSFER_RATE;

    private UltimateNutrition() {
    }

    public static int getPasteMBPerFood() {
        return Math.max(1, UltimateNutritionConfig.nutritionalPasteMBPerFood);
    }

    public static float getSaturation() {
        return Math.max(0, UltimateNutritionConfig.nutritionalPasteSaturation);
    }

    public static int getCanteenCapacity() {
        return Math.max(1, UltimateNutritionConfig.canteenMaxStorage);
    }

    public static int getCanteenTransferRate() {
        return Math.max(1, UltimateNutritionConfig.canteenTransferRate);
    }

    public static int getPasteOutput(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemFood)) {
            return 0;
        }
        int nutrition = ((ItemFood) stack.getItem()).getHealAmount(stack);
        if (nutrition <= 0) {
            return 0;
        }
        long amount = (long) nutrition * getPasteMBPerFood();
        return (int) Math.min(Integer.MAX_VALUE, amount);
    }

    public static ItemStack getContainerItem(ItemStack input) {
        if (input == null || input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        // Vanilla soups return their bowl from onItemUseFinish rather than the crafting-container API.
        if (input.getItem() instanceof ItemSoup) {
            return new ItemStack(Items.BOWL);
        }
        if (!input.getItem().hasContainerItem(input)) {
            return ItemStack.EMPTY;
        }
        ItemStack remainder = input.getItem().getContainerItem(input);
        return remainder == null ? ItemStack.EMPTY : remainder.copy();
    }
}
