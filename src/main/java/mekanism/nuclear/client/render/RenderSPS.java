package mekanism.nuclear.client.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import mekanism.client.MekanismClient;
import mekanism.client.model.ModelEnergyCube.ModelEnergyCore;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.nuclear.common.content.sps.SPSStatus;
import mekanism.nuclear.common.content.sps.SPSValidator;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/** Stable SPS core, orbit, wall-arc, and coil-beam presentation for 1.12. */
@SideOnly(Side.CLIENT)
public final class RenderSPS extends TileEntitySpecialRenderer<TileEntitySPSPort> {

    private static final int STRUCTURE_CACHE_TICKS = 20;
    private final ModelEnergyCore core = new ModelEnergyCore();
    private final Map<World, Map<BlockPos, CachedStructure>> structureCache = new WeakHashMap<>();

    @Override
    public void render(TileEntitySPSPort tile, double x, double y, double z, float partialTick,
          int destroyStage, float alpha) {
        SPSStatus status = tile.getStatus();
        if (!status.isFormed() || !status.isController() || status.getProcessedThisTick() <= 0
              || status.getMin() == null || status.getMax() == null) {
            return;
        }
        SPSValidator.Result structure = getStructure(tile, status.getMin());
        if (structure == null || !structure.isFormed()) {
            return;
        }

        double centerX = (structure.getMin().getX() + structure.getMax().getX() + 1) / 2D;
        double centerY = (structure.getMin().getY() + structure.getMax().getY() + 1) / 2D;
        double centerZ = (structure.getMin().getZ() + structure.getMax().getZ() + 1) / 2D;
        double localCenterX = centerX - tile.getPos().getX();
        double localCenterY = centerY - tile.getPos().getY();
        double localCenterZ = centerZ - tile.getPos().getZ();
        float activity = energyScale(status.getProcessedThisTick());
        float ticks = MekanismClient.ticksPassed + partialTick;

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        renderElectricEffects(tile, structure, localCenterX, localCenterY, localCenterZ,
              activity, ticks);
        renderCore(localCenterX, localCenterY, localCenterZ, activity, ticks);
        GlStateManager.popMatrix();
    }

    private SPSValidator.Result getStructure(TileEntitySPSPort tile, BlockPos min) {
        Map<BlockPos, CachedStructure> worldCache = structureCache.computeIfAbsent(
              tile.getWorld(), ignored -> new HashMap<>());
        long now = tile.getWorld().getTotalWorldTime();
        CachedStructure cached = worldCache.get(tile.getPos());
        if (cached == null || !cached.min.equals(min) || now < cached.tick
              || now - cached.tick >= STRUCTURE_CACHE_TICKS) {
            cached = new CachedStructure(min, now,
                  SPSValidator.validateAtMin(tile.getWorld(), min));
            worldCache.put(tile.getPos(), cached);
        }
        return cached.result;
    }

    private void renderElectricEffects(TileEntitySPSPort tile, SPSValidator.Result structure,
          double centerX, double centerY, double centerZ, float activity, float ticks) {
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
        GlStateManager.depthMask(false);

        long phase = (long) ticks / 4L;
        for (BlockPos coil : structure.getCoils()) {
            double startX = coil.getX() + 0.5D - tile.getPos().getX();
            double startY = coil.getY() + 0.5D - tile.getPos().getY();
            double startZ = coil.getZ() + 0.5D - tile.getPos().getZ();
            long seed = coil.toLong() ^ (phase * 0x9E3779B97F4A7C15L);
            renderBolt(startX, startY, startZ, centerX, centerY, centerZ, seed,
                  4F + activity * 3F, 150, 62, 255, 70);
            renderBolt(startX, startY, startZ, centerX, centerY, centerZ, seed,
                  1.25F + activity, 245, 225, 255, 230);
        }

        Random wallRandom = new Random(tile.getPos().toLong() ^ (phase * 31L));
        double[] wall = randomInnerWall(structure, tile.getPos(), wallRandom);
        renderBolt(centerX, centerY, centerZ, wall[0], wall[1], wall[2], wallRandom.nextLong(),
              2F + activity * 2F, 174, 82, 255, 90);
        renderBolt(centerX, centerY, centerZ, wall[0], wall[1], wall[2], wallRandom.nextLong(),
              0.8F + activity, 255, 235, 255, 210);
        renderOrbits(centerX, centerY, centerZ, 0.8D + 1.5D * activity, ticks);

        GlStateManager.glLineWidth(1F);
        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
    }

