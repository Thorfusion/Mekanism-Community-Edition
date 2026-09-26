package mekanism.ultimate.common;

import mekanism.common.Mekanism;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/** Fluid identities owned by the Ultimate module. */
public final class UltimateFluids {

    private static final ResourceLocation LIQUID_TEXTURE =
          new ResourceLocation(Mekanism.MODID, "blocks/liquid/LiquidSteam");

    public static Fluid NutritionalPaste = new Fluid("nutritional_paste", LIQUID_TEXTURE, LIQUID_TEXTURE)
          .setColor(0xFFEB6CA3)
          .setDensity(1_250)
          .setViscosity(1_500);

    private static boolean registered;

    private UltimateFluids() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }
        if (!FluidRegistry.registerFluid(NutritionalPaste)) {
            Fluid existing = FluidRegistry.getFluid("nutritional_paste");
            if (existing == null) {
                throw new IllegalStateException("Nutritional Paste fluid registration failed without an existing identity");
            }
            NutritionalPaste = existing;
        }
        FluidRegistry.addBucketForFluid(NutritionalPaste);
        registered = true;
    }
}
