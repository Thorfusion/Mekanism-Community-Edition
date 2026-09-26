package mekanism.mekasuit.common.item;

import java.util.List;
import mekanism.api.EnumColor;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import mekanism.common.MekanismFluids;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.MekaSuitJetpackHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** MekaSuit chest piece with fill-only Hydrogen storage supplied by installed Jetpack Units. */
public final class ItemMekaSuitBodyarmor extends ItemMekaSuitArmor implements IGasItem {

    private static final String STORED_GAS = "MekaSuitJetpackGas";

    public ItemMekaSuitBodyarmor() {
        super(EntityEquipmentSlot.CHEST);
    }

    @Override
    public int getMaxGas(ItemStack stack) {
        int installed = ModuleContainer.fromStack(stack, ModuleTarget.BODYARMOR)
              .getInstalledCount(MekaSuitModules.JETPACK_UNIT);
        long capacity = (long) MekaSuitConfig.suitJetpackStorage * installed;
        return (int) Math.min(Integer.MAX_VALUE, capacity);
    }

    @Override
    public int getRate(ItemStack stack) {
        return MekaSuitConfig.suitJetpackTransferRate;
    }

    @Override
    public int addGas(ItemStack stack, GasStack gas) {
        if (gas == null || gas.amount <= 0 || gas.getGas() != MekanismFluids.Hydrogen) {
            return 0;
        }
        GasStack stored = getGas(stack);
        int amount = stored == null ? 0 : stored.amount;
        int accepted = Math.min(gas.amount, Math.min(getRate(stack), getMaxGas(stack) - amount));
        if (accepted > 0) {
            setGas(stack, new GasStack(MekanismFluids.Hydrogen, amount + accepted));
        }
        return accepted;
    }

    @Override
    public GasStack removeGas(ItemStack stack, int amount) {
        return null;
    }

    @Override
    public boolean canReceiveGas(ItemStack stack, Gas type) {
        return type == MekanismFluids.Hydrogen && getMaxGas(stack) > 0;
    }

    @Override
    public boolean canProvideGas(ItemStack stack, Gas type) {
        return false;
    }

    @Override
    public GasStack getGas(ItemStack stack) {
        GasStack stored = GasStack.readFromNBT(ItemDataUtils.getCompound(stack, STORED_GAS));
        if (stored == null || stored.getGas() != MekanismFluids.Hydrogen || stored.amount <= 0) {
            return null;
        }
        int amount = Math.min(stored.amount, getMaxGas(stack));
        return amount > 0 ? new GasStack(MekanismFluids.Hydrogen, amount) : null;
    }

    @Override
    public void setGas(ItemStack stack, GasStack gas) {
        int capacity = getMaxGas(stack);
        if (gas == null || gas.getGas() != MekanismFluids.Hydrogen || gas.amount <= 0 || capacity <= 0) {
            ItemDataUtils.removeData(stack, STORED_GAS);
            return;
        }
        GasStack clamped = new GasStack(MekanismFluids.Hydrogen, Math.min(gas.amount, capacity));
        ItemDataUtils.setCompound(stack, STORED_GAS, clamped.write(new NBTTagCompound()));
    }

    public int getStoredGas(ItemStack stack) {
        GasStack stored = getGas(stack);
        return stored == null ? 0 : stored.amount;
    }

    public void useGas(ItemStack stack, int amount) {
        if (amount > 0) {
            int remaining = Math.max(0, getStoredGas(stack) - amount);
            setGas(stack, remaining == 0 ? null : new GasStack(MekanismFluids.Hydrogen, remaining));
        }
    }

    public void clampGas(ItemStack stack) {
        GasStack stored = GasStack.readFromNBT(ItemDataUtils.getCompound(stack, STORED_GAS));
        setGas(stack, stored);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        ModuleData jetpack = ModuleContainer.fromStack(stack, ModuleTarget.BODYARMOR)
              .get(MekaSuitModules.JETPACK_UNIT);
        if (jetpack != null) {
            tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.mekasuit.hydrogen") + ": "
                  + EnumColor.GREY + getStoredGas(stack) + " / " + getMaxGas(stack) + " mB");
            tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.mode") + ": " + EnumColor.GREY
                  + LangUtils.localize("module.mode." + jetpack.getMode()));
        }
    }
}
