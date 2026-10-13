package defense.common.integration;

import java.lang.reflect.Method;
import java.util.logging.Level;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.common.Loader;
import defense.common.DefenseTech;
import defense.common.Settings;
import defense.common.entity.EntityMissile;

/** Optional loader that never resolves a Flan class unless Flan is installed. */
public final class FlansModIntegration
{
    private static final String COMMON_BRIDGE = "defense.common.integration.flans.FlansModCompat";
    private static final String CLIENT_BRIDGE = "defense.client.integration.flans.FlansModClientCompat";

    private static IFlansModIntegration bridge;
    private static boolean attempted;

    private FlansModIntegration() {}

    public static void preInit()
    {
        if(attempted)
        {
            return;
        }

        attempted = true;

        if(!Settings.ENABLE_FLANS_MOD_INTEGRATION)
        {
            DefenseTech.LOGGER.info("Optional Flan's Mod integration is disabled by configuration.");
            return;
        }

        if(!Loader.isModLoaded("flansmod"))
        {
            return;
        }

        try
        {
            bridge = (IFlansModIntegration)Class.forName(COMMON_BRIDGE).newInstance();
            bridge.preInit();
            DefenseTech.LOGGER.info("Enabled optional Flan's Mod aircraft integration.");
        }
        catch(Throwable throwable)
        {
            bridge = null;
            DefenseTech.LOGGER.log(Level.WARNING, "Flan's Mod was found, but DefenseTech compatibility could not be enabled.", throwable);
        }
    }

    public static void init()
    {
        if(isEnabled())
        {
            bridge.init();
        }
    }

    public static void postInit()
    {
        if(isEnabled())
        {
            bridge.postInit();
        }
    }

    public static void clientInit()
    {
        if(!isEnabled())
        {
            return;
        }

        try
        {
            Class<?> clientBridge = Class.forName(CLIENT_BRIDGE);
            Method init = clientBridge.getMethod("init");
            init.invoke(null);
        }
        catch(Throwable throwable)
        {
            DefenseTech.LOGGER.log(Level.WARNING, "DefenseTech could not initialize its Flan aircraft-bomb models.", throwable);
        }
    }

    public static Entity findNearestAircraft(World world, AxisAlignedBB bounds, Entity excluded)
    {
        return isEnabled() ? bridge.findNearestAircraft(world, bounds, excluded) : null;
    }

    public static boolean isAircraft(Entity entity)
    {
        return isEnabled() && bridge.isAircraft(entity);
    }

    public static void interceptAircraft(Entity aircraft, EntityMissile interceptor)
    {
        if(isEnabled())
        {
            bridge.interceptAircraft(aircraft, interceptor);
        }
    }

    public static boolean isEnabled()
    {
        return Settings.ENABLE_FLANS_MOD_INTEGRATION && bridge != null;
    }
}
