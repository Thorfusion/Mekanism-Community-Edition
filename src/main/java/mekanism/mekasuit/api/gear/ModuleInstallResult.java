package mekanism.mekasuit.api.gear;

/** Result returned by transactional module installation attempts. */
public enum ModuleInstallResult {
    SUCCESS,
    INVALID_COUNT,
    UNREGISTERED,
    UNSUPPORTED_TARGET,
    MAX_INSTALLED,
    CONFLICT
}
