package mekanism.ultimate.api.frequency;

import javax.annotation.Nullable;

/** Stable read-only identity for an Ultimate frequency on the username-based 1.7 security system. */
public interface IFrequencyCE
{
    String getName();

    @Nullable
    String getOwnerName();
}
