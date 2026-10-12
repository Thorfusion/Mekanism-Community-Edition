package mekanism.mekasuit.api.gear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

/** Ordered registry used by module containers and optional integrations. */
public final class ModuleRegistry
{
    private static final ModuleRegistry INSTANCE = new ModuleRegistry();

    private final Map<ResourceLocation, ModuleType> types =
          new LinkedHashMap<ResourceLocation, ModuleType>();

    public static ModuleRegistry getInstance()
    {
        return INSTANCE;
    }

    public synchronized ModuleType register(ModuleType type)
    {
        if (type == null)
        {
            throw new IllegalArgumentException("Module type is required");
        }
        ModuleType previous = types.get(type.getId());
        if (previous != null && previous != type)
        {
            throw new IllegalArgumentException("Duplicate module id: " + type.getId());
        }
        types.put(type.getId(), type);
        return type;
    }

    public synchronized ModuleType get(ResourceLocation id)
    {
        return types.get(id);
    }

    public synchronized Collection<ModuleType> values()
    {
        return Collections.unmodifiableList(new ArrayList<ModuleType>(types.values()));
    }
}
