package mekanism.ultimate.common;

import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.IOException;

import mekanism.api.MekanismConfig;
import mekanism.api.MekanismConfig.ultimate;
import mekanism.common.Mekanism;
import mekanism.common.Resource;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.Version;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.base.IModule;
import mekanism.common.block.BlockMachine.MachineType;
import mekanism.common.content.tank.DynamicTankChemicalHooks;
import mekanism.common.recipe.ShapelessMekanismRecipe;
import mekanism.common.recipe.ShapedMekanismRecipe;
import mekanism.common.MekanismItems;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.common.config.UltimateChemicalTankConfig;
import mekanism.ultimate.common.config.UltimateGearConfig;
import mekanism.ultimate.common.config.UltimateNutritionConfig;
import mekanism.ultimate.common.gear.ArmoredFreeRunnersHandler;
import mekanism.ultimate.common.gear.HDPEElytraTickHandler;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import mekanism.ultimate.common.integration.mekanism.DynamicTankChemicalIntegration;
import mekanism.ultimate.common.item.ItemBlockChemicalTank;
import mekanism.ultimate.common.item.ItemBlockUltimateFactory;
import mekanism.ultimate.common.item.ItemBlockPersonalBarrel;
import mekanism.ultimate.common.recipe.ChemicalTankUpgradeRecipe;
import mekanism.ultimate.common.recipe.UltimatePigmentRecipes;
import mekanism.ultimate.common.tier.ChemicalTankTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeChunkManager;
import mekanism.common.chunkloading.ChunkManager;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = MekanismUltimate.MODID, name = "Mekanism Community Edition: Ultimate (Beta)", version = "GRADLE_MODVERSION", dependencies = "required-after:Mekanism;after:etfuturum", guiFactory = "mekanism.ultimate.client.gui.UltimateGuiFactory")
public class MekanismUltimate implements IModule
{
    public static final String MODID = "MekanismUltimate";

    @Instance(MODID)
    public static MekanismUltimate instance;

    @SidedProxy(clientSide = "mekanism.ultimate.client.UltimateClientProxy", serverSide = "mekanism.ultimate.common.UltimateCommonProxy")
    public static UltimateCommonProxy proxy;

    public static Version versionNumber = new Version(GRADLE_VERSIONMOD);

    public static final SimpleNetworkWrapper network =
          NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    public static Configuration configuration;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        configuration = new Configuration(new File(event.getModConfigurationDirectory(), "mekanism/MekanismUltimate.cfg"));
        configuration.load();
        proxy.loadConfiguration();
        UltimateFluids.register();
        DynamicTankChemicalHooks.register(
              DynamicTankChemicalIntegration.INSTANCE);
        UltimateItems.register();
        ForgeChunkManager.setForcedChunkLoadingCallback(this,
              new ChunkManager());
        proxy.registerPackets(network);
        GameRegistry.registerBlock(UltimateBlocks.UltimateFactory, ItemBlockUltimateFactory.class, "UltimateFactory");
        GameRegistry.registerBlock(UltimateBlocks.NutritionalLiquifier,
              "nutritional_liquifier");
        GameRegistry.registerBlock(UltimateBlocks.BioFuelBlock,
              "block_bio_fuel");
        GameRegistry.registerBlock(UltimateBlocks.PersonalBarrel,
              ItemBlockPersonalBarrel.class, "personal_barrel");
        GameRegistry.registerBlock(UltimateBlocks.IndustrialAlarm,
              "industrial_alarm");
        GameRegistry.registerBlock(UltimateBlocks.DimensionalStabilizer,
              "dimensional_stabilizer");
        GameRegistry.registerBlock(UltimateBlocks.PigmentExtractor,
              "pigment_extractor");
        GameRegistry.registerBlock(UltimateBlocks.PigmentMixer,
              "pigment_mixer");
        GameRegistry.registerBlock(UltimateBlocks.PaintingMachine,
              "painting_machine");
        GameRegistry.registerBlock(UltimateBlocks.BasicChemicalTank,
              ItemBlockChemicalTank.class, "basic_chemical_tank");
        GameRegistry.registerBlock(UltimateBlocks.AdvancedChemicalTank,
              ItemBlockChemicalTank.class, "advanced_chemical_tank");
        GameRegistry.registerBlock(UltimateBlocks.EliteChemicalTank,
              ItemBlockChemicalTank.class, "elite_chemical_tank");
        GameRegistry.registerBlock(UltimateBlocks.UltimateChemicalTank,
              ItemBlockChemicalTank.class, "ultimate_chemical_tank");
        GameRegistry.registerBlock(UltimateBlocks.CreativeChemicalTank,
              ItemBlockChemicalTank.class, "creative_chemical_tank");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        RecipeSorter.register("mekanismultimate_chemical_tank_upgrade",
              ChemicalTankUpgradeRecipe.class, Category.SHAPED,
              "after:mekanism_shaped");
        proxy.registerTileEntities();
        proxy.registerRenderInformation();
        FMLCommonHandler.instance().bus().register(this);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        MinecraftForge.EVENT_BUS.register(
              ArmoredFreeRunnersHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(
              HDPEElytraTickHandler.INSTANCE);
        Mekanism.modulesLoaded.add(this);
        UltimatePigmentRecipes.registerDefaults();
        addRecipes();
        if (EtFuturumElytraCompat.isAvailable())
        {
            Mekanism.logger.info(
                  "Enabled Et Futurum Requiem Elytra integration.");
        }
        Mekanism.logger.info("Loaded MekanismUltimate module.");
    }

