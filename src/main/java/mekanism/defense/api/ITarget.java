package defense.api;

import mekanism.api.Pos3D;

/** This should only be used to improve or restrict the targeting behavior of an entity. By default
 * sentry guns and other automated tracking will target an entity. Especially if they use build in
 * interfaces such as IMob from MC. As well IMissile is automatically tracked but it
 * is recommended to use this to help targeting predict the position. The other use of this
 * interface is to apply anti-targeting to an entity. For example canBeTargeted will prevent the
 * entity from being target out right. As well predicted position can be used to trick missiles into
 * missing.
 * 
 * @author Darkguardsman */
public interface ITarget
{
    /** Gets the predicted position of this entity after a specified amount of ticks.
     * 
     * @param ticks - The amount of time.
     * @return The predicted Vector, or if not predictable, the current position. */
    public Pos3D getPredictedPosition(int ticks);

    /** Can this be targeted by automated targeting systems or AIs. Used to implement radar jammers,
     * cloaking devices, and other addons for the Entity being targeted
     * 
     * @param entity - entity that is targeting this, can be an Entity, EntityLiving, or TileEntity
     * @return true if it can */
    public boolean canBeTargeted(Object entity);

    /** Type of target */
    public TargetType getType();

    public static enum TargetType
    {
        /** General air should only be applied to living entities */
        AIR,
        /** Anything that is an instance of IMissile */
        MISSILE,
        /** Flying machine that may or may not be piloted */
        PLANE,
        /** Any living entity that walks or hovers on the ground */
        GROUND,
        /** Causes all sentry types to target */
        ANY;
    }
}
