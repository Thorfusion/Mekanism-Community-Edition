package defense.common.explosion.missile;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.IMissile;
import defense.client.model.missile.ModelCondensedMissile;
import defense.client.model.missile.ModelMissileBase;

public class MissileModule extends Missile
{
    public MissileModule()
    {
        super("missileModule", 1);
        this.hasBlock = false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelCondensedMissile();
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (entity instanceof IMissile)
        {
            ((IMissile) entity).dropMissileAsItem();
        }
    }
}
