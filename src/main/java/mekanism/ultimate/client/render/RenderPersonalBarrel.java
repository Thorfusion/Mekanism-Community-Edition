package mekanism.ultimate.client.render;

import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/** Prevents the inherited Personal Chest renderer from drawing over the cube. */
public final class RenderPersonalBarrel extends TileEntitySpecialRenderer
{
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y,
          double z, float partialTick)
    {
        // The barrel is rendered entirely by the normal block renderer.
    }
}
