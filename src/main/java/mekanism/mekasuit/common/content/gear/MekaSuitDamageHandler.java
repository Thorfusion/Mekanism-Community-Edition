package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Base MekaSuit energy absorption, applied before vanilla armor reduction. */
public final class MekaSuitDamageHandler {

    public static final MekaSuitDamageHandler INSTANCE = new MekaSuitDamageHandler();

    private MekaSuitDamageHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        if (!(living instanceof EntityPlayer) || living.world.isRemote || event.getAmount() <= 0
              || !canAbsorb(event.getSource())) {
            return;
        }
        float originalDamage = event.getAmount();
        float absorbedRatio = 0F;
        for (ItemStack stack : living.getArmorInventoryList()) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemMekaSuitArmor)) {
                continue;
            }
            ItemMekaSuitArmor armor = (ItemMekaSuitArmor) stack.getItem();
            Absorption absorption = calculateAbsorption(originalDamage, armor.getDamageAbsorptionRatio(),
                  absorbedRatio, armor.getEnergy(stack), MekaSuitConfig.suitDamageUsage);
            if (absorption.energyUsed > 0) {
                armor.setEnergy(stack, armor.getEnergy(stack) - absorption.energyUsed);
            }
            absorbedRatio += absorption.absorbedRatio;
            if (absorbedRatio >= 1F) {
                break;
            }
        }
        if (absorbedRatio > 0F) {
            event.setAmount(originalDamage * Math.max(0F, 1F - absorbedRatio));
        }
    }

    static boolean canAbsorb(DamageSource source) {
        if (source == null || source == DamageSource.OUT_OF_WORLD || source.isDamageAbsolute()) {
            return false;
        }
        if (!source.isUnblockable()) {
            return true;
        }
        String type = source.getDamageType();
        return "anvil".equals(type) || "cactus".equals(type) || "cramming".equals(type)
              || "dragonBreath".equals(type) || "fall".equals(type) || "fallingBlock".equals(type)
              || "flyIntoWall".equals(type) || "generic".equals(type) || "hotFloor".equals(type)
              || "inFire".equals(type) || "inWall".equals(type) || "lava".equals(type)
              || "lightningBolt".equals(type) || "onFire".equals(type) || "wither".equals(type);
    }

    static Absorption calculateAbsorption(float damage, float pieceRatio, float alreadyAbsorbed,
          double availableEnergy, long energyPerDamage) {
        float ratio = Math.min(Math.max(0F, pieceRatio), Math.max(0F, 1F - alreadyAbsorbed));
        if (damage <= 0 || ratio <= 0) {
            return new Absorption(0F, 0D);
        }
        double required = energyPerDamage <= 0 ? 0D : Math.ceil(damage * ratio * energyPerDamage);
        if (required <= 0) {
            return new Absorption(ratio, 0D);
        }
        double usable = Double.isFinite(availableEnergy) ? Math.max(0D, availableEnergy) : 0D;
        double used = Math.min(usable, required);
        return new Absorption((float) (ratio * used / required), used);
    }

    static final class Absorption {

        final float absorbedRatio;
        final double energyUsed;

        Absorption(float absorbedRatio, double energyUsed) {
            this.absorbedRatio = absorbedRatio;
            this.energyUsed = energyUsed;
        }
    }
}
