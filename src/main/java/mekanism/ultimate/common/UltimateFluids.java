package mekanism.ultimate.common;

import mekanism.common.Mekanism;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/** Fluid identities owned by the Ultimate module. */
public final class UltimateFluids {

    private static final ResourceLocation LIQUID_TEXTURE =
          new ResourceLocation(Mekanism.MODID, "blocks/liquid/LiquidSteam");

    public static Fluid NutritionalPaste = new Fluid("nutritional_paste", LIQUID_TEXTURE, LIQUID_TEXTURE)
          .setColor(0xFFEB6CA3)
          .setDensity(1_250)
          .setViscosity(1_500);

    /** Client-only visual carrier; Dynamic Tanks persist chemical identity separately. */
    public static Fluid ChemicalRender = new Fluid("mekanismultimate_chemical_render", LIQUID_TEXTURE, LIQUID_TEXTURE) {
        @Override
        public int getColor(FluidStack stack) {
            return stack != null && stack.tag != null && stack.tag.hasKey("renderColor")
                  ? 0xFF000000 | stack.tag.getInteger("renderColor") : 0xFFFFFFFF;
        }
    };

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
        if (!FluidRegistry.registerFluid(ChemicalRender)) {
            Fluid existing = FluidRegistry.getFluid("mekanismultimate_chemical_render");
            if (existing == null) {
                throw new IllegalStateException("Chemical render fluid registration failed without an existing identity");
            }
            ChemicalRender = existing;
        }
        registered = true;
    }
}
