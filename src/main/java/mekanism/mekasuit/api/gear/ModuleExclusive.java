package mekanism.mekasuit.api.gear;

/** Mutually-exclusive behavior groups for installed modules. */
public enum ModuleExclusive
{
    INTERACT_BLOCK,
    INTERACT_ENTITY,
    OVERRIDE_DROPS,
    OVERRIDE_JUMP
}
