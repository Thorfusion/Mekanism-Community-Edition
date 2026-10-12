package mekanism.nuclear.common;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import mekanism.nuclear.common.inventory.ContainerIsotopicCentrifuge;
import mekanism.nuclear.common.inventory.ContainerAntiprotonicNucleosynthesizer;
import mekanism.nuclear.common.inventory.ContainerFissionReactor;
import mekanism.nuclear.common.inventory.ContainerSPS;
import mekanism.nuclear.common.network.PacketFissionReactorControl;
import mekanism.nuclear.common.network.PacketRadiationData;
import mekanism.nuclear.common.tile.TileEntityIsotopicCentrifuge;
import mekanism.nuclear.common.tile.TileEntityAntiprotonicNucleosynthesizer;
import mekanism.nuclear.common.tile.TileEntityRadioactiveWasteBarrel;
import mekanism.nuclear.common.tile.TileEntityFissionReactorLogicAdapter;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class NuclearCommonProxy implements IGuiHandler
{
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
        GameRegistry.registerTileEntity(TileEntitySPSPort.class,
              "mekanismnuclear:sps_port");
    }

    public void registerRenderInformation()
    {
    }

    public void registerPackets(SimpleNetworkWrapper network)
    {
        network.registerMessage(PacketRadiationData.Handler.class,
              PacketRadiationData.class, 0, Side.CLIENT);
        network.registerMessage(PacketFissionReactorControl.Handler.class,
              PacketFissionReactorControl.class, 1, Side.SERVER);
    }

    public void handleRadiationData(double environmental, double dose)
    {
    }

    public int getRadiationScale()
    {
        return 0;
    }

    public void resetRadiationData()
    {
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world,
          int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return id == 0 && tile instanceof TileEntityIsotopicCentrifuge
              ? new ContainerIsotopicCentrifuge(player.inventory,
                    (TileEntityIsotopicCentrifuge)tile)
              : id == 1 && tile instanceof TileEntityFissionReactorPort
                    ? new ContainerFissionReactor(player,
                          (TileEntityFissionReactorPort)tile)
                    : id == 2 && tile instanceof TileEntitySPSPort
                          ? new ContainerSPS(player,
                                (TileEntitySPSPort)tile)
                          : id == 3 && tile instanceof
                                TileEntityAntiprotonicNucleosynthesizer
                                ? new ContainerAntiprotonicNucleosynthesizer(
                                      player.inventory,
                                      (TileEntityAntiprotonicNucleosynthesizer)tile)
                                : null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world,
          int x, int y, int z)
    {
        return null;
    }
}
