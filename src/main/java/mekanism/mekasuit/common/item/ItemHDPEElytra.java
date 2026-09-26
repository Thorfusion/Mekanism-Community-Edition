package mekanism.mekasuit.common.item;

import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.common.MekanismItems;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

/** Stable HDPE Reinforced Elytra adapted to 1.12's durability and equipment APIs. */
public final class ItemHDPEElytra extends ItemElytra {

    public static final int MAX_DAMAGE = 648;

    public ItemHDPEElytra() {
        setMaxDamage(MAX_DAMAGE);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.CHEST;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair != null && !repair.isEmpty() && repair.getItem() == MekanismItems.Polyethene
              && repair.getMetadata() == 2;
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
        return armorType == EntityEquipmentSlot.CHEST;
    }
}
