package mekanism.ultimate.common;

import mekanism.common.Mekanism;
import mekanism.common.base.IGuiProvider;
import mekanism.common.config.MekanismConfig;
import mekanism.common.inventory.container.ContainerFactory;
import mekanism.ultimate.common.tile.TileEntityUltimateFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class UltimateCommonProxy implements IGuiProvider {

    public void preInit() {
    }

    public void registerBlockRenders() {
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityUltimateFactory.class, new ResourceLocation(MekanismUltimate.MODID, "ultimate_factory"));
    }

    public void loadConfiguration() {
        if (MekanismConfig.local().ultimate != null) {
            MekanismConfig.local().ultimate.load(Mekanism.configurationultimate);
            if (Mekanism.configurationultimate.hasChanged()) {
                Mekanism.configurationultimate.save();
            }
        }
    }

    @Override
    public Object getClientGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        return null;
    }

    @Override
    public Container getServerGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (ID == 0 && tile instanceof TileEntityUltimateFactory) {
            return new ContainerFactory(player.inventory, (TileEntityUltimateFactory) tile);
        }
        return null;
    }
}
