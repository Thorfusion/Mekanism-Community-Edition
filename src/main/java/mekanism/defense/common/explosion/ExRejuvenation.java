package defense.common.explosion;

import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.missile.ModelMissileBase;
import defense.client.model.missile.ModelRegenMissile;
import defense.common.explosive.blast.BlastRegen;

public class ExRejuvenation extends Explosion
{
    public ExRejuvenation()
    {
        super("rejuvenation", 2);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	return new ModelRegenMissile();
    }

    @Override
    public void init()
    {
        GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "ICI", "CDC", "ICI", 'D', Blocks.diamond_block, 'C', Items.clock, 'I', Blocks.iron_block }));
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        new BlastRegen(world, entity, x, y, z, 16).doExplode();
    }
}
