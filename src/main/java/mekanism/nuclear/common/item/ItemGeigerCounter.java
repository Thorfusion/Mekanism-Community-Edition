package mekanism.nuclear.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.common.item.ItemMekanism;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.config.NuclearRadiationConfig;
import mekanism.nuclear.common.radiation.RadiationDisplay;
import mekanism.nuclear.common.radiation.RadiationManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/** Reads environmental radiation at the player's current position. */
public final class ItemGeigerCounter extends ItemMekanism {

    public ItemGeigerCounter() {
        setRegistryName(new ResourceLocation(MekanismNuclear.MODID, "geiger_counter"));
        setTranslationKey("GeigerCounter");
        setMaxStackSize(1);
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(new TextComponentTranslation("tooltip.GeigerCounter").getFormattedText());
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        if (!world.isRemote) {
            double level = RadiationManager.INSTANCE.getRadiationLevel(world, player.getPosition());
            player.sendMessage(new TextComponentTranslation("message.mekanismnuclear.radiation.environment",
                  RadiationDisplay.formatDoseRate(level)));
            if (level >= RadiationManager.MIN_MAGNITUDE) {
                player.sendMessage(new TextComponentTranslation("message.mekanismnuclear.radiation.decay",
                      RadiationDisplay.formatDuration(RadiationDisplay.getDecaySeconds(level,
                            NuclearRadiationConfig.getSourceDecayRate()))));
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
