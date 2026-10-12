package mekanism.mekasuit.common.content.gear;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/** Potion acceleration and preventable-magic absorption for 1.7.10. */
public final class MekaSuitInhalationHelper
{
    public static final MekaSuitInhalationHelper INSTANCE =
          new MekaSuitInhalationHelper();
    public static final String BENEFICIAL_CONFIG = "purification.beneficial";
    public static final String NEUTRAL_CONFIG = "purification.neutral";
    public static final String HARMFUL_CONFIG = "purification.harmful";

    private static final int EXTRA_DURATION_TICKS = 9;
    private static final ItemStack MILK_BUCKET = new ItemStack(
          Items.milk_bucket);
    private static final Field POTION_DURATION = findPotionDurationField();

    private MekaSuitInhalationHelper()
    {
    }

    private static Field findPotionDurationField()
    {
        for (String name : new String[] {"duration", "field_76460_b"})
        {
            try
            {
                Field field = PotionEffect.class.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException ignored)
            {
            }
        }
        throw new IllegalStateException(
              "Unable to find PotionEffect duration field");
    }

    @SuppressWarnings("unchecked")
    public static void tick(ItemStack helmet, EntityPlayer player,
          boolean server)
    {
        ModuleData module = getModule(helmet);
        if (module == null || !module.isEnabled() || player == null)
        {
            return;
        }
        long usage = Math.max(0, MekaSuitConfig.suitPotionTickUsage);
        boolean free = usage == 0 || player.capabilities.isCreativeMode;
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)helmet.getItem();
        double energy = free ? 0 : armor.getEnergy(helmet);
        if (!free && energy < usage)
        {
            return;
        }
        Collection<PotionEffect> active = player.getActivePotionEffects();
        for (PotionEffect effect : new ArrayList<PotionEffect>(active))
        {
            if (!canHandle(effect, module))
            {
                continue;
            }
            if (!free)
            {
                if (energy < usage)
                {
                    break;
                }
                energy -= usage;
                if (server)
                {
                    armor.setEnergy(helmet, armor.getEnergy(helmet) - usage);
                }
            }
            speedUpEffectSafely(effect);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer)
              || event.entityLiving.worldObj.isRemote || event.ammount <= 0
              || !isPreventableMagic(event.source))
        {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entityLiving;
        ItemStack helmet = player.getCurrentArmor(3);
        ModuleData module = getModule(helmet);
        if (module == null || !module.isEnabled())
        {
            return;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)helmet.getItem();
        DamageAbsorption absorption = calculateDamageAbsorption(event.ammount,
              MekaSuitConfig.suitMagicDamageReductionRatio,
              armor.getEnergy(helmet), MekaSuitConfig.suitMagicReduceUsage);
        if (absorption.energyUsed > 0)
        {
            armor.setEnergy(helmet,
                  armor.getEnergy(helmet) - absorption.energyUsed);
        }
        event.ammount = absorption.remainingDamage;
    }

    static boolean canHandle(PotionEffect effect, ModuleData module)
    {
        if (effect == null || module == null
              || !effect.isCurativeItem(MILK_BUCKET))
        {
            return false;
        }
        Potion potion = effect.getPotionID() >= 0
              && effect.getPotionID() < Potion.potionTypes.length
              ? Potion.potionTypes[effect.getPotionID()] : null;
        return potion != null && (potion.isBadEffect()
              ? module.getBooleanConfig(HARMFUL_CONFIG)
              : module.getBooleanConfig(BENEFICIAL_CONFIG));
    }

    static void speedUpEffectSafely(PotionEffect effect)
    {
        if (effect == null || effect.getDuration() <= 0)
        {
            return;
        }
        try
        {
            POTION_DURATION.setInt(effect,
                  Math.max(0, effect.getDuration() - EXTRA_DURATION_TICKS));
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException(
                  "Unable to shorten potion duration", e);
        }
    }

    static boolean isPreventableMagic(DamageSource source)
    {
        return source == DamageSource.magic || source != null
              && "indirectMagic".equals(source.getDamageType());
    }

    static DamageAbsorption calculateDamageAbsorption(float damage,
          float configuredRatio, double availableEnergy, long energyPerDamage)
    {
        if (!(damage > 0) || !(configuredRatio > 0))
        {
            return new DamageAbsorption(damage > 0 ? damage : 0, 0);
        }
        float ratio = Math.max(0F, Math.min(1F, configuredRatio));
        double requested = damage * ratio;
        double required = energyPerDamage <= 0 ? 0
              : Math.ceil(energyPerDamage * requested);
        double absorbedRatio = ratio;
        double energyUsed = 0;
        if (required > 0)
        {
            double usable = availableEnergy > 0
                  && !Double.isNaN(availableEnergy) ? availableEnergy : 0;
            energyUsed = Math.min(usable, required);
            absorbedRatio *= energyUsed / required;
        }
        return new DamageAbsorption(Math.max(0F,
              damage * (1F - (float)absorbedRatio)), energyUsed);
    }

    private static ModuleData getModule(ItemStack helmet)
    {
        if (helmet == null
              || !(helmet.getItem() instanceof ItemMekaSuitArmor))
        {
            return null;
        }
        ItemMekaSuitArmor armor = (ItemMekaSuitArmor)helmet.getItem();
        return armor.getModuleTarget() == ModuleTarget.HELMET
              ? ModuleContainer.fromStack(helmet, ModuleTarget.HELMET).get(
                    MekaSuitModules.INHALATION_PURIFICATION_UNIT) : null;
    }

    static final class DamageAbsorption
    {
        final float remainingDamage;
        final double energyUsed;

        DamageAbsorption(float remainingDamage, double energyUsed)
        {
            this.remainingDamage = remainingDamage;
            this.energyUsed = energyUsed;
        }
    }
}
