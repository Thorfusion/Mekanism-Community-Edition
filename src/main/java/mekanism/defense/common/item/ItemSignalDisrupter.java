package defense.common.item;

import io.netty.buffer.ByteBuf;

import java.util.List;

import mekanism.common.item.ItemEnergized;
import mekanism.common.util.LangUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.IItemFrequency;
import defense.common.CreativeTabHandler;
import defense.common.DefenseTech;
import defense.common.Reference;
import defense.common.base.IItemPacket;

public class ItemSignalDisrupter extends ItemEnergized implements IItemFrequency, IItemPacket
{
	private static final long ENERGY_PER_TICK = 20;
	 
    public ItemSignalDisrupter()
    {
        super(100000);
        
        setUnlocalizedName("signalDisrupter");
        this.setCreativeTab(CreativeTabHandler.INSTANCE);
        this.setTextureName(Reference.PREFIX + "signalDisrupter");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
       itemIcon = reg.registerIcon(getIconString());
    }

    /** Allows items to add custom lines of information to the mouseover description */
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        super.addInformation(itemStack, par2EntityPlayer, par3List, par4);
        par3List.add(LangUtils.localize("info.misc.freq") + " " + this.getFrequency(itemStack));
    }

    @Override
    public int getFrequency(ItemStack itemStack)
    {
        if (itemStack.stackTagCompound == null)
        {
            return 0;
        }
        
        return itemStack.stackTagCompound.getInteger("frequency");
    }

    @Override
    public void setFrequency(int frequency, ItemStack itemStack)
    {
        if (itemStack.stackTagCompound == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        itemStack.stackTagCompound.setInteger("frequency", frequency);
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5)
    {
        if (!world.isRemote)
        {
            if (this.getEnergy(itemStack) > 20 && world.getWorldTime() % 20 == 0)
            {
                this.setEnergy(itemStack, getEnergy(itemStack) - ENERGY_PER_TICK);
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {        
        par3EntityPlayer.openGui(DefenseTech.INSTANCE, 0, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
        return par1ItemStack;
    }

    @Override
    public void handlePacket(EntityPlayer player, ByteBuf dataStream)
    {
        ItemStack itemStack = player.getCurrentEquippedItem();
        int frequency = dataStream.readInt();
        
        if(itemStack != null)
        {
            Item clientItem = itemStack.getItem();
            
            if(clientItem instanceof ItemSignalDisrupter)
            {
                ((ItemSignalDisrupter)clientItem).setFrequency(frequency, itemStack);
            }
        }
    }
}
