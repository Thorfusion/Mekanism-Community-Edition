package mekanism.api;

/**
 * Additive bridge for the modern Stone Generator Upgrade. It intentionally
 * stays outside the ordinal-backed legacy Upgrade enum so old upgrade NBT is
 * unchanged.
 */
public interface IStoneGeneratorUpgradeTile
{
    boolean hasStoneGeneratorUpgrade();

    boolean installStoneGeneratorUpgrade();
}
