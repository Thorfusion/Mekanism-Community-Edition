package mekanism.mekasuit.common.content.gear;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import mekanism.api.energy.EnergizedItemManager;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.common.util.ChargeUtils;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Stable Charge Distribution behavior adapted to 1.12's universal energy-item bridge. */
public final class MekaSuitChargeDistributionHelper {

    public static final String CHARGE_SUIT_CONFIG = "charge_suit";
    public static final String CHARGE_INVENTORY_CONFIG = "charge_inventory";

    private MekaSuitChargeDistributionHelper() {
    }

    public static ModuleData getModule(ItemStack bodyarmor) {
        if (bodyarmor == null || bodyarmor.isEmpty()
              || !(bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor)) {
            return null;
        }
        return ModuleContainer.fromStack(bodyarmor, ModuleTarget.BODYARMOR)
              .get(MekaSuitModules.CHARGE_DISTRIBUTION_UNIT);
    }

    public static void tick(ItemStack bodyarmor, EntityPlayer player) {
        ModuleData module = getModule(bodyarmor);
        if (module == null || !module.isEnabled() || player == null) {
            return;
        }
        if (module.getBooleanConfig(CHARGE_INVENTORY_CONFIG)) {
            chargeInventory(bodyarmor, player);
        }
        if (module.getBooleanConfig(CHARGE_SUIT_CONFIG)) {
            balanceSuit(player.getArmorInventoryList());
        }
    }

    static double chargeInventory(ItemStack sourceStack, EntityPlayer player) {
        if (!(sourceStack.getItem() instanceof IEnergizedItem)) {
            return 0D;
        }
        IEnergizedItem source = (IEnergizedItem) sourceStack.getItem();
        double available = Math.min(MekaSuitConfig.suitInventoryChargeRate, source.getEnergy(sourceStack));
        if (!(available > 0D)) {
            return 0D;
        }
        return chargeItems(sourceStack, source, available, player.getHeldItemMainhand(),
              player.getHeldItemOffhand(), player.inventory.mainInventory);
    }

    static double chargeItems(ItemStack sourceStack, IEnergizedItem source, double available,
          ItemStack mainHand, ItemStack offHand, Iterable<ItemStack> inventory) {
        ChargeBuffer buffer = new ChargeBuffer(available);
        charge(buffer, mainHand, sourceStack);
        charge(buffer, offHand, sourceStack);
        if (buffer.getEnergy() > 0D) {
            for (ItemStack stack : inventory) {
                if (stack != mainHand && stack != offHand) {
                    charge(buffer, stack, sourceStack);
                    if (!(buffer.getEnergy() > 0D)) {
                        break;
                    }
                }
            }
        }
        double used = available - buffer.getEnergy();
        if (used > 0D) {
            source.setEnergy(sourceStack, source.getEnergy(sourceStack) - used);
        }
        return used;
    }

    static void balanceSuit(Iterable<ItemStack> armor) {
        List<EnergyEntry> entries = new ArrayList<>(4);
        double total = 0D;
        for (ItemStack stack : armor) {
            if (!stack.isEmpty() && stack.getItem() instanceof IEnergizedItem) {
                IEnergizedItem item = (IEnergizedItem) stack.getItem();
                double capacity = Math.max(0D, item.getMaxEnergy(stack));
                if (capacity > 0D) {
                    entries.add(new EnergyEntry(stack, item, capacity));
                    total += Math.max(0D, Math.min(capacity, item.getEnergy(stack)));
                }
            }
        }
        if (entries.size() < 2) {
            return;
        }
        entries.sort(Comparator.comparingDouble(entry -> entry.capacity));
        double remaining = total;
        for (int i = 0; i < entries.size(); i++) {
            EnergyEntry entry = entries.get(i);
            double share = remaining / (entries.size() - i);
            double target = Math.min(entry.capacity, share);
            entry.item.setEnergy(entry.stack, target);
            remaining -= target;
        }
    }

    private static void charge(ChargeBuffer buffer, ItemStack target, ItemStack source) {
        if (buffer.getEnergy() > 0D && target != source && target != null && !target.isEmpty()) {
            if (target.getItem() instanceof IEnergizedItem) {
                buffer.setEnergy(buffer.getEnergy() - EnergizedItemManager.charge(target, buffer.getEnergy()));
            } else {
                ChargeUtils.charge(target, buffer);
            }
        }
    }

    private static final class EnergyEntry {

        private final ItemStack stack;
        private final IEnergizedItem item;
        private final double capacity;

        private EnergyEntry(ItemStack stack, IEnergizedItem item, double capacity) {
            this.stack = stack;
            this.item = item;
            this.capacity = capacity;
        }
    }

    private static final class ChargeBuffer implements IStrictEnergyStorage {

        private final double capacity;
        private double energy;

        private ChargeBuffer(double energy) {
            capacity = Math.max(0D, energy);
            this.energy = capacity;
        }

        @Override
        public double getEnergy() {
            return energy;
        }

        @Override
        public void setEnergy(double energy) {
            this.energy = Math.max(0D, Math.min(capacity, energy));
        }

        @Override
        public double getMaxEnergy() {
            return capacity;
        }
    }
}
