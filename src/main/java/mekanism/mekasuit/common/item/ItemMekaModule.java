package mekanism.mekasuit.common.item;

import java.util.List;
import mekanism.api.EnumColor;
import mekanism.common.Mekanism;
import mekanism.common.util.LangUtils;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.api.gear.ModuleType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Physical install item associated with one module definition. */
public final class ItemMekaModule extends Item {

    private final ModuleType moduleType;
    private final EnumRarity rarity;

    public ItemMekaModule(ModuleType moduleType, EnumRarity rarity) {
        if (moduleType == null) {
            throw new IllegalArgumentException("Module type is required");
        }
        this.moduleType = moduleType;
        this.rarity = rarity == null ? EnumRarity.COMMON : rarity;
        setCreativeTab(Mekanism.tabMekanism);
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return rarity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(EnumColor.GREY + LangUtils.localize(moduleType.getDescriptionKey()));
        tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.mekasuit.maxInstalled") + ": "
              + EnumColor.GREY + moduleType.getMaxInstallCount());
        StringBuilder targets = new StringBuilder();
        for (ModuleTarget target : moduleType.getSupportedTargets()) {
            if (targets.length() > 0) {
                targets.append(", ");
            }
            targets.append(LangUtils.localize("module.target." + target.name().toLowerCase(java.util.Locale.ROOT)));
        }
        tooltip.add(EnumColor.AQUA + LangUtils.localize("tooltip.mekasuit.supportedGear") + ": "
              + EnumColor.GREY + targets);
    }
}
