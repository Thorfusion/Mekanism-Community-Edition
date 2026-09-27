package mekanism.ultimate.client.model;

import mekanism.client.model.ModelFreeRunners;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import net.minecraft.client.model.ModelRenderer;

/** Legacy-model adaptation of the pinned stable Armored Free Runners geometry. */
public final class ModelArmoredFreeRunners extends ModelFreeRunners {

    private final ModelRenderer plateLeft;
    private final ModelRenderer plateRight;
    private final ModelRenderer topPlateLeft;
    private final ModelRenderer topPlateRight;
    private final ModelRenderer connectionLeft;
    private final ModelRenderer connectionRight;
    private final ModelRenderer armoredBraceLeft;
    private final ModelRenderer armoredBraceRight;
    private final ModelRenderer batteryLeft;
    private final ModelRenderer batteryRight;

    public ModelArmoredFreeRunners() {
        textureWidth = 64;
        textureHeight = 32;

        plateLeft = part(0, 11, true);
        plateLeft.addBox(0.5F, 21F, -3F, 3, 2, 1);
        plateLeft.setTextureOffset(0, 7).addBox(0.5F, 17F, -3F, 3, 1, 1);
        plateRight = part(0, 11, false);
        plateRight.addBox(-3.5F, 21F, -3F, 3, 2, 1);
        plateRight.setTextureOffset(0, 7).addBox(-3.5F, 17F, -3F, 3, 1, 1);

        topPlateLeft = part(12, 7, true);
        topPlateLeft.addBox(0F, 0F, -0.25F, 2, 2, 1);
        topPlateLeft.setRotationPoint(1F, 16F, -2F);
        topPlateLeft.rotateAngleX = -0.7854F;
        topPlateRight = part(12, 7, false);
        topPlateRight.addBox(-2F, 0F, -0.25F, 2, 2, 1);
        topPlateRight.setRotationPoint(-1F, 16F, -2F);
        topPlateRight.rotateAngleX = -0.7854F;

        connectionLeft = part(8, 7, true);
        connectionLeft.addBox(2.5F, 18F, -3F, 1, 3, 1);
        connectionLeft.addBox(0.5F, 18F, -3F, 1, 3, 1);
        connectionRight = part(8, 7, false);
        connectionRight.addBox(-1.5F, 18F, -3F, 1, 3, 1);
        connectionRight.addBox(-3.5F, 18F, -3F, 1, 3, 1);

        armoredBraceLeft = part(10, 12, false);
        armoredBraceLeft.addBox(0.2F, 17F, -2.3F, 4, 1, 1);
        armoredBraceLeft.setTextureOffset(8, 10).addBox(0.2F, 21F, -2.3F, 4, 1, 3);
        armoredBraceRight = part(10, 12, true);
        armoredBraceRight.addBox(-4.2F, 17F, -2.3F, 4, 1, 1);
        armoredBraceRight.setTextureOffset(8, 10).addBox(-4.2F, 21F, -2.3F, 4, 1, 3);

        batteryLeft = part(22, 11, false);
        batteryLeft.addBox(1.5F, 18F, -3F, 1, 2, 1);
        batteryRight = part(22, 11, false);
        batteryRight.addBox(-2.5F, 18F, -3F, 1, 2, 1);
    }

    @Override
    public void render(float scale) {
        super.render(scale);
        renderLeftAdditions(scale);
        renderRightAdditions(scale);
    }

    @Override
    public void renderLeft(float scale) {
        super.renderLeft(scale);
        renderLeftAdditions(scale);
    }

    @Override
    public void renderRight(float scale) {
        super.renderRight(scale);
        renderRightAdditions(scale);
    }

    private void renderLeftAdditions(float scale) {
        plateLeft.render(scale);
        topPlateLeft.render(scale);
        connectionLeft.render(scale);
        armoredBraceLeft.render(scale);
        renderBattery(batteryLeft, scale);
    }

    private void renderRightAdditions(float scale) {
        plateRight.render(scale);
        topPlateRight.render(scale);
        connectionRight.render(scale);
        armoredBraceRight.render(scale);
        renderBattery(batteryRight, scale);
    }

    private static void renderBattery(ModelRenderer battery, float scale) {
        GlowInfo glowInfo = MekanismRenderer.enableGlow();
        battery.render(scale);
        MekanismRenderer.disableGlow(glowInfo);
    }

    private ModelRenderer part(int textureX, int textureY, boolean mirror) {
        ModelRenderer part = new ModelRenderer(this, textureX, textureY);
        part.setTextureSize(64, 32);
        part.mirror = mirror;
        return part;
    }
}
