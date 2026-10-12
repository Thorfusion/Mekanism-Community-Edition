package mekanism.ultimate.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Worn armor adapter for the stable Armored Free Runners model. */
@SideOnly(Side.CLIENT)
public final class ModelArmoredFreeRunnersArmor extends ModelBiped
{
    public static final ModelArmoredFreeRunnersArmor INSTANCE =
          new ModelArmoredFreeRunnersArmor();
    public static final ResourceLocation TEXTURE = new ResourceLocation(
          "mekanismultimate",
          "textures/entity/armor/armored_free_runners.png");

    private final ModelArmoredFreeRunners runners =
          new ModelArmoredFreeRunners();

    private ModelArmoredFreeRunnersArmor()
    {
        clear(bipedHead);
        clear(bipedHeadwear);
        clear(bipedBody);
        clear(bipedRightArm);
        clear(bipedLeftArm);
        attach(bipedLeftLeg, true);
        attach(bipedRightLeg, false);
    }

    @Override
    public void render(Entity entity, float limbSwing,
          float limbSwingAmount, float ageInTicks, float netHeadYaw,
          float headPitch, float scale)
    {
        if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase living = (EntityLivingBase)entity;
            isSneak = living.isSneaking();
            isRiding = living.isRiding();
            isChild = living.isChild();
        }
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks,
              netHeadYaw, headPitch, scale, entity);
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks,
              netHeadYaw, headPitch, scale);
    }

    private void attach(ModelRenderer leg, boolean left)
    {
        leg.cubeList.clear();
        leg.showModel = true;
        leg.isHidden = false;
        leg.addChild(new RunnerPart(left));
    }

    private static void clear(ModelRenderer part)
    {
        part.cubeList.clear();
        part.showModel = false;
        part.isHidden = true;
    }

    private final class RunnerPart extends ModelRenderer
    {
        private final boolean left;

        private RunnerPart(boolean left)
        {
            super(ModelArmoredFreeRunnersArmor.this);
            this.left = left;
        }

        @Override
        public void render(float scale)
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(0F, 0F, 0.06F);
            GL11.glScalef(1.02F, 1.02F, 1.02F);
            GL11.glTranslatef(left ? -0.1375F : 0.1375F,
                  -0.75F, -0.0625F);
            Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
            if (left)
            {
                runners.renderLeft(scale);
            }
            else
            {
                runners.renderRight(scale);
            }
            GL11.glPopMatrix();
        }
    }
}
