package mekanism.ultimate.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public final class UltimateSounds {

    public static final ResourceLocation INDUSTRIAL_ALARM_LOCATION =
          new ResourceLocation(MekanismUltimate.MODID, "tile.industrial_alarm");
    public static final SoundEvent INDUSTRIAL_ALARM =
          new SoundEvent(INDUSTRIAL_ALARM_LOCATION).setRegistryName(INDUSTRIAL_ALARM_LOCATION);

    private UltimateSounds() {
    }
}
