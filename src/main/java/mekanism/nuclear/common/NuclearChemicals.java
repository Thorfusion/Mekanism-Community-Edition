package mekanism.nuclear.common;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;

/** Modern Nuclear chemicals represented through the 1.12 gas API. */
public final class NuclearChemicals {

    public static final String URANIUM_HEXAFLUORIDE_NAME = "uranium_hexafluoride";
    public static final String FISSILE_FUEL_NAME = "fissile_fuel";
    public static final String NUCLEAR_WASTE_NAME = "nuclear_waste";
    public static final String PLUTONIUM_NAME = "plutonium";

    public static Gas UraniumHexafluoride;
    public static Gas FissileFuel;
    public static Gas NuclearWaste;
    public static Gas Plutonium;

    private NuclearChemicals() {
    }

    /** Registers in a fixed order so client and server gas IDs remain identical. */
    public static synchronized void register() {
        UraniumHexafluoride = registerOrGet(URANIUM_HEXAFLUORIDE_NAME, 0x809960);
        FissileFuel = registerOrGet(FISSILE_FUEL_NAME, 0x2E332F);
        NuclearWaste = registerOrGet(NUCLEAR_WASTE_NAME, 0x4F412A);
        Plutonium = registerOrGet(PLUTONIUM_NAME, 0x1F919C);
    }

    public static boolean isRadioactive(String name) {
        return FISSILE_FUEL_NAME.equals(name) || NUCLEAR_WASTE_NAME.equals(name) || PLUTONIUM_NAME.equals(name);
    }

    private static Gas registerOrGet(String name, int tint) {
        Gas existing = GasRegistry.getGas(name);
        return existing == null ? GasRegistry.register(new Gas(name, tint)) : existing;
    }
}
