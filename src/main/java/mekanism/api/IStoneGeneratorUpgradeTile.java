package mekanism.api;

/**
 * Additive bridge for the modern Stone Generator Upgrade. Kept outside the
 * ordinal-backed legacy Upgrade enum so existing upgrade NBT remains stable.
 */
public interface IStoneGeneratorUpgradeTile {

    boolean hasStoneGeneratorUpgrade();

    boolean installStoneGeneratorUpgrade();
}
