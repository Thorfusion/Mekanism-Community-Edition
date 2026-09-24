package mekanism.nuclear.common.radiation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/** A lazily decayed environmental radiation source measured in Sv/h. */
public final class RadiationSource {

    private static final String NBT_POSITION = "Position";
    private static final String NBT_MAGNITUDE = "Magnitude";
    private static final String NBT_LAST_DECAY_TICK = "LastDecayTick";
    private static final long DECAY_INTERVAL = 20;

    private final BlockPos position;
    private double magnitude;
    private long lastDecayTick;

    public RadiationSource(BlockPos position, double magnitude, long gameTime) {
        if (position == null) {
            throw new NullPointerException("position");
        }
        this.position = position.toImmutable();
        this.magnitude = Math.max(0, magnitude);
        lastDecayTick = gameTime;
    }

    public BlockPos getPosition() {
        return position;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public long getLastDecayTick() {
        return lastDecayTick;
    }

    public void add(double amount) {
        if (amount > 0) {
            magnitude += amount;
        }
    }

    /**
     * Applies all elapsed whole-second decay steps without requiring a global
     * per-tick scan.
     *
     * @return {@code true} when the source fell below the removal threshold
     */
    public boolean decayTo(long gameTime, double decayRate, double minimumMagnitude) {
        long elapsed = Math.max(0, gameTime - lastDecayTick);
        long operations = elapsed / DECAY_INTERVAL;
        if (operations > 0) {
            magnitude *= Math.pow(decayRate, operations);
            lastDecayTick += operations * DECAY_INTERVAL;
        }
        return magnitude < minimumMagnitude;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound data = new NBTTagCompound();
        data.setLong(NBT_POSITION, position.toLong());
        data.setDouble(NBT_MAGNITUDE, magnitude);
        data.setLong(NBT_LAST_DECAY_TICK, lastDecayTick);
        return data;
    }

    public static RadiationSource deserializeNBT(NBTTagCompound data) {
        if (data == null || !data.hasKey(NBT_POSITION) || !data.hasKey(NBT_MAGNITUDE)) {
            return null;
        }
        return new RadiationSource(BlockPos.fromLong(data.getLong(NBT_POSITION)),
              data.getDouble(NBT_MAGNITUDE), data.getLong(NBT_LAST_DECAY_TICK));
    }
}
