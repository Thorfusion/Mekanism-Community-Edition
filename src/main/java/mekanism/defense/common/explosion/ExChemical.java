package defense.common.explosion;

import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.missile.ModelChemicalMissile;
import defense.client.model.missile.ModelContagiousMissile;
import defense.client.model.missile.ModelMissileBase;
import defense.common.DefenseTechItems;
import defense.common.explosive.Explosive;
import defense.common.explosive.blast.BlastChemical;

public class ExChemical extends Explosion
{
    public ExChemical(String mingZi, int tier)
    {
        super(mingZi, tier);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	if(this.getTier() == 1)
    	{
    		return new ModelChemicalMissile();
    	}
    	else {
    		return new ModelContagiousMissile();
    	}
    }

    @Override
    public void init()
    {
        if (this.getTier() == 1)
        {
            GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "@@@", "@?@", "@@@", '@', DefenseTechItems.itemPoisonPowder, '?', Explosive.debilitation.getItemStack() }));
        }
        else if (this.getTier() == 2)
        {
            GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(2), new Object[] { " @ ", "@?@", " @ ", '?', Items.rotten_flesh, '@', Explosive.chemical.getItemStack() }));
        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getTier() == 1)
        {
            new BlastChemical(world, entity, x, y, z, 20, 20 * 30, false).setPoison().setRGB(0.8f, 0.8f, 0).explode();
        }
        else if (this.getTier() == 2)
        {
            new BlastChemical(world, entity, x, y, z, 20, 20 * 30, false).setContagious().setRGB(0.3f, 0.8f, 0).explode();
        }

    }

}
