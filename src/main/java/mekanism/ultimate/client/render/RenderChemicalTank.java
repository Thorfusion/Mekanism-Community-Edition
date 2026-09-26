package mekanism.ultimate.client.render;

import mekanism.client.render.MekanismRenderer;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderChemicalTank extends TileEntitySpecialRenderer<TileEntityChemicalTank> {

    @Override
    public void render(TileEntityChemicalTank tile, double x, double y, double z,
          float partialTicks, int destroyStage, float alpha) {
        MekanismRenderer.machineRenderer().render(tile, x, y, z, partialTicks, destroyStage, alpha);
    }
}
