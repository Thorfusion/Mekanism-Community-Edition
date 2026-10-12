package mekanism.ultimate.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.common.item.ItemFreeRunners;
import mekanism.ultimate.client.model.ModelArmoredFreeRunnersArmor;
import mekanism.ultimate.common.config.UltimateGearConfig;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

/** Stable Armored Free Runners behavior adapted to Forge 1.7 armor hooks. */
public final class ItemArmoredFreeRunners extends ItemFreeRunners
      implements ISpecialArmor
{
    public ItemArmoredFreeRunners()
    {
        setUnlocalizedName("ArmoredFreeRunners");
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.rare;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase entity,
          ItemStack armor, DamageSource source, double damage, int slot)
    {
        double ratio = source.isUnblockable() ? 0D
              : calculateAbsorptionRatio(
                    UltimateGearConfig.armoredFreeRunnerArmor,
                    UltimateGearConfig.armoredFreeRunnerToughness,
                    damage);
        return new ArmorProperties(0, ratio, Integer.MAX_VALUE);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor,
          int slot)
    {
        return UltimateGearConfig.armoredFreeRunnerArmor;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack,
          DamageSource source, int damage, int slot)
    {
        // Energy is the durability resource; physical armor does not wear out.
    }

    /**
     * Reproduces the 1.12 armor/toughness curve through 1.7's ratio-based
     * special-armor API.
     */
    public static double calculateAbsorptionRatio(int armor,
          double toughness, double damage)
    {
        if (armor <= 0 || damage <= 0 || Double.isNaN(damage))
        {
            return 0D;
        }
        double safeToughness = Math.max(0D, toughness);
        double effectiveArmor = Math.max(armor * 0.2D,
              armor - damage / (2D + safeToughness / 4D));
        effectiveArmor = Math.min(20D, effectiveArmor);
        return Math.max(0D, Math.min(1D, effectiveArmor / 25D));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot,
          String type)
    {
        return "mekanismultimate:textures/entity/armor/armored_free_runners.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving,
          ItemStack itemStack, int armorSlot)
    {
        return ModelArmoredFreeRunnersArmor.INSTANCE;
    }
}
