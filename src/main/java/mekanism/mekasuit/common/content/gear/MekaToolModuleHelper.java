package mekanism.mekasuit.common.content.gear;

import mekanism.api.energy.IEnergizedItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/** Stable Meka-Tool unit calculations adapted to the 1.7 item hooks. */
public final class MekaToolModuleHelper
{
    private MekaToolModuleHelper()
    {
    }

    public static float getEfficiency(ItemStack stack)
    {
        ModuleData data = enabled(stack,
              MekaSuitModules.EXCAVATION_ESCALATION_UNIT);
        return data == null ? MekaSuitConfig.toolEfficiency
              : ExcavationMode.byName(data.getMode()).efficiency;
    }

    public static int getAttackDamage(ItemStack stack)
    {
        ModuleData data = enabled(stack,
              MekaSuitModules.ATTACK_AMPLIFICATION_UNIT);
        return data == null ? 0 : AttackDamage.byName(data.getMode()).damage;
    }

    public static double getEffectiveAttackDamage(ItemStack stack)
    {
        int bonus = getAttackDamage(stack);
        if (bonus <= 0)
        {
            return MekaSuitConfig.toolBaseDamage;
        }
        long cost = getAttackEnergyCost(bonus);
        double available = stack != null
              && stack.getItem() instanceof IEnergizedItem
              ? ((IEnergizedItem)stack.getItem()).getEnergy(stack) : 0;
        double scale = cost <= 0 ? 1 : Math.min(1D, available / cost);
        return MekaSuitConfig.toolBaseDamage + bonus * scale;
    }

    public static long getAttackEnergyCost(int bonusDamage)
    {
        return scaledEnergy(MekaSuitConfig.toolWeaponUsage,
              Math.max(0, bonusDamage) / 4D);
    }

    public static boolean hasSilkTouch(ItemStack stack)
    {
        return enabled(stack, MekaSuitModules.SILK_TOUCH_UNIT) != null;
    }

    public static int getFortuneLevel(ItemStack stack)
    {
        ModuleData data = enabled(stack, MekaSuitModules.FORTUNE_UNIT);
        return data == null ? 0 : data.getInstalledCount();
    }

    public static int getFarmingDiameter(ItemStack stack)
    {
        ModuleData data = enabled(stack, MekaSuitModules.FARMING_UNIT);
        return data == null ? 0 : FarmingRadius.byName(data.getMode()).diameter;
    }

    public static boolean hasShearing(ItemStack stack)
    {
        return enabled(stack, MekaSuitModules.SHEARING_UNIT) != null;
    }

    public static int getBlastingRadius(ItemStack stack)
    {
        ModuleData data = enabled(stack, MekaSuitModules.BLASTING_UNIT);
        return data == null ? 0 : BlastingRadius.byName(data.getMode()).radius;
    }

    public static int getVeinMiningRange(ItemStack stack)
    {
        ModuleData data = enabled(stack, MekaSuitModules.VEIN_MINING_UNIT);
        return data == null ? -1 : VeinMiningRange.byName(data.getMode()).range;
    }

    public static boolean isExtendedVeinMining(ItemStack stack)
    {
        ModuleData data = enabled(stack, MekaSuitModules.VEIN_MINING_UNIT);
        return data != null && MekaSuitConfig.toolExtendedMining
              && data.getBooleanConfig("extended");
    }

    public static boolean hasTeleportation(ItemStack stack)
    {
        return enabled(stack, MekaSuitModules.TELEPORTATION_UNIT) != null;
    }

    public static boolean requiresTeleportationTarget(ItemStack stack)
    {
        ModuleData data = enabled(stack, MekaSuitModules.TELEPORTATION_UNIT);
        return data == null
              || data.getBooleanConfig("teleportation_requires_block");
    }

    public static long getTeleportationEnergyCost(double distanceSquared)
    {
        return scaledEnergy(MekaSuitConfig.toolTeleportUsage,
              Math.max(0D, distanceSquared) / 10D);
    }

    public static long getMiningEnergyCost(ItemStack stack, float hardness)
    {
        long cost = scaledEnergy(getBaseMiningUsage(stack),
              getEfficiency(stack));
        return hardness == 0 ? Math.max(1L, cost / 2) : cost;
    }

    public static long getVeinMiningEnergyCost(ItemStack stack, float hardness,
          int distance, boolean ore)
    {
        long base = hardness == 0
              ? Math.max(1L, getBaseMiningUsage(stack) / 2)
              : getBaseMiningUsage(stack);
        double multiplier = 0.5D * Math.pow(Math.max(0, distance),
              ore ? 1.5D : 2D);
        return scaledEnergy(base, multiplier);
    }

