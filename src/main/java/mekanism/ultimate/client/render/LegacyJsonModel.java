package mekanism.ultimate.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

/** Minimal 1.8+ block-model reader for the modern models backported to 1.7. */
@SideOnly(Side.CLIENT)
final class LegacyJsonModel
{
    private final List<Element> elements;
    private final Map<String, String> textures;
    private final Map<String, IIcon> icons = new HashMap<String, IIcon>();
    private final double[] bounds;

    private LegacyJsonModel(List<Element> elements,
          Map<String, String> textures)
    {
        this.elements = Collections.unmodifiableList(elements);
        this.textures = Collections.unmodifiableMap(textures);
        bounds = calculateBounds(elements);
    }

    static LegacyJsonModel load(String location) throws IOException
    {
        return load(modelLocation(location, null), new HashSet<String>());
    }

    private static LegacyJsonModel load(ResourceLocation location,
          Set<String> loading) throws IOException
    {
        String key = location.toString();
        if (!loading.add(key))
        {
            throw new IOException("Circular block-model parent chain at "
                  + key);
        }

        IResource resource = Minecraft.getMinecraft().getResourceManager()
              .getResource(location);
        JsonObject json;
        Reader reader = new InputStreamReader(resource.getInputStream(),
              StandardCharsets.UTF_8);
        try
        {
            json = new JsonParser().parse(reader).getAsJsonObject();
        }
        finally
        {
            reader.close();
        }

        List<Element> elements = new ArrayList<Element>();
        Map<String, String> textures =
              new LinkedHashMap<String, String>();
        if (json.has("parent"))
        {
            String parentName = json.get("parent").getAsString();
            ResourceLocation parent = modelLocation(parentName,
                  location.getResourceDomain());
            try
            {
                LegacyJsonModel inherited = load(parent, loading);
                elements.addAll(inherited.elements);
                textures.putAll(inherited.textures);
            }
            catch (IOException exception)
            {
                /* Vanilla cube parents add no geometry needed by these models. */
                if (!"minecraft".equals(parent.getResourceDomain()))
                {
                    throw exception;
                }
            }
        }

        if (json.has("textures"))
        {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject(
                  "textures").entrySet())
            {
                textures.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
        if (json.has("elements"))
        {
            elements.clear();
            for (JsonElement element : json.getAsJsonArray("elements"))
            {
                elements.add(parseElement(element.getAsJsonObject()));
            }
        }
        loading.remove(key);
        return new LegacyJsonModel(elements, textures);
    }

    void stitch(TextureMap map)
    {
        icons.clear();
        for (Element element : elements)
        {
            for (Face face : element.faces.values())
            {
                String texture = resolveTexture(face.texture);
                if (!icons.containsKey(texture))
                {
                    icons.put(texture, map.registerIcon(
                          atlasLocation(texture)));
                }
            }
        }
    }

    IIcon getIcon(Face face)
    {
        return icons.get(resolveTexture(face.texture));
    }

    List<Element> getElements()
    {
        return elements;
    }

    double[] getBounds()
    {
        return bounds.clone();
    }

    private String resolveTexture(String value)
    {
        String resolved = value;
        Set<String> visited = new HashSet<String>();
        while (resolved.startsWith("#"))
        {
            String key = resolved.substring(1);
            if (!visited.add(key))
            {
                return "minecraft:missingno";
            }
            String next = textures.get(key);
            if (next == null)
            {
                return "minecraft:missingno";
            }
            resolved = next;
        }
        return resolved;
    }

    private static String atlasLocation(String texture)
    {
        ResourceLocation location = new ResourceLocation(texture);
        String path = location.getResourcePath().replace('\\', '/');
        if (path.startsWith("textures/"))
        {
            path = path.substring("textures/".length());
        }
        if (path.startsWith("blocks/"))
        {
            path = path.substring("blocks/".length());
        }
        else if (path.startsWith("block/"))
        {
            path = path.substring("block/".length());
        }
        if (path.endsWith(".png"))
        {
            path = path.substring(0, path.length() - 4);
        }
        return location.getResourceDomain() + ":" + path;
    }

    private static ResourceLocation modelLocation(String value,
          String defaultDomain)
    {
        String domain = "minecraft";
        String path = value;
        int separator = value.indexOf(':');
        if (separator >= 0)
        {
            domain = value.substring(0, separator);
            path = value.substring(separator + 1);
        }
        path = path.replace('\\', '/');
        if (!path.startsWith("models/"))
        {
            path = "models/" + path;
        }
        if (!path.endsWith(".json"))
        {
            path += ".json";
        }
        return new ResourceLocation(domain, path);
    }

    private static Element parseElement(JsonObject json)
    {
        double[] from = vector(json.getAsJsonArray("from"));
        double[] to = vector(json.getAsJsonArray("to"));
        Rotation rotation = null;
        if (json.has("rotation"))
        {
            JsonObject value = json.getAsJsonObject("rotation");
            rotation = new Rotation(vector(value.getAsJsonArray("origin")),
                  value.get("axis").getAsString().charAt(0),
                  value.get("angle").getAsDouble(),
                  value.has("rescale")
                        && value.get("rescale").getAsBoolean());
        }
        boolean shade = !json.has("shade")
              || json.get("shade").getAsBoolean();
        Map<Direction, Face> faces =
              new LinkedHashMap<Direction, Face>();
        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject(
              "faces").entrySet())
        {
            JsonObject face = entry.getValue().getAsJsonObject();
            faces.put(Direction.valueOf(entry.getKey().toUpperCase()),
                  new Face(vector4(face.getAsJsonArray("uv")),
                        face.get("texture").getAsString(),
                        face.has("rotation")
                              ? face.get("rotation").getAsInt() : 0));
        }
        return new Element(from, to, rotation, shade, faces);
    }

