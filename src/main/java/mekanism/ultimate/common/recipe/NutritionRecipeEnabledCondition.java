package mekanism.ultimate.common.recipe;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import mekanism.ultimate.common.config.UltimateNutritionConfig;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public final class NutritionRecipeEnabledCondition implements IConditionFactory {

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        return () -> UltimateNutritionConfig.nutritionalLiquifierEnabled;
    }
}
