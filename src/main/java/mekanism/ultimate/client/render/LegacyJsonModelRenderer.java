package mekanism.ultimate.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import mekanism.common.Mekanism;
import mekanism.client.render.MekanismRenderer;
import mekanism.ultimate.client.render.LegacyJsonModel.Direction;
import mekanism.ultimate.client.render.LegacyJsonModel.Element;
import mekanism.ultimate.client.render.LegacyJsonModel.Face;
import mekanism.ultimate.client.render.LegacyJsonModel.Rotation;
import mekanism.ultimate.common.block.ILegacyJsonModelBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import org.lwjgl.opengl.GL11;

/** Renders the selected modern JSON cuboid models through the 1.7 atlas. */
@SideOnly(Side.CLIENT)
public final class LegacyJsonModelRenderer
      implements ISimpleBlockRenderingHandler
{
    public static final LegacyJsonModelRenderer INSTANCE =
          new LegacyJsonModelRenderer();

    private final int renderId = RenderingRegistry.getNextAvailableRenderId();
    private final Set<String> registeredModels =
          new LinkedHashSet<String>();
    private final Map<String, LegacyJsonModel> models =
          new LinkedHashMap<String, LegacyJsonModel>();

    private LegacyJsonModelRenderer()
    {
    }

    public void registerBlock(Block block)
    {
        if (!(block instanceof ILegacyJsonModelBlock))
        {
            throw new IllegalArgumentException(block
                  + " is not a legacy JSON model block");
        }
        ILegacyJsonModelBlock modelBlock = (ILegacyJsonModelBlock)block;
        modelBlock.setLegacyRenderType(renderId);
        for (String model : modelBlock.getLegacyModels())
        {
            registeredModels.add(model);
        }
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event)
    {
        if (event.map.getTextureType() != 0)
        {
            return;
        }
        models.clear();
        for (String location : registeredModels)
        {
            try
            {
                LegacyJsonModel model = LegacyJsonModel.load(location);
                model.stitch(event.map);
                models.put(location, model);
            }
            catch (IOException exception)
            {
                Mekanism.logger.error("Unable to load legacy block model "
                      + location, exception);
            }
        }
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId,
          RenderBlocks renderer)
    {
        if (!(block instanceof ILegacyJsonModelBlock))
        {
            MekanismRenderer.renderItem(renderer, metadata, block);
            return;
        }
        ILegacyJsonModelBlock modelBlock = (ILegacyJsonModelBlock)block;
        LegacyJsonModel model = models.get(modelBlock.getLegacyModel(metadata));
        if (model == null)
        {
            MekanismRenderer.renderItem(renderer, metadata, block);
            return;
        }

        double[] bounds = model.getBounds();
        double sizeX = bounds[3] - bounds[0];
        double sizeY = bounds[4] - bounds[1];
        double sizeZ = bounds[5] - bounds[2];
        double scale = 1D / Math.max(1D, Math.max(sizeX,
              Math.max(sizeY, sizeZ)));

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT
              | GL11.GL_COLOR_BUFFER_BIT);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        if (block.getRenderBlockPass() > 0)
        {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
        GL11.glRotatef(180F, 0F, 1F, 0F);
        GL11.glScaled(scale, scale, scale);
        GL11.glTranslated(-(bounds[0] + bounds[3]) / 2D,
              -(bounds[1] + bounds[4]) / 2D,
              -(bounds[2] + bounds[5]) / 2D);
        Minecraft.getMinecraft().renderEngine.bindTexture(
              TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        draw(model, tessellator, null, 0, 0, 0, 0, 0,
              0xF000F0, 1F, 1F, 1F, true);
        tessellator.draw();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
          Block block, int modelId, RenderBlocks renderer)
    {
        if (!(block instanceof ILegacyJsonModelBlock))
        {
            return renderer.renderStandardBlock(block, x, y, z);
        }
        ILegacyJsonModelBlock modelBlock = (ILegacyJsonModelBlock)block;
        int metadata = world.getBlockMetadata(x, y, z);
        LegacyJsonModel model = models.get(modelBlock.getLegacyModel(world,
              x, y, z, metadata));
        if (model == null)
        {
            return renderer.renderStandardBlock(block, x, y, z);
        }

        int color = block.colorMultiplier(world, x, y, z);
        float red = (color >> 16 & 0xFF) / 255F;
        float green = (color >> 8 & 0xFF) / 255F;
        float blue = (color & 0xFF) / 255F;
        draw(model, Tessellator.instance, world, x, y, z,
              modelBlock.getLegacyModelRotationX(world, x, y, z, metadata),
              modelBlock.getLegacyModelRotationY(world, x, y, z, metadata),
              block.getMixedBrightnessForBlock(world, x, y, z),
              red, green, blue, false);
        return true;
    }

    private static void draw(LegacyJsonModel model,
          Tessellator tessellator, IBlockAccess world, double offsetX,
          double offsetY, double offsetZ, int rotationX, int rotationY,
          int brightness,
          float red, float green, float blue, boolean inventory)
    {
        for (Element element : model.getElements())
        {
            for (Map.Entry<Direction, Face> entry :
                  element.faces.entrySet())
            {
                Face face = entry.getValue();
                IIcon icon = model.getIcon(face);
                if (icon == null)
                {
                    continue;
                }
                double[][] vertices = vertices(element, entry.getKey());
                for (double[] vertex : vertices)
                {
                    rotateElement(vertex, element.rotation);
                    rotateModel(vertex, rotationX, rotationY);
                }
                double[] normal = normal(vertices);
                float shade = element.shade ? shade(normal) : 1F;
                if (inventory)
                {
                    tessellator.setNormal((float)normal[0],
                          (float)normal[1], (float)normal[2]);
                }
                else
                {
                    tessellator.setBrightness(faceBrightness(world,
                          offsetX, offsetY, offsetZ, vertices, normal,
                          brightness));
                }
                tessellator.setColorOpaque_F(red * shade,
                      green * shade, blue * shade);
                for (int vertex = 0; vertex < 4; vertex++)
                {
                    int uvVertex = (vertex + face.rotation / 90) & 3;
                    double u = uvVertex == 0 || uvVertex == 1
                          ? face.uv[0] : face.uv[2];
                    double v = uvVertex == 0 || uvVertex == 3
                          ? face.uv[1] : face.uv[3];
                    tessellator.addVertexWithUV(
                          offsetX + vertices[vertex][0],
                          offsetY + vertices[vertex][1],
                          offsetZ + vertices[vertex][2],
                          icon.getInterpolatedU(u),
                          icon.getInterpolatedV(v));
                }
            }
        }
    }

    private static int faceBrightness(IBlockAccess world, double offsetX,
          double offsetY, double offsetZ, double[][] vertices,
          double[] normal, int fallback)
    {
        if (world == null)
        {
            return fallback;
        }
        double centreX = 0D;
        double centreY = 0D;
        double centreZ = 0D;
        for (double[] vertex : vertices)
        {
            centreX += vertex[0];
            centreY += vertex[1];
            centreZ += vertex[2];
        }
        int sampleX = MathHelper.floor_double(offsetX + centreX / 4D
              + normal[0] * 0.01D);
        int sampleY = MathHelper.floor_double(offsetY + centreY / 4D
              + normal[1] * 0.01D);
        int sampleZ = MathHelper.floor_double(offsetZ + centreZ / 4D
              + normal[2] * 0.01D);
        int blockX = MathHelper.floor_double(offsetX + 0.5D);
        int blockY = MathHelper.floor_double(offsetY + 0.5D);
        int blockZ = MathHelper.floor_double(offsetZ + 0.5D);
        if (sampleX == blockX && sampleY == blockY && sampleZ == blockZ)
        {
            /* A face inside a non-full JSON model still needs light from the
             * outside of its owning block. Sampling its geometric centre
             * reads the block itself, making internal rods and components
             * black. Use the exterior neighbour along the face normal, as
             * the baked-model lighting path does for a block face. */
            double absX = Math.abs(normal[0]);
            double absY = Math.abs(normal[1]);
            double absZ = Math.abs(normal[2]);
            if (absX >= absY && absX >= absZ)
            {
                sampleX += normal[0] < 0D ? -1 : 1;
            }
            else if (absY >= absZ)
            {
                sampleY += normal[1] < 0D ? -1 : 1;
            }
            else
            {
                sampleZ += normal[2] < 0D ? -1 : 1;
            }
        }
        int sampled = world.getBlock(sampleX, sampleY, sampleZ)
              .getMixedBrightnessForBlock(world, sampleX, sampleY, sampleZ);
        return combineBrightness(fallback, sampled);
    }

    private static int combineBrightness(int first, int second)
    {
        int low = Math.max(first & 0xFFFF, second & 0xFFFF);
        int high = Math.max(first >>> 16 & 0xFFFF,
              second >>> 16 & 0xFFFF);
        return low | high << 16;
    }

    private static double[][] vertices(Element element, Direction direction)
    {
        double west = element.from[0];
        double down = element.from[1];
        double north = element.from[2];
        double east = element.to[0];
        double up = element.to[1];
        double south = element.to[2];
        switch (direction)
        {
            case DOWN:
                return quad(west, down, south, west, down, north,
                      east, down, north, east, down, south);
            case UP:
                return quad(west, up, north, west, up, south,
                      east, up, south, east, up, north);
            case NORTH:
                return quad(east, up, north, east, down, north,
                      west, down, north, west, up, north);
            case SOUTH:
                return quad(west, up, south, west, down, south,
                      east, down, south, east, up, south);
            case WEST:
                return quad(west, up, north, west, down, north,
                      west, down, south, west, up, south);
            case EAST:
                return quad(east, up, south, east, down, south,
                      east, down, north, east, up, north);
            default:
                throw new IllegalArgumentException(direction.name());
        }
    }

    private static double[][] quad(double... values)
    {
        double[][] result = new double[4][3];
        for (int vertex = 0; vertex < 4; vertex++)
        {
            System.arraycopy(values, vertex * 3, result[vertex], 0, 3);
        }
        return result;
    }

    private static void rotateElement(double[] vertex, Rotation rotation)
    {
        if (rotation == null)
        {
            return;
        }
        rotate(vertex, rotation.origin, rotation.axis, rotation.angle);
        if (rotation.rescale)
        {
            double scale = 1D / Math.cos(Math.toRadians(
                  Math.abs(rotation.angle) == 22.5D ? 22.5D : 45D));
            for (int axis = 0; axis < 3; axis++)
            {
                if ("xyz".charAt(axis) != rotation.axis)
                {
                    vertex[axis] = rotation.origin[axis]
                          + (vertex[axis] - rotation.origin[axis]) * scale;
                }
            }
        }
    }

    private static void rotateModel(double[] vertex, int rotationX,
          int rotationY)
    {
        double[] centre = {0.5D, 0.5D, 0.5D};
        if (rotationX != 0)
        {
            /* 1.12 ModelRotation builds its JSON x/y matrices with the
             * negative angle. Keep the legacy renderer source-faithful. */
            rotate(vertex, centre, 'x', -rotationX);
        }
        if (rotationY != 0)
        {
            rotate(vertex, centre, 'y', -rotationY);
        }
    }

    private static void rotate(double[] vertex, double[] origin, char axis,
          double angle)
    {
        double radians = Math.toRadians(angle);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);
        double x = vertex[0] - origin[0];
        double y = vertex[1] - origin[1];
        double z = vertex[2] - origin[2];
        if (axis == 'x')
        {
            vertex[1] = origin[1] + y * cos - z * sin;
            vertex[2] = origin[2] + y * sin + z * cos;
        }
        else if (axis == 'y')
        {
            vertex[0] = origin[0] + x * cos + z * sin;
            vertex[2] = origin[2] - x * sin + z * cos;
        }
        else if (axis == 'z')
        {
            vertex[0] = origin[0] + x * cos - y * sin;
            vertex[1] = origin[1] + x * sin + y * cos;
        }
    }

    private static double[] normal(double[][] vertices)
    {
        double ax = vertices[1][0] - vertices[0][0];
        double ay = vertices[1][1] - vertices[0][1];
        double az = vertices[1][2] - vertices[0][2];
        double bx = vertices[2][0] - vertices[0][0];
        double by = vertices[2][1] - vertices[0][1];
        double bz = vertices[2][2] - vertices[0][2];
        double[] normal = {ay * bz - az * by,
              az * bx - ax * bz, ax * by - ay * bx};
        double length = Math.sqrt(normal[0] * normal[0]
              + normal[1] * normal[1] + normal[2] * normal[2]);
        if (length > 0D)
        {
            normal[0] /= length;
            normal[1] /= length;
            normal[2] /= length;
        }
        return normal;
    }

    private static float shade(double[] normal)
    {
        if (normal[1] > 0.5D)
        {
            return 1F;
        }
        if (normal[1] < -0.5D)
        {
            return 0.5F;
        }
        return Math.abs(normal[0]) > Math.abs(normal[2]) ? 0.6F : 0.8F;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId()
    {
        return renderId;
    }
}
