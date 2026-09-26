package mekanism.mekasuit.api.gear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.ResourceLocation;

/** Immutable definition of an installable MekaSuit unit. */
public final class ModuleType {

    private final ResourceLocation id;
    private final int maxInstallCount;
    private final boolean canDisable;
    private final boolean enabledByDefault;
    private final boolean handlesModeChange;
    private final long energyCost;
    private final Set<ModuleTarget> supportedTargets;
    private final Set<ModuleExclusive> exclusiveFlags;
    private final List<String> modes;
    private final String defaultMode;
    private final int modeInstallOffset;
    private final Map<String, Boolean> booleanConfigs;
    private final Map<String, EnumConfigDefinition> enumConfigs;

    private ModuleType(Builder builder) {
        id = builder.id;
        maxInstallCount = builder.maxInstallCount;
        canDisable = builder.canDisable;
        enabledByDefault = builder.enabledByDefault;
        handlesModeChange = builder.handlesModeChange;
        energyCost = builder.energyCost;
        supportedTargets = Collections.unmodifiableSet(EnumSet.copyOf(builder.supportedTargets));
        exclusiveFlags = builder.exclusiveFlags.isEmpty() ? Collections.emptySet()
              : Collections.unmodifiableSet(EnumSet.copyOf(builder.exclusiveFlags));
        modes = Collections.unmodifiableList(new ArrayList<>(builder.modes));
        defaultMode = builder.defaultMode;
        modeInstallOffset = builder.modeInstallOffset;
        booleanConfigs = Collections.unmodifiableMap(new LinkedHashMap<>(builder.booleanConfigs));
        enumConfigs = Collections.unmodifiableMap(new LinkedHashMap<>(builder.enumConfigs));
    }

    public static Builder builder(ResourceLocation id, ModuleTarget firstTarget, ModuleTarget... otherTargets) {
        return new Builder(id, firstTarget, otherTargets);
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getMaxInstallCount() {
        return maxInstallCount;
    }

    public boolean canDisable() {
        return canDisable;
    }

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    public boolean handlesModeChange() {
        return handlesModeChange;
    }

    public boolean hasModes() {
        return !modes.isEmpty();
    }

    public String getDefaultMode() {
        return defaultMode;
    }

    public List<String> getAvailableModes(int installedCount) {
        if (modes.isEmpty()) {
            return Collections.emptyList();
        }
        int count = Math.min(modes.size(), Math.max(1, installedCount) + modeInstallOffset);
        return modes.subList(0, count);
    }

    public boolean isModeAllowed(String mode, int installedCount) {
        if (mode == null) {
            return false;
        }
        // Older/custom module definitions may opt into free-form mode storage
        // without declaring an installed-count-bounded schema.
        return modes.isEmpty() ? handlesModeChange : getAvailableModes(installedCount).contains(mode);
    }

    public boolean hasBooleanConfigs() {
        return !booleanConfigs.isEmpty();
    }

    public Set<String> getBooleanConfigKeys() {
        return booleanConfigs.keySet();
    }

    public boolean supportsBooleanConfig(String key) {
        return booleanConfigs.containsKey(key);
    }

    public boolean getDefaultBooleanConfig(String key) {
        Boolean value = booleanConfigs.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Unknown boolean module config: " + key);
        }
        return value;
    }

    public boolean hasEnumConfigs() {
        return !enumConfigs.isEmpty();
    }

    public Set<String> getEnumConfigKeys() {
        return enumConfigs.keySet();
    }

    public boolean supportsEnumConfig(String key) {
        return enumConfigs.containsKey(key);
    }

    public String getDefaultEnumConfig(String key) {
        return enumConfig(key).getDefaultValue();
    }

    public List<String> getAvailableEnumConfigValues(String key, int installedCount) {
        return enumConfig(key).getAvailableValues(installedCount);
    }

    public String normalizeEnumConfig(String key, String value, int installedCount) {
        return enumConfig(key).normalize(value, installedCount);
    }

    public String cycleEnumConfig(String key, String value, int installedCount, int shift) {
        return enumConfig(key).cycle(value, installedCount, shift);
    }

    public String normalizeMode(String mode, int installedCount) {
        if (modes.isEmpty()) {
            // Preserve legacy/free-form values even for definitions that do not
            // currently expose a mode control; setMode still enforces the flag.
            return mode == null ? "normal" : mode;
        }
        return isModeAllowed(mode, installedCount) ? mode : defaultMode;
    }

    public String cycleMode(String mode, int installedCount, int shift) {
        List<String> available = getAvailableModes(installedCount);
        if (available.isEmpty()) {
            return "normal";
        }
        int current = available.indexOf(normalizeMode(mode, installedCount));
        return available.get(Math.floorMod(current + shift, available.size()));
    }

    public long getEnergyCost() {
        return energyCost;
    }

    public Set<ModuleTarget> getSupportedTargets() {
        return supportedTargets;
    }

    public boolean supports(ModuleTarget target) {
        return supportedTargets.contains(target);
    }

    public Set<ModuleExclusive> getExclusiveFlags() {
        return exclusiveFlags;
    }

    public boolean conflictsWith(ModuleType other) {
        if (other == null || other == this || exclusiveFlags.isEmpty() || other.exclusiveFlags.isEmpty()) {
            return false;
        }
        for (ModuleExclusive flag : exclusiveFlags) {
            if (other.exclusiveFlags.contains(flag)) {
                return true;
            }
        }
        return false;
    }

    public String getTranslationKey() {
        return "module." + id.getNamespace() + "." + id.getPath();
    }

    public String getDescriptionKey() {
        return getTranslationKey() + ".description";
    }

