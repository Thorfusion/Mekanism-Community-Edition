package mekanism.mekasuit.client;

import java.util.ArrayList;
import java.util.List;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Compact status text for configurable pants and boots units. */
@SideOnly(Side.CLIENT)
public final class MekaSuitMobilityHudHandler {

    public static final MekaSuitMobilityHudHandler INSTANCE = new MekaSuitMobilityHudHandler();

    private MekaSuitMobilityHudHandler() {
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.player == null || minecraft.gameSettings.showDebugInfo) {
            return;
        }
        List<String> lines = new ArrayList<>();
        addPants(lines, minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
        addBoots(lines, minecraft.player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        int y = 6;
        for (String line : lines) {
            minecraft.fontRenderer.drawStringWithShadow(line, 6, y, 0x86E7FF);
            y += 10;
        }
    }

    private static void addPants(List<String> lines, ItemStack stack) {
        if (!isTarget(stack, ModuleTarget.PANTS)) {
            return;
        }
        ModuleContainer modules = ModuleContainer.fromStack(stack, ModuleTarget.PANTS);
        ModuleData sprint = modules.get(MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT);
        if (sprint != null) {
            lines.add(I18n.format("hud.mekasuit.sprint", I18n.format("module.mode." + sprint.getMode())));
        }
        ModuleData swim = modules.get(MekaSuitModules.HYDROSTATIC_REPULSOR_UNIT);
        if (swim != null && swim.getInstalledCount() >= 4) {
            lines.add(I18n.format("hud.mekasuit.swim",
                  I18n.format(swim.isEnabled() && swim.getBooleanConfig("swim_boost")
                        ? "gui.mekasuit.on" : "gui.mekasuit.off")));
        }
    }

    private static void addBoots(List<String> lines, ItemStack stack) {
        if (!isTarget(stack, ModuleTarget.BOOTS)) {
            return;
        }
        ModuleContainer modules = ModuleContainer.fromStack(stack, ModuleTarget.BOOTS);
        ModuleData hydraulic = modules.get(MekaSuitModules.HYDRAULIC_PROPULSION_UNIT);
        if (hydraulic != null) {
            lines.add(I18n.format("hud.mekasuit.jump",
                  I18n.format("module.config.value." + hydraulic.getEnumConfig("jump_boost"))));
        }
        ModuleData magnet = modules.get(MekaSuitModules.MAGNETIC_ATTRACTION_UNIT);
        if (magnet != null) {
            lines.add(I18n.format("hud.mekasuit.magnet", magnet.isEnabled()
                  ? I18n.format("module.config.value." + magnet.getEnumConfig("range"))
                  : I18n.format("gui.mekasuit.off")));
        }
    }

    private static boolean isTarget(ItemStack stack, ModuleTarget target) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemMekaSuitArmor
              && ((ItemMekaSuitArmor) stack.getItem()).getModuleTarget() == target;
    }
}