    private void renderBolt(double startX, double startY, double startZ, double endX,
          double endY, double endZ, long seed, float width, int red, int green,
          int blue, int alpha) {
        Random random = new Random(seed);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.glLineWidth(width);
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        int segments = 15;
        for (int segment = 0; segment <= segments; segment++) {
            double fraction = segment / (double) segments;
            double envelope = Math.sin(Math.PI * fraction) * 0.22D;
            double jitterX = segment == 0 || segment == segments ? 0
                  : (random.nextDouble() - 0.5D) * envelope;
            double jitterY = segment == 0 || segment == segments ? 0
                  : (random.nextDouble() - 0.5D) * envelope;
            double jitterZ = segment == 0 || segment == segments ? 0
                  : (random.nextDouble() - 0.5D) * envelope;
            buffer.pos(startX + (endX - startX) * fraction + jitterX,
                  startY + (endY - startY) * fraction + jitterY,
                  startZ + (endZ - startZ) * fraction + jitterZ)
                  .color(red, green, blue, alpha).endVertex();
        }
        tessellator.draw();
    }

    private void renderOrbits(double centerX, double centerY, double centerZ,
          double radius, float ticks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.glLineWidth(1.4F);
        for (int orbit = 0; orbit < 3; orbit++) {
            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            double phase = Math.toRadians(ticks * (1.5D + orbit * 0.35D) + orbit * 120D);
            for (int point = 0; point <= 48; point++) {
                double angle = point * Math.PI * 2D / 48D + phase;
                double cosine = Math.cos(angle) * radius;
                double sine = Math.sin(angle) * radius;
                double x;
                double y;
                double z;
                if (orbit == 0) {
                    x = centerX + cosine;
                    y = centerY + sine * 0.45D;
                    z = centerZ + sine * 0.75D;
                } else if (orbit == 1) {
                    x = centerX + sine * 0.55D;
                    y = centerY + cosine;
                    z = centerZ + sine * 0.65D;
                } else {
                    x = centerX + sine * 0.7D;
                    y = centerY + sine * 0.55D;
                    z = centerZ + cosine;
                }
                buffer.pos(x, y, z).color(201, 118, 255, 115).endVertex();
            }
            tessellator.draw();
        }
    }

    private void renderCore(double centerX, double centerY, double centerZ,
          float activity, float ticks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) centerX, (float) centerY, (float) centerZ);
        bindTexture(MekanismUtils.getResource(ResourceType.RENDER, "EnergyCore.png"));
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
        GlowInfo glow = MekanismRenderer.enableGlow();

        float pulse = 0.92F + 0.08F * (float) Math.sin(Math.toRadians(ticks * 5F));
        float scale = (0.35F + activity * 2.2F) * pulse;
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(ticks * 1.8F, 0, 1, 0);
        GlStateManager.rotate(ticks * 1.1F, 1, 0, 1);
        GlStateManager.color(0.78F, 0.28F, 1F, 0.82F);
        core.render(0.0625F);

        GlStateManager.scale(0.62F, 0.62F, 0.62F);
        GlStateManager.rotate(-ticks * 3F, 1, 1, 0);
        GlStateManager.color(1F, 0.92F, 1F, 0.9F);
        core.render(0.0625F);

        MekanismRenderer.resetColor();
        MekanismRenderer.disableGlow(glow);
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    private static double[] randomInnerWall(SPSValidator.Result structure, BlockPos origin,
          Random random) {
        double minX = structure.getMin().getX() + 1.05D - origin.getX();
        double minY = structure.getMin().getY() + 1.05D - origin.getY();
        double minZ = structure.getMin().getZ() + 1.05D - origin.getZ();
        double maxX = structure.getMax().getX() - 0.05D - origin.getX();
        double maxY = structure.getMax().getY() - 0.05D - origin.getY();
        double maxZ = structure.getMax().getZ() - 0.05D - origin.getZ();
        double x = minX + random.nextDouble() * (maxX - minX);
        double y = minY + random.nextDouble() * (maxY - minY);
        double z = minZ + random.nextDouble() * (maxZ - minZ);
        switch (random.nextInt(6)) {
            case 0: x = minX; break;
            case 1: x = maxX; break;
            case 2: y = minY; break;
            case 3: y = maxY; break;
            case 4: z = minZ; break;
            default: z = maxZ;
        }
        return new double[]{x, y, z};
    }

    private static float energyScale(double processed) {
        return (float) Math.min(1D, Math.max(0D, (Math.log10(processed) + 2D) / 4D));
    }

    private static final class CachedStructure {

        private final BlockPos min;
        private final long tick;
        private final SPSValidator.Result result;

        private CachedStructure(BlockPos min, long tick, SPSValidator.Result result) {
            this.min = min;
            this.tick = tick;
            this.result = result;
        }
    }
}
