package mekanism.mekasuit.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import mekanism.mekasuit.client.model.ModelArmoredFreeRunnersArmor;
import mekanism.common.item.ItemFreeRunners;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Stable Armored Free Runners behavior layered over the existing 1.12 Free Runner controller. */
public final class ItemArmoredFreeRunners extends ItemFreeRunners {

    private static final UUID ARMOR_UUID = UUID.fromString("a8c2ef76-a289-4ec2-80b8-afc8cb828bd3");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("4f4bfa9d-412a-47b7-bce6-b76e5702644f");
    private static final UUID KNOCKBACK_UUID = UUID.fromString("590b179c-c66e-42a0-8759-84382bcbf8b6");

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack,
          EntityEquipmentSlot armorSlot, ModelBiped defaultModel) {
        return ModelArmoredFreeRunnersArmor.INSTANCE;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));
        if (slot == EntityEquipmentSlot.FEET) {
            add(modifiers, SharedMonsterAttributes.ARMOR.getName(), ARMOR_UUID,
                  "Armored Free Runners armor", MekaSuitConfig.armoredFreeRunnerArmor);
            add(modifiers, SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), TOUGHNESS_UUID,
                  "Armored Free Runners toughness", MekaSuitConfig.armoredFreeRunnerToughness);
            add(modifiers, SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), KNOCKBACK_UUID,
                  "Armored Free Runners knockback resistance", MekaSuitConfig.armoredFreeRunnerKnockbackResistance);
        }
        return modifiers;
    }

    private static void add(Multimap<String, AttributeModifier> modifiers, String attribute, UUID id,
          String name, double amount) {
        if (amount > 0) {
            modifiers.put(attribute, new AttributeModifier(id, name, amount, 0));
        }
    }
}
