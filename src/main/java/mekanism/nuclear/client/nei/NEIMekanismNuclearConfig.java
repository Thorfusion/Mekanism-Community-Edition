package mekanism.nuclear.client.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import mekanism.nuclear.client.gui.GuiIsotopicCentrifuge;
import mekanism.nuclear.client.gui.GuiAntiprotonicNucleosynthesizer;
import mekanism.nuclear.common.MekanismNuclear;

public class NEIMekanismNuclearConfig implements IConfigureNEI
{
    @Override
    public void loadConfig()
    {
        API.registerRecipeHandler(new IsotopicCentrifugeRecipeHandler());
        API.registerUsageHandler(new IsotopicCentrifugeRecipeHandler());
        API.registerRecipeHandler(new NucleosynthesizingRecipeHandler());
        API.registerUsageHandler(new NucleosynthesizingRecipeHandler());
        API.setGuiOffset(GuiIsotopicCentrifuge.class,
              IsotopicCentrifugeRecipeHandler.X_OFFSET,
              IsotopicCentrifugeRecipeHandler.Y_OFFSET);
        API.setGuiOffset(GuiAntiprotonicNucleosynthesizer.class,
              NucleosynthesizingRecipeHandler.X_OFFSET,
              NucleosynthesizingRecipeHandler.Y_OFFSET);
    }

    @Override
    public String getName()
    {
        return "Mekanism Nuclear NEI Plugin";
    }

    @Override
    public String getVersion()
    {
        return MekanismNuclear.versionNumber.toString();
    }
}
