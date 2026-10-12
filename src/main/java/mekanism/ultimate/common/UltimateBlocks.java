package mekanism.ultimate.common;

import mekanism.ultimate.common.block.BlockUltimateFactory;
import mekanism.ultimate.common.block.BlockBioFuel;
import mekanism.ultimate.common.block.BlockChemicalTank;
import mekanism.ultimate.common.block.BlockNutritionalLiquifier;
import mekanism.ultimate.common.block.BlockPigmentMachine;
import mekanism.ultimate.common.block.BlockPigmentMachine.MachineType;
import mekanism.ultimate.common.block.BlockPersonalBarrel;
import mekanism.ultimate.common.block.BlockIndustrialAlarm;
import mekanism.ultimate.common.block.BlockDimensionalStabilizer;
import mekanism.ultimate.common.tier.ChemicalTankTier;
import net.minecraft.block.Block;

public final class UltimateBlocks
{
    public static final Block UltimateFactory = new BlockUltimateFactory().setBlockName("UltimateFactory");
    public static final Block NutritionalLiquifier =
          new BlockNutritionalLiquifier();
    public static final Block BioFuelBlock = new BlockBioFuel();
    public static final Block PersonalBarrel = new BlockPersonalBarrel();
    public static final Block IndustrialAlarm = new BlockIndustrialAlarm();
    public static final Block DimensionalStabilizer =
          new BlockDimensionalStabilizer();
    public static final BlockPigmentMachine PigmentExtractor =
          new BlockPigmentMachine(MachineType.EXTRACTOR);
    public static final BlockPigmentMachine PigmentMixer =
          new BlockPigmentMachine(MachineType.MIXER);
    public static final BlockPigmentMachine PaintingMachine =
          new BlockPigmentMachine(MachineType.PAINTING);
    public static final BlockChemicalTank BasicChemicalTank =
          new BlockChemicalTank(ChemicalTankTier.BASIC);
    public static final BlockChemicalTank AdvancedChemicalTank =
          new BlockChemicalTank(ChemicalTankTier.ADVANCED);
    public static final BlockChemicalTank EliteChemicalTank =
          new BlockChemicalTank(ChemicalTankTier.ELITE);
    public static final BlockChemicalTank UltimateChemicalTank =
          new BlockChemicalTank(ChemicalTankTier.ULTIMATE);
    public static final BlockChemicalTank CreativeChemicalTank =
          new BlockChemicalTank(ChemicalTankTier.CREATIVE);

    private static final BlockChemicalTank[] CHEMICAL_TANKS = {
          BasicChemicalTank, AdvancedChemicalTank, EliteChemicalTank,
          UltimateChemicalTank, CreativeChemicalTank
    };

    private UltimateBlocks()
    {
    }

    public static BlockChemicalTank getChemicalTank(ChemicalTankTier tier)
    {
        return CHEMICAL_TANKS[tier.ordinal()];
    }

    public static BlockChemicalTank[] getChemicalTanks()
    {
        return CHEMICAL_TANKS.clone();
    }
}
