package mekanism.nuclear.common;

import cpw.mods.fml.common.registry.GameRegistry;
import mekanism.common.Mekanism;
import mekanism.nuclear.common.block.BlockAntiprotonicNucleosynthesizer;
import mekanism.nuclear.common.block.BlockIsotopicCentrifuge;
import mekanism.nuclear.common.block.BlockFissionReactorComponent;
import mekanism.nuclear.common.block.BlockFissionReactorLogicAdapter;
import mekanism.nuclear.common.block.BlockFissionReactorPort;
import mekanism.nuclear.common.block.BlockNuclearOre;
import mekanism.nuclear.common.block.BlockNuclearStorage;
import mekanism.nuclear.common.block.BlockRadioactiveWasteBarrel;
import mekanism.nuclear.common.block.BlockSPSCasing;
import mekanism.nuclear.common.block.BlockSPSPort;
import mekanism.nuclear.common.block.BlockSuperchargedCoil;
import mekanism.nuclear.common.content.fission.FissionReactorComponent;
import mekanism.nuclear.common.item.ItemBlockAntiprotonicNucleosynthesizer;
import net.minecraft.block.Block;

public final class NuclearBlocks
{
    public static final Block IsotopicCentrifuge = new BlockIsotopicCentrifuge()
          .setBlockName("IsotopicCentrifuge");
    public static final Block AntiprotonicNucleosynthesizer =
          new BlockAntiprotonicNucleosynthesizer()
                .setBlockName("AntiprotonicNucleosynthesizer");
    public static final BlockNuclearOre UraniumOre = (BlockNuclearOre)new BlockNuclearOre(
          NuclearOreType.URANIUM).setBlockName("UraniumOre");
    public static final BlockNuclearOre FluoriteOre = (BlockNuclearOre)new BlockNuclearOre(
          NuclearOreType.FLUORITE).setBlockName("FluoriteOre");
    public static final Block FluoriteBlock = new BlockNuclearStorage()
          .setBlockName("FluoriteBlock");
    public static final Block RadioactiveWasteBarrel =
          new BlockRadioactiveWasteBarrel().setBlockName("RadioactiveWasteBarrel");
    public static final Block FissionReactorCasing =
          new BlockFissionReactorComponent(FissionReactorComponent.CASING)
                .setBlockName("FissionReactorCasing");
    public static final Block FissionReactorPort =
          new BlockFissionReactorPort().setBlockName("FissionReactorPort");
    public static final Block FissionReactorLogicAdapter =
          new BlockFissionReactorLogicAdapter()
                .setBlockName("FissionReactorLogicAdapter");
    public static final Block FissionFuelAssembly =
          new BlockFissionReactorComponent(FissionReactorComponent.FUEL_ASSEMBLY)
                .setBlockName("FissionFuelAssembly");
    public static final Block ControlRodAssembly =
          new BlockFissionReactorComponent(FissionReactorComponent.CONTROL_ROD)
                .setBlockName("ControlRodAssembly");
    public static final Block SPSCasing = new BlockSPSCasing()
          .setBlockName("SPSCasing");
    public static final Block SPSPort = new BlockSPSPort()
          .setBlockName("SPSPort");
    public static final Block SuperchargedCoil = new BlockSuperchargedCoil()
          .setBlockName("SuperchargedCoil");

    private NuclearBlocks()
    {
    }

    public static void register()
    {
        UraniumOre.setCreativeTab(Mekanism.tabMekanism);
        FluoriteOre.setCreativeTab(Mekanism.tabMekanism);
        FluoriteBlock.setCreativeTab(Mekanism.tabMekanism);
        RadioactiveWasteBarrel.setCreativeTab(Mekanism.tabMekanism);
        FissionReactorCasing.setCreativeTab(Mekanism.tabMekanism);
        FissionReactorPort.setCreativeTab(Mekanism.tabMekanism);
        FissionReactorLogicAdapter.setCreativeTab(Mekanism.tabMekanism);
        FissionFuelAssembly.setCreativeTab(Mekanism.tabMekanism);
        ControlRodAssembly.setCreativeTab(Mekanism.tabMekanism);
        SPSCasing.setCreativeTab(Mekanism.tabMekanism);
        SPSPort.setCreativeTab(Mekanism.tabMekanism);
        SuperchargedCoil.setCreativeTab(Mekanism.tabMekanism);
        GameRegistry.registerBlock(IsotopicCentrifuge, "isotopic_centrifuge");
        GameRegistry.registerBlock(AntiprotonicNucleosynthesizer,
              ItemBlockAntiprotonicNucleosynthesizer.class,
              "antiprotonic_nucleosynthesizer");
        GameRegistry.registerBlock(UraniumOre, "uranium_ore");
        GameRegistry.registerBlock(FluoriteOre, "fluorite_ore");
        GameRegistry.registerBlock(FluoriteBlock, "block_fluorite");
        GameRegistry.registerBlock(RadioactiveWasteBarrel,
              "radioactive_waste_barrel");
        GameRegistry.registerBlock(FissionReactorCasing,
              "fission_reactor_casing");
        GameRegistry.registerBlock(FissionReactorPort,
              "fission_reactor_port");
        GameRegistry.registerBlock(FissionReactorLogicAdapter,
              "fission_reactor_logic_adapter");
        GameRegistry.registerBlock(FissionFuelAssembly,
              "fission_fuel_assembly");
        GameRegistry.registerBlock(ControlRodAssembly,
              "control_rod_assembly");
        GameRegistry.registerBlock(SPSCasing, "sps_casing");
        GameRegistry.registerBlock(SPSPort, "sps_port");
        GameRegistry.registerBlock(SuperchargedCoil, "supercharged_coil");
        NuclearOreDictionary.registerOreBlocks(UraniumOre, FluoriteOre, FluoriteBlock);
    }
}
