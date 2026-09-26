package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemHDPEElytra;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

/** Elytra Unit eligibility and energy rules shared by the tick adapter and input validation. */
public final class MekaSuitElytraHelper {

    private MekaSuitElytraHelper() {
    }

    public static ModuleData getModule(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemMekaSuitBodyarmor)) {
            return null;
        }
        return ModuleContainer.fromStack(stack, ModuleTarget.BODYARMOR).get(MekaSuitModules.ELYTRA_UNIT);
    }

    public static boolean hasEnabledModule(ItemStack stack) {
        ModuleData module = getModule(stack);
        return module != null && module.isEnabled();
    }

    public static boolean canUse(ItemStack stack, EntityPlayer player) {
        if (stack == null || stack.isEmpty() || player == null) {
            return false;
        }
        if (stack.getItem() instanceof ItemHDPEElytra) {
            // Match vanilla Elytra behavior. Sneaking is only the stable MekaSuit
            // unit's deliberate early-exit control, not a restriction on this item.
            return ItemElytra.isUsable(stack);
        }
        return !player.isSneaking() && hasUsableModule(stack, player.capabilities.isCreativeMode);
    }

    public static boolean hasUsableModule(ItemStack stack, boolean creative) {
        return hasEnabledModule(stack) && !isBlockedByHoverJetpack(stack)
              && (creative || hasEnergy(stack, getEnergyUsage()));
    }

    public static boolean shouldRenderWings(ItemStack stack) {
        return stack != null && !stack.isEmpty()
              && (stack.getItem() instanceof ItemHDPEElytra || hasEnabledModule(stack));
    }

    public static long getEnergyUsage() {
        return Math.max(0L, MekaSuitConfig.suitElytraEnergyUsage);
    }

    public static boolean hasEnergy(ItemStack stack, long amount) {
        return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemMekaSuitBodyarmor
              && ((ItemMekaSuitBodyarmor) stack.getItem()).getEnergy(stack) >= Math.max(0L, amount);
    }

    public static void useEnergy(ItemStack stack, long amount) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemMekaSuitBodyarmor) {
            ItemMekaSuitBodyarmor armor = (ItemMekaSuitBodyarmor) stack.getItem();
            armor.setEnergy(stack, armor.getEnergy(stack) - Math.max(0L, amount));
        }
    }

    private static boolean isBlockedByHoverJetpack(ItemStack stack) {
        ModuleData jetpack = MekaSuitJetpackHelper.getModule(stack);
        return jetpack != null && jetpack.isEnabled()
              && MekaSuitJetpackHelper.HOVER.equals(jetpack.getMode())
              && stack.getItem() instanceof ItemMekaSuitBodyarmor
              && ((ItemMekaSuitBodyarmor) stack.getItem()).getStoredGas(stack) > 0;
    }
}
