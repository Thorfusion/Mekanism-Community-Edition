package mekanism.nuclear.common.content.sps;

import mekanism.nuclear.common.NuclearBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

/** Block roles understood by the fixed-size SPS validator. */
public enum SPSComponent {
    AIR,
    CASING,
    PORT,
    COIL,
    INVALID,
    UNLOADED;

    public static SPSComponent fromState(IBlockState state) {
        if (state == null || state.getMaterial() == Material.AIR) {
            return AIR;
        }
        Block block = state.getBlock();
        if (block == NuclearBlocks.SPSCasing) {
            return CASING;
        } else if (block == NuclearBlocks.SPSPort) {
            return PORT;
        } else if (block == NuclearBlocks.SuperchargedCoil) {
            return COIL;
        }
        return INVALID;
    }
}
