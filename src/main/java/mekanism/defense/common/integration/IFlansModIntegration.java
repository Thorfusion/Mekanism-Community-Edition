package defense.common.integration;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import defense.common.entity.EntityMissile;

/**
 * Flan-free boundary used by the optional integration loader. Implementations
 * may reference Flan classes, while callers remain safe when Flan is absent.
 */
public interface IFlansModIntegration
{
    void preInit();

    void init();

    void postInit();

    Entity findNearestAircraft(World world, AxisAlignedBB bounds, Entity excluded);

    boolean isAircraft(Entity entity);

    void interceptAircraft(Entity aircraft, EntityMissile interceptor);
}
