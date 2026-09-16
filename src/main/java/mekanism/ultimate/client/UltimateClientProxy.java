package mekanism.ultimate.client;

import mekanism.client.gui.GuiFactory;
import mekanism.client.render.tileentity.RenderConfigurableMachine;
import mekanism.ultimate.client.render.UltimateFactoryItemRenderer;
import mekanism.ultimate.common.UltimateBlocks;
import mekanism.ultimate.common.UltimateCommonProxy;
import mekanism.ultimate.common.tile.TileEntityUltimateFactory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UltimateClientProxy extends UltimateCommonProxy
{
    @Override
    public void registerTileEntities()
    {
        ClientRegistry.registerTileEntity(TileEntityUltimateFactory.class, "MekanismUltimateUltimateFactory", new RenderConfigurableMachine());
    }

    @Override
    public void registerRenderInformation()
    {
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(UltimateBlocks.UltimateFactory), new UltimateFactoryItemRenderer());
    }

    @Override
    public GuiScreen getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return id == 0 && tile instanceof TileEntityUltimateFactory ? new GuiFactory(player.inventory, (TileEntityUltimateFactory)tile) : null;
    }
}
