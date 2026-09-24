package mekanism.nuclear.client;

import mekanism.nuclear.client.gui.GuiIsotopicCentrifuge;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.NuclearBlocks;
import mekanism.nuclear.common.NuclearCommonProxy;
import mekanism.nuclear.common.NuclearItems;
import mekanism.nuclear.common.item.ItemNuclearMaterial;
import mekanism.nuclear.common.tile.TileEntityIsotopicCentrifuge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NuclearClientProxy extends NuclearCommonProxy {

    @Override
    public void registerBlockRenders() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(NuclearBlocks.IsotopicCentrifuge), 0,
              new ModelResourceLocation(new ResourceLocation(MekanismNuclear.MODID, "isotopic_centrifuge"), "inventory"));
    }

    @Override
    public void registerItemRenders() {
        for (ItemNuclearMaterial item : NuclearItems.all()) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                  new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(
              (stack, tintIndex) -> tintIndex == 0 && stack.getItem() instanceof ItemNuclearMaterial
                    ? ((ItemNuclearMaterial) stack.getItem()).getTint() : 0xFFFFFF,
              NuclearItems.all().toArray(new Item[0]));
    }

    @Override
    public GuiScreen getClientGui(int id, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return id == 0 && tile instanceof TileEntityIsotopicCentrifuge
              ? new GuiIsotopicCentrifuge(player.inventory, (TileEntityIsotopicCentrifuge) tile) : null;
    }
}
