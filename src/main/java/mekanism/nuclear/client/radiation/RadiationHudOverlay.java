package mekanism.nuclear.client.radiation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.nuclear.common.radiation.MekaSuitRadiationBridge;
import mekanism.nuclear.common.radiation.RadiationDisplay;
import mekanism.nuclear.common.radiation.RadiationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/** Compact warning overlay for hazardous environmental radiation or dose. */
@SideOnly(Side.CLIENT)
public final class RadiationHudOverlay
{
    public static final RadiationHudOverlay INSTANCE = new RadiationHudOverlay();

    private RadiationHudOverlay()
    {
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event)
    {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT)
        {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null || minecraft.gameSettings.showDebugInfo)
        {
            return;
        }
        double environmental = ClientRadiationData.getEnvironmental();
        double dose = ClientRadiationData.getDose();
        ItemStack bodyArmor = minecraft.thePlayer.getCurrentArmor(2);
        boolean geiger = MekaSuitRadiationBridge.hasGeiger(bodyArmor);
        boolean dosimeter = MekaSuitRadiationBridge.hasDosimeter(bodyArmor);
        boolean hazardous = environmental >= RadiationManager.MIN_MAGNITUDE
              || dose >= RadiationManager.MIN_MAGNITUDE;
        if (!geiger && !dosimeter && !hazardous)
        {
            return;
        }
        String rateText = "Radiation: "
              + RadiationDisplay.formatDoseRate(environmental);
        String doseText = "Dose: " + RadiationDisplay.formatDose(dose);
        boolean showRate = geiger || !geiger && !dosimeter && hazardous;
        boolean showDose = dosimeter || !geiger && !dosimeter && hazardous;
        int width = Math.max(showRate
                    ? minecraft.fontRenderer.getStringWidth(rateText) : 0,
              showDose ? minecraft.fontRenderer.getStringWidth(doseText) : 0);
        int x = event.resolution.getScaledWidth() - width - 6;
        int y = 6;
        if (showRate)
        {
            minecraft.fontRenderer.drawStringWithShadow(rateText, x, y,
                  color(environmental));
            y += 11;
        }
        if (showDose)
        {
            minecraft.fontRenderer.drawStringWithShadow(doseText, x, y,
                  color(dose));
        }
    }

    private static int color(double magnitude)
    {
        if (magnitude < RadiationManager.MIN_MAGNITUDE)
        {
            return 0xAAAAAA;
        }
        else if (magnitude < 0.001D)
        {
            return 0xFFFF55;
        }
        else if (magnitude < 0.1D)
        {
            return 0xFFAA00;
        }
        else if (magnitude < 10D)
        {
            return 0xFF5555;
        }
        return 0xAA0000;
    }
}
