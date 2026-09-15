package defense.client.model.missile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.client.tmt.ModelRendererTurbo;

/**
 * Keeps the approved Anvil missile airframe and its four side boosters while
 * allowing T4 payloads to replace the Anvil-specific fist/rock warhead.
 */
@SideOnly(Side.CLIENT)
public abstract class ModelT4AnvilMissile extends ModelAnvilMissile
{
    protected ModelRendererTurbo[] warheadModel;

    protected ModelT4AnvilMissile()
    {
        // ModelAnvilMissile indices 25..39 form the payload-specific fist.
        // The central airframe, fins, and side boosters remain untouched.
        for(int index = 25; index <= 39; index++)
        {
            bodyModel[index] = null;
        }
    }

    @Override
    public void render(float scale)
    {
        super.render(scale);

        if(warheadModel != null)
        {
            for(ModelRendererTurbo model : warheadModel)
            {
                if(model != null)
                {
                    model.render(scale);
                }
            }
        }
    }
}
