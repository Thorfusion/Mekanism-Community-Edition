package defense.common.item;

import java.util.List;

import mekanism.common.item.ItemEnergized;
import mekanism.common.util.LangUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.api.IItemTracker;
import defense.common.CreativeTabHandler;
import defense.common.Reference;

public class ItemTracker extends ItemEnergized implements IItemTracker
{
    private static final long ENERGY_PER_TICK = 1;

    public ItemTracker()
    {
        super(100000);
        
        setUnlocalizedName("tracker");
        this.setCreativeTab(CreativeTabHandler.INSTANCE);
        this.setTextureName(Reference.PREFIX + "tracker");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
        if (par1IconRegister instanceof TextureMap)
        {
            ((TextureMap) par1IconRegister).setTextureEntry(Reference.PREFIX + "tracker", new TextureTracker());
            this.itemIcon = ((TextureMap) par1IconRegister).getTextureExtry(Reference.PREFIX + "tracker");
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        super.addInformation(itemStack, par2EntityPlayer, par3List, par4);

        Entity trackingEntity = getTrackingEntity(FMLClientHandler.instance().getClient().theWorld, itemStack);

        if (trackingEntity != null)
        {
            par3List.add(LangUtils.localize("info.tracker.tracking") + " " + trackingEntity.getCommandSenderName());
        }

        par3List.add(LangUtils.localize("info.tracker.tooltip"));
    }

    @Override
    public void setTrackingEntity(ItemStack itemStack, Entity entity)
    {
        if (itemStack.stackTagCompound == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        if (entity != null)
        {
            itemStack.stackTagCompound.setInteger("trackingEntity", entity.getEntityId());
        }
    }

    @Override
    public Entity getTrackingEntity(World worldObj, ItemStack itemStack)
    {
        if (worldObj != null)
        {
            if (itemStack.stackTagCompound != null)
            {
                int trackingID = itemStack.stackTagCompound.getInteger("trackingEntity");
                return worldObj.getEntityByID(trackingID);
            }
        }
        return null;
    }

    @Override
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        super.onCreated(par1ItemStack, par2World, par3EntityPlayer);
        setTrackingEntity(par1ItemStack, par3EntityPlayer);
    }

    @Override
    public void onUpdate(ItemStack itemStack, World par2World, Entity par3Entity, int par4, boolean par5)
    {
        super.onUpdate(itemStack, par2World, par3Entity, par4, par5);

        if (par3Entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) par3Entity;

            if (player.inventory.getCurrentItem() != null)
            {
                if (player.inventory.getCurrentItem().getItem() == this && par2World.getWorldTime() % 20 == 0)
                {
                    Entity trackingEntity = this.getTrackingEntity(par2World, itemStack);

                    if (trackingEntity != null)
                    {
                        if (getEnergy(itemStack) < ENERGY_PER_TICK)
                        {
                            this.setTrackingEntity(itemStack, null);
                        }
                        
                        setEnergy(itemStack, getEnergy(itemStack) - ENERGY_PER_TICK);
                    }
                }
            }
        }
    }

    /** Called when the player Left Clicks (attacks) an entity. Processed before damage is done, if
     * return value is true further processing is canceled and the entity is not attacked.
     * 
     * @param itemStack The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction. */
    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity)
    {
        if (!player.worldObj.isRemote)
        {
            if (this.getEnergy(itemStack) > ENERGY_PER_TICK)
            {
                setTrackingEntity(itemStack, entity);
                player.addChatMessage(new ChatComponentText(LangUtils.localize("message.tracker.nowtrack") + " " + entity.getCommandSenderName()));
                return true;
            }
            else
            {
                player.addChatMessage(new ChatComponentText(LangUtils.localize("message.tracker.nopower")));
            }
        }

        return false;
    }
}
