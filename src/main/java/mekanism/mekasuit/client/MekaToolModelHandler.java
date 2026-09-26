package mekanism.mekasuit.client;

import java.util.List;
import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import mekanism.mekasuit.common.MekaSuitItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

/** Selects the stable left-hand Meka-Tool mesh for left-hand perspectives. */
@SideOnly(Side.CLIENT)
public final class MekaToolModelHandler {

    public static final MekaToolModelHandler INSTANCE = new MekaToolModelHandler();
    public static final ModelResourceLocation LEFT_MODEL = new ModelResourceLocation(
          MekaSuitItems.MekaTool.getRegistryName(), "left");

    private MekaToolModelHandler() {
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();
        ModelResourceLocation inventory = new ModelResourceLocation(
              MekaSuitItems.MekaTool.getRegistryName(), "inventory");
        IBakedModel right = registry.getObject(inventory);
        IBakedModel left = registry.getObject(LEFT_MODEL);
        if (right != null && left != null) {
            registry.putObject(inventory, new HandedModel(right, left));
        }
    }

    private static final class HandedModel implements IBakedModel {

        private final IBakedModel right;
        private final IBakedModel left;

        private HandedModel(IBakedModel right, IBakedModel left) {
            this.right = right;
            this.left = left;
        }

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
            return right.getQuads(state, side, rand);
        }

        @Override public boolean isAmbientOcclusion() { return right.isAmbientOcclusion(); }
        @Override public boolean isGui3d() { return right.isGui3d(); }
        @Override public boolean isBuiltInRenderer() { return right.isBuiltInRenderer(); }
        @Nonnull @Override public TextureAtlasSprite getParticleTexture() { return right.getParticleTexture(); }
        @Nonnull @Deprecated @Override public ItemCameraTransforms getItemCameraTransforms() { return right.getItemCameraTransforms(); }
        @Nonnull @Override public ItemOverrideList getOverrides() { return right.getOverrides(); }

        @Nonnull
        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull TransformType type) {
            if (type == TransformType.FIRST_PERSON_LEFT_HAND || type == TransformType.THIRD_PERSON_LEFT_HAND) {
                return left.handlePerspective(type);
            }
            return right.handlePerspective(type);
        }
    }
}
