package mekanism.mekasuit.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import mekanism.api.EnumColor;
import mekanism.common.item.ItemEnergized;
import mekanism.common.util.LangUtils;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.MekaSuitEnergyHelper;
import mekanism.mekasuit.common.content.gear.MekaToolInteractionHelper;
import mekanism.mekasuit.common.content.gear.MekaToolMiningHelper;
import mekanism.mekasuit.common.content.gear.MekaToolModuleHelper;
import mekanism.mekasuit.common.content.gear.MekaToolTeleportationHelper;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Energy-backed host for Meka-Tool modules. */
public final class ItemMekaTool extends ItemEnergized
      implements IModuleContainerItem, IMekaSuitItem
{
    public static final String REGISTRY_NAME = "meka_tool";

    public ItemMekaTool()
    {
        super(MekaSuitConfig.DEFAULT_TOOL_CAPACITY);
        setUnlocalizedName("MekaTool");
    }

    @Override
    public String getRegistryName()
    {
        return REGISTRY_NAME;
    }

    @Override
    public ModuleTarget getModuleTarget()
    {
        return ModuleTarget.MEKA_TOOL;
    }

    @Override
    public double getMaxEnergy(ItemStack stack)
    {
        return MekaSuitEnergyHelper.getScaledValue(stack, getModuleTarget(),
              MekaSuitConfig.toolCapacity);
    }

    @Override
    public double getMaxTransfer(ItemStack stack)
    {
        return MekaSuitEnergyHelper.getScaledValue(stack, getModuleTarget(),
              MekaSuitConfig.toolChargeRate);
    }

    @Override
    public boolean canSend(ItemStack stack)
    {
        return false;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.epic;
    }

    public boolean isEnchantable(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return false;
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack stack)
    {
        long required = getMiningEnergyCost(stack, 1F);
        double available = getEnergy(stack);
        return block != Blocks.bedrock
              && MekaToolModuleHelper.getEfficiency(stack) > 0
              && (available >= required
                    || required > 0 && available / required > 1.0E-5D);
    }

    @Override
    public float getDigSpeed(ItemStack stack, Block block, int metadata)
    {
        MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        float efficiency = MekaToolModuleHelper.getEfficiency(stack);
        if (efficiency <= 0)
        {
            return 0;
        }
        long required = getMiningEnergyCost(stack, 1F);
        double available = getEnergy(stack);
        return available >= required ? efficiency
              : (float)(MekaSuitConfig.toolEfficiency * available / required);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block,
          int x, int y, int z, EntityLivingBase entity)
    {
        if (!(entity instanceof EntityPlayer)
              || !((EntityPlayer)entity).capabilities.isCreativeMode)
        {
            setEnergy(stack, getEnergy(stack) - getMiningEnergyCost(stack,
                  block.getBlockHardness(world, x, y, z)));
        }
        return true;
    }

    public long getMiningEnergyCost(float hardness)
    {
        double scaled = MekaSuitConfig.toolMiningUsage
              * (double)MekaSuitConfig.toolEfficiency;
        long cost = scaled >= Long.MAX_VALUE ? Long.MAX_VALUE
              : Math.max(1L, (long)Math.ceil(scaled));
        return hardness == 0 ? Math.max(1L, cost / 2) : cost;
    }

    public long getMiningEnergyCost(ItemStack stack, float hardness)
    {
        return MekaToolModuleHelper.getMiningEnergyCost(stack, hardness);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z,
          EntityPlayer player)
    {
        MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        boolean sheared = MekaToolInteractionHelper.shearBlock(this, stack,
              x, y, z, player);
        if (!sheared)
        {
            MekaToolMiningHelper.mineArea(this, stack, x, y, z, player);
        }
        return sheared;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
          int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return MekaToolInteractionHelper.useFarming(this, stack, player, world,
              x, y, z, side);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack,
          EntityPlayer player, EntityLivingBase target)
    {
        return MekaToolInteractionHelper.shearEntity(this, stack, player,
              target);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world,
          EntityPlayer player)
    {
        MekaToolTeleportationHelper.use(this, stack, player, world);
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot,
          boolean selected)
    {
        super.onUpdate(stack, world, entity, slot, selected);
        if (!world.isRemote)
        {
            MekaToolModuleHelper.synchronizeHarvestEnchantments(stack);
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target,
          EntityLivingBase attacker)
    {
        if (!(attacker instanceof EntityPlayer)
              || !((EntityPlayer)attacker).capabilities.isCreativeMode)
        {
            int bonusDamage = MekaToolModuleHelper.getAttackDamage(stack);
            if (bonusDamage > 0)
            {
                setEnergy(stack, getEnergy(stack)
                      - MekaToolModuleHelper.getAttackEnergyCost(bonusDamage));
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Multimap getAttributeModifiers(ItemStack stack)
    {
        Multimap modifiers = HashMultimap.create(
              super.getAttributeModifiers(stack));
        modifiers.put(SharedMonsterAttributes.attackDamage
                    .getAttributeUnlocalizedName(),
              new AttributeModifier(field_111210_e, "Meka-Tool damage",
                    MekaToolModuleHelper.getEffectiveAttackDamage(stack), 0));
        return modifiers;
    }

    @Override
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(MekanismMekaSuit.MODID + ":"
              + REGISTRY_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip,
          boolean advanced)
    {
        super.addInformation(stack, player, tooltip, advanced);
        ModuleContainer modules = ModuleContainer.fromStack(stack,
              getModuleTarget());
        tooltip.add(EnumColor.AQUA
              + LangUtils.localize("tooltip.mekasuit.modules") + ": "
              + EnumColor.GREY + modules.getModules().size());
        if (advanced)
        {
            for (ModuleData module : modules.getModules())
            {
                String details = EnumColor.INDIGO + "- "
                      + LangUtils.localize(module.getType().getTranslationKey())
                      + " x" + module.getInstalledCount();
                if (!module.isEnabled())
                {
                    details += " [" + LangUtils.localize("gui.mekasuit.off")
                          + "]";
                }
                else if (module.getType().hasModes())
                {
                    details += " [" + LangUtils.localize("module.mode."
                          + module.getMode()) + "]";
                }
                tooltip.add(details);
            }
        }
    }
}
