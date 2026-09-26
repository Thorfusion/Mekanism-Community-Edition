package mekanism.mekasuit.common.content.gear;

import mekanism.api.gas.GasStack;
import mekanism.common.MekanismFluids;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.GasUtils;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/** Server-side Electrolytic Breathing Unit behavior adapted to the 1.12 gas API. */
public final class MekaSuitBreathingHelper {

    public static final String FILL_HELD_CONFIG = "fill_held";
    private static final int MAX_AIR = 300;

    private MekaSuitBreathingHelper() {
    }

    public static void tick(ItemStack helmet, EntityPlayer player) {
        if (helmet.isEmpty() || !(helmet.getItem() instanceof ItemMekaSuitArmor)
              || player == null || player.world == null) {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor) helmet.getItem();
        if (armor.getModuleTarget() != ModuleTarget.HELMET) {
            return;
        }
        ModuleData module = ModuleContainer.fromStack(helmet, ModuleTarget.HELMET)
              .get(MekaSuitModules.ELECTROLYTIC_BREATHING_UNIT);
        if (module == null || !module.isEnabled()) {
            return;
        }
        int productionRate = getEnvironmentalRate(player, module.getInstalledCount());
        if (productionRate <= 0) {
            return;
        }

        double usagePerUnit = getEnergyUsagePerUnit();
        int maxRate = getEnergyLimitedRate(productionRate, armor.getEnergy(helmet), usagePerUnit);
        if (maxRate <= 0) {
            return;
        }

        int hydrogenAvailable = maxRate * 2;
        int hydrogenAccepted = 0;
        ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (canFillChest(chest)) {
            int accepted = fillHydrogen(chest, hydrogenAvailable);
            hydrogenAccepted += accepted;
            hydrogenAvailable -= accepted;
        }
        if (module.getBooleanConfig(FILL_HELD_CONFIG) && hydrogenAvailable > 0) {
            hydrogenAccepted += fillHydrogen(player.getHeldItemMainhand(), hydrogenAvailable);
        }

        int oxygenAccepted = Math.min(maxRate, Math.max(0, MAX_AIR - player.getAir()));
        int used = getElectrolysisUnitsUsed(hydrogenAccepted, oxygenAccepted);
        if (used > 0) {
            armor.setEnergy(helmet, armor.getEnergy(helmet) - usagePerUnit * used);
            player.setAir(player.getAir() + oxygenAccepted);
        }
    }

    static int getMaxProductionRate(int installedCount) {
        int clamped = Math.max(0, Math.min(4, installedCount));
        return clamped == 0 ? 0 : 1 << clamped;
    }

    static int getEnergyLimitedRate(int productionRate, double storedEnergy, double usagePerUnit) {
        if (productionRate <= 0 || storedEnergy < 0 || Double.isNaN(storedEnergy)) {
            return 0;
        }
        if (!(usagePerUnit > 0) || Double.isInfinite(storedEnergy)) {
            return productionRate;
        }
        return Math.min(productionRate, (int) Math.min(Integer.MAX_VALUE, Math.floor(storedEnergy / usagePerUnit)));
    }

    static int getElectrolysisUnitsUsed(int hydrogenAccepted, int oxygenAccepted) {
        return Math.max((Math.max(0, hydrogenAccepted) + 1) / 2, Math.max(0, oxygenAccepted));
    }

    private static int getEnvironmentalRate(EntityPlayer player, int installedCount) {
        int maxRate = getMaxProductionRate(installedCount);
        if (player.isInsideOfMaterial(Material.WATER)) {
            return maxRate;
        }
        BlockPos eye = new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        return player.world.isRainingAt(eye) ? maxRate / 2 : 0;
    }

    private static double getEnergyUsagePerUnit() {
        double hydrogenEnergyDensity = MekanismConfig.current().general.FROM_H2.val();
        return hydrogenEnergyDensity > 0 ? hydrogenEnergyDensity * 2D : 0D;
    }

    private static boolean canFillChest(ItemStack chest) {
        if (!chest.isEmpty() && chest.getItem() instanceof ItemMekaSuitArmor) {
            ItemMekaSuitArmor armor = (ItemMekaSuitArmor) chest.getItem();
            return armor.getModuleTarget() != ModuleTarget.BODYARMOR
                  || ModuleContainer.fromStack(chest, ModuleTarget.BODYARMOR)
                  .getInstalledCount(MekaSuitModules.JETPACK_UNIT) > 0;
        }
        return true;
    }

    private static int fillHydrogen(ItemStack stack, int amount) {
        return amount <= 0 ? 0 : GasUtils.addGas(stack, new GasStack(MekanismFluids.Hydrogen, amount));
    }
}
