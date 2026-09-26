package mekanism.mekasuit.common.content.gear;

import java.lang.reflect.Field;
import java.util.ArrayList;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Stable Inhalation Purification behavior adapted to 1.12 potion and damage APIs. */
public final class MekaSuitInhalationHelper {

    public static final MekaSuitInhalationHelper INSTANCE = new MekaSuitInhalationHelper();
    public static final String BENEFICIAL_CONFIG = "purification.beneficial";
    public static final String NEUTRAL_CONFIG = "purification.neutral";
    public static final String HARMFUL_CONFIG = "purification.harmful";

    private static final int EXTRA_DURATION_TICKS = 9;
    private static final ItemStack MILK_BUCKET = new ItemStack(Items.MILK_BUCKET);
    private static final Field POTION_DURATION = findPotionDurationField();

    private MekaSuitInhalationHelper() {
    }

    private static Field findPotionDurationField() {
        for (String name : new String[]{"duration", "field_76460_b"}) {
            try {
                Field field = PotionEffect.class.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new IllegalStateException("Unable to find PotionEffect duration field");
    }

    public static void tick(ItemStack helmet, EntityPlayer player, boolean server) {
        if (helmet.isEmpty() || !(helmet.getItem() instanceof ItemMekaSuitArmor)
              || player == null || player.world == null || player.isSpectator()) {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor) helmet.getItem();
        if (armor.getModuleTarget() != ModuleTarget.HELMET) {
            return;
        }
        ModuleData module = ModuleContainer.fromStack(helmet, ModuleTarget.HELMET)
              .get(MekaSuitModules.INHALATION_PURIFICATION_UNIT);
        if (module == null || !module.isEnabled()) {
            return;
        }

        long usage = Math.max(0, MekaSuitConfig.suitPotionTickUsage);
        boolean free = usage == 0 || player.capabilities.isCreativeMode;
        double energy = free ? 0 : armor.getEnergy(helmet);
        if (!free && energy < usage) {
            return;
        }
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            if (!canHandle(effect, module)) {
                continue;
            }
            if (!free) {
                if (energy < usage) {
                    break;
                }
                energy -= usage;
                if (server) {
                    armor.setEnergy(helmet, armor.getEnergy(helmet) - usage);
                }
            }
            speedUpEffectSafely(effect);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (!(living instanceof EntityPlayer) || living.world.isRemote || event.getAmount() <= 0
              || !isPreventableMagic(event.getSource())) {
            return;
        }
        EntityPlayer player = (EntityPlayer) living;
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helmet.isEmpty() || !(helmet.getItem() instanceof ItemMekaSuitArmor)) {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor) helmet.getItem();
        if (armor.getModuleTarget() != ModuleTarget.HELMET) {
            return;
        }
        ModuleData module = ModuleContainer.fromStack(helmet, ModuleTarget.HELMET)
              .get(MekaSuitModules.INHALATION_PURIFICATION_UNIT);
        if (module == null || !module.isEnabled()) {
            return;
        }

        DamageAbsorption absorption = calculateDamageAbsorption(event.getAmount(),
              MekaSuitConfig.suitMagicDamageReductionRatio, armor.getEnergy(helmet),
              MekaSuitConfig.suitMagicReduceUsage);
        if (absorption.energyUsed > 0) {
            armor.setEnergy(helmet, armor.getEnergy(helmet) - absorption.energyUsed);
        }
        event.setAmount(absorption.remainingDamage);
    }

    static boolean canHandle(PotionEffect effect, ModuleData module) {
        if (effect == null || effect.getPotion() == null || module == null || !effect.isCurativeItem(MILK_BUCKET)) {
            return false;
        }
        switch (getCategory(effect.getPotion())) {
            case BENEFICIAL:
                return module.getBooleanConfig(BENEFICIAL_CONFIG);
            case NEUTRAL:
                return module.getBooleanConfig(NEUTRAL_CONFIG);
            case HARMFUL:
                return module.getBooleanConfig(HARMFUL_CONFIG);
            default:
                return false;
        }
    }

    static EffectCategory getCategory(Potion potion) {
        if (potion == MobEffects.GLOWING) {
            return EffectCategory.NEUTRAL;
        }
        return potion != null && potion.isBadEffect() ? EffectCategory.HARMFUL : EffectCategory.BENEFICIAL;
    }

    static void speedUpEffectSafely(PotionEffect effect) {
        if (effect == null || effect.getDuration() <= 0) {
            return;
        }
        try {
            POTION_DURATION.setInt(effect, Math.max(0, effect.getDuration() - EXTRA_DURATION_TICKS));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to shorten potion duration", e);
        }
    }

    static boolean isPreventableMagic(DamageSource source) {
        return source == DamageSource.MAGIC || source != null && "indirectMagic".equals(source.getDamageType());
    }

    static DamageAbsorption calculateDamageAbsorption(float damage, float configuredRatio,
          double availableEnergy, long energyPerDamage) {
        if (!(damage > 0) || !(configuredRatio > 0)) {
            return new DamageAbsorption(damage > 0 ? damage : 0, 0);
        }
        float ratio = Math.max(0F, Math.min(1F, configuredRatio));
        double requestedAbsorption = damage * ratio;
        double requiredEnergy = energyPerDamage <= 0 ? 0 : Math.ceil(energyPerDamage * requestedAbsorption);
        double absorbedRatio = ratio;
        double energyUsed = 0;
        if (requiredEnergy > 0) {
            double usableEnergy = availableEnergy > 0 && !Double.isNaN(availableEnergy) ? availableEnergy : 0;
            energyUsed = Math.min(usableEnergy, requiredEnergy);
            absorbedRatio *= energyUsed / requiredEnergy;
        }
        return new DamageAbsorption(Math.max(0F, damage * (1F - (float) absorbedRatio)), energyUsed);
    }

    enum EffectCategory {
        BENEFICIAL,
        NEUTRAL,
        HARMFUL
    }

    static final class DamageAbsorption {

        final float remainingDamage;
        final double energyUsed;

        DamageAbsorption(float remainingDamage, double energyUsed) {
            this.remainingDamage = remainingDamage;
            this.energyUsed = energyUsed;
        }
    }
}
