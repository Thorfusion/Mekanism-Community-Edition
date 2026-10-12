package mekanism.nuclear.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.nuclear.common.MekanismNuclear;
import mekanism.nuclear.common.config.NuclearRadiationConfig;
import mekanism.nuclear.common.radiation.RadiationDisplay;
import mekanism.nuclear.common.radiation.RadiationManager;
import mekanism.nuclear.common.radiation.RadiationPosition;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

/** Reads environmental radiation at the player's current position. */
public final class ItemGeigerCounter extends Item
{
    private static final String REGISTRY_NAME = "geiger_counter";

    @SideOnly(Side.CLIENT)
    private IIcon[] radiationIcons;

    public ItemGeigerCounter()
    {
        setUnlocalizedName("GeigerCounter");
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
        radiationIcons = new IIcon[6];
        for (int index = 0; index < radiationIcons.length; index++)
        {
            radiationIcons[index] = register.registerIcon(MekanismNuclear.MODID
                  + ":" + REGISTRY_NAME + "_" + index);
        }
        itemIcon = radiationIcons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack)
    {
        return currentIcon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass)
    {
        return currentIcon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player,
          ItemStack usingItem, int useRemaining)
    {
        return currentIcon();
    }

    @SideOnly(Side.CLIENT)
    private IIcon currentIcon()
    {
        int scale = MekanismNuclear.proxy == null ? 0
              : MekanismNuclear.proxy.getRadiationScale();
        scale = Math.max(0, Math.min(5, scale));
        return radiationIcons == null ? itemIcon : radiationIcons[scale];
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip,
          boolean advanced)
    {
        tooltip.add(StatCollector.translateToLocal("tooltip.GeigerCounter"));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world,
          EntityPlayer player)
    {
        if (!player.isSneaking() && !world.isRemote)
        {
            RadiationPosition position = new RadiationPosition(
                  MathHelper.floor_double(player.posX),
                  MathHelper.floor_double(player.posY),
                  MathHelper.floor_double(player.posZ));
            double level = RadiationManager.INSTANCE.getRadiationLevel(world,
                  position);
            player.addChatMessage(new ChatComponentTranslation(
                  "message.mekanismnuclear.radiation.environment",
                  RadiationDisplay.formatDoseRate(level)));
            if (level >= RadiationManager.MIN_MAGNITUDE)
            {
                player.addChatMessage(new ChatComponentTranslation(
                      "message.mekanismnuclear.radiation.decay",
                      RadiationDisplay.formatDuration(
                            RadiationDisplay.getDecaySeconds(level,
                                  NuclearRadiationConfig.getSourceDecayRate()))));
            }
        }
        return stack;
    }
}
