package mekanism.mekasuit.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.client.MekanismClient;
import mekanism.client.MekanismKeyHandler;
import mekanism.client.sound.SoundHandler;
import mekanism.common.KeySync;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.content.gear.MekaSuitElytraHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitGravitationalHandler;
import mekanism.mekasuit.common.content.gear.MekaSuitGravitationalHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitJetpackHelper;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import mekanism.mekasuit.common.item.ItemMekaSuitBodyarmor;
import mekanism.mekasuit.common.network.PacketMekaSuitBodyMode;
import mekanism.mekasuit.common.network.PacketMekaSuitBodyMode.Action;
import mekanism.mekasuit.common.network.PacketMekaSuitElytraMode;
import mekanism.mekasuit.common.network.PacketMekaSuitStartElytra;
import mekanism.ultimate.common.integration.EtFuturumElytraCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import org.lwjgl.input.Keyboard;

/** Local key synchronization, prediction, and bodyarmor mode switching. */
@SideOnly(Side.CLIENT)
public final class MekaSuitJetpackClientHandler
{
    public static final MekaSuitJetpackClientHandler INSTANCE =
          new MekaSuitJetpackClientHandler();
    public static final KeyBinding boostKey = new KeyBinding(
          "mekanism.key.boost", Keyboard.KEY_LCONTROL,
          MekanismKeyHandler.keybindCategory);

    private boolean modeKeyDown;
    private boolean boostKeyDown;
    private boolean jumpKeyDown;

    private MekaSuitJetpackClientHandler()
    {
    }

    public static void registerKeyBinding()
    {
        ClientRegistry.registerKeyBinding(boostKey);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase != Phase.END)
        {
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (player == null)
        {
            modeKeyDown = false;
            boostKeyDown = false;
            jumpKeyDown = false;
            return;
        }
        ItemStack bodyarmor = player.getCurrentArmor(2);
        boolean wearingBodyarmor = bodyarmor != null
              && bodyarmor.getItem() instanceof ItemMekaSuitBodyarmor;
        boolean currentModeKey = minecraft.currentScreen == null
              && MekanismKeyHandler.armorModeSwitchKey.getIsKeyPressed();
        if (wearingBodyarmor && currentModeKey && !modeKeyDown)
        {
            changeMode(player, bodyarmor);
        }
        modeKeyDown = currentModeKey;

        boolean currentBoostKey = minecraft.currentScreen == null
              && boostKey.getIsKeyPressed();
        MekaSuitGravitationalHandler.INSTANCE.setBoosting(player,
              currentBoostKey);
        if (currentBoostKey != boostKeyDown)
        {
            boostKeyDown = currentBoostKey;
            MekanismMekaSuit.network.sendToServer(
                  new PacketMekaSuitBodyMode(Action.BOOSTING, "",
                        currentBoostKey));
        }

        ModuleData gravitational = wearingBodyarmor
              ? MekaSuitGravitationalHelper.getModule(bodyarmor) : null;
        if (gravitational != null && gravitational.isEnabled()
              && currentBoostKey && player.capabilities.isFlying
              && !player.capabilities.isCreativeMode
              && MekaSuitGravitationalHelper.canBoost(bodyarmor,
                    gravitational))
        {
            MekaSuitGravitationalHelper.applyBoost(player,
                  MekaSuitGravitationalHelper.getSpeedBoost(gravitational));
        }

        boolean currentJumpKey = minecraft.currentScreen == null
              && minecraft.gameSettings.keyBindJump.getIsKeyPressed();
        boolean requestElytra = currentJumpKey && !jumpKeyDown
              && EtFuturumElytraCompat.isAvailable()
              && !player.onGround && player.motionY < 0D
              && !EtFuturumElytraCompat.isFlying(player)
              && !player.isInWater() && player.ridingEntity == null
              && !player.capabilities.isFlying
              && MekaSuitElytraHelper.canUse(bodyarmor, player);
        jumpKeyDown = currentJumpKey;
        if (requestElytra)
        {
            MekanismMekaSuit.network.sendToServer(
                  new PacketMekaSuitStartElytra());
        }

        if (!wearingBodyarmor
              || !MekaSuitJetpackHelper.hasModule(bodyarmor))
        {
            return;
        }
        MekanismClient.updateKey(minecraft.gameSettings.keyBindJump,
              KeySync.ASCEND);
        MekanismClient.updateKey(minecraft.gameSettings.keyBindSneak,
              KeySync.DESCEND);
        boolean ascending = minecraft.currentScreen == null
              && minecraft.gameSettings.keyBindJump.getIsKeyPressed();
        boolean descending = minecraft.currentScreen == null
              && minecraft.gameSettings.keyBindSneak.getIsKeyPressed();
        if (MekaSuitJetpackHelper.isActive(bodyarmor, player, ascending,
              descending)
              && MekaSuitJetpackHelper.applyMotion(player,
                    MekaSuitJetpackHelper.getMode(bodyarmor),
                    MekaSuitJetpackHelper.getThrust(bodyarmor), ascending,
                    descending))
        {
            player.fallDistance = 0;
        }
    }

