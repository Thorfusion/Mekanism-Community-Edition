package mekanism.ultimate.client.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.GuiGauge;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.nei.BaseRecipeHandler;
import mekanism.common.ObfuscatedNames;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.common.UltimateBlocks;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.client.gui.GuiPigmentMachine;
import mekanism.ultimate.client.gui.element.GuiChemicalGauge;
import mekanism.ultimate.common.recipe.UltimatePigmentRecipes;
import mekanism.ultimate.common.recipe.type.ChemicalChemicalToChemicalRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemChemicalToItemRecipeCE;
import mekanism.ultimate.common.recipe.type.ItemToChemicalRecipeCE;
import mekanism.ultimate.common.tile.TileEntityPigmentMachine;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

/** NEI presentation and cross-recipe pigment lookup for all three machines. */
public final class PigmentRecipeHandler extends BaseRecipeHandler
{
    public static final int X_OFFSET = 3;
    public static final int Y_OFFSET = 4;
    public static final String CHEMICAL_LOOKUP_ID =
          "mekanismultimate.pigment";

    private static final ResourceLocation GUI_RESOURCE =
          MekanismUtils.getResource(ResourceType.GUI, "GuiBlank.png");

    private final Mode mode;
    private GuiChemicalGauge firstGauge;
    private GuiChemicalGauge secondGauge;
    private GuiChemicalGauge outputGauge;
    private int ticksPassed;

    public PigmentRecipeHandler(Mode mode)
    {
        this.mode = mode;
        createGuiElements();
    }

    @Override
    public void addGuiElements()
    {
        // BaseRecipeHandler invokes this before this constructor assigns mode.
    }

    private void createGuiElements()
    {
        if (mode == Mode.EXTRACTING)
        {
            guiElements.add(new GuiSlot(SlotType.INPUT, this,
                  GUI_RESOURCE, 26, 35));
            outputGauge = GuiChemicalGauge.getDummy(
                  GuiGauge.Type.STANDARD, this, GUI_RESOURCE, 131, 13);
            guiElements.add(outputGauge);
            guiElements.add(progress(ProgressBar.LARGE_RIGHT, 64, 40));
        }
        else if (mode == Mode.MIXING)
        {
            firstGauge = GuiChemicalGauge.getDummy(
                  GuiGauge.Type.STANDARD, this, GUI_RESOURCE, 25, 13);
            outputGauge = GuiChemicalGauge.getDummy(
                  GuiGauge.Type.STANDARD, this, GUI_RESOURCE, 79, 4);
            secondGauge = GuiChemicalGauge.getDummy(
                  GuiGauge.Type.STANDARD, this, GUI_RESOURCE, 133, 13);
            guiElements.add(firstGauge);
            guiElements.add(outputGauge);
            guiElements.add(secondGauge);
            guiElements.add(progress(ProgressBar.SMALL_RIGHT, 47, 39));
            guiElements.add(progress(ProgressBar.SMALL_LEFT, 101, 39));
        }
        else
        {
            firstGauge = GuiChemicalGauge.getDummy(
                  GuiGauge.Type.STANDARD, this, GUI_RESOURCE, 25, 13);
            guiElements.add(firstGauge);
            guiElements.add(new GuiSlot(SlotType.INPUT, this,
                  GUI_RESOURCE, 45, 35));
            guiElements.add(new GuiSlot(SlotType.OUTPUT, this,
                  GUI_RESOURCE, 116, 35));
            guiElements.add(progress(ProgressBar.LARGE_RIGHT, 64, 40));
        }
    }

    private GuiProgress progress(ProgressBar bar, int x, int y)
    {
        return new GuiProgress(new IProgressInfoHandler()
        {
            @Override
            public double getProgress()
            {
                return (ticksPassed % 20) / 20D;
            }
        }, bar, this, GUI_RESOURCE, x, y);
    }

    @Override
    public String getRecipeName()
    {
        return LangUtils.localize(mode.translationKey);
    }

    @Override
    public String getOverlayIdentifier()
    {
        return mode.overlay;
    }

    @Override
    public String getGuiTexture()
    {
        return "mekanism:gui/GuiBlank.png";
    }

    @Override
    public Class getGuiClass()
    {
        return GuiPigmentMachine.class;
    }

