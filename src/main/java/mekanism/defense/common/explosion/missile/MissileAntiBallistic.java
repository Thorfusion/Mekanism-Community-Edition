package defense.common.explosion.missile;

import java.util.List;

import mekanism.api.Pos3D;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.ITarget;
import defense.api.ITarget.TargetType;
import defense.client.model.missile.ModelHomingMissile;
import defense.client.model.missile.ModelMissileBase;
import defense.common.Reference;
import defense.common.entity.EntityMissile;
import defense.common.explosive.blast.BlastRepulsive;
import defense.common.integration.FlansModIntegration;

/** Anti-Ballistic missile.
 * 
 * @author Calclavia */
public class MissileAntiBallistic extends Missile
{
    public static final int ABMRange = 30;

    public MissileAntiBallistic()
    {
        super("antiBallistic", 2);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
        return new ModelHomingMissile();
    }

    @Override
    public void update(EntityMissile missileObj)
    {
        if(missileObj.lockedTarget != null)
        {
            if(missileObj.lockedTarget.isDead)
            {
                missileObj.explode();
                return;
            }

            Pos3D target = new Pos3D(missileObj.lockedTarget);

            if(missileObj.lockedTarget instanceof ITarget && ((ITarget)missileObj.lockedTarget).getType() == TargetType.MISSILE)
            {
                if(((ITarget)missileObj.lockedTarget).canBeTargeted(this))
                {
                    target = ((ITarget)missileObj.lockedTarget).getPredictedPosition(4);
                }
            }
            else
            {
                target.translate(missileObj.lockedTarget.motionX * 4, missileObj.lockedTarget.motionY * 4, missileObj.lockedTarget.motionZ * 4);
            }

            double interceptRadius = Math.max(2.5D, Math.max(missileObj.lockedTarget.width, missileObj.lockedTarget.height) * 0.5D);

            if(missileObj.getDistanceSqToEntity(missileObj.lockedTarget) <= interceptRadius * interceptRadius)
            {
                if(FlansModIntegration.isAircraft(missileObj.lockedTarget))
                {
                    FlansModIntegration.interceptAircraft(missileObj.lockedTarget, missileObj);
                }

                missileObj.explode();
                return;
            }

            missileObj.motionX = (target.xPos - missileObj.posX) * (0.3F);
            missileObj.motionY = (target.yPos - missileObj.posY) * (0.3F);
            missileObj.motionZ = (target.zPos - missileObj.posZ) * (0.3F);

            return;
        }

        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(missileObj.posX - ABMRange, missileObj.posY - ABMRange, missileObj.posZ - ABMRange, missileObj.posX + ABMRange, missileObj.posY + ABMRange, missileObj.posZ + ABMRange);
        Entity nearestEntity = findNearestMissile(missileObj, bounds);

        if(nearestEntity == null)
        {
            nearestEntity = FlansModIntegration.findNearestAircraft(missileObj.worldObj, bounds, missileObj);
        }

        if(nearestEntity != null)
        {
            // Lock the interceptor onto the nearest valid missile or aircraft.
            missileObj.lockedTarget = nearestEntity;
            missileObj.didTargetLockBefore = true;
            missileObj.worldObj.playSoundAtEntity(missileObj, Reference.PREFIX + "targetlocked", 5F, 0.9F);
        }
        else {
            missileObj.motionX = missileObj.deltaPathX / missileObj.missileFlightTime;
            missileObj.motionZ = missileObj.deltaPathZ / missileObj.missileFlightTime;

            if(missileObj.didTargetLockBefore == true)
            {
                missileObj.explode();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Entity findNearestMissile(EntityMissile interceptor, AxisAlignedBB bounds)
    {
        List<Entity> candidates = interceptor.worldObj.getEntitiesWithinAABB(Entity.class, bounds);
        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for(Entity candidate : candidates)
        {
            if(candidate == interceptor || !(candidate instanceof ITarget))
            {
                continue;
            }

            ITarget target = (ITarget)candidate;

            if(target.getType() != TargetType.MISSILE || !target.canBeTargeted(this))
            {
                continue;
            }

            double distance = interceptor.getDistanceSqToEntity(candidate);

            if(distance < nearestDistance)
            {
                nearest = candidate;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    @Override
    public boolean isCruise()
    {
        return true;
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRepulsive(world, entity, x, y, z, 6).setDestroyItems().explode();
    }
}
