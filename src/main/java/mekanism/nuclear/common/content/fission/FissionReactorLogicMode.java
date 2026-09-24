package mekanism.nuclear.common.content.fission;

import java.util.Locale;
import mekanism.nuclear.common.config.NuclearFissionConfig;
import net.minecraft.util.IStringSerializable;

/** Stable Fission Reactor Logic Adapter modes. Ordinals are persisted; append only. */
public enum FissionReactorLogicMode implements IStringSerializable {
    DISABLED,
    ACTIVATION,
    TEMPERATURE,
    CRITICAL_WASTE_LEVEL,
    DAMAGED,
    DEPLETED;

    private final String name = name().toLowerCase(Locale.ROOT);

    @Override
    public String getName() {
        return name;
    }

    public FissionReactorLogicMode next() {
        FissionReactorLogicMode[] modes = values();
        return modes[(ordinal() + 1) % modes.length];
    }

    public boolean shouldOutput(FissionReactorState state) {
        if (state == null || !state.isFormed()) {
            return false;
        }
        switch (this) {
            case TEMPERATURE:
                return state.getTemperature() >= NuclearFissionConfig.MIN_DAMAGE_TEMPERATURE;
            case CRITICAL_WASTE_LEVEL:
                return state.getWasteCapacity() > 0 && state.getNuclearWaste() >= Math.ceil(
                      state.getWasteCapacity() * NuclearFissionConfig.getExcessWasteRatio());
            case DAMAGED:
                return state.getDamage() >= NuclearFissionConfig.MAX_DAMAGE;
            case DEPLETED:
                return state.getFissileFuel() == 0 && state.getBurnRemaining() == 0;
            default:
                return false;
        }
    }

    public static FissionReactorLogicMode byIndex(int index) {
        FissionReactorLogicMode[] modes = values();
        return modes[Math.floorMod(index, modes.length)];
    }
}
