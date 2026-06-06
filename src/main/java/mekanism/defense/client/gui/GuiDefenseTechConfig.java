package defense.client.gui;

import java.util.ArrayList;
import java.util.List;

import mekanism.common.util.LangUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import defense.common.Reference;
import defense.common.Settings;

public class GuiDefenseTechConfig extends GuiConfig
{
	public GuiDefenseTechConfig(GuiScreen parent)
	{
		super(parent, getConfigElements(), Reference.NAME, false, false, Reference.NAME);
	}
	
	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.add(new DummyCategoryElement(LangUtils.localize("defensetech.configgui.ctgy.general"), "defensetech.configgui.ctgy.general", GeneralEntry.class));
		list.add(new DummyCategoryElement(LangUtils.localize("defensetech.configgui.ctgy.explosives"), "defensetech.configgui.ctgy.explosives", ExplosivesEntry.class));
		return list;
	}

	public static class GeneralEntry extends CategoryEntry
	{
		public GeneralEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
		{
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen()
		{
			return new GuiConfig(owningScreen,
					new ConfigElement(Settings.CONFIGURATION.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
					owningScreen.modID, Configuration.CATEGORY_GENERAL, false, false,
					GuiConfig.getAbridgedConfigPath(Settings.CONFIGURATION.toString()));
		}
	}
	
	public static class ExplosivesEntry extends CategoryEntry
	{
		public ExplosivesEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
		{
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen()
		{
			return new GuiConfig(owningScreen,
					new ConfigElement(Settings.CONFIGURATION.getCategory("explosives")).getChildElements(),
					owningScreen.modID, Configuration.CATEGORY_GENERAL, false, false,
					GuiConfig.getAbridgedConfigPath(Settings.CONFIGURATION.toString()));
		}
	}
}
