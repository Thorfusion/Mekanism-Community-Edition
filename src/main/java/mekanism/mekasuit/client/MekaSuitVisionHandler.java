package mekanism.mekasuit.client;

import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.content.gear.MekaSuitVisionHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitNutritionalHelper;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Client vision, fog, and HUD behavior for the Vision Enhancement Unit. */
@SideOnly(Side.CLIENT)
public final class MekaSuitVisionHandler {

    public static final MekaSuitVisionHandler INSTANCE = new MekaSuitVisionHandler();

    private static final int VISION_DURATION = 220;
    private static final ResourceLocation HUD_ICON = new ResourceLocation(
          MekanismMekaSuit.MODID, "gui/hud/vision_enhancement_unit.png");
    private static final ResourceLocation NUTRITION_ICON = new ResourceLocation(
          MekanismMekaSuit.MODID, "gui/hud/nutritional_injection_unit.png");

    private boolean visionApplied;

    private MekaSuitVisionHandler() {
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase != Phase.START) {
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            visionApplied = false;
            return;
        }
        if (isActive(player)) {
            visionApplied = true;
            // Keeping the duration above ten seconds prevents vanilla's night-vision flicker.
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION,
                  VISION_DURATION, 0, false, false));
        } else if (visionApplied) {
            visionApplied = false;
            PotionEffect effect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
            if (effect != null && effect.getDuration() <= VISION_DURATION) {
                // Do not remove a longer effect supplied by a potion or another mod.
                player.removePotionEffect(MobEffects.NIGHT_VISION);
            }
        }
    }

    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        if (!isActivePlayer(event.getEntity())) {
            return;
        }
        event.setRed(0.1F * event.getRed() + 0.9F * 0.4F);
        event.setGreen(0.1F * event.getGreen() + 0.9F * 0.8F);
        event.setBlue(0.1F * event.getBlue() + 0.9F * 0.4F);
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (!isActivePlayer(event.getEntity())) {
            return;
        }
        Material material = event.getState().getMaterial();
        if (material != Material.WATER && material != Material.LAVA) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        float scale = getFogScale(player);
        float density;
        if (material == Material.LAVA) {
            // Stable expands lava visibility before applying its installed-count scale.
            density = 2F / (24F * scale);
        } else if (player.isPotionActive(MobEffects.WATER_BREATHING)) {
            density = 0.01F / scale;
        } else {
            density = Math.max(0.01F, 0.1F - EnchantmentHelper.getRespirationModifier(player) * 0.03F) / scale;
        }
        GlStateManager.setFog(GlStateManager.FogMode.EXP);
        event.setDensity(density);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onFogRender(EntityViewRenderEvent.RenderFogEvent event) {
        if (!isActivePlayer(event.getEntity())) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        float far = event.getFarPlaneDistance();
        if (far < 20F) {
            far = 5F + 2.5F * (float) Math.pow(Math.E, 0.16F * far);
            far = Math.min(192F, far);
        }
        far *= getFogScale(player);
        GlStateManager.setFog(GlStateManager.FogMode.LINEAR);
        GlStateManager.setFogStart(-8F);
        GlStateManager.setFogEnd(far);
    }

    @SubscribeEvent
    public void renderHud(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        if (player == null || minecraft.gameSettings.showDebugInfo) {
            return;
        }
        ModuleData module = MekaSuitVisionHelper.getModule(
              player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        ModuleData nutrition = MekaSuitNutritionalHelper.getModule(helmet);
        if (module == null && (nutrition == null || !nutrition.isEnabled())) {
            return;
        }
        ScaledResolution resolution = new ScaledResolution(minecraft);
        int iconX = resolution.getScaledWidth() - 26;
        int y = resolution.getScaledHeight() - 28;
        if (module != null) {
            boolean enabled = module.isEnabled();
            String status = I18n.format(enabled ? "gui.mekasuit.on" : "gui.mekasuit.off");
            drawHudElement(minecraft, HUD_ICON, iconX, y, status, enabled ? 0x55FFBB : 0x777777,
                  enabled ? 1F : 0.5F);
            y -= 20;
        }
        if (nutrition != null && nutrition.isEnabled()) {
            String percent = Math.round(100 * MekaSuitNutritionalHelper.getRatio(helmet)) + "%";
            drawHudElement(minecraft, NUTRITION_ICON, iconX, y, percent, 0xEB6CA3, 1F);
        }
    }

    private static void drawHudElement(Minecraft minecraft, ResourceLocation icon, int iconX, int y,
          String text, int color, float brightness) {
        minecraft.fontRenderer.drawStringWithShadow(text,
              iconX - minecraft.fontRenderer.getStringWidth(text) - 2, y + 4, color);
        minecraft.getTextureManager().bindTexture(icon);
        GlStateManager.enableBlend();
        GlStateManager.color(brightness, brightness, brightness, 1F);
        Gui.drawModalRectWithCustomSizedTexture(iconX, y, 0, 0, 16, 16, 16, 16);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
    }

    private static boolean isActivePlayer(Entity entity) {
        Minecraft minecraft = Minecraft.getMinecraft();
        return entity instanceof EntityPlayer && entity == minecraft.player && isActive((EntityPlayer) entity);
    }

    private static boolean isActive(EntityPlayer player) {
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        return MekaSuitVisionHelper.isActive(helmet, player);
    }

    private static float getFogScale(EntityPlayer player) {
        ModuleData module = MekaSuitVisionHelper.getModule(
              player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        return MekaSuitVisionHelper.getFogDistanceScale(module == null ? 1 : module.getInstalledCount());
    }
}
