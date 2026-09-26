package mekanism.ultimate.common.tier;

import java.util.Locale;
import mekanism.common.tier.BaseTier;
import mekanism.ultimate.common.config.UltimateChemicalTankConfig;
import net.minecraft.util.IStringSerializable;

/** Stable v10.7 Chemical Tank capacities and output rates, expressed in mB. */
public enum ChemicalTankTier implements IStringSerializable {
    BASIC(BaseTier.BASIC, 64_000L, 1_000L),
    ADVANCED(BaseTier.ADVANCED, 256_000L, 16_000L),
    ELITE(BaseTier.ELITE, 1_024_000L, 128_000L),
    ULTIMATE(BaseTier.ULTIMATE, 8_192_000L, 512_000L),
    CREATIVE(BaseTier.CREATIVE, Long.MAX_VALUE, Long.MAX_VALUE / 2);

    private final BaseTier baseTier;
    private final long baseStorage;
    private final long baseOutput;

    ChemicalTankTier(BaseTier baseTier, long baseStorage, long baseOutput) {
        this.baseTier = baseTier;
        this.baseStorage = baseStorage;
        this.baseOutput = baseOutput;
    }

    public BaseTier getBaseTier() {
        return baseTier;
    }

    public long getStorage() {
        return UltimateChemicalTankConfig.getStorage(this);
    }

    public long getOutput() {
        return UltimateChemicalTankConfig.getOutput(this);
    }

    public long getBaseStorage() {
        return baseStorage;
    }

    public long getBaseOutput() {
        return baseOutput;
    }

    public boolean isCreative() {
        return this == CREATIVE;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
