package mekanism.ultimate.common;

import mekanism.ultimate.common.block.BlockUltimateFactory;
import mekanism.ultimate.common.block.BlockNutritionalLiquifier;
import mekanism.ultimate.common.block.BlockChemicalTank;
import mekanism.ultimate.common.block.BlockIndustrialAlarm;
import mekanism.ultimate.common.block.BlockPersonalBarrel;
import mekanism.ultimate.common.block.BlockBioFuel;
import mekanism.ultimate.common.block.BlockPigmentMachine;
import mekanism.ultimate.common.block.BlockDimensionalStabilizer;
import mekanism.ultimate.common.tile.TileEntityPaintingMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentExtractor;
import mekanism.ultimate.common.tile.TileEntityPigmentMixer;
import mekanism.ultimate.common.item.ItemBlockUltimateFactory;
import mekanism.ultimate.common.item.ItemBlockChemicalTank;
import mekanism.ultimate.common.item.ItemBlockPersonalBarrel;
import mekanism.ultimate.common.tier.ChemicalTankTier;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(MekanismUltimate.MODID)
public final class UltimateBlocks {

    public static final Block UltimateFactory = new BlockUltimateFactory();
    public static final Block NutritionalLiquifier = new BlockNutritionalLiquifier();
    public static final BlockPersonalBarrel PersonalBarrel = new BlockPersonalBarrel();
    public static final BlockIndustrialAlarm IndustrialAlarm = new BlockIndustrialAlarm();
    public static final Block BioFuelBlock = new BlockBioFuel();
    public static final BlockPigmentMachine PigmentExtractor =
          new BlockPigmentMachine(TileEntityPigmentExtractor::new, 4);
    public static final BlockPigmentMachine PigmentMixer =
          new BlockPigmentMachine(TileEntityPigmentMixer::new, 5);
    public static final BlockPigmentMachine PaintingMachine =
          new BlockPigmentMachine(TileEntityPaintingMachine::new, 6);
    public static final BlockDimensionalStabilizer DimensionalStabilizer =
          new BlockDimensionalStabilizer();
    public static final BlockChemicalTank BasicChemicalTank = new BlockChemicalTank(ChemicalTankTier.BASIC);
    public static final BlockChemicalTank AdvancedChemicalTank = new BlockChemicalTank(ChemicalTankTier.ADVANCED);
    public static final BlockChemicalTank EliteChemicalTank = new BlockChemicalTank(ChemicalTankTier.ELITE);
    public static final BlockChemicalTank UltimateChemicalTank = new BlockChemicalTank(ChemicalTankTier.ULTIMATE);
    public static final BlockChemicalTank CreativeChemicalTank = new BlockChemicalTank(ChemicalTankTier.CREATIVE);

    private static final BlockChemicalTank[] CHEMICAL_TANKS = {
          BasicChemicalTank, AdvancedChemicalTank, EliteChemicalTank,
          UltimateChemicalTank, CreativeChemicalTank
    };

    private UltimateBlocks() {
    }

    public static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(UltimateFactory.setTranslationKey("UltimateFactory")
              .setRegistryName(new ResourceLocation(MekanismUltimate.MODID, "ultimate_factory")));
        registry.register(NutritionalLiquifier.setTranslationKey("NutritionalLiquifier")
              .setRegistryName(new ResourceLocation(MekanismUltimate.MODID, "nutritional_liquifier")));
        registry.register(PersonalBarrel.setTranslationKey("PersonalBarrel")
              .setRegistryName(new ResourceLocation(MekanismUltimate.MODID, "personal_barrel")));
        registry.register(IndustrialAlarm.setTranslationKey("IndustrialAlarm")
              .setRegistryName(new ResourceLocation(MekanismUltimate.MODID, "industrial_alarm")));
        registry.register(BioFuelBlock.setTranslationKey("BioFuelBlock")
              .setRegistryName(new ResourceLocation(MekanismUltimate.MODID, "block_bio_fuel")));
        registry.register(PigmentExtractor.setTranslationKey("PigmentExtractor")
              .setRegistryName(new ResourceLocation(MekanismUltimate.MODID, "pigment_extractor")));
        registry.register(PigmentMixer.setTranslationKey("PigmentMixer")
              .setRegistryName(new ResourceLocation(MekanismUltimate.MODID, "pigment_mixer")));
        registry.register(PaintingMachine.setTranslationKey("PaintingMachine")
              .setRegistryName(new ResourceLocation(MekanismUltimate.MODID, "painting_machine")));
        registry.register(DimensionalStabilizer.setTranslationKey("DimensionalStabilizer")
              .setRegistryName(new ResourceLocation(MekanismUltimate.MODID, "dimensional_stabilizer")));
        for (BlockChemicalTank tank : CHEMICAL_TANKS) {
            String tier = tank.getTier().getName();
            registry.register(tank.setTranslationKey("ChemicalTank"
                        + tank.getTier().getBaseTier().getSimpleName())
                  .setRegistryName(new ResourceLocation(MekanismUltimate.MODID,
                        tier + "_chemical_tank")));
        }
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(new ItemBlockUltimateFactory(UltimateFactory)
              .setRegistryName(UltimateFactory.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(NutritionalLiquifier)
              .setRegistryName(NutritionalLiquifier.getRegistryName()));
        registry.register(new ItemBlockPersonalBarrel(PersonalBarrel)
              .setRegistryName(PersonalBarrel.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(IndustrialAlarm)
              .setRegistryName(IndustrialAlarm.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(BioFuelBlock)
              .setRegistryName(BioFuelBlock.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(PigmentExtractor)
              .setRegistryName(PigmentExtractor.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(PigmentMixer)
              .setRegistryName(PigmentMixer.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(PaintingMachine)
              .setRegistryName(PaintingMachine.getRegistryName()));
        registry.register(new net.minecraft.item.ItemBlock(DimensionalStabilizer)
              .setRegistryName(DimensionalStabilizer.getRegistryName()));
        for (BlockChemicalTank tank : CHEMICAL_TANKS) {
            registry.register(new ItemBlockChemicalTank(tank)
                  .setRegistryName(tank.getRegistryName()));
        }
    }

    public static BlockChemicalTank getChemicalTank(ChemicalTankTier tier) {
        return CHEMICAL_TANKS[tier.ordinal()];
    }
}
