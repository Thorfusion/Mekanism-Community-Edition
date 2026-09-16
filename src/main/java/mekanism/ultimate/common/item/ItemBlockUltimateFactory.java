package mekanism.ultimate.common.item;

import java.util.List;

import mekanism.api.EnumColor;
import mekanism.api.MekanismConfig.usage;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.item.ItemBlockMachine;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockUltimateFactory extends ItemBlockMachine
{
    public ItemBlockUltimateFactory(Block block)
    {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return "tile.UltimateFactory";
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        RecipeType recipe = RecipeType.values()[getRecipeType(stack)];
        String key = "tile.Ultimate" + recipe.getUnlocalizedName() + "Factory";
        if (StatCollector.canTranslate(key))
        {
            return LangUtils.localize(key);
        }
        return BaseTier.ULTIMATE.getLocalizedName() + " " + recipe.getLocalizedName() + " " + LangUtils.localize("tile.MachineBlock.Factory.name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced)
    {
        if (MekanismKeyHandler.getIsKeyPressed(MekanismKeyHandler.sneakKey) && MekanismKeyHandler.getIsKeyPressed(MekanismKeyHandler.modeSwitchKey))
        {
            list.addAll(MekanismUtils.splitTooltip(LangUtils.localize("tooltip.Factory"), stack));
            return;
        }

        super.addInformation(stack, player, list, advanced);
        if (MekanismKeyHandler.getIsKeyPressed(MekanismKeyHandler.sneakKey))
        {
            list.add(EnumColor.INDIGO + LangUtils.localize("tooltip.recipeType") + ": " + EnumColor.GREY + RecipeType.values()[getRecipeType(stack)].getLocalizedName());
        }
    }

    @Override
    public double getMaxEnergy(ItemStack stack)
    {
        return MekanismUtils.getMaxEnergy(stack, usage.factoryUsage * FactoryTier.ULTIMATE.processes * 400);
    }

    @Override
    public BaseTier getBaseTier(ItemStack stack)
    {
        return BaseTier.ULTIMATE;
    }

    @Override
    public void setBaseTier(ItemStack stack, BaseTier tier)
    {
    }
}
