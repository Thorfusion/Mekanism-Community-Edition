package mekanism.ultimate.common;

import io.netty.buffer.ByteBuf;
import mekanism.common.Mekanism;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.config.MekanismConfig;
import mekanism.ultimate.common.capability.UltimateChemicalCapabilities;
import mekanism.ultimate.common.recipe.UltimatePigmentRecipes;
import mekanism.common.content.tank.DynamicTankChemicalHooks;
import mekanism.ultimate.common.integration.mekanism.DynamicTankChemicalIntegration;
import mekanism.ultimate.common.content.gear.HDPEElytraTickHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = MekanismUltimate.MODID, useMetadata = true,
      dependencies = "required-after:mekanism",
      guiFactory = "mekanism.ultimate.client.gui.UltimateGuiFactory")
@Mod.EventBusSubscriber
public class MekanismUltimate implements IModule {

    public static final String MODID = "mekanismultimate";

    @Instance(MODID)
    public static MekanismUltimate instance;

    @SidedProxy(clientSide = "mekanism.ultimate.client.UltimateClientProxy", serverSide = "mekanism.ultimate.common.UltimateCommonProxy")
    public static UltimateCommonProxy proxy;

    public static Version versionNumber = new Version(999, 999, 999);
    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        UltimateBlocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        UltimateBlocks.registerItemBlocks(event.getRegistry());
        UltimateItems.registerItems(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(UltimateSounds.INDUSTRIAL_ALARM);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        proxy.registerBlockRenders();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        UltimateChemicalCapabilities.register();
        UltimateFluids.register();
        DynamicTankChemicalHooks.register(DynamicTankChemicalIntegration.INSTANCE);
        proxy.registerPackets();
        proxy.preInit();
        proxy.loadConfiguration();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        UltimatePigmentRecipes.registerDefaults();
        Mekanism.modulesLoaded.add(this);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new UltimateGuiHandler());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(HDPEElytraTickHandler.INSTANCE);
        proxy.registerTileEntities();
        Mekanism.logger.info("Loaded Mekanism Ultimate module.");
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "Ultimate";
    }

    @Override
    public void writeConfig(ByteBuf dataStream, MekanismConfig config) {
        config.ultimate.write(dataStream);
    }

    @Override
    public void readConfig(ByteBuf dataStream, MekanismConfig destConfig) {
        destConfig.ultimate.read(dataStream);
    }

    @Override
    public void resetClient() {
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID) || event.getModID().equals(Mekanism.MODID)) {
            proxy.loadConfiguration();
        }
    }
}
