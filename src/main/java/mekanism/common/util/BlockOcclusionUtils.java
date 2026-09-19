package mekanism.common.util;

import mekanism.common.base.IBlockOcclusion;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public final class BlockOcclusionUtils
{
	private BlockOcclusionUtils() {}

	/**
	 * Checks whether the block at the supplied coordinates completely hides an
	 * adjacent face. Vanilla cannot answer this per metadata because
	 * {@link Block#isOpaqueCube()} has no world or metadata parameters.
	 */
	public static boolean isFullOpaqueCube(IBlockAccess world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);

		if(block instanceof IBlockOcclusion)
		{
			return ((IBlockOcclusion)block).isFullOpaqueCube(world, x, y, z);
		}

		return block.isOpaqueCube();
	}
}
