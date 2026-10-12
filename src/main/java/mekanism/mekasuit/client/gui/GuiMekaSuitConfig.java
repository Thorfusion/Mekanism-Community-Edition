package mekanism.mekasuit.client.gui;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import mekanism.common.util.LangUtils;
import mekanism.mekasuit.common.MekanismMekaSuit;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

@SideOnly(Side.CLIENT)
public final class GuiMekaSuitConfig extends GuiConfig
{
    public GuiMekaSuitConfig(GuiScreen parent)
    {
        super(parent, getConfigElements(), MekanismMekaSuit.MODID, false,
              false, "Mekanism Community Edition: MekaSuit (Beta)");
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.add(category("meka_tool", MekaToolEntry.class));
        list.add(category("mekasuit", MekaSuitEntry.class));
        list.add(category("modification_station",
              ModificationStationEntry.class));
        list.add(category("generators", GeneratorsEntry.class));
        return list;
    }

    private static DummyCategoryElement category(String key,
          Class<? extends CategoryEntry> entryClass)
    {
        String langKey = MekanismMekaSuit.MODID + ".configgui.ctgy." + key;
        return new DummyCategoryElement(LangUtils.localize(langKey),
              langKey, entryClass);
    }

    private static GuiScreen buildChildScreen(GuiConfig owningScreen,
          IConfigElement configElement, String category)
    {
        return new GuiConfig(owningScreen, new ConfigElement(
              MekanismMekaSuit.configuration.getCategory(category))
                    .getChildElements(), owningScreen.modID, category, false,
              configElement.requiresMcRestart()
                    || owningScreen.allRequireMcRestart,
              GuiConfig.getAbridgedConfigPath(
                    MekanismMekaSuit.configuration.toString()));
    }

    public static final class MekaToolEntry extends CategoryEntry
    {
        public MekaToolEntry(GuiConfig screen, GuiConfigEntries entries,
              IConfigElement element)
        {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiMekaSuitConfig.buildChildScreen(owningScreen,
                  configElement, "meka_tool");
        }
    }

    public static final class MekaSuitEntry extends CategoryEntry
    {
        public MekaSuitEntry(GuiConfig screen, GuiConfigEntries entries,
              IConfigElement element)
        {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiMekaSuitConfig.buildChildScreen(owningScreen,
                  configElement, "mekasuit");
        }
    }

    public static final class ModificationStationEntry extends CategoryEntry
    {
        public ModificationStationEntry(GuiConfig screen,
              GuiConfigEntries entries, IConfigElement element)
        {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiMekaSuitConfig.buildChildScreen(owningScreen,
                  configElement, "modification_station");
        }
    }

    public static final class GeneratorsEntry extends CategoryEntry
    {
        public GeneratorsEntry(GuiConfig screen, GuiConfigEntries entries,
              IConfigElement element)
        {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiMekaSuitConfig.buildChildScreen(owningScreen,
                  configElement, "generators");
        }
    }
}
