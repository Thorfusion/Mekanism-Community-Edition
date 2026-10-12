package mekanism.nuclear.common;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import io.netty.buffer.ByteBuf;
import java.io.File;
import java.io.IOException;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Tier.GasTankTier;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.recipe.ShapedMekanismRecipe;
import mekanism.common.recipe.ShapelessMekanismRecipe;
import mekanism.common.util.MekanismUtils;
import mekanism.nuclear.common.config.NuclearWorldGenConfig;
import mekanism.nuclear.common.config.NuclearFissionConfig;
import mekanism.nuclear.common.config.NuclearRadiationConfig;
import mekanism.nuclear.common.config.NuclearSPSConfig;
import mekanism.nuclear.common.content.fission.FissionReactorFormationManager;
import mekanism.nuclear.common.content.sps.SPSFormationManager;
import mekanism.nuclear.common.recipe.NuclearLegacyRecipeRegistry;
import mekanism.nuclear.common.recipe.NuclearRecipeRegistry;
import mekanism.nuclear.common.radiation.PlayerRadiationHandler;
import mekanism.nuclear.common.world.NuclearWorldGenerator;
import mekanism.generators.common.GeneratorsBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = MekanismNuclear.MODID, name = "Mekanism Community Edition: Nuclear (Beta)",
      version = "GRADLE_MODVERSION",
      dependencies = "required-after:Mekanism@[GRADLE_MODVERSION];required-after:MekanismUltimate@[GRADLE_MODVERSION];required-after:MekanismGenerators@[GRADLE_MODVERSION]",
      guiFactory = "mekanism.nuclear.client.gui.NuclearGuiFactory")
public final class MekanismNuclear implements IModule
{
    public static final String MODID = "mekanismnuclear";

    @Instance(MODID)
    public static MekanismNuclear instance;

    @SidedProxy(clientSide = "mekanism.nuclear.client.NuclearClientProxy",
          serverSide = "mekanism.nuclear.common.NuclearCommonProxy")
    public static NuclearCommonProxy proxy;

