package mekanism.nuclear.client.radiation;

import mekanism.nuclear.common.radiation.RadiationDisplay;
import mekanism.nuclear.common.radiation.RadiationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Compact warning overlay for hazardous environmental radiation or player dose. */
@SideOnly(Side.CLIENT)
public final class RadiationHudOverlay {

    public static final RadiationHudOverlay INSTANCE = new RadiationHudOverlay();

    private RadiationHudOverlay() {
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || minecraft.gameSettings.showDebugInfo) {
            return;
        }
        double environmental = ClientRadiationData.getEnvironmental();
        double dose = ClientRadiationData.getDose();
        if (environmental < RadiationManager.MIN_MAGNITUDE && dose < RadiationManager.MIN_MAGNITUDE) {
            return;
        }
        String rateText = "Radiation: " + RadiationDisplay.formatDoseRate(environmental);
        String doseText = "Dose: " + RadiationDisplay.formatDose(dose);
        ScaledResolution resolution = new ScaledResolution(minecraft);
        int width = Math.max(minecraft.fontRenderer.getStringWidth(rateText),
              minecraft.fontRenderer.getStringWidth(doseText));
        int x = resolution.getScaledWidth() - width - 6;
        minecraft.fontRenderer.drawStringWithShadow(rateText, x, 6, color(environmental));
        minecraft.fontRenderer.drawStringWithShadow(doseText, x, 17, color(dose));
    }

    private static int color(double magnitude) {
        if (magnitude < RadiationManager.MIN_MAGNITUDE) {
            return 0xAAAAAA;
        } else if (magnitude < 0.001D) {
            return 0xFFFF55;
        } else if (magnitude < 0.1D) {
            return 0xFFAA00;
        } else if (magnitude < 10D) {
            return 0xFF5555;
        }
        return 0xAA0000;
    }
}
