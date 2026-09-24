package mekanism.mekasuit.common.item;

import com.google.common.collect.Multimap;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.EnumColor;
import mekanism.common.item.ItemEnergized;
import mekanism.common.util.LangUtils;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.MekaSuitEnergyHelper;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** First 1.12 vertical slice of the stable Meka-Tool. */
public final class ItemMekaTool extends ItemEnergized implements IModuleContainerItem {

    public ItemMekaTool() {
        super(MekaSuitConfig.DEFAULT_TOOL_CAPACITY);
    }

    @Override
    public ModuleTarget getModuleTarget() {
        return ModuleTarget.MEKA_TOOL;
    }

    @Override
    public double getMaxEnergy(ItemStack stack) {
        return MekaSuitEnergyHelper.getScaledValue(stack, getModuleTarget(), MekaSuitConfig.toolCapacity);
    }

    @Override
    public double getMaxTransfer(ItemStack stack) {
        return MekaSuitEnergyHelper.getScaledValue(stack, getModuleTarget(), MekaSuitConfig.toolChargeRate);
    }

    @Override
    public boolean canSend(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canHarvestBlock(@Nonnull IBlockState state, ItemStack stack) {
        return state.getBlock() != Blocks.BEDROCK && getEnergy(stack) >= getMiningEnergyCost(1F);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return getEnergy(stack) >= getMiningEnergyCost(1F)
              ? MekaSuitConfig.toolEfficiency : 0F;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode) {
            setEnergy(stack, getEnergy(stack) - getMiningEnergyCost(state.getBlockHardness(world, pos)));
        }
        return true;
    }

    public long getMiningEnergyCost(float hardness) {
        long cost;
        double scaled = MekaSuitConfig.toolMiningUsage * (double) MekaSuitConfig.toolEfficiency;
        if (scaled >= Long.MAX_VALUE) {
            cost = Long.MAX_VALUE;
        } else {
            cost = Math.max(1L, (long) Math.ceil(scaled));
        }
        return hardness == 0 ? Math.max(1L, cost / 2) : cost;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        ModuleContainer modules = ModuleContainer.fromStack(stack, getModuleTarget());
        tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.mekasuit.modules") + ": "
              + EnumColor.GREY + modules.getModules().size());
        if (flag.isAdvanced()) {
            for (ModuleData module : modules.getModules()) {
                tooltip.add(EnumColor.INDIGO + "- " + LangUtils.localize(module.getType().getTranslationKey())
                      + " x" + module.getInstalledCount());
            }
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Nonnull
    @Override
    @Deprecated
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = super.getItemAttributeModifiers(slot);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                  new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Meka-Tool damage", 4D, 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                  new AttributeModifier(ATTACK_SPEED_MODIFIER, "Meka-Tool speed", -2.4D, 0));
        }
        return modifiers;
    }
}
