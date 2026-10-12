package mekanism.mekasuit.common.item;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.Optional.Interface;
import cpw.mods.fml.common.Optional.InterfaceList;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import java.util.List;
import mekanism.api.EnumColor;
import mekanism.api.MekanismConfig.general;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.lasers.ILaserDissipation;
import mekanism.common.Mekanism;
import mekanism.common.integration.IC2ItemManager;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.client.ModelMekaSuitArmor;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.MekaSuitEnergyHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitBreathingHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitInhalationHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.MekaSuitMobilityHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitNutritionalHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitProtectionHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitVisionHelper;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

/** Energy-backed MekaSuit armor and module host. */
@InterfaceList({
      @Interface(iface = "ic2.api.item.ISpecialElectricItem", modid = "IC2")
})
public class ItemMekaSuitArmor extends ItemArmor implements IEnergizedItem,
      ISpecialElectricItem, IEnergyContainerItem, IModuleContainerItem,
      IMekaSuitItem, ILaserDissipation, IFluidContainerItem
{
    private static final ArmorMaterial MEKASUIT_MATERIAL =
          EnumHelper.addArmorMaterial("MEKANISM_MEKASUIT", 0,
                new int[] {3, 6, 8, 3}, 0);

    private final String registryName;
    private final ModuleTarget moduleTarget;
    @SideOnly(Side.CLIENT)
    private IIcon tintIcon;

    public ItemMekaSuitArmor(String registryName, String translationKey,
          int armorType)
    {
        super(MEKASUIT_MATERIAL, 0, armorType);
        this.registryName = registryName;
        moduleTarget = target(armorType);
        setUnlocalizedName(translationKey);
        setMaxStackSize(1);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public ModuleTarget getModuleTarget()
    {
        return moduleTarget;
    }

    public float getDamageAbsorptionRatio()
    {
        switch (moduleTarget)
        {
            case HELMET:
                return 0.15F;
            case BODYARMOR:
                return 0.40F;
            case PANTS:
                return 0.30F;
            case BOOTS:
                return 0.15F;
            default:
                return 0;
        }
    }

    public boolean hasEnabledModule(ItemStack stack,
          mekanism.mekasuit.api.gear.ModuleType type)
    {
        return ModuleContainer.fromStack(stack, moduleTarget).hasEnabled(type);
    }

    @Override
    public double getDissipationPercent(ItemStack stack)
    {
        return MekaSuitProtectionHelper.getLaserDissipation(moduleTarget,
              hasEnabledModule(stack, MekaSuitModules.LASER_DISSIPATION_UNIT));
    }

    @Override
    public double getRefractionPercent(ItemStack stack)
    {
        return MekaSuitProtectionHelper.getLaserRefraction(moduleTarget,
              hasEnabledModule(stack, MekaSuitModules.LASER_DISSIPATION_UNIT));
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity)
    {
        return armorType == this.armorType;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot,
          String type)
    {
        int layer = slot == 2 ? 2 : 1;
        return MekanismMekaSuit.MODID
              + ":textures/models/armor/mekasuit_layer_" + layer + ".png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving,
          ItemStack stack, int armorSlot)
    {
        return ModelMekaSuitArmor.get(armorType, stack, entityLiving);
    }

    @Override
    public double getEnergy(ItemStack stack)
    {
        return stack.stackTagCompound == null ? 0
              : stack.stackTagCompound.getDouble("energyStored");
    }

    @Override
    public void setEnergy(ItemStack stack, double amount)
    {
        if (stack.stackTagCompound == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.stackTagCompound.setDouble("energyStored",
              Math.max(0, Math.min(amount, getMaxEnergy(stack))));
    }

    @Override
    public double getMaxEnergy(ItemStack stack)
    {
        return MekaSuitEnergyHelper.getScaledValue(stack, moduleTarget,
              MekaSuitConfig.suitCapacity);
    }

    @Override
    public double getMaxTransfer(ItemStack stack)
    {
        return MekaSuitEnergyHelper.getScaledValue(stack, moduleTarget,
              MekaSuitConfig.suitChargeRate);
    }

    @Override
    public boolean canReceive(ItemStack stack)
    {
        return getEnergy(stack) < getMaxEnergy(stack);
    }

    @Override
    public boolean canSend(ItemStack stack)
    {
        return false;
    }

    @Override
    public int receiveEnergy(ItemStack stack, int energy, boolean simulate)
    {
        if (!canReceive(stack))
        {
            return 0;
        }
        double accepted = Math.min(energy * general.FROM_TE,
              getMaxEnergy(stack) - getEnergy(stack));
        if (!simulate)
        {
            setEnergy(stack, getEnergy(stack) + accepted);
        }
        return (int)Math.round(accepted * general.TO_TE);
    }

    @Override
    public int extractEnergy(ItemStack stack, int energy, boolean simulate)
    {
        return 0;
    }

    @Override
    public int getEnergyStored(ItemStack stack)
    {
        return (int)Math.round(getEnergy(stack) * general.TO_TE);
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack)
    {
        return (int)Math.round(getMaxEnergy(stack) * general.TO_TE);
    }

    @Override
    public FluidStack getFluid(ItemStack stack)
    {
        return MekaSuitNutritionalHelper.supportsStorage(stack)
              ? MekaSuitNutritionalHelper.getFluid(stack) : null;
    }

    @Override
    public int getCapacity(ItemStack stack)
    {
        return MekaSuitNutritionalHelper.supportsStorage(stack)
              ? MekaSuitNutritionalHelper.getCapacity() : 0;
    }

    @Override
    public int fill(ItemStack stack, FluidStack resource, boolean doFill)
    {
        return MekaSuitNutritionalHelper.fill(stack, resource, doFill);
    }

    @Override
    public FluidStack drain(ItemStack stack, int maxDrain,
          boolean doDrain)
    {
        return null;
    }

    @Override
    @Method(modid = "IC2")
    public boolean canProvideEnergy(ItemStack stack)
    {
        return false;
    }

    @Override
    @Method(modid = "IC2")
    public Item getChargedItem(ItemStack stack)
    {
        return this;
    }

    @Override
    @Method(modid = "IC2")
    public Item getEmptyItem(ItemStack stack)
    {
        return this;
    }

    @Override
    @Method(modid = "IC2")
    public double getMaxCharge(ItemStack stack)
    {
        return 0;
    }

    @Override
    @Method(modid = "IC2")
    public int getTier(ItemStack stack)
    {
        return 4;
    }

    @Override
    @Method(modid = "IC2")
    public double getTransferLimit(ItemStack stack)
    {
        return 0;
    }

    @Override
    @Method(modid = "IC2")
    public IElectricItemManager getManager(ItemStack stack)
    {
        return IC2ItemManager.getManager(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List items)
    {
        ItemStack empty = new ItemStack(this);
        items.add(empty);
        ItemStack charged = new ItemStack(this);
        setEnergy(charged, getMaxEnergy(charged));
        items.add(charged);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1D - getEnergy(stack) / getMaxEnergy(stack);
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
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return false;
    }

    @Override
    public void onArmorTick(net.minecraft.world.World world,
          EntityPlayer player, ItemStack stack)
    {
        if (moduleTarget == ModuleTarget.HELMET)
        {
            if (!world.isRemote)
            {
                MekaSuitBreathingHelper.tick(stack, player);
                MekaSuitVisionHelper.tickServer(stack, player);
                MekaSuitNutritionalHelper.tick(stack, player);
            }
            MekaSuitInhalationHelper.tick(stack, player, !world.isRemote);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(MekanismMekaSuit.MODID + ":"
              + registryName);
        tintIcon = register.registerIcon(MekanismMekaSuit.MODID + ":"
              + registryName + "_tint_layer");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return pass == 1 ? tintIcon : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass)
    {
        return pass == 1 ? tintIcon : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
        return pass == 1 ? MekaSuitMobilityHelper.getColor(stack) : 0xFFFFFF;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip,
          boolean advanced)
    {
        tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.storedEnergy")
              + ": " + EnumColor.GREY
              + MekanismUtils.getEnergyDisplay(getEnergy(stack)));
        ModuleContainer modules = ModuleContainer.fromStack(stack, moduleTarget);
        if (MekaSuitNutritionalHelper.supportsStorage(stack))
        {
            tooltip.add(EnumColor.AQUA
                  + LangUtils.localize("tooltip.nutritionalPaste") + ": "
                  + EnumColor.GREY
                  + MekaSuitNutritionalHelper.getStored(stack) + " / "
                  + MekaSuitNutritionalHelper.getCapacity() + " mB");
        }
        tooltip.add(EnumColor.AQUA
              + LangUtils.localize("tooltip.mekasuit.modules") + ": "
              + EnumColor.GREY + modules.getModules().size());
        if (advanced)
        {
            for (ModuleData module : modules.getModules())
            {
                tooltip.add(EnumColor.INDIGO + "- "
                      + LangUtils.localize(module.getType().getTranslationKey())
                      + " x" + module.getInstalledCount());
            }
        }
    }

    private static ModuleTarget target(int armorType)
    {
        switch (armorType)
        {
            case 0:
                return ModuleTarget.HELMET;
            case 1:
                return ModuleTarget.BODYARMOR;
            case 2:
                return ModuleTarget.PANTS;
            case 3:
                return ModuleTarget.BOOTS;
            default:
                throw new IllegalArgumentException(
                      "Unsupported MekaSuit armor slot: " + armorType);
        }
    }
}
