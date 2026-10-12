package mekanism.ultimate.client.nei;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.ItemList;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import mekanism.client.nei.BaseRecipeHandler;
import mekanism.common.ObfuscatedNames;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.client.gui.GuiNutritionalLiquifier;
import mekanism.ultimate.common.UltimateFluids;
import mekanism.ultimate.common.nutrition.UltimateNutrition;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

/** Synthetic NEI recipes for every registered food item variant. */
public final class NutritionalLiquifierRecipeHandler
      extends BaseRecipeHandler
{
    public static final int X_OFFSET = 3;
    public static final int Y_OFFSET = 12;

    private int ticksPassed;

    @Override
    public String getRecipeName()
    {
        return LangUtils.localize(
              "nei.mekanismultimate.nutritionalLiquifying");
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "nutritionalliquifier";
    }

    @Override
    public String getGuiTexture()
    {
        return "mekanism:gui/GuiBlank.png";
    }

    @Override
    public Class getGuiClass()
    {
        return GuiNutritionalLiquifier.class;
    }

    public String getRecipeId()
    {
        return "mekanismultimate.nutritionalLiquifying";
    }

    @Override
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(-X_OFFSET, 0, 0, Y_OFFSET, 176, 70);
    }

    @Override
    public void drawExtras(int recipe)
    {
        CachedNutritionRecipe cached =
              (CachedNutritionRecipe)arecipes.get(recipe);
        int progress = Math.min(52, (ticksPassed % 41) * 52 / 40);
        if (progress > 0)
        {
            drawTexturedModalRect(50 - X_OFFSET, 39 - Y_OFFSET,
                  176, 58, progress, 8);
        }
        displayGauge(58, 78 - X_OFFSET, 13 - Y_OFFSET,
              176, 0, 58, cached.paste, null);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        ticksPassed++;
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(
              new Rectangle(50 - X_OFFSET, 39 - Y_OFFSET, 52, 10),
              getRecipeId()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (getRecipeId().equals(outputId))
        {
            addAllFoods();
        }
        else if (isFluidLookup(outputId) && results.length == 1
              && results[0] instanceof FluidStack
              && ((FluidStack)results[0]).getFluid()
                    == UltimateFluids.NutritionalPaste)
        {
            addAllFoods();
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (ItemStack food : getFoods())
        {
            ItemStack remainder = UltimateNutrition.getContainerItem(food);
            if (remainder != null
                  && NEIServerUtils.areStacksSameTypeCrafting(
                        remainder, result))
            {
                arecipes.add(new CachedNutritionRecipe(food));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        if (UltimateNutrition.getPasteOutput(ingredient) > 0)
        {
            arecipes.add(new CachedNutritionRecipe(ingredient));
        }
    }

    @Override
    public List<String> handleTooltip(GuiRecipe gui,
          List<String> tooltip, int recipe)
    {
        FluidStack fluid = getFluidUnderMouse(gui, recipe);
        if (fluid != null)
        {
            tooltip.add(fluid.getLocalizedName());
            tooltip.add(fluid.amount + " mB");
        }
        return super.handleTooltip(gui, tooltip, recipe);
    }

    @Override
    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode,
          int recipe)
    {
        FluidStack fluid = getFluidUnderMouse(gui, recipe);
        if (fluid != null)
        {
            if (keyCode == NEIClientConfig.getKeyBinding("gui.recipe")
                  && doFluidLookup(fluid, false))
            {
                return true;
            }
            if (keyCode == NEIClientConfig.getKeyBinding("gui.usage")
                  && doFluidLookup(fluid, true))
            {
                return true;
            }
        }
        return super.keyTyped(gui, keyChar, keyCode, recipe);
    }

    @Override
    public boolean mouseClicked(GuiRecipe gui, int button, int recipe)
    {
        FluidStack fluid = getFluidUnderMouse(gui, recipe);
        if (fluid != null)
        {
            if (button == 0 && doFluidLookup(fluid, false))
            {
                return true;
            }
            if (button == 1 && doFluidLookup(fluid, true))
            {
                return true;
            }
        }
        return super.mouseClicked(gui, button, recipe);
    }

    private FluidStack getFluidUnderMouse(GuiRecipe gui, int recipe)
    {
        Point point = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipe);
        int x = point.x - (Integer)MekanismUtils.getPrivateValue(gui,
              GuiContainer.class, ObfuscatedNames.GuiContainer_guiLeft)
              - offset.x;
        int y = point.y - (Integer)MekanismUtils.getPrivateValue(gui,
              GuiContainer.class, ObfuscatedNames.GuiContainer_guiTop)
              - offset.y;
        return x >= 78 - X_OFFSET && x <= 94 - X_OFFSET
              && y >= 13 - Y_OFFSET && y <= 71 - Y_OFFSET
              ? ((CachedNutritionRecipe)arecipes.get(recipe)).paste : null;
    }

    private void addAllFoods()
    {
        for (ItemStack food : getFoods())
        {
            arecipes.add(new CachedNutritionRecipe(food));
        }
    }

    private static List<ItemStack> getFoods()
    {
        List<ItemStack> foods = new ArrayList<ItemStack>();
        for (ItemStack stack : ItemList.items)
        {
            if (stack != null && UltimateNutrition.getPasteOutput(stack) > 0)
            {
                ItemStack food = stack.copy();
                food.stackSize = 1;
                foods.add(food);
            }
        }
        return foods;
    }

    @Override
    public int recipiesPerPage()
    {
        return 1;
    }

    @Override
    public void addGuiElements()
    {
    }

    public final class CachedNutritionRecipe
          extends TemplateRecipeHandler.CachedRecipe
    {
        private final PositionedStack ingredient;
        private final PositionedStack result;
        public final FluidStack paste;

        private CachedNutritionRecipe(ItemStack food)
        {
            ItemStack input = food.copy();
            input.stackSize = 1;
            ingredient = new PositionedStack(input,
                  27 - X_OFFSET, 35 - Y_OFFSET);
            ItemStack remainder = UltimateNutrition.getContainerItem(input);
            result = remainder == null ? null : new PositionedStack(
                  remainder, 108 - X_OFFSET, 35 - Y_OFFSET);
            paste = new FluidStack(UltimateFluids.NutritionalPaste,
                  UltimateNutrition.getPasteOutput(input));
        }

        @Override
        public PositionedStack getIngredient()
        {
            return ingredient;
        }

        @Override
        public PositionedStack getResult()
        {
            return result;
        }
    }
}
