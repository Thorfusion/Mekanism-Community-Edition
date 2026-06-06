package defense.common.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.common.explosive.Explosive;
import defense.common.explosive.ExplosiveRegistry;
import mekanism.common.util.LangUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMissile extends ItemBase
{
    public ItemMissile()
    {
        super("missile");
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {}

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return this.getUnlocalizedName() + "." + ExplosiveRegistry.get(itemStack.getItemDamage()).getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName()
    {
        return "missile";
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (Explosive zhaPin : ExplosiveRegistry.getAllMissles())
        {
            if (zhaPin.hasMissileForm())
            {
                par3List.add(new ItemStack(par1, 1, zhaPin.getID()));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
    {
        int tierdata = ExplosiveRegistry.get(stack.getItemDamage()).getTier();
        list.add(LangUtils.localize("info.misc.tier") + ": " + tierdata);

		super.addInformation(stack, player, list, bool);

    }
}
