package mekanism.ultimate.common.content.chemical;

import java.util.Locale;
import javax.annotation.Nullable;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;

/** Stable pigment palette with native 1.7 dye-damage mappings. */
public enum PigmentTypeCE implements IChemicalTypeCE
{
    BLACK("black", 0x404040, 0),
    BLUE("blue", 0x366BD0, 4),
    GREEN("green", 0x59C15F, 2),
    CYAN("cyan", 0x00F3D0, 6),
    DARK_RED("dark_red", 0xC9071F, -1),
    PURPLE("purple", 0xA460D9, 5),
    ORANGE("orange", 0xFFA160, 14),
    LIGHT_GRAY("light_gray", 0xCFCFCF, 7),
    GRAY("gray", 0x7A7A7A, 8),
    LIGHT_BLUE("light_blue", 0x559EFF, 12),
    LIME("lime", 0x75FF89, 10),
    AQUA("aqua", 0x30FFF9, -1),
    RED("red", 0xFF383C, 1),
    MAGENTA("magenta", 0xD55ECB, 13),
    YELLOW("yellow", 0xFFDD4F, 11),
    WHITE("white", 0xFFFFFF, 15),
    BROWN("brown", 0xA17649, 3),
    PINK("pink", 0xFFBCC4, 9);

    private static final String DOMAIN = "mekanismultimate";

    private final String path;
    private final String registryName;
    private final int color;
    private final int dyeDamage;

    PigmentTypeCE(String path, int color, int dyeDamage)
    {
        this.path = path;
        this.registryName = DOMAIN + ":" + path;
        this.color = color;
        this.dyeDamage = dyeDamage;
    }

    public String getPath()
    {
        return path;
    }

    public int getColor()
    {
        return color;
    }

    public boolean hasDye()
    {
        return dyeDamage >= 0;
    }

    public int getDyeDamage()
    {
        return dyeDamage;
    }

    /** Colored blocks use the inverse of the legacy dye item damage. */
    public int getBlockMetadata()
    {
        return hasDye() ? 15 - dyeDamage : -1;
    }

    @Nullable
    public static PigmentTypeCE byDyeDamage(int damage)
    {
        if (damage < 0)
        {
            return null;
        }
        for (PigmentTypeCE pigment : values())
        {
            if (pigment.hasDye() && pigment.dyeDamage == damage)
            {
                return pigment;
            }
        }
        return null;
    }

    @Nullable
    public static PigmentTypeCE byPath(String path)
    {
        if (path != null)
        {
            String normalized = path.toLowerCase(Locale.ROOT);
            for (PigmentTypeCE pigment : values())
            {
                if (pigment.path.equals(normalized)
                      || pigment.registryName.equals(normalized))
                {
                    return pigment;
                }
            }
        }
        return null;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public ChemicalKind getKind()
    {
        return ChemicalKind.PIGMENT;
    }

    @Override
    public boolean isRadioactive()
    {
        return false;
    }
}
