package defense.common.explosion.missile;

import defense.common.entity.EntityMissile;
import defense.common.entity.EntityMissile.MissileType;
import defense.common.explosive.Explosive;
import defense.common.explosive.blast.BlastNuclear;
import mekanism.api.Pos3D;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class MissileNuclearCluster extends MissileCluster
{
    public MissileNuclearCluster()
    {
        super("nuclearCluster", 3);
        this.hasBlock = false;
    }

    public static final int MAX_CLUSTER = 4;

    @Override
    public void update(EntityMissile missileObj)
    {
        if (missileObj.motionY < -0.5)
        {
            if (missileObj.missileCount < MAX_CLUSTER)
            {
                if (!missileObj.worldObj.isRemote)
                {
                    Pos3D position = new Pos3D(missileObj);
                    EntityMissile clusterMissile = new EntityMissile(missileObj.worldObj, position, new Pos3D(missileObj), Explosive.nuclear.getID());
                    missileObj.worldObj.spawnEntityInWorld(clusterMissile);
                    clusterMissile.missileType = MissileType.CruiseMissile;
                    clusterMissile.protectionTime = 20;
                    clusterMissile.launch(missileObj.targetVector.clone().translate(new Pos3D((missileObj.missileCount - MAX_CLUSTER / 2) * Math.random() * 30, (missileObj.missileCount - MAX_CLUSTER / 2) * Math.random() * 30, (missileObj.missileCount - MAX_CLUSTER / 2) * Math.random() * 30)));
                }

                missileObj.protectionTime = 20;
                missileObj.missileCount++;
            }
            else
            {
                missileObj.setDead();
            }
        }
    }

    @Override
    public void createExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastNuclear(world, entity, x, y, z, 30, 50).setNuclear().explode();
    }

    @Override
    public boolean isCruise()
    {
        return false;
    }
}
