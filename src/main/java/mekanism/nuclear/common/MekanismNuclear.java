package mekanism.nuclear.common;

import io.netty.buffer.ByteBuf;
import mekanism.common.Mekanism;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.config.MekanismConfig;
import mekanism.nuclear.common.config.NuclearWorldGenConfig;
import mekanism.nuclear.common.config.NuclearRadiationConfig;
import mekanism.nuclear.common.content.fission.FissionReactorFormationManager;
import mekanism.nuclear.common.recipe.NuclearLegacyRecipeRegistry;
import mekanism.nuclear.common.recipe.NuclearRecipeRegistry;
import mekanism.nuclear.common.radiation.RadiationCapabilities;
import mekanism.nuclear.common.world.NuclearWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

/**
 * Entry point for the separately packaged Nuclear module.
 *
 * <p>The first registered vertical slice is the Isotopic Centrifuge, built on
 * the shared Ultimate chemical and recipe layer.</p>
 */
@Mod(modid = MekanismNuclear.MODID, useMetadata = true)
@Mod.EventBusSubscriber
public final class MekanismNuclear implements IModule {

    public static final String MODID = "mekanismnuclear";

    @Instance(MODID)
    public static MekanismNuclear instance;

    @SidedProxy(clientSide = "mekanism.nuclear.client.NuclearClientProxy", serverSide = "mekanism.nuclear.common.NuclearCommonProxy")
    public static NuclearCommonProxy proxy;

    public static Version versionNumber = new Version(999, 999, 999);
    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        NuclearBlocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        NuclearBlocks.registerItemBlocks(event.getRegistry());
        NuclearItems.registerItems(event.getRegistry());
        // Register before the recipe registry event so legacy Mekanism and
        // other mods can discover these material identities while adding recipes.
        NuclearOreDictionary.register();
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        proxy.registerBlockRenders();
        proxy.registerItemRenders();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RadiationCapabilities.register();
        proxy.registerPackets();
        NuclearWorldGenConfig.load(event.getSuggestedConfigurationFile());
        NuclearRadiationConfig.load(event.getSuggestedConfigurationFile());
        NuclearChemicals.register();
        NuclearRecipeRegistry.registerDefaults();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Mekanism.modulesLoaded.add(this);
        NuclearLegacyRecipeRegistry.register();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new NuclearGuiHandler());
        proxy.registerTileEntities();
        MinecraftForge.EVENT_BUS.register(FissionReactorFormationManager.INSTANCE);
        GameRegistry.registerWorldGenerator(NuclearWorldGenerator.INSTANCE, 2);
        Mekanism.logger.info("Loaded Mekanism Nuclear module.");
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "Nuclear";
    }

    @Override
    public void writeConfig(ByteBuf dataStream, MekanismConfig config) {
    }

    @Override
    public void readConfig(ByteBuf dataStream, MekanismConfig destConfig) {
    }

    @Override
    public void resetClient() {
    }
}
