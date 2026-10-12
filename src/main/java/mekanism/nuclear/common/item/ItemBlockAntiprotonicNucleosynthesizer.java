package mekanism.nuclear.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mekanism.common.util.LangUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockAntiprotonicNucleosynthesizer extends ItemBlock
{
    public ItemBlockAntiprotonicNucleosynthesizer(Block block)
    {
        super(block);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player,
          List tooltip, boolean advanced)
    {
        tooltip.add(LangUtils.localize(
              "tooltip.AntiprotonicNucleosynthesizer"));
    }
}
