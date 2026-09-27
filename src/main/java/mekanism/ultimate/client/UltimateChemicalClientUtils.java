package mekanism.ultimate.client;

import mekanism.common.util.LangUtils;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.content.chemical.PigmentTypeCE;
import mekanism.ultimate.common.integration.mekanism.MekGasChemicalType;
import net.minecraft.util.ResourceLocation;

/** Client presentation helpers for chemicals owned by the Ultimate facade. */
public final class UltimateChemicalClientUtils {

    private UltimateChemicalClientUtils() {
    }

    public static String getDisplayName(IChemicalTypeCE type) {
        if (type instanceof MekGasChemicalType) {
            return ((MekGasChemicalType) type).getGas().getLocalizedName();
        }
        if (type instanceof PigmentTypeCE) {
            return LangUtils.localize("chemical.pigment."
                  + ((PigmentTypeCE) type).getPath());
        }
        return type == null ? LangUtils.localize("gui.empty") : type.getRegistryName();
    }

    public static int getColor(IChemicalTypeCE type) {
        if (type instanceof MekGasChemicalType) {
            return 0xFF000000 | ((MekGasChemicalType) type).getGas().getTint();
        }
        if (type instanceof PigmentTypeCE) {
            return 0xFF000000 | ((PigmentTypeCE) type).getColor();
        }
        return 0xFFFFFFFF;
    }

    public static String getModId(IChemicalTypeCE type) {
        if (type == null || type.getRegistryName() == null) {
            return MekanismUltimate.MODID;
        }
        ResourceLocation name = new ResourceLocation(type.getRegistryName());
        return name.getNamespace();
    }
}
