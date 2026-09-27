package mekanism.common.frequency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import mekanism.common.security.SecurityFrequency;
import mekanism.common.util.SecurityUtils;
import net.minecraft.world.World;

/** Shared lookup for owner-managed frequencies visible to trusted players. */
public final class TrustedFrequencyUtils {

    private TrustedFrequencyUtils() {
    }

    public static List<Frequency> collect(Map<UUID, FrequencyManager> managers,
          Class<? extends Frequency> frequencyClass, String managerName, UUID requester,
          World world) {
        List<Frequency> result = new ArrayList<>();
        if (requester == null || world == null) return result;
        for (Frequency securityBase : mekanism.common.Mekanism.securityFrequencies.getFrequencies()) {
            if (!(securityBase instanceof SecurityFrequency)) continue;
            UUID owner = securityBase.ownerUUID;
            if (owner == null || owner.equals(requester) || !SecurityUtils.isTrusted(owner, requester)) continue;
            FrequencyManager manager = managers.get(owner);
            if (manager == null) {
                manager = new FrequencyManager(frequencyClass, managerName, owner);
                managers.put(owner, manager);
                manager.createOrLoad(world);
            }
            for (Frequency frequency : manager.getFrequencies()) {
                if (frequency.isTrusted() && SecurityUtils.canUseFrequency(frequency, requester)) {
                    result.add(frequency);
                }
            }
        }
        return result;
    }
}
