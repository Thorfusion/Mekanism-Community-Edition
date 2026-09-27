package mekanism.ultimate.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.EnumColor;
import mekanism.api.IStoneGeneratorUpgradeTile;
import mekanism.common.Mekanism;
import mekanism.common.item.ItemMekanism;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public final class ItemStoneGeneratorUpgrade extends ItemMekanism {

    public ItemStoneGeneratorUpgrade() {
        setMaxStackSize(1);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos,
          EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!player.isSneaking()) return EnumActionResult.PASS;
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof IStoneGeneratorUpgradeTile)) return EnumActionResult.PASS;
        if (!world.isRemote && ((IStoneGeneratorUpgradeTile) tile).installStoneGeneratorUpgrade()) {
            player.getHeldItem(hand).shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            tooltip.add(LangUtils.localize("tooltip.hold") + " " + EnumColor.AQUA + "shift"
                  + EnumColor.GREY + " " + LangUtils.localize("tooltip.forDetails"));
        } else {
            tooltip.addAll(MekanismUtils.splitTooltip(
                  LangUtils.localize("upgrade.stone_generator.desc"), stack));
        }
    }
}
