package mekanism.ultimate.client.gui;

import mekanism.common.Mekanism;
import mekanism.ultimate.common.MekanismUltimate;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiUltimateConfig extends GuiConfig {

    public GuiUltimateConfig(GuiScreen parent) {
        super(parent, new ConfigElement(Mekanism.configurationultimate.getCategory("ultimate")).getChildElements(),
              MekanismUltimate.MODID, false, false, "Mekanism Ultimate");
    }
}
