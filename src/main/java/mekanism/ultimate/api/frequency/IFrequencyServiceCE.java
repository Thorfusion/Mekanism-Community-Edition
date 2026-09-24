package mekanism.ultimate.api.frequency;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Lookup boundary for old-Mekanism or Ultimate-owned frequency stores.
 */
public interface IFrequencyServiceCE<FREQUENCY extends IFrequencyCE> {

    /**
     * Returns a read-only snapshot of frequencies visible to the given owner.
     */
    Collection<FREQUENCY> getFrequencies(@Nullable UUID ownerUUID);

    @Nullable
    FREQUENCY findFrequency(String name, @Nullable UUID ownerUUID);
}
