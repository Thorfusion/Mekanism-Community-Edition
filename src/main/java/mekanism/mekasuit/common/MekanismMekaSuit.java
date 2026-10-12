package mekanism.mekasuit.common;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import io.netty.buffer.ByteBuf;
import java.io.File;
import java.io.IOException;
import mekanism.common.Mekanism;
import mekanism.common.MekanismBlocks;
import mekanism.common.MekanismItems;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Tier.EnergyCubeTier;
import mekanism.common.Tier.InductionProviderTier;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.recipe.ShapedMekanismRecipe;
import mekanism.common.util.MekanismUtils;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.MekaSuitDamageHandler;
import mekanism.mekasuit.common.content.gear.MekaSuitChargeDistributionHandler;
import mekanism.mekasuit.common.content.gear.MekaSuitGravitationalHandler;
import mekanism.mekasuit.common.content.gear.MekaSuitElytraTickHandler;
import mekanism.mekasuit.common.content.gear.MekaSuitInhalationHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitJetpackHandler;
import mekanism.mekasuit.common.content.gear.MekaSuitMobilityHandler;
import mekanism.mekasuit.common.content.gear.MekaSuitModuleDropHandler;
import mekanism.mekasuit.common.integration.MekaSuitGeneratorsIntegration;
import mekanism.ultimate.common.UltimateItems;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

/** Entry point for the separately packaged beta MekaSuit module. */
@Mod(modid = MekanismMekaSuit.MODID,
      name = "Mekanism Community Edition: MekaSuit (Beta)",
      version = "GRADLE_MODVERSION",
      dependencies = "required-after:Mekanism@[GRADLE_MODVERSION];required-after:MekanismUltimate@[GRADLE_MODVERSION];required-after:MekanismGenerators@[GRADLE_MODVERSION];required-after:MekanismTools@[GRADLE_MODVERSION];after:etfuturum",
      guiFactory = "mekanism.mekasuit.client.gui.MekaSuitGuiFactory")
public final class MekanismMekaSuit implements IModule
{
    public static final String MODID = "mekanismmekasuit";

    @Instance(MODID)
    public static MekanismMekaSuit instance;

    @SidedProxy(clientSide = "mekanism.mekasuit.client.MekaSuitClientProxy",
          serverSide = "mekanism.mekasuit.common.MekaSuitCommonProxy")
    public static MekaSuitCommonProxy proxy;

