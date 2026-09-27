package mekanism.mekasuit.client.gui;

import java.util.ArrayList;
import java.util.List;
import mekanism.mekasuit.common.MekanismMekaSuit;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiMekaSuitConfig extends GuiConfig {

    public GuiMekaSuitConfig(GuiScreen parent) {
        super(parent, categories(), MekanismMekaSuit.MODID, false, false,
              "Mekanism MekaSuit (Beta)");
    }

    private static List<IConfigElement> categories() {
        List<IConfigElement> categories = new ArrayList<>();
        categories.add(category("Meka-Tool", "meka_tool", MekaToolEntry.class));
        categories.add(category("MekaSuit", "mekasuit", SuitEntry.class));
        categories.add(category("Modification Station", "modification_station", StationEntry.class));
        return categories;
    }

    private static DummyCategoryElement category(String name, String key,
          Class<? extends CategoryEntry> entryClass) {
        return new DummyCategoryElement(name, MekanismMekaSuit.MODID + ".configgui." + key,
              entryClass);
    }

    private static GuiScreen child(GuiConfig parent, String category) {
        return new GuiConfig(parent,
              new ConfigElement(MekanismMekaSuit.configuration.getCategory(category)).getChildElements(),
              MekanismMekaSuit.MODID, category, false, false,
              GuiConfig.getAbridgedConfigPath(MekanismMekaSuit.configuration.toString()));
    }

    public static final class MekaToolEntry extends CategoryEntry {
        public MekaToolEntry(GuiConfig screen, GuiConfigEntries entries, IConfigElement element) {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return child(owningScreen, "meka_tool");
        }
    }

    public static final class SuitEntry extends CategoryEntry {
        public SuitEntry(GuiConfig screen, GuiConfigEntries entries, IConfigElement element) {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return child(owningScreen, "mekasuit");
        }
    }

    public static final class StationEntry extends CategoryEntry {
        public StationEntry(GuiConfig screen, GuiConfigEntries entries, IConfigElement element) {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return child(owningScreen, "modification_station");
        }
    }
}
