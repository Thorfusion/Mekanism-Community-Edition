package mekanism.mekasuit.common.content.gear;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.capabilities.ItemCapabilityWrapper.ItemCapability;
import mekanism.common.util.ItemDataUtils;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import mekanism.ultimate.common.UltimateFluids;
import mekanism.ultimate.common.nutrition.UltimateNutrition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/** Storage, feeding, and fill-only item capability for the helmet nutrition unit. */
public final class MekaSuitNutritionalHelper {

    private static final String FLUID_KEY = "nutritionalPaste";
    private static final IFluidTankProperties[] NO_TANKS = new IFluidTankProperties[0];

    private MekaSuitNutritionalHelper() {
    }

    public static void tick(ItemStack helmet, EntityPlayer player) {
        ModuleData module = getModule(helmet);
        if (module == null || !module.isEnabled() || player == null || player.capabilities.isCreativeMode
              || player.isSpectator() || !player.canEat(false)) {
            return;
        }
        int mbPerFood = UltimateNutrition.getPasteMBPerFood();
        int pasteLimited = getStored(helmet) / mbPerFood;
        int needed = Math.min(20 - player.getFoodStats().getFoodLevel(), pasteLimited);
        if (needed <= 0) {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor) helmet.getItem();
        long usage = Math.max(0, MekaSuitConfig.suitNutritionalInjectionUsage);
        int energyLimited = usage == 0 ? needed : clampToInt(armor.getEnergy(helmet) / usage);
        int toFeed = Math.min(needed, energyLimited);
        if (toFeed <= 0) {
            return;
        }
        if (usage > 0) {
            armor.setEnergy(helmet, armor.getEnergy(helmet) - usage * (double) toFeed);
        }
        setStored(helmet, getStored(helmet) - toFeed * mbPerFood);
        // Stable currently passes 'needed' here even when energy is insufficient. Using the committed
        // amount prevents food restoration that was not paid for by both paste and energy.
        player.getFoodStats().addStats(toFeed, UltimateNutrition.getSaturation());
    }

    public static ModuleData getModule(ItemStack helmet) {
        if (helmet == null || helmet.isEmpty() || !(helmet.getItem() instanceof ItemMekaSuitArmor)) {
            return null;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor) helmet.getItem();
        if (armor.getModuleTarget() != ModuleTarget.HELMET) {
            return null;
        }
        return ModuleContainer.fromStack(helmet, ModuleTarget.HELMET)
              .get(MekaSuitModules.NUTRITIONAL_INJECTION_UNIT);
    }

    public static boolean supportsStorage(ItemStack stack) {
        return getModule(stack) != null;
    }

    public static int getStored(ItemStack stack) {
        if (!ItemDataUtils.hasData(stack, FLUID_KEY)) {
            return 0;
        }
        FluidStack stored = FluidStack.loadFluidStackFromNBT(ItemDataUtils.getCompound(stack, FLUID_KEY));
        return stored != null && stored.getFluid() == UltimateFluids.NutritionalPaste
              ? Math.max(0, Math.min(stored.amount, getCapacity())) : 0;
    }

    public static int getCapacity() {
        return Math.max(1, MekaSuitConfig.suitNutritionalStorage);
    }

    public static double getRatio(ItemStack stack) {
        return (double) getStored(stack) / getCapacity();
    }

    static void setStored(ItemStack stack, int amount) {
        int clamped = Math.max(0, Math.min(amount, getCapacity()));
        if (clamped == 0) {
            ItemDataUtils.removeData(stack, FLUID_KEY);
        } else {
            ItemDataUtils.setCompound(stack, FLUID_KEY,
                  new FluidStack(UltimateFluids.NutritionalPaste, clamped).writeToNBT(new NBTTagCompound()));
        }
    }

    static int clampToInt(double value) {
        if (Double.isNaN(value) || value <= 0) {
            return 0;
        }
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    public static final class NutritionalFluidCapability extends ItemCapability implements IFluidHandlerItem {

        @Override
        public boolean canProcess(Capability<?> capability) {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && supportsStorage(getStack());
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            if (!supportsStorage(getStack())) {
                return NO_TANKS;
            }
            int stored = getStored(getStack());
            FluidStack contents = stored <= 0 ? null : new FluidStack(UltimateFluids.NutritionalPaste, stored);
            return new IFluidTankProperties[]{new FluidTankProperties(contents, getCapacity(), true, false)};
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (getStack().getCount() != 1 || !supportsStorage(getStack()) || resource == null
                  || resource.getFluid() != UltimateFluids.NutritionalPaste || resource.amount <= 0) {
                return 0;
            }
            int stored = getStored(getStack());
            int accepted = Math.min(resource.amount,
                  Math.min(Math.max(1, MekaSuitConfig.suitNutritionalTransferRate), getCapacity() - stored));
            if (accepted > 0 && doFill) {
                setStored(getStack(), stored + accepted);
            }
            return Math.max(0, accepted);
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }

        @Nonnull
        @Override
        public ItemStack getContainer() {
            return getStack();
        }
    }
}
