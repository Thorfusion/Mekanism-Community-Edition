package mekanism.ultimate.common;

import mekanism.api.MekanismConfig.ultimate;
import mekanism.common.inventory.container.ContainerFactory;
import mekanism.ultimate.common.tile.TileEntityUltimateFactory;
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import mekanism.ultimate.common.inventory.ContainerNutritionalLiquifier;
import mekanism.ultimate.common.config.UltimateNutritionConfig;
import mekanism.ultimate.common.config.UltimateGearConfig;
import mekanism.ultimate.common.config.UltimateChemicalTankConfig;
import mekanism.ultimate.common.inventory.ContainerChemicalTank;
import mekanism.ultimate.common.inventory.ContainerPigmentMachine;
import mekanism.ultimate.common.network.PacketStartHDPEElytra;
import mekanism.ultimate.common.tile.TileEntityChemicalTank;
import mekanism.ultimate.common.tile.TileEntityPaintingMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentExtractor;
import mekanism.ultimate.common.tile.TileEntityPigmentMachine;
import mekanism.ultimate.common.tile.TileEntityPigmentMixer;
import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
import mekanism.ultimate.common.tile.TileEntityIndustrialAlarm;
import mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer;
import mekanism.ultimate.common.inventory.ContainerDimensionalStabilizer;
import mekanism.ultimate.common.network.PacketDimensionalStabilizer;
import mekanism.common.inventory.container.ContainerPersonalChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class UltimateCommonProxy implements IGuiHandler
{
    public void registerPackets(SimpleNetworkWrapper network)
    {
        network.registerMessage(PacketStartHDPEElytra.Handler.class,
              PacketStartHDPEElytra.class, 0, Side.SERVER);
        network.registerMessage(PacketDimensionalStabilizer.Handler.class,
              PacketDimensionalStabilizer.class, 1, Side.SERVER);
    }

    public void loadConfiguration()
    {
        ultimate.factoryEnabled = MekanismUltimate.configuration.get("machines", "UltimateFactoryEnabled", true,
            "Enable Ultimate Factories. Disabling this hides them from creative tabs and prevents crafting and tier-installer upgrades; existing placed factories remain usable.").setRequiresMcRestart(true).getBoolean();
        ultimate.allowTierInstallerUpgrade = MekanismUltimate.configuration.get("machines", "AllowTierInstallerUpgrade", true,
            "Allow Ultimate Tier Installers to upgrade Elite Factories into Ultimate Factories.").getBoolean();
        ultimate.factoryUsageMultiplier = MekanismUltimate.configuration.get("usage", "UltimateFactoryUsageMultiplier", 1D,
            "Multiplier applied to Mekanism's FactoryUsage value for each process in an Ultimate Factory.", 0D, 1000D).getDouble();
        ultimate.factoryEnergyStorageTicks = MekanismUltimate.configuration.get("usage", "UltimateFactoryEnergyStorageTicks", 400,
            "Number of ticks of full nine-process base usage that an Ultimate Factory can store.", 1, Integer.MAX_VALUE).getInt();
        ultimate.enableFactoryRecipes = MekanismUltimate.configuration.get("recipes", "EnableUltimateFactoryRecipes", true,
            "Enable crafting recipes for all Ultimate Factory variants.").setRequiresMcRestart(true).getBoolean();
        ultimate.sawmillFactoriesEnabled = MekanismUltimate.configuration.get("machines", "SawingFactoriesEnabled", true,
            "Enable the backported Basic, Advanced, Elite, and Ultimate Sawing Factories. Existing placed factories remain usable when disabled.").setRequiresMcRestart(true).getBoolean();
        UltimateConfig.enableSawmillFactoryRecipes = MekanismUltimate.configuration.get("recipes", "EnableSawingFactoryRecipes", true,
            "Enable crafting recipes for all Sawing Factory tiers.").setRequiresMcRestart(true).getBoolean();
        UltimateConfig.allowDimensionalStabilizerChunkloading = MekanismUltimate.configuration.get("machines", "AllowDimensionalStabilizerChunkloading", true,
            "Allow Dimensional Stabilizers to keep selected chunks loaded.").getBoolean();
        UltimateConfig.dimensionalStabilizerEnergyPerChunk = MekanismUltimate.configuration.get("usage", "DimensionalStabilizerEnergyPerChunk", 5000D,
            "Energy used per tick for each loaded chunk.", 0D, Double.MAX_VALUE).getDouble();
        UltimateConfig.dimensionalStabilizerEnergyStorageTicks = MekanismUltimate.configuration.get("usage", "DimensionalStabilizerEnergyStorageTicks", 400,
            "Energy buffer duration in ticks when all 25 chunks are selected.", 1, Integer.MAX_VALUE).getInt();
        UltimateConfig.enableDimensionalStabilizerRecipe = MekanismUltimate.configuration.get("recipes", "EnableDimensionalStabilizerRecipe", true,
            "Enable the Dimensional Stabilizer crafting recipe.").setRequiresMcRestart(true).getBoolean();
        UltimateNutritionConfig.load(MekanismUltimate.configuration);
        UltimateGearConfig.load(MekanismUltimate.configuration);
        UltimateChemicalTankConfig.load(MekanismUltimate.configuration);

        if (MekanismUltimate.configuration.hasChanged())
        {
            MekanismUltimate.configuration.save();
        }
    }

    public void registerTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityUltimateFactory.class, "MekanismUltimateUltimateFactory");
        GameRegistry.registerTileEntity(TileEntityNutritionalLiquifier.class,
              "mekanismultimate:nutritional_liquifier");
        GameRegistry.registerTileEntity(TileEntityChemicalTank.class,
              "mekanismultimate:chemical_tank");
        GameRegistry.registerTileEntity(TileEntityPigmentExtractor.class,
              "mekanismultimate:pigment_extractor");
        GameRegistry.registerTileEntity(TileEntityPigmentMixer.class,
              "mekanismultimate:pigment_mixer");
        GameRegistry.registerTileEntity(TileEntityPaintingMachine.class,
              "mekanismultimate:painting_machine");
        GameRegistry.registerTileEntity(TileEntityPersonalBarrel.class,
              "mekanismultimate:personal_barrel");
        GameRegistry.registerTileEntity(TileEntityIndustrialAlarm.class,
              "mekanismultimate:industrial_alarm");
        GameRegistry.registerTileEntity(
              TileEntityDimensionalStabilizer.class,
              "mekanismultimate:dimensional_stabilizer");
    }

    public void registerRenderInformation()
    {
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return id == 0 && tile instanceof TileEntityUltimateFactory
              ? new ContainerFactory(player.inventory,
                    (TileEntityUltimateFactory)tile)
              : id == 1 && tile instanceof TileEntityNutritionalLiquifier
                    ? new ContainerNutritionalLiquifier(player.inventory,
                          (TileEntityNutritionalLiquifier)tile)
                    : id == 2 && tile instanceof TileEntityChemicalTank
                          ? new ContainerChemicalTank(player.inventory,
                                (TileEntityChemicalTank)tile)
                          : id == 3
                                && tile instanceof TileEntityPersonalBarrel
                                      ? new ContainerPersonalChest(
                                            player.inventory,
                                            (TileEntityPersonalBarrel)tile,
                                            null, true, -1)
                          : id == 7
                                && tile instanceof
                                      TileEntityDimensionalStabilizer
                                      ? new ContainerDimensionalStabilizer(
                                            player.inventory,
                                            (TileEntityDimensionalStabilizer)
                                                  tile)
                          : id >= 4 && id <= 6
                                && tile instanceof TileEntityPigmentMachine
                                      ? new ContainerPigmentMachine(
                                            player.inventory,
                                            (TileEntityPigmentMachine)tile)
                                      : null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }
}
