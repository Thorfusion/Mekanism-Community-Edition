package defense.common.explosion;

import mekanism.common.recipe.ShapedMekanismRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.missile.ModelMissileBase;
import defense.client.model.missile.ModelThermobaricMissile;
import defense.client.model.missile.ModelNuclearMissile;
import defense.common.explosive.Explosive;
import defense.common.explosive.blast.BlastNuclear;

public class ExNuclear extends Explosion
{
    public ExNuclear(String mingZi, int tier)
    {
        super(mingZi, tier);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ModelMissileBase getMissileModel()
    {
    	if(getTier() == 3)
    	{
    		return new ModelNuclearMissile();
    	}
    	else {
    		return new ModelThermobaricMissile();
    	}
    }

    @Override
    public void init()
    {
        if (this.getTier() == 3)
        {
            if (OreDictionary.getOres("ingotUranium").size() > 0)
            {
                GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "UUU", "UEU", "UUU", 'E', thermobaric.getItemStack(), 'U', "ingotUranium" }));
            }
            else
            {
                GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "EEE", "EEE", "EEE", 'E', thermobaric.getItemStack() }));

            }
        }
        else
        {
            GameRegistry.addRecipe(new ShapedMekanismRecipe(this.getItemStack(), new Object[] { "CIC", "IRI", "CIC", 'R', Explosive.repulsive.getItemStack(), 'C', Explosive.chemical.getItemStack(), 'I', Explosive.incendiary.getItemStack() }));

        }
    }

    @Override
    public void doCreateExplosion(World world, double x, double y, double z, Entity entity)
    {
        if (this.getTier() == 3)
        {
            new BlastNuclear(world, entity, x, y, z, 50, 80).setNuclear().explode();
        }
        else
        {
            new BlastNuclear(world, entity, x, y, z, 30, 45).explode();
        }
    }
}
