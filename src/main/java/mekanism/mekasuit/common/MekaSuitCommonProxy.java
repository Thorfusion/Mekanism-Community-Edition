package mekanism.mekasuit.common;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import mekanism.mekasuit.common.inventory.ContainerModificationStation;
import mekanism.mekasuit.common.network.PacketModificationStationAction;
import mekanism.mekasuit.common.network.PacketMekaSuitBodyMode;
import mekanism.mekasuit.common.network.PacketMekaSuitArmorMode;
import mekanism.mekasuit.common.network.PacketMekaSuitElytraMode;
import mekanism.mekasuit.common.network.PacketMekaSuitStartElytra;
import mekanism.mekasuit.common.tile.TileEntityModificationStation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/** Common-side lifecycle surface for the standalone MekaSuit module. */
public class MekaSuitCommonProxy implements IGuiHandler
{
    public void registerTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityModificationStation.class,
              "mekanismmekasuit:modification_station");
    }

    public void registerPackets(SimpleNetworkWrapper network)
    {
        network.registerMessage(PacketModificationStationAction.Handler.class,
              PacketModificationStationAction.class, 0, Side.SERVER);
        network.registerMessage(PacketMekaSuitBodyMode.Handler.class,
              PacketMekaSuitBodyMode.class, 1, Side.SERVER);
        network.registerMessage(PacketMekaSuitArmorMode.Handler.class,
              PacketMekaSuitArmorMode.class, 2, Side.SERVER);
        network.registerMessage(PacketMekaSuitElytraMode.Handler.class,
              PacketMekaSuitElytraMode.class, 3, Side.SERVER);
        network.registerMessage(PacketMekaSuitStartElytra.Handler.class,
              PacketMekaSuitStartElytra.class, 4, Side.SERVER);
    }

    public void registerRenderInformation()
    {
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world,
          int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return id == 0 && tile instanceof TileEntityModificationStation
              ? new ContainerModificationStation(player.inventory,
                    (TileEntityModificationStation)tile) : null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world,
          int x, int y, int z)
    {
        return null;
    }
}
