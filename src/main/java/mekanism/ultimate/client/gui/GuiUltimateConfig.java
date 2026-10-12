package mekanism.ultimate.client.gui;

import java.util.ArrayList;
import java.util.List;

import mekanism.common.util.LangUtils;
import mekanism.ultimate.common.MekanismUltimate;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;

public class GuiUltimateConfig extends GuiConfig
{
    public GuiUltimateConfig(GuiScreen parent)
    {
        super(parent, getConfigElements(), MekanismUltimate.MODID, false,
              false, "Mekanism Community Edition: Ultimate (Beta)");
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.add(new DummyCategoryElement(LangUtils.localize("mekanism.configgui.ctgy.ultimate.machines"), "mekanism.configgui.ctgy.ultimate.machines", MachinesEntry.class));
        list.add(new DummyCategoryElement(LangUtils.localize("mekanism.configgui.ctgy.ultimate.usage"), "mekanism.configgui.ctgy.ultimate.usage", UsageEntry.class));
        list.add(new DummyCategoryElement(LangUtils.localize("mekanism.configgui.ctgy.ultimate.recipes"), "mekanism.configgui.ctgy.ultimate.recipes", RecipesEntry.class));
        list.add(new DummyCategoryElement(LangUtils.localize("mekanism.configgui.ctgy.ultimate.nutrition"), "mekanism.configgui.ctgy.ultimate.nutrition", NutritionEntry.class));
        list.add(new DummyCategoryElement(LangUtils.localize("mekanism.configgui.ctgy.ultimate.chemical_tanks"), "mekanism.configgui.ctgy.ultimate.chemical_tanks", ChemicalTanksEntry.class));
        list.add(new DummyCategoryElement(LangUtils.localize("mekanism.configgui.ctgy.ultimate.armored_free_runners"), "mekanism.configgui.ctgy.ultimate.armored_free_runners", ArmoredFreeRunnersEntry.class));
        return list;
    }

    private static GuiScreen buildChildScreen(GuiConfig owningScreen, IConfigElement configElement, String category)
    {
        return new GuiConfig(owningScreen,
            new ConfigElement(MekanismUltimate.configuration.getCategory(category)).getChildElements(),
            owningScreen.modID, category, false,
            configElement.requiresMcRestart() || owningScreen.allRequireMcRestart,
            GuiConfig.getAbridgedConfigPath(MekanismUltimate.configuration.toString()));
    }

    public static class MachinesEntry extends CategoryEntry
    {
        public MachinesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
        {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiUltimateConfig.buildChildScreen(owningScreen, configElement, "machines");
        }
    }

    public static class UsageEntry extends CategoryEntry
    {
        public UsageEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
        {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiUltimateConfig.buildChildScreen(owningScreen, configElement, "usage");
        }
    }

    public static class RecipesEntry extends CategoryEntry
    {
        public RecipesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
        {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiUltimateConfig.buildChildScreen(owningScreen, configElement, "recipes");
        }
    }

    public static class NutritionEntry extends CategoryEntry
    {
        public NutritionEntry(GuiConfig owningScreen,
              GuiConfigEntries owningEntryList, IConfigElement prop)
        {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiUltimateConfig.buildChildScreen(owningScreen,
                  configElement, "nutrition");
        }
    }

    public static class ChemicalTanksEntry extends CategoryEntry
    {
        public ChemicalTanksEntry(GuiConfig owningScreen,
              GuiConfigEntries owningEntryList, IConfigElement prop)
        {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiUltimateConfig.buildChildScreen(owningScreen,
                  configElement, "chemical_tanks");
        }
    }

    public static class ArmoredFreeRunnersEntry extends CategoryEntry
    {
        public ArmoredFreeRunnersEntry(GuiConfig owningScreen,
              GuiConfigEntries owningEntryList, IConfigElement prop)
        {
            super(owningScreen, owningEntryList, prop);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiUltimateConfig.buildChildScreen(owningScreen,
                  configElement, "armored_free_runners");
        }
    }
}
