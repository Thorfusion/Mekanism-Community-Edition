package mekanism.mekasuit.api.gear;

import net.minecraft.nbt.NBTTagCompound;

/** Mutable per-item state for one installed module type. */
public final class ModuleData {

    private final ModuleType type;
    private int installedCount;
    private boolean enabled;
    private String mode;
    private NBTTagCompound config;

    public ModuleData(ModuleType type, int installedCount, boolean enabled, String mode, NBTTagCompound config) {
        if (type == null) {
            throw new IllegalArgumentException("Module type is required");
        }
        this.type = type;
        setInstalledCount(installedCount);
        this.enabled = type.canDisable() ? enabled : true;
        this.mode = sanitizeMode(mode);
        this.config = config == null ? new NBTTagCompound() : config.copy();
    }

    public ModuleType getType() {
        return type;
    }

    public int getInstalledCount() {
        return installedCount;
    }

    public void setInstalledCount(int installedCount) {
        if (installedCount < 1 || installedCount > type.getMaxInstallCount()) {
            throw new IllegalArgumentException("Installed count is outside the module limit");
        }
        this.installedCount = installedCount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean setEnabled(boolean enabled) {
        if (!enabled && !type.canDisable()) {
            return false;
        }
        this.enabled = enabled;
        return true;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = sanitizeMode(mode);
    }

    public NBTTagCompound getConfig() {
        return config.copy();
    }

    public void setConfig(NBTTagCompound config) {
        this.config = config == null ? new NBTTagCompound() : config.copy();
    }

    private static String sanitizeMode(String mode) {
        if (mode == null || mode.trim().isEmpty()) {
            return "normal";
        }
        String trimmed = mode.trim();
        return trimmed.length() > 64 ? trimmed.substring(0, 64) : trimmed;
    }
}
