package mekanism.mekasuit.client;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.vecmath.Vector3f;
import mekanism.common.Mekanism;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.content.gear.MekaSuitMobilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.OBJState;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/** Segmented stable MekaSuit OBJ attached to the animated 1.12 biped limbs. */
@SideOnly(Side.CLIENT)
public final class ModelMekaSuitArmor extends ModelBiped {

    private static final ResourceLocation MODEL = new ResourceLocation(
          MekanismMekaSuit.MODID, "models/entity/mekasuit.obj");
    private static final Map<EntityEquipmentSlot, ModelMekaSuitArmor> MODELS =
          new EnumMap<>(EntityEquipmentSlot.class);
    private static OBJModel sourceModel;

    private final EntityEquipmentSlot slot;
    private ItemStack stack = ItemStack.EMPTY;
    private boolean adjacentWorn;

    private ModelMekaSuitArmor(EntityEquipmentSlot slot) {
        super(0F);
        this.slot = slot;
        clearVanillaGeometry();
        if (slot == EntityEquipmentSlot.HEAD) {
            bipedHead.addChild(new ObjPart(this, Part.HEAD));
        } else if (slot == EntityEquipmentSlot.CHEST) {
            bipedBody.addChild(new ObjPart(this, Part.BODY));
            bipedLeftArm.addChild(new ObjPart(this, Part.LEFT_ARM));
            bipedRightArm.addChild(new ObjPart(this, Part.RIGHT_ARM));
        } else if (slot == EntityEquipmentSlot.LEGS || slot == EntityEquipmentSlot.FEET) {
            bipedLeftLeg.addChild(new ObjPart(this, Part.LEFT_LEG));
            bipedRightLeg.addChild(new ObjPart(this, Part.RIGHT_LEG));
        }
    }

    public static ModelMekaSuitArmor get(EntityEquipmentSlot slot, ItemStack stack, EntityLivingBase wearer) {
        ModelMekaSuitArmor model = MODELS.computeIfAbsent(slot, ModelMekaSuitArmor::new);
        model.stack = stack;
        model.adjacentWorn = wearer != null && isMekaSuit(wearer.getItemStackFromSlot(adjacentSlot(slot)));
        return model;
    }

