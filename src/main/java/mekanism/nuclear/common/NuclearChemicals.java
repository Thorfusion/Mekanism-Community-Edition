package mekanism.nuclear.common;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasCoolantRegistry;
import mekanism.api.gas.GasRegistry;

/** Nuclear gas identities, registered in a fixed client/server order. */
public final class NuclearChemicals
{
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

    private NuclearChemicals()
    {
    }

    public static synchronized void register()
    {
        HydrofluoricAcid = registerOrGet(HYDROFLUORIC_ACID_NAME);
        UraniumOxide = registerOrGet(URANIUM_OXIDE_NAME);
        UraniumHexafluoride = registerOrGet(URANIUM_HEXAFLUORIDE_NAME);
        FissileFuel = registerOrGet(FISSILE_FUEL_NAME);
        Sodium = registerOrGet(SODIUM_NAME);
        SuperheatedSodium = registerOrGet(SUPERHEATED_SODIUM_NAME);
        NuclearWaste = registerOrGet(NUCLEAR_WASTE_NAME);
        SpentNuclearWaste = registerOrGet(SPENT_NUCLEAR_WASTE_NAME);
        Plutonium = registerOrGet(PLUTONIUM_NAME);
        Polonium = registerOrGet(POLONIUM_NAME);
        Antimatter = registerOrGet(ANTIMATTER_NAME);
        GasCoolantRegistry.register(Sodium, SuperheatedSodium,
              5D, 0.4D, 100_000D);
    }

    public static boolean isRadioactive(String name)
    {
        return NUCLEAR_WASTE_NAME.equals(name) || SPENT_NUCLEAR_WASTE_NAME.equals(name)
              || PLUTONIUM_NAME.equals(name) || POLONIUM_NAME.equals(name);
    }

    /** Stable radioactivity in Sv/h per mB for the pinned release baseline. */
    public static double getRadioactivityPerMb(String name)
    {
        if (NUCLEAR_WASTE_NAME.equals(name) || SPENT_NUCLEAR_WASTE_NAME.equals(name))
        {
            return 0.01D;
        }
        if (PLUTONIUM_NAME.equals(name))
        {
            return 0.02D;
        }
        if (POLONIUM_NAME.equals(name))
        {
            return 0.05D;
        }
        return 0;
    }

    /** Stable Waste Barrel blacklist: usable products do not passively decay. */
    public static boolean isWasteBarrelDecayBlacklisted(String name)
    {
        return PLUTONIUM_NAME.equals(name) || POLONIUM_NAME.equals(name);
    }

    private static Gas registerOrGet(String name)
    {
        Gas existing = GasRegistry.getGas(name);
        return existing == null ? GasRegistry.register(new Gas(name)) : existing;
    }
}
