package mekanism.mekasuit.api.gear;

/** Marks an item as a host for the NBT-backed MekaSuit module framework. */
public interface IModuleContainerItem {

    ModuleTarget getModuleTarget();
}
