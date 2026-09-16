package defense.client.model.missile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

/** Anvil-derived T4 airframe with a compact layered containment warhead. */
@SideOnly(Side.CLIENT)
public class ModelAntimatterMissile extends ModelT4AnvilMissile
{
    public ModelAntimatterMissile()
    {
        int textureX = 32;
        int textureY = 128;

        warheadModel = new ModelRendererTurbo[4];
        warheadModel[0] = new ModelRendererTurbo(this, 0, 76, textureX, textureY);
        warheadModel[1] = new ModelRendererTurbo(this, 0, 84, textureX, textureY);
        warheadModel[2] = new ModelRendererTurbo(this, 0, 97, textureX, textureY);
        warheadModel[3] = new ModelRendererTurbo(this, 24, 76, textureX, textureY);

        warheadModel[0].addBox(-3F, -1F, -3F, 6, 2, 6, 0F);
        warheadModel[0].setRotationPoint(0F, -21F, 0F);

        warheadModel[1].addBox(-4F, -1F, -4F, 8, 5, 8, 0F);
        warheadModel[1].setRotationPoint(0F, -26F, 0F);

        warheadModel[2].addBox(-3F, -1F, -3F, 6, 3, 6, 0F);
        warheadModel[2].setRotationPoint(0F, -29F, 0F);

        warheadModel[3].addBox(-1F, -1F, -1F, 2, 3, 2, 0F);
        warheadModel[3].setRotationPoint(0F, -32F, 0F);
    }
}
