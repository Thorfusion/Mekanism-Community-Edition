package mekanism.mekasuit.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.api.EnumColor;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import mekanism.common.util.LangUtils;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/** MekaSuit chest piece with fill-only Hydrogen storage for Jetpack Units. */
public final class ItemMekaSuitBodyarmor extends ItemMekaSuitArmor
      implements IGasItem
{
    private static final String STORED_GAS = "MekaSuitJetpackGas";

    public ItemMekaSuitBodyarmor(String registryName, String translationKey)
    {
        super(registryName, translationKey, 1);
    }

    @Override
    public int getMaxGas(ItemStack stack)
    {
        int installed = ModuleContainer.fromStack(stack,
              ModuleTarget.BODYARMOR).getInstalledCount(
                    MekaSuitModules.JETPACK_UNIT);
        long capacity = (long)MekaSuitConfig.suitJetpackStorage * installed;
        return capacity >= Integer.MAX_VALUE ? Integer.MAX_VALUE
              : (int)Math.max(0, capacity);
    }

    @Override
    public int getRate(ItemStack stack)
    {
        return MekaSuitConfig.suitJetpackTransferRate;
    }

    @Override
    public int addGas(ItemStack stack, GasStack gas)
    {
        Gas hydrogen = GasRegistry.getGas("hydrogen");
        if (gas == null || gas.amount <= 0 || gas.getGas() != hydrogen)
        {
            return 0;
        }
        int stored = getStoredGas(stack);
        int accepted = Math.min(gas.amount, Math.min(getRate(stack),
              getMaxGas(stack) - stored));
        if (accepted > 0)
        {
            setGas(stack, new GasStack(hydrogen, stored + accepted));
        }
        return Math.max(0, accepted);
    }

    @Override
    public GasStack removeGas(ItemStack stack, int amount)
    {
        return null;
    }

    @Override
    public boolean canReceiveGas(ItemStack stack, Gas type)
    {
        return type == GasRegistry.getGas("hydrogen") && getMaxGas(stack) > 0;
    }

    @Override
    public boolean canProvideGas(ItemStack stack, Gas type)
    {
        return false;
    }

    @Override
    public GasStack getGas(ItemStack stack)
    {
        if (!stack.hasTagCompound())
        {
            return null;
        }
        GasStack stored = GasStack.readFromNBT(
              stack.stackTagCompound.getCompoundTag(STORED_GAS));
        if (stored == null || stored.getGas() != GasRegistry.getGas("hydrogen"))
        {
            return null;
        }
        int amount = Math.min(stored.amount, getMaxGas(stack));
        return amount > 0 ? new GasStack(stored.getGas(), amount) : null;
    }

    @Override
    public void setGas(ItemStack stack, GasStack gas)
    {
        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        int capacity = getMaxGas(stack);
        Gas hydrogen = GasRegistry.getGas("hydrogen");
        if (gas == null || gas.getGas() != hydrogen || gas.amount <= 0
              || capacity <= 0)
        {
            stack.stackTagCompound.removeTag(STORED_GAS);
            return;
        }
        GasStack clamped = new GasStack(hydrogen,
              Math.min(gas.amount, capacity));
        stack.stackTagCompound.setTag(STORED_GAS,
              clamped.write(new NBTTagCompound()));
    }

    public int getStoredGas(ItemStack stack)
    {
        GasStack stored = getGas(stack);
        return stored == null ? 0 : stored.amount;
    }

    public void useGas(ItemStack stack, int amount)
    {
        if (amount <= 0)
        {
            return;
        }
        int remaining = Math.max(0, getStoredGas(stack) - amount);
        setGas(stack, remaining == 0 ? null : new GasStack(
              GasRegistry.getGas("hydrogen"), remaining));
    }

    public void clampGas(ItemStack stack)
    {
        GasStack stored = stack.hasTagCompound() ? GasStack.readFromNBT(
              stack.stackTagCompound.getCompoundTag(STORED_GAS)) : null;
        setGas(stack, stored);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player,
          List tooltip, boolean advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);
        ModuleData jetpack = ModuleContainer.fromStack(stack,
              ModuleTarget.BODYARMOR).get(MekaSuitModules.JETPACK_UNIT);
        if (jetpack != null)
        {
            tooltip.add(EnumColor.AQUA
                  + LangUtils.localize("tooltip.mekasuit.hydrogen") + ": "
                  + EnumColor.GREY + getStoredGas(stack) + " / "
                  + getMaxGas(stack) + " mB");
            tooltip.add(EnumColor.AQUA + LangUtils.localize("gui.mode") + ": "
                  + EnumColor.GREY + LangUtils.localize("module.mode."
                        + jetpack.getMode()));
        }
    }
}
