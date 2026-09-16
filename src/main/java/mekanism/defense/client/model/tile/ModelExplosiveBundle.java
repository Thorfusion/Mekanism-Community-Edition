package defense.client.model.tile;

import mekanism.client.model.ModelObsidianTNT;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.model.missile.ModelMissileBase;

/**
 * DefenseTech adapter for Mekanism's shared nine-charge explosive geometry.
 * Payloads provide their own original DefenseTech texture atlases.
 */
@SideOnly(Side.CLIENT)
public class ModelExplosiveBundle extends ModelMissileBase
{
    public static final ModelExplosiveBundle INSTANCE = new ModelExplosiveBundle();

    private final ModelObsidianTNT model = new ModelObsidianTNT();

    private ModelExplosiveBundle() {}

    @Override
    public void render(float size)
    {
        model.render(size);
    }
}
