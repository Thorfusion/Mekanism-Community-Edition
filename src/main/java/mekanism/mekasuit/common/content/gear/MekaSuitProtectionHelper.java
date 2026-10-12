package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.ModuleTarget;

/** Stable per-piece protection values shared by MekaSuit integrations. */
public final class MekaSuitProtectionHelper
{
    private MekaSuitProtectionHelper()
    {
    }

    public static double getLaserDissipation(ModuleTarget target,
          boolean enabled)
    {
        if (!enabled || target == null)
        {
            return 0;
        }
        switch (target)
        {
            case HELMET:
                return 0.15D;
            case BODYARMOR:
                return 0.30D;
            case PANTS:
                return 0.1875D;
            case BOOTS:
                return 0.1125D;
            default:
                return 0;
        }
    }

    public static double getLaserRefraction(ModuleTarget target,
          boolean enabled)
    {
        if (!enabled || target == null)
        {
            return 0;
        }
        switch (target)
        {
            case HELMET:
                return 0.20D;
            case BODYARMOR:
                return 0.40D;
            case PANTS:
                return 0.25D;
            case BOOTS:
                return 0.15D;
            default:
                return 0;
        }
    }

    public static double getRadiationShielding(ModuleTarget target,
          boolean enabled)
    {
        if (!enabled || target == null)
        {
            return 0;
        }
        switch (target)
        {
            case HELMET:
                return 0.25D;
            case BODYARMOR:
                return 0.40D;
            case PANTS:
                return 0.20D;
            case BOOTS:
                return 0.15D;
            default:
                return 0;
        }
    }
}
