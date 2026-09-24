package mekanism.mekasuit.common;

import mekanism.mekasuit.common.block.BlockModificationStation;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

/** Blocks owned and packaged by the MekaSuit module. */
public final class MekaSuitBlocks {

    public static final Block ModificationStation = new BlockModificationStation();

    private MekaSuitBlocks() {
    }

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(ModificationStation.setTranslationKey("ModificationStation")
              .setRegistryName(new ResourceLocation(MekanismMekaSuit.MODID, "modification_station")));
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlock(ModificationStation).setRegistryName(ModificationStation.getRegistryName()));
    }
}
