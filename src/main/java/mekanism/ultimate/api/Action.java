package mekanism.ultimate.api;

/**
 * Controls whether a storage operation is simulated or committed.
 */
public enum Action {
    SIMULATE,
    EXECUTE;

    public boolean execute() {
        return this == EXECUTE;
    }

    public boolean simulate() {
        return this == SIMULATE;
    }
}
