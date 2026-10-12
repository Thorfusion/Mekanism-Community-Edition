package mekanism.mekasuit.common.content.gear;

import mekanism.api.MekanismConfig.general;
import mekanism.api.gas.GasRegistry;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTransmission;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

/** Server-side Electrolytic Breathing Unit behavior for the legacy gas API. */
public final class MekaSuitBreathingHelper
{
    public static final String FILL_HELD_CONFIG = "fill_held";
    private static final int MAX_AIR = 300;

    private MekaSuitBreathingHelper()
    {
    }

    public static void tick(ItemStack helmet, EntityPlayer player)
    {
        if (helmet == null || !(helmet.getItem() instanceof ItemMekaSuitArmor)
              || player == null || player.worldObj == null)
        {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)helmet.getItem();
        if (armor.getModuleTarget() != ModuleTarget.HELMET)
        {
            return;
        }
        ModuleData module = ModuleContainer.fromStack(helmet,
              ModuleTarget.HELMET).get(
                    MekaSuitModules.ELECTROLYTIC_BREATHING_UNIT);
        if (module == null || !module.isEnabled())
        {
            return;
        }
        int productionRate = getEnvironmentalRate(player,
              module.getInstalledCount());
        if (productionRate <= 0)
        {
            return;
        }
        double usagePerUnit = Math.max(0, general.FROM_H2 * 2D);
        int maxRate = getEnergyLimitedRate(productionRate,
              armor.getEnergy(helmet), usagePerUnit);
        if (maxRate <= 0)
        {
            return;
        }

        int hydrogenAvailable = maxRate * 2;
        int hydrogenAccepted = 0;
        ItemStack chest = player.getCurrentArmor(2);
        if (canFillChest(chest))
        {
            int accepted = fillHydrogen(chest, hydrogenAvailable);
            hydrogenAccepted += accepted;
            hydrogenAvailable -= accepted;
        }
        if (module.getBooleanConfig(FILL_HELD_CONFIG)
              && hydrogenAvailable > 0)
        {
            hydrogenAccepted += fillHydrogen(
                  player.getCurrentEquippedItem(), hydrogenAvailable);
        }
        int oxygenAccepted = Math.min(maxRate,
              Math.max(0, MAX_AIR - player.getAir()));
        int used = getElectrolysisUnitsUsed(hydrogenAccepted,
              oxygenAccepted);
        if (used > 0)
        {
            armor.setEnergy(helmet, armor.getEnergy(helmet)
                  - usagePerUnit * used);
            player.setAir(player.getAir() + oxygenAccepted);
        }
    }

    static int getMaxProductionRate(int installedCount)
    {
        int clamped = Math.max(0, Math.min(4, installedCount));
        return clamped == 0 ? 0 : 1 << clamped;
    }

    static int getEnergyLimitedRate(int productionRate, double storedEnergy,
          double usagePerUnit)
    {
        if (productionRate <= 0 || storedEnergy < 0
              || Double.isNaN(storedEnergy))
        {
            return 0;
        }
        if (!(usagePerUnit > 0) || Double.isInfinite(storedEnergy))
        {
            return productionRate;
        }
        return Math.min(productionRate, (int)Math.min(Integer.MAX_VALUE,
              Math.floor(storedEnergy / usagePerUnit)));
    }

    static int getElectrolysisUnitsUsed(int hydrogenAccepted,
          int oxygenAccepted)
    {
        return Math.max((Math.max(0, hydrogenAccepted) + 1) / 2,
              Math.max(0, oxygenAccepted));
    }

    private static int getEnvironmentalRate(EntityPlayer player,
          int installedCount)
    {
        int maxRate = getMaxProductionRate(installedCount);
        if (player.isInsideOfMaterial(Material.water))
        {
            return maxRate;
        }
        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY + player.getEyeHeight());
        int z = MathHelper.floor_double(player.posZ);
        return player.worldObj.isRaining()
              && player.worldObj.canBlockSeeTheSky(x, y, z)
              ? maxRate / 2 : 0;
    }

    private static boolean canFillChest(ItemStack chest)
    {
        if (chest != null && chest.getItem() instanceof ItemMekaSuitArmor)
        {
            ItemMekaSuitArmor armor = (ItemMekaSuitArmor)chest.getItem();
            return armor.getModuleTarget() != ModuleTarget.BODYARMOR
                  || ModuleContainer.fromStack(chest,
                        ModuleTarget.BODYARMOR).getInstalledCount(
                              MekaSuitModules.JETPACK_UNIT) > 0;
        }
        return true;
    }

    private static int fillHydrogen(ItemStack stack, int amount)
    {
        return amount <= 0 || GasRegistry.getGas("hydrogen") == null ? 0
              : GasTransmission.addGas(stack, new GasStack(
                    GasRegistry.getGas("hydrogen"), amount));
    }
}
