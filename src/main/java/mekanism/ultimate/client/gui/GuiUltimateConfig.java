package mekanism.ultimate.client.gui;

import java.util.ArrayList;
import java.util.List;
import mekanism.common.Mekanism;
import mekanism.ultimate.common.MekanismUltimate;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiUltimateConfig extends GuiConfig {

    public GuiUltimateConfig(GuiScreen parent) {
        super(parent, getConfigElements(),
              MekanismUltimate.MODID, false, false, "Mekanism Ultimate");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> elements = new ArrayList<>();
        elements.addAll(new ConfigElement(Mekanism.configurationultimate.getCategory("ultimate")).getChildElements());
        elements.addAll(new ConfigElement(Mekanism.configurationultimate.getCategory("nutrition")).getChildElements());
        return elements;
    }
}
