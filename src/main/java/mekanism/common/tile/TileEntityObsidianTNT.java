package mekanism.common.tile;

import mekanism.api.MekanismConfig.mekce_client;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//For a TESR
public class TileEntityObsidianTNT extends TileEntity
{
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		double distance = Math.max(1, mekce_client.tesrGlobalRenderDistance);
		return distance * distance;
	}

	@Override
	public boolean canUpdate()
	{
		return false;
	}
}
