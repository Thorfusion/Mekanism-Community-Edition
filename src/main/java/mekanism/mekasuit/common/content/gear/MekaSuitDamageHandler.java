package mekanism.mekasuit.common.content.gear;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/** Base MekaSuit energy absorption, applied before vanilla armor reduction. */
public final class MekaSuitDamageHandler
{
    public static final MekaSuitDamageHandler INSTANCE =
          new MekaSuitDamageHandler();

    private MekaSuitDamageHandler()
    {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingHurt(LivingHurtEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer)
              || event.entityLiving.worldObj.isRemote || event.ammount <= 0
              || !canAbsorb(event.source))
        {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entityLiving;
        float originalDamage = event.ammount;
        float absorbedRatio = 0;
        for (int slot = 1; slot <= 4; slot++)
        {
            ItemStack stack = player.getEquipmentInSlot(slot);
            if (stack == null
                  || !(stack.getItem() instanceof ItemMekaSuitArmor))
            {
                continue;
            }
            ItemMekaSuitArmor armor = (ItemMekaSuitArmor)stack.getItem();
            Absorption absorption = calculateAbsorption(originalDamage,
                  armor.getDamageAbsorptionRatio(), absorbedRatio,
                  armor.getEnergy(stack), MekaSuitConfig.suitDamageUsage);
            if (absorption.energyUsed > 0)
            {
                armor.setEnergy(stack,
                      armor.getEnergy(stack) - absorption.energyUsed);
            }
            absorbedRatio += absorption.absorbedRatio;
            if (absorbedRatio >= 1)
            {
                break;
            }
        }
        if (absorbedRatio > 0)
        {
            event.ammount = originalDamage * Math.max(0,
                  1 - absorbedRatio);
        }
    }

    static boolean canAbsorb(DamageSource source)
    {
        if (source == null || source == DamageSource.outOfWorld
              || source.isDamageAbsolute())
        {
            return false;
        }
        if (!source.isUnblockable())
        {
            return true;
        }
        String type = source.getDamageType();
        return "anvil".equals(type) || "cactus".equals(type)
              || "fall".equals(type) || "fallingBlock".equals(type)
              || "generic".equals(type) || "inFire".equals(type)
              || "inWall".equals(type) || "lava".equals(type)
              || "lightningBolt".equals(type) || "onFire".equals(type)
              || "wither".equals(type);
    }

    static Absorption calculateAbsorption(float damage, float pieceRatio,
          float alreadyAbsorbed, double availableEnergy,
          long energyPerDamage)
    {
        float ratio = Math.min(Math.max(0, pieceRatio),
              Math.max(0, 1 - alreadyAbsorbed));
        if (damage <= 0 || ratio <= 0)
        {
            return new Absorption(0, 0);
        }
        double required = energyPerDamage <= 0 ? 0
              : Math.ceil(damage * ratio * energyPerDamage);
        if (required <= 0)
        {
            return new Absorption(ratio, 0);
        }
        double usable = Double.isNaN(availableEnergy)
              || Double.isInfinite(availableEnergy) ? 0
              : Math.max(0, availableEnergy);
        double used = Math.min(usable, required);
        return new Absorption((float)(ratio * used / required), used);
    }

    static final class Absorption
    {
        final float absorbedRatio;
        final double energyUsed;

        Absorption(float absorbedRatio, double energyUsed)
        {
            this.absorbedRatio = absorbedRatio;
            this.energyUsed = energyUsed;
        }
    }
}
