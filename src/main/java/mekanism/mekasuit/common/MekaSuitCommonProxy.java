package mekanism.mekasuit.common;

import mekanism.common.base.IGuiProvider;
import mekanism.mekasuit.common.inventory.ContainerModificationStation;
import mekanism.mekasuit.common.network.PacketModificationStationAction;
import mekanism.mekasuit.common.network.PacketMekaSuitModeChange;
import mekanism.mekasuit.common.network.PacketMekaSuitBoostState;
import mekanism.mekasuit.common.network.PacketMekaSuitGravitationalMode;
import mekanism.mekasuit.common.network.PacketMekaSuitElytraMode;
import mekanism.mekasuit.common.network.PacketMekaSuitStartElytra;
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
        MekanismMekaSuit.network.registerMessage(PacketMekaSuitModeChange.Handler.class,
              PacketMekaSuitModeChange.class, 1, Side.SERVER);
        MekanismMekaSuit.network.registerMessage(PacketMekaSuitGravitationalMode.Handler.class,
              PacketMekaSuitGravitationalMode.class, 2, Side.SERVER);
        MekanismMekaSuit.network.registerMessage(PacketMekaSuitBoostState.Handler.class,
              PacketMekaSuitBoostState.class, 3, Side.SERVER);
        MekanismMekaSuit.network.registerMessage(PacketMekaSuitElytraMode.Handler.class,
              PacketMekaSuitElytraMode.class, 4, Side.SERVER);
        MekanismMekaSuit.network.registerMessage(PacketMekaSuitStartElytra.Handler.class,
              PacketMekaSuitStartElytra.class, 5, Side.SERVER);
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
