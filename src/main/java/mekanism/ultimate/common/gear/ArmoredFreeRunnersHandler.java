package mekanism.ultimate.common.gear;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.UUID;
import mekanism.ultimate.common.config.UltimateGearConfig;
import mekanism.ultimate.common.item.ItemArmoredFreeRunners;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

/** Applies the modern boots-only knockback attribute on legacy entities. */
public final class ArmoredFreeRunnersHandler
{
    public static final ArmoredFreeRunnersHandler INSTANCE =
          new ArmoredFreeRunnersHandler();

    static final UUID KNOCKBACK_UUID = UUID.fromString(
          "590b179c-c66e-42a0-8759-84382bcbf8b6");

    private ArmoredFreeRunnersHandler()
    {
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event)
    {
        EntityLivingBase entity = event.entityLiving;
        if (entity.worldObj.isRemote)
        {
            return;
        }
        ItemStack boots = entity.getEquipmentInSlot(1);
        double resistance = boots != null
              && boots.getItem() instanceof ItemArmoredFreeRunners
              ? clampResistance(
                    UltimateGearConfig.armoredFreeRunnerKnockbackResistance)
              : 0D;
        IAttributeInstance attribute = entity.getEntityAttribute(
              SharedMonsterAttributes.knockbackResistance);
        AttributeModifier current = attribute.getModifier(KNOCKBACK_UUID);
        if (current != null
              && Double.compare(current.getAmount(), resistance) != 0)
        {
            attribute.removeModifier(current);
            current = null;
        }
        if (resistance <= 0D)
        {
            if (current != null)
            {
                attribute.removeModifier(current);
            }
        }
        else if (current == null)
        {
            attribute.applyModifier(new AttributeModifier(KNOCKBACK_UUID,
                  "Armored Free Runners knockback resistance", resistance,
                  0).setSaved(false));
        }
    }

    static double clampResistance(double resistance)
    {
        return Math.max(0D, Math.min(1D, resistance));
    }
}
