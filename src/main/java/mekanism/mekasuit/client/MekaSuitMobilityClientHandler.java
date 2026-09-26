package mekanism.mekasuit.client;

import mekanism.client.MekanismKeyHandler;
import mekanism.client.sound.SoundHandler;
import mekanism.common.MekanismSounds;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.MekaSuitMobilityHelper;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import mekanism.mekasuit.common.network.PacketMekaSuitArmorMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/** Client input adapter for the modern sneaking-speed attribute absent from 1.12. */
@SideOnly(Side.CLIENT)
public final class MekaSuitMobilityClientHandler {

    public static final MekaSuitMobilityClientHandler INSTANCE = new MekaSuitMobilityClientHandler();
    public static final KeyBinding modeKey = new KeyBinding(
          "mekanism.key.mekasuitMode", Keyboard.KEY_N, MekanismKeyHandler.keybindCategory);

    private boolean modeKeyDown;

    private MekaSuitMobilityClientHandler() {
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        boolean down = player != null && minecraft.currentScreen == null && modeKey.isKeyDown();
        if (down && !modeKeyDown) {
            changeMode(player);
        }
        modeKeyDown = down;
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (!player.isSneaking()) {
            return;
        }
        ItemStack pants = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        float sneakSpeed = MekaSuitMobilityHelper.getSneakSpeed(pants);
        if (sneakSpeed <= 0.3F) {
            return;
        }
        float multiplier = sneakSpeed / 0.3F;
        event.getMovementInput().moveForward *= multiplier;
        event.getMovementInput().moveStrafe *= multiplier;
    }

    private static void changeMode(EntityPlayer player) {
        ItemStack pants = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        ModuleContainer pantsModules = moduleContainer(pants, ModuleTarget.PANTS);
        ModuleContainer bootModules = moduleContainer(boots, ModuleTarget.BOOTS);
        ModuleData sprint = pantsModules == null ? null
              : pantsModules.get(MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT);
        ModuleData magnet = bootModules == null ? null
              : bootModules.get(MekaSuitModules.MAGNETIC_ATTRACTION_UNIT);
        if (magnet != null && (player.isSneaking() || sprint == null)) {
            boolean enabled = !magnet.isEnabled();
            if (bootModules.setEnabled(MekaSuitModules.MAGNETIC_ATTRACTION_UNIT, enabled)) {
                bootModules.save(boots);
                MekanismMekaSuit.network.sendToServer(new PacketMekaSuitArmorMode(EntityEquipmentSlot.FEET,
                      MekaSuitModules.MAGNETIC_ATTRACTION_UNIT.getId(), "", true));
                player.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation(
                      "tooltip.mekasuit.magnetic.modeChanged",
                      new net.minecraft.util.text.TextComponentTranslation(enabled
                            ? "gui.mekasuit.on" : "gui.mekasuit.off")), true);
                SoundHandler.playSound(MekanismSounds.HYDRAULIC);
            }
        } else if (sprint != null) {
            String mode = sprint.getType().cycleMode(sprint.getMode(), sprint.getInstalledCount(), 1);
            if (pantsModules.setMode(MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT, mode)) {
                pantsModules.save(pants);
                MekanismMekaSuit.network.sendToServer(new PacketMekaSuitArmorMode(EntityEquipmentSlot.LEGS,
                      MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT.getId(), mode, false));
                player.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation(
                      "tooltip.mekasuit.locomotive.modeChanged",
                      new net.minecraft.util.text.TextComponentTranslation("module.mode." + mode)), true);
                SoundHandler.playSound(MekanismSounds.HYDRAULIC);
            }
        }
    }

    private static ModuleContainer moduleContainer(ItemStack stack, ModuleTarget target) {
        return stack.isEmpty() || !(stack.getItem() instanceof mekanism.mekasuit.common.item.ItemMekaSuitArmor)
              ? null : ModuleContainer.fromStack(stack, target);
    }
}
