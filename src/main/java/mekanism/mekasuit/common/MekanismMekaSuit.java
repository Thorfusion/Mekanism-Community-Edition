package mekanism.mekasuit.common;

import io.netty.buffer.ByteBuf;
import mekanism.common.Mekanism;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.config.MekanismConfig;
import mekanism.mekasuit.common.config.MekaSuitConfig;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.MekaSuitInhalationHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitJetpackHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
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

/** Entry point for the separately packaged beta MekaSuit module. */
@Mod(modid = MekanismMekaSuit.MODID, useMetadata = true,
      dependencies = "required-after:mekanism;required-after:mekanismultimate")
@Mod.EventBusSubscriber
public final class MekanismMekaSuit implements IModule {

    public static final String MODID = "mekanismmekasuit";

    @Instance(MODID)
    public static MekanismMekaSuit instance;

    @SidedProxy(clientSide = "mekanism.mekasuit.client.MekaSuitClientProxy",
          serverSide = "mekanism.mekasuit.common.MekaSuitCommonProxy")
    public static MekaSuitCommonProxy proxy;

    public static Version versionNumber = new Version(999, 999, 999);
    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        MekaSuitBlocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        MekaSuitBlocks.registerItemBlocks(event.getRegistry());
        MekaSuitItems.registerItems(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        proxy.registerBlockRenders();
        proxy.registerItemRenders();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MekaSuitModules.bootstrap();
        MekaSuitConfig.load(event.getSuggestedConfigurationFile());
        proxy.registerPackets();
        proxy.registerClientHandlers();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Mekanism.modulesLoaded.add(this);
        MinecraftForge.EVENT_BUS.register(MekaSuitInhalationHelper.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MekaSuitJetpackHandler.INSTANCE);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new MekaSuitGuiHandler());
        proxy.registerTileEntities();
        Mekanism.logger.info("Loaded Mekanism MekaSuit beta module.");
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "MekaSuit";
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
