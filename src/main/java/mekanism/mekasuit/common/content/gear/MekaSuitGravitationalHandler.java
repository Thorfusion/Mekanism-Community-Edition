package mekanism.mekasuit.common.content.gear;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import mekanism.mekasuit.api.gear.ModuleData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

/** Server-authoritative survival flight and boost controller for the Gravitational Modulating Unit. */
public final class MekaSuitGravitationalHandler {

    public static final MekaSuitGravitationalHandler INSTANCE = new MekaSuitGravitationalHandler();

    private final Set<UUID> grantedFlight = new HashSet<>();
    private final Set<UUID> boostingPlayers = new HashSet<>();

    private MekaSuitGravitationalHandler() {
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != Phase.END || event.side != Side.SERVER) {
            return;
        }
        EntityPlayer player = event.player;
        UUID playerId = player.getUniqueID();
        if (player.capabilities.isCreativeMode || player.isSpectator()) {
            // Creative and spectator own their flight capability; never revoke it.
            grantedFlight.remove(playerId);
            return;
        }

        ItemStack bodyarmor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        boolean canProvideFlight = MekaSuitGravitationalHelper.canProvideFlight(bodyarmor);
        updateFlightCapability(player, canProvideFlight);
        if (!canProvideFlight || !player.capabilities.isFlying) {
            return;
        }

        ModuleData module = MekaSuitGravitationalHelper.getModule(bodyarmor);
        boolean boosting = boostingPlayers.contains(playerId)
              && MekaSuitGravitationalHelper.canBoost(bodyarmor, module);
        long usage = MekaSuitGravitationalHelper.getBaseEnergyUsage();
        if (boosting) {
            MekaSuitGravitationalHelper.applyBoost(player,
                  MekaSuitGravitationalHelper.getSpeedBoost(module));
            usage = MekaSuitGravitationalHelper.getBoostEnergyUsage();
        }
        MekaSuitGravitationalHelper.useEnergy(bodyarmor, usage);
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).connection.floatingTickCount = 0;
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        UUID playerId = event.player.getUniqueID();
        grantedFlight.remove(playerId);
        boostingPlayers.remove(playerId);
    }

    public void setBoosting(EntityPlayer player, boolean boosting) {
        if (player == null) {
            return;
        }
        if (boosting) {
            boostingPlayers.add(player.getUniqueID());
        } else {
            boostingPlayers.remove(player.getUniqueID());
        }
    }

    private void updateFlightCapability(EntityPlayer player, boolean shouldAllow) {
        UUID playerId = player.getUniqueID();
        boolean changed = false;
        if (shouldAllow) {
            if (!player.capabilities.allowFlying) {
                player.capabilities.allowFlying = true;
                grantedFlight.add(playerId);
                changed = true;
            }
        } else if (grantedFlight.remove(playerId)) {
            if (player.capabilities.allowFlying) {
                player.capabilities.allowFlying = false;
                changed = true;
            }
            if (player.capabilities.isFlying) {
                player.capabilities.isFlying = false;
                changed = true;
            }
        }
        if (changed && player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).sendPlayerAbilities();
        }
    }
}
