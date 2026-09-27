package mekanism.common.content.tank;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Module-neutral chemical identity stored by a Dynamic Tank. Ultimate resolves
 * the stable kind/name pair into its chemical facade only at the boundary.
 */
public final class DynamicTankChemicalStack {

    private static final int DATA_VERSION = 1;
    private static final int MAX_ID_LENGTH = 256;

    public final String kind;
    public final String registryName;
    public long amount;

    public DynamicTankChemicalStack(String kind, String registryName, long amount) {
        if (!isValidId(kind) || !isValidId(registryName)) {
            throw new IllegalArgumentException("Invalid Dynamic Tank chemical identity");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Chemical amount must be positive: " + amount);
        }
        this.kind = kind;
        this.registryName = registryName;
        this.amount = amount;
    }

    public DynamicTankChemicalStack copy() {
        return new DynamicTankChemicalStack(kind, registryName, amount);
    }

    public boolean isSameType(DynamicTankChemicalStack other) {
        return other != null && kind.equals(other.kind) && registryName.equals(other.registryName);
    }

    public NBTTagCompound write(NBTTagCompound data) {
        data.setInteger("dataVersion", DATA_VERSION);
        data.setString("kind", kind);
        data.setString("name", registryName);
        data.setLong("amount", amount);
        return data;
    }

    @Nullable
    public static DynamicTankChemicalStack read(NBTTagCompound data) {
        if (data == null || data.getInteger("dataVersion") != DATA_VERSION) {
            return null;
        }
        String kind = data.getString("kind");
        String name = data.getString("name");
        long amount = data.getLong("amount");
        return isValidId(kind) && isValidId(name) && amount > 0
              ? new DynamicTankChemicalStack(kind, name, amount) : null;
    }

    public static boolean isValidId(String value) {
        return value != null && !value.isEmpty() && value.length() <= MAX_ID_LENGTH;
    }
}
