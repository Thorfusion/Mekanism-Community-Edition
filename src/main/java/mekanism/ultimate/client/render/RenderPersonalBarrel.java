package mekanism.ultimate.client.render;

import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Prevents the Personal Barrel from inheriting the Personal Chest TESR.
 *
 * <p>The barrel is rendered entirely by its baked block model. Its tile extends
 * the legacy Personal Chest solely to reuse secure inventory behavior, and the
 * renderer dispatcher otherwise also applies the animated chest model to the
 * subclass.</p>
 */
@SideOnly(Side.CLIENT)
public final class RenderPersonalBarrel extends TileEntitySpecialRenderer<TileEntityPersonalBarrel> {

    @Override
    public void render(TileEntityPersonalBarrel tile, double x, double y, double z,
          float partialTicks, int destroyStage, float alpha) {
        // Intentionally empty: the normal baked block model is the complete render.
    }
}
