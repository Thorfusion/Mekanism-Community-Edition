package mekanism.mekasuit.common.content.gear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mekanism.common.util.ItemDataUtils;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleInstallResult;
import mekanism.mekasuit.api.gear.ModuleRegistry;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.api.gear.ModuleType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * Validated, versioned module state. Unknown module entries are retained so
 * removing an optional integration does not destroy its item data.
 */
public final class ModuleContainer {

    public static final int DATA_VERSION = 1;
    public static final String NBT_ROOT = "MekaSuitModules";

    private static final String NBT_VERSION = "version";
    private static final String NBT_MODULES = "modules";
    private static final String NBT_ID = "id";
    private static final String NBT_INSTALLED = "installed";
    private static final String NBT_ENABLED = "enabled";
    private static final String NBT_MODE = "mode";
    private static final String NBT_CONFIG = "config";

    private final ModuleRegistry registry;
    private final ModuleTarget target;
    private final Map<ResourceLocation, ModuleData> modules = new LinkedHashMap<>();
    private final List<NBTTagCompound> unknownModules = new ArrayList<>();

    public ModuleContainer(ModuleRegistry registry, ModuleTarget target) {
        if (registry == null || target == null) {
            throw new IllegalArgumentException("Module registry and target are required");
        }
        this.registry = registry;
        this.target = target;
    }

    public static ModuleContainer fromStack(ItemStack stack, ModuleTarget target) {
        ModuleContainer container = new ModuleContainer(ModuleRegistry.getInstance(), target);
        if (stack != null && !stack.isEmpty()) {
            container.read(ItemDataUtils.getCompound(stack, NBT_ROOT));
        }
        return container;
    }

    public void save(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("Cannot save modules to an empty stack");
        }
        ItemDataUtils.setCompound(stack, NBT_ROOT, write());
    }

    public ModuleInstallResult install(ModuleType type, int amount) {
        if (type == null || amount < 1) {
            return ModuleInstallResult.INVALID_COUNT;
        }
        if (registry.get(type.getId()) != type) {
            return ModuleInstallResult.UNREGISTERED;
        }
        if (!type.supports(target)) {
            return ModuleInstallResult.UNSUPPORTED_TARGET;
        }
        ModuleData current = modules.get(type.getId());
        int installed = current == null ? 0 : current.getInstalledCount();
        if (amount > type.getMaxInstallCount() - installed) {
            return ModuleInstallResult.MAX_INSTALLED;
        }
        for (ModuleData other : modules.values()) {
            if (type.conflictsWith(other.getType())) {
                return ModuleInstallResult.CONFLICT;
            }
        }
        if (current == null) {
            modules.put(type.getId(), new ModuleData(type, amount, type.isEnabledByDefault(), type.getDefaultMode(), null));
        } else {
            current.setInstalledCount(installed + amount);
        }
        return ModuleInstallResult.SUCCESS;
    }

    public int remove(ModuleType type, int amount) {
        if (type == null || amount < 1) {
            return 0;
        }
        ModuleData current = modules.get(type.getId());
        if (current == null) {
            return 0;
        }
        int removed = Math.min(amount, current.getInstalledCount());
        int remaining = current.getInstalledCount() - removed;
        if (remaining == 0) {
            modules.remove(type.getId());
        } else {
            current.setInstalledCount(remaining);
        }
        return removed;
    }

    public boolean setEnabled(ModuleType type, boolean enabled) {
        ModuleData data = get(type);
        return data != null && data.setEnabled(enabled);
    }

    public boolean setMode(ModuleType type, String mode) {
        ModuleData data = get(type);
        if (data == null || !type.handlesModeChange()) {
            return false;
        }
        return data.setMode(mode);
    }

    public boolean setConfig(ModuleType type, NBTTagCompound config) {
        ModuleData data = get(type);
        if (data == null) {
            return false;
        }
        data.setConfig(config);
        return true;
    }

    public ModuleData get(ModuleType type) {
        return type == null ? null : modules.get(type.getId());
    }

    public int getInstalledCount(ModuleType type) {
        ModuleData data = get(type);
        return data == null ? 0 : data.getInstalledCount();
    }

    public boolean hasEnabled(ModuleType type) {
        ModuleData data = get(type);
        return data != null && data.isEnabled();
    }

    public Collection<ModuleData> getModules() {
        return Collections.unmodifiableCollection(modules.values());
    }

    public ModuleTarget getTarget() {
        return target;
    }

    public void read(NBTTagCompound root) {
        modules.clear();
        unknownModules.clear();
        if (root == null || !root.hasKey(NBT_MODULES, NBT.TAG_LIST)) {
            return;
        }
        NBTTagList list = root.getTagList(NBT_MODULES, NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            String idString = entry.getString(NBT_ID);
            ResourceLocation id;
            try {
                id = new ResourceLocation(idString);
            } catch (RuntimeException ex) {
                continue;
            }
            ModuleType type = registry.get(id);
            if (type == null) {
                unknownModules.add(entry.copy());
                continue;
            }
            if (!type.supports(target)) {
                continue;
            }
            int storedCount = entry.getInteger(NBT_INSTALLED);
            if (storedCount < 1) {
                continue;
            }
            ModuleData existing = modules.get(id);
            long total = (long) storedCount + (existing == null ? 0 : existing.getInstalledCount());
            int combined = (int) Math.min(type.getMaxInstallCount(), total);
            if (existing != null) {
                existing.setInstalledCount(combined);
                continue;
            }
            if (hasConflict(type)) {
                continue;
            }
            boolean enabled = !entry.hasKey(NBT_ENABLED) || entry.getBoolean(NBT_ENABLED);
            String mode = entry.getString(NBT_MODE);
            NBTTagCompound config = entry.hasKey(NBT_CONFIG, NBT.TAG_COMPOUND)
                  ? entry.getCompoundTag(NBT_CONFIG) : new NBTTagCompound();
            modules.put(id, new ModuleData(type, combined, enabled, mode, config));
        }
    }

    public NBTTagCompound write() {
        NBTTagCompound root = new NBTTagCompound();
        root.setInteger(NBT_VERSION, DATA_VERSION);
        NBTTagList list = new NBTTagList();
        for (ModuleData data : modules.values()) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString(NBT_ID, data.getType().getId().toString());
            entry.setInteger(NBT_INSTALLED, data.getInstalledCount());
            entry.setBoolean(NBT_ENABLED, data.isEnabled());
            entry.setString(NBT_MODE, data.getMode());
            entry.setTag(NBT_CONFIG, data.getConfig());
            list.appendTag(entry);
        }
        for (NBTTagCompound unknown : unknownModules) {
            list.appendTag(unknown.copy());
        }
        root.setTag(NBT_MODULES, list);
        return root;
    }

    private boolean hasConflict(ModuleType type) {
        for (ModuleData installed : modules.values()) {
            if (type.conflictsWith(installed.getType())) {
                return true;
            }
        }
        return false;
    }
}
