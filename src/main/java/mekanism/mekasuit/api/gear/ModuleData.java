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
        this.mode = type.normalizeMode(sanitizeMode(mode), installedCount);
        this.config = config == null ? new NBTTagCompound() : config.copy();
        for (String key : type.getBooleanConfigKeys()) {
            if (!this.config.hasKey(key)) {
                this.config.setBoolean(key, type.getDefaultBooleanConfig(key));
            }
        }
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
        if (mode != null) {
            mode = type.normalizeMode(mode, installedCount);
        }
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

    public boolean setMode(String mode) {
        String sanitized = sanitizeMode(mode);
        if (!type.isModeAllowed(sanitized, installedCount)) {
            return false;
        }
        this.mode = sanitized;
        return true;
    }

    public NBTTagCompound getConfig() {
        return config.copy();
    }

    public void setConfig(NBTTagCompound config) {
        this.config = config == null ? new NBTTagCompound() : config.copy();
        for (String key : type.getBooleanConfigKeys()) {
            if (!this.config.hasKey(key)) {
                this.config.setBoolean(key, type.getDefaultBooleanConfig(key));
            }
        }
    }

    public boolean getBooleanConfig(String key) {
        return type.supportsBooleanConfig(key)
              && (config.hasKey(key) ? config.getBoolean(key) : type.getDefaultBooleanConfig(key));
    }

    public boolean setBooleanConfig(String key, boolean value) {
        if (!type.supportsBooleanConfig(key)) {
            return false;
        }
        config.setBoolean(key, value);
        return true;
    }

    private static String sanitizeMode(String mode) {
        if (mode == null || mode.trim().isEmpty()) {
            return "normal";
        }
        String trimmed = mode.trim();
        return trimmed.length() > 64 ? trimmed.substring(0, 64) : trimmed;
    }
}
