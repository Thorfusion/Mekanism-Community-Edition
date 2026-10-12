package mekanism.ultimate.client.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import mekanism.ultimate.client.gui.GuiNutritionalLiquifier;
import mekanism.ultimate.client.gui.GuiPigmentMachine;
import mekanism.ultimate.client.nei.PigmentRecipeHandler.Mode;
import mekanism.ultimate.common.MekanismUltimate;

/** NEI entry point for Ultimate-owned recipes. */
public final class NEIMekanismUltimateConfig implements IConfigureNEI
{
    @Override
    public void loadConfig()
    {
        API.registerRecipeHandler(new NutritionalLiquifierRecipeHandler());
        API.registerUsageHandler(new NutritionalLiquifierRecipeHandler());
        API.setGuiOffset(GuiNutritionalLiquifier.class,
              NutritionalLiquifierRecipeHandler.X_OFFSET,
              NutritionalLiquifierRecipeHandler.Y_OFFSET);
        for (Mode mode : Mode.values())
        {
            API.registerRecipeHandler(new PigmentRecipeHandler(mode));
            API.registerUsageHandler(new PigmentRecipeHandler(mode));
        }
        API.setGuiOffset(GuiPigmentMachine.class,
              PigmentRecipeHandler.X_OFFSET,
              PigmentRecipeHandler.Y_OFFSET);
    }

    @Override
    public String getName()
    {
        return "Mekanism Ultimate NEI Plugin";
    }

    @Override
    public String getVersion()
    {
        return MekanismUltimate.versionNumber.toString();
    }
}
