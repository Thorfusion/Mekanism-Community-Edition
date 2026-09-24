package mekanism.nuclear.common;

import mekanism.nuclear.common.block.BlockIsotopicCentrifuge;
import mekanism.nuclear.common.block.BlockNuclearOre;
import mekanism.nuclear.common.block.BlockRadioactiveWasteBarrel;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(MekanismNuclear.MODID)
public final class NuclearBlocks {

    public static final Block IsotopicCentrifuge = new BlockIsotopicCentrifuge();
    public static final Block RadioactiveWasteBarrel = new BlockRadioactiveWasteBarrel();
    public static final BlockNuclearOre UraniumOre = new BlockNuclearOre(NuclearOreType.URANIUM);
    public static final BlockNuclearOre FluoriteOre = new BlockNuclearOre(NuclearOreType.FLUORITE);

    private NuclearBlocks() {
    }

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(IsotopicCentrifuge.setTranslationKey("IsotopicCentrifuge")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "isotopic_centrifuge")));
        registry.register(RadioactiveWasteBarrel.setTranslationKey("RadioactiveWasteBarrel")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "radioactive_waste_barrel")));
        registry.register(UraniumOre.setTranslationKey("UraniumOre")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "uranium_ore")));
        registry.register(FluoriteOre.setTranslationKey("FluoriteOre")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "fluorite_ore")));
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlock(IsotopicCentrifuge).setRegistryName(IsotopicCentrifuge.getRegistryName()));
        registry.register(new ItemBlock(RadioactiveWasteBarrel).setRegistryName(RadioactiveWasteBarrel.getRegistryName()));
        ItemBlock uraniumOre = new ItemBlock(UraniumOre);
        uraniumOre.setRegistryName(UraniumOre.getRegistryName());
        ItemBlock fluoriteOre = new ItemBlock(FluoriteOre);
        fluoriteOre.setRegistryName(FluoriteOre.getRegistryName());
        registry.register(uraniumOre);
        registry.register(fluoriteOre);
        NuclearOreDictionary.registerOreBlocks(uraniumOre, fluoriteOre);
    }
}
