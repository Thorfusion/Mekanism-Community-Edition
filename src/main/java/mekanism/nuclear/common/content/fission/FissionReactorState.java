package mekanism.nuclear.common.content.fission;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/** Persisted controller state shared by the first fission-reactor slice. */
public final class FissionReactorState {

    private static final int DATA_VERSION = 1;

    private boolean formed;
    private BlockPos min;
    private BlockPos max;
    private int volume;
    private int fuelAssemblies;
    private int controlRods;
    private int surfaceArea;

    private long fissileFuel;
    private long coolant;
    private long heatedCoolant;
    private long nuclearWaste;
    private CoolantType coolantType = CoolantType.NONE;
    private double heat;
    private double damage;
    private double burnRate;
    private boolean active;

    public void applyStructure(FissionReactorValidator.Result result) {
        if (!result.isFormed()) {
            throw new IllegalArgumentException("Cannot apply an invalid fission reactor structure");
        }
        formed = true;
        min = result.getMin();
        max = result.getMax();
        volume = result.getVolume();
        fuelAssemblies = result.getFuelAssemblies();
        controlRods = result.getControlRods();
        surfaceArea = result.getSurfaceArea();
    }

    public void setUnformed() {
        formed = false;
        active = false;
    }

    public boolean isFormed() {
        return formed;
    }

    public boolean hasBounds() {
        return min != null && max != null;
    }

    public boolean watches(BlockPos pos) {
        return hasBounds() && pos.getX() >= min.getX() - 1 && pos.getX() <= max.getX() + 1
              && pos.getY() >= min.getY() - 1 && pos.getY() <= max.getY() + 1
              && pos.getZ() >= min.getZ() - 1 && pos.getZ() <= max.getZ() + 1;
    }

    public boolean watchesChunk(int chunkX, int chunkZ) {
        return hasBounds() && chunkX >= (min.getX() >> 4) && chunkX <= (max.getX() >> 4)
              && chunkZ >= (min.getZ() >> 4) && chunkZ <= (max.getZ() >> 4);
    }

    @Nullable
    public BlockPos getMin() {
        return min;
    }

    @Nullable
    public BlockPos getMax() {
        return max;
    }

    public int getVolume() {
        return volume;
    }

    public int getFuelAssemblies() {
        return fuelAssemblies;
    }

    public int getControlRods() {
        return controlRods;
    }

    public int getSurfaceArea() {
        return surfaceArea;
    }

    public long getFissileFuel() {
        return fissileFuel;
    }

    public void setFissileFuel(long fissileFuel) {
        this.fissileFuel = nonNegative(fissileFuel);
    }

    public long getCoolant() {
        return coolant;
    }

    public void setCoolant(long coolant) {
        this.coolant = nonNegative(coolant);
    }

    public long getHeatedCoolant() {
        return heatedCoolant;
    }

    public void setHeatedCoolant(long heatedCoolant) {
        this.heatedCoolant = nonNegative(heatedCoolant);
    }

    public long getNuclearWaste() {
        return nuclearWaste;
    }

    public void setNuclearWaste(long nuclearWaste) {
        this.nuclearWaste = nonNegative(nuclearWaste);
    }

    public CoolantType getCoolantType() {
        return coolantType;
    }

    public void setCoolantType(@Nullable CoolantType coolantType) {
        this.coolantType = coolantType == null ? CoolantType.NONE : coolantType;
    }

    public double getHeat() {
        return heat;
    }

    public void setHeat(double heat) {
        this.heat = finiteNonNegative(heat);
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = finiteNonNegative(damage);
    }

    public double getBurnRate() {
        return burnRate;
    }

    public void setBurnRate(double burnRate) {
        this.burnRate = finiteNonNegative(burnRate);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active && formed;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("Version", DATA_VERSION);
        tag.setBoolean("Formed", formed);
        if (hasBounds()) {
            tag.setTag("Min", writePos(min));
            tag.setTag("Max", writePos(max));
        }
        tag.setInteger("Volume", volume);
        tag.setInteger("FuelAssemblies", fuelAssemblies);
        tag.setInteger("ControlRods", controlRods);
        tag.setInteger("SurfaceArea", surfaceArea);
        tag.setLong("FissileFuel", fissileFuel);
        tag.setLong("Coolant", coolant);
        tag.setLong("HeatedCoolant", heatedCoolant);
        tag.setLong("NuclearWaste", nuclearWaste);
        tag.setString("CoolantType", coolantType.name());
        tag.setDouble("Heat", heat);
        tag.setDouble("Damage", damage);
        tag.setDouble("BurnRate", burnRate);
        tag.setBoolean("Active", active);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        min = tag.hasKey("Min", 10) ? readPos(tag.getCompoundTag("Min")) : null;
        max = tag.hasKey("Max", 10) ? readPos(tag.getCompoundTag("Max")) : null;
        formed = tag.getBoolean("Formed") && hasBounds();
        volume = nonNegative(tag.getInteger("Volume"));
        fuelAssemblies = nonNegative(tag.getInteger("FuelAssemblies"));
        controlRods = nonNegative(tag.getInteger("ControlRods"));
        surfaceArea = nonNegative(tag.getInteger("SurfaceArea"));
        fissileFuel = nonNegative(tag.getLong("FissileFuel"));
        coolant = nonNegative(tag.getLong("Coolant"));
        heatedCoolant = nonNegative(tag.getLong("HeatedCoolant"));
        nuclearWaste = nonNegative(tag.getLong("NuclearWaste"));
        try {
            coolantType = CoolantType.valueOf(tag.getString("CoolantType"));
        } catch (IllegalArgumentException ignored) {
            coolantType = CoolantType.NONE;
        }
        heat = finiteNonNegative(tag.getDouble("Heat"));
        damage = finiteNonNegative(tag.getDouble("Damage"));
        burnRate = finiteNonNegative(tag.getDouble("BurnRate"));
        active = formed && tag.getBoolean("Active");
    }

    private static NBTTagCompound writePos(BlockPos pos) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("X", pos.getX());
        tag.setInteger("Y", pos.getY());
        tag.setInteger("Z", pos.getZ());
        return tag;
    }

    private static BlockPos readPos(NBTTagCompound tag) {
        return new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
    }

    private static int nonNegative(int value) {
        return Math.max(0, value);
    }

    private static long nonNegative(long value) {
        return Math.max(0, value);
    }

    private static double finiteNonNegative(double value) {
        return Double.isFinite(value) ? Math.max(0, value) : 0;
    }

    public enum CoolantType {
        NONE,
        WATER,
        SODIUM
    }
}
