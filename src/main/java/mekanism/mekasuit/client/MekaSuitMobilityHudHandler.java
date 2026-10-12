package mekanism.mekasuit.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/** Compact status text for configurable pants and boots units. */
@SideOnly(Side.CLIENT)
public final class MekaSuitMobilityHudHandler
{
    public static final MekaSuitMobilityHudHandler INSTANCE =
          new MekaSuitMobilityHudHandler();

    private MekaSuitMobilityHudHandler()
    {
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event)
    {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT)
        {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null
              || minecraft.gameSettings.showDebugInfo)
        {
            return;
        }
        List<String> lines = new ArrayList<String>();
        addPants(lines, minecraft.thePlayer.getCurrentArmor(1));
        addBoots(lines, minecraft.thePlayer.getCurrentArmor(0));
        int y = 6;
        for (String line : lines)
        {
            minecraft.fontRenderer.drawStringWithShadow(line, 6, y,
                  0x86E7FF);
            y += 10;
        }
    }

    static void addPants(List<String> lines, ItemStack stack)
    {
        if (!isTarget(stack, ModuleTarget.PANTS))
        {
            return;
        }
        ModuleContainer modules = ModuleContainer.fromStack(stack,
              ModuleTarget.PANTS);
        ModuleData sprint = modules.get(
              MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT);
        if (sprint != null)
        {
            lines.add(translate("hud.mekasuit.sprint",
                  localize("module.mode." + sprint.getMode())));
        }
        ModuleData swim = modules.get(
              MekaSuitModules.HYDROSTATIC_REPULSOR_UNIT);
        if (swim != null && swim.getInstalledCount() >= 4)
        {
            lines.add(translate("hud.mekasuit.swim",
                  localize(swim.isEnabled()
                        && swim.getBooleanConfig("swim_boost")
                              ? "gui.mekasuit.on"
                              : "gui.mekasuit.off")));
        }
    }

    static void addBoots(List<String> lines, ItemStack stack)
    {
        if (!isTarget(stack, ModuleTarget.BOOTS))
        {
            return;
        }
        ModuleContainer modules = ModuleContainer.fromStack(stack,
              ModuleTarget.BOOTS);
        ModuleData hydraulic = modules.get(
              MekaSuitModules.HYDRAULIC_PROPULSION_UNIT);
        if (hydraulic != null)
        {
            lines.add(translate("hud.mekasuit.jump",
                  localize("module.config.value."
                        + hydraulic.getEnumConfig("jump_boost"))));
        }
        ModuleData magnet = modules.get(
              MekaSuitModules.MAGNETIC_ATTRACTION_UNIT);
        if (magnet != null)
        {
            lines.add(translate("hud.mekasuit.magnet",
                  magnet.isEnabled()
                        ? localize("module.config.value."
                              + magnet.getEnumConfig("range"))
                        : localize("gui.mekasuit.off")));
        }
    }

    private static boolean isTarget(ItemStack stack, ModuleTarget target)
    {
        return stack != null
              && stack.getItem() instanceof ItemMekaSuitArmor
              && ((ItemMekaSuitArmor)stack.getItem()).getModuleTarget()
                    == target;
    }

    private static String localize(String key)
    {
        return StatCollector.translateToLocal(key);
    }

    private static String translate(String key, String value)
    {
        return StatCollector.translateToLocalFormatted(key, value);
    }
}