    public static void synchronizeHarvestEnchantments(ItemStack stack)
    {
        if (stack == null)
        {
            return;
        }
        int silkLevel = hasSilkTouch(stack) ? 1 : 0;
        int fortuneLevel = getFortuneLevel(stack);
        if (EnchantmentHelper.getEnchantmentLevel(
              Enchantment.silkTouch.effectId, stack) == silkLevel
              && EnchantmentHelper.getEnchantmentLevel(
                    Enchantment.fortune.effectId, stack) == fortuneLevel)
        {
            return;
        }
        NBTTagList updated = new NBTTagList();
        NBTTagList existing = stack.getEnchantmentTagList();
        if (existing != null)
        {
            for (int i = 0; i < existing.tagCount(); i++)
            {
                NBTTagCompound enchantment = existing.getCompoundTagAt(i);
                int id = enchantment.getShort("id");
                if (id != Enchantment.silkTouch.effectId
                      && id != Enchantment.fortune.effectId)
                {
                    updated.appendTag(enchantment.copy());
                }
            }
        }
        addEnchantment(updated, Enchantment.silkTouch.effectId, silkLevel);
        addEnchantment(updated, Enchantment.fortune.effectId, fortuneLevel);
        if (updated.tagCount() == 0)
        {
            if (stack.hasTagCompound())
            {
                stack.stackTagCompound.removeTag("ench");
            }
        }
        else
        {
            stack.setTagInfo("ench", updated);
        }
    }

    private static long getBaseMiningUsage(ItemStack stack)
    {
        return hasSilkTouch(stack) ? MekaSuitConfig.toolSilkMiningUsage
              : MekaSuitConfig.toolMiningUsage;
    }

    private static ModuleData enabled(ItemStack stack, ModuleType type)
    {
        if (stack == null)
        {
            return null;
        }
        ModuleData data = ModuleContainer.fromStack(stack,
              ModuleTarget.MEKA_TOOL).get(type);
        return data != null && data.isEnabled() ? data : null;
    }

    private static long scaledEnergy(long base, double multiplier)
    {
        if (base <= 0 || multiplier <= 0)
        {
            return 0;
        }
        double scaled = base * multiplier;
        if (Double.isNaN(scaled) || Double.isInfinite(scaled)
              || scaled >= Long.MAX_VALUE)
        {
            return Long.MAX_VALUE;
        }
        return Math.max(1L, (long)Math.ceil(scaled));
    }

    private static void addEnchantment(NBTTagList list, int id, int level)
    {
        if (level <= 0)
        {
            return;
        }
        NBTTagCompound enchantment = new NBTTagCompound();
        enchantment.setShort("id", (short)id);
        enchantment.setShort("lvl", (short)level);
        list.appendTag(enchantment);
    }

    private enum ExcavationMode
    {
        OFF("off", 0), SLOW("slow", 4), NORMAL("normal", 16),
        FAST("fast", 32), SUPER_FAST("super_fast", 64),
        EXTREME("extreme", 128);

        private final String name;
        private final float efficiency;

        ExcavationMode(String name, float efficiency)
        {
            this.name = name;
            this.efficiency = efficiency;
        }

        private static ExcavationMode byName(String name)
        {
            for (ExcavationMode mode : values())
            {
                if (mode.name.equals(name))
                {
                    return mode;
                }
            }
            return NORMAL;
        }
    }

    private enum AttackDamage
    {
        OFF("off", 0), LOW("low", 4), MED("med", 8), HIGH("high", 16),
        EXTREME("extreme", 24), MAX("max", 32);

        private final String name;
        private final int damage;

        AttackDamage(String name, int damage)
        {
            this.name = name;
            this.damage = damage;
        }

        private static AttackDamage byName(String name)
        {
            for (AttackDamage damage : values())
            {
                if (damage.name.equals(name))
                {
                    return damage;
                }
            }
            return MED;
        }
    }

    private enum FarmingRadius
    {
        OFF("off", 0), LOW("low", 1), MED("med", 3), HIGH("high", 5),
        ULTRA("ultra", 7);

        private final String name;
        private final int diameter;

        FarmingRadius(String name, int diameter)
        {
            this.name = name;
            this.diameter = diameter;
        }

        private static FarmingRadius byName(String name)
        {
            for (FarmingRadius radius : values())
            {
                if (radius.name.equals(name))
                {
                    return radius;
                }
            }
            return LOW;
        }
    }

    private enum BlastingRadius
    {
        OFF("off", 0), LOW("low", 1), MED("med", 2), HIGH("high", 3),
        EXTREME("extreme", 4);

        private final String name;
        private final int radius;

        BlastingRadius(String name, int radius)
        {
            this.name = name;
            this.radius = radius;
        }

        private static BlastingRadius byName(String name)
        {
            for (BlastingRadius radius : values())
            {
                if (radius.name.equals(name))
                {
                    return radius;
                }
            }
            return LOW;
        }
    }

    private enum VeinMiningRange
    {
        OFF("off", 0), LOW("low", 2), MED("med", 4), HIGH("high", 6),
        EXTREME("extreme", 8);

        private final String name;
        private final int range;

        VeinMiningRange(String name, int range)
        {
            this.name = name;
            this.range = range;
        }

        private static VeinMiningRange byName(String name)
        {
            for (VeinMiningRange range : values())
            {
                if (range.name.equals(name))
                {
                    return range;
                }
            }
            return LOW;
        }
    }
}
