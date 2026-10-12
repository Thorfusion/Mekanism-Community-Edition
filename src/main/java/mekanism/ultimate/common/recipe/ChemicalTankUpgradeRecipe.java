package mekanism.ultimate.common.recipe;

import mekanism.ultimate.common.item.ItemBlockChemicalTank;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/** Tier recipe wrapper that carries the complete portable tank state forward. */
public final class ChemicalTankUpgradeRecipe implements IRecipe
{
    private final IRecipe shaped;

    public ChemicalTankUpgradeRecipe(IRecipe shaped)
    {
        if (shaped == null)
        {
            throw new IllegalArgumentException("Shaped recipe cannot be null");
        }
        this.shaped = shaped;
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world)
    {
        return shaped.matches(inventory, world);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory)
    {
        ItemStack result = shaped.getCraftingResult(inventory);
        if (result == null)
        {
            return null;
        }
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++)
        {
            ItemStack input = inventory.getStackInSlot(slot);
            if (input != null
                  && input.getItem() instanceof ItemBlockChemicalTank)
            {
                result.setTagCompound(input.hasTagCompound()
                      ? (net.minecraft.nbt.NBTTagCompound)input
                            .getTagCompound().copy() : null);
                break;
            }
        }
        return result;
    }

    @Override
    public int getRecipeSize()
    {
        return shaped.getRecipeSize();
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return shaped.getRecipeOutput();
    }
}
