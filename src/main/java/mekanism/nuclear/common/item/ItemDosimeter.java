package mekanism.nuclear.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.common.item.ItemMekanism;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.config.NuclearRadiationConfig;
import mekanism.nuclear.common.radiation.IRadiationExposure;
import mekanism.nuclear.common.radiation.PlayerRadiationHandler;
import mekanism.nuclear.common.radiation.RadiationDisplay;
import mekanism.nuclear.common.radiation.RadiationManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/** Reads the accumulated radiation dose persisted on a player. */
public final class ItemDosimeter extends ItemMekanism {

    public ItemDosimeter() {
        setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "dosimeter"));
        setTranslationKey("Dosimeter");
        setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(new TextComponentTranslation("tooltip.Dosimeter").getFormattedText());
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!world.isRemote) {
            sendReading(player, player, "message.mekanismnuclear.radiation.dose");
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target,
          EnumHand hand) {
        if (!player.isSneaking() && target instanceof EntityPlayer) {
            if (!player.world.isRemote) {
                sendReading((EntityPlayer) target, player, "message.mekanismnuclear.radiation.dose_target");
            }
            return true;
        }
        return false;
    }

    private static void sendReading(EntityPlayer target, EntityPlayer recipient, String translation) {
        IRadiationExposure exposure = PlayerRadiationHandler.getExposure(target);
        double dose = exposure == null || !NuclearRadiationConfig.isRadiationEnabled()
              ? RadiationManager.BASELINE : exposure.getRadiation();
        recipient.sendMessage(new TextComponentTranslation(translation, target.getDisplayName(),
              RadiationDisplay.formatDose(dose)));
        if (dose >= RadiationManager.MIN_MAGNITUDE) {
            recipient.sendMessage(new TextComponentTranslation("message.mekanismnuclear.radiation.decay",
                  RadiationDisplay.formatDuration(RadiationDisplay.getDecaySeconds(dose,
                        NuclearRadiationConfig.getTargetDecayRate()))));
        }
    }
}
