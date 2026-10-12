package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Elytra Unit eligibility and energy rules shared by input and tick handling. */
public final class MekaSuitElytraHelper
{
    private MekaSuitElytraHelper()
    {
    }

    public static ModuleData getModule(ItemStack stack)
    {
        if (stack == null
              || !(stack.getItem() instanceof ItemMekaSuitBodyarmor))
        {
            return null;
        }
        return ModuleContainer.fromStack(stack, ModuleTarget.BODYARMOR)
              .get(MekaSuitModules.ELYTRA_UNIT);
    }

    public static boolean hasEnabledModule(ItemStack stack)
    {
        ModuleData module = getModule(stack);
        return isEnabled(module);
    }

    public static boolean isEnabled(ModuleData module)
    {
        return module != null && module.isEnabled();
    }

    public static boolean canUse(ItemStack stack, EntityPlayer player)
    {
        return player != null && !player.isSneaking()
              && player.ridingEntity == null
              && hasUsableModule(stack,
                    player.capabilities.isCreativeMode);
    }

    public static boolean hasUsableModule(ItemStack stack, boolean creative)
    {
        return EtFuturumElytraCompat.isAvailable()
              && hasEnabledModule(stack) && !isBlockedByHoverJetpack(stack)
              && (creative || hasEnergy(stack, getEnergyUsage()));
    }

    public static boolean shouldRenderWings(ItemStack stack)
    {
        return hasEnabledModule(stack);
    }

    public static long getEnergyUsage()
    {
        return Math.max(0L, MekaSuitConfig.suitElytraEnergyUsage);
    }

    public static boolean hasEnergy(ItemStack stack, long amount)
    {
        return stack != null
              && stack.getItem() instanceof ItemMekaSuitBodyarmor
              && ((ItemMekaSuitBodyarmor)stack.getItem()).getEnergy(stack)
                    >= Math.max(0L, amount);
    }

    public static double useEnergy(ItemStack stack, long amount)
    {
        if (stack == null
              || !(stack.getItem() instanceof ItemMekaSuitBodyarmor))
        {
            return 0D;
        }
        ItemMekaSuitBodyarmor armor =
              (ItemMekaSuitBodyarmor)stack.getItem();
        double before = armor.getEnergy(stack);
        armor.setEnergy(stack, remainingEnergy(before, amount));
        return before - armor.getEnergy(stack);
    }

    public static double remainingEnergy(double stored, long amount)
    {
        return Math.max(0D, stored - Math.max(0L, amount));
    }

    private static boolean isBlockedByHoverJetpack(ItemStack stack)
    {
        ModuleData jetpack = MekaSuitJetpackHelper.getModule(stack);
        return jetpack != null && jetpack.isEnabled()
              && MekaSuitJetpackHelper.HOVER.equals(jetpack.getMode())
              && stack.getItem() instanceof ItemMekaSuitBodyarmor
              && ((ItemMekaSuitBodyarmor)stack.getItem())
                    .getStoredGas(stack) > 0;
    }
}
