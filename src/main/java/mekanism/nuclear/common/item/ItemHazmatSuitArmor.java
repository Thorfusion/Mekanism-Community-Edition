package mekanism.nuclear.common.item;

import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.nuclear.common.MekanismNuclear;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

/** Source-faithful zero-armor Hazmat equipment whose full set blocks radiation exposure. */
public final class ItemHazmatSuitArmor extends ItemArmor {

    private static final ArmorMaterial HAZMAT_MATERIAL = EnumHelper.addArmorMaterial(
          "MEKANISM_NUCLEAR_HAZMAT", "mekanismnuclear:hazmat", 0,
          new int[]{0, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);

    private final double shielding;

    public ItemHazmatSuitArmor(String registryName, String translationKey,
          EntityEquipmentSlot slot, double shielding) {
        super(HAZMAT_MATERIAL, 0, slot);
        if (shielding < 0 || shielding > 1) {
            throw new IllegalArgumentException("Hazmat shielding must be between zero and one");
        }
        this.shielding = shielding;
        setRegistryName(new ResourceLocation(MekanismNuclear.MODID, registryName));
        setTranslationKey(translationKey);
        setCreativeTab(Mekanism.tabMekanism);
    }

    public double getRadiationShielding() {
        return shielding;
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        int layer = slot == EntityEquipmentSlot.LEGS ? 2 : 1;
        return MekanismNuclear.MODID + ":textures/models/armor/hazmat_layer_" + layer + ".png";
    }
}
