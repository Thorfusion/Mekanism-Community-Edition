package mekanism.ultimate.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.api.EnumColor;
import mekanism.api.IStoneGeneratorUpgradeTile;
import mekanism.common.Mekanism;
import mekanism.common.item.ItemMekanism;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import mekanism.ultimate.common.MekanismUltimate;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public final class ItemStoneGeneratorUpgrade extends ItemMekanism
{
    public ItemStoneGeneratorUpgrade()
    {
        setUnlocalizedName("StoneGeneratorUpgrade");
        setMaxStackSize(1);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player,
          World world, int x, int y, int z, int side, float hitX, float hitY,
          float hitZ)
    {
        if (!player.isSneaking())
        {
            return false;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof IStoneGeneratorUpgradeTile))
        {
            return false;
        }
        if (!SecurityUtils.canAccess(player, tile))
        {
            if (!world.isRemote)
            {
                SecurityUtils.displayNoAccess(player);
            }
            return true;
        }
        if (!world.isRemote
              && ((IStoneGeneratorUpgradeTile)tile)
                    .installStoneGeneratorUpgrade())
        {
            stack.stackSize--;
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("rawtypes")
    public void addInformation(ItemStack stack, EntityPlayer player,
          List tooltip, boolean advanced)
    {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
        {
            tooltip.add(LangUtils.localize("tooltip.hold") + " "
                  + EnumColor.AQUA + "shift" + EnumColor.GREY + " "
                  + LangUtils.localize("tooltip.forDetails"));
        }
        else
        {
            tooltip.addAll(MekanismUtils.splitTooltip(LangUtils.localize(
                  "upgrade.stone_generator.desc"), stack));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        itemIcon = register.registerIcon(
              MekanismUltimate.MODID.toLowerCase()
                    + ":upgrade_stone_generator");
    }
}
