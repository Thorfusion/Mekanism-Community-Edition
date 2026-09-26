package mekanism.mekasuit.client;

import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.content.gear.MekaSuitElytraHelper;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Renders the stable HDPE wing texture for the reinforced Elytra and enabled MekaSuit unit. */
@SideOnly(Side.CLIENT)
public final class LayerMekaSuitElytra implements LayerRenderer<EntityLivingBase> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
          MekanismMekaSuit.MODID, "textures/entity/hdpe_elytra.png");

    private final RenderLivingBase<?> renderer;
    private final ModelElytra model = new ModelElytra();

    public LayerMekaSuitElytra(RenderLivingBase<?> renderer) {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount,
          float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!MekaSuitElytraHelper.shouldRenderWings(chest)) {
            return;
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        renderer.bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, 0F, 0.125F);
        model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        if (chest.isItemEnchanted()) {
            LayerArmorBase.renderEnchantedGlint(renderer, entity, model, limbSwing, limbSwingAmount,
                  partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
