package mekanism.ultimate.common;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import mekanism.api.MekanismConfig;
import mekanism.common.Mekanism;
import mekanism.common.Tier.BaseTier;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.Version;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.base.IModule;
import mekanism.common.recipe.ShapedMekanismRecipe;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.common.item.ItemBlockUltimateFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = MekanismUltimate.MODID, name = "Mekanism Community Edition: Ultimate", version = "GRADLE_MODVERSION", dependencies = "required-after:Mekanism")
public class MekanismUltimate implements IModule
{
    public static final String MODID = "MekanismUltimate";

    @Instance(MODID)
    public static MekanismUltimate instance;

    @SidedProxy(clientSide = "mekanism.ultimate.client.UltimateClientProxy", serverSide = "mekanism.ultimate.common.UltimateCommonProxy")
    public static UltimateCommonProxy proxy;

    public static Version versionNumber = new Version(GRADLE_VERSIONMOD);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
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

        for (RecipeType type : RecipeType.values())
        {
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
    }

    @Override
    public void readConfig(ByteBuf dataStream) throws IOException
    {
    }

    @Override
    public void resetClient()
    {
    }
}
