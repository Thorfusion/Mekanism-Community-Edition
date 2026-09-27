package mekanism.ultimate.client.jei;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import mekanism.common.util.LangUtils;
import mekanism.ultimate.client.UltimateChemicalClientUtils;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;

/** Lightweight colored chemical renderer for JEI's ingredient list and gauges. */
public final class ChemicalStackRenderer implements IIngredientRenderer<ChemicalStackCE> {

    private final long capacity;
    private final boolean showAmount;
    private final int width;
    private final int height;
    @Nullable
    private final IDrawable overlay;

    public ChemicalStackRenderer() {
        this(1_000, false, 16, 16, null);
    }

    public ChemicalStackRenderer(long capacity, boolean showAmount, int width, int height,
          @Nullable IDrawable overlay) {
        this.capacity = Math.max(1, capacity);
        this.showAmount = showAmount;
        this.width = width;
        this.height = height;
        this.overlay = overlay;
    }

    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition,
          @Nullable ChemicalStackCE stack) {
        if (stack != null && stack.getAmount() > 0) {
            int scaled = Math.max(1, (int) Math.min(height,
                  Math.ceil((double) stack.getAmount() * height / capacity)));
            Gui.drawRect(xPosition, yPosition + height - scaled, xPosition + width,
                  yPosition + height, UltimateChemicalClientUtils.getColor(stack.getType()));
        }
        if (overlay != null) {
            overlay.draw(minecraft, xPosition, yPosition);
        }
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, ChemicalStackCE stack,
          ITooltipFlag tooltipFlag) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(UltimateChemicalClientUtils.getDisplayName(stack.getType()));
        if (showAmount) {
            tooltip.add(TextFormatting.GRAY + LangUtils.localizeWithFormat(
                  "gui.chemical.amount", stack.getAmount()));
        }
        return tooltip;
    }

    @Override
    public FontRenderer getFontRenderer(Minecraft minecraft, ChemicalStackCE stack) {
        return minecraft.fontRenderer;
    }
}
