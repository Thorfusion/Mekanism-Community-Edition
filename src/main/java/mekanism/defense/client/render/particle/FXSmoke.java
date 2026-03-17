package defense.client.render.particle;

import mekanism.api.Pos3D;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXSmoke extends EntitySmokeFX
{
    public FXSmoke(World par1World, Pos3D position, float red, float green, float blue, float scale, double distance)
    {
        super(par1World, position.xPos, position.yPos, position.zPos, 0, 0, 0, scale);
        this.renderDistanceWeight = distance;
        this.particleRed = red;
        this.particleBlue = blue;
        this.particleGreen = green;

        float colorVarient = (float) (Math.random() * 0.90000001192092896D);
        this.particleRed *= colorVarient;
        this.particleBlue *= colorVarient;
        this.particleGreen *= colorVarient;
    }

    public FXSmoke setAge(int age)
    {
        this.particleMaxAge = age;
        return this;
    }
}