    private static double[] vector(JsonArray values)
    {
        double[] result = new double[3];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = values.get(i).getAsDouble() / 16D;
        }
        return result;
    }

    private static double[] vector4(JsonArray values)
    {
        double[] result = new double[4];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = values.get(i).getAsDouble();
        }
        return result;
    }

    private static double[] calculateBounds(List<Element> elements)
    {
        double[] result = {Double.POSITIVE_INFINITY,
              Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
              Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
              Double.NEGATIVE_INFINITY};
        for (Element element : elements)
        {
            for (int axis = 0; axis < 3; axis++)
            {
                result[axis] = Math.min(result[axis],
                      Math.min(element.from[axis], element.to[axis]));
                result[axis + 3] = Math.max(result[axis + 3],
                      Math.max(element.from[axis], element.to[axis]));
            }
        }
        if (elements.isEmpty())
        {
            return new double[] {0, 0, 0, 1, 1, 1};
        }
        return result;
    }

    static final class Element
    {
        final double[] from;
        final double[] to;
        final Rotation rotation;
        final boolean shade;
        final Map<Direction, Face> faces;

        Element(double[] from, double[] to, Rotation rotation,
              boolean shade, Map<Direction, Face> faces)
        {
            this.from = from;
            this.to = to;
            this.rotation = rotation;
            this.shade = shade;
            this.faces = faces;
        }
    }

    static final class Face
    {
        final double[] uv;
        final String texture;
        final int rotation;

        Face(double[] uv, String texture, int rotation)
        {
            this.uv = uv;
            this.texture = texture;
            this.rotation = rotation;
        }
    }

    static final class Rotation
    {
        final double[] origin;
        final char axis;
        final double angle;
        final boolean rescale;

        Rotation(double[] origin, char axis, double angle, boolean rescale)
        {
            this.origin = origin;
            this.axis = axis;
            this.angle = angle;
            this.rescale = rescale;
        }
    }

    enum Direction
    {
        DOWN, UP, NORTH, SOUTH, WEST, EAST
    }
}
