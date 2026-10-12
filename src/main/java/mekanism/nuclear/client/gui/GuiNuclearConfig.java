package mekanism.nuclear.client.gui;

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
import mekanism.nuclear.common.MekanismNuclear;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

@SideOnly(Side.CLIENT)
public final class GuiNuclearConfig extends GuiConfig
{
    public GuiNuclearConfig(GuiScreen parent)
    {
        super(parent, getConfigElements(), MekanismNuclear.MODID, false,
              false, "Mekanism Community Edition: Nuclear (Beta)");
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        list.add(category("fission_reactor", FissionEntry.class));
        list.add(category("radiation", RadiationEntry.class));
        list.add(category("sps", SPSEntry.class));
        list.add(category("worldgen", WorldGenEntry.class));
        return list;
    }

    private static DummyCategoryElement category(String key,
          Class<? extends CategoryEntry> entryClass)
    {
        String langKey = MekanismNuclear.MODID + ".configgui.ctgy." + key;
        return new DummyCategoryElement(LangUtils.localize(langKey),
              langKey, entryClass);
    }

    private static GuiScreen buildChildScreen(GuiConfig owningScreen,
          IConfigElement configElement, String category)
    {
        return new GuiConfig(owningScreen, new ConfigElement(
              MekanismNuclear.configuration.getCategory(category))
                    .getChildElements(), owningScreen.modID, category, false,
              configElement.requiresMcRestart()
                    || owningScreen.allRequireMcRestart,
              GuiConfig.getAbridgedConfigPath(
                    MekanismNuclear.configuration.toString()));
    }

    public static final class FissionEntry extends CategoryEntry
    {
        public FissionEntry(GuiConfig screen, GuiConfigEntries entries,
              IConfigElement element)
        {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiNuclearConfig.buildChildScreen(owningScreen,
                  configElement, "fission_reactor");
        }
    }

    public static final class RadiationEntry extends CategoryEntry
    {
        public RadiationEntry(GuiConfig screen, GuiConfigEntries entries,
              IConfigElement element)
        {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiNuclearConfig.buildChildScreen(owningScreen,
                  configElement, "radiation");
        }
    }

    public static final class SPSEntry extends CategoryEntry
    {
        public SPSEntry(GuiConfig screen, GuiConfigEntries entries,
              IConfigElement element)
        {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiNuclearConfig.buildChildScreen(owningScreen,
                  configElement, "sps");
        }
    }

    public static final class WorldGenEntry extends CategoryEntry
    {
        public WorldGenEntry(GuiConfig screen, GuiConfigEntries entries,
              IConfigElement element)
        {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen()
        {
            return GuiNuclearConfig.buildChildScreen(owningScreen,
                  configElement, "worldgen");
        }
    }
}
