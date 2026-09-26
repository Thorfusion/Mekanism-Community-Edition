package mekanism.mekasuit.common.content.gear;

import java.util.List;
import mekanism.api.energy.IEnergizedItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

/** 1.12-native adapters for the stable MekaSuit pants and boots units. */
public final class MekaSuitMobilityHelper {

    private MekaSuitMobilityHelper() {
    }

    public static ModuleData getEnabled(ItemStack stack, ModuleTarget target, ModuleType type) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemMekaSuitArmor)) {
            return null;
        }
        ModuleData data = ModuleContainer.fromStack(stack, target).get(type);
        return data != null && data.isEnabled() ? data : null;
    }

    public static float getSprintBoost(ItemStack pants) {
        ModuleData module = getEnabled(pants, ModuleTarget.PANTS, MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT);
        return module == null ? 0F : SprintBoost.byName(module.getMode()).boost;
    }

    public static long getSprintEnergyUsage(float boost) {
        return scaledEnergy(MekaSuitConfig.suitSprintBoostUsage, boost / 0.1D);
    }

    public static boolean canSprintBoost(ItemStack pants, EntityPlayer player) {
        float boost = getSprintBoost(pants);
        return boost > 0 && player.isSprinting() && !player.isElytraFlying()
              && canUseEnergy(pants, player, getSprintEnergyUsage(boost));
    }

    public static int getGyroscopicLevel(ItemStack pants) {
        ModuleData module = getEnabled(pants, ModuleTarget.PANTS, MekaSuitModules.GYROSCOPIC_STABILIZATION_UNIT);
        return module == null ? 0 : module.getInstalledCount();
    }

    public static int getHydrostaticLevel(ItemStack pants) {
        ModuleData module = getEnabled(pants, ModuleTarget.PANTS, MekaSuitModules.HYDROSTATIC_REPULSOR_UNIT);
        return module == null ? 0 : module.getInstalledCount();
    }

    public static boolean hasSwimBoost(ItemStack pants) {
        ModuleData module = getEnabled(pants, ModuleTarget.PANTS, MekaSuitModules.HYDROSTATIC_REPULSOR_UNIT);
        return module != null && module.getInstalledCount() >= 4 && module.getBooleanConfig("swim_boost");
    }

    public static int getMotorizedServoLevel(ItemStack pants) {
        ModuleData module = getEnabled(pants, ModuleTarget.PANTS, MekaSuitModules.MOTORIZED_SERVO_UNIT);
        return module == null ? 0 : module.getInstalledCount();
    }

    public static float getSneakSpeed(ItemStack pants) {
        return 0.3F + Math.min(1F, 0.15F * getMotorizedServoLevel(pants));
    }

    public static float getJumpBoost(ItemStack boots) {
        ModuleData module = getEnabled(boots, ModuleTarget.BOOTS, MekaSuitModules.HYDRAULIC_PROPULSION_UNIT);
        return module == null ? 0F : HydraulicSetting.byName(module.getEnumConfig("jump_boost")).value;
    }

    public static long getJumpEnergyUsage(float boost) {
        return scaledEnergy(MekaSuitConfig.suitBaseJumpUsage, boost / 0.1D);
    }

    public static float limitJumpBoostForSprint(float boost, boolean sprintBoostActive) {
        return sprintBoostActive ? (float) Math.sqrt(boost) : boost;
    }

    public static float getStepHeight(ItemStack boots) {
        ModuleData module = getEnabled(boots, ModuleTarget.BOOTS, MekaSuitModules.HYDRAULIC_PROPULSION_UNIT);
        return module == null ? 0F : StepSetting.byName(module.getEnumConfig("step_assist")).value;
    }

    public static float getMagneticRange(ItemStack boots) {
        ModuleData module = getEnabled(boots, ModuleTarget.BOOTS, MekaSuitModules.MAGNETIC_ATTRACTION_UNIT);
        return module == null ? 0F : MagneticRange.byName(module.getEnumConfig("range")).range;
    }

    public static int getFrostWalkerLevel(ItemStack boots) {
        ModuleData module = getEnabled(boots, ModuleTarget.BOOTS, MekaSuitModules.FROST_WALKER_UNIT);
        return module == null ? 0 : module.getInstalledCount();
    }

    public static int getSoulSurferLevel(ItemStack boots) {
        ModuleData module = getEnabled(boots, ModuleTarget.BOOTS, MekaSuitModules.SOUL_SURFER_UNIT);
        return module == null ? 0 : module.getInstalledCount();
    }

    public static void synchronizeMovementEnchantments(ItemStack pants, ItemStack boots) {
        synchronizeEnchantment(pants, Enchantments.DEPTH_STRIDER, Math.min(3, getHydrostaticLevel(pants)));
        synchronizeEnchantment(boots, Enchantments.FROST_WALKER, getFrostWalkerLevel(boots));
    }

    public static void attractItems(EntityPlayer player, ItemStack boots) {
        float range = getMagneticRange(boots);
        if (range <= 0) {
            return;
        }
        long usage = scaledEnergy(MekaSuitConfig.suitItemAttractionUsage, range);
        double reach = 4D + range;
        AxisAlignedBB box = player.getEntityBoundingBox().grow(reach);
        List<EntityItem> items = player.world.getEntitiesWithinAABB(EntityItem.class, box,
              item -> item != null && !item.isDead && !item.cannotPickup());
        for (EntityItem item : items) {
            if (item.getDistanceSq(player) <= 0.001D || !canUseEnergy(boots, player, usage)) {
                continue;
            }
            double dx = clamp(player.posX - item.posX, -1D, 1D);
            double dy = clamp(player.posY + 0.2D - item.posY, -1D, 1D);
            double dz = clamp(player.posZ - item.posZ, -1D, 1D);
            item.motionX += (dx - item.motionX) * 0.2D;
            item.motionY += (dy - item.motionY) * 0.2D;
            item.motionZ += (dz - item.motionZ) * 0.2D;
            item.velocityChanged = true;
            useEnergy(boots, player, usage);
        }
    }

    public static void applySoulSurfer(EntityPlayer player, ItemStack boots) {
        int level = getSoulSurferLevel(boots);
        if (level <= 0 || !player.onGround) {
            return;
        }
        BlockPos below = new BlockPos(player.posX, player.getEntityBoundingBox().minY - 0.2D, player.posZ);
        if (player.world.getBlockState(below).getBlock() != Blocks.SOUL_SAND) {
            return;
        }
        double efficiency = Math.min(1D, level / 3D);
        double desiredFactor = 0.4D + 0.6D * efficiency;
        double correction = desiredFactor / 0.4D;
        player.motionX *= correction;
        player.motionZ *= correction;
        player.velocityChanged = true;
    }

    public static boolean canUseEnergy(ItemStack stack, EntityPlayer player, long amount) {
        if (amount <= 0 || player.capabilities.isCreativeMode) {
            return true;
        }
        return stack.getItem() instanceof IEnergizedItem
              && ((IEnergizedItem) stack.getItem()).getEnergy(stack) >= amount;
    }

    public static boolean useEnergy(ItemStack stack, EntityPlayer player, long amount) {
        if (!canUseEnergy(stack, player, amount)) {
            return false;
        }
        if (amount > 0 && !player.capabilities.isCreativeMode) {
            IEnergizedItem energized = (IEnergizedItem) stack.getItem();
            energized.setEnergy(stack, energized.getEnergy(stack) - amount);
        }
        return true;
    }

    public static int getColor(ItemStack armor) {
        if (armor == null || armor.isEmpty() || !(armor.getItem() instanceof ItemMekaSuitArmor)) {
            return 0xFFFFFF;
        }
        ModuleData module = ModuleContainer.fromStack(armor,
              ((ItemMekaSuitArmor) armor.getItem()).getModuleTarget()).get(MekaSuitModules.COLOR_MODULATION_UNIT);
        return module == null ? 0xFFFFFF : color(module.getEnumConfig("color"));
    }

    private static void synchronizeEnchantment(ItemStack stack, Enchantment enchantment, int level) {
        if (stack == null || stack.isEmpty()
              || EnchantmentHelper.getEnchantmentLevel(enchantment, stack) == level) {
            return;
        }
        int enchantmentId = Enchantment.getEnchantmentID(enchantment);
        NBTTagList updated = new NBTTagList();
        NBTTagList existing = stack.getEnchantmentTagList();
        if (existing != null) {
            for (int i = 0; i < existing.tagCount(); i++) {
                NBTTagCompound entry = existing.getCompoundTagAt(i);
                if (entry.getShort("id") != enchantmentId) {
                    updated.appendTag(entry.copy());
                }
            }
        }
        if (level > 0) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setShort("id", (short) enchantmentId);
            entry.setShort("lvl", (short) level);
            updated.appendTag(entry);
        }
        if (updated.tagCount() == 0) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("ench", NBT.TAG_LIST)) {
                tag.removeTag("ench");
            }
        } else {
            stack.setTagInfo("ench", updated);
        }
    }

    private static long scaledEnergy(long base, double multiplier) {
        if (base <= 0 || multiplier <= 0) {
            return 0;
        }
        double value = base * multiplier;
        return !Double.isFinite(value) || value >= Long.MAX_VALUE ? Long.MAX_VALUE
              : Math.max(1L, (long) Math.ceil(value));
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static int color(String name) {
        switch (name) {
            case "orange": return 0xF9801D;
            case "magenta": return 0xC74EBD;
            case "light_blue": return 0x3AB3DA;
            case "yellow": return 0xFED83D;
            case "lime": return 0x80C71F;
            case "pink": return 0xF38BAA;
            case "gray": return 0x474F52;
            case "light_gray": return 0x9D9D97;
            case "cyan": return 0x169C9C;
            case "purple": return 0x8932B8;
            case "blue": return 0x3C44AA;
            case "brown": return 0x835432;
            case "green": return 0x5E7C16;
            case "red": return 0xB02E26;
            case "black": return 0x1D1D21;
            default: return 0xFFFFFF;
        }
    }

    private enum SprintBoost {
        OFF("off", 0F), LOW("low", 0.05F), MED("med", 0.1F), HIGH("high", 0.25F), ULTRA("ultra", 0.5F);
        private final String name;
        private final float boost;
        SprintBoost(String name, float boost) { this.name = name; this.boost = boost; }
        private static SprintBoost byName(String name) {
            for (SprintBoost value : values()) if (value.name.equals(name)) return value;
            return LOW;
        }
    }

    private enum HydraulicSetting {
        OFF("off", 0F), LOW("low", 0.5F), MED("med", 1F), HIGH("high", 3F), ULTRA("ultra", 5F);
        private final String name;
        private final float value;
        HydraulicSetting(String name, float value) { this.name = name; this.value = value; }
        private static HydraulicSetting byName(String name) {
            for (HydraulicSetting value : values()) if (value.name.equals(name)) return value;
            return LOW;
        }
    }

    private enum StepSetting {
        OFF("off", 0F), LOW("low", 0.5F), MED("med", 1F), HIGH("high", 1.5F), ULTRA("ultra", 2F);
        private final String name;
        private final float value;
        StepSetting(String name, float value) { this.name = name; this.value = value; }
        private static StepSetting byName(String name) {
            for (StepSetting value : values()) if (value.name.equals(name)) return value;
            return LOW;
        }
    }

    private enum MagneticRange {
        OFF("off", 0F), LOW("low", 1F), MED("med", 3F), HIGH("high", 5F), ULTRA("ultra", 10F);
        private final String name;
        private final float range;
        MagneticRange(String name, float range) { this.name = name; this.range = range; }
        private static MagneticRange byName(String name) {
            for (MagneticRange value : values()) if (value.name.equals(name)) return value;
            return LOW;
        }
    }
}
