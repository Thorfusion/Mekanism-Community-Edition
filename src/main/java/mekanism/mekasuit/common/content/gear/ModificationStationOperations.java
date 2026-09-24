package mekanism.mekasuit.common.content.gear;

import mekanism.api.energy.IEnergizedItem;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleInstallResult;
import mekanism.mekasuit.api.gear.ModuleRegistry;
import mekanism.mekasuit.api.gear.ModuleType;
import mekanism.mekasuit.common.MekaSuitItems;
import mekanism.mekasuit.common.item.ItemMekaModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/** Server-authoritative, inventory-independent Modification Station transactions. */
public final class ModificationStationOperations {

    private ModificationStationOperations() {
    }

    public static int getInstallableCount(ItemStack host, ItemStack modules) {
        if (!isHost(host) || modules == null || modules.isEmpty()
              || !(modules.getItem() instanceof ItemMekaModule)) {
            return 0;
        }
        ModuleType type = ((ItemMekaModule) modules.getItem()).getModuleType();
        ModuleContainer container = container(host);
        int remaining = type.getMaxInstallCount() - container.getInstalledCount(type);
        int count = Math.min(modules.getCount(), remaining);
        return count > 0 && container.install(type, count) == ModuleInstallResult.SUCCESS ? count : 0;
    }

    public static int install(ItemStack host, ItemStack modules) {
        int count = getInstallableCount(host, modules);
        if (count <= 0) {
            return 0;
        }
        ModuleType type = ((ItemMekaModule) modules.getItem()).getModuleType();
        ModuleContainer container = container(host);
        if (container.install(type, count) != ModuleInstallResult.SUCCESS) {
            return 0;
        }
        container.save(host);
        modulesChanged(host);
        return count;
    }

    public static ItemStack remove(ItemStack host, ResourceLocation moduleId, int amount) {
        if (!isHost(host) || moduleId == null || amount < 1) {
            return ItemStack.EMPTY;
        }
        ModuleType type = ModuleRegistry.getInstance().get(moduleId);
        ItemMekaModule moduleItem = MekaSuitItems.getModuleItem(type);
        if (type == null || moduleItem == null) {
            return ItemStack.EMPTY;
        }
        ModuleContainer container = container(host);
        int removed = container.remove(type, amount);
        if (removed <= 0) {
            return ItemStack.EMPTY;
        }
        container.save(host);
        clampEnergy(host);
        modulesChanged(host);
        return new ItemStack(moduleItem, removed);
    }

    public static boolean toggleEnabled(ItemStack host, ResourceLocation moduleId) {
        ModuleContainer container = getContainerForKnownType(host, moduleId);
        ModuleType type = ModuleRegistry.getInstance().get(moduleId);
        ModuleData data = container == null || type == null ? null : container.get(type);
        if (data == null || !container.setEnabled(type, !data.isEnabled())) {
            return false;
        }
        container.save(host);
        modulesChanged(host);
        return true;
    }

    public static boolean setMode(ItemStack host, ResourceLocation moduleId, String mode) {
        ModuleContainer container = getContainerForKnownType(host, moduleId);
        ModuleType type = ModuleRegistry.getInstance().get(moduleId);
        if (container == null || type == null || mode == null || mode.length() > 64
              || !container.setMode(type, mode)) {
            return false;
        }
        container.save(host);
        modulesChanged(host);
        return true;
    }

    public static boolean setBooleanConfig(ItemStack host, ResourceLocation moduleId, String key, boolean value) {
        ModuleContainer container = getContainerForKnownType(host, moduleId);
        ModuleType type = ModuleRegistry.getInstance().get(moduleId);
        ModuleData data = container == null || type == null ? null : container.get(type);
        if (data == null || !isSafeConfigKey(key)) {
            return false;
        }
        NBTTagCompound config = data.getConfig();
        config.setBoolean(key, value);
        if (!container.setConfig(type, config)) {
            return false;
        }
        container.save(host);
        modulesChanged(host);
        return true;
    }

    private static ModuleContainer getContainerForKnownType(ItemStack host, ResourceLocation moduleId) {
        return isHost(host) && moduleId != null && ModuleRegistry.getInstance().get(moduleId) != null
              ? container(host) : null;
    }

    private static ModuleContainer container(ItemStack host) {
        return ModuleContainer.fromStack(host, ((IModuleContainerItem) host.getItem()).getModuleTarget());
    }

    private static boolean isHost(ItemStack host) {
        return host != null && !host.isEmpty() && host.getItem() instanceof IModuleContainerItem;
    }

    private static void clampEnergy(ItemStack host) {
        if (host.getItem() instanceof IEnergizedItem) {
            IEnergizedItem energized = (IEnergizedItem) host.getItem();
            energized.setEnergy(host, energized.getEnergy(host));
        }
    }

    private static void modulesChanged(ItemStack host) {
        if (host.getItem() instanceof IModuleContainerItem
              && ((IModuleContainerItem) host.getItem()).getModuleTarget() == mekanism.mekasuit.api.gear.ModuleTarget.MEKA_TOOL) {
            MekaToolModuleHelper.synchronizeHarvestEnchantments(host);
        }
    }

    private static boolean isSafeConfigKey(String key) {
        if (key == null || key.isEmpty() || key.length() > 64) {
            return false;
        }
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z')
                  && !(c >= '0' && c <= '9') && c != '_' && c != '.' && c != '-') {
                return false;
            }
        }
        return true;
    }
}
