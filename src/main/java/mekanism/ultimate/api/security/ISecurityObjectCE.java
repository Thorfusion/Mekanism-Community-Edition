package mekanism.ultimate.api.security;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Ownership and requested access mode for an Ultimate object.
 */
public interface ISecurityObjectCE {

    @Nullable
    UUID getOwnerUUID();

    SecurityModeCE getSecurityMode();
}