    public static Version versionNumber = new Version(GRADLE_VERSIONMOD);
    public static final SimpleNetworkWrapper network =
          NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    public static Configuration configuration;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MekaSuitModules.bootstrap();
        configuration = new Configuration(new File(
              event.getModConfigurationDirectory(),
              "mekanism/MekanismMekaSuit.cfg"));
        loadConfiguration();
        MekaSuitItems.register();
        MekaSuitGeneratorsIntegration.registerItems();
        MekaSuitBlocks.register();
        proxy.registerPackets(network);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.registerRenderInformation();
        proxy.registerTileEntities();
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(MekaSuitDamageHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MekaSuitInhalationHelper.INSTANCE);
        FMLCommonHandler.instance().bus().register(
              MekaSuitJetpackHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(
              MekaSuitChargeDistributionHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(
              MekaSuitGravitationalHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(
              MekaSuitMobilityHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(
              MekaSuitElytraTickHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MekaSuitMobilityHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MekaSuitModuleDropHandler.INSTANCE);
        MekaSuitGeneratorsIntegration.registerHandlers();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        Mekanism.modulesLoaded.add(this);
        addRecipes();
        MekaSuitGeneratorsIntegration.addRecipes();
        Mekanism.logger.info("Loaded Mekanism MekaSuit beta module.");
    }

    @SuppressWarnings("unchecked")
    private void addRecipes()
    {
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(new ItemStack(MekaSuitItems.ModuleBase, 2),
                    new Object[] {
                          "NIN", "IHI", "NIN",
                          Character.valueOf('N'), "nuggetBronze",
                          Character.valueOf('I'), "ingotTin",
                          Character.valueOf('H'),
                                new ItemStack(MekanismItems.Polyethene, 1, 2)
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(new ItemStack(MekaSuitItems.EnergyUnit),
                    new Object[] {
                          "ACA", "ABA", "HHH",
                          Character.valueOf('A'), MekanismItems.ReinforcedAlloy,
                          Character.valueOf('C'),
                                MekanismUtils.getEnergyCube(EnergyCubeTier.BASIC),
                          Character.valueOf('B'), MekaSuitItems.ModuleBase,
                          Character.valueOf('H'),
                                new ItemStack(MekanismItems.Polyethene, 1, 2)
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(
                    new ItemStack(MekaSuitBlocks.ModificationStation),
                    new Object[] {
                          "PWP", "CXC", "PAP",
                          Character.valueOf('P'),
                                new ItemStack(MekanismItems.Polyethene, 1, 2),
                          Character.valueOf('W'), "chestWood",
                          Character.valueOf('C'), "circuitUltimate",
                          Character.valueOf('X'),
                                new ItemStack(MekanismBlocks.BasicBlock, 1, 8),
                          Character.valueOf('A'), "alloyUltimate"
                    }));
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(new ItemStack(MekaSuitItems.MekaTool),
                    new Object[] {
                          "UCU", "HDH", "AIA",
                          Character.valueOf('U'),
                                MekanismUtils.getControlCircuit(BaseTier.ULTIMATE),
                          Character.valueOf('C'), MekanismItems.Configurator,
                          Character.valueOf('H'),
                                new ItemStack(MekanismItems.Polyethene, 1, 2),
                          Character.valueOf('D'),
                                MekanismItems.AtomicDisassembler,
                          Character.valueOf('A'), MekanismItems.AtomicAlloy,
                          Character.valueOf('I'),
                                MekanismUtils.getEnergyCube(EnergyCubeTier.BASIC)
                    }));
        addArmorRecipe(MekaSuitItems.MekaSuitHelmet, Items.diamond_helmet);
        addArmorRecipe(MekaSuitItems.MekaSuitBodyarmor,
              Items.diamond_chestplate);
        addArmorRecipe(MekaSuitItems.MekaSuitPants,
              Items.diamond_leggings);
        addArmorRecipe(MekaSuitItems.MekaSuitBoots, Items.diamond_boots);
        addArmorProtectionModuleRecipes();
        addToolModuleRecipes();
        addHelmetModuleRecipes();
        addBodyarmorModuleRecipes();
        addMobilityModuleRecipes();
    }

    private void addArmorProtectionModuleRecipes()
    {
        Object hdpe = new ItemStack(MekanismItems.Polyethene, 1, 2);
        Object dye = new ItemStack(Items.dye, 1,
              OreDictionary.WILDCARD_VALUE);
        addRecipe(MekaSuitItems.ColorModulationUnit,
              "M#M", "OPO", "HHH", 'M', dye, '#',
              new ItemStack(MekanismBlocks.MachineBlock2, 1, 13), 'O', dye,
              'P', MekaSuitItems.ModuleBase, 'H', hdpe);
        addRecipe(MekaSuitItems.LaserDissipationUnit,
              "A#A", "APA", "HHH", 'A', "alloyAdvanced", '#',
              new ItemStack(MekanismBlocks.MachineBlock2, 1, 14), 'P',
              MekaSuitItems.ModuleBase, 'H', hdpe);
        addRecipe(MekaSuitItems.RadiationShieldingUnit,
              "A#A", "APA", "HHH", 'A', "alloyAdvanced", '#',
              "blockLead", 'P', MekaSuitItems.ModuleBase, 'H', hdpe);

        net.minecraft.item.Item dosimeter = GameRegistry.findItem(
              "mekanismnuclear", "dosimeter");
        if (dosimeter != null)
        {
            addRecipe(MekaSuitItems.DosimeterUnit,
                  "A#A", "APA", "HHH", 'A', "alloyAdvanced", '#',
                  dosimeter, 'P', MekaSuitItems.ModuleBase, 'H', hdpe);
        }
        net.minecraft.item.Item geiger = GameRegistry.findItem(
              "mekanismnuclear", "geiger_counter");
        if (geiger != null)
        {
            addRecipe(MekaSuitItems.GeigerUnit,
                  "A#A", "APA", "HHH", 'A', "alloyAdvanced", '#',
                  geiger, 'P', MekaSuitItems.ModuleBase, 'H', hdpe);
        }
    }

    @SuppressWarnings("unchecked")
    private void addToolModuleRecipes()
    {
        Object hdpe = new ItemStack(MekanismItems.Polyethene, 1, 2);
        addRecipe(MekaSuitItems.ExcavationEscalationUnit,
              "A#A", "APA", "HHH", 'A', "alloyAdvanced", '#',
              Items.iron_pickaxe, 'P', MekaSuitItems.ModuleBase, 'H', hdpe);
        addRecipe(MekaSuitItems.AttackAmplificationUnit,
              "A#A", "APA", "HHH", 'A', "alloyAdvanced", '#',
              Items.iron_sword, 'P', MekaSuitItems.ModuleBase, 'H', hdpe);
        addRecipe(MekaSuitItems.FarmingUnit,
              "A#A", "APA", "HHH", 'A', "alloyAdvanced", '#',
              Items.iron_hoe, 'P', MekaSuitItems.ModuleBase, 'H', hdpe);
        addRecipe(MekaSuitItems.ShearingUnit,
              "A#A", "APA", "HHH", 'A', "alloyAdvanced", '#',
              Items.shears, 'P', MekaSuitItems.ModuleBase, 'H', hdpe);
        addRecipe(MekaSuitItems.SilkTouchUnit,
              "A#A", "DPD", "HHH", 'A', "alloyElite", '#',
              "blockRefinedGlowstone", 'D', Items.diamond_pickaxe, 'P',
              MekaSuitItems.ModuleBase, 'H', MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.FortuneUnit,
              "A#A", "DPD", "HHH", 'A', "alloyElite", '#',
              "blockRefinedGlowstone", 'D', "blockDiamond", 'P',
              MekaSuitItems.ModuleBase, 'H', MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.BlastingUnit,
              "A#A", "CPC", "HHH", 'A', "alloyUltimate", '#',
              Blocks.tnt, 'C', "circuitUltimate", 'P',
              MekaSuitItems.ModuleBase, 'H', MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.VeinMiningUnit,
              "A#A", "xPs", "HHH", 'A', "alloyElite", '#',
              Items.diamond_pickaxe, 'x', Items.diamond_axe, 'P',
              MekaSuitItems.ModuleBase, 's', Items.diamond_shovel, 'H',
              MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.TeleportationUnit,
              "A#A", "APA", "HHH", 'A', "alloyUltimate", '#',
              MekanismItems.TeleportationCore, 'P', MekaSuitItems.ModuleBase,
              'H', MekanismItems.AtomicAlloy);
    }

    private void addHelmetModuleRecipes()
    {
        Object hdpe = new ItemStack(MekanismItems.Polyethene, 1, 2);
        addRecipe(MekaSuitItems.ElectrolyticBreathingUnit,
              "A#A", "APA", "HHH", 'A', "alloyAdvanced", '#',
              MekanismItems.ElectrolyticCore, 'P', MekaSuitItems.ModuleBase,
              'H', hdpe);
        addRecipe(MekaSuitItems.InhalationPurificationUnit,
              "A#A", "APA", "HoH", 'A', "alloyElite", '#',
              MekanismItems.AtomicAlloy, 'P', MekaSuitItems.ModuleBase,
              'H', MekanismItems.AtomicAlloy, 'o', MekanismItems.GasMask);
        addRecipe(MekaSuitItems.VisionEnhancementUnit,
              "A#A", "APA", "HHH", 'A', "alloyElite", '#',
              Items.emerald, 'P', MekaSuitItems.ModuleBase, 'H',
              MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.NutritionalInjectionUnit,
              "A#A", "APA", "HHH", 'A', "alloyElite", '#',
              UltimateItems.Canteen, 'P', MekaSuitItems.ModuleBase, 'H',
              MekanismItems.AtomicAlloy);
    }

    private void addBodyarmorModuleRecipes()
    {
        addRecipe(MekaSuitItems.JetpackUnit,
              "A#A", "APA", "HHH", 'A', "alloyElite", '#',
              MekanismItems.Jetpack, 'P', MekaSuitItems.ModuleBase, 'H',
              "pelletPolonium");
        addRecipe(MekaSuitItems.JetpackUnit,
              "A#A", "APA", "HHH", 'A', "alloyElite", '#',
              MekanismItems.ArmoredJetpack, 'P', MekaSuitItems.ModuleBase,
              'H', "pelletPolonium");
        addRecipe(MekaSuitItems.ChargeDistributionUnit,
              "A#A", "APA", "HHH", 'A', "alloyElite", '#',
              new ItemStack(MekanismBlocks.BasicBlock2, 1, 4), 'P',
              MekaSuitItems.ModuleBase, 'H', "pelletPolonium");
        addRecipe(MekaSuitItems.GravitationalModulatingUnit,
              "A#A", "EPE", "HHH", 'A', "alloyUltimate", '#',
              Items.nether_star, 'E', MekanismUtils.getInductionProvider(
                    InductionProviderTier.ULTIMATE), 'P',
              MekaSuitItems.ModuleBase, 'H', "pelletAntimatter");
        if (EtFuturumElytraCompat.isAvailable())
        {
            addRecipe(MekaSuitItems.ElytraUnit,
                  "A#A", "APA", "HNH", 'A', "alloyElite", '#',
                  UltimateItems.HDPEElytra, 'P', MekaSuitItems.ModuleBase,
                  'H', "pelletPolonium", 'N', "pelletAntimatter");
        }
    }

    private void addMobilityModuleRecipes()
    {
        Object tablet = MekanismItems.EnergyTablet.getUnchargedItem();
        addRecipe(MekaSuitItems.LocomotiveBoostingUnit,
              "A#A", "EPE", "HHH", 'A', "alloyElite", '#',
              Items.diamond_leggings, 'E', tablet, 'P',
              MekaSuitItems.ModuleBase, 'H', MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.GyroscopicStabilizationUnit,
              "A#A", "#P#", "H#H", 'A', "alloyElite", '#',
              Blocks.obsidian, 'P', MekaSuitItems.ModuleBase, 'H',
              MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.HydrostaticRepulsorUnit,
              "A#A", "APA", "HHH", 'A', "alloyElite", '#',
              "ingotCopper", 'P', MekaSuitItems.ModuleBase, 'H',
              MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.MotorizedServoUnit,
              "ACA", "#P#", "HHH", 'A', "alloyElite", 'C',
              "circuitElite", '#', Blocks.packed_ice, 'P',
              MekaSuitItems.ModuleBase, 'H', MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.HydraulicPropulsionUnit,
              "A#A", "EPE", "HHH", 'A', "alloyElite", '#',
              MekanismItems.FreeRunners, 'E', tablet, 'P',
              MekaSuitItems.ModuleBase, 'H', MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.MagneticAttractionUnit,
              "A#A", "CPC", "HHH", 'A', "alloyElite", '#',
              Blocks.iron_bars, 'C', "circuitElite", 'P',
              MekaSuitItems.ModuleBase, 'H', MekanismItems.AtomicAlloy);
        addRecipe(MekaSuitItems.SoulSurferUnit,
              "ABA", "APA", "H#H", 'A', "alloyElite", 'B',
              Blocks.soul_sand, '#', MekanismItems.FreeRunners, 'P',
              MekaSuitItems.ModuleBase, 'H', MekanismItems.AtomicAlloy);
    }

    @SuppressWarnings("unchecked")
    private void addRecipe(net.minecraft.item.Item output, Object... input)
    {
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(new ItemStack(output), input));
    }

    @SuppressWarnings("unchecked")
    private void addArmorRecipe(net.minecraft.item.Item output,
          net.minecraft.item.Item center)
    {
        CraftingManager.getInstance().getRecipeList().add(
              new ShapedMekanismRecipe(new ItemStack(output), new Object[] {
                    "HUH", "HDH", "AIA",
                    Character.valueOf('U'),
                          MekanismUtils.getControlCircuit(BaseTier.ULTIMATE),
                    Character.valueOf('H'),
                          new ItemStack(MekanismItems.Polyethene, 1, 2),
                    Character.valueOf('D'), center,
                    Character.valueOf('A'), MekanismItems.AtomicAlloy,
                    Character.valueOf('I'),
                          MekanismUtils.getEnergyCube(EnergyCubeTier.BASIC)
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
        return "MekaSuit";
    }

    @Override
    public void writeConfig(ByteBuf dataStream) throws IOException
    {
        MekaSuitConfig.write(dataStream);
    }

    @Override
    public void readConfig(ByteBuf dataStream) throws IOException
    {
        MekaSuitConfig.read(dataStream);
    }

    @Override
    public void resetClient()
    {
        loadConfiguration();
    }

    private static void loadConfiguration()
    {
        configuration.load();
        MekaSuitConfig.load(configuration);
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
