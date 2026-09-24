package mekanism.ultimate.api.frequency;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Stable read-only identity for an Ultimate frequency.
 */
public interface IFrequencyCE {

    String getName();

    @Nullable
    UUID getOwnerUUID();
}
