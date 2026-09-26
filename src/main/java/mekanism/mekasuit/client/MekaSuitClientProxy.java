package mekanism.mekasuit.client;

import mekanism.mekasuit.client.gui.GuiModificationStation;
import mekanism.mekasuit.common.MekaSuitBlocks;
import mekanism.mekasuit.common.MekaSuitCommonProxy;
import mekanism.mekasuit.common.MekaSuitItems;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.tile.TileEntityModificationStation;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class MekaSuitClientProxy extends MekaSuitCommonProxy {

    @Override
    public void registerClientHandlers() {
        ClientRegistry.registerKeyBinding(MekaSuitJetpackClientHandler.boostKey);
        MinecraftForge.EVENT_BUS.register(MekaSuitVisionHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(MekaSuitJetpackClientHandler.INSTANCE);
    }

    @Override
    public void registerBlockRenders() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MekaSuitBlocks.ModificationStation), 0,
              new ModelResourceLocation(MekaSuitBlocks.ModificationStation.getRegistryName(), "inventory"));
    }

    @Override
    public void registerItemRenders() {
        OBJLoader.INSTANCE.addDomain(MekanismMekaSuit.MODID);
        for (Item item : MekaSuitItems.allRegistered()) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                  new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }

    @Override
    public GuiScreen getClientGui(int id, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return id == 0 && tile instanceof TileEntityModificationStation
              ? new GuiModificationStation(player.inventory, (TileEntityModificationStation) tile) : null;
    }
}