    public String getRecipeId()
    {
        return mode.recipeId;
    }

    @Override
    public void drawBackground(int recipe)
    {
        GL11.glColor4f(1, 1, 1, 1);
        changeTexture(getGuiTexture());
        drawTexturedModalRect(0, 0, X_OFFSET, Y_OFFSET, 170, 80);
        configureGauges((CachedPigmentRecipe)arecipes.get(recipe));
        for (GuiElement element : guiElements)
        {
            element.renderBackground(0, 0, -X_OFFSET, -Y_OFFSET);
        }
    }

    private void configureGauges(CachedPigmentRecipe recipe)
    {
        if (mode == Mode.EXTRACTING)
        {
            setGauge(outputGauge, recipe.outputChemicals[0]);
        }
        else if (mode == Mode.MIXING)
        {
            setGauge(firstGauge, recipe.inputChemicals[0]);
            setGauge(secondGauge, recipe.inputChemicals[1]);
            setGauge(outputGauge, recipe.outputChemicals[0]);
        }
        else
        {
            setGauge(firstGauge, recipe.inputChemicals[0]);
        }
    }

    private static void setGauge(GuiChemicalGauge gauge,
          ChemicalStackCE stack)
    {
        gauge.setDummy(stack, Math.max(1, stack.getAmount()));
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
              new Rectangle(47 - X_OFFSET, 39 - Y_OFFSET, 82, 10),
              getRecipeId()));
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results)
    {
        if (getRecipeId().equals(outputId))
        {
            addAllRecipes();
        }
        else if (CHEMICAL_LOOKUP_ID.equals(outputId)
              && results.length == 1
              && results[0] instanceof IChemicalStackCE)
        {
            addChemicalRecipes((IChemicalStackCE)results[0], false);
        }
        else
        {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients)
    {
        if (CHEMICAL_LOOKUP_ID.equals(inputId)
              && ingredients.length == 1
              && ingredients[0] instanceof IChemicalStackCE)
        {
            addChemicalRecipes((IChemicalStackCE)ingredients[0], true);
        }
        else
        {
            super.loadUsageRecipes(inputId, ingredients);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result)
    {
        if (mode == Mode.PAINTING)
        {
            for (ItemChemicalToItemRecipeCE recipe
                  : UltimatePigmentRecipes.PAINTING.getRecipes())
            {
                if (NEIServerUtils.areStacksSameTypeCrafting(
                      recipe.getOutput(), result))
                {
                    arecipes.add(new CachedPigmentRecipe(recipe));
                }
            }
        }
        super.loadCraftingRecipes(result);
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient)
    {
        if (isMachineBlock(ingredient))
        {
            addAllRecipes();
            return;
        }
        if (mode == Mode.EXTRACTING)
        {
            for (ItemToChemicalRecipeCE recipe
                  : UltimatePigmentRecipes.EXTRACTING.getRecipes())
            {
                if (recipe.getInput().testType(ingredient))
                {
                    arecipes.add(new CachedPigmentRecipe(recipe));
                }
            }
        }
        else if (mode == Mode.PAINTING)
        {
            for (ItemChemicalToItemRecipeCE recipe
                  : UltimatePigmentRecipes.PAINTING.getRecipes())
            {
                if (recipe.getItemInput().testType(ingredient))
                {
                    arecipes.add(new CachedPigmentRecipe(recipe));
                }
            }
        }
        super.loadUsageRecipes(ingredient);
    }

    private boolean isMachineBlock(ItemStack stack)
    {
        return stack != null && stack.getItem() == Item.getItemFromBlock(
              mode == Mode.EXTRACTING ? UltimateBlocks.PigmentExtractor
                    : mode == Mode.MIXING ? UltimateBlocks.PigmentMixer
                          : UltimateBlocks.PaintingMachine);
    }

    private void addAllRecipes()
    {
        if (mode == Mode.EXTRACTING)
        {
            for (ItemToChemicalRecipeCE recipe
                  : UltimatePigmentRecipes.EXTRACTING.getRecipes())
            {
                arecipes.add(new CachedPigmentRecipe(recipe));
            }
        }
        else if (mode == Mode.MIXING)
        {
            for (ChemicalChemicalToChemicalRecipeCE recipe
                  : UltimatePigmentRecipes.MIXING.getRecipes())
            {
                arecipes.add(new CachedPigmentRecipe(recipe));
            }
        }
        else
        {
            for (ItemChemicalToItemRecipeCE recipe
                  : UltimatePigmentRecipes.PAINTING.getRecipes())
            {
                arecipes.add(new CachedPigmentRecipe(recipe));
            }
        }
    }

    private void addChemicalRecipes(IChemicalStackCE chemical,
          boolean usage)
    {
        if (mode == Mode.EXTRACTING && !usage)
        {
            for (ItemToChemicalRecipeCE recipe
                  : UltimatePigmentRecipes.EXTRACTING.getRecipes())
            {
                if (sameType(recipe.getOutput(), chemical))
                {
                    arecipes.add(new CachedPigmentRecipe(recipe));
                }
            }
        }
        else if (mode == Mode.MIXING)
        {
            for (ChemicalChemicalToChemicalRecipeCE recipe
                  : UltimatePigmentRecipes.MIXING.getRecipes())
            {
                boolean matches = usage
                      ? sameType(recipe.getLeftInput().getType(), chemical)
                            || sameType(recipe.getRightInput().getType(),
                                  chemical)
                      : sameType(recipe.getOutput(), chemical);
                if (matches)
                {
                    arecipes.add(new CachedPigmentRecipe(recipe));
                }
            }
        }
        else if (mode == Mode.PAINTING && usage)
        {
            for (ItemChemicalToItemRecipeCE recipe
                  : UltimatePigmentRecipes.PAINTING.getRecipes())
            {
                if (sameType(recipe.getChemicalInput().getType(), chemical))
                {
                    arecipes.add(new CachedPigmentRecipe(recipe));
                }
            }
        }
    }

    private static boolean sameType(IChemicalStackCE first,
          IChemicalStackCE second)
    {
        return first != null && second != null
              && LongChemicalTank.isSameType(first.getType(),
                    second.getType());
    }

    private static boolean sameType(
          mekanism.ultimate.api.chemical.IChemicalTypeCE first,
          IChemicalStackCE second)
    {
        return first != null && second != null
              && LongChemicalTank.isSameType(first, second.getType());
    }

    @Override
    public List<String> handleTooltip(GuiRecipe gui,
          List<String> tooltip, int recipe)
    {
        ChemicalStackCE chemical = getChemicalUnderMouse(gui, recipe);
        if (chemical != null)
        {
            tooltip.add(TileEntityPigmentMachine.getChemicalName(
                  chemical.getType()));
            tooltip.add(String.format(LangUtils.localize(
                  "gui.chemical.amount"), chemical.getAmount()));
        }
        return super.handleTooltip(gui, tooltip, recipe);
    }

    @Override
    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode,
          int recipe)
    {
        ChemicalStackCE chemical = getChemicalUnderMouse(gui, recipe);
        if (chemical != null)
        {
            if (keyCode == NEIClientConfig.getKeyBinding("gui.recipe")
                  && doChemicalLookup(chemical, false))
            {
                return true;
            }
            if (keyCode == NEIClientConfig.getKeyBinding("gui.usage")
                  && doChemicalLookup(chemical, true))
            {
                return true;
            }
        }
        return super.keyTyped(gui, keyChar, keyCode, recipe);
    }

    @Override
    public boolean mouseClicked(GuiRecipe gui, int button, int recipe)
    {
        ChemicalStackCE chemical = getChemicalUnderMouse(gui, recipe);
        if (chemical != null)
        {
            if (button == 0 && doChemicalLookup(chemical, false))
            {
                return true;
            }
            if (button == 1 && doChemicalLookup(chemical, true))
            {
                return true;
            }
        }
        return super.mouseClicked(gui, button, recipe);
    }

    private boolean doChemicalLookup(ChemicalStackCE chemical,
          boolean usage)
    {
        return usage
              ? GuiUsageRecipe.openRecipeGui(CHEMICAL_LOOKUP_ID,
                    new Object[] {chemical})
              : GuiCraftingRecipe.openRecipeGui(CHEMICAL_LOOKUP_ID,
                    new Object[] {chemical});
    }

    private ChemicalStackCE getChemicalUnderMouse(GuiRecipe gui,
          int recipe)
    {
        Point point = GuiDraw.getMousePosition();
        Point offset = gui.getRecipePosition(recipe);
        int x = point.x - (Integer)MekanismUtils.getPrivateValue(gui,
              GuiContainer.class, ObfuscatedNames.GuiContainer_guiLeft)
              - offset.x;
        int y = point.y - (Integer)MekanismUtils.getPrivateValue(gui,
              GuiContainer.class, ObfuscatedNames.GuiContainer_guiTop)
              - offset.y;
        CachedPigmentRecipe cached =
              (CachedPigmentRecipe)arecipes.get(recipe);
        if (mode == Mode.EXTRACTING
              && inGauge(x, y, 131, 13))
        {
            return cached.outputChemicals[0];
        }
        if (mode == Mode.MIXING)
        {
            if (inGauge(x, y, 25, 13))
            {
                return cached.inputChemicals[0];
            }
            if (inGauge(x, y, 133, 13))
            {
                return cached.inputChemicals[1];
            }
            if (inGauge(x, y, 79, 4))
            {
                return cached.outputChemicals[0];
            }
        }
        return mode == Mode.PAINTING && inGauge(x, y, 25, 13)
              ? cached.inputChemicals[0] : null;
    }

    private static boolean inGauge(int x, int y, int gaugeX, int gaugeY)
    {
        int left = gaugeX + 1 - X_OFFSET;
        int top = gaugeY + 1 - Y_OFFSET;
        return x >= left && x < left + 16
              && y >= top && y < top + 58;
    }

    @Override
    public int recipiesPerPage()
    {
        return 1;
    }

    public enum Mode
    {
        EXTRACTING("tile.PigmentExtractor.name",
              "pigmentextractor",
              "mekanismultimate.pigmentextracting"),
        MIXING("tile.PigmentMixer.name", "pigmentmixer",
              "mekanismultimate.pigmentmixing"),
        PAINTING("tile.PaintingMachine.name", "paintingmachine",
              "mekanismultimate.painting");

        private final String translationKey;
        private final String overlay;
        private final String recipeId;

        Mode(String translationKey, String overlay, String recipeId)
        {
            this.translationKey = translationKey;
            this.overlay = overlay;
            this.recipeId = recipeId;
        }
    }

    public final class CachedPigmentRecipe
          extends TemplateRecipeHandler.CachedRecipe
    {
        private final PositionedStack ingredient;
        private final PositionedStack result;
        private final ChemicalStackCE[] inputChemicals;
        private final ChemicalStackCE[] outputChemicals;

        private CachedPigmentRecipe(ItemToChemicalRecipeCE recipe)
        {
            ingredient = positioned(recipe.getInput().getRepresentations(),
                  27 - X_OFFSET, 36 - Y_OFFSET);
            result = null;
            inputChemicals = new ChemicalStackCE[0];
            outputChemicals = new ChemicalStackCE[] {recipe.getOutput()};
        }

        private CachedPigmentRecipe(
              ChemicalChemicalToChemicalRecipeCE recipe)
        {
            ingredient = null;
            result = null;
            inputChemicals = new ChemicalStackCE[] {
                  new ChemicalStackCE(recipe.getLeftInput().getType(),
                        recipe.getLeftInput().getAmount()),
                  new ChemicalStackCE(recipe.getRightInput().getType(),
                        recipe.getRightInput().getAmount())
            };
            outputChemicals = new ChemicalStackCE[] {recipe.getOutput()};
        }

        private CachedPigmentRecipe(ItemChemicalToItemRecipeCE recipe)
        {
            ingredient = positioned(
                  recipe.getItemInput().getRepresentations(),
                  46 - X_OFFSET, 36 - Y_OFFSET);
            result = new PositionedStack(recipe.getOutput(),
                  117 - X_OFFSET, 36 - Y_OFFSET);
            inputChemicals = new ChemicalStackCE[] {
                  new ChemicalStackCE(recipe.getChemicalInput().getType(),
                        recipe.getChemicalInput().getAmount())
            };
            outputChemicals = new ChemicalStackCE[0];
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

    private static PositionedStack positioned(List<ItemStack> stacks,
          int x, int y)
    {
        return new PositionedStack(stacks.toArray(
              new ItemStack[stacks.size()]), x, y);
    }
}
