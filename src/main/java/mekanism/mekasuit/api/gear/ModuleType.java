package mekanism.mekasuit.api.gear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
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

        public Builder exclusive(ModuleExclusive... flags) {
            if (flags != null) {
                Collections.addAll(exclusiveFlags, flags);
            }
            return this;
        }

        public ModuleType build() {
            return new ModuleType(this);
        }
    }
}
