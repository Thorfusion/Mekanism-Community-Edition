package mekanism.ultimate.common;

import mekanism.common.inventory.container.ContainerFactory;
import mekanism.ultimate.common.tile.TileEntityUltimateFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class UltimateCommonProxy implements IGuiHandler
{
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
