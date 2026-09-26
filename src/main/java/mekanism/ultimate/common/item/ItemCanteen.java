package mekanism.ultimate.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.base.FluidItemWrapper;
import mekanism.common.base.IFluidItemWrapper;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.item.ItemMekanism;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import mekanism.ultimate.common.UltimateFluids;
import mekanism.ultimate.common.nutrition.UltimateNutrition;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;

/** Portable stable-style Nutritional Paste storage and drinking item. */
public final class ItemCanteen extends ItemMekanism implements IFluidItemWrapper {

    private static final String FLUID_KEY = "nutritionalPaste";

    public ItemCanteen() {
        setMaxStackSize(1);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new ItemCapabilityWrapper(stack, new FluidItemWrapper());
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        if (!ItemDataUtils.hasData(container, FLUID_KEY)) {
            return null;
        }
        FluidStack stored = FluidStack.loadFluidStackFromNBT(ItemDataUtils.getCompound(container, FLUID_KEY));
        if (stored == null || stored.getFluid() != UltimateFluids.NutritionalPaste || stored.amount <= 0) {
            return null;
        }
        stored.amount = Math.min(stored.amount, getCapacity(container));
        return stored;
    }

    @Override
    public int getCapacity(ItemStack container) {
        return UltimateNutrition.getCanteenCapacity();
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (resource == null || resource.getFluid() != UltimateFluids.NutritionalPaste || resource.amount <= 0) {
            return 0;
        }
        FluidStack stored = getFluid(container);
        int amount = stored == null ? 0 : stored.amount;
        int accepted = Math.min(resource.amount, Math.min(UltimateNutrition.getCanteenTransferRate(), getCapacity(container) - amount));
        if (accepted > 0 && doFill) {
            setFluid(container, amount + accepted);
        }
        return Math.max(0, accepted);
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        FluidStack stored = getFluid(container);
        if (stored == null || maxDrain <= 0) {
            return null;
        }
        int drained = Math.min(stored.amount, Math.min(maxDrain, UltimateNutrition.getCanteenTransferRate()));
        if (doDrain) {
            setFluid(container, stored.amount - drained);
        }
        return new FluidStack(UltimateFluids.NutritionalPaste, drained);
    }

    public void setFluid(ItemStack container, int amount) {
        int clamped = Math.max(0, Math.min(amount, getCapacity(container)));
        if (clamped == 0) {
            ItemDataUtils.removeData(container, FLUID_KEY);
        } else {
            ItemDataUtils.setCompound(container, FLUID_KEY,
                  new FluidStack(UltimateFluids.NutritionalPaste, clamped).writeToNBT(new NBTTagCompound()));
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        FluidStack stored = getFluid(stack);
        if (!player.capabilities.isCreativeMode && !player.isSpectator() && player.canEat(false)
              && stored != null && stored.amount >= UltimateNutrition.getPasteMBPerFood()) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entity) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.capabilities.isCreativeMode || player.isSpectator()) {
                return stack;
            }
            FluidStack stored = getFluid(stack);
            int mbPerFood = UltimateNutrition.getPasteMBPerFood();
            int needed = Math.min(20 - player.getFoodStats().getFoodLevel(),
                  stored == null ? 0 : stored.amount / mbPerFood);
            if (needed > 0) {
                player.getFoodStats().addStats(needed, UltimateNutrition.getSaturation());
                setFluid(stack, stored.amount - needed * mbPerFood);
            }
        }
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Nonnull
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        FluidStack stored = getFluid(stack);
        return 1D - (stored == null ? 0D : (double) stored.amount / getCapacity(stack));
    }

    @Override
    public int getRGBDurabilityForDisplay(@Nonnull ItemStack stack) {
        return MathHelper.hsvToRGB(0.93F, 0.54F, 0.92F);
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }
        items.add(new ItemStack(this));
        ItemStack full = new ItemStack(this);
        setFluid(full, getCapacity(full));
        items.add(full);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        FluidStack stored = getFluid(stack);
        int amount = stored == null ? 0 : stored.amount;
        tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.nutritionalPaste") + ": " + EnumColor.GREY
              + amount + " / " + getCapacity(stack) + " mB");
    }
}
