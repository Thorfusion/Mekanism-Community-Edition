package mekanism.ultimate.api.frequency;

import java.util.Collection;
import javax.annotation.Nullable;

/** Lookup boundary for old-Mekanism or Ultimate-owned frequency stores. */
public interface IFrequencyServiceCE<FREQUENCY extends IFrequencyCE>
{
    /** Returns a read-only snapshot of frequencies visible to the given owner. */
    Collection<FREQUENCY> getFrequencies(@Nullable String ownerName);

    @Nullable
    FREQUENCY findFrequency(String name, @Nullable String ownerName);
}
