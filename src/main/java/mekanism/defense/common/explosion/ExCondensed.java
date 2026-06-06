package defense.common.explosion;

import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.missile.ModelCondensedMissile;
import defense.client.model.missile.ModelMissileBase;
import defense.common.explosive.blast.BlastRepulsive;

public class ExCondensed extends Explosion
{
    public ExCondensed(String mingZi, int tier)
    {
        super(mingZi, tier);
        this.setFuseTime(1);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelCondensedMissile();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(3), new Object[] { "@?@", '@', Blocks.tnt, '?', Items.redstone }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRepulsive(world, entity, x, y, z, 2.5f).explode();
    }
}
