package mekanism.mekasuit.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mekanism.api.EnumColor;
import mekanism.common.util.LangUtils;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/** Physical install item associated with one module definition. */
public final class ItemMekaModule extends ItemMekaSuit
{
    private static final Map<ResourceLocation, ItemMekaModule> MODULE_ITEMS =
          new LinkedHashMap<ResourceLocation, ItemMekaModule>();

    private final ModuleType moduleType;
    private final EnumRarity rarity;
    private final String textureDomain;

    public ItemMekaModule(ModuleType moduleType, EnumRarity rarity,
          String registryName, String translationKey)
    {
        this(moduleType, rarity, registryName, translationKey,
              MekanismMekaSuit.MODID);
    }

    public ItemMekaModule(ModuleType moduleType, EnumRarity rarity,
          String registryName, String translationKey, String textureDomain)
    {
        super(registryName, translationKey);
        if (moduleType == null)
        {
            throw new IllegalArgumentException("Module type is required");
        }
        this.moduleType = moduleType;
        this.rarity = rarity == null ? EnumRarity.common : rarity;
        this.textureDomain = textureDomain == null
              || textureDomain.isEmpty() ? MekanismMekaSuit.MODID
                    : textureDomain;
        ItemMekaModule previous = MODULE_ITEMS.get(moduleType.getId());
        if (previous != null && previous != this)
        {
            throw new IllegalArgumentException(
                  "Duplicate physical module item for " + moduleType.getId());
        }
        MODULE_ITEMS.put(moduleType.getId(), this);
    }

    public ModuleType getModuleType()
    {
        return moduleType;
    }

    public static ItemMekaModule getFor(ModuleType type)
    {
        return type == null ? null : MODULE_ITEMS.get(type.getId());
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return rarity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(textureDomain + ":"
              + getRegistryName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip,
          boolean advanced)
    {
        tooltip.add(EnumColor.GREY
              + LangUtils.localize(moduleType.getDescriptionKey()));
        tooltip.add(EnumColor.AQUA
              + LangUtils.localize("tooltip.mekasuit.maxInstalled") + ": "
              + EnumColor.GREY + moduleType.getMaxInstallCount());
        StringBuilder targets = new StringBuilder();
        for (ModuleTarget target : moduleType.getSupportedTargets())
        {
            if (targets.length() > 0)
            {
                targets.append(", ");
            }
            targets.append(LangUtils.localize("module.target."
                  + target.name().toLowerCase(Locale.ROOT)));
        }
        tooltip.add(EnumColor.AQUA
              + LangUtils.localize("tooltip.mekasuit.supportedGear") + ": "
              + EnumColor.GREY + targets);
        if (moduleType == MekaSuitModules.ELYTRA_UNIT
              && !EtFuturumElytraCompat.isAvailable())
        {
            tooltip.add(EnumColor.RED
                  + LangUtils.localize(
                        "tooltip.mekasuit.requiresEtFuturum"));
        }
        else if (moduleType == MekaSuitModules.FROST_WALKER_UNIT)
        {
            tooltip.add(EnumColor.RED
                  + LangUtils.localize("tooltip.mekasuit.unavailable1710"));
        }
    }
}
