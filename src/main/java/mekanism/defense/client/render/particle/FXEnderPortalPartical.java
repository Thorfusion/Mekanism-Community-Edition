package defense.client.render.particle;

import mekanism.api.Pos3D;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXEnderPortalPartical extends EntityPortalFX
{
    public FXEnderPortalPartical(World par1World, Pos3D position, float red, float green, float blue, float scale, double distance)
    {
        super(par1World, position.xPos, position.yPos, position.zPos, 0, 0, 0);
        this.particleScale = scale;
        try
        {
            ReflectionHelper.setPrivateValue(EntityPortalFX.class, this, this.particleScale, 0);
        }
        catch (Exception e)
        {
            FMLLog.warning("Failed to correctly spawn portal effects.");
        }
        this.renderDistanceWeight = distance;
    }

}
