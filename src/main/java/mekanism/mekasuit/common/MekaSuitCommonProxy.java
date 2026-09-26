package mekanism.mekasuit.common;

import mekanism.common.base.IGuiProvider;
import mekanism.mekasuit.common.inventory.ContainerModificationStation;
import mekanism.mekasuit.common.network.PacketModificationStationAction;
import mekanism.mekasuit.common.tile.TileEntityModificationStation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class MekaSuitCommonProxy implements IGuiProvider {

    public void registerBlockRenders() {
    }

    public void registerItemRenders() {
    }

    public void registerPackets() {
        MekanismMekaSuit.network.registerMessage(PacketModificationStationAction.Handler.class,
              PacketModificationStationAction.class, 0, Side.SERVER);
    }

    public void registerClientHandlers() {
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityModificationStation.class,
              new ResourceLocation(MekanismMekaSuit.MODID, "modification_station"));
    }

    @Override
    public Object getClientGui(int id, EntityPlayer player, World world, BlockPos pos) {
        return null;
    }

    @Override
    public Container getServerGui(int id, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return id == 0 && tile instanceof TileEntityModificationStation
              ? new ContainerModificationStation(player.inventory, (TileEntityModificationStation) tile) : null;
    }
}
