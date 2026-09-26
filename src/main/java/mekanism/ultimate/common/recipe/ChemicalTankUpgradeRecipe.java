package mekanism.ultimate.common.recipe;

import com.google.gson.JsonObject;
import javax.annotation.Nonnull;
import mekanism.common.recipe.ShapedMekanismRecipe;
import mekanism.ultimate.common.item.ItemBlockChemicalTank;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

/** Stable mek-data-style tier recipe that carries all portable tank state forward. */
public final class ChemicalTankUpgradeRecipe extends IForgeRegistryEntry.Impl<IRecipe>
      implements IRecipe {

    private final IRecipe shaped;

    private ChemicalTankUpgradeRecipe(IRecipe shaped) {
        this.shaped = shaped;
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inventory, @Nonnull World world) {
        return shaped.matches(inventory, world);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inventory) {
        ItemStack result = shaped.getCraftingResult(inventory);
        if (result.isEmpty()) {
            return result;
        }
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack input = inventory.getStackInSlot(slot);
            if (!input.isEmpty() && input.getItem() instanceof ItemBlockChemicalTank) {
                result.setTagCompound(input.hasTagCompound() ? input.getTagCompound().copy() : null);
                break;
            }
        }
        return result;
    }

    @Override
    public boolean canFit(int width, int height) {
        return shaped.canFit(width, height);
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return shaped.getRecipeOutput();
    }

    @Nonnull
    @Override
    public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inventory) {
        return shaped.getRemainingItems(inventory);
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return shaped.getIngredients();
    }

    @Override
    public String getGroup() {
        return shaped.getGroup();
    }

    public static final class Factory implements IRecipeFactory {

        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            return new ChemicalTankUpgradeRecipe(ShapedMekanismRecipe.factory(context, json));
        }
    }
}
