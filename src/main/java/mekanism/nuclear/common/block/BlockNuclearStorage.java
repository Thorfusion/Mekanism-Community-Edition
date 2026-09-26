package mekanism.nuclear.common.block;

import mekanism.common.Mekanism;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/** A dense mineral storage block with the stable Fluorite block properties. */
public class BlockNuclearStorage extends Block {

    public BlockNuclearStorage() {
        super(Material.ROCK);
        setHardness(5F);
        setResistance(9F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(Mekanism.tabMekanism);
    }
}
