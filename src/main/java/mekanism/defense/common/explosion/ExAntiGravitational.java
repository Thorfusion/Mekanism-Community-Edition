package defense.common.explosion;

import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.missile.ModelAntiGravitationalMissile;
import defense.client.model.missile.ModelMissileBase;
import defense.common.explosive.blast.BlastAntiGravitational;

public class ExAntiGravitational extends Explosion
{
    public ExAntiGravitational()
    {
        super("antiGravitational", 3);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelAntiGravitationalMissile();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "EEE", "ETE", "EEE", 'T', repulsive.getItemStack(), 'E', Items.ender_eye }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastAntiGravitational(world, entity, x, y, z, 30).explode();
    }
}
