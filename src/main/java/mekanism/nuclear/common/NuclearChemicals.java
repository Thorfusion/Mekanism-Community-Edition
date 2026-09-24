package mekanism.nuclear.common;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;

/** Modern Nuclear chemicals represented through the 1.12 gas API. */
public final class NuclearChemicals {

    public static final String HYDROFLUORIC_ACID_NAME = "hydrofluoric_acid";
    public static final String URANIUM_OXIDE_NAME = "uranium_oxide";
    public static final String URANIUM_HEXAFLUORIDE_NAME = "uranium_hexafluoride";
    public static final String FISSILE_FUEL_NAME = "fissile_fuel";
    public static final String SODIUM_NAME = "sodium";
    public static final String SUPERHEATED_SODIUM_NAME = "superheated_sodium";
    public static final String NUCLEAR_WASTE_NAME = "nuclear_waste";
    public static final String SPENT_NUCLEAR_WASTE_NAME = "spent_nuclear_waste";
    public static final String PLUTONIUM_NAME = "plutonium";
    public static final String POLONIUM_NAME = "polonium";
    public static final String ANTIMATTER_NAME = "antimatter";

    public static Gas HydrofluoricAcid;
    public static Gas UraniumOxide;
    public static Gas UraniumHexafluoride;
    public static Gas FissileFuel;
    public static Gas Sodium;
    public static Gas SuperheatedSodium;
    public static Gas NuclearWaste;
    public static Gas SpentNuclearWaste;
    public static Gas Plutonium;
    public static Gas Polonium;
    public static Gas Antimatter;

    private NuclearChemicals() {
    }

    /** Registers in a fixed order so client and server gas IDs remain identical. */
    public static synchronized void register() {
        HydrofluoricAcid = registerOrGet(HYDROFLUORIC_ACID_NAME, 0xC6C7BD);
        UraniumOxide = registerOrGet(URANIUM_OXIDE_NAME, 0xE1F573);
        UraniumHexafluoride = registerOrGet(URANIUM_HEXAFLUORIDE_NAME, 0x809960);
        FissileFuel = registerOrGet(FISSILE_FUEL_NAME, 0x2E332F);
        Sodium = registerOrGet(SODIUM_NAME, 0xE9FEF4);
        SuperheatedSodium = registerOrGet(SUPERHEATED_SODIUM_NAME, 0xD19469);
        NuclearWaste = registerOrGet(NUCLEAR_WASTE_NAME, 0x4F412A);
        SpentNuclearWaste = registerOrGet(SPENT_NUCLEAR_WASTE_NAME, 0x262015);
        Plutonium = registerOrGet(PLUTONIUM_NAME, 0x1F919C);
        Polonium = registerOrGet(POLONIUM_NAME, 0x1B9E7B);
        Antimatter = registerOrGet(ANTIMATTER_NAME, 0xA464B3);
    }

    public static boolean isRadioactive(String name) {
        return NUCLEAR_WASTE_NAME.equals(name) || SPENT_NUCLEAR_WASTE_NAME.equals(name)
              || PLUTONIUM_NAME.equals(name) || POLONIUM_NAME.equals(name);
    }

    private static Gas registerOrGet(String name, int tint) {
        Gas existing = GasRegistry.getGas(name);
        return existing == null ? GasRegistry.register(new Gas(name, tint)) : existing;
    }
}
