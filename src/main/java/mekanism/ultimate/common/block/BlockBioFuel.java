package mekanism.ultimate.common.block;

import mekanism.common.Mekanism;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public final class BlockBioFuel extends Block {

    public BlockBioFuel() {
        super(Material.GRASS);
        setHardness(0.5F);
        setSoundType(SoundType.PLANT);
        setCreativeTab(Mekanism.tabMekanism);
    }
}
