package defense.common.potion;

import defense.common.Settings;
import net.minecraft.potion.Potion;

public abstract class CustomPotion extends Potion
{
    /** Creates a new type of potion
     * 
     * @param id - The ID of this potion. Make it greater than 20.
     * @param isBadEffect - Is this potion a good potion or a bad one?
     * @param color - The color of this potion.
     * @param name - The name of this potion. */
    public CustomPotion(int id, boolean isBadEffect, int color, String name)
    {
        super(getPotionID(name, id), isBadEffect, color);
        this.setPotionName("potion." + name);
        Potion.potionTypes[this.getId()] = this;
    }

    public static int getPotionID(String name, int id)
    {
        Settings.CONFIGURATION.load();
        int finalID = Settings.CONFIGURATION.get("Potion ID", name + " ID", id).getInt(id);
        Settings.CONFIGURATION.save();
        return finalID;
    }

    @Override
    public Potion setIconIndex(int par1, int par2)
    {
        super.setIconIndex(par1, par2);
        return this;
    }

    @Override
    protected Potion setEffectiveness(double par1)
    {
        super.setEffectiveness(par1);
        return this;
    }
}
