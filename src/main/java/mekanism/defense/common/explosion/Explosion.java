package defense.common.explosion;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.common.Reference;
import defense.common.entity.EntityMissile;
import defense.common.explosive.Explosive;

public abstract class Explosion extends Explosive
{
    @SideOnly(Side.CLIENT)
    private ResourceLocation resourceLocation;

    public Explosion(String name, int tier)
    {
        super(name, tier);
    }

    /** Called when launched. */
    public void launch(EntityMissile missileObj) {}

    /** Called every tick while flying. */
    public void update(EntityMissile missileObj) {}

    public boolean onInteract(EntityMissile missileObj, EntityPlayer entityPlayer)
    {
        return false;
    }

    /** Is this missile compatible with the cruise launcher?
     * 
     * @return */
    public boolean isCruise()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getMissileResource()
    {
        if(resourceLocation == null)
        {
            resourceLocation = new ResourceLocation(Reference.DOMAIN, Reference.MODEL_TEXTURE_PATH + "missile_" + this.getUnlocalizedName() + ".png");
        }

        return resourceLocation;
    }
}
