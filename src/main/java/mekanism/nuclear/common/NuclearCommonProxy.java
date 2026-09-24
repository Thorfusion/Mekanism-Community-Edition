package mekanism.nuclear.common;

import mekanism.common.base.IGuiProvider;
import mekanism.nuclear.common.inventory.ContainerIsotopicCentrifuge;
import mekanism.nuclear.common.tile.TileEntityIsotopicCentrifuge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class NuclearCommonProxy implements IGuiProvider {

    public void registerBlockRenders() {
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityIsotopicCentrifuge.class,
              new ResourceLocation(MekanismNuclear.MODID, "isotopic_centrifuge"));
    }

    @Override
    public Object getClientGui(int id, EntityPlayer player, World world, BlockPos pos) {
        return null;
    }

    @Override
    public Container getServerGui(int id, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return id == 0 && tile instanceof TileEntityIsotopicCentrifuge
              ? new ContainerIsotopicCentrifuge(player.inventory, (TileEntityIsotopicCentrifuge) tile) : null;
    }
}
