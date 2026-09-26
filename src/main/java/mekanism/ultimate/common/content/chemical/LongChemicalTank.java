package mekanism.ultimate.common.content.chemical;

import java.util.Objects;
import javax.annotation.Nullable;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.api.chemical.IChemicalTypeResolverCE;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Single-type, long-backed chemical storage.
 */
public class LongChemicalTank implements IChemicalTankCE {

    public static final int DATA_VERSION = 1;

    private static final String NBT_DATA_VERSION = "UMDataVersion";
    private static final String NBT_KIND = "ChemicalKind";
    private static final String NBT_NAME = "ChemicalName";
    private static final String NBT_AMOUNT = "Amount";

    private long capacity;

    @Nullable
    private ChemicalStackCE stored;

    public LongChemicalTank(long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Chemical capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
    }

    @Nullable
    @Override
    public ChemicalStackCE getStack() {
        return stored;
    }

    @Override
    public long getStored() {
        return stored == null ? 0 : stored.getAmount();
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    /**
     * Resizes this tank without ever exposing an amount above the new capacity.
     * This is primarily used by tiered blocks after config reload or a tier
     * installer upgrade.
     */
    public void setCapacity(long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Chemical capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
        if (stored != null && stored.getAmount() > capacity) {
            stored = stored.copyWithAmount(capacity);
        }
    }

    @Override
    public long insert(IChemicalStackCE stack, Action action) {
        Objects.requireNonNull(stack, "stack");
        Objects.requireNonNull(action, "action");
        validateAmount(stack.getAmount());
        if (stack.isEmpty() || stored != null && !isSameType(stored.getType(), stack.getType())) {
            return 0;
        }

        long accepted = Math.min(capacity - getStored(), stack.getAmount());
        if (accepted > 0 && action.execute()) {
            stored = new ChemicalStackCE(stack.getType(), getStored() + accepted);
        }
        return accepted;
    }

    @Nullable
    @Override
    public ChemicalStackCE extract(long amount, Action action) {
        Objects.requireNonNull(action, "action");
        validateAmount(amount);
        if (amount == 0 || stored == null) {
            return null;
        }

        long extracted = Math.min(amount, stored.getAmount());
        ChemicalStackCE result = stored.copyWithAmount(extracted);
        if (action.execute()) {
            long remaining = stored.getAmount() - extracted;
            stored = remaining == 0 ? null : stored.copyWithAmount(remaining);
        }
        return result;
    }

    public void clear() {
        stored = null;
    }

    public void writeToNBT(NBTTagCompound data) {
        Objects.requireNonNull(data, "data");
        data.setInteger(NBT_DATA_VERSION, DATA_VERSION);
        if (stored != null) {
            data.setString(NBT_KIND, stored.getType().getKind().name());
            data.setString(NBT_NAME, stored.getType().getRegistryName());
            data.setLong(NBT_AMOUNT, stored.getAmount());
        } else {
            data.removeTag(NBT_KIND);
            data.removeTag(NBT_NAME);
            data.removeTag(NBT_AMOUNT);
        }
    }

    public void readFromNBT(NBTTagCompound data, IChemicalTypeResolverCE resolver) {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(resolver, "resolver");
        stored = null;

        int version = data.getInteger(NBT_DATA_VERSION);
        if (version != DATA_VERSION || !data.hasKey(NBT_KIND) || !data.hasKey(NBT_NAME) || !data.hasKey(NBT_AMOUNT)) {
            return;
        }

        long amount = data.getLong(NBT_AMOUNT);
        if (amount <= 0) {
            return;
        }

        ChemicalKind kind;
        try {
            kind = ChemicalKind.valueOf(data.getString(NBT_KIND));
        } catch (IllegalArgumentException ignored) {
            return;
        }

        IChemicalTypeCE type = resolver.resolve(kind, data.getString(NBT_NAME));
        if (type != null && type.getKind() == kind && type.getRegistryName().equals(data.getString(NBT_NAME))) {
            stored = new ChemicalStackCE(type, Math.min(amount, capacity));
        }
    }

    public static boolean isSameType(IChemicalTypeCE first, IChemicalTypeCE second) {
        return first == second || first != null && second != null && first.getKind() == second.getKind()
              && first.getRegistryName().equals(second.getRegistryName());
    }

    private static void validateAmount(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Chemical amount cannot be negative: " + amount);
        }
    }
}
