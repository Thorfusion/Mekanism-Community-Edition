package mekanism.ultimate.api.security;

import javax.annotation.Nullable;

/** Ownership and requested access mode for a username-based 1.7 object. */
public interface ISecurityObjectCE
{
    @Nullable
    String getOwnerName();

    SecurityModeCE getSecurityMode();
}
