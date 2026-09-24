package mekanism.nuclear.common;

/**
 * Native 1.12 ore defaults taken from the final official pre-negative-height
 * Mekanism branch (1.16.x, commit 160d59e8d4b11aec446fc4d7d84b9f01dba5da68).
 */
public enum NuclearOreType {
    URANIUM("uranium", 8, 8, 60, 0, 0),
    FLUORITE("fluorite", 6, 12, 32, 1, 4);

    private final String name;
    private final int veinsPerChunk;
    private final int maxVeinSize;
    private final int maxHeight;
    private final int minExperience;
    private final int maxExperience;

    NuclearOreType(String name, int veinsPerChunk, int maxVeinSize, int maxHeight,
          int minExperience, int maxExperience) {
        this.name = name;
        this.veinsPerChunk = veinsPerChunk;
        this.maxVeinSize = maxVeinSize;
        this.maxHeight = maxHeight;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
    }

    public String getName() {
        return name;
    }

    public int getVeinsPerChunk() {
        return veinsPerChunk;
    }

    public int getMaxVeinSize() {
        return maxVeinSize;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMinExperience() {
        return minExperience;
    }

    public int getMaxExperience() {
        return maxExperience;
    }
}
