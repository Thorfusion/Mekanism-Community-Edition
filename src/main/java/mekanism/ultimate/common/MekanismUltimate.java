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
import mekanism.common.recipe.ShapedMekanismRecipe;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.common.item.ItemBlockUltimateFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = MekanismUltimate.MODID, name = "Mekanism Community Edition: Ultimate", version = "GRADLE_MODVERSION", dependencies = "required-after:Mekanism", guiFactory = "mekanism.ultimate.client.gui.UltimateGuiFactory")
public class MekanismUltimate implements IModule
{
    public static final String MODID = "MekanismUltimate";

    @Instance(MODID)
    public static MekanismUltimate instance;

    @SidedProxy(clientSide = "mekanism.ultimate.client.UltimateClientProxy", serverSide = "mekanism.ultimate.common.UltimateCommonProxy")
    public static UltimateCommonProxy proxy;

    public static Version versionNumber = new Version(GRADLE_VERSIONMOD);

    public static Configuration configuration;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        configuration = new Configuration(new File(event.getModConfigurationDirectory(), "mekanism/MekanismUltimate.cfg"));
        proxy.loadConfiguration();
        GameRegistry.registerBlock(UltimateBlocks.UltimateFactory, ItemBlockUltimateFactory.class, "UltimateFactory");
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.registerTileEntities();
        proxy.registerRenderInformation();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        Mekanism.modulesLoaded.add(this);
        addRecipes();
        Mekanism.logger.info("Loaded MekanismUltimate module.");
    }

    private void addRecipes()
    {
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
    }

    @Override
    public void resetClient()
    {
    }
}
