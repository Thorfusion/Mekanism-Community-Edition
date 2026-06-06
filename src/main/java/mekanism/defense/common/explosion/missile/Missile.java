package defense.common.explosion.missile;

import net.minecraft.item.ItemStack;
import defense.common.DefenseTechItems;
import defense.common.explosion.Explosion;

/** Ex object that are only defined as missiles
 * 
 * @author Calclavia */
public abstract class Missile extends Explosion
{
    public Missile(String name, int tier)
    {
        super(name, tier);
        
        hasBlock = false;
        hasGrenade = false;
        hasMinecart = false;
    }

    @Override
    public ItemStack getItemStack()
    {
        return new ItemStack(DefenseTechItems.itemMissile, 1, getID());
    }
}
