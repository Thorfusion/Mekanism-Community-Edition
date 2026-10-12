package mekanism.nuclear.common.radiation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.config.NuclearRadiationConfig;
import mekanism.nuclear.common.network.PacketRadiationData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

/** Server-authoritative accumulation, decay, persistence, and player effects. */
public final class PlayerRadiationHandler
{
    public static final PlayerRadiationHandler INSTANCE = new PlayerRadiationHandler();

    private static final int EXPOSURE_INTERVAL = 20;
    private static final double SECONDS_PER_HOUR = 3_600D;

    private PlayerRadiationHandler()
    {
    }

    @SubscribeEvent
    public void attachExposure(EntityEvent.EntityConstructing event)
    {
        if (event.entity instanceof EntityPlayer
              && event.entity.getExtendedProperties(RadiationExposure.PROPERTY_KEY) == null)
        {
            event.entity.registerExtendedProperties(RadiationExposure.PROPERTY_KEY,
                  new RadiationExposure());
        }
    }

    @SubscribeEvent
    public void clonePlayer(PlayerEvent.Clone event)
    {
        IRadiationExposure oldExposure = getExposure(event.original);
        IRadiationExposure newExposure = getExposure(event.entityPlayer);
        if (oldExposure != null && newExposure != null)
        {
            newExposure.deserializeNBT(oldExposure.serializeNBT());
        }
    }

    @SubscribeEvent
    public void updatePlayer(LivingEvent.LivingUpdateEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer))
        {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entityLiving;
        if (player.worldObj.isRemote || !player.isEntityAlive())
        {
            return;
        }
        IRadiationExposure exposure = getExposure(player);
        if (!(exposure instanceof RadiationExposure))
        {
            return;
        }
        RadiationExposure radiation = (RadiationExposure)exposure;
        boolean playing = !player.capabilities.isCreativeMode;
        boolean exposureTick = Math.floorMod(player.ticksExisted
              + player.getEntityId(), EXPOSURE_INTERVAL) == 0;
        if (!NuclearRadiationConfig.isRadiationEnabled())
        {
            if (exposureTick && player instanceof EntityPlayerMP)
            {
                MekanismNuclear.network.sendTo(new PacketRadiationData(
                      RadiationManager.BASELINE, RadiationManager.BASELINE),
                      (EntityPlayerMP)player);
            }
            return;
        }
        if (exposureTick)
        {
            RadiationPosition position = new RadiationPosition(
                  MathHelper.floor_double(player.posX),
                  MathHelper.floor_double(player.posY),
                  MathHelper.floor_double(player.posZ));
            double environmental = RadiationManager.INSTANCE.getRadiationLevel(
                  player.worldObj, position);
            if (playing && environmental > RadiationManager.BASELINE)
            {
                double resistance = RadiationShielding.getResistance(player);
                radiation.radiate(environmental / SECONDS_PER_HOUR
                      * (1 - resistance));
            }
            radiation.decay(NuclearRadiationConfig.getTargetDecayRate());
            if (player instanceof EntityPlayerMP)
            {
                MekanismNuclear.network.sendTo(new PacketRadiationData(
                      environmental, radiation.getRadiation()),
                      (EntityPlayerMP)player);
            }
        }
        if (playing)
        {
            applyNegativeEffects(player, radiation);
        }
    }

    private static void applyNegativeEffects(EntityPlayer player,
          RadiationExposure exposure)
    {
        double severity = exposure.getSeverity();
        double minimum = NuclearRadiationConfig.getNegativeEffectsMinSeverity();
        if (severity <= minimum)
        {
            return;
        }
        double chance = minimum + player.getRNG().nextDouble() * (1 - minimum);
        if (severity > chance)
        {
            float strength = exposure.getDamageStrength();
            if (player.getRNG().nextBoolean())
            {
                player.attackEntityFrom(NuclearDamageSources.RADIATION, strength);
            }
            player.addExhaustion(strength);
        }
    }

    public static IRadiationExposure getExposure(EntityPlayer player)
    {
        if (player == null)
        {
            return null;
        }
        Object exposure = player.getExtendedProperties(RadiationExposure.PROPERTY_KEY);
        return exposure instanceof IRadiationExposure
              ? (IRadiationExposure)exposure : null;
    }
}
