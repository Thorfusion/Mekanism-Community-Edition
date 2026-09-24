package mekanism.nuclear.common.radiation;

import net.minecraft.nbt.NBTTagCompound;

/** Long-lived player radiation dose with checked arithmetic and versioned NBT. */
public final class RadiationExposure implements IRadiationExposure {

    public static final int DATA_VERSION = 1;
    private static final String NBT_VERSION = "version";
    private static final String NBT_RADIATION = "radiation";
    private static final double LOG_MIN = Math.log10(RadiationManager.MIN_MAGNITUDE);
    private static final double LOG_MAX = Math.log10(100D);
    private static final double SEVERITY_RANGE = LOG_MAX - LOG_MIN;

    private double radiation = RadiationManager.BASELINE;

    @Override
    public double getRadiation() {
        return radiation;
    }

    @Override
    public void setRadiation(double radiation) {
        if (Double.isNaN(radiation) || radiation <= RadiationManager.BASELINE) {
            this.radiation = RadiationManager.BASELINE;
        } else if (Double.isInfinite(radiation)) {
            this.radiation = Double.MAX_VALUE;
        } else {
            this.radiation = radiation;
        }
    }

    @Override
    public void radiate(double magnitude) {
        if (Double.isNaN(magnitude) || magnitude <= 0) {
            return;
        }
        if (Double.isInfinite(magnitude) || radiation > Double.MAX_VALUE - magnitude) {
            radiation = Double.MAX_VALUE;
        } else {
            radiation += magnitude;
        }
    }

    @Override
    public void decay(double decayRate) {
        if (Double.isNaN(decayRate)) {
            return;
        }
        double boundedRate = Math.max(0, Math.min(1, decayRate));
        setRadiation(radiation * boundedRate);
    }

    /** Stable logarithmic dose severity, normalized to the range zero through one. */
    public double getSeverity() {
        if (radiation < RadiationManager.MIN_MAGNITUDE) {
            return 0;
        }
        return Math.min(1, Math.max(0, (Math.log10(radiation) - LOG_MIN) / SEVERITY_RANGE));
    }

    public float getDamageStrength() {
        return Math.max(1, (float) Math.log1p(radiation));
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger(NBT_VERSION, DATA_VERSION);
        data.setDouble(NBT_RADIATION, radiation);
        return data;
    }

    @Override
    public void deserializeNBT(NBTTagCompound data) {
        setRadiation(data == null ? RadiationManager.BASELINE : data.getDouble(NBT_RADIATION));
    }
}