    private EnumConfigDefinition enumConfig(String key) {
        EnumConfigDefinition definition = enumConfigs.get(key);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown enum module config: " + key);
        }
        return definition;
    }

    /** Ordered string enum whose available values can grow with the installed module count. */
    public static final class EnumConfigDefinition {

        private final List<String> values;
        private final String defaultValue;
        private final int installOffset;

        private EnumConfigDefinition(int installOffset, String defaultValue, String... values) {
            if (installOffset < 0 || defaultValue == null || values == null || values.length == 0) {
                throw new IllegalArgumentException("An enum config requires values, a default, and a non-negative offset");
            }
            List<String> declared = new ArrayList<>(Arrays.asList(values));
            if (declared.contains(null) || !declared.contains(defaultValue)) {
                throw new IllegalArgumentException("The default enum config value must be declared");
            }
            this.values = Collections.unmodifiableList(declared);
            this.defaultValue = defaultValue;
            this.installOffset = installOffset;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public List<String> getAvailableValues(int installedCount) {
            int count = Math.min(values.size(), Math.max(1, installedCount) + installOffset);
            return values.subList(0, count);
        }

        public String normalize(String value, int installedCount) {
            return getAvailableValues(installedCount).contains(value) ? value : defaultValue;
        }

        public String cycle(String value, int installedCount, int shift) {
            List<String> available = getAvailableValues(installedCount);
            int current = available.indexOf(normalize(value, installedCount));
            return available.get(Math.floorMod(current + shift, available.size()));
        }
    }

    public static final class Builder {

        private final ResourceLocation id;
        private final EnumSet<ModuleTarget> supportedTargets;
        private final EnumSet<ModuleExclusive> exclusiveFlags = EnumSet.noneOf(ModuleExclusive.class);
        private int maxInstallCount = 1;
        private boolean canDisable = true;
        private boolean enabledByDefault = true;
        private boolean handlesModeChange;
        private long energyCost;
        private List<String> modes = Collections.emptyList();
        private String defaultMode = "normal";
        private int modeInstallOffset;
        private final Map<String, Boolean> booleanConfigs = new LinkedHashMap<>();
        private final Map<String, EnumConfigDefinition> enumConfigs = new LinkedHashMap<>();

        private Builder(ResourceLocation id, ModuleTarget firstTarget, ModuleTarget... otherTargets) {
            if (id == null || firstTarget == null) {
                throw new IllegalArgumentException("Module id and first target are required");
            }
            this.id = id;
            supportedTargets = EnumSet.of(firstTarget, otherTargets);
        }

        public Builder maxInstallCount(int maxInstallCount) {
            if (maxInstallCount < 1) {
                throw new IllegalArgumentException("Maximum install count must be positive");
            }
            this.maxInstallCount = maxInstallCount;
            return this;
        }

        public Builder noDisable() {
            canDisable = false;
            enabledByDefault = true;
            return this;
        }

        public Builder disabledByDefault() {
            if (!canDisable) {
                throw new IllegalStateException("A non-disableable module cannot start disabled");
            }
            enabledByDefault = false;
            return this;
        }

        public Builder handlesModeChange() {
            handlesModeChange = true;
            return this;
        }

        /**
         * Defines an ordered, installed-count-bounded mode schema. At a given
         * install count, {@code installedCount + installOffset} leading modes
         * are available. This mirrors modern module enum configs without
         * importing the modern codec/config stack.
         */
        public Builder modes(int installOffset, String defaultMode, String... modes) {
            if (installOffset < 0 || defaultMode == null || modes == null || modes.length == 0) {
                throw new IllegalArgumentException("A module mode schema requires modes, a default, and a non-negative offset");
            }
            List<String> values = new ArrayList<>(Arrays.asList(modes));
            if (!values.contains(defaultMode) || values.contains(null)) {
                throw new IllegalArgumentException("The default module mode must be one of the declared modes");
            }
            this.modes = values;
            this.defaultMode = defaultMode;
            modeInstallOffset = installOffset;
            handlesModeChange = true;
            return this;
        }

        public Builder energyCost(long energyCost) {
            if (energyCost < 0) {
                throw new IllegalArgumentException("Module energy cost cannot be negative");
            }
            this.energyCost = energyCost;
            return this;
        }

        public Builder booleanConfig(String key, boolean defaultValue) {
            if (!isSafeConfigKey(key)) {
                throw new IllegalArgumentException("Module config keys may only contain letters, numbers, '_', '.', or '-'");
            }
            if (enumConfigs.containsKey(key) || booleanConfigs.put(key, defaultValue) != null) {
                throw new IllegalArgumentException("Duplicate module config key: " + key);
            }
            return this;
        }

        /**
         * Defines an ordered string enum config. At a given install count,
         * {@code installedCount + installOffset} leading values are available.
         */
        public Builder enumConfig(String key, int installOffset, String defaultValue, String... values) {
            if (!isSafeConfigKey(key)) {
                throw new IllegalArgumentException("Module config keys may only contain letters, numbers, '_', '.', or '-'");
            }
            if (booleanConfigs.containsKey(key) || enumConfigs.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate module config key: " + key);
            }
            enumConfigs.put(key, new EnumConfigDefinition(installOffset, defaultValue, values));
            return this;
        }

        public Builder exclusive(ModuleExclusive... flags) {
            if (flags != null) {
                Collections.addAll(exclusiveFlags, flags);
            }
            return this;
        }

        public ModuleType build() {
            return new ModuleType(this);
        }

        private static boolean isSafeConfigKey(String key) {
            if (key == null || key.isEmpty() || key.length() > 64) {
                return false;
            }
            for (int i = 0; i < key.length(); i++) {
                char c = key.charAt(i);
                if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z')
                      && !(c >= '0' && c <= '9') && c != '_' && c != '.' && c != '-') {
                    return false;
                }
            }
            return true;
        }
    }
}
