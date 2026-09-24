package mekanism.nuclear.common;

import mekanism.nuclear.common.block.BlockIsotopicCentrifuge;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(MekanismNuclear.MODID)
public final class NuclearBlocks {

    public static final Block IsotopicCentrifuge = new BlockIsotopicCentrifuge();

    private NuclearBlocks() {
    }

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(IsotopicCentrifuge.setTranslationKey("IsotopicCentrifuge")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "isotopic_centrifuge")));
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlock(IsotopicCentrifuge).setRegistryName(IsotopicCentrifuge.getRegistryName()));
    }
}
