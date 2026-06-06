package defense.common.item;

import java.util.List;

import mekanism.common.util.LangUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.ExplosionEvent.ExplosivePreDetonationEvent;
import defense.api.ExplosiveType;
import defense.common.Reference;
import defense.common.entity.EntityGrenade;
import defense.common.explosive.Explosive;
import defense.common.explosive.ExplosiveRegistry;

public class ItemGrenade extends ItemBase
{
    public static final IIcon[] ICONS = new IIcon[256];

    public ItemGrenade()
    {
        super("grenade");
        this.setMaxStackSize(16);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        return par1ItemStack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 3 * 20;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if(itemStack != null)
        {
            entityPlayer.setItemInUse(itemStack, getMaxItemUseDuration(itemStack));
        }

        return itemStack;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int nengLiang)
    {
        if(!world.isRemote)
        {
            Explosive explosive = ExplosiveRegistry.get(itemStack.getItemDamage());
            ExplosivePreDetonationEvent evt = new ExplosivePreDetonationEvent(world, entityPlayer, ExplosiveType.ITEM, explosive);
            MinecraftForge.EVENT_BUS.post(evt);

            if(!evt.isCanceled())
            {
                if(!entityPlayer.capabilities.isCreativeMode)
                {
                    itemStack.stackSize--;

                    if(itemStack.stackSize <= 0)
                    {
                        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                    }
                }

                world.playSoundAtEntity(entityPlayer, "game.tnt.primed", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                world.spawnEntityInWorld(new EntityGrenade(world, entityPlayer, explosive.getID(), (float)(getMaxItemUseDuration(itemStack) - nengLiang) / (float)getMaxItemUseDuration(itemStack)));
            }
            else {
                entityPlayer.addChatMessage(new ChatComponentText("Grenades are banned in this region."));
            }
        }
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return getUnlocalizedName() + "." + ExplosiveRegistry.get(itemstack.getItemDamage()).getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName()
    {
        return "grenade";
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        int explosiveTier = ExplosiveRegistry.get(par1ItemStack.getItemDamage()).getTier();
        par3List.add(LangUtils.localize("info.misc.tier") + ": " + explosiveTier);
		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        for(int i = 0; i < ExplosiveRegistry.getExplosives().size(); i++)
        {
        	if(ExplosiveRegistry.get(i).hasGrenadeForm())
        	{
        		ICONS[i] = iconRegister.registerIcon(Reference.PREFIX + "grenade_" + ExplosiveRegistry.get(i).getUnlocalizedName());
        	}
        }
    }

    @Override
    public IIcon getIconFromDamage(int i)
    {
        return ICONS[i];
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for(Explosive explosive : ExplosiveRegistry.getExplosives())
        {
            if(explosive.hasGrenadeForm())
            {
                par3List.add(new ItemStack(par1, 1, explosive.getID()));
            }
        }
    }
}
