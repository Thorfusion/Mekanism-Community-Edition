package mekanism.ultimate.common.recipe;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import mekanism.common.config.MekanismConfig;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class UltimateRecipeEnabledCondition implements IConditionFactory {

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        return () -> MekanismConfig.current().ultimate != null
              && MekanismConfig.current().ultimate.factoryEnabled.val()
              && MekanismConfig.current().ultimate.enableFactoryRecipes.val();
    }
}
