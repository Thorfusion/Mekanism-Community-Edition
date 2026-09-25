package mekanism.mekasuit.common.item;

import com.google.common.collect.HashMultimap;
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
import mekanism.mekasuit.common.content.gear.MekaToolInteractionHelper;
import mekanism.mekasuit.common.content.gear.MekaToolMiningHelper;
import mekanism.mekasuit.common.content.gear.MekaToolModuleHelper;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
        long required = getMiningEnergyCost(stack, 1F);
        double available = getEnergy(stack);
        return state.getBlock() != Blocks.BEDROCK && MekaToolModuleHelper.getEfficiency(stack) > 0
              && (available >= required || required > 0 && available / required > 1.0E-5D);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        float efficiency = MekaToolModuleHelper.getEfficiency(stack);
        if (efficiency <= 0) {
            return 0;
        }
        long required = getMiningEnergyCost(stack, 1F);
        double available = getEnergy(stack);
        return available >= required ? efficiency
              : (float) (MekaSuitConfig.toolEfficiency * available / required);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode) {
            setEnergy(stack, getEnergy(stack) - getMiningEnergyCost(stack, state.getBlockHardness(world, pos)));
        }
        return true;
    }

    public long getMiningEnergyCost(float hardness) {
        double scaled = MekaSuitConfig.toolMiningUsage * (double) MekaSuitConfig.toolEfficiency;
        long cost = scaled >= Long.MAX_VALUE ? Long.MAX_VALUE : Math.max(1L, (long) Math.ceil(scaled));
        return hardness == 0 ? Math.max(1L, cost / 2) : cost;
    }

    public long getMiningEnergyCost(ItemStack stack, float hardness) {
        return MekaToolModuleHelper.getMiningEnergyCost(stack, hardness);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        boolean sheared = MekaToolInteractionHelper.shearBlock(this, stack, pos, player);
        if (!sheared) {
            MekaToolMiningHelper.mineArea(this, stack, pos, player);
        }
        return sheared;
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
          EnumFacing side, float hitX, float hitY, float hitZ) {
        return MekaToolInteractionHelper.useFarming(this, player.getHeldItem(hand), player, world, pos, side);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        return MekaToolInteractionHelper.shearEntity(this, stack, player, target);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (!world.isRemote) {
            MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!(attacker instanceof EntityPlayer) || !((EntityPlayer) attacker).capabilities.isCreativeMode) {
            int bonusDamage = MekaToolModuleHelper.getAttackDamage(stack);
            if (bonusDamage > 0) {
                setEnergy(stack, getEnergy(stack) - MekaToolModuleHelper.getAttackEnergyCost(bonusDamage));
            }
        }
        return true;
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
                String details = EnumColor.INDIGO + "- " + LangUtils.localize(module.getType().getTranslationKey())
                      + " x" + module.getInstalledCount();
                if (!module.isEnabled()) {
                    details += " [" + LangUtils.localize("gui.mekasuit.off") + "]";
                } else if (module.getType().hasModes()) {
                    details += " [" + LangUtils.localize("module.mode." + module.getMode()) + "]";
                }
                tooltip.add(details);
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

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                  new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Meka-Tool damage",
                        MekaToolModuleHelper.getEffectiveAttackDamage(stack), 0));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                  new AttributeModifier(ATTACK_SPEED_MODIFIER, "Meka-Tool speed", MekaSuitConfig.toolAttackSpeed, 0));
        }
        return modifiers;
    }
}
