package mekanism.nuclear.common;

import mekanism.common.base.IGuiProvider;
import mekanism.nuclear.common.inventory.ContainerIsotopicCentrifuge;
import mekanism.nuclear.common.inventory.ContainerFissionReactor;
import mekanism.nuclear.common.inventory.ContainerSPS;
import mekanism.nuclear.common.network.PacketFissionReactorControl;
import mekanism.nuclear.common.network.PacketRadiationData;
import mekanism.nuclear.common.tile.TileEntityIsotopicCentrifuge;
import mekanism.nuclear.common.tile.TileEntityRadioactiveWasteBarrel;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import mekanism.nuclear.common.tile.TileEntityFissionReactorLogicAdapter;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class NuclearCommonProxy implements IGuiProvider {

    public void registerBlockRenders() {
    }

    public void registerItemRenders() {
    }

    public void registerPackets() {
        MekanismNuclear.network.registerMessage(PacketRadiationData.Handler.class,
              PacketRadiationData.class, 0, Side.CLIENT);
        MekanismNuclear.network.registerMessage(PacketFissionReactorControl.Handler.class,
              PacketFissionReactorControl.class, 1, Side.SERVER);
    }

    public void handleRadiationData(double environmental, double dose) {
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityIsotopicCentrifuge.class,
              new ResourceLocation(MekanismNuclear.MODID, "isotopic_centrifuge"));
        GameRegistry.registerTileEntity(TileEntityRadioactiveWasteBarrel.class,
              new ResourceLocation(MekanismNuclear.MODID, "radioactive_waste_barrel"));
        GameRegistry.registerTileEntity(TileEntityFissionReactorPort.class,
              new ResourceLocation(MekanismNuclear.MODID, "fission_reactor_port"));
        GameRegistry.registerTileEntity(TileEntityFissionReactorLogicAdapter.class,
              new ResourceLocation(MekanismNuclear.MODID, "fission_reactor_logic_adapter"));
        GameRegistry.registerTileEntity(TileEntitySPSPort.class,
              new ResourceLocation(MekanismNuclear.MODID, "sps_port"));
    }

    @Override
    public Object getClientGui(int id, EntityPlayer player, World world, BlockPos pos) {
        return null;
    }

    @Override
    public Container getServerGui(int id, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return id == 0 && tile instanceof TileEntityIsotopicCentrifuge
              ? new ContainerIsotopicCentrifuge(player.inventory, (TileEntityIsotopicCentrifuge) tile)
              : id == 1 && tile instanceof TileEntityFissionReactorPort
                    ? new ContainerFissionReactor(player, (TileEntityFissionReactorPort) tile)
                    : id == 2 && tile instanceof TileEntitySPSPort
                          ? new ContainerSPS(player, (TileEntitySPSPort) tile) : null;
    }
}
