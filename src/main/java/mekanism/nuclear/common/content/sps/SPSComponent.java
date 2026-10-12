package mekanism.nuclear.common.content.sps;

import mekanism.nuclear.common.NuclearBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

/** Block roles understood by the fixed-size SPS validator. */
public enum SPSComponent
{
    AIR,
    CASING,
    PORT,
    COIL,
    INVALID,
    UNLOADED;

    public static SPSComponent fromWorld(IBlockAccess world,
          SPSPosition position)
    {
        return fromBlock(world.getBlock(position.getX(), position.getY(),
              position.getZ()));
    }

    public static SPSComponent fromBlock(Block block)
    {
        if (block == null || block == Blocks.air)
        {
            return AIR;
        }
        if (block == NuclearBlocks.SPSCasing)
        {
            return CASING;
        }
        if (block == NuclearBlocks.SPSPort)
        {
            return PORT;
        }
        if (block == NuclearBlocks.SuperchargedCoil)
        {
            return COIL;
        }
        return INVALID;
    }
}
