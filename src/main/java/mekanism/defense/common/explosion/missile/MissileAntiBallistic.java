package defense.common.explosion.missile;

import mekanism.api.Pos3D;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.ITarget;
import defense.api.ITarget.TargetType;
import defense.client.model.missile.ModelAntiMissileMissile;
import defense.client.model.missile.ModelMissileBase;
import defense.common.Reference;
import defense.common.entity.EntityMissile;
import defense.common.explosive.blast.BlastRepulsive;

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
    	return new ModelAntiMissileMissile();
    }

    @Override
    public void update(EntityMissile missileObj)
    {
        if(missileObj.lockedTarget != null)
        {
            Pos3D target = new Pos3D(missileObj.lockedTarget);

            if(missileObj.lockedTarget.isDead)
            {
                missileObj.explode();
                return;
            }

            if(missileObj.lockedTarget instanceof ITarget && ((ITarget)missileObj.lockedTarget).getType() == TargetType.MISSILE)
            {
                if(((ITarget)missileObj.lockedTarget).canBeTargeted(this))
                {
                    target = ((ITarget)missileObj.lockedTarget).getPredictedPosition(4);
                }
            }

            missileObj.motionX = (target.xPos - missileObj.posX) * (0.3F);
            missileObj.motionY = (target.yPos - missileObj.posY) * (0.3F);
            missileObj.motionZ = (target.zPos - missileObj.posZ) * (0.3F);

            return;
        }

        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(missileObj.posX - ABMRange, missileObj.posY - ABMRange, missileObj.posZ - ABMRange, missileObj.posX + ABMRange, missileObj.posY + ABMRange, missileObj.posZ + ABMRange);
        // TODO: Check if this works.
        Entity nearestEntity = missileObj.worldObj.findNearestEntityWithinAABB(ITarget.class, bounds, missileObj);

        if(nearestEntity instanceof ITarget && ((ITarget) nearestEntity).getType() == TargetType.MISSILE)
        {
            if(((ITarget)nearestEntity).canBeTargeted(this))
            {
                // Lock target onto missileObj missile
                missileObj.lockedTarget = nearestEntity;
                missileObj.didTargetLockBefore = true;
                missileObj.worldObj.playSoundAtEntity(missileObj, Reference.PREFIX + "targetlocked", 5F, 0.9F);
            }
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
