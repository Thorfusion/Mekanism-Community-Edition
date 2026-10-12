package mekanism.nuclear.client.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import mekanism.api.gas.GasStack;
import mekanism.client.nei.BaseRecipeHandler;
import mekanism.common.ObfuscatedNames;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.client.gui.GuiAntiprotonicNucleosynthesizer;
import mekanism.nuclear.common.recipe.NuclearRecipeRegistry;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

/** NEI recipe and usage view for Nuclear's item-plus-Antimatter recipes. */
public class NucleosynthesizingRecipeHandler extends BaseRecipeHandler
{
    public static final int X_OFFSET = 16;
    public static final int Y_OFFSET = 6;

    private int ticksPassed;

    @Override
    public String getRecipeName()
    {
        return LangUtils.localize(
              "nei.mekanismnuclear.nucleosynthesizing");
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "antiprotonicnucleosynthesizer";
    }

    @Override
    public String getGuiTexture()
    {
        return "mekanism:gui/GuiAdvancedMachine.png";
    }

    @Override
    public Class getGuiClass()
    {
        return GuiAntiprotonicNucleosynthesizer.class;
    }

    public String getRecipeId()
    {
        return "mekanismnuclear.nucleosynthesizing";
    }

    public Collection<ItemChemicalToItemRecipeCE> getRecipes()
    {
        return NuclearRecipeRegistry.NUCLEOSYNTHESIZING.getRecipes();
    }

    @Override
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(12, 0, 28, 5, 144, 68);
    }

    @Override
    public void drawExtras(int recipe)
    {
        CachedNucleosynthesizingRecipe cached =
              (CachedNucleosynthesizingRecipe)arecipes.get(recipe);
        int progress = Math.min(28, (ticksPassed % 41) * 28 / 40);
        if (progress > 0)
        {
            drawTexturedModalRect(63, 34, 176, 0, progress, 8);
        }
        displayGauge(45, 32, 6, 12, cached.gas);
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
              new Rectangle(63, 34, 28, 8), getRecipeId()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (getRecipeId().equals(outputId))
        {
            for (ItemChemicalToItemRecipeCE recipe : getRecipes())
            {
                arecipes.add(new CachedNucleosynthesizingRecipe(recipe));
            }
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        for (ItemChemicalToItemRecipeCE recipe : getRecipes())
        {
            if (NEIServerUtils.areStacksSameTypeCrafting(
                  recipe.getOutput(), result))
            {
                arecipes.add(new CachedNucleosynthesizingRecipe(recipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients)
    {
        if ("gas".equals(inputId) && ingredients.length == 1
              && ingredients[0] instanceof GasStack)
        {
            GasStack wanted = (GasStack)ingredients[0];
            for (ItemChemicalToItemRecipeCE recipe : getRecipes())
            {
                CachedNucleosynthesizingRecipe cached =
                      new CachedNucleosynthesizingRecipe(recipe);
                if (wanted.isGasEqual(cached.gas))
                {
                    arecipes.add(cached);
                }
            }
        }
        else
        {
            super.loadUsageRecipes(inputId, ingredients);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        for (ItemChemicalToItemRecipeCE recipe : getRecipes())
        {
            if (recipe.getItemInput().testType(ingredient))
            {
                arecipes.add(new CachedNucleosynthesizingRecipe(recipe));
            }
        }
    }

    @Override
    public List<String> handleTooltip(GuiRecipe gui,
          List<String> tooltip, int recipe)
    {
        GasStack gas = getGasUnderMouse(gui, recipe);
        if (gas != null)
        {
            tooltip.add(LangUtils.localizeGasStack(gas));
        }
        return super.handleTooltip(gui, tooltip, recipe);
    }

    @Override
    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode,
          int recipe)
    {
        GasStack gas = getGasUnderMouse(gui, recipe);
        if (gas != null)
        {
            if (keyCode == NEIClientConfig.getKeyBinding("gui.recipe")
                  && doGasLookup(gas, false))
            {
                return true;
            }
            if (keyCode == NEIClientConfig.getKeyBinding("gui.usage")
                  && doGasLookup(gas, true))
            {
                return true;
            }
        }
        return super.keyTyped(gui, keyChar, keyCode, recipe);
    }

    @Override
    public boolean mouseClicked(GuiRecipe gui, int button, int recipe)
    {
        GasStack gas = getGasUnderMouse(gui, recipe);
        if (gas != null)
        {
            if (button == 0 && doGasLookup(gas, false))
            {
                return true;
            }
            if (button == 1 && doGasLookup(gas, true))
            {
                return true;
            }
        }
        return super.mouseClicked(gui, button, recipe);
    }

    @Nullable
    private GasStack getGasUnderMouse(GuiRecipe gui, int recipe)
    {
        Point point = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipe);
        int x = point.x - (Integer)MekanismUtils.getPrivateValue(gui,
              GuiContainer.class, ObfuscatedNames.GuiContainer_guiLeft)
              - offset.x;
        int y = point.y - (Integer)MekanismUtils.getPrivateValue(gui,
              GuiContainer.class, ObfuscatedNames.GuiContainer_guiTop)
              - offset.y;
        return x >= 45 && x <= 51 && y >= 32 && y <= 44
              ? ((CachedNucleosynthesizingRecipe)arecipes.get(recipe)).gas
              : null;
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

    public class CachedNucleosynthesizingRecipe
          extends TemplateRecipeHandler.CachedRecipe
    {
        private final PositionedStack ingredient;
        private final PositionedStack result;
        public final GasStack gas;

        CachedNucleosynthesizingRecipe(
              ItemChemicalToItemRecipeCE recipe)
        {
            ingredient = new PositionedStack(recipe.getItemInput()
                  .getRepresentations().toArray(new ItemStack[0]), 40, 12);
            result = new PositionedStack(recipe.getOutput(), 100, 30);
            MekGasChemicalType type = (MekGasChemicalType)recipe
                  .getChemicalInput().getType();
            gas = new GasStack(type.getGas(), (int)Math.min(
                  Integer.MAX_VALUE,
                  recipe.getChemicalInput().getAmount()));
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
