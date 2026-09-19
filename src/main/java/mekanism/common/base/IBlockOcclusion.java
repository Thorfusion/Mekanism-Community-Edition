package mekanism.common.base;

import net.minecraft.world.IBlockAccess;

/**
 * Allows blocks which store multiple shapes in metadata to report whether a
 * particular block in the world completely covers all six faces.
 */
public interface IBlockOcclusion
{
	public boolean isFullOpaqueCube(IBlockAccess world, int x, int y, int z);
}
