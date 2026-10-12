package mekanism.nuclear.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.config.NuclearRadiationConfig;
import mekanism.nuclear.common.radiation.IRadiationExposure;
import mekanism.nuclear.common.radiation.PlayerRadiationHandler;
import mekanism.nuclear.common.radiation.RadiationDisplay;
import mekanism.nuclear.common.radiation.RadiationManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/** Reads the accumulated radiation dose persisted on a player. */
public final class ItemDosimeter extends Item
{
    private static final String REGISTRY_NAME = "dosimeter";

    public ItemDosimeter()
    {
        setUnlocalizedName("Dosimeter");
        setMaxStackSize(1);
    }

    public String getRegistryName()
    {
        return REGISTRY_NAME;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.uncommon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(MekanismNuclear.MODID + ":"
              + REGISTRY_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip,
          boolean advanced)
    {
        tooltip.add(StatCollector.translateToLocal("tooltip.Dosimeter"));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world,
          EntityPlayer player)
    {
        if (!player.isSneaking() && !world.isRemote)
        {
            sendReading(player, player,
                  "message.mekanismnuclear.radiation.dose");
        }
        return stack;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player,
          EntityLivingBase target)
    {
        if (!player.isSneaking() && target instanceof EntityPlayer)
        {
            if (!player.worldObj.isRemote)
            {
                sendReading((EntityPlayer)target, player,
                      "message.mekanismnuclear.radiation.dose_target");
            }
            return true;
        }
        return false;
    }

    private static void sendReading(EntityPlayer target, EntityPlayer recipient,
          String translation)
    {
        IRadiationExposure exposure = PlayerRadiationHandler.getExposure(target);
        double dose = exposure == null
              || !NuclearRadiationConfig.isRadiationEnabled()
              ? RadiationManager.BASELINE : exposure.getRadiation();
        recipient.addChatMessage(new ChatComponentTranslation(translation,
              target.getCommandSenderName(), RadiationDisplay.formatDose(dose)));
        if (dose >= RadiationManager.MIN_MAGNITUDE)
        {
            recipient.addChatMessage(new ChatComponentTranslation(
                  "message.mekanismnuclear.radiation.decay",
                  RadiationDisplay.formatDuration(
                        RadiationDisplay.getDecaySeconds(dose,
                              NuclearRadiationConfig.getTargetDecayRate()))));
        }
    }
}
