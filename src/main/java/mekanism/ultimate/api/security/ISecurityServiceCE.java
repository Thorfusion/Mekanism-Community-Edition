package mekanism.ultimate.api.security;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Server-authoritative security decision service.
 */
public interface ISecurityServiceCE {

    boolean canAccess(EntityPlayer player, ISecurityObjectCE target);
}
