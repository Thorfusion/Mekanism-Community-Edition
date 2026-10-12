package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import mekanism.ultimate.common.UltimateFluids;
import mekanism.ultimate.common.nutrition.UltimateNutrition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

/** Fill-only Nutritional Paste storage and feeding for the helmet unit. */
public final class MekaSuitNutritionalHelper
{
    private static final String FLUID_KEY = "nutritionalPaste";

    private MekaSuitNutritionalHelper()
    {
    }

    public static void tick(ItemStack helmet, EntityPlayer player)
    {
        ModuleData module = getModule(helmet);
        if (module == null || !module.isEnabled() || player == null
              || player.capabilities.isCreativeMode || !player.canEat(false))
        {
            return;
        }
        int mbPerFood = UltimateNutrition.getPasteMBPerFood();
        int pasteLimited = getStored(helmet) / mbPerFood;
        int needed = Math.min(20 - player.getFoodStats().getFoodLevel(),
              pasteLimited);
        if (needed <= 0)
        {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)helmet.getItem();
        long usage = Math.max(0,
              MekaSuitConfig.suitNutritionalInjectionUsage);
        int energyLimited = usage == 0 ? needed
              : clampToInt(armor.getEnergy(helmet) / usage);
        int toFeed = Math.min(needed, energyLimited);
        if (toFeed <= 0)
        {
            return;
        }
        if (usage > 0)
        {
            armor.setEnergy(helmet, armor.getEnergy(helmet)
                  - usage * (double)toFeed);
        }
        setStored(helmet, getStored(helmet) - toFeed * mbPerFood);
        player.getFoodStats().addStats(toFeed,
              UltimateNutrition.getSaturation());
    }

    public static ModuleData getModule(ItemStack helmet)
    {
        if (helmet == null
              || !(helmet.getItem() instanceof ItemMekaSuitArmor))
        {
            return null;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)helmet.getItem();
        if (armor.getModuleTarget() != ModuleTarget.HELMET)
        {
            return null;
        }
        return ModuleContainer.fromStack(helmet, ModuleTarget.HELMET)
              .get(MekaSuitModules.NUTRITIONAL_INJECTION_UNIT);
    }

    public static boolean supportsStorage(ItemStack stack)
    {
        return getModule(stack) != null;
    }

    public static FluidStack getFluid(ItemStack stack)
    {
        if (stack == null || stack.stackTagCompound == null
              || !stack.stackTagCompound.hasKey(FLUID_KEY, 10))
        {
            return null;
        }
        FluidStack stored = FluidStack.loadFluidStackFromNBT(
              stack.stackTagCompound.getCompoundTag(FLUID_KEY));
        if (stored == null
              || stored.getFluid() != UltimateFluids.NutritionalPaste)
        {
            return null;
        }
        stored.amount = Math.max(0, Math.min(stored.amount, getCapacity()));
        return stored.amount == 0 ? null : stored;
    }

    public static int getStored(ItemStack stack)
    {
        FluidStack stored = getFluid(stack);
        return stored == null ? 0 : stored.amount;
    }

    public static int getCapacity()
    {
        return Math.max(1, MekaSuitConfig.suitNutritionalStorage);
    }

    public static double getRatio(ItemStack stack)
    {
        return (double)getStored(stack) / getCapacity();
    }

    public static int fill(ItemStack stack, FluidStack resource,
          boolean doFill)
    {
        if (stack == null || stack.stackSize != 1
              || !supportsStorage(stack) || resource == null
              || resource.getFluid() != UltimateFluids.NutritionalPaste
              || resource.amount <= 0)
        {
            return 0;
        }
        int stored = getStored(stack);
        int accepted = calculateFill(stored, resource.amount);
        if (accepted > 0 && doFill)
        {
            setStored(stack, stored + accepted);
        }
        return accepted;
    }

    static int calculateFill(int stored, int offered)
    {
        if (offered <= 0)
        {
            return 0;
        }
        return Math.max(0, Math.min(offered, Math.min(
              Math.max(1, MekaSuitConfig.suitNutritionalTransferRate),
              getCapacity() - Math.max(0, stored))));
    }

    static void setStored(ItemStack stack, int amount)
    {
        int clamped = Math.max(0, Math.min(amount, getCapacity()));
        if (stack.stackTagCompound == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (clamped == 0)
        {
            stack.stackTagCompound.removeTag(FLUID_KEY);
        }
        else
        {
            stack.stackTagCompound.setTag(FLUID_KEY,
                  new FluidStack(UltimateFluids.NutritionalPaste, clamped)
                        .writeToNBT(new NBTTagCompound()));
        }
    }

    static int clampToInt(double value)
    {
        if (Double.isNaN(value) || value <= 0)
        {
            return 0;
        }
        return value >= Integer.MAX_VALUE
              ? Integer.MAX_VALUE : (int)value;
    }
}
