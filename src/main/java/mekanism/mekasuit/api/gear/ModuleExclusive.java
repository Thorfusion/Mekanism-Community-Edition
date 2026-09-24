package mekanism.mekasuit.api.gear;

/**
 * Mutually-exclusive behavior groups. Modules sharing a flag cannot coexist
 * in one module container, even when one of them is disabled.
 */
public enum ModuleExclusive {
    INTERACT_BLOCK,
    INTERACT_ENTITY,
    OVERRIDE_DROPS,
    OVERRIDE_JUMP
}
