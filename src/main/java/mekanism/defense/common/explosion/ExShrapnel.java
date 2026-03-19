package defense.common.explosion;

import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.missile.ModelAnvilMissile;
import defense.client.model.missile.ModelFragmentationMissile;
import defense.client.model.missile.ModelMissileBase;
import defense.client.model.missile.ModelShrapnelMissile;
import defense.common.explosive.Explosive;
import defense.common.explosive.blast.BlastShrapnel;

public class ExShrapnel extends Explosion
{
    public ExShrapnel(String name, int tier)
    {
        super(name, tier);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	if (getUnlocalizedName().equalsIgnoreCase("shrapnel"))
        {
            return new ModelShrapnelMissile();
        }
    	else if (getUnlocalizedName().equalsIgnoreCase("anvil"))
    	{
    		return new ModelAnvilMissile();
    	}
        else {
            return new ModelFragmentationMissile();
        }
    }

    @Override
    public void init()
    {
        if (this.getID() == Explosive.shrapnel.getID())
        {
            GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "???", "?@?", "???", '@', repulsive.getItemStack(), '?', Items.arrow }));
        }
        else if (this.getID() == Explosive.anvil.getID())
        {
            GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(10), new Object[] { "SSS", "SAS", "SSS", 'A', Blocks.anvil, 'S', Explosive.shrapnel.getItemStack() }));
        }
        else if (this.getID() == Explosive.fragmentation.getID())
        {
            GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { " @ ", "@?@", " @ ", '?', incendiary.getItemStack(), '@', Explosive.shrapnel.getItemStack() }));
        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getTier() == 2)
        {
            new BlastShrapnel(world, entity, x, y, z, 15, true, true, false).explode();
        }
        else if (this.getID() == Explosive.anvil.getID())
        {
            new BlastShrapnel(world, entity, x, y, z, 25, false, false, true).explode();
        }
        else
        {
            new BlastShrapnel(world, entity, x, y, z, 30, true, false, false).explode();
        }
    }
}
