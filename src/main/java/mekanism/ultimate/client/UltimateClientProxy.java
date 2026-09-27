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
import mekanism.ultimate.common.tile.TileEntityNutritionalLiquifier;
import mekanism.ultimate.client.gui.GuiNutritionalLiquifier;
import mekanism.ultimate.client.gui.GuiChemicalTank;
import mekanism.ultimate.client.render.RenderChemicalTank;
import mekanism.ultimate.client.render.RenderPersonalBarrel;
import mekanism.ultimate.common.UltimateItems;
import mekanism.client.sound.SoundHandler;
import mekanism.ultimate.common.UltimateSounds;
import mekanism.ultimate.common.tile.TileEntityPersonalBarrel;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UltimateClientProxy extends UltimateCommonProxy {

    private final Map<RecipeType, ModelResourceLocation> itemModels = new EnumMap<>(RecipeType.class);

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(ArmoredFreeRunnersModelHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(HDPEElytraClientHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(HDPEElytraRenderHandler.INSTANCE);
        UltimateItems.ArmoredFreeRunners.setTileEntityItemStackRenderer(new RenderArmoredFreeRunners());
        ClientRegistry.bindTileEntitySpecialRenderer(
              mekanism.ultimate.common.tile.TileEntityChemicalTank.class,
              new RenderChemicalTank());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPersonalBarrel.class,
              new RenderPersonalBarrel());
    }

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
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(UltimateBlocks.NutritionalLiquifier), 0,
              new ModelResourceLocation(UltimateBlocks.NutritionalLiquifier.getRegistryName(), "inventory"));
        for (net.minecraft.block.Block block : new net.minecraft.block.Block[]{
              UltimateBlocks.PersonalBarrel, UltimateBlocks.IndustrialAlarm, UltimateBlocks.BioFuelBlock}) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                  new ModelResourceLocation(block.getRegistryName(), "inventory"));
        }
        for (net.minecraft.block.Block block : new net.minecraft.block.Block[]{
              UltimateBlocks.PigmentExtractor, UltimateBlocks.PigmentMixer, UltimateBlocks.PaintingMachine}) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0,
                  new ModelResourceLocation(block.getRegistryName(), "inventory"));
        }
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(UltimateBlocks.DimensionalStabilizer), 0,
              new ModelResourceLocation(UltimateBlocks.DimensionalStabilizer.getRegistryName(), "inventory"));
        for (mekanism.ultimate.common.tier.ChemicalTankTier tier
              : mekanism.ultimate.common.tier.ChemicalTankTier.values()) {
            Item tankItem = Item.getItemFromBlock(UltimateBlocks.getChemicalTank(tier));
            ModelLoader.setCustomModelResourceLocation(tankItem, 0,
                  new ModelResourceLocation(tankItem.getRegistryName(), "inventory"));
        }
        for (Item ultimateItem : UltimateItems.allRegistered()) {
            ModelLoader.setCustomModelResourceLocation(ultimateItem, 0,
                  new ModelResourceLocation(ultimateItem.getRegistryName(), "inventory"));
        }
    }

    @Override
    public GuiScreen getClientGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (ID == 0 && tile instanceof TileEntityUltimateFactory) {
            return new GuiFactory(player.inventory, (TileEntityUltimateFactory) tile);
        } else if (ID == 1 && tile instanceof TileEntityNutritionalLiquifier) {
            return new GuiNutritionalLiquifier(player.inventory, (TileEntityNutritionalLiquifier) tile);
        } else if (ID == 2 && tile instanceof mekanism.ultimate.common.tile.TileEntityChemicalTank) {
            return new GuiChemicalTank(player.inventory,
                  (mekanism.ultimate.common.tile.TileEntityChemicalTank) tile);
        } else if (ID == 3 && tile instanceof TileEntityPersonalBarrel) {
            return new mekanism.ultimate.client.gui.GuiPersonalBarrel(player.inventory,
                  (TileEntityPersonalBarrel) tile);
        } else if (ID >= 4 && ID <= 6 && tile instanceof mekanism.ultimate.common.tile.TileEntityPigmentMachine) {
            return new mekanism.ultimate.client.gui.GuiPigmentMachine(player.inventory,
                  (mekanism.ultimate.common.tile.TileEntityPigmentMachine) tile);
        } else if (ID == 7 && tile instanceof mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer) {
            return new mekanism.ultimate.client.gui.GuiDimensionalStabilizer(player.inventory,
                  (mekanism.ultimate.common.tile.TileEntityDimensionalStabilizer) tile);
        }
        return null;
    }

    @Override
    public void updateIndustrialAlarmSound(BlockPos pos, boolean active) {
        if (active) {
            SoundHandler.startTileSound(UltimateSounds.INDUSTRIAL_ALARM_LOCATION, 1F, pos);
        } else {
            SoundHandler.stopTileSound(pos);
        }
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
