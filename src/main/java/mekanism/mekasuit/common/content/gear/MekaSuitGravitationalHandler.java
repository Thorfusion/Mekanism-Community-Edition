package mekanism.mekasuit.common.content.gear;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import mekanism.common.ObfuscatedNames;
import mekanism.common.util.MekanismUtils;
import mekanism.mekasuit.api.gear.ModuleData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;

/** Server-authoritative survival flight and boost controller. */
public final class MekaSuitGravitationalHandler
{
    public static final MekaSuitGravitationalHandler INSTANCE =
          new MekaSuitGravitationalHandler();

    private final Set<UUID> grantedFlight = new HashSet<UUID>();
    private final Set<UUID> boostingPlayers = new HashSet<UUID>();

    private MekaSuitGravitationalHandler()
    {
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase != Phase.END || event.side != Side.SERVER)
        {
            return;
        }
        EntityPlayer player = event.player;
        UUID playerId = player.getUniqueID();
        if (player.capabilities.isCreativeMode)
        {
            grantedFlight.remove(playerId);
            return;
        }
        ItemStack bodyarmor = player.getCurrentArmor(2);
        boolean canProvideFlight =
              MekaSuitGravitationalHelper.canProvideFlight(bodyarmor);
        updateFlightCapability(player, canProvideFlight);
        if (!canProvideFlight || !player.capabilities.isFlying)
        {
            return;
        }
        ModuleData module =
              MekaSuitGravitationalHelper.getModule(bodyarmor);
        boolean boosting = boostingPlayers.contains(playerId)
              && MekaSuitGravitationalHelper.canBoost(bodyarmor, module);
        long usage = MekaSuitGravitationalHelper.getBaseEnergyUsage();
        if (boosting)
        {
            MekaSuitGravitationalHelper.applyBoost(player,
                  MekaSuitGravitationalHelper.getSpeedBoost(module));
            usage = MekaSuitGravitationalHelper.getBoostEnergyUsage();
        }
        MekaSuitGravitationalHelper.useEnergy(bodyarmor, usage);
        if (player instanceof EntityPlayerMP)
        {
            MekanismUtils.setPrivateValue(
                  ((EntityPlayerMP)player).playerNetServerHandler, 0,
                  NetHandlerPlayServer.class,
                  ObfuscatedNames.NetHandlerPlayServer_floatingTickCount);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event)
    {
        UUID playerId = event.player.getUniqueID();
        grantedFlight.remove(playerId);
        boostingPlayers.remove(playerId);
    }

    public void setBoosting(EntityPlayer player, boolean boosting)
    {
        if (player == null)
        {
            return;
        }
        if (boosting)
        {
            boostingPlayers.add(player.getUniqueID());
        }
        else
        {
            boostingPlayers.remove(player.getUniqueID());
        }
    }

    public boolean isBoosting(EntityPlayer player)
    {
        return player != null
              && boostingPlayers.contains(player.getUniqueID());
    }

    private void updateFlightCapability(EntityPlayer player,
          boolean shouldAllow)
    {
        UUID playerId = player.getUniqueID();
        boolean changed = false;
        if (shouldAllow)
        {
            if (!player.capabilities.allowFlying)
            {
                player.capabilities.allowFlying = true;
                grantedFlight.add(playerId);
                changed = true;
            }
        }
        else if (grantedFlight.remove(playerId))
        {
            if (player.capabilities.allowFlying)
            {
                player.capabilities.allowFlying = false;
                changed = true;
            }
            if (player.capabilities.isFlying)
            {
                player.capabilities.isFlying = false;
                changed = true;
            }
        }
        if (changed && player instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)player).sendPlayerAbilities();
        }
    }
}
