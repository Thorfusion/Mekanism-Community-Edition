package mekanism.nuclear.common;

import io.netty.buffer.ByteBuf;
import mekanism.common.Mekanism;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.config.MekanismConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Entry point for the separately packaged Nuclear module.
 *
 * <p>Content registration begins with the Isotopic Centrifuge after the shared
 * Ultimate recipe layer is complete. Keeping this entry point content-free
 * verifies the artifact and dependency boundary before registry names exist.</p>
 */
@Mod(modid = MekanismNuclear.MODID, useMetadata = true)
public final class MekanismNuclear implements IModule {

    public static final String MODID = "mekanismnuclear";

    @Instance(MODID)
    public static MekanismNuclear instance;

    public static Version versionNumber = new Version(999, 999, 999);

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Mekanism.modulesLoaded.add(this);
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
