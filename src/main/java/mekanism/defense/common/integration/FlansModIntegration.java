package defense.common.integration;

import java.lang.reflect.Method;
import java.util.logging.Level;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.common.Loader;
import defense.common.DefenseTech;
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
        if(bridge != null)
        {
            bridge.init();
        }
    }

    public static void postInit()
    {
        if(bridge != null)
        {
            bridge.postInit();
        }
    }

    public static void clientInit()
    {
        if(bridge == null)
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
        return bridge == null ? null : bridge.findNearestAircraft(world, bounds, excluded);
    }

    public static boolean isAircraft(Entity entity)
    {
        return bridge != null && bridge.isAircraft(entity);
    }

    public static void interceptAircraft(Entity aircraft, EntityMissile interceptor)
    {
        if(bridge != null)
        {
            bridge.interceptAircraft(aircraft, interceptor);
        }
    }
}
