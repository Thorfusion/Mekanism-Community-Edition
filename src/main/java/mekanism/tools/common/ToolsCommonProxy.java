package mekanism.tools.common;

import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;

public class ToolsCommonProxy {

    /**
     * Set and load the mod's common configuration properties.
     */
    public void loadConfiguration() {
        MekanismConfig.current().tools.load(Mekanism.configurationtools);
        if (Mekanism.configurationtools.hasChanged()) {
            Mekanism.configurationtools.save();
        }
    }

    public void registerItemRenders() {
    }
}