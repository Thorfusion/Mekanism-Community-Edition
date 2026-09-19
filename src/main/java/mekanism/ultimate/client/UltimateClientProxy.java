package mekanism.ultimate.client;

import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import mekanism.client.gui.GuiFactory;
import mekanism.common.base.IFactory;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.block.states.BlockStateFacing;
import mekanism.common.block.states.BlockStateMachine;
import mekanism.ultimate.common.MekanismUltimate;
import mekanism.ultimate.common.UltimateBlocks;
import mekanism.ultimate.common.UltimateCommonProxy;
import mekanism.ultimate.common.tile.TileEntityUltimateFactory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UltimateClientProxy extends UltimateCommonProxy {

    private final Map<RecipeType, ModelResourceLocation> itemModels = new EnumMap<>(RecipeType.class);

    @Override
    public void registerBlockRenders() {
        ModelLoader.setCustomStateMapper(UltimateBlocks.UltimateFactory, new UltimateFactoryStateMapper());
        Item item = Item.getItemFromBlock(UltimateBlocks.UltimateFactory);
        for (RecipeType type : RecipeType.values()) {
            ModelResourceLocation model = new ModelResourceLocation(getModelLocation(type), "inventory");
            itemModels.put(type, model);
            ModelLoader.registerItemVariants(item, model);
        }
        ModelLoader.setCustomMeshDefinition(item, stack -> {
            RecipeType type = ((IFactory) stack.getItem()).getRecipeTypeOrNull(stack);
            return itemModels.get(type == null ? RecipeType.SMELTING : type);
        });
    }

    @Override
    public GuiScreen getClientGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (ID == 0 && tile instanceof TileEntityUltimateFactory) {
            return new GuiFactory(player.inventory, (TileEntityUltimateFactory) tile);
        }
        return null;
    }

    private static ResourceLocation getModelLocation(RecipeType type) {
        return new ResourceLocation(MekanismUltimate.MODID, "ultimate_factory_" + type.getName());
    }

    private static class UltimateFactoryStateMapper extends StateMapperBase {

        @Nonnull
        @Override
        protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
            RecipeType recipe = state.getValue(BlockStateMachine.recipeProperty);
            EnumFacing facing = state.getValue(BlockStateFacing.facingProperty);
            if (!facing.getAxis().isHorizontal()) {
                facing = EnumFacing.NORTH;
            }
            String variant = "active=" + state.getValue(BlockStateMachine.activeProperty) + ",facing=" + facing.getName();
            return new ModelResourceLocation(getModelLocation(recipe), variant);
        }
    }
}