    private static void changeMode(EntityPlayer player,
          ItemStack bodyarmor)
    {
        ModuleContainer container = ModuleContainer.fromStack(bodyarmor,
              ModuleTarget.BODYARMOR);
        ModuleData jetpack = container.get(MekaSuitModules.JETPACK_UNIT);
        ModuleData gravitational = container.get(
              MekaSuitModules.GRAVITATIONAL_MODULATING_UNIT);
        ModuleData elytra = container.get(MekaSuitModules.ELYTRA_UNIT);
        if (elytra != null && (player.isSneaking()
              || jetpack == null && gravitational == null))
        {
            toggleElytra(player, bodyarmor, container, elytra);
            return;
        }
        if (jetpack != null)
        {
            String mode = player.isSneaking()
                  ? MekaSuitJetpackHelper.DISABLED
                  : jetpack.getType().cycleMode(jetpack.getMode(),
                        jetpack.getInstalledCount(), 1);
            if (container.setMode(MekaSuitModules.JETPACK_UNIT, mode))
            {
                container.save(bodyarmor);
                MekanismMekaSuit.network.sendToServer(
                      new PacketMekaSuitBodyMode(Action.JETPACK_MODE, mode,
                            false));
                player.addChatMessage(new ChatComponentTranslation(
                      "tooltip.mekasuit.jetpack.modeChanged",
                      new ChatComponentTranslation("module.mode." + mode)));
                SoundHandler.playSound("mekanism:etc.Hydraulic");
            }
            return;
        }

        if (gravitational != null)
        {
            boolean enabled = !gravitational.isEnabled();
            if (container.setEnabled(
                  MekaSuitModules.GRAVITATIONAL_MODULATING_UNIT, enabled))
            {
                container.save(bodyarmor);
                MekanismMekaSuit.network.sendToServer(
                      new PacketMekaSuitBodyMode(
                            Action.GRAVITATIONAL_ENABLED, "", enabled));
                player.addChatMessage(new ChatComponentTranslation(
                      "tooltip.mekasuit.gravitational.modeChanged",
                      new ChatComponentTranslation(enabled
                            ? "gui.mekasuit.on" : "gui.mekasuit.off")));
                SoundHandler.playSound("mekanism:etc.Hydraulic");
            }
            return;
        }

        if (elytra != null)
        {
            toggleElytra(player, bodyarmor, container, elytra);
        }
    }

    private static void toggleElytra(EntityPlayer player,
          ItemStack bodyarmor, ModuleContainer container,
          ModuleData module)
    {
        if (!EtFuturumElytraCompat.isAvailable())
        {
            return;
        }
        boolean enabled = !module.isEnabled();
        if (!container.setEnabled(MekaSuitModules.ELYTRA_UNIT, enabled))
        {
            return;
        }
        container.save(bodyarmor);
        MekanismMekaSuit.network.sendToServer(
              new PacketMekaSuitElytraMode(enabled));
        player.addChatMessage(new ChatComponentTranslation(
              "tooltip.mekasuit.elytra.modeChanged",
              new ChatComponentTranslation(enabled
                    ? "gui.mekasuit.on" : "gui.mekasuit.off")));
        SoundHandler.playSound("mekanism:etc.Hydraulic");
    }
}
