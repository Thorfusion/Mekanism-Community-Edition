package mekanism.ultimate.client.jei;

import com.google.common.base.MoreObjects;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mekanism.ultimate.client.UltimateChemicalClientUtils;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mezz.jei.api.ingredients.IIngredientHelper;

public final class ChemicalStackHelper implements IIngredientHelper<ChemicalStackCE> {

    @Override
    public List<ChemicalStackCE> expandSubtypes(List<ChemicalStackCE> contained) {
        return contained;
    }

    @Nullable
    @Override
    public ChemicalStackCE getMatch(Iterable<ChemicalStackCE> ingredients, ChemicalStackCE toMatch) {
        for (ChemicalStackCE stack : ingredients) {
            if (LongChemicalTank.isSameType(stack.getType(), toMatch.getType())) {
                return stack;
            }
        }
        return null;
    }

    @Override
    public String getDisplayName(ChemicalStackCE ingredient) {
        return UltimateChemicalClientUtils.getDisplayName(ingredient.getType());
    }

    @Override
    public String getUniqueId(ChemicalStackCE ingredient) {
        return ingredient.getType().getKind().name().toLowerCase() + ":"
              + ingredient.getType().getRegistryName();
    }

    @Override
    public String getWildcardId(ChemicalStackCE ingredient) {
        return getUniqueId(ingredient);
    }

    @Override
    public String getModId(ChemicalStackCE ingredient) {
        return UltimateChemicalClientUtils.getModId(ingredient.getType());
    }

    @Override
    public Iterable<Color> getColors(ChemicalStackCE ingredient) {
        return Collections.singleton(new Color(
              UltimateChemicalClientUtils.getColor(ingredient.getType()), true));
    }

    @Override
    public String getResourceId(ChemicalStackCE ingredient) {
        return ingredient.getType().getRegistryName();
    }

    @Override
    public ChemicalStackCE copyIngredient(ChemicalStackCE ingredient) {
        return ingredient.copyWithAmount(ingredient.getAmount());
    }

    @Override
    public String getErrorInfo(@Nullable ChemicalStackCE ingredient) {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(ChemicalStackCE.class);
        if (ingredient != null) {
            helper.add("Kind", ingredient.getType().getKind());
            helper.add("Name", ingredient.getType().getRegistryName());
            helper.add("Amount", ingredient.getAmount());
        }
        return helper.toString();
    }
}
