package defense.common.explosion.missile;

import mekanism.api.Pos3D;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.IItemTracker;
import defense.client.model.missile.ModelHomingMissile;
import defense.client.model.missile.ModelMissileBase;
import defense.common.Vector2;
import defense.common.entity.EntityMissile;
import defense.common.entity.EntityMissile.MissileType;
import defense.common.explosive.blast.BlastRepulsive;

public class MissileHoming extends Missile
{
    public MissileHoming()
    {
        super("homing", 1);
        this.hasBlock = false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelHomingMissile();
    }

    @Override
    public void launch(EntityMissile missileObj)
    {
        if (!missileObj.worldObj.isRemote)
        {
            WorldServer worldServer = (WorldServer) missileObj.worldObj;
            Entity trackingEntity = worldServer.getEntityByID(missileObj.trackingVar);

            if (trackingEntity != null)
            {
                if (trackingEntity == missileObj)
                {
                    missileObj.setExplode();
                }

                missileObj.targetVector = new Pos3D(trackingEntity);
            }
        }
    }

    @Override
    public void update(EntityMissile missileObj)
    {
        if (missileObj.airborneTicks > missileObj.missileFlightTime / 2 && missileObj.missileType == MissileType.MISSILE)
        {
            WorldServer worldServer = (WorldServer) missileObj.worldObj;
            Entity trackingEntity = worldServer.getEntityByID(missileObj.trackingVar);

            if (trackingEntity != null)
            {
                if (trackingEntity.equals(missileObj))
                {
                    missileObj.setExplode();
                }

                missileObj.targetVector = new Pos3D(trackingEntity);

                missileObj.missileType = MissileType.CruiseMissile;

                missileObj.deltaPathX = missileObj.targetVector.xPos - missileObj.posX;
                missileObj.deltaPathY = missileObj.targetVector.yPos - missileObj.posY;
                missileObj.deltaPathZ = missileObj.targetVector.zPos - missileObj.posZ;

                missileObj.flatDistance = Vector2.distance(new Vector2(missileObj.startPos), new Vector2(missileObj.targetVector));
                missileObj.maxHeight = 150 + (int) (missileObj.flatDistance * 1.8);
                missileObj.missileFlightTime = (float) Math.max(100, 2.4 * missileObj.flatDistance);
                missileObj.acceleration = (float) missileObj.maxHeight * 2 / (missileObj.missileFlightTime * missileObj.missileFlightTime);

                if (missileObj.xiaoDanMotion.equals(new Pos3D()) || missileObj.xiaoDanMotion == null)
                {
                    float suDu = 0.3f;
                    missileObj.xiaoDanMotion = new Pos3D();
                    missileObj.xiaoDanMotion.xPos = missileObj.deltaPathX / (missileObj.missileFlightTime * suDu);
                    missileObj.xiaoDanMotion.yPos = missileObj.deltaPathY / (missileObj.missileFlightTime * suDu);
                    missileObj.xiaoDanMotion.zPos = missileObj.deltaPathZ / (missileObj.missileFlightTime * suDu);
                }
            }
        }
    }

    @Override
    public boolean onInteract(EntityMissile missileObj, EntityPlayer entityPlayer)
    {
        if (!missileObj.worldObj.isRemote && missileObj.airborneTicks <= 0)
        {
            if (entityPlayer.getCurrentEquippedItem() != null)
            {
                if (entityPlayer.getCurrentEquippedItem().getItem() instanceof IItemTracker)
                {
                    Entity trackingEntity = ((IItemTracker) entityPlayer.getCurrentEquippedItem().getItem()).getTrackingEntity(missileObj.worldObj, entityPlayer.getCurrentEquippedItem());

                    if (trackingEntity != null)
                    {
                        if (missileObj.trackingVar != trackingEntity.getEntityId())
                        {
                            missileObj.trackingVar = trackingEntity.getEntityId();
                            entityPlayer.addChatMessage(new ChatComponentText("Missile target locked to: " + trackingEntity.getCommandSenderName()));

                            if (missileObj.getLauncher() != null && missileObj.getLauncher().getController() != null)
                            {
                                Pos3D newTarget = new Pos3D(trackingEntity);
                                newTarget.yPos = 0;
                                missileObj.getLauncher().getController().setTarget(newTarget);
                            }

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean isCruise()
    {
        return false;
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRepulsive(world, entity, x, y, z, 4).setDestroyItems().explode();
    }
}
