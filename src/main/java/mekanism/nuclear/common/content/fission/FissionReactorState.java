package mekanism.nuclear.common.content.fission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mekanism.nuclear.common.config.NuclearFissionConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

/** Long-backed, server-authoritative state for a formed Fission Reactor. */
public final class FissionReactorState {

    private static final int DATA_VERSION = 2;

    private boolean formed;
    private BlockPos min;
    private BlockPos max;
    private int volume;
    private int exteriorBlocks;
    private int fuelAssemblies;
    private int controlRods;
    private int surfaceArea;
    private List<BlockPos> ports = Collections.emptyList();
    private List<BlockPos> logicAdapters = Collections.emptyList();

    private long fissileFuel;
    private long coolant;
    private long heatedCoolant;
    private long nuclearWaste;
    private CoolantType coolantType = CoolantType.NONE;
    private HeatedCoolantType heatedCoolantType = HeatedCoolantType.NONE;
    private double heat;
    private double damage;
    private double burnRate = NuclearFissionConfig.getDefaultBurnRate();
    private double burnRemaining;
    private double partialWaste;
    private double lastBurnRate;
    private long lastBoilRate;
    private double lastEnvironmentLoss;
    private long pendingRadiation;
    private boolean active;
    private boolean forceDisabled;

    public void applyStructure(FissionReactorValidator.Result result) {
        if (!result.isFormed()) {
            throw new IllegalArgumentException("Cannot apply an invalid fission reactor structure");
        }
        boolean initializeHeat = !hasBounds() || heat <= 0;
        formed = true;
        min = result.getMin();
        max = result.getMax();
        volume = result.getVolume();
        exteriorBlocks = result.getExteriorBlocks();
        fuelAssemblies = result.getFuelAssemblies();
        controlRods = result.getControlRods();
        surfaceArea = result.getSurfaceArea();
        ports = Collections.unmodifiableList(new ArrayList<>(result.getPorts()));
        logicAdapters = Collections.unmodifiableList(new ArrayList<>(result.getLogicAdapters()));
        clampContents();
        setBurnRate(burnRate <= 0 ? NuclearFissionConfig.getDefaultBurnRate() : burnRate);
        if (initializeHeat) {
            heat = NuclearFissionConfig.AMBIENT_TEMPERATURE * getHeatCapacity();
        }
    }

    /** Copies tank and operating values after this state has received the new structure. */
    public void copyOperationalFrom(FissionReactorState other) {
        if (other == null || other == this) {
            return;
        }
        fissileFuel = Math.min(other.fissileFuel, getFuelCapacity());
        nuclearWaste = Math.min(other.nuclearWaste, getWasteCapacity());
        coolantType = other.coolantType;
        coolant = Math.min(other.coolant, getCoolantCapacity());
        heatedCoolantType = other.heatedCoolantType;
        heatedCoolant = Math.min(other.heatedCoolant, getHeatedCoolantCapacity());
        heat = finiteNonNegative(other.heat);
        damage = finiteNonNegative(other.damage);
        burnRate = Math.min(finiteNonNegative(other.burnRate), getMaxBurnRate());
        burnRemaining = fraction(other.burnRemaining);
        partialWaste = fraction(other.partialWaste);
        lastBurnRate = finiteNonNegative(other.lastBurnRate);
        lastBoilRate = nonNegative(other.lastBoilRate);
        lastEnvironmentLoss = finiteNonNegative(other.lastEnvironmentLoss);
        pendingRadiation = nonNegative(other.pendingRadiation);
        forceDisabled = other.forceDisabled;
        active = other.active && !forceDisabled;
        clearEmptyTypes();
    }

