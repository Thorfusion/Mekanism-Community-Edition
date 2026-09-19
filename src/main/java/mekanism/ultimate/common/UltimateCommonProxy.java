package mekanism.ultimate.common;

import mekanism.api.MekanismConfig.ultimate;
import mekanism.common.inventory.container.ContainerFactory;
import mekanism.ultimate.common.tile.TileEntityUltimateFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class UltimateCommonProxy implements IGuiHandler
{
    public void loadConfiguration()
    {
        ultimate.factoryEnabled = MekanismUltimate.configuration.get("machines", "UltimateFactoryEnabled", true,
            "Enable Ultimate Factories. Disabling this hides them from creative tabs and prevents crafting and tier-installer upgrades; existing placed factories remain usable.").getBoolean();
        ultimate.allowTierInstallerUpgrade = MekanismUltimate.configuration.get("machines", "AllowTierInstallerUpgrade", true,
            "Allow Ultimate Tier Installers to upgrade Elite Factories into Ultimate Factories.").getBoolean();
        ultimate.factoryUsageMultiplier = MekanismUltimate.configuration.get("usage", "UltimateFactoryUsageMultiplier", 1D,
            "Multiplier applied to Mekanism's FactoryUsage value for each process in an Ultimate Factory.", 0D, 1000D).getDouble();
        ultimate.factoryEnergyStorageTicks = MekanismUltimate.configuration.get("usage", "UltimateFactoryEnergyStorageTicks", 400,
            "Number of ticks of full nine-process base usage that an Ultimate Factory can store.", 1, Integer.MAX_VALUE).getInt();
        ultimate.enableFactoryRecipes = MekanismUltimate.configuration.get("recipes", "EnableUltimateFactoryRecipes", true,
            "Enable crafting recipes for all Ultimate Factory variants.").getBoolean();
        ultimate.sawmillFactoriesEnabled = MekanismUltimate.configuration.get("machines", "SawingFactoriesEnabled", true,
            "Enable the backported Basic, Advanced, Elite, and Ultimate Sawing Factories. Existing placed factories remain usable when disabled.").setRequiresMcRestart(true).getBoolean();
        UltimateConfig.enableSawmillFactoryRecipes = MekanismUltimate.configuration.get("recipes", "EnableSawingFactoryRecipes", true,
            "Enable crafting recipes for all Sawing Factory tiers.").setRequiresMcRestart(true).getBoolean();

        if (MekanismUltimate.configuration.hasChanged())
        {
            MekanismUltimate.configuration.save();
        }
    }

    public void registerTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityUltimateFactory.class, "MekanismUltimateUltimateFactory");
    }

    public void registerRenderInformation()
    {
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return id == 0 && tile instanceof TileEntityUltimateFactory ? new ContainerFactory(player.inventory, (TileEntityUltimateFactory)tile) : null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}
