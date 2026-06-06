package defense.common.explosion.missile;

import mekanism.api.Pos3D;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.missile.ModelClusterMissile;
import defense.client.model.missile.ModelMissileBase;
import defense.common.entity.EntityMissile;
import defense.common.entity.EntityMissile.MissileType;
import defense.common.explosive.blast.BlastRepulsive;

/** @author Calclavia */
public class MissileCluster extends Missile
{
    public static final int MAX_CLUSTER = 12;
    protected double spread = 20;

    public MissileCluster(String name, int tier)
    {
        super(name, tier);
        this.hasBlock = false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelClusterMissile();
    }

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
                    EntityMissile clusterMissile = new EntityMissile(missileObj.worldObj, position.clone(), position.clone(), 0);

                    double radius = spread;
                    double theta = 0;
                    double x = 0;
                    double y = 0;
                    double z = 0;
                    //TODO make spread equal to a 30 degree angle from center point

                    if (missileObj.missileCount > 0)
                    {
                        theta = (missileObj.missileCount / 12.0) * Math.PI * 2;
                        
                        x = radius * Math.cos(theta);
                        clusterMissile.posX += Math.cos(theta) * 5;
                        
                        z = radius * Math.sin(theta);
                        clusterMissile.posZ += Math.sin(theta) * 5;
                    }

                    clusterMissile.missileType = MissileType.CruiseMissile;
                    clusterMissile.protectionTime = 20 + missileObj.targetHeight - 1;

                    clusterMissile.launch(missileObj.targetVector.clone().translate(new Pos3D(x, y, z)));
                    missileObj.worldObj.spawnEntityInWorld(clusterMissile);
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
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRepulsive(world, entity, x, y, z, 6).setDestroyItems().explode();
    }

    @Override
    public boolean isCruise()
    {
        return false;
    }
}
