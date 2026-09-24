package mekanism.nuclear.common.block;

import mekanism.common.Mekanism;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/** Reinforced frame and face block for the Supercritical Phase Shifter. */
public class BlockSPSCasing extends Block {

    public BlockSPSCasing() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(16F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(Mekanism.tabMekanism);
    }
}
