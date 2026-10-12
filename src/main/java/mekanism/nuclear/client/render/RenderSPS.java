package mekanism.nuclear.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import mekanism.client.MekanismClient;
import mekanism.client.model.ModelEnergyCube.ModelEnergyCore;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.nuclear.common.content.sps.SPSPosition;
import mekanism.nuclear.common.content.sps.SPSStatus;
import mekanism.nuclear.common.content.sps.SPSValidator;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/** Stable SPS core, orbit, wall-arc, and coil-beam presentation for 1.7. */
@SideOnly(Side.CLIENT)
public final class RenderSPS extends TileEntitySpecialRenderer
{
    private static final int STRUCTURE_CACHE_TICKS = 20;

    private final ModelEnergyCore core = new ModelEnergyCore();
    private final Map<World, Map<SPSPosition, CachedStructure>> structureCache =
          new WeakHashMap<World, Map<SPSPosition, CachedStructure>>();

    @Override
    public void renderTileEntityAt(TileEntity rawTile, double x, double y,
          double z, float partialTick)
    {
        if (!(rawTile instanceof TileEntitySPSPort))
        {
            return;
        }
        TileEntitySPSPort tile = (TileEntitySPSPort)rawTile;
        SPSStatus status = tile.getStatus();
        if (!status.isFormed() || !status.isController()
              || status.getProcessedThisTick() <= 0
              || status.getMin() == null || status.getMax() == null)
        {
            return;
        }

        SPSValidator.Result structure = getStructure(tile, status.getMin());
        if (structure == null || !structure.isFormed())
        {
            return;
        }

        SPSPosition min = structure.getMin();
        SPSPosition max = structure.getMax();
        double centerX = (min.getX() + max.getX() + 1) / 2D;
        double centerY = (min.getY() + max.getY() + 1) / 2D;
        double centerZ = (min.getZ() + max.getZ() + 1) / 2D;
        double localCenterX = centerX - tile.xCoord;
        double localCenterY = centerY - tile.yCoord;
        double localCenterZ = centerZ - tile.zCoord;
        float activity = energyScale(status.getProcessedThisTick());
        float ticks = MekanismClient.ticksPassed + partialTick;

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        renderElectricEffects(tile, structure, localCenterX, localCenterY,
              localCenterZ, activity, ticks);
        renderCore(localCenterX, localCenterY, localCenterZ, activity, ticks);
        GL11.glPopMatrix();
    }

    private SPSValidator.Result getStructure(TileEntitySPSPort tile,
          SPSPosition min)
    {
        World world = tile.getWorldObj();
        Map<SPSPosition, CachedStructure> worldCache =
              structureCache.get(world);
        if (worldCache == null)
        {
            worldCache = new HashMap<SPSPosition, CachedStructure>();
            structureCache.put(world, worldCache);
        }
        SPSPosition tilePos = new SPSPosition(tile.xCoord, tile.yCoord,
              tile.zCoord);
        long now = world.getTotalWorldTime();
        CachedStructure cached = worldCache.get(tilePos);
        if (cached == null || !cached.min.equals(min) || now < cached.tick
              || now - cached.tick >= STRUCTURE_CACHE_TICKS)
        {
            cached = new CachedStructure(min, now,
                  SPSValidator.validateAtMin(world, min));
            worldCache.put(tilePos, cached);
        }
        return cached.result;
    }

    private void renderElectricEffects(TileEntitySPSPort tile,
          SPSValidator.Result structure, double centerX, double centerY,
          double centerZ, float activity, float ticks)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDepthMask(false);

        long phase = (long)ticks / 4L;
        for (SPSPosition coil : structure.getCoils())
        {
            double startX = coil.getX() + 0.5D - tile.xCoord;
            double startY = coil.getY() + 0.5D - tile.yCoord;
            double startZ = coil.getZ() + 0.5D - tile.zCoord;
            long seed = positionSeed(coil)
                  ^ (phase * 0x9E3779B97F4A7C15L);
            renderBolt(startX, startY, startZ, centerX, centerY, centerZ,
                  seed, 4F + activity * 3F, 150, 62, 255, 70);
            renderBolt(startX, startY, startZ, centerX, centerY, centerZ,
                  seed, 1.25F + activity, 245, 225, 255, 230);
        }

        SPSPosition origin = new SPSPosition(tile.xCoord, tile.yCoord,
              tile.zCoord);
        Random wallRandom = new Random(positionSeed(origin) ^ (phase * 31L));
        double[] wall = randomInnerWall(structure, origin, wallRandom);
        renderBolt(centerX, centerY, centerZ, wall[0], wall[1], wall[2],
              wallRandom.nextLong(), 2F + activity * 2F,
              174, 82, 255, 90);
        renderBolt(centerX, centerY, centerZ, wall[0], wall[1], wall[2],
              wallRandom.nextLong(), 0.8F + activity,
              255, 235, 255, 210);
        renderOrbits(centerX, centerY, centerZ,
              0.8D + 1.5D * activity, ticks);

