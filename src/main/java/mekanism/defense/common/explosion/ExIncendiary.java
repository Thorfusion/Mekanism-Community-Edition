package defense.common.explosion;

import mekanism.api.Pos3D;
import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.missile.ModelIncendiaryMissile;
import defense.client.model.missile.ModelMissileBase;
import defense.common.explosive.blast.BlastFire;

public class ExIncendiary extends Explosion
{
    public ExIncendiary(String mingZi, int tier)
    {
        super(mingZi, tier);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelIncendiaryMissile();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@!@", '@', "dustSulfur", '?', repulsive.getItemStack(), '!', Items.lava_bucket }));
    }

    @Override
    public void onDetonation(World worldObj, Pos3D position, int fuseTicks)
    {
        super.onDetonation(worldObj, position, fuseTicks);
        worldObj.spawnParticle("lava", position.xPos, position.yPos + 0.5D, position.zPos, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastFire(world, entity, x, y, z, 14).explode();
    }
}