    static void warmUp() {
        sourceModel();
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD,
              EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET}) {
            MODELS.computeIfAbsent(slot, ModelMekaSuitArmor::new).warmUpParts();
        }
    }

    private void warmUpParts() {
        for (ModelRenderer renderer : new ModelRenderer[]{bipedHead, bipedBody, bipedLeftArm,
              bipedRightArm, bipedLeftLeg, bipedRightLeg}) {
            if (renderer.childModels != null) {
                for (ModelRenderer child : renderer.childModels) {
                    if (child instanceof ObjPart) {
                        ((ObjPart) child).warmUp();
                    }
                }
            }
        }
    }

    private void clearVanillaGeometry() {
        bipedHead.cubeList.clear();
        bipedHeadwear.cubeList.clear();
        bipedBody.cubeList.clear();
        bipedLeftArm.cubeList.clear();
        bipedRightArm.cubeList.clear();
        bipedLeftLeg.cubeList.clear();
        bipedRightLeg.cubeList.clear();
    }

    private static synchronized OBJModel sourceModel() {
        if (sourceModel == null) {
            try {
                sourceModel = (OBJModel) OBJLoader.INSTANCE.loadModel(MODEL);
                sourceModel = (OBJModel) sourceModel.process(ImmutableMap.of("flip-v", "true"));
            } catch (Exception e) {
                Mekanism.logger.error("Unable to load segmented MekaSuit armor model", e);
            }
        }
        return sourceModel;
    }

    private static EntityEquipmentSlot adjacentSlot(EntityEquipmentSlot slot) {
        switch (slot) {
            case HEAD: return EntityEquipmentSlot.CHEST;
            case CHEST: return EntityEquipmentSlot.HEAD;
            case LEGS: return EntityEquipmentSlot.FEET;
            case FEET: return EntityEquipmentSlot.LEGS;
            default: return slot;
        }
    }

    private static boolean isMekaSuit(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof mekanism.mekasuit.common.item.ItemMekaSuitArmor;
    }

    private enum Part {
        HEAD(0, 0, 0),
        BODY(0, 0, 0),
        LEFT_ARM(-0.3125F, -0.125F, 0),
        RIGHT_ARM(0.3125F, -0.125F, 0),
        LEFT_LEG(-0.125F, -0.75F, 0),
        RIGHT_LEG(0.125F, -0.75F, 0);

        private final float x;
        private final float y;
        private final float z;

        Part(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private boolean matches(String group) {
            switch (this) {
                case HEAD: return group.contains("_head");
                case BODY: return group.contains("_body") && !group.contains("_arm");
                case LEFT_ARM: return group.contains("left_arm");
                case RIGHT_ARM: return group.contains("right_arm");
                case LEFT_LEG: return group.contains("left_leg");
                case RIGHT_LEG: return group.contains("right_leg");
                default: return false;
            }
        }
    }

    private final class ObjPart extends ModelRenderer {

        private final Part part;
        private final IBakedModel[] baked = new IBakedModel[2];

        private ObjPart(ModelBiped owner, Part part) {
            super(owner, 0, 0);
            this.part = part;
        }

        @Override
        public void render(float scale) {
            int variant = adjacentWorn ? 1 : 0;
            if (baked[variant] == null) {
                baked[variant] = bake(part, adjacentWorn);
            }
            if (baked[variant] == null) {
                return;
            }
            Minecraft minecraft = Minecraft.getMinecraft();
            minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.pushMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
            int color = 0xFF000000 | MekaSuitMobilityHelper.getColor(stack);
            for (EnumFacing side : EnumFacing.VALUES) {
                renderQuads(buffer, baked[variant].getQuads(null, side, 0), color);
            }
            renderQuads(buffer, baked[variant].getQuads(null, null, 0), color);
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        private void warmUp() {
            if (baked[0] == null) {
                baked[0] = bake(part, false);
            }
            if (baked[1] == null) {
                baked[1] = bake(part, true);
            }
        }

        private IBakedModel bake(Part part, boolean hasAdjacent) {
            OBJModel model = sourceModel();
            if (model == null) {
                return null;
            }
            List<String> groups = new ArrayList<>();
            for (String group : model.getMatLib().getGroups().keySet()) {
                if (belongsToSlot(group, hasAdjacent) && part.matches(group)) {
                    groups.add(group);
                }
            }
            if (groups.isEmpty()) {
                return null;
            }
            TRSRTransformation base = new TRSRTransformation(new Vector3f(-1F, 0.5F, 0F),
                  TRSRTransformation.quatFromXYZDegrees(new Vector3f(0F, 0F, 180F)),
                  new Vector3f(1F, 1F, 1F), null);
            TRSRTransformation offset = new TRSRTransformation(new Vector3f(part.x, part.y, part.z),
                  null, null, null);
            Function<ResourceLocation, TextureAtlasSprite> textures = location ->
                  Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
            return model.bake(new OBJState(groups, true, offset.compose(base)),
                  DefaultVertexFormats.ITEM, textures);
        }

        private boolean belongsToSlot(String group, boolean hasAdjacent) {
            boolean matchesSlot;
            switch (slot) {
                case HEAD:
                    matchesSlot = group.contains("helmet");
                    break;
                case CHEST:
                    matchesSlot = group.contains("chest");
                    break;
                case LEGS:
                    matchesSlot = group.contains("leggings");
                    break;
                case FEET:
                    matchesSlot = group.contains("boots");
                    break;
                default:
                    return false;
            }
            if (!matchesSlot || !hasAdjacent) {
                return matchesSlot;
            }
            if (group.startsWith("excl_")) {
                return false;
            }
            return !group.startsWith("shared_") || adjacentSlot(slot).ordinal() <= slot.ordinal();
        }

        private void renderQuads(BufferBuilder buffer, List<BakedQuad> quads, int color) {
            for (BakedQuad quad : quads) {
                LightUtil.renderQuadColor(buffer, quad, color);
            }
        }
    }
}
