package mekanism.nuclear.client.gui;

import java.util.ArrayList;
import java.util.List;
import mekanism.nuclear.common.MekanismNuclear;
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
public final class GuiNuclearConfig extends GuiConfig {

    public GuiNuclearConfig(GuiScreen parent) {
        super(parent, categories(), MekanismNuclear.MODID, false, false,
              "Mekanism Nuclear (Beta)");
    }

    private static List<IConfigElement> categories() {
        List<IConfigElement> categories = new ArrayList<>();
        categories.add(category("Fission Reactor", "fission_reactor", FissionEntry.class));
        categories.add(category("Radiation", "radiation", RadiationEntry.class));
        categories.add(category("Supercritical Phase Shifter", "sps", SPSEntry.class));
        categories.add(category("World Generation", "worldgen", WorldGenEntry.class));
        return categories;
    }

    private static DummyCategoryElement category(String name, String key,
          Class<? extends CategoryEntry> entryClass) {
        return new DummyCategoryElement(name, MekanismNuclear.MODID + ".configgui." + key,
              entryClass);
    }

    private static GuiScreen child(GuiConfig parent, String category) {
        return new GuiConfig(parent,
              new ConfigElement(MekanismNuclear.configuration.getCategory(category)).getChildElements(),
              MekanismNuclear.MODID, category, false, false,
              GuiConfig.getAbridgedConfigPath(MekanismNuclear.configuration.toString()));
    }

    public static final class FissionEntry extends CategoryEntry {
        public FissionEntry(GuiConfig screen, GuiConfigEntries entries, IConfigElement element) {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return child(owningScreen, "fission_reactor");
        }
    }

    public static final class RadiationEntry extends CategoryEntry {
        public RadiationEntry(GuiConfig screen, GuiConfigEntries entries, IConfigElement element) {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return child(owningScreen, "radiation");
        }
    }

    public static final class SPSEntry extends CategoryEntry {
        public SPSEntry(GuiConfig screen, GuiConfigEntries entries, IConfigElement element) {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return child(owningScreen, "sps");
        }
    }

    public static final class WorldGenEntry extends CategoryEntry {
        public WorldGenEntry(GuiConfig screen, GuiConfigEntries entries, IConfigElement element) {
            super(screen, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            return child(owningScreen, "worldgen");
        }
    }
}
