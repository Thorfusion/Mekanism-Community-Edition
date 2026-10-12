package mekanism.mekasuit.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.FMLCommonHandler;
import mekanism.mekasuit.client.gui.GuiModificationStation;
import mekanism.mekasuit.common.MekaSuitCommonProxy;
import mekanism.mekasuit.common.tile.TileEntityModificationStation;
import mekanism.mekasuit.common.MekaSuitItems;
import mekanism.mekasuit.common.MekaSuitBlocks;
import mekanism.ultimate.client.render.LegacyJsonModelRenderer;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(Side.CLIENT)
public class MekaSuitClientProxy extends MekaSuitCommonProxy
{
    @Override
    public void registerRenderInformation()
    {
        LegacyJsonModelRenderer.INSTANCE.registerBlock(
              MekaSuitBlocks.ModificationStation);
        ModelMekaSuitArmor.preload();
        MekaSuitJetpackClientHandler.registerKeyBinding();
        MekaSuitMobilityClientHandler.registerKeyBinding();
        FMLCommonHandler.instance().bus().register(
              MekaSuitJetpackClientHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(
              MekaSuitMobilityClientHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(
              MekaSuitVisionHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MekaSuitVisionHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(
              MekaSuitMobilityHudHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(
              MekaSuitSkinLayerHandler.INSTANCE);
        if (EtFuturumElytraCompat.isAvailable())
        {
            MinecraftForge.EVENT_BUS.register(
                  MekaSuitElytraRenderHandler.INSTANCE);
        }
        MinecraftForgeClient.registerItemRenderer(MekaSuitItems.MekaTool,
              new MekaToolItemRenderer());
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world,
          int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        return id == 0 && tile instanceof TileEntityModificationStation
              ? new GuiModificationStation(player.inventory,
                    (TileEntityModificationStation)tile) : null;
    }
}
