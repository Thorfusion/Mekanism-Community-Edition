package mekanism.ultimate.common.content.chemical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.Test;

public class LongChemicalTankTest {

    private static final long LARGE_AMOUNT = (long) Integer.MAX_VALUE + 50_000L;
    private static final TestChemicalType TEST_GAS = new TestChemicalType("test:gas", ChemicalKind.GAS);
    private static final TestChemicalType OTHER_GAS = new TestChemicalType("test:other_gas", ChemicalKind.GAS);

    @Test
    public void simulationDoesNotMutateAndExecutionUsesLongs() {
        LongChemicalTank tank = new LongChemicalTank(LARGE_AMOUNT + 1_000L);
        ChemicalStackCE input = new ChemicalStackCE(TEST_GAS, LARGE_AMOUNT);

        assertEquals(LARGE_AMOUNT, tank.insert(input, Action.SIMULATE));
        assertEquals(0, tank.getStored());
        assertEquals(LARGE_AMOUNT, tank.insert(input, Action.EXECUTE));
        assertEquals(LARGE_AMOUNT, tank.getStored());

        assertEquals(25_000L, tank.extract(25_000L, Action.SIMULATE).getAmount());
        assertEquals(LARGE_AMOUNT, tank.getStored());
        assertEquals(25_000L, tank.extract(25_000L, Action.EXECUTE).getAmount());
        assertEquals(LARGE_AMOUNT - 25_000L, tank.getStored());
    }

    @Test
    public void capacityAndChemicalIdentityAreEnforced() {
        LongChemicalTank tank = new LongChemicalTank(100);
        assertEquals(100, tank.insert(new ChemicalStackCE(TEST_GAS, 150), Action.EXECUTE));
        assertEquals(0, tank.insert(new ChemicalStackCE(OTHER_GAS, 1), Action.EXECUTE));
        assertEquals(100, tank.getStored());
        assertEquals(100, tank.extract(200, Action.EXECUTE).getAmount());
        assertNull(tank.getStack());
    }

    @Test
    public void nbtRoundTripPreservesLongAmount() {
        LongChemicalTank source = new LongChemicalTank(LARGE_AMOUNT + 1);
        source.insert(new ChemicalStackCE(TEST_GAS, LARGE_AMOUNT), Action.EXECUTE);
        NBTTagCompound data = new NBTTagCompound();
        source.writeToNBT(data);

        LongChemicalTank loaded = new LongChemicalTank(LARGE_AMOUNT + 1);
        loaded.readFromNBT(data, (kind, name) -> kind == TEST_GAS.getKind() && name.equals(TEST_GAS.getRegistryName()) ? TEST_GAS : null);

        assertEquals(LARGE_AMOUNT, loaded.getStored());
        assertSame(TEST_GAS, loaded.getStack().getType());
        assertEquals(LongChemicalTank.DATA_VERSION, data.getInteger("UMDataVersion"));
        assertEquals(LARGE_AMOUNT, data.getLong("Amount"));
    }

    @Test
    public void malformedDataClearsRatherThanCreatingInvalidState() {
        LongChemicalTank tank = new LongChemicalTank(100);
        tank.insert(new ChemicalStackCE(TEST_GAS, 50), Action.EXECUTE);
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("UMDataVersion", LongChemicalTank.DATA_VERSION);
        data.setString("ChemicalKind", ChemicalKind.GAS.name());
        data.setString("ChemicalName", TEST_GAS.getRegistryName());
        data.setLong("Amount", -1);

        tank.readFromNBT(data, (kind, name) -> TEST_GAS);

        assertEquals(0, tank.getStored());
        assertNull(tank.getStack());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeInsertIsRejected() {
        new ChemicalStackCE(TEST_GAS, -1);
    }

    private static final class TestChemicalType implements IChemicalTypeCE {

        private final String name;
        private final ChemicalKind kind;

        private TestChemicalType(String name, ChemicalKind kind) {
            this.name = name;
            this.kind = kind;
        }

        @Override
        public String getRegistryName() {
            return name;
        }

        @Override
        public ChemicalKind getKind() {
            return kind;
        }

        @Override
        public boolean isRadioactive() {
            return false;
        }
    }
}
