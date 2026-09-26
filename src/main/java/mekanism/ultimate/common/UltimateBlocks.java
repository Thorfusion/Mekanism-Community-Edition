package mekanism.ultimate.common;

import mekanism.ultimate.common.block.BlockUltimateFactory;
import mekanism.ultimate.common.block.BlockNutritionalLiquifier;
import mekanism.ultimate.common.block.BlockChemicalTank;
import mekanism.ultimate.common.item.ItemBlockUltimateFactory;
import mekanism.ultimate.common.item.ItemBlockChemicalTank;
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
        for (BlockChemicalTank tank : CHEMICAL_TANKS) {
            registry.register(new ItemBlockChemicalTank(tank)
                  .setRegistryName(tank.getRegistryName()));
        }
    }

    public static BlockChemicalTank getChemicalTank(ChemicalTankTier tier) {
        return CHEMICAL_TANKS[tier.ordinal()];
    }
}