    /** Removes duplicated operating data from a non-controller port. */
    public void clearOperationalState() {
        fissileFuel = coolant = heatedCoolant = nuclearWaste = pendingRadiation = 0;
        coolantType = CoolantType.NONE;
        heatedCoolantType = HeatedCoolantType.NONE;
        heat = NuclearFissionConfig.AMBIENT_TEMPERATURE * getHeatCapacity();
        damage = burnRemaining = partialWaste = lastBurnRate = lastEnvironmentLoss = 0;
        lastBoilRate = 0;
        active = false;
        forceDisabled = false;
        burnRate = Math.min(NuclearFissionConfig.getDefaultBurnRate(), getMaxBurnRate());
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

    public BlockPos getCenter() {
        if (!hasBounds()) {
            return BlockPos.ORIGIN;
        }
        return new BlockPos((min.getX() + max.getX()) / 2, (min.getY() + max.getY()) / 2,
              (min.getZ() + max.getZ()) / 2);
    }

    public int getVolume() {
        return volume;
    }

    public int getExteriorBlocks() {
        return exteriorBlocks;
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

    public List<BlockPos> getPorts() {
        return ports;
    }

    public List<BlockPos> getLogicAdapters() {
        return logicAdapters;
    }

    public int getWidth() {
        return hasBounds() ? max.getX() - min.getX() + 1 : 0;
    }

    public int getHeight() {
        return hasBounds() ? max.getY() - min.getY() + 1 : 0;
    }

    public int getLength() {
        return hasBounds() ? max.getZ() - min.getZ() + 1 : 0;
    }

    public boolean isPositionOutsideBounds(BlockPos position) {
        return !hasBounds() || position.getX() < min.getX() || position.getX() > max.getX()
              || position.getY() < min.getY() || position.getY() > max.getY()
              || position.getZ() < min.getZ() || position.getZ() > max.getZ();
    }

    public long getFuelCapacity() {
        return saturatedMultiply(fuelAssemblies, NuclearFissionConfig.getMaxFuelPerAssembly());
    }

    public long getWasteCapacity() {
        return getFuelCapacity();
    }

    public long getCoolantCapacity() {
        return saturatedMultiply(volume, NuclearFissionConfig.getCooledCoolantPerVolume());
    }

    public long getHeatedCoolantCapacity() {
        return saturatedMultiply(volume, NuclearFissionConfig.getHeatedCoolantPerVolume());
    }

    public double getHeatCapacity() {
        return Math.max(1D, exteriorBlocks * NuclearFissionConfig.getCasingHeatCapacity());
    }

    public double getMaxBurnRate() {
        return saturatedMultiply(fuelAssemblies, NuclearFissionConfig.getBurnPerAssembly());
    }

    public double getBoilEfficiency() {
        if (fuelAssemblies <= 0) {
            return 0;
        }
        return Math.min(1D, surfaceArea / (fuelAssemblies * NuclearFissionConfig.getSurfaceAreaTarget()));
    }

    public long getFissileFuel() {
        return fissileFuel;
    }

    public void setFissileFuel(long amount) {
        fissileFuel = Math.min(nonNegative(amount), getFuelCapacity());
    }

    public long insertFissileFuel(long amount, boolean execute) {
        long accepted = accepted(amount, fissileFuel, getFuelCapacity());
        if (execute) {
            fissileFuel += accepted;
        }
        return accepted;
    }

    public long extractFissileFuel(long amount, boolean execute) {
        long extracted = Math.min(nonNegative(amount), fissileFuel);
        if (execute) {
            fissileFuel -= extracted;
        }
        return extracted;
    }

    public long getCoolant() {
        return coolant;
    }

    public void setCoolant(long amount) {
        coolant = Math.min(nonNegative(amount), getCoolantCapacity());
        clearEmptyTypes();
    }

    public long insertCoolant(CoolantType type, long amount, boolean execute) {
        if (type == null || type == CoolantType.NONE || coolant > 0 && coolantType != type) {
            return 0;
        }
        long accepted = accepted(amount, coolant, getCoolantCapacity());
        if (execute && accepted > 0) {
            coolantType = type;
            coolant += accepted;
        }
        return accepted;
    }

    public long extractCoolant(long amount, boolean execute) {
        long extracted = Math.min(nonNegative(amount), coolant);
        if (execute) {
            coolant -= extracted;
            clearEmptyTypes();
        }
        return extracted;
    }

    public long getHeatedCoolant() {
        return heatedCoolant;
    }

    public void setHeatedCoolant(long amount) {
        heatedCoolant = Math.min(nonNegative(amount), getHeatedCoolantCapacity());
        clearEmptyTypes();
    }

    /** Returns accepted output; callers intentionally discard overflow like stable Mekanism. */
    public long insertHeatedCoolant(HeatedCoolantType type, long amount, boolean execute) {
        if (type == null || type == HeatedCoolantType.NONE
              || heatedCoolant > 0 && heatedCoolantType != type) {
            return 0;
        }
        long accepted = accepted(amount, heatedCoolant, getHeatedCoolantCapacity());
        if (execute && accepted > 0) {
            heatedCoolantType = type;
            heatedCoolant += accepted;
        }
        return accepted;
    }

    public long extractHeatedCoolant(long amount, boolean execute) {
        long extracted = Math.min(nonNegative(amount), heatedCoolant);
        if (execute) {
            heatedCoolant -= extracted;
            clearEmptyTypes();
        }
        return extracted;
    }

    public long getNuclearWaste() {
        return nuclearWaste;
    }

    public void setNuclearWaste(long amount) {
        nuclearWaste = Math.min(nonNegative(amount), getWasteCapacity());
    }

    public long insertNuclearWaste(long amount, boolean execute) {
        long accepted = accepted(amount, nuclearWaste, getWasteCapacity());
        if (execute) {
            nuclearWaste += accepted;
        }
        return accepted;
    }

    public long extractNuclearWaste(long amount, boolean execute) {
        long extracted = Math.min(nonNegative(amount), nuclearWaste);
        if (execute) {
            nuclearWaste -= extracted;
        }
        return extracted;
    }

    public CoolantType getCoolantType() {
        return coolantType;
    }

    public void setCoolantType(@Nullable CoolantType type) {
        coolantType = type == null ? CoolantType.NONE : type;
        if (coolantType == CoolantType.NONE) {
            coolant = 0;
        }
    }

    public HeatedCoolantType getHeatedCoolantType() {
        return heatedCoolantType;
    }

    public double getHeat() {
        return heat;
    }

    public void setHeat(double heat) {
        this.heat = finiteNonNegative(heat);
    }

    public void addHeat(double amount) {
        setHeat(heat + amount);
    }

    public double getTemperature() {
        return heat / getHeatCapacity();
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
        this.burnRate = Math.min(finiteNonNegative(burnRate), getMaxBurnRate());
    }

    public double getBurnRemaining() {
        return burnRemaining;
    }

    public void setBurnRemaining(double burnRemaining) {
        this.burnRemaining = fraction(burnRemaining);
    }

    public double getPartialWaste() {
        return partialWaste;
    }

    public void setPartialWaste(double partialWaste) {
        this.partialWaste = fraction(partialWaste);
    }

    public double getLastBurnRate() {
        return lastBurnRate;
    }

    public void setLastBurnRate(double lastBurnRate) {
        this.lastBurnRate = finiteNonNegative(lastBurnRate);
    }

    public long getLastBoilRate() {
        return lastBoilRate;
    }

    public void setLastBoilRate(long lastBoilRate) {
        this.lastBoilRate = nonNegative(lastBoilRate);
    }

    public double getLastEnvironmentLoss() {
        return lastEnvironmentLoss;
    }

    public void setLastEnvironmentLoss(double lastEnvironmentLoss) {
        this.lastEnvironmentLoss = finiteNonNegative(lastEnvironmentLoss);
    }

    public void addPendingRadiation(long amount) {
        pendingRadiation = saturatedAdd(pendingRadiation, nonNegative(amount));
    }

    public long consumePendingRadiation() {
        long amount = pendingRadiation;
        pendingRadiation = 0;
        return amount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active && formed && !forceDisabled;
    }

    public boolean isForceDisabled() {
        return forceDisabled;
    }

    public void setForceDisabled(boolean forceDisabled) {
        this.forceDisabled = forceDisabled;
        if (forceDisabled) {
            active = false;
        }
    }

    /** Resets volatile contents and returns the scaled waste release for the world radiation system. */
    public long applyMeltdown() {
        long radioactiveRelease = saturatedMultiply(nuclearWaste + partialWaste,
              NuclearFissionConfig.getMeltdownRadiationMultiplier());
        heatedCoolant = nuclearWaste = 0;
        heatedCoolantType = HeatedCoolantType.NONE;
        burnRemaining = partialWaste = lastBurnRate = lastEnvironmentLoss = 0;
        lastBoilRate = 0;
        active = false;
        forceDisabled = false;
        damage = NuclearFissionConfig.getPostMeltdownDamage();
        heat = NuclearFissionConfig.AMBIENT_TEMPERATURE * getHeatCapacity();
        return radioactiveRelease;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("Version", DATA_VERSION);
        tag.setBoolean("Formed", formed);
        if (hasBounds()) {
            tag.setTag("Min", writePos(min));
            tag.setTag("Max", writePos(max));
        }
        tag.setInteger("Volume", volume);
        tag.setInteger("ExteriorBlocks", exteriorBlocks);
        tag.setInteger("FuelAssemblies", fuelAssemblies);
        tag.setInteger("ControlRods", controlRods);
        tag.setInteger("SurfaceArea", surfaceArea);
        NBTTagList portList = new NBTTagList();
        for (BlockPos port : ports) {
            portList.appendTag(writePos(port));
        }
        tag.setTag("Ports", portList);
        NBTTagList logicList = new NBTTagList();
        for (BlockPos adapter : logicAdapters) {
            logicList.appendTag(writePos(adapter));
        }
        tag.setTag("LogicAdapters", logicList);
        tag.setLong("FissileFuel", fissileFuel);
        tag.setLong("Coolant", coolant);
        tag.setLong("HeatedCoolant", heatedCoolant);
        tag.setLong("NuclearWaste", nuclearWaste);
        tag.setString("CoolantType", coolantType.name());
        tag.setString("HeatedCoolantType", heatedCoolantType.name());
        tag.setDouble("Heat", heat);
        tag.setDouble("Damage", damage);
        tag.setDouble("BurnRate", burnRate);
        tag.setDouble("BurnRemaining", burnRemaining);
        tag.setDouble("PartialWaste", partialWaste);
        tag.setDouble("LastBurnRate", lastBurnRate);
        tag.setLong("LastBoilRate", lastBoilRate);
        tag.setDouble("LastEnvironmentLoss", lastEnvironmentLoss);
        tag.setLong("PendingRadiation", pendingRadiation);
        tag.setBoolean("Active", active);
        tag.setBoolean("ForceDisabled", forceDisabled);
        return tag;
    }

    public void readFromNBT(NBTTagCompound tag) {
        int version = tag.getInteger("Version");
        min = tag.hasKey("Min", 10) ? readPos(tag.getCompoundTag("Min")) : null;
        max = tag.hasKey("Max", 10) ? readPos(tag.getCompoundTag("Max")) : null;
        formed = tag.getBoolean("Formed") && hasBounds();
        volume = nonNegative(tag.getInteger("Volume"));
        exteriorBlocks = nonNegative(tag.getInteger("ExteriorBlocks"));
        if (version < 2 && exteriorBlocks == 0 && hasBounds()) {
            int width = max.getX() - min.getX() + 1;
            int height = max.getY() - min.getY() + 1;
            int length = max.getZ() - min.getZ() + 1;
            exteriorBlocks = volume - Math.max(0, width - 2) * Math.max(0, height - 2) * Math.max(0, length - 2);
        }
        fuelAssemblies = nonNegative(tag.getInteger("FuelAssemblies"));
        controlRods = nonNegative(tag.getInteger("ControlRods"));
        surfaceArea = nonNegative(tag.getInteger("SurfaceArea"));
        NBTTagList portList = tag.getTagList("Ports", 10);
        List<BlockPos> loadedPorts = new ArrayList<>(portList.tagCount());
        for (int i = 0; i < portList.tagCount(); i++) {
            loadedPorts.add(readPos(portList.getCompoundTagAt(i)));
        }
        ports = Collections.unmodifiableList(loadedPorts);
        NBTTagList logicList = tag.getTagList("LogicAdapters", 10);
        List<BlockPos> loadedLogicAdapters = new ArrayList<>(logicList.tagCount());
        for (int i = 0; i < logicList.tagCount(); i++) {
            loadedLogicAdapters.add(readPos(logicList.getCompoundTagAt(i)));
        }
        logicAdapters = Collections.unmodifiableList(loadedLogicAdapters);
        fissileFuel = nonNegative(tag.getLong("FissileFuel"));
        coolant = nonNegative(tag.getLong("Coolant"));
        heatedCoolant = nonNegative(tag.getLong("HeatedCoolant"));
        nuclearWaste = nonNegative(tag.getLong("NuclearWaste"));
        coolantType = enumValue(CoolantType.class, tag.getString("CoolantType"), CoolantType.NONE);
        heatedCoolantType = enumValue(HeatedCoolantType.class, tag.getString("HeatedCoolantType"),
              HeatedCoolantType.NONE);
        if (version < 2 && heatedCoolant > 0 && heatedCoolantType == HeatedCoolantType.NONE) {
            heatedCoolantType = coolantType == CoolantType.SODIUM
                  ? HeatedCoolantType.SUPERHEATED_SODIUM : HeatedCoolantType.STEAM;
        }
        heat = finiteNonNegative(tag.getDouble("Heat"));
        damage = finiteNonNegative(tag.getDouble("Damage"));
        burnRate = finiteNonNegative(tag.hasKey("BurnRate") ? tag.getDouble("BurnRate")
              : NuclearFissionConfig.getDefaultBurnRate());
        burnRemaining = fraction(tag.getDouble("BurnRemaining"));
        partialWaste = fraction(tag.getDouble("PartialWaste"));
        lastBurnRate = finiteNonNegative(tag.getDouble("LastBurnRate"));
        lastBoilRate = nonNegative(tag.getLong("LastBoilRate"));
        lastEnvironmentLoss = finiteNonNegative(tag.getDouble("LastEnvironmentLoss"));
        pendingRadiation = nonNegative(tag.getLong("PendingRadiation"));
        forceDisabled = tag.getBoolean("ForceDisabled");
        active = formed && !forceDisabled && tag.getBoolean("Active");
        clampContents();
        setBurnRate(burnRate);
        if (formed && heat <= 0) {
            heat = NuclearFissionConfig.AMBIENT_TEMPERATURE * getHeatCapacity();
        }
    }

    private void clampContents() {
        fissileFuel = Math.min(fissileFuel, getFuelCapacity());
        nuclearWaste = Math.min(nuclearWaste, getWasteCapacity());
        coolant = Math.min(coolant, getCoolantCapacity());
        heatedCoolant = Math.min(heatedCoolant, getHeatedCoolantCapacity());
        clearEmptyTypes();
    }

    private void clearEmptyTypes() {
        if (coolant == 0) {
            coolantType = CoolantType.NONE;
        }
        if (heatedCoolant == 0) {
            heatedCoolantType = HeatedCoolantType.NONE;
        }
    }

    private static long accepted(long requested, long stored, long capacity) {
        return Math.min(nonNegative(requested), Math.max(0, capacity - stored));
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

    private static double fraction(double value) {
        return Double.isFinite(value) ? Math.max(0, Math.min(Math.nextDown(1D), value % 1D)) : 0;
    }

    private static long saturatedMultiply(long first, long second) {
        if (first <= 0 || second <= 0) {
            return 0;
        }
        return first > Long.MAX_VALUE / second ? Long.MAX_VALUE : first * second;
    }

    private static long saturatedMultiply(long value, double multiplier) {
        double result = value * multiplier;
        return !Double.isFinite(result) || result >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) Math.max(0, result);
    }

    private static long saturatedMultiply(double value, double multiplier) {
        double result = value * multiplier;
        return !Double.isFinite(result) || result >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) Math.max(0, result);
    }

    private static long saturatedAdd(long first, long second) {
        return second > 0 && first > Long.MAX_VALUE - second ? Long.MAX_VALUE : first + second;
    }

    private static <E extends Enum<E>> E enumValue(Class<E> type, String name, E fallback) {
        try {
            return Enum.valueOf(type, name);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    public enum CoolantType {
        NONE,
        WATER,
        SODIUM
    }

    public enum HeatedCoolantType {
        NONE,
        STEAM,
        SUPERHEATED_SODIUM
    }
}
