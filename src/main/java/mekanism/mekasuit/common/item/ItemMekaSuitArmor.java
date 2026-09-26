package mekanism.mekasuit.common.item;

import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.EnumColor;
import mekanism.api.energy.IEnergizedItem;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.integration.forgeenergy.ForgeEnergyItemWrapper;
import mekanism.common.integration.ic2.IC2ItemManager;
import mekanism.common.integration.redstoneflux.RFIntegration;
import mekanism.common.integration.tesla.TeslaItemWrapper;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.MekaSuitBreathingHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitEnergyHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitInhalationHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitVisionHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitNutritionalHelper;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Energy-backed MekaSuit armor with stable base capacity and armor values. */
@InterfaceList({
      @Interface(iface = "ic2.api.item.ISpecialElectricItem", modid = MekanismHooks.IC2_MOD_ID),
      @Interface(iface = "cofh.redstoneflux.api.IEnergyContainerItem", modid = MekanismHooks.REDSTONEFLUX_MOD_ID)
})
public final class ItemMekaSuitArmor extends ItemArmor implements IEnergizedItem, ISpecialElectricItem,
      IEnergyContainerItem, IModuleContainerItem {

    private static final ArmorMaterial MEKASUIT_MATERIAL = EnumHelper.addArmorMaterial(
          "MEKANISM_MEKASUIT", MekanismMekaSuit.MODID + ":mekasuit", 0,
          new int[]{3, 6, 8, 3}, 0, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 3F);

    private final ModuleTarget moduleTarget;

    public ItemMekaSuitArmor(EntityEquipmentSlot slot) {
        super(MEKASUIT_MATERIAL, renderIndex(slot), slot);
        moduleTarget = target(slot);
        setMaxStackSize(1);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    public ModuleTarget getModuleTarget() {
        return moduleTarget;
    }

    @Override
    public double getEnergy(ItemStack stack) {
        return ItemDataUtils.getDouble(stack, "energyStored");
    }

    @Override
    public void setEnergy(ItemStack stack, double amount) {
        ItemDataUtils.setDouble(stack, "energyStored", Math.max(0, Math.min(amount, getMaxEnergy(stack))));
    }

    @Override
    public double getMaxEnergy(ItemStack stack) {
        return MekaSuitEnergyHelper.getScaledValue(stack, moduleTarget, MekaSuitConfig.suitCapacity);
    }

    @Override
    public double getMaxTransfer(ItemStack stack) {
        return MekaSuitEnergyHelper.getScaledValue(stack, moduleTarget, MekaSuitConfig.suitChargeRate);
    }

    @Override
    public boolean canReceive(ItemStack stack) {
        return getEnergy(stack) < getMaxEnergy(stack);
    }

    @Override
    public boolean canSend(ItemStack stack) {
        return false;
    }

    @Override
    @Method(modid = MekanismHooks.REDSTONEFLUX_MOD_ID)
    public int receiveEnergy(ItemStack stack, int energy, boolean simulate) {
        if (!canReceive(stack)) {
            return 0;
        }
        double accepted = Math.min(RFIntegration.fromRF(energy), getMaxEnergy(stack) - getEnergy(stack));
        if (!simulate) {
            setEnergy(stack, getEnergy(stack) + accepted);
        }
        return RFIntegration.toRF(accepted);
    }

    @Override
    @Method(modid = MekanismHooks.REDSTONEFLUX_MOD_ID)
    public int extractEnergy(ItemStack stack, int energy, boolean simulate) {
        return 0;
    }

    @Override
    @Method(modid = MekanismHooks.REDSTONEFLUX_MOD_ID)
    public int getEnergyStored(ItemStack stack) {
        return RFIntegration.toRF(getEnergy(stack));
    }

    @Override
    @Method(modid = MekanismHooks.REDSTONEFLUX_MOD_ID)
    public int getMaxEnergyStored(ItemStack stack) {
        return RFIntegration.toRF(getMaxEnergy(stack));
    }

    @Override
    @Method(modid = MekanismHooks.IC2_MOD_ID)
    public IElectricItemManager getManager(ItemStack stack) {
        return IC2ItemManager.getManager(this);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new ItemCapabilityWrapper(stack, new TeslaItemWrapper(), new ForgeEnergyItemWrapper(),
              new MekaSuitNutritionalHelper.NutritionalFluidCapability());
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }
        items.add(new ItemStack(this));
        ItemStack charged = new ItemStack(this);
        setEnergy(charged, getMaxEnergy(charged));
        items.add(charged);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1D - getEnergy(stack) / getMaxEnergy(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(@Nonnull ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0F, (float) (1D - getDurabilityForDisplay(stack))) / 3F, 1F, 1F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.storedEnergy") + ": " + EnumColor.GREY
              + MekanismUtils.getEnergyDisplay(getEnergy(stack), getMaxEnergy(stack)));
        ModuleContainer modules = ModuleContainer.fromStack(stack, moduleTarget);
        tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.mekasuit.modules") + ": "
              + EnumColor.GREY + modules.getModules().size());
        if (flag.isAdvanced()) {
            for (ModuleData module : modules.getModules()) {
                tooltip.add(EnumColor.INDIGO + "- " + LangUtils.localize(module.getType().getTranslationKey())
                      + " x" + module.getInstalledCount());
            }
        }
        if (moduleTarget == ModuleTarget.HELMET && MekaSuitNutritionalHelper.supportsStorage(stack)) {
            tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.nutritionalPaste") + ": " + EnumColor.GREY
                  + MekaSuitNutritionalHelper.getStored(stack) + " / "
                  + MekaSuitNutritionalHelper.getCapacity() + " mB");
        }
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        int layer = slot == EntityEquipmentSlot.LEGS ? 2 : 1;
        return MekanismMekaSuit.MODID + ":textures/models/armor/mekasuit_layer_" + layer + ".png";
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (moduleTarget == ModuleTarget.HELMET) {
            if (!world.isRemote) {
                MekaSuitBreathingHelper.tick(stack, player);
                MekaSuitVisionHelper.tickServer(stack, player);
                MekaSuitNutritionalHelper.tick(stack, player);
            }
            MekaSuitInhalationHelper.tick(stack, player, !world.isRemote);
        }
    }

    private static int renderIndex(EntityEquipmentSlot slot) {
        switch (slot) {
            case HEAD:
                return 0;
            case CHEST:
                return 1;
            case LEGS:
                return 2;
            case FEET:
                return 3;
            default:
                throw new IllegalArgumentException("Unsupported MekaSuit slot: " + slot);
        }
    }

    private static ModuleTarget target(EntityEquipmentSlot slot) {
        switch (slot) {
            case HEAD:
                return ModuleTarget.HELMET;
            case CHEST:
                return ModuleTarget.BODYARMOR;
            case LEGS:
                return ModuleTarget.PANTS;
            case FEET:
                return ModuleTarget.BOOTS;
            default:
                throw new IllegalArgumentException("Unsupported MekaSuit slot: " + slot);
        }
    }
}