    public static Version versionNumber = new Version(GRADLE_VERSIONMOD);
    public static final SimpleNetworkWrapper network =
          NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static Configuration configuration;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.registerPackets(network);
        MinecraftForge.EVENT_BUS.register(PlayerRadiationHandler.INSTANCE);
        NuclearChemicals.register();
        NuclearItems.register();
        NuclearBlocks.register();
        NuclearOreDictionary.register();
        NuclearRecipeRegistry.registerDefaults();
        configuration = new Configuration(new File(
              event.getModConfigurationDirectory(),
              "mekanism/MekanismNuclear.cfg"));
        loadConfiguration();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.registerTileEntities();
        proxy.registerRenderInformation();
        FMLCommonHandler.instance().bus().register(this);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        Mekanism.modulesLoaded.add(this);
        MinecraftForge.EVENT_BUS.register(FissionReactorFormationManager.INSTANCE);
        MinecraftForge.EVENT_BUS.register(SPSFormationManager.INSTANCE);
        NuclearLegacyRecipeRegistry.register();
        cpw.mods.fml.common.registry.GameRegistry.registerWorldGenerator(
              NuclearWorldGenerator.INSTANCE, 2);
        addRecipes();
        Mekanism.logger.info("Loaded Mekanism Nuclear module.");
    }

    private void addRecipes()
    {
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.IsotopicCentrifuge), new Object[] {
                  "III", "CTC", "III",
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('C'), MekanismUtils.getControlCircuit(BaseTier.ULTIMATE),
                  Character.valueOf('T'), MekanismUtils.getEmptyGasTank(GasTankTier.BASIC)
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.AntiprotonicNucleosynthesizer),
              new Object[] {
                  "#C#", "AXA", "#C#",
                  Character.valueOf('#'), "alloyUltimate",
                  Character.valueOf('C'), "circuitUltimate",
                  Character.valueOf('A'), NuclearOreDictionary.PELLET_ANTIMATTER,
                  Character.valueOf('X'), new ItemStack(MekanismBlocks.BasicBlock, 1, 8)
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.FluoriteBlock), new Object[] {
                  "FFF", "FFF", "FFF",
                  Character.valueOf('F'), NuclearOreDictionary.GEM_FLUORITE
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapelessMekanismRecipe(
              new ItemStack(NuclearItems.FluoriteGem, 9),
              NuclearOreDictionary.BLOCK_FLUORITE));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.RadioactiveWasteBarrel), new Object[] {
                  "SIS", "I I", "SIS",
                  Character.valueOf('S'), "ingotSteel",
                  Character.valueOf('I'), "ingotLead"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearItems.GeigerCounter), new Object[] {
                  " I ", "ICI", " I ",
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('C'), "circuitBasic"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearItems.Dosimeter), new Object[] {
                  " I ", "IRI", " I ",
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('R'), "dustRedstone"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearItems.HazmatMask), new Object[] {
                  "III", "IDI",
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('D'), "dyeOrange"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearItems.HazmatGown), new Object[] {
                  "IDI", "III", "III",
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('D'), "dyeOrange"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearItems.HazmatPants), new Object[] {
                  "III", "IDI", "I I",
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('D'), "dyeOrange"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearItems.HazmatBoots), new Object[] {
                  "I I", "IDI",
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('D'), "dyeBlack"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.FissionReactorCasing, 4), new Object[] {
                  " I ", "IXI", " I ",
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('X'), new ItemStack(MekanismBlocks.BasicBlock, 1, 8)
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.FissionReactorPort, 2), new Object[] {
                  " F ", "FCF", " F ",
                  Character.valueOf('F'), NuclearBlocks.FissionReactorCasing,
                  Character.valueOf('C'), "circuitElite"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.FissionReactorLogicAdapter), new Object[] {
                  " R ", "RFR", " R ",
                  Character.valueOf('F'), NuclearBlocks.FissionReactorCasing,
                  Character.valueOf('R'), "dustRedstone"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.FissionFuelAssembly), new Object[] {
                  "ISI", "ITI", "ISI",
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('S'), "ingotSteel",
                  Character.valueOf('T'), MekanismUtils.getEmptyGasTank(GasTankTier.BASIC)
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.ControlRodAssembly), new Object[] {
                  "ICI", "SIS", "SIS",
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('S'), "ingotSteel",
                  Character.valueOf('C'), "circuitElite"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(GeneratorsBlocks.ReactorGlass, 4, 0), new Object[] {
                  "SIS", "IGI", "SIS",
                  Character.valueOf('S'), MekanismItems.EnrichedIron,
                  Character.valueOf('I'), "ingotLead",
                  Character.valueOf('G'), "blockGlass"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.SPSCasing), new Object[] {
                  "HPH", "PUP", "HPH",
                  Character.valueOf('H'), new ItemStack(MekanismItems.Polyethene, 1, 2),
                  Character.valueOf('P'), NuclearOreDictionary.PELLET_POLONIUM,
                  Character.valueOf('U'), NuclearOreDictionary.PELLET_PLUTONIUM
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.SPSPort), new Object[] {
                  " C ", "CUC", " C ",
                  Character.valueOf('C'), NuclearBlocks.SPSCasing,
                  Character.valueOf('U'), "circuitUltimate"
              }));
        CraftingManager.getInstance().getRecipeList().add(new ShapedMekanismRecipe(
              new ItemStack(NuclearBlocks.SuperchargedCoil), new Object[] {
                  "ccc", "ULU", "PPP",
                  Character.valueOf('c'), "ingotCopper",
                  Character.valueOf('U'), "circuitUltimate",
                  Character.valueOf('L'), new ItemStack(MekanismBlocks.MachineBlock2, 1, 13),
                  Character.valueOf('P'), NuclearOreDictionary.PELLET_POLONIUM
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
        return "Nuclear";
    }

    @Override
    public void writeConfig(ByteBuf dataStream) throws IOException
    {
        NuclearFissionConfig.write(dataStream);
        NuclearRadiationConfig.write(dataStream);
        NuclearSPSConfig.write(dataStream);
        NuclearWorldGenConfig.write(dataStream);
    }

    @Override
    public void readConfig(ByteBuf dataStream) throws IOException
    {
        NuclearFissionConfig.read(dataStream);
        NuclearRadiationConfig.read(dataStream);
        NuclearSPSConfig.read(dataStream);
        NuclearWorldGenConfig.read(dataStream);
    }

    @Override
    public void resetClient()
    {
        loadConfiguration();
        proxy.resetRadiationData();
    }

    private static void loadConfiguration()
    {
        configuration.load();
        NuclearFissionConfig.load(configuration);
        NuclearRadiationConfig.load(configuration);
        NuclearSPSConfig.load(configuration);
        NuclearWorldGenConfig.load(configuration);
        if (configuration.hasChanged())
        {
            configuration.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event)
    {
        if (MODID.equals(event.modID))
        {
            loadConfiguration();
        }
    }
}
