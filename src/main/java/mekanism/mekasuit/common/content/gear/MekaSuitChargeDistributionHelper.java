package mekanism.mekasuit.common.content.gear;

import cofh.api.energy.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import mekanism.api.MekanismConfig.general;
import mekanism.api.energy.EnergizedItemManager;
import mekanism.api.energy.IEnergizedItem;
import mekanism.common.util.MekanismUtils;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Stable suit balancing and inventory charging behavior. */
public final class MekaSuitChargeDistributionHelper
{
    public static final String CHARGE_SUIT_CONFIG = "charge_suit";
    public static final String CHARGE_INVENTORY_CONFIG = "charge_inventory";

    private MekaSuitChargeDistributionHelper()
    {
    }

    public static ModuleData getModule(ItemStack bodyarmor)
    {
        if (bodyarmor == null
              || !(bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor))
        {
            return null;
        }
        return ModuleContainer.fromStack(bodyarmor,
              ModuleTarget.BODYARMOR).get(
                    MekaSuitModules.CHARGE_DISTRIBUTION_UNIT);
    }

    public static void tick(ItemStack bodyarmor, EntityPlayer player)
    {
        ModuleData module = getModule(bodyarmor);
        if (module == null || !module.isEnabled() || player == null)
        {
            return;
        }
        if (module.getBooleanConfig(CHARGE_INVENTORY_CONFIG))
        {
            chargeInventory(bodyarmor, player);
        }
        if (module.getBooleanConfig(CHARGE_SUIT_CONFIG))
        {
            balanceSuit(player.inventory.armorInventory);
        }
    }

    static double chargeInventory(ItemStack sourceStack,
          EntityPlayer player)
    {
        if (!(sourceStack.getItem() instanceof IEnergizedItem))
        {
            return 0;
        }
        IEnergizedItem source = (IEnergizedItem)sourceStack.getItem();
        double available = Math.min(MekaSuitConfig.suitInventoryChargeRate,
              source.getEnergy(sourceStack));
        if (available <= 0)
        {
            return 0;
        }
        return chargeItems(sourceStack, source, available,
              player.getCurrentEquippedItem(),
              player.inventory.mainInventory);
    }

    static double chargeItems(ItemStack sourceStack, IEnergizedItem source,
          double available, ItemStack held, ItemStack[] inventory)
    {
        double remaining = Math.max(0, available);
        remaining -= charge(held, sourceStack, remaining);
        if (inventory != null)
        {
            for (ItemStack stack : inventory)
            {
                if (remaining <= 0)
                {
                    break;
                }
                if (stack != held)
                {
                    remaining -= charge(stack, sourceStack, remaining);
                }
            }
        }
        double used = Math.max(0, available) - remaining;
        if (used > 0)
        {
            source.setEnergy(sourceStack,
                  source.getEnergy(sourceStack) - used);
        }
        return used;
    }

    static void balanceSuit(ItemStack[] armor)
    {
        List<EnergyEntry> entries = new ArrayList<EnergyEntry>(4);
        double total = 0;
        if (armor == null)
        {
            return;
        }
        for (ItemStack stack : armor)
        {
            if (stack != null && stack.getItem() instanceof IEnergizedItem)
            {
                IEnergizedItem item = (IEnergizedItem)stack.getItem();
                double capacity = Math.max(0, item.getMaxEnergy(stack));
                if (capacity > 0)
                {
                    entries.add(new EnergyEntry(stack, item, capacity));
                    total += Math.max(0, Math.min(capacity,
                          item.getEnergy(stack)));
                }
            }
        }
        if (entries.size() < 2)
        {
            return;
        }
        Collections.sort(entries, new Comparator<EnergyEntry>()
        {
            @Override
            public int compare(EnergyEntry first, EnergyEntry second)
            {
                return Double.compare(first.capacity, second.capacity);
            }
        });
        double remaining = total;
        for (int index = 0; index < entries.size(); index++)
        {
            EnergyEntry entry = entries.get(index);
            double share = remaining / (entries.size() - index);
            double target = Math.min(entry.capacity, share);
            entry.item.setEnergy(entry.stack, target);
            remaining -= target;
        }
    }

    private static double charge(ItemStack target, ItemStack source,
          double available)
    {
        if (target == null || target == source || available <= 0)
        {
            return 0;
        }
        if (target.getItem() instanceof IEnergizedItem)
        {
            return EnergizedItemManager.charge(target, available);
        }
        if (MekanismUtils.useIC2()
              && target.getItem() instanceof IElectricItem)
        {
            double sent = ElectricItem.manager.charge(target,
                  safeInt(available * general.TO_IC2), 4, true, false);
            return Math.min(available, sent * general.FROM_IC2);
        }
        if (MekanismUtils.useRF()
              && target.getItem() instanceof IEnergyContainerItem)
        {
            int sent = ((IEnergyContainerItem)target.getItem()).receiveEnergy(
                  target, safeInt(available * general.TO_TE), false);
            return Math.min(available, sent * general.FROM_TE);
        }
        return 0;
    }

    private static int safeInt(double value)
    {
        return value <= 0 ? 0 : value >= Integer.MAX_VALUE
              ? Integer.MAX_VALUE : (int)Math.floor(value);
    }

    private static final class EnergyEntry
    {
        private final ItemStack stack;
        private final IEnergizedItem item;
        private final double capacity;

        private EnergyEntry(ItemStack stack, IEnergizedItem item,
              double capacity)
        {
            this.stack = stack;
            this.item = item;
            this.capacity = capacity;
        }
    }
}
