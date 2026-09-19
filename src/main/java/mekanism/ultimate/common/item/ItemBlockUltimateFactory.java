package mekanism.ultimate.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.EnumColor;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.ItemBlockMachine;
import mekanism.common.tier.BaseTier;
import mekanism.common.tier.FactoryTier;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockUltimateFactory extends ItemBlockMachine {

    public ItemBlockUltimateFactory(Block block) {
        super(block);
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        return "tile.UltimateFactory";
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        RecipeType recipe = getRecipeTypeOrNull(stack);
        if (recipe == null) {
            recipe = RecipeType.SMELTING;
        }
        return BaseTier.ULTIMATE.getLocalizedName() + " " + recipe.getLocalizedName() + " " + LangUtils.localize("tile.MachineBlock.Factory.name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, World world, @Nonnull List<String> list, @Nonnull ITooltipFlag flag) {
        if (!MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.sneakKey)) {
            list.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.INDIGO + GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode())
                  + EnumColor.GREY + " " + LangUtils.localize("tooltip.forDetails") + ".");
            return;
        }
        RecipeType recipe = getRecipeTypeOrNull(stack);
        if (recipe != null) {
            list.add(EnumColor.INDIGO + LangUtils.localize("tooltip.recipeType") + ": " + EnumColor.GREY + recipe.getLocalizedName());
        }
        list.add(EnumColor.BRIGHT_GREEN + LangUtils.localize("tooltip.storedEnergy") + ": " + EnumColor.GREY
              + MekanismUtils.getEnergyDisplay(getEnergy(stack), getMaxEnergy(stack)));
    }

    @Override
    public double getEnergy(ItemStack stack) {
        return ItemDataUtils.getDouble(stack, "energyStored");
    }

    @Override
    public void setEnergy(ItemStack stack, double amount) {
        ItemDataUtils.setDouble(stack, "energyStored", Math.max(Math.min(amount, getMaxEnergy(stack)), 0));
    }

    @Override
    public double getMaxEnergy(ItemStack stack) {
        RecipeType recipe = getRecipeTypeOrNull(stack);
        if (recipe == null) {
            recipe = RecipeType.SMELTING;
        }
        double usage = recipe.getEnergyUsage();
        int storageTicks = 400;
        if (MekanismConfig.current().ultimate != null) {
            usage *= MekanismConfig.current().ultimate.factoryUsageMultiplier.val();
            storageTicks = MekanismConfig.current().ultimate.factoryEnergyStorageTicks.val();
        }
        return MekanismUtils.getMaxEnergy(stack, usage * FactoryTier.ULTIMATE.processes * storageTicks);
    }

    @Override
    public boolean canReceive(ItemStack stack) {
        return true;
    }

    @Override
    public boolean hasTank(Object... data) {
        return false;
    }

    @Override
    public BaseTier getBaseTier(ItemStack stack) {
        return BaseTier.ULTIMATE;
    }

    @Override
    public void setBaseTier(ItemStack stack, BaseTier tier) {
    }
}
