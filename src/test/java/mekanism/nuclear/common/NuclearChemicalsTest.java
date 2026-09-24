package mekanism.nuclear.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import mekanism.api.gas.GasRegistry;
import org.junit.BeforeClass;
import org.junit.Test;

public class NuclearChemicalsTest {

    @BeforeClass
    public static void registerChemicals() {
        NuclearChemicals.register();
    }

    @Test
    public void registersStableNamesAndColorsIdempotently() {
        assertChemical(NuclearChemicals.HYDROFLUORIC_ACID_NAME, 0xC6C7BD, NuclearChemicals.HydrofluoricAcid);
        assertChemical(NuclearChemicals.URANIUM_OXIDE_NAME, 0xE1F573, NuclearChemicals.UraniumOxide);
        assertChemical(NuclearChemicals.URANIUM_HEXAFLUORIDE_NAME, 0x809960, NuclearChemicals.UraniumHexafluoride);
        assertChemical(NuclearChemicals.FISSILE_FUEL_NAME, 0x2E332F, NuclearChemicals.FissileFuel);
        assertChemical(NuclearChemicals.SODIUM_NAME, 0xE9FEF4, NuclearChemicals.Sodium);
        assertChemical(NuclearChemicals.SUPERHEATED_SODIUM_NAME, 0xD19469, NuclearChemicals.SuperheatedSodium);
        assertChemical(NuclearChemicals.NUCLEAR_WASTE_NAME, 0x4F412A, NuclearChemicals.NuclearWaste);
        assertChemical(NuclearChemicals.SPENT_NUCLEAR_WASTE_NAME, 0x262015, NuclearChemicals.SpentNuclearWaste);
        assertChemical(NuclearChemicals.PLUTONIUM_NAME, 0x1F919C, NuclearChemicals.Plutonium);
        assertChemical(NuclearChemicals.POLONIUM_NAME, 0x1B9E7B, NuclearChemicals.Polonium);
        assertChemical(NuclearChemicals.ANTIMATTER_NAME, 0xA464B3, NuclearChemicals.Antimatter);

        int count = GasRegistry.getRegisteredGasses().size();
        NuclearChemicals.register();
        assertEquals(count, GasRegistry.getRegisteredGasses().size());
    }

    @Test
    public void radioactivityMatchesStableDataMap() {
        assertTrue(NuclearChemicals.isRadioactive(NuclearChemicals.NUCLEAR_WASTE_NAME));
        assertTrue(NuclearChemicals.isRadioactive(NuclearChemicals.SPENT_NUCLEAR_WASTE_NAME));
        assertTrue(NuclearChemicals.isRadioactive(NuclearChemicals.PLUTONIUM_NAME));
        assertTrue(NuclearChemicals.isRadioactive(NuclearChemicals.POLONIUM_NAME));
        assertFalse(NuclearChemicals.isRadioactive(NuclearChemicals.FISSILE_FUEL_NAME));
        assertFalse(NuclearChemicals.isRadioactive(NuclearChemicals.ANTIMATTER_NAME));
    }

    private static void assertChemical(String name, int color, mekanism.api.gas.Gas gas) {
        assertSame(gas, GasRegistry.getGas(name));
        assertEquals(color, gas.getTint());
    }
}
