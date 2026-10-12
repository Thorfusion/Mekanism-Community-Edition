package mekanism.ultimate.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.api.EnumColor;
import mekanism.common.util.LangUtils;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.UltimateFluids;
import mekanism.ultimate.common.nutrition.UltimateNutrition;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

/** Portable stable-style Nutritional Paste storage and drinking item. */
public final class ItemCanteen extends Item implements IFluidContainerItem
{
    private static final String FLUID_KEY = "nutritionalPaste";

    public ItemCanteen()
    {
        setUnlocalizedName("Canteen");
        setMaxStackSize(1);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.uncommon;
    }

    @Override
    public FluidStack getFluid(ItemStack container)
    {
        if (container == null || container.stackTagCompound == null
              || !container.stackTagCompound.hasKey(FLUID_KEY, 10))
        {
            return null;
        }
        FluidStack stored = FluidStack.loadFluidStackFromNBT(
              container.stackTagCompound.getCompoundTag(FLUID_KEY));
        if (stored == null
              || stored.getFluid() != UltimateFluids.NutritionalPaste
              || stored.amount <= 0)
        {
            return null;
        }
        stored.amount = Math.min(stored.amount, getCapacity(container));
        return stored;
    }

    @Override
    public int getCapacity(ItemStack container)
    {
        return UltimateNutrition.getCanteenCapacity();
    }

    @Override
    public int fill(ItemStack container, FluidStack resource,
          boolean doFill)
    {
        if (container == null || container.stackSize != 1
              || resource == null
              || resource.getFluid() != UltimateFluids.NutritionalPaste
              || resource.amount <= 0)
        {
            return 0;
        }
        FluidStack stored = getFluid(container);
        int amount = stored == null ? 0 : stored.amount;
        int accepted = Math.min(resource.amount, Math.min(
              UltimateNutrition.getCanteenTransferRate(),
              getCapacity(container) - amount));
        if (accepted > 0 && doFill)
        {
            setFluid(container, amount + accepted);
        }
        return Math.max(0, accepted);
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain,
          boolean doDrain)
    {
        FluidStack stored = getFluid(container);
        if (stored == null || maxDrain <= 0)
        {
            return null;
        }
        int drained = Math.min(stored.amount, Math.min(maxDrain,
              UltimateNutrition.getCanteenTransferRate()));
        if (doDrain)
        {
            setFluid(container, stored.amount - drained);
        }
        return new FluidStack(UltimateFluids.NutritionalPaste, drained);
    }

    public void setFluid(ItemStack container, int amount)
    {
        int clamped = Math.max(0, Math.min(amount,
              getCapacity(container)));
        if (container.stackTagCompound == null)
        {
            container.setTagCompound(new NBTTagCompound());
        }
        if (clamped == 0)
        {
            container.stackTagCompound.removeTag(FLUID_KEY);
        }
        else
        {
            container.stackTagCompound.setTag(FLUID_KEY,
                  new FluidStack(UltimateFluids.NutritionalPaste, clamped)
                        .writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world,
          EntityPlayer player)
    {
        FluidStack stored = getFluid(stack);
        if (!player.capabilities.isCreativeMode && player.canEat(false)
              && stored != null
              && stored.amount >= UltimateNutrition.getPasteMBPerFood())
        {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world,
          EntityPlayer player)
    {
        if (!world.isRemote && !player.capabilities.isCreativeMode)
        {
            FluidStack stored = getFluid(stack);
            int mbPerFood = UltimateNutrition.getPasteMBPerFood();
            int needed = Math.min(20 - player.getFoodStats().getFoodLevel(),
                  stored == null ? 0 : stored.amount / mbPerFood);
            if (needed > 0)
            {
                player.getFoodStats().addStats(needed,
                      UltimateNutrition.getSaturation());
                setFluid(stack, stored.amount - needed * mbPerFood);
            }
        }
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.drink;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        FluidStack stored = getFluid(stack);
        return 1D - (stored == null ? 0D
              : (double)stored.amount / getCapacity(stack));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        ItemStack empty = new ItemStack(this);
        items.add(empty);
        ItemStack full = new ItemStack(this);
        setFluid(full, getCapacity(full));
        items.add(full);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(
              MekanismUltimate.MODID.toLowerCase() + ":canteen");
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player,
          List tooltip, boolean advanced)
    {
        FluidStack stored = getFluid(stack);
        int amount = stored == null ? 0 : stored.amount;
        tooltip.add(EnumColor.AQUA
              + LangUtils.localize("tooltip.nutritionalPaste") + ": "
              + EnumColor.GREY + amount + " / " + getCapacity(stack)
              + " mB");
    }
}
