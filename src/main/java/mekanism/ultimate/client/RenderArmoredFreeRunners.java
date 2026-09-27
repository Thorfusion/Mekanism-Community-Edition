package mekanism.ultimate.client;

import javax.annotation.Nonnull;
import mekanism.client.render.item.ItemLayerWrapper;
import mekanism.client.render.item.MekanismItemStackRenderer;
import mekanism.ultimate.client.model.ModelArmoredFreeRunners;
import mekanism.ultimate.client.model.ModelArmoredFreeRunnersArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;

/** Built-in item renderer for the three-dimensional Armored Free Runners model. */
public final class RenderArmoredFreeRunners extends MekanismItemStackRenderer {

    public static ItemLayerWrapper model;

    private final ModelArmoredFreeRunners runners = new ModelArmoredFreeRunners();

    @Override
    protected void renderBlockSpecific(@Nonnull ItemStack stack, TransformType transformType) {
    }

    @Override
    protected void renderItemSpecific(@Nonnull ItemStack stack, TransformType transformType) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180F, 0F, 0F, 1F);
        GlStateManager.rotate(90F, 0F, -1F, 0F);
        GlStateManager.scale(2F, 2F, 2F);
        GlStateManager.translate(0.2F, -1.43F, 0.12F);
        Minecraft.getMinecraft().renderEngine.bindTexture(ModelArmoredFreeRunnersArmor.TEXTURE);
        runners.render(0.0625F);
        GlStateManager.popMatrix();
    }

    @Nonnull
    @Override
    protected TransformType getTransform(@Nonnull ItemStack stack) {
        return model == null ? TransformType.NONE : model.getTransform();
    }
}
