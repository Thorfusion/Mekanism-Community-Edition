package mekanism.ultimate.common.integration.mekanism;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import org.junit.Test;

public class LongBackedMekGasTankTest {

    private static final Gas GAS = new Gas("long_view_test", 0x445566);
    private static final Gas OTHER = new Gas("long_view_other", 0x665544);

    @Test
    public void legacyMutationsCommitToAuthoritativeTank() {
        LongChemicalTank authoritative = new LongChemicalTank(10_000);
        LongBackedMekGasTank view = new LongBackedMekGasTank(authoritative);

        assertEquals(4_000, view.receive(new GasStack(GAS, 4_000), true));
        assertEquals(4_000, authoritative.getStored());
        assertSame(GAS, view.getGasType());
        assertFalse(view.canReceiveType(OTHER));

        GasStack simulated = view.draw(750, false);
        assertEquals(750, simulated.amount);
        assertEquals(4_000, authoritative.getStored());

        GasStack drawn = view.draw(750, true);
        assertEquals(750, drawn.amount);
        assertEquals(3_250, authoritative.getStored());
        assertEquals(6_750, view.getNeeded());

        view.setGas(null);
        assertEquals(0, authoritative.getStored());
        assertNull(view.getGas());
    }

    @Test
    public void longQueriesSaturateAndLegacyTransfersRemainBounded() {
        long capacity = (long) Integer.MAX_VALUE + 50_000L;
        LongChemicalTank authoritative = new LongChemicalTank(capacity);
        authoritative.insert(new ChemicalStackCE(new MekGasChemicalType(GAS), capacity), Action.EXECUTE);
        LongBackedMekGasTank view = new LongBackedMekGasTank(authoritative);

        assertEquals(Integer.MAX_VALUE, view.getMaxGas());
        assertEquals(Integer.MAX_VALUE, view.getStored());
        assertEquals(0, view.getNeeded());

        GasStack drawn = view.draw(Integer.MAX_VALUE, true);
        assertEquals(Integer.MAX_VALUE, drawn.amount);
        assertEquals(50_000, authoritative.getStored());
        assertTrue(view.canDraw(GAS));
    }
}
