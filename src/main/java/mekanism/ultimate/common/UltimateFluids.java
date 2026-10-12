package mekanism.ultimate.common;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/** Fluid identities owned by the Ultimate module. */
public final class UltimateFluids
{
    public static Fluid NutritionalPaste = createNutritionalPaste();
    /** Client render proxy for non-fluid chemical contents. */
    public static Fluid ChemicalRender = createChemicalRender();

    private static boolean registered;

    private UltimateFluids()
    {
    }

    public static synchronized void register()
    {
        if (registered)
        {
            return;
        }
        if (!FluidRegistry.registerFluid(NutritionalPaste))
        {
            Fluid existing = FluidRegistry.getFluid("nutritional_paste");
            if (existing == null)
            {
                throw new IllegalStateException(
                      "Nutritional Paste registration failed without an existing identity");
            }
            NutritionalPaste = existing;
        }
        if (!FluidRegistry.registerFluid(ChemicalRender))
        {
            Fluid existing = FluidRegistry.getFluid(
                  "mekanismultimate_chemical_render");
            if (existing == null)
            {
                throw new IllegalStateException(
                      "Chemical render fluid registration failed without an existing identity");
            }
            ChemicalRender = existing;
        }
        registered = true;
    }

    private static Fluid createNutritionalPaste()
    {
        return new Fluid("nutritional_paste")
        {
            @Override
            public int getColor()
            {
                return 0xFFEB6CA3;
            }

            @Override
            public int getColor(FluidStack stack)
            {
                return 0xFFEB6CA3;
            }
        }.setUnlocalizedName("nutritional_paste")
              .setDensity(1_250).setViscosity(1_500);
    }

    private static Fluid createChemicalRender()
    {
        return new Fluid("mekanismultimate_chemical_render")
        {
            @Override
            public int getColor()
            {
                return 0xFFFFFFFF;
            }

            @Override
            public int getColor(FluidStack stack)
            {
                return stack != null && stack.tag != null
                      && stack.tag.hasKey("renderColor")
                            ? 0xFF000000
                                  | stack.tag.getInteger("renderColor")
                            : getColor();
            }
        }.setUnlocalizedName("mekanismultimate_chemical_render");
    }
}
