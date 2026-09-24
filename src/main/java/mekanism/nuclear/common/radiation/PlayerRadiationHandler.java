package mekanism.nuclear.common.radiation;

import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.config.NuclearRadiationConfig;
import mekanism.nuclear.common.network.PacketRadiationData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Server-authoritative accumulation, decay, persistence, and effects for player radiation. */
@Mod.EventBusSubscriber(modid = MekanismNuclear.MODID)
public final class PlayerRadiationHandler {

    private static final ResourceLocation CAPABILITY_ID = new ResourceLocation(MekanismNuclear.MODID,
          "radiation_exposure");
    private static final int EXPOSURE_INTERVAL = 20;
    private static final double SECONDS_PER_HOUR = 3_600D;

    private PlayerRadiationHandler() {
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(CAPABILITY_ID, new RadiationExposureProvider());
        }
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        IRadiationExposure oldExposure = getExposure(event.getOriginal());
        IRadiationExposure newExposure = getExposure(event.getEntityPlayer());
        if (oldExposure != null && newExposure != null) {
            newExposure.deserializeNBT(oldExposure.serializeNBT());
        }
    }

    @SubscribeEvent
    public static void updatePlayer(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.world.isRemote || !player.isEntityAlive()) {
            return;
        }
        IRadiationExposure exposure = getExposure(player);
        if (!(exposure instanceof RadiationExposure)) {
            return;
        }
        RadiationExposure radiation = (RadiationExposure) exposure;
        boolean playing = !player.capabilities.isCreativeMode && !player.isSpectator();
        boolean exposureTick = Math.floorMod(player.ticksExisted + player.getEntityId(), EXPOSURE_INTERVAL) == 0;
        if (!NuclearRadiationConfig.isRadiationEnabled()) {
            if (exposureTick && player instanceof EntityPlayerMP) {
                MekanismNuclear.network.sendTo(new PacketRadiationData(RadiationManager.BASELINE,
                      RadiationManager.BASELINE), (EntityPlayerMP) player);
            }
            return;
        }
        if (exposureTick) {
            double environmental = RadiationManager.INSTANCE.getRadiationLevel(player.world, player.getPosition());
            if (playing) {
                if (environmental > RadiationManager.BASELINE) {
                    double resistance = RadiationShielding.getResistance(player);
                    radiation.radiate(environmental / SECONDS_PER_HOUR * (1 - resistance));
                }
            }
            radiation.decay(NuclearRadiationConfig.getTargetDecayRate());
            if (player instanceof EntityPlayerMP) {
                MekanismNuclear.network.sendTo(new PacketRadiationData(environmental, radiation.getRadiation()),
                      (EntityPlayerMP) player);
            }
        }
        if (playing) {
            applyNegativeEffects(player, radiation);
        }
    }

    private static void applyNegativeEffects(EntityPlayer player, RadiationExposure exposure) {
        double severity = exposure.getSeverity();
        double minimum = NuclearRadiationConfig.getNegativeEffectsMinSeverity();
        if (severity <= minimum) {
            return;
        }
        double chance = minimum + player.getRNG().nextDouble() * (1 - minimum);
        if (severity > chance) {
            float strength = exposure.getDamageStrength();
            if (player.getRNG().nextBoolean()) {
                player.attackEntityFrom(NuclearDamageSources.RADIATION, strength);
            }
            player.addExhaustion(strength);
        }
    }

    public static IRadiationExposure getExposure(EntityPlayer player) {
        return player == null || RadiationCapabilities.EXPOSURE_CAPABILITY == null ? null
              : player.getCapability(RadiationCapabilities.EXPOSURE_CAPABILITY, null);
    }
}