    private void addRecipes()
    {
        addMaterialRecipes();
        addPigmentMachineRecipes();
        addNutritionRecipes();
        addGearRecipes();
        if (!MekanismConfig.recipes.enableFactories)
        {
            return;
        }

        if (RecipeType.SAWING.isEnabled() && UltimateConfig.enableSawmillFactoryRecipes)
        {
            RecipeType type = RecipeType.SAWING;
            MachineType.BASIC_FACTORY.addRecipe(new ShapedMekanismRecipe(MekanismUtils.getFactory(FactoryTier.BASIC, type), new Object[] {
                "RCR", "iOi", "RCR",
                Character.valueOf('R'), "alloyBasic",
                Character.valueOf('C'), MekanismUtils.getControlCircuit(BaseTier.BASIC),
                Character.valueOf('i'), "ingotIron",
                Character.valueOf('O'), type.getStack()
            }));
            MachineType.ADVANCED_FACTORY.addRecipe(new ShapedMekanismRecipe(MekanismUtils.getFactory(FactoryTier.ADVANCED, type), new Object[] {
                "ECE", "oOo", "ECE",
                Character.valueOf('E'), "alloyAdvanced",
                Character.valueOf('C'), MekanismUtils.getControlCircuit(BaseTier.ADVANCED),
                Character.valueOf('o'), "ingot" + Resource.OSMIUM.getOredictName(),
                Character.valueOf('O'), MekanismUtils.getFactory(FactoryTier.BASIC, type)
            }));
            MachineType.ELITE_FACTORY.addRecipe(new ShapedMekanismRecipe(MekanismUtils.getFactory(FactoryTier.ELITE, type), new Object[] {
                "RCR", "gOg", "RCR",
                Character.valueOf('R'), "alloyElite",
                Character.valueOf('C'), MekanismUtils.getControlCircuit(BaseTier.ELITE),
                Character.valueOf('g'), "ingotGold",
                Character.valueOf('O'), MekanismUtils.getFactory(FactoryTier.ADVANCED, type)
            }));
        }

        if (!ultimate.factoryEnabled || !ultimate.enableFactoryRecipes)
        {
            return;
        }

        for (RecipeType type : RecipeType.values())
        {
            if (type == RecipeType.SAWING && (!type.isEnabled() || !UltimateConfig.enableSawmillFactoryRecipes))
            {
                continue;
            }

            ItemStack result = MekanismUtils.getFactory(FactoryTier.ULTIMATE, type);
            ItemStack eliteFactory = MekanismUtils.getFactory(FactoryTier.ELITE, type);
            CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(result, new Object[] {
                "ACA", "IPI", "ACA",
                Character.valueOf('A'), "alloyUltimate",
                Character.valueOf('C'), MekanismUtils.getControlCircuit(BaseTier.ULTIMATE),
                Character.valueOf('I'), "gemDiamond",
                Character.valueOf('P'), eliteFactory
            }));
        }
    }

    @SuppressWarnings("unchecked")
    private void addMaterialRecipes()
    {
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateItems.StoneGeneratorUpgrade),
                    new Object[] {
                          " G ", "WAL", " G ",
                          Character.valueOf('G'), "blockGlass",
                          Character.valueOf('W'), Items.water_bucket,
                          Character.valueOf('A'), "alloyBasic",
                          Character.valueOf('L'), Items.lava_bucket
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateItems.ChemicalUpgrade),
                    new Object[] {
                          " G ", "AIA", " G ",
                          Character.valueOf('G'), "blockGlass",
                          Character.valueOf('A'), "alloyBasic",
                          Character.valueOf('I'), "dustIron"
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateBlocks.PersonalBarrel),
                    new Object[] {
                          "SGS", "PCP", "SSS",
                          Character.valueOf('S'), "ingotSteel",
                          Character.valueOf('G'), "blockGlass",
                          Character.valueOf('P'), "chestWood",
                          Character.valueOf('C'),
                                MekanismUtils.getControlCircuit(
                                      BaseTier.BASIC)
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateBlocks.IndustrialAlarm),
                    new Object[] {
                          "III", "C#C", "III",
                          Character.valueOf('I'), "ingotLead",
                          Character.valueOf('C'),
                                MekanismUtils.getControlCircuit(
                                      BaseTier.BASIC),
                          Character.valueOf('#'), Blocks.redstone_lamp
                    }));
        if (UltimateConfig.enableDimensionalStabilizerRecipe)
        {
            CraftingManager.getInstance().getRecipeList().add(
                  new ShapedMekanismRecipe(
                        new ItemStack(
                              UltimateBlocks.DimensionalStabilizer),
                        new Object[] {
                              "ICI", "ADA", "ICI",
                              Character.valueOf('I'),
                                    "ingotRefinedObsidian",
                              Character.valueOf('C'),
                                    MekanismUtils.getControlCircuit(
                                          BaseTier.ULTIMATE),
                              Character.valueOf('A'), "alloyUltimate",
                              Character.valueOf('D'), "blockDiamond"
                        }));
        }
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateBlocks.BioFuelBlock),
                    new Object[] {
                          "###", "###", "###",
                          Character.valueOf('#'),
                                new ItemStack(MekanismItems.BioFuel)
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapelessMekanismRecipe(
                    new ItemStack(MekanismItems.BioFuel, 9),
                    new Object[] {
                          new ItemStack(UltimateBlocks.BioFuelBlock)
                    }));

        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateBlocks.BasicChemicalTank),
                    new Object[] {
                          "AOA", "O O", "AOA",
                          Character.valueOf('A'), "alloyBasic",
                          Character.valueOf('O'), "ingotOsmium"
                    }));
        addChemicalTankUpgrade(ChemicalTankTier.ADVANCED,
              ChemicalTankTier.BASIC, "alloyAdvanced");
        addChemicalTankUpgrade(ChemicalTankTier.ELITE,
              ChemicalTankTier.ADVANCED, "alloyElite");
        addChemicalTankUpgrade(ChemicalTankTier.ULTIMATE,
              ChemicalTankTier.ELITE, "alloyUltimate");
    }

    @SuppressWarnings("unchecked")
    private void addPigmentMachineRecipes()
    {
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateBlocks.PigmentExtractor),
                    new Object[] {
                          "RCR", "FXF", "RCR",
                          Character.valueOf('R'), "dustRedstone",
                          Character.valueOf('C'),
                                MekanismUtils.getControlCircuit(
                                      BaseTier.BASIC),
                          Character.valueOf('F'), Items.flint,
                          Character.valueOf('X'),
                                new ItemStack(
                                      mekanism.common.MekanismBlocks.BasicBlock,
                                      1, 8)
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateBlocks.PigmentMixer),
                    new Object[] {
                          "ACA", "RXR", "ACA",
                          Character.valueOf('A'), "alloyElite",
                          Character.valueOf('C'),
                                MekanismUtils.getControlCircuit(
                                      BaseTier.ELITE),
                          Character.valueOf('R'),
                                new ItemStack(MekanismItems.Polyethene, 1, 1),
                          Character.valueOf('X'),
                                new ItemStack(
                                      mekanism.common.MekanismBlocks.BasicBlock,
                                      1, 8)
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateBlocks.PaintingMachine),
                    new Object[] {
                          "ACA", "DXD", "ACA",
                          Character.valueOf('A'), "alloyAdvanced",
                          Character.valueOf('C'),
                                MekanismUtils.getControlCircuit(
                                      BaseTier.ADVANCED),
                          Character.valueOf('D'),
                                new ItemStack(Items.dye, 1,
                                      OreDictionary.WILDCARD_VALUE),
                          Character.valueOf('X'),
                                new ItemStack(
                                      mekanism.common.MekanismBlocks.BasicBlock,
                                      1, 8)
                    }));
    }

    @SuppressWarnings("unchecked")
    private void addChemicalTankUpgrade(ChemicalTankTier result,
          ChemicalTankTier previous, String alloy)
    {
        ShapedMekanismRecipe shaped = new ShapedMekanismRecipe(
              new ItemStack(UltimateBlocks.getChemicalTank(result)),
              new Object[] {
                    "AOA", "OPO", "AOA",
                    Character.valueOf('A'), alloy,
                    Character.valueOf('O'), "ingotOsmium",
                    Character.valueOf('P'),
                          new ItemStack(UltimateBlocks.getChemicalTank(previous))
              });
        CraftingManager.getInstance().getRecipeList().add(
              new ChemicalTankUpgradeRecipe(shaped));
    }

    @SuppressWarnings("unchecked")
    private void addGearRecipes()
    {
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateItems.ArmoredFreeRunners),
                    new Object[] {
                          " S ", "#P#", "I I",
                          Character.valueOf('S'), "blockSteel",
                          Character.valueOf('#'), "dustDiamond",
                          Character.valueOf('P'),
                                new ItemStack(MekanismItems.FreeRunners),
                          Character.valueOf('I'), "ingotBronze"
                    }));
        net.minecraft.item.Item etFuturumElytra =
              EtFuturumElytraCompat.getElytraItem();
        if (etFuturumElytra != null
              && EtFuturumElytraCompat.isAvailable())
        {
            CraftingManager.getInstance().getRecipeList().add(
                  new ShapedMekanismRecipe(
                        new ItemStack(UltimateItems.HDPEElytra),
                        new Object[] {
                              "AHA", "HPH", "H H",
                              Character.valueOf('A'), "alloyUltimate",
                              Character.valueOf('H'),
                                    new ItemStack(MekanismItems.Polyethene,
                                          1, 2),
                              Character.valueOf('P'), etFuturumElytra
                        }));
        }
    }

    @SuppressWarnings("unchecked")
    private void addNutritionRecipes()
    {
        if (!mekanism.ultimate.common.config.UltimateNutritionConfig
              .nutritionalLiquifierEnabled)
        {
            return;
        }
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(new ItemStack(UltimateItems.Canteen),
                    new Object[] {
                          " I ", "I#I", " I ",
                          Character.valueOf('I'), "ingotTin",
                          Character.valueOf('#'), net.minecraft.init.Items.bowl
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(UltimateBlocks.NutritionalLiquifier),
                    new Object[] {
                          "RCR", "#X#", "RCR",
                          Character.valueOf('R'), "dustRedstone",
                          Character.valueOf('C'),
                                MekanismUtils.getControlCircuit(BaseTier.BASIC),
                          Character.valueOf('#'), net.minecraft.init.Items.bowl,
                          Character.valueOf('X'),
                                new ItemStack(mekanism.common.MekanismBlocks.BasicBlock,
                                      1, 8)
                    }));
    }

    @Override
    public Version getVersion()
    {
        return versionNumber;
    }

    @Override
    public String getName()
    {
        return "Ultimate";
    }

    @Override
    public void writeConfig(ByteBuf dataStream) throws IOException
    {
        dataStream.writeBoolean(ultimate.factoryEnabled);
        dataStream.writeBoolean(ultimate.allowTierInstallerUpgrade);
        dataStream.writeDouble(ultimate.factoryUsageMultiplier);
        dataStream.writeInt(ultimate.factoryEnergyStorageTicks);
        dataStream.writeBoolean(ultimate.enableFactoryRecipes);
        dataStream.writeBoolean(ultimate.sawmillFactoriesEnabled);
        dataStream.writeBoolean(UltimateConfig.enableSawmillFactoryRecipes);
        dataStream.writeBoolean(
              UltimateConfig.allowDimensionalStabilizerChunkloading);
        dataStream.writeBoolean(
              UltimateConfig.enableDimensionalStabilizerRecipe);
        dataStream.writeDouble(
              UltimateConfig.dimensionalStabilizerEnergyPerChunk);
        dataStream.writeInt(
              UltimateConfig.dimensionalStabilizerEnergyStorageTicks);
        UltimateChemicalTankConfig.write(dataStream);
        UltimateNutritionConfig.write(dataStream);
        UltimateGearConfig.write(dataStream);
    }

    @Override
    public void readConfig(ByteBuf dataStream) throws IOException
    {
        ultimate.factoryEnabled = dataStream.readBoolean();
        ultimate.allowTierInstallerUpgrade = dataStream.readBoolean();
        ultimate.factoryUsageMultiplier = dataStream.readDouble();
        ultimate.factoryEnergyStorageTicks = dataStream.readInt();
        ultimate.enableFactoryRecipes = dataStream.readBoolean();
        ultimate.sawmillFactoriesEnabled = dataStream.readBoolean();
        UltimateConfig.enableSawmillFactoryRecipes = dataStream.readBoolean();
        UltimateConfig.allowDimensionalStabilizerChunkloading =
              dataStream.readBoolean();
        UltimateConfig.enableDimensionalStabilizerRecipe =
              dataStream.readBoolean();
        UltimateConfig.dimensionalStabilizerEnergyPerChunk =
              Math.max(0D, dataStream.readDouble());
        UltimateConfig.dimensionalStabilizerEnergyStorageTicks =
              Math.max(1, dataStream.readInt());
        UltimateChemicalTankConfig.read(dataStream);
        UltimateNutritionConfig.read(dataStream);
        UltimateGearConfig.read(dataStream);
    }

    @Override
    public void resetClient()
    {
        configuration.load();
        proxy.loadConfiguration();
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event)
    {
        if (MODID.equals(event.modID))
        {
            proxy.loadConfiguration();
        }
    }
}
