package mekanism.ultimate.common;

import mekanism.ultimate.common.block.BlockUltimateFactory;
import mekanism.ultimate.common.item.ItemBlockUltimateFactory;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(MekanismUltimate.MODID)
public final class UltimateBlocks {

    public static final Block UltimateFactory = new BlockUltimateFactory();

    private UltimateBlocks() {
    }

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(UltimateFactory.setTranslationKey("UltimateFactory")
              .setRegistryName(new ResourceLocation(MekanismUltimate.MODID, "ultimate_factory")));
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlockUltimateFactory(UltimateFactory)
              .setRegistryName(UltimateFactory.getRegistryName()));
    }
}
