package mekanism.nuclear.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.nuclear.client.gui.GuiIsotopicCentrifuge;
import mekanism.nuclear.client.gui.GuiAntiprotonicNucleosynthesizer;
import mekanism.nuclear.client.gui.GuiFissionReactor;
import mekanism.nuclear.client.gui.GuiSPS;
import mekanism.nuclear.client.radiation.ClientRadiationData;
import mekanism.nuclear.client.radiation.RadiationHudOverlay;
import mekanism.nuclear.client.render.RenderSPS;
import mekanism.ultimate.client.render.LegacyJsonModelRenderer;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.NuclearChemicals;
import mekanism.nuclear.common.NuclearCommonProxy;
import mekanism.nuclear.common.tile.TileEntityIsotopicCentrifuge;
import mekanism.nuclear.common.tile.TileEntityAntiprotonicNucleosynthesizer;
import mekanism.nuclear.common.tile.TileEntityRadioactiveWasteBarrel;
import mekanism.nuclear.common.tile.TileEntityFissionReactorLogicAdapter;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class NuclearClientProxy extends NuclearCommonProxy
{
    @Override
    public void registerTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityIsotopicCentrifuge.class,
              "mekanismnuclear:isotopic_centrifuge");
        GameRegistry.registerTileEntity(
              TileEntityAntiprotonicNucleosynthesizer.class,
              "mekanismnuclear:antiprotonic_nucleosynthesizer");
        GameRegistry.registerTileEntity(TileEntityRadioactiveWasteBarrel.class,
              "mekanismnuclear:radioactive_waste_barrel");
        GameRegistry.registerTileEntity(TileEntityFissionReactorPort.class,
              "mekanismnuclear:fission_reactor_port");
        GameRegistry.registerTileEntity(TileEntityFissionReactorLogicAdapter.class,
              "mekanismnuclear:fission_reactor_logic_adapter");
        ClientRegistry.registerTileEntity(TileEntitySPSPort.class,
              "mekanismnuclear:sps_port", new RenderSPS());
    }

    @Override
    public void registerRenderInformation()
    {
        LegacyJsonModelRenderer.INSTANCE.registerBlock(
              NuclearBlocks.IsotopicCentrifuge);
        LegacyJsonModelRenderer.INSTANCE.registerBlock(
              NuclearBlocks.RadioactiveWasteBarrel);
        LegacyJsonModelRenderer.INSTANCE.registerBlock(
              NuclearBlocks.FissionFuelAssembly);
        LegacyJsonModelRenderer.INSTANCE.registerBlock(
              NuclearBlocks.ControlRodAssembly);
        LegacyJsonModelRenderer.INSTANCE.registerBlock(
              NuclearBlocks.SuperchargedCoil);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(RadiationHudOverlay.INSTANCE);
    }

    @Override
    public void handleRadiationData(double environmental, double dose)
    {
        ClientRadiationData.set(environmental, dose);
    }

    @Override
    public int getRadiationScale()
    {
        return ClientRadiationData.getEnvironmentalScale();
    }

    @Override
    public void resetRadiationData()
    {
        ClientRadiationData.reset();
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event)
    {
        if (event.map.getTextureType() != 0)
        {
            return;
        }
        NuclearChemicals.HydrofluoricAcid.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidHydrogenChloride"));
        NuclearChemicals.UraniumOxide.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidOre"));
        NuclearChemicals.UraniumHexafluoride.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidCleanOre"));
        NuclearChemicals.FissileFuel.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidEthene"));
        NuclearChemicals.Sodium.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidSodium"));
        NuclearChemicals.SuperheatedSodium.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidSodium"));
        NuclearChemicals.NuclearWaste.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidOre"));
        NuclearChemicals.SpentNuclearWaste.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidOre"));
        NuclearChemicals.Plutonium.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidOsmium"));
        NuclearChemicals.Polonium.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidOsmium"));
        NuclearChemicals.Antimatter.setIcon(
              event.map.registerIcon("mekanism:liquid/LiquidEnergy"));
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world,
          int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return id == 0 && tile instanceof TileEntityIsotopicCentrifuge
              ? new GuiIsotopicCentrifuge(player.inventory,
                    (TileEntityIsotopicCentrifuge)tile)
              : id == 1 && tile instanceof TileEntityFissionReactorPort
                    ? new GuiFissionReactor(player,
                          (TileEntityFissionReactorPort)tile)
                    : id == 2 && tile instanceof TileEntitySPSPort
                          ? new GuiSPS(player,
                                (TileEntitySPSPort)tile)
                          : id == 3 && tile instanceof
                                TileEntityAntiprotonicNucleosynthesizer
                                ? new GuiAntiprotonicNucleosynthesizer(
                                      player.inventory,
                                      (TileEntityAntiprotonicNucleosynthesizer)tile)
                                : null;
    }
}
