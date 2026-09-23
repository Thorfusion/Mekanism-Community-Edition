package mekanism.common.inventory.container;

import javax.annotation.Nonnull;
import mekanism.common.content.matrix.SynchronizedMatrixData;
import mekanism.common.inventory.slot.SlotEnergy.SlotCharge;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.tile.TileEntityInductionCasing;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;

public class ContainerInductionMatrix extends ContainerEnergyStorage<TileEntityInductionCasing> {

    private static final int DOUBLE_PARTS = 4;
    private static final int DOUBLE_PROPERTIES = 5;
    private static final int FIRST_INT_PROPERTY = DOUBLE_PARTS * DOUBLE_PROPERTIES;

    private final long[] lastDoubleValues = new long[DOUBLE_PROPERTIES];
    private final int[] lastIntValues = new int[5];

    public ContainerInductionMatrix(InventoryPlayer inventory, TileEntityInductionCasing tile) {
        super(tile, inventory);
    }

    @Override
    public void addListener(@Nonnull IContainerListener listener) {
        super.addListener(listener);
        sendAllMatrixData(listener);
        cacheMatrixData();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        long[] doubleValues = getDoubleValues();
        int[] intValues = getIntValues();
        for (IContainerListener listener : listeners) {
            for (int valueIndex = 0; valueIndex < doubleValues.length; valueIndex++) {
                if (doubleValues[valueIndex] != lastDoubleValues[valueIndex]) {
                    sendDouble(listener, valueIndex, doubleValues[valueIndex]);
                }
            }
            for (int valueIndex = 0; valueIndex < intValues.length; valueIndex++) {
                if (intValues[valueIndex] != lastIntValues[valueIndex]) {
                    listener.sendWindowProperty(this, FIRST_INT_PROPERTY + valueIndex, intValues[valueIndex]);
                }
            }
        }
        System.arraycopy(doubleValues, 0, lastDoubleValues, 0, doubleValues.length);
        System.arraycopy(intValues, 0, lastIntValues, 0, intValues.length);
    }

    @Override
    public void updateProgressBar(int property, int value) {
        SynchronizedMatrixData structure = getOrCreateClientStructure();
        if (property < FIRST_INT_PROPERTY) {
            int valueIndex = property / DOUBLE_PARTS;
            int part = property % DOUBLE_PARTS;
            long oldBits = getDoubleValues()[valueIndex];
            int shift = part * Short.SIZE;
            long mask = 0xFFFFL << shift;
            long newBits = (oldBits & ~mask) | (((long) value & 0xFFFFL) << shift);
            setDoubleValue(structure, valueIndex, Double.longBitsToDouble(newBits));
        } else {
            setIntValue(structure, property - FIRST_INT_PROPERTY, value);
        }
    }

    private void sendAllMatrixData(IContainerListener listener) {
        long[] doubleValues = getDoubleValues();
        for (int valueIndex = 0; valueIndex < doubleValues.length; valueIndex++) {
            sendDouble(listener, valueIndex, doubleValues[valueIndex]);
        }
        int[] intValues = getIntValues();
        for (int valueIndex = 0; valueIndex < intValues.length; valueIndex++) {
            listener.sendWindowProperty(this, FIRST_INT_PROPERTY + valueIndex, intValues[valueIndex]);
        }
    }

    private void sendDouble(IContainerListener listener, int valueIndex, long value) {
        int firstProperty = valueIndex * DOUBLE_PARTS;
        for (int part = 0; part < DOUBLE_PARTS; part++) {
            listener.sendWindowProperty(this, firstProperty + part, (int) (value >>> (part * Short.SIZE) & 0xFFFFL));
        }
    }

    private void cacheMatrixData() {
        long[] doubleValues = getDoubleValues();
        int[] intValues = getIntValues();
        System.arraycopy(doubleValues, 0, lastDoubleValues, 0, doubleValues.length);
        System.arraycopy(intValues, 0, lastIntValues, 0, intValues.length);
    }

    private long[] getDoubleValues() {
        SynchronizedMatrixData structure = tileEntity.structure;
        return new long[]{
              Double.doubleToRawLongBits(structure == null ? 0 : structure.getEnergy()),
              Double.doubleToRawLongBits(structure == null ? 0 : structure.getStorageCap()),
              Double.doubleToRawLongBits(structure == null ? 0 : structure.getTransferCap()),
              Double.doubleToRawLongBits(structure == null ? 0 : structure.getLastInput()),
              Double.doubleToRawLongBits(structure == null ? 0 : structure.getLastOutput())
        };
    }

    private int[] getIntValues() {
        SynchronizedMatrixData structure = tileEntity.structure;
        return new int[]{
              structure == null ? 0 : structure.volWidth,
              structure == null ? 0 : structure.volHeight,
              structure == null ? 0 : structure.volLength,
              structure == null ? 0 : structure.getCellCount(),
              structure == null ? 0 : structure.getProviderCount()
        };
    }

    private SynchronizedMatrixData getOrCreateClientStructure() {
        if (tileEntity.structure == null) {
            tileEntity.structure = new SynchronizedMatrixData();
        }
        return tileEntity.structure;
    }

    private void setDoubleValue(SynchronizedMatrixData structure, int valueIndex, double value) {
        switch (valueIndex) {
            case 0:
                structure.setCachedTotal(value);
                break;
            case 1:
                structure.setStorageCap(value);
                break;
            case 2:
                structure.setTransferCap(value);
                break;
            case 3:
                structure.setLastInput(value);
                break;
            case 4:
                structure.setLastOutput(value);
                break;
            default:
        }
    }

    private void setIntValue(SynchronizedMatrixData structure, int valueIndex, int value) {
        switch (valueIndex) {
            case 0:
                structure.volWidth = value;
                break;
            case 1:
                structure.volHeight = value;
                break;
            case 2:
                structure.volLength = value;
                break;
            case 3:
                structure.setClientCells(value);
                break;
            case 4:
                structure.setClientProviders(value);
                break;
            default:
        }
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new SlotCharge(tileEntity, 0, 146, 20));
        addSlotToContainer(new SlotDischarge(tileEntity, 1, 146, 51));
    }
}
