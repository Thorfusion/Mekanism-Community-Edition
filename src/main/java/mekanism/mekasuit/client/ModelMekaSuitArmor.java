package mekanism.mekasuit.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import mekanism.mekasuit.client.MekaSuitModelParts.Layer;
import mekanism.mekasuit.client.MekaSuitModelParts.Part;
import mekanism.mekasuit.common.content.gear.MekaSuitMobilityHelper;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

/** Segmented stable MekaSuit OBJ attached to the animated 1.7 biped limbs. */
@SideOnly(Side.CLIENT)
public final class ModelMekaSuitArmor extends ModelBiped
{
    private static final float WORN_SCALE = 1.035F;
    private static final WavefrontObject MODEL = (WavefrontObject)
          AdvancedModelLoader.loadModel(new ResourceLocation(
                "mekanismmekasuit", "models/entity/mekasuit.obj"));
    private static final ResourceLocation[] TEXTURES = textures();
    private static final ModelMekaSuitArmor[] MODELS =
          new ModelMekaSuitArmor[4];

    private final int armorType;
    private ItemStack stack;
    private boolean adjacentWorn;

    private ModelMekaSuitArmor(int armorType)
    {
        super(0F);
        this.armorType = armorType;
        clearVanillaGeometry();
        if (armorType == 0)
        {
            bipedHead.addChild(new ObjPart(Part.HEAD));
        }
        else if (armorType == 1)
        {
            bipedBody.addChild(new ObjPart(Part.BODY));
            bipedLeftArm.addChild(new ObjPart(Part.LEFT_ARM));
            bipedRightArm.addChild(new ObjPart(Part.RIGHT_ARM));
        }
        else
        {
            bipedLeftLeg.addChild(new ObjPart(Part.LEFT_LEG));
            bipedRightLeg.addChild(new ObjPart(Part.RIGHT_LEG));
        }
    }

    /** Forces the stable suit mesh through Forge's OBJ parser during startup. */
    public static void preload()
    {
    }

    public static ModelMekaSuitArmor get(int armorType, ItemStack stack,
          EntityLivingBase wearer)
    {
        if (armorType < 0 || armorType >= MODELS.length)
        {
            throw new IllegalArgumentException(
                  "Unsupported MekaSuit armor slot: " + armorType);
        }
        ModelMekaSuitArmor model = MODELS[armorType];
        if (model == null)
        {
            model = new ModelMekaSuitArmor(armorType);
            MODELS[armorType] = model;
        }
        model.stack = stack;
        model.adjacentWorn = wearer != null
              && isMekaSuit(wearer.getEquipmentInSlot(
                    adjacentEquipmentSlot(armorType)));
        return model;
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
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks,
              netHeadYaw, headPitch, scale);
    }

    private void clearVanillaGeometry()
    {
        bipedHead.cubeList.clear();
        bipedHeadwear.cubeList.clear();
        bipedBody.cubeList.clear();
        bipedLeftArm.cubeList.clear();
        bipedRightArm.cubeList.clear();
        bipedLeftLeg.cubeList.clear();
        bipedRightLeg.cubeList.clear();
    }

    private static int adjacentEquipmentSlot(int armorType)
    {
        switch (armorType)
        {
            case 0:
                return 3;
            case 1:
                return 4;
            case 2:
                return 1;
            case 3:
                return 2;
            default:
                return 0;
        }
    }

    private static boolean isMekaSuit(ItemStack stack)
    {
        return stack != null
              && stack.getItem() instanceof ItemMekaSuitArmor;
    }

    private static ResourceLocation[] textures()
    {
        ResourceLocation[] textures = new ResourceLocation[
              Layer.values().length];
        textures[Layer.PLAYER.ordinal()] = texture("mekasuit_player");
        textures[Layer.BODY.ordinal()] = texture("mekasuit_armor_body");
        textures[Layer.HELMET.ordinal()] = texture("mekasuit_armor_helmet");
        textures[Layer.EXOSKELETON.ordinal()] = texture(
              "mekasuit_armor_exoskeleton");
        return textures;
    }

    private static ResourceLocation texture(String name)
    {
        return new ResourceLocation("mekanismmekasuit",
              "textures/entity/armor/" + name + ".png");
    }

    private final class ObjPart extends ModelRenderer
    {
        private final Part part;
        private final String[][][] groups =
              new String[2][Layer.values().length][];

        private ObjPart(Part part)
        {
            super(ModelMekaSuitArmor.this, 0, 0);
            this.part = part;
            cacheGroups(false);
            cacheGroups(true);
        }

        @SuppressWarnings("unchecked")
        private void cacheGroups(boolean adjacent)
        {
            List<String>[] names = new List[Layer.values().length];
            for (int i = 0; i < names.length; i++)
            {
                names[i] = new ArrayList<String>();
            }
            for (GroupObject group : MODEL.groupObjects)
            {
                if (MekaSuitModelParts.belongsToSlot(group.name,
                      armorType, adjacent)
                      && MekaSuitModelParts.matchesPart(group.name, part))
                {
                    names[MekaSuitModelParts.suitLayer(group.name).ordinal()]
                          .add(group.name);
                }
            }
            int variant = adjacent ? 1 : 0;
            for (int i = 0; i < names.length; i++)
            {
                groups[variant][i] = names[i].toArray(
                      new String[names[i].size()]);
            }
        }

        @Override
        public void render(float scale)
        {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT
                  | GL11.GL_COLOR_BUFFER_BIT);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA,
                  GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_NORMALIZE);
            GL11.glScalef(WORN_SCALE, WORN_SCALE, WORN_SCALE);
            GL11.glTranslatef(part.x, part.y, part.z);
            GL11.glTranslatef(0F, 1.5F, 0F);
            GL11.glRotatef(180F, 0F, 0F, 1F);
            int color = MekaSuitMobilityHelper.getColor(stack);
            GL11.glColor4f((color >> 16 & 0xFF) / 255F,
                  (color >> 8 & 0xFF) / 255F,
                  (color & 0xFF) / 255F, 1F);
            String[][] selected = groups[adjacentWorn ? 1 : 0];
            for (Layer layer : new Layer[] {Layer.PLAYER, Layer.BODY,
                  Layer.HELMET, Layer.EXOSKELETON})
            {
                String[] names = selected[layer.ordinal()];
                if (names.length > 0)
                {
                    Minecraft.getMinecraft().renderEngine.bindTexture(
                          TEXTURES[layer.ordinal()]);
                    MODEL.renderOnly(names);
                }
            }
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
