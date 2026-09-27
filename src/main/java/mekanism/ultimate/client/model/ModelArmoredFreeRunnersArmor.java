package mekanism.ultimate.client.model;

import javax.annotation.Nonnull;
import mekanism.ultimate.common.MekanismUltimate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/** Worn armor adapter for the stable Armored Free Runners model. */
public final class ModelArmoredFreeRunnersArmor extends ModelBiped {

    public static final ModelArmoredFreeRunnersArmor INSTANCE = new ModelArmoredFreeRunnersArmor();
    public static final ResourceLocation TEXTURE = new ResourceLocation(
          MekanismUltimate.MODID, "textures/entity/armor/armored_free_runners.png");

    private final ModelArmoredFreeRunners runners = new ModelArmoredFreeRunners();

    private ModelArmoredFreeRunnersArmor() {
        clear(bipedHead);
        clear(bipedHeadwear);
        clear(bipedBody);
        clear(bipedRightArm);
        clear(bipedLeftArm);
        attach(bipedLeftLeg, true);
        attach(bipedRightLeg, false);
    }

    @Override
    public void render(@Nonnull Entity entity, float limbSwing, float limbSwingAmount,
          float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        isSneak = entity.isSneaking();
        isRiding = entity.isRiding();
        isChild = entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isChild();
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    private void attach(ModelRenderer leg, boolean left) {
        clear(leg);
        leg.showModel = true;
        leg.isHidden = false;
        leg.addChild(new RunnerPart(left));
    }

    private static void clear(ModelRenderer part) {
        part.cubeList.clear();
        part.showModel = false;
        part.isHidden = true;
    }

    private final class RunnerPart extends ModelRenderer {

        private final boolean left;

        private RunnerPart(boolean left) {
            super(ModelArmoredFreeRunnersArmor.this);
            this.left = left;
        }

        @Override
        public void render(float scale) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0F, 0F, 0.06F);
            GlStateManager.scale(1.02F, 1.02F, 1.02F);
            GlStateManager.translate(left ? -0.1375F : 0.1375F, -0.75F, -0.0625F);
            Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
            if (left) {
                runners.renderLeft(scale);
            } else {
                runners.renderRight(scale);
            }
            GlStateManager.popMatrix();
        }
    }
}
