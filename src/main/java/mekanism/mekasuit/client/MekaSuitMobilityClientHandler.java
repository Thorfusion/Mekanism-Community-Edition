package mekanism.mekasuit.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.client.MekanismKeyHandler;
import mekanism.client.sound.SoundHandler;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.api.gear.ModuleTarget;
import mekanism.mekasuit.common.MekanismMekaSuit;
import mekanism.mekasuit.common.content.gear.MekaSuitModules;
import mekanism.mekasuit.common.content.gear.ModuleContainer;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import mekanism.mekasuit.common.network.PacketMekaSuitArmorMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import org.lwjgl.input.Keyboard;

/** Client mode input for pants and boots units. */
@SideOnly(Side.CLIENT)
public final class MekaSuitMobilityClientHandler
{
    public static final MekaSuitMobilityClientHandler INSTANCE =
          new MekaSuitMobilityClientHandler();
    public static final KeyBinding modeKey = new KeyBinding(
          "mekanism.key.mekasuitMode", Keyboard.KEY_N,
          MekanismKeyHandler.keybindCategory);

    private boolean modeKeyDown;

    private MekaSuitMobilityClientHandler()
    {
    }

    public static void registerKeyBinding()
    {
        ClientRegistry.registerKeyBinding(modeKey);
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
        boolean down = player != null && minecraft.currentScreen == null
              && modeKey.getIsKeyPressed();
        if (down && !modeKeyDown)
        {
            changeMode(player);
        }
        modeKeyDown = down;
    }

    private static void changeMode(EntityPlayer player)
    {
        ItemStack pants = player.getCurrentArmor(1);
        ItemStack boots = player.getCurrentArmor(0);
        ModuleContainer pantsModules = moduleContainer(pants,
              ModuleTarget.PANTS);
        ModuleContainer bootModules = moduleContainer(boots,
              ModuleTarget.BOOTS);
        ModuleData sprint = pantsModules == null ? null
              : pantsModules.get(
                    MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT);
        ModuleData magnet = bootModules == null ? null
              : bootModules.get(MekaSuitModules.MAGNETIC_ATTRACTION_UNIT);
        if (magnet != null && (player.isSneaking() || sprint == null))
        {
            boolean enabled = !magnet.isEnabled();
            if (bootModules.setEnabled(
                  MekaSuitModules.MAGNETIC_ATTRACTION_UNIT, enabled))
            {
                bootModules.save(boots);
                MekanismMekaSuit.network.sendToServer(
                      new PacketMekaSuitArmorMode(0,
                            MekaSuitModules.MAGNETIC_ATTRACTION_UNIT.getId(),
                            "", true));
                player.addChatMessage(new ChatComponentTranslation(
                      "tooltip.mekasuit.magnetic.modeChanged",
                      new ChatComponentTranslation(enabled
                            ? "gui.mekasuit.on" : "gui.mekasuit.off")));
                SoundHandler.playSound("mekanism:etc.Hydraulic");
            }
        }
        else if (sprint != null)
        {
            String mode = sprint.getType().cycleMode(sprint.getMode(),
                  sprint.getInstalledCount(), 1);
            if (pantsModules.setMode(
                  MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT, mode))
            {
                pantsModules.save(pants);
                MekanismMekaSuit.network.sendToServer(
                      new PacketMekaSuitArmorMode(1,
                            MekaSuitModules.LOCOMOTIVE_BOOSTING_UNIT.getId(),
                            mode, false));
                player.addChatMessage(new ChatComponentTranslation(
                      "tooltip.mekasuit.locomotive.modeChanged",
                      new ChatComponentTranslation("module.mode." + mode)));
                SoundHandler.playSound("mekanism:etc.Hydraulic");
            }
        }
    }

    private static ModuleContainer moduleContainer(ItemStack stack,
          ModuleTarget target)
    {
        return stack == null
              || !(stack.getItem() instanceof ItemMekaSuitArmor) ? null
              : ModuleContainer.fromStack(stack, target);
    }
}
