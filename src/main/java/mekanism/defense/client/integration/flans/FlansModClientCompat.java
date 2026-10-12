package defense.client.integration.flans;

import com.flansmod.client.model.ModelBomb;
import com.flansmod.common.guns.BulletType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.common.integration.flans.FlansModCompat;

/** Client-only model setup for the optional aircraft bombs. */
@SideOnly(Side.CLIENT)
public final class FlansModClientCompat
{
    private FlansModClientCompat() {}

    public static void init()
    {
        ModelBomb model = new ModelBomb();

        for(BulletType type : FlansModCompat.getBombTypes())
        {
            type.model = model;
            type.texture = "defaultBullet";
        }
    }
}