        GL11.glLineWidth(1F);
        GL11.glDepthMask(true);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    private void renderBolt(double startX, double startY, double startZ,
          double endX, double endY, double endZ, long seed, float width,
          int red, int green, int blue, int alpha)
    {
        Random random = new Random(seed);
        Tessellator tessellator = Tessellator.instance;
        GL11.glLineWidth(width);
        tessellator.startDrawing(GL11.GL_LINE_STRIP);
        tessellator.setColorRGBA(red, green, blue, alpha);
        int segments = 15;
        for (int segment = 0; segment <= segments; segment++)
        {
            double fraction = segment / (double)segments;
            double envelope = Math.sin(Math.PI * fraction) * 0.22D;
            double jitterX = segment == 0 || segment == segments ? 0
                  : (random.nextDouble() - 0.5D) * envelope;
            double jitterY = segment == 0 || segment == segments ? 0
                  : (random.nextDouble() - 0.5D) * envelope;
            double jitterZ = segment == 0 || segment == segments ? 0
                  : (random.nextDouble() - 0.5D) * envelope;
            tessellator.addVertex(
                  startX + (endX - startX) * fraction + jitterX,
                  startY + (endY - startY) * fraction + jitterY,
                  startZ + (endZ - startZ) * fraction + jitterZ);
        }
        tessellator.draw();
    }

    private void renderOrbits(double centerX, double centerY,
          double centerZ, double radius, float ticks)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glLineWidth(1.4F);
        for (int orbit = 0; orbit < 3; orbit++)
        {
            tessellator.startDrawing(GL11.GL_LINE_STRIP);
            tessellator.setColorRGBA(201, 118, 255, 115);
            double phase = Math.toRadians(
                  ticks * (1.5D + orbit * 0.35D) + orbit * 120D);
            for (int point = 0; point <= 48; point++)
            {
                double angle = point * Math.PI * 2D / 48D + phase;
                double cosine = Math.cos(angle) * radius;
                double sine = Math.sin(angle) * radius;
                double pointX;
                double pointY;
                double pointZ;
                if (orbit == 0)
                {
                    pointX = centerX + cosine;
                    pointY = centerY + sine * 0.45D;
                    pointZ = centerZ + sine * 0.75D;
                }
                else if (orbit == 1)
                {
                    pointX = centerX + sine * 0.55D;
                    pointY = centerY + cosine;
                    pointZ = centerZ + sine * 0.65D;
                }
                else
                {
                    pointX = centerX + sine * 0.7D;
                    pointY = centerY + sine * 0.55D;
                    pointZ = centerZ + cosine;
                }
                tessellator.addVertex(pointX, pointY, pointZ);
            }
            tessellator.draw();
        }
    }

    private void renderCore(double centerX, double centerY, double centerZ,
          float activity, float ticks)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(centerX, centerY, centerZ);
        bindTexture(MekanismUtils.getResource(ResourceType.RENDER,
              "EnergyCore.png"));
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        MekanismRenderer.glowOn();

        float pulse = 0.92F + 0.08F
              * (float)Math.sin(Math.toRadians(ticks * 5F));
        float scale = (0.35F + activity * 2.2F) * pulse;
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(ticks * 1.8F, 0, 1, 0);
        GL11.glRotatef(ticks * 1.1F, 1, 0, 1);
        GL11.glColor4f(0.78F, 0.28F, 1F, 0.82F);
        core.render(0.0625F);

        GL11.glScalef(0.62F, 0.62F, 0.62F);
        GL11.glRotatef(-ticks * 3F, 1, 1, 0);
        GL11.glColor4f(1F, 0.92F, 1F, 0.9F);
        core.render(0.0625F);

        MekanismRenderer.resetColor();
        MekanismRenderer.glowOff();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPopMatrix();
    }

    private static double[] randomInnerWall(SPSValidator.Result structure,
          SPSPosition origin, Random random)
    {
        SPSPosition min = structure.getMin();
        SPSPosition max = structure.getMax();
        double minX = min.getX() + 1.05D - origin.getX();
        double minY = min.getY() + 1.05D - origin.getY();
        double minZ = min.getZ() + 1.05D - origin.getZ();
        double maxX = max.getX() - 0.05D - origin.getX();
        double maxY = max.getY() - 0.05D - origin.getY();
        double maxZ = max.getZ() - 0.05D - origin.getZ();
        double x = minX + random.nextDouble() * (maxX - minX);
        double y = minY + random.nextDouble() * (maxY - minY);
        double z = minZ + random.nextDouble() * (maxZ - minZ);
        switch (random.nextInt(6))
        {
            case 0: x = minX; break;
            case 1: x = maxX; break;
            case 2: y = minY; break;
            case 3: y = maxY; break;
            case 4: z = minZ; break;
            default: z = maxZ;
        }
        return new double[]{x, y, z};
    }

    private static long positionSeed(SPSPosition position)
    {
        return ((long)position.getX() & 0x3FFFFFFL) << 38
              | ((long)position.getZ() & 0x3FFFFFFL) << 12
              | ((long)position.getY() & 0xFFFL);
    }

    private static float energyScale(double processed)
    {
        return (float)Math.min(1D,
              Math.max(0D, (Math.log10(processed) + 2D) / 4D));
    }

    private static final class CachedStructure
    {
        private final SPSPosition min;
        private final long tick;
        private final SPSValidator.Result result;

        private CachedStructure(SPSPosition min, long tick,
              SPSValidator.Result result)
        {
            this.min = min;
            this.tick = tick;
            this.result = result;
        }
    }
}
