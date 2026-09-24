package mekanism.mekasuit.api.gear;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

/** Ordered registry used by the Modification Station and optional integrations. */
public final class ModuleRegistry {

    private static final ModuleRegistry INSTANCE = new ModuleRegistry();

    private final Map<ResourceLocation, ModuleType> types = new LinkedHashMap<>();

    public static ModuleRegistry getInstance() {
        return INSTANCE;
    }

    public synchronized ModuleType register(ModuleType type) {
        if (type == null) {
            throw new IllegalArgumentException("Module type is required");
        }
        ModuleType previous = types.putIfAbsent(type.getId(), type);
        if (previous != null && previous != type) {
            throw new IllegalArgumentException("Duplicate module id: " + type.getId());
        }
        return type;
    }

    public synchronized ModuleType get(ResourceLocation id) {
        return types.get(id);
    }

    public synchronized Collection<ModuleType> values() {
        return Collections.unmodifiableList(new java.util.ArrayList<>(types.values()));
    }
}
