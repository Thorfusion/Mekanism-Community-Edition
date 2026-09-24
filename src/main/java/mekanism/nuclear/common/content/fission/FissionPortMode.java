package mekanism.nuclear.common.content.fission;

import net.minecraft.util.IStringSerializable;

public enum FissionPortMode implements IStringSerializable {
    INPUT("input"),
    OUTPUT_WASTE("output_waste"),
    OUTPUT_COOLANT("output_coolant");

    private final String name;

    FissionPortMode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public FissionPortMode next() {
        FissionPortMode[] modes = values();
        return modes[(ordinal() + 1) % modes.length];
    }

    public static FissionPortMode byIndex(int index) {
        FissionPortMode[] modes = values();
        return modes[Math.floorMod(index, modes.length)];
    }
}
