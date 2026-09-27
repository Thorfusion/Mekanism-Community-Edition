package mekanism.ultimate.common.content.chemical;

import java.util.Locale;
import javax.annotation.Nullable;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.MekanismUltimate;
import net.minecraft.item.EnumDyeColor;

/** Exact stable pigment palette, including Mekanism's two non-vanilla mix colors. */
public enum PigmentTypeCE implements IChemicalTypeCE {
    BLACK("black", 0x404040, EnumDyeColor.BLACK),
    BLUE("blue", 0x366BD0, EnumDyeColor.BLUE),
    GREEN("green", 0x59C15F, EnumDyeColor.GREEN),
    CYAN("cyan", 0x00F3D0, EnumDyeColor.CYAN),
    DARK_RED("dark_red", 0xC9071F, null),
    PURPLE("purple", 0xA460D9, EnumDyeColor.PURPLE),
    ORANGE("orange", 0xFFA160, EnumDyeColor.ORANGE),
    LIGHT_GRAY("light_gray", 0xCFCFCF, EnumDyeColor.SILVER),
    GRAY("gray", 0x7A7A7A, EnumDyeColor.GRAY),
    LIGHT_BLUE("light_blue", 0x559EFF, EnumDyeColor.LIGHT_BLUE),
    LIME("lime", 0x75FF89, EnumDyeColor.LIME),
    AQUA("aqua", 0x30FFF9, null),
    RED("red", 0xFF383C, EnumDyeColor.RED),
    MAGENTA("magenta", 0xD55ECB, EnumDyeColor.MAGENTA),
    YELLOW("yellow", 0xFFDD4F, EnumDyeColor.YELLOW),
    WHITE("white", 0xFFFFFF, EnumDyeColor.WHITE),
    BROWN("brown", 0xA17649, EnumDyeColor.BROWN),
    PINK("pink", 0xFFBCC4, EnumDyeColor.PINK);

    private final String path;
    private final String registryName;
    private final int color;
    private final EnumDyeColor dyeColor;

    PigmentTypeCE(String path, int color, @Nullable EnumDyeColor dyeColor) {
        this.path = path;
        this.registryName = MekanismUltimate.MODID + ":" + path;
        this.color = color;
        this.dyeColor = dyeColor;
    }

    public String getPath() {
        return path;
    }

    public int getColor() {
        return color;
    }

    @Nullable
    public EnumDyeColor getDyeColor() {
        return dyeColor;
    }

    @Nullable
    public static PigmentTypeCE byDye(EnumDyeColor color) {
        if (color != null) {
            for (PigmentTypeCE pigment : values()) {
                if (pigment.dyeColor == color) {
                    return pigment;
                }
            }
        }
        return null;
    }

    @Nullable
    public static PigmentTypeCE byPath(String path) {
        if (path != null) {
            String normalized = path.toLowerCase(Locale.ROOT);
            for (PigmentTypeCE pigment : values()) {
                if (pigment.path.equals(normalized) || pigment.registryName.equals(normalized)) {
                    return pigment;
                }
            }
        }
        return null;
    }

    @Override
    public String getRegistryName() {
        return registryName;
    }

    @Override
    public ChemicalKind getKind() {
        return ChemicalKind.PIGMENT;
    }

    @Override
    public boolean isRadioactive() {
        return false;
    }
}
