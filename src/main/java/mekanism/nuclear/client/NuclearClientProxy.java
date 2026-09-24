package mekanism.nuclear.client;

import mekanism.nuclear.client.gui.GuiIsotopicCentrifuge;
import mekanism.nuclear.client.radiation.ClientRadiationData;
import mekanism.nuclear.client.radiation.RadiationHudOverlay;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.NuclearCommonProxy;
import mekanism.nuclear.common.NuclearItems;
import mekanism.nuclear.common.tile.TileEntityIsotopicCentrifuge;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NuclearClientProxy extends NuclearCommonProxy {

    @Override
    public void registerBlockRenders() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(NuclearBlocks.IsotopicCentrifuge), 0,
              new ModelResourceLocation(new ResourceLocation(MekanismNuclear.MODID, "isotopic_centrifuge"), "inventory"));
        registerBlockItem(NuclearBlocks.RadioactiveWasteBarrel);
        registerBlockItem(NuclearBlocks.UraniumOre);
        registerBlockItem(NuclearBlocks.FluoriteOre);
        registerBlockItem(NuclearBlocks.FissionReactorCasing);
        registerBlockItem(NuclearBlocks.ReactorGlass);
        registerBlockItem(NuclearBlocks.FissionReactorPort);
        registerBlockItem(NuclearBlocks.FissionReactorLogicAdapter);
        registerBlockItem(NuclearBlocks.FissionFuelAssembly);
        registerBlockItem(NuclearBlocks.ControlRodAssembly);
    }

    private static void registerBlockItem(net.minecraft.block.Block block) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
              new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }

    @Override
    public void registerItemRenders() {
        for (Item item : NuclearItems.allRegistered()) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                  new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
        NuclearItems.GeigerCounter.addPropertyOverride(
              new ResourceLocation(MekanismNuclear.MODID, "radiation"),
              (stack, world, entity) -> ClientRadiationData.getEnvironmentalScale());
    }

    @Override
    public void registerPackets() {
        super.registerPackets();
        MinecraftForge.EVENT_BUS.register(RadiationHudOverlay.INSTANCE);
    }

    @Override
    public void handleRadiationData(double environmental, double dose) {
        net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(
              () -> ClientRadiationData.set(environmental, dose));
    }

    @Override
    public GuiScreen getClientGui(int id, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return id == 0 && tile instanceof TileEntityIsotopicCentrifuge
              ? new GuiIsotopicCentrifuge(player.inventory, (TileEntityIsotopicCentrifuge) tile) : null;
    }
}
