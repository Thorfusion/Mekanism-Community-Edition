package mekanism.mekasuit.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import mekanism.mekasuit.client.MekaSuitModelParts.Layer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

/** Stable right-handed Meka-Tool mesh adapted to legacy item perspectives. */
@SideOnly(Side.CLIENT)
public final class MekaToolItemRenderer implements IItemRenderer
{
    private static final WavefrontObject MODEL = (WavefrontObject)
          AdvancedModelLoader.loadModel(new ResourceLocation(
                "mekanismmekasuit", "models/block/mekatool_right.obj"));
    private static final String[][] GROUPS = groups();
    private static final ResourceLocation[] TEXTURES = textures();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type,
          ItemStack item, ItemRendererHelper helper)
    {
        /* The modern GUI transform replaces, rather than stacks with, the
         * legacy inventory-block isometric transform. */
        return helper != ItemRendererHelper.INVENTORY_BLOCK;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item,
          Object... data)
    {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT
              | GL11.GL_COLOR_BUFFER_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_NORMALIZE);
        if (type == ItemRenderType.INVENTORY)
        {
            RenderHelper.enableGUIStandardItemLighting();
        }
        applyTransform(type);
        for (Layer layer : new Layer[] {Layer.BODY, Layer.EXOSKELETON,
              Layer.LED, Layer.TOOL})
        {
            String[] names = GROUPS[layer.ordinal()];
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

    private static void applyTransform(ItemRenderType type)
    {
        if (type == ItemRenderType.INVENTORY)
        {
            /* Match RenderItem.setupGuiTransform from the 1.12 renderer. */
            GL11.glTranslatef(8F, 8F, 0F);
            GL11.glScalef(16F, -16F, 16F);
            applyModernPerspective(0.40625F, -0.21875F, 0F,
                  90F, 40F, 90F);
        }
        else if (type == ItemRenderType.EQUIPPED)
        {
            cancelLegacyThirdPersonTransform();

            /* LayerHeldItem's right-hand transform in 1.12. */
            GL11.glRotatef(-90F, 1F, 0F, 0F);
            GL11.glRotatef(180F, 0F, 1F, 0F);
            GL11.glTranslatef(0.0625F, 0.125F, -0.625F);
            applyModernPerspective(0.0625F, -0.625F, -0.25F,
                  90F, 0F, 0F);
        }
        else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            /* Undo Forge's block helper and the legacy 0.4 item scale. The
             * remaining equip/swing transforms are shared with 1.12. */
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            GL11.glScalef(2.5F, 2.5F, 2.5F);
            GL11.glRotatef(-45F, 0F, 1F, 0F);
            applyModernPerspective(0F, -0.1875F, 0F,
                  90F, 0F, -10F);
        }
        else if (type == ItemRenderType.ENTITY)
        {
            /* Forge 1.7 applies a 0.5 BLOCK_3D scale to non-block items. */
            GL11.glScalef(2F, 2F, 2F);
            applyModernPerspective(0.0625F, 0.125F, 0.4375F,
                  0F, 0F, 0F);
        }
    }

    private static void cancelLegacyThirdPersonTransform()
    {
        /* Invert RenderPlayer's 1.7 BLOCK_3D transform, leaving the arm's
         * postRender matrix in place for the 1.12 held-item transform. */
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        GL11.glScalef(-8F / 3F, -8F / 3F, 8F / 3F);
        GL11.glRotatef(-45F, 0F, 1F, 0F);
        GL11.glRotatef(-20F, 1F, 0F, 0F);
        GL11.glTranslatef(0F, -0.1875F, 0.3125F);
        GL11.glTranslatef(0.0625F, -0.4375F, -0.0625F);
    }

    private static void applyModernPerspective(float translateX,
          float translateY, float translateZ, float rotateX,
          float rotateY, float rotateZ)
    {
        /* Forge stores these blockstate transforms in corner space, then
         * converts them back before applying the camera transform. The two
         * conversions cancel, so the effective 1.12 order is the JSON
         * translation/rotation followed by RenderItem's half-block shift. */
        GL11.glTranslatef(translateX, translateY, translateZ);
        GL11.glRotatef(rotateX, 1F, 0F, 0F);
        GL11.glRotatef(rotateY, 0F, 1F, 0F);
        GL11.glRotatef(rotateZ, 0F, 0F, 1F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
    }

    @SuppressWarnings("unchecked")
    private static String[][] groups()
    {
        List<String>[] names = new List[Layer.values().length];
        for (int i = 0; i < names.length; i++)
        {
            names[i] = new ArrayList<String>();
        }
        for (GroupObject group : MODEL.groupObjects)
        {
            names[MekaSuitModelParts.toolLayer(group.name).ordinal()]
                  .add(group.name);
        }
        String[][] groups = new String[names.length][];
        for (int i = 0; i < names.length; i++)
        {
            groups[i] = names[i].toArray(new String[names[i].size()]);
        }
        return groups;
    }

    private static ResourceLocation[] textures()
    {
        ResourceLocation[] textures = new ResourceLocation[
              Layer.values().length];
        textures[Layer.BODY.ordinal()] = armor("mekasuit_armor_body");
        textures[Layer.EXOSKELETON.ordinal()] = armor(
              "mekasuit_armor_exoskeleton");
        textures[Layer.LED.ordinal()] = new ResourceLocation(
              "mekanismmekasuit", "textures/blocks/models/leds.png");
        textures[Layer.TOOL.ordinal()] = armor("mekatool");
        return textures;
    }

    private static ResourceLocation armor(String name)
    {
        return new ResourceLocation("mekanismmekasuit",
              "textures/entity/armor/" + name + ".png");
    }
}
