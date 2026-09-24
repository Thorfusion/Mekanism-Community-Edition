package mekanism.nuclear.common;

import mekanism.nuclear.common.block.BlockIsotopicCentrifuge;
import mekanism.nuclear.common.block.BlockFissionReactorComponent;
import mekanism.nuclear.common.block.BlockFissionReactorPort;
import mekanism.nuclear.common.block.BlockNuclearOre;
import mekanism.nuclear.common.block.BlockRadioactiveWasteBarrel;
import mekanism.nuclear.common.content.fission.FissionReactorComponent;
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
    public static final Block FissionReactorCasing = new BlockFissionReactorComponent(FissionReactorComponent.CASING);
    public static final Block ReactorGlass = new BlockFissionReactorComponent(FissionReactorComponent.GLASS);
    public static final Block FissionReactorPort = new BlockFissionReactorPort();
    public static final Block FissionReactorLogicAdapter = new BlockFissionReactorComponent(FissionReactorComponent.LOGIC_ADAPTER);
    public static final Block FissionFuelAssembly = new BlockFissionReactorComponent(FissionReactorComponent.FUEL_ASSEMBLY);
    public static final Block ControlRodAssembly = new BlockFissionReactorComponent(FissionReactorComponent.CONTROL_ROD);

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
        registry.register(FissionReactorCasing.setTranslationKey("FissionReactorCasing")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "fission_reactor_casing")));
        registry.register(ReactorGlass.setTranslationKey("ReactorGlass")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "reactor_glass")));
        registry.register(FissionReactorPort.setTranslationKey("FissionReactorPort")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "fission_reactor_port")));
        registry.register(FissionReactorLogicAdapter.setTranslationKey("FissionReactorLogicAdapter")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "fission_reactor_logic_adapter")));
        registry.register(FissionFuelAssembly.setTranslationKey("FissionFuelAssembly")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "fission_fuel_assembly")));
        registry.register(ControlRodAssembly.setTranslationKey("ControlRodAssembly")
              .setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "control_rod_assembly")));
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
        registerItemBlock(registry, FissionReactorCasing);
        registerItemBlock(registry, ReactorGlass);
        registerItemBlock(registry, FissionReactorPort);
        registerItemBlock(registry, FissionReactorLogicAdapter);
        registerItemBlock(registry, FissionFuelAssembly);
        registerItemBlock(registry, ControlRodAssembly);
        NuclearOreDictionary.registerOreBlocks(uraniumOre, fluoriteOre);
    }

    private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {
        registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }
}
