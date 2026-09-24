package mekanism.ultimate.common.integration.mekanism;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import org.junit.Test;

public class MekGasTankAdapterTest {

    @Test
    public void oldGasApiUsesBoundedWindowsWithoutTruncatingStorage() {
        Gas gas = new Gas("ultimate_test_gas", 0xFFFFFF);
        long capacity = (long) Integer.MAX_VALUE + 10_000L;
        LongChemicalTank tank = new LongChemicalTank(capacity);
        MekGasTankAdapter adapter = new MekGasTankAdapter(tank);

        assertEquals(Integer.MAX_VALUE, adapter.receiveGas(EnumFacing.NORTH,
              new GasStack(gas, Integer.MAX_VALUE), true));
        assertEquals(10_000, adapter.receiveGas(EnumFacing.NORTH, new GasStack(gas, 10_000), false));
        assertEquals((long) Integer.MAX_VALUE, tank.getStored());
        assertEquals(10_000, adapter.receiveGas(EnumFacing.NORTH, new GasStack(gas, 10_000), true));
        assertEquals(capacity, tank.getStored());

        GasTankInfo info = adapter.getTankInfo()[0];
        assertEquals(Integer.MAX_VALUE, info.getStored());
        assertEquals(Integer.MAX_VALUE, info.getMaxGas());
        assertEquals(Integer.MAX_VALUE, info.getGas().amount);

        GasStack simulated = adapter.drawGas(EnumFacing.SOUTH, 5_000, false);
        assertNotNull(simulated);
        assertEquals(5_000, simulated.amount);
        assertEquals(capacity, tank.getStored());

        GasStack extracted = adapter.drawGas(EnumFacing.SOUTH, 5_000, true);
        assertNotNull(extracted);
        assertEquals(gas, extracted.getGas());
        assertEquals(5_000, extracted.amount);
        assertEquals(capacity - 5_000, tank.getStored());
    }

    @Test
    public void gasIdentityAndSidesAreRespected() {
        Gas gas = new Gas("ultimate_test_input", 0xFFFFFF);
        Gas other = new Gas("ultimate_test_other", 0xFFFFFF);
        MekGasTankAdapter adapter = new MekGasTankAdapter(new LongChemicalTank(100),
              side -> side == EnumFacing.NORTH, side -> side == EnumFacing.SOUTH);

        assertFalse(adapter.canReceiveGas(EnumFacing.SOUTH, gas));
        assertEquals(0, adapter.receiveGas(EnumFacing.SOUTH, new GasStack(gas, 10), true));
        assertEquals(10, adapter.receiveGas(EnumFacing.NORTH, new GasStack(gas, 10), true));
        assertFalse(adapter.canReceiveGas(EnumFacing.NORTH, other));
        assertEquals(0, adapter.receiveGas(EnumFacing.NORTH, new GasStack(other, 10), true));
        assertFalse(adapter.canDrawGas(EnumFacing.NORTH, gas));
        assertTrue(adapter.canDrawGas(EnumFacing.SOUTH, gas));
        assertNull(adapter.drawGas(EnumFacing.NORTH, 10, true));
    }

    @Test
    public void gasBridgeSavesAndReloadsByStableGasName() {
        Gas gas = new Gas("ultimate_test_saved_gas", 0xFFFFFF);
        long amount = (long) Integer.MAX_VALUE + 123L;
        MekGasTankAdapter source = new MekGasTankAdapter(new LongChemicalTank(amount + 1));
        source.receiveGas(EnumFacing.NORTH, new GasStack(gas, Integer.MAX_VALUE), true);
        source.receiveGas(EnumFacing.NORTH, new GasStack(gas, 123), true);
        NBTTagCompound data = new NBTTagCompound();
        source.writeToNBT(data);

        MekGasTankAdapter loaded = new MekGasTankAdapter(new LongChemicalTank(amount + 1));
        loaded.readFromNBT(data, name -> gas.getName().equals(name) ? gas : null);

        assertEquals(amount, loaded.getTank().getStored());
        GasStack output = loaded.drawGas(EnumFacing.SOUTH, 200, true);
        assertNotNull(output);
        assertEquals(gas, output.getGas());
        assertEquals(200, output.amount);
        assertEquals(amount - 200, loaded.getTank().getStored());
    }
}
