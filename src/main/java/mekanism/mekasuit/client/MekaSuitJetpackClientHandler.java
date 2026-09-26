package mekanism.mekasuit.client;

import mekanism.client.MekanismClient;
import mekanism.client.MekanismKeyHandler;
import mekanism.client.sound.SoundHandler;
import mekanism.common.KeySync;
import mekanism.common.MekanismSounds;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.content.gear.MekaSuitGravitationalHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitJetpackHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import mekanism.mekasuit.common.network.PacketMekaSuitBoostState;
import mekanism.mekasuit.common.network.PacketMekaSuitGravitationalMode;
import mekanism.mekasuit.common.network.PacketMekaSuitModeChange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/** Local input synchronization, movement prediction, and armor-mode switching for bodyarmor flight units. */
@SideOnly(Side.CLIENT)
public final class MekaSuitJetpackClientHandler {

    public static final MekaSuitJetpackClientHandler INSTANCE = new MekaSuitJetpackClientHandler();
    public static final KeyBinding boostKey = new KeyBinding(
          "mekanism.key.boost", Keyboard.KEY_LCONTROL, MekanismKeyHandler.keybindCategory);

    private boolean modeKeyDown;
    private boolean boostKeyDown;

    private MekaSuitJetpackClientHandler() {
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.player;
        if (player == null) {
            modeKeyDown = false;
            boostKeyDown = false;
            return;
        }
        ItemStack bodyarmor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        boolean wearingBodyarmor = !bodyarmor.isEmpty() && bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor;
        boolean currentModeKey = minecraft.currentScreen == null && MekanismKeyHandler.armorModeSwitchKey.isKeyDown();
        if (wearingBodyarmor && currentModeKey && !modeKeyDown) {
            changeMode(player, bodyarmor);
        }
        modeKeyDown = currentModeKey;

        boolean currentBoostKey = minecraft.currentScreen == null && boostKey.isKeyDown();
        if (currentBoostKey != boostKeyDown) {
            boostKeyDown = currentBoostKey;
            MekanismMekaSuit.network.sendToServer(new PacketMekaSuitBoostState(currentBoostKey));
        }

        ModuleData gravitational = wearingBodyarmor
              ? MekaSuitGravitationalHelper.getModule(bodyarmor) : null;
        if (gravitational != null && gravitational.isEnabled() && currentBoostKey
              && player.capabilities.isFlying && !player.capabilities.isCreativeMode && !player.isSpectator()
              && MekaSuitGravitationalHelper.canBoost(bodyarmor, gravitational)) {
            MekaSuitGravitationalHelper.applyBoost(player,
                  MekaSuitGravitationalHelper.getSpeedBoost(gravitational));
        }
        if (!wearingBodyarmor || !MekaSuitJetpackHelper.hasModule(bodyarmor)) {
            return;
        }

        MekanismClient.updateKey(minecraft.gameSettings.keyBindJump, KeySync.ASCEND);
        MekanismClient.updateKey(minecraft.gameSettings.keyBindSneak, KeySync.DESCEND);
        boolean ascending = minecraft.currentScreen == null && minecraft.gameSettings.keyBindJump.isKeyDown();
        boolean descending = minecraft.currentScreen == null && minecraft.gameSettings.keyBindSneak.isKeyDown();
        if (MekaSuitJetpackHelper.isActive(bodyarmor, player, ascending, descending)
              && MekaSuitJetpackHelper.applyMotion(player, MekaSuitJetpackHelper.getMode(bodyarmor),
              MekaSuitJetpackHelper.getThrust(bodyarmor), ascending, descending)) {
            player.fallDistance = 0F;
        }
    }

    private static void changeMode(EntityPlayer player, ItemStack bodyarmor) {
        ModuleContainer container = ModuleContainer.fromStack(bodyarmor, ModuleTarget.BODYARMOR);
        ModuleData module = container.get(MekaSuitModules.JETPACK_UNIT);
        if (module != null) {
            String mode = player.isSneaking() ? MekaSuitJetpackHelper.DISABLED
                  : module.getType().cycleMode(module.getMode(), module.getInstalledCount(), 1);
            if (container.setMode(MekaSuitModules.JETPACK_UNIT, mode)) {
                container.save(bodyarmor);
                MekanismMekaSuit.network.sendToServer(new PacketMekaSuitModeChange(mode));
                player.sendMessage(new TextComponentTranslation("tooltip.mekasuit.jetpack.modeChanged",
                      new TextComponentTranslation("module.mode." + mode)));
                SoundHandler.playSound(MekanismSounds.HYDRAULIC);
            }
            return;
        }

        module = container.get(MekaSuitModules.GRAVITATIONAL_MODULATING_UNIT);
        if (module != null) {
            boolean enabled = !module.isEnabled();
            if (!container.setEnabled(MekaSuitModules.GRAVITATIONAL_MODULATING_UNIT, enabled)) {
                return;
            }
            container.save(bodyarmor);
            MekanismMekaSuit.network.sendToServer(new PacketMekaSuitGravitationalMode(enabled));
            player.sendMessage(new TextComponentTranslation("tooltip.mekasuit.gravitational.modeChanged",
                  new TextComponentTranslation(enabled ? "gui.mekasuit.on" : "gui.mekasuit.off")));
            SoundHandler.playSound(MekanismSounds.HYDRAULIC);
        }
    }
}
