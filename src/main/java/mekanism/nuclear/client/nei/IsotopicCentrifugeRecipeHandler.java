package mekanism.nuclear.client.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.List;
import mekanism.api.gas.GasStack;
import mekanism.client.nei.BaseRecipeHandler;
import mekanism.common.ObfuscatedNames;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.client.gui.GuiIsotopicCentrifuge;
import mekanism.nuclear.common.recipe.NuclearRecipeRegistry;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import mekanism.ultimate.common.recipe.type.ChemicalToChemicalRecipeCE;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.lwjgl.opengl.GL11;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

public class IsotopicCentrifugeRecipeHandler extends BaseRecipeHandler
{
    public static final int X_OFFSET = 5;
    public static final int Y_OFFSET = 12;

    @Override
    public String getRecipeName()
    {
        return LangUtils.localize("nei.mekanismnuclear.centrifuging");
    }

    @Override
    public String getOverlayIdentifier()
    {
        return "isotopiccentrifuge";
    }

    @Override
    public String getGuiTexture()
    {
        return "mekanism:gui/nei/GuiSolarNeutronActivator.png";
    }

    @Override
    public Class getGuiClass()
    {
        return GuiIsotopicCentrifuge.class;
    }

    public String getRecipeId()
    {
        return "mekanismnuclear.centrifuging";
    }

    public Collection<ChemicalToChemicalRecipeCE> getRecipes()
    {
        return NuclearRecipeRegistry.CENTRIFUGING.getRecipes();
    }

    @Override
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(-2, 0, 3, Y_OFFSET, 170, 70);
    }

    @Override
    public void drawExtras(int recipe)
    {
        CachedCentrifugingRecipe cached = (CachedCentrifugingRecipe)arecipes.get(recipe);
        drawTexturedModalRect(64 - X_OFFSET, 39 - Y_OFFSET, 176, 58, 55, 8);
        displayGauge(58, 26 - X_OFFSET, 14 - Y_OFFSET, 176, 0, 58,
              null, cached.input);
        displayGauge(58, 134 - X_OFFSET, 14 - Y_OFFSET, 176, 0, 58,
              null, cached.output);
    }

    @Override
    public void loadTransferRects()
    {
        transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(
              new Rectangle(64 - X_OFFSET, 39 - Y_OFFSET, 48, 8), getRecipeId()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (getRecipeId().equals(outputId))
        {
            for (ChemicalToChemicalRecipeCE recipe : getRecipes())
            {
                arecipes.add(new CachedCentrifugingRecipe(recipe));
            }
        }
        else if ("gas".equals(outputId) && results.length == 1
              && results[0] instanceof GasStack)
        {
            GasStack wanted = (GasStack)results[0];
            for (ChemicalToChemicalRecipeCE recipe : getRecipes())
            {
                CachedCentrifugingRecipe cached = new CachedCentrifugingRecipe(recipe);
                if (wanted.isGasEqual(cached.output))
                {
                    arecipes.add(cached);
                }
            }
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients)
    {
        if ("gas".equals(inputId) && ingredients.length == 1
              && ingredients[0] instanceof GasStack)
        {
            GasStack wanted = (GasStack)ingredients[0];
            for (ChemicalToChemicalRecipeCE recipe : getRecipes())
            {
                CachedCentrifugingRecipe cached = new CachedCentrifugingRecipe(recipe);
                if (wanted.isGasEqual(cached.input))
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
    public List<String> handleTooltip(GuiRecipe gui, List<String> tooltip, int recipe)
    {
        GasStack gas = getGasUnderMouse(gui, recipe);
        if (gas != null)
        {
            tooltip.add(LangUtils.localizeGasStack(gas));
        }
        return super.handleTooltip(gui, tooltip, recipe);
    }

    @Override
    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe)
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

    private GasStack getGasUnderMouse(GuiRecipe gui, int recipe)
    {
        Point point = GuiDraw.getMousePosition();
        int x = point.x - (Integer)MekanismUtils.getPrivateValue(
              gui, GuiContainer.class, ObfuscatedNames.GuiContainer_guiLeft);
        int y = point.y - (Integer)MekanismUtils.getPrivateValue(
              gui, GuiContainer.class, ObfuscatedNames.GuiContainer_guiTop);
        CachedCentrifugingRecipe cached = (CachedCentrifugingRecipe)arecipes.get(recipe);
        if (x >= 26 && x <= 42 && y >= 18 && y <= 76)
        {
            return cached.input;
        }
        if (x >= 134 && x <= 150 && y >= 18 && y <= 76)
        {
            return cached.output;
        }
        return null;
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

    public class CachedCentrifugingRecipe extends TemplateRecipeHandler.CachedRecipe
    {
        public final GasStack input;
        public final GasStack output;

        CachedCentrifugingRecipe(ChemicalToChemicalRecipeCE recipe)
        {
            MekGasChemicalType inputType = (MekGasChemicalType)recipe.getInput().getType();
            MekGasChemicalType outputType = (MekGasChemicalType)recipe.getOutput().getType();
            input = new GasStack(inputType.getGas(), (int)recipe.getInput().getAmount());
            output = new GasStack(outputType.getGas(), (int)recipe.getOutput().getAmount());
        }

        @Override
        public PositionedStack getResult()
        {
            return null;
        }
    }
}
