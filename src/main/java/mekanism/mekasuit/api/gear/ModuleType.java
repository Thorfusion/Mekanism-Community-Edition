package mekanism.mekasuit.api.gear;

import java.util.Collections;
import java.util.EnumSet;
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
