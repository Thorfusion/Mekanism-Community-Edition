package mekanism.mekasuit.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.content.gear.MekaSuitGravitationalHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitJetpackHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitNutritionalHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitVisionHelper;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import mekanism.common.util.LangUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

/** Client night vision and fog presentation for Vision Enhancement. */
@SideOnly(Side.CLIENT)
public final class MekaSuitVisionHandler
{
    public static final MekaSuitVisionHandler INSTANCE =
          new MekaSuitVisionHandler();
    private static final int VISION_DURATION = 220;
    private static final ResourceLocation VISION_ICON = icon(
          "vision_enhancement_unit");
    private static final ResourceLocation NUTRITION_ICON = icon(
          "nutritional_injection_unit");
    private static final ResourceLocation GRAVITATIONAL_ICON = icon(
          "gravitational_modulation_unit");

    private boolean visionApplied;

    private MekaSuitVisionHandler()
    {
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase != Phase.START)
        {
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null)
        {
            visionApplied = false;
            return;
        }
        if (isActive(player))
        {
            visionApplied = true;
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id,
                  VISION_DURATION, 0, true));
        }
        else if (visionApplied)
        {
            visionApplied = false;
            PotionEffect effect = player.getActivePotionEffect(
                  Potion.nightVision);
            if (effect != null && effect.getDuration() <= VISION_DURATION)
            {
                player.removePotionEffectClient(Potion.nightVision.id);
            }
        }
    }

    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event)
    {
        if (isActivePlayer(event))
        {
            event.red = 0.1F * event.red + 0.9F * 0.4F;
            event.green = 0.1F * event.green + 0.9F * 0.8F;
            event.blue = 0.1F * event.blue + 0.9F * 0.4F;
        }
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event)
    {
        if (!isActivePlayer(event))
        {
            return;
        }
        Material material = event.block.getMaterial();
        if (material != Material.water && material != Material.lava)
        {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entity;
        float scale = getFogScale(player);
        float density = material == Material.lava
              ? 2F / (24F * scale) : 0.1F / scale;
        GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
        event.density = density;
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onFogRender(EntityViewRenderEvent.RenderFogEvent event)
    {
        if (!isActivePlayer(event))
        {
            return;
        }
        float far = event.farPlaneDistance;
        if (far < 20F)
        {
            far = Math.min(192F,
                  5F + 2.5F * (float)Math.pow(Math.E, 0.16F * far));
        }
        far *= getFogScale((EntityPlayer)event.entity);
        GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
        GL11.glFogf(GL11.GL_FOG_START, -8F);
        GL11.glFogf(GL11.GL_FOG_END, far);
    }

    @SubscribeEvent
    public void renderHud(RenderGameOverlayEvent.Post event)
    {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT)
        {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (player == null || minecraft.gameSettings.showDebugInfo)
        {
            return;
        }

        ItemStack helmet = player.getCurrentArmor(3);
        ModuleData vision = MekaSuitVisionHelper.getModule(helmet);
        ModuleData nutrition = MekaSuitNutritionalHelper.getModule(helmet);
        ItemStack bodyarmor = player.getCurrentArmor(2);
        ModuleData jetpack = MekaSuitJetpackHelper.getModule(bodyarmor);
        ModuleData gravitational = MekaSuitGravitationalHelper.getModule(
              bodyarmor);
        if (vision == null && (nutrition == null || !nutrition.isEnabled())
              && (jetpack == null || !jetpack.isEnabled())
              && gravitational == null)
        {
            return;
        }

        int iconX = event.resolution.getScaledWidth() - 26;
        int y = event.resolution.getScaledHeight() - 28;
        if (vision != null)
        {
            boolean enabled = vision.isEnabled();
            drawHudElement(minecraft, VISION_ICON, iconX, y,
                  LangUtils.localize(enabled ? "gui.mekasuit.on"
                        : "gui.mekasuit.off"),
                  enabled ? 0x55FFBB : 0x777777,
                  enabled ? 1F : 0.5F);
            y -= 20;
        }
        if (nutrition != null && nutrition.isEnabled())
        {
            String percent = Math.round(100D
                  * MekaSuitNutritionalHelper.getRatio(helmet)) + "%";
            drawHudElement(minecraft, NUTRITION_ICON, iconX, y, percent,
                  0xEB6CA3, 1F);
            y -= 20;
        }
        if (gravitational != null)
        {
            boolean enabled = gravitational.isEnabled();
            drawHudElement(minecraft, GRAVITATIONAL_ICON, iconX, y,
                  LangUtils.localize(enabled ? "gui.mekasuit.on"
                        : "gui.mekasuit.off"),
                  enabled ? 0x86E7FF : 0x777777,
                  enabled ? 1F : 0.5F);
            y -= 20;
        }
        if (jetpack != null && jetpack.isEnabled()
              && bodyarmor != null
              && bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor)
        {
            ItemMekaSuitBodyarmor item =
                  (ItemMekaSuitBodyarmor)bodyarmor.getItem();
            int capacity = item.getMaxGas(bodyarmor);
            int percent = capacity <= 0 ? 0 : Math.round(100F
                  * item.getStoredGas(bodyarmor) / capacity);
            String mode = jetpack.getMode();
            String iconName = MekaSuitJetpackHelper.DISABLED.equals(mode)
                  ? "off" : mode;
            drawHudElement(minecraft, icon("jetpack_" + iconName), iconX,
                  y, percent + "%", 0x86E7FF, 1F);
        }
    }

    private static void drawHudElement(Minecraft minecraft,
          ResourceLocation icon, int iconX, int y, String text, int color,
          float brightness)
    {
        minecraft.fontRenderer.drawStringWithShadow(text,
              iconX - minecraft.fontRenderer.getStringWidth(text) - 2,
              y + 4, color);
        minecraft.getTextureManager().bindTexture(icon);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(brightness, brightness, brightness, 1F);
        Gui.func_146110_a(iconX, y, 0, 0, 16, 16, 16, 16);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private static ResourceLocation icon(String name)
    {
        return new ResourceLocation(MekanismMekaSuit.MODID,
              "gui/hud/" + name + ".png");
    }

    private static boolean isActivePlayer(EntityViewRenderEvent event)
    {
        EntityPlayer local = Minecraft.getMinecraft().thePlayer;
        return event.entity instanceof EntityPlayer && event.entity == local
              && isActive(local);
    }

    private static boolean isActive(EntityPlayer player)
    {
        return MekaSuitVisionHelper.isActive(player.getCurrentArmor(3), player);
    }

    private static float getFogScale(EntityPlayer player)
    {
        ItemStack helmet = player.getCurrentArmor(3);
        ModuleData module = MekaSuitVisionHelper.getModule(helmet);
        return MekaSuitVisionHelper.getFogDistanceScale(
              module == null ? 1 : module.getInstalledCount());
    }
}
