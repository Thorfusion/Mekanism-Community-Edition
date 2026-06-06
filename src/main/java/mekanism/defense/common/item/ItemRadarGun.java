package defense.common.item;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.Pos3D;
import mekanism.common.Mekanism;
import mekanism.common.item.ItemEnergized;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.common.CreativeTabHandler;
import defense.common.DefenseTech;
import defense.common.DefenseTechBlocks;
import defense.common.Reference;
import defense.common.base.IItemPacket;
import defense.common.network.PacketItem.ItemMessage;
import defense.common.tile.TileCruiseLauncher;
import defense.common.tile.TileLauncherPrefab;
import defense.common.tile.TileLauncherScreen;

public class ItemRadarGun extends ItemEnergized implements IItemPacket
{
	public static final int YONG_DIAN_LIANG = 1000;
    public static final int JU_LI = 1000;
    
    public ItemRadarGun()
    {
        super(2000000);
        
        this.setUnlocalizedName("radarGun");
        this.setCreativeTab(CreativeTabHandler.INSTANCE);
        this.setTextureName(Reference.PREFIX + "radarGun");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
       itemIcon = reg.registerIcon(getIconString());
    }

    /** Allows items to add custom lines of information to the mouseover description */
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List par3List, boolean par4)
    {
        Pos3D coord = getLink(itemStack);
        par3List.add("\uaa74" + LangUtils.localize("info.radarGun.savedCoords"));
        par3List.add(LangUtils.localize("gui.misc.x") + " " + (int)coord.xPos + ", " + LangUtils.localize("gui.misc.y") + " " + (int)coord.yPos + ", " + LangUtils.localize("gui.misc.z") + " " + (int)coord.zPos);
        par3List.add((int) new Pos3D(entityPlayer).distance(coord) + " " + LangUtils.localize("info.radarGun.meters") + " (" + (int)(new Pos3D(entityPlayer).xPos - coord.xPos) + ", " + (int) (new Pos3D(entityPlayer).yPos - coord.yPos) + ", " + (int) (new Pos3D(entityPlayer).zPos - coord.zPos) + ")");

		super.addInformation(itemStack, entityPlayer, par3List, par4);
    }

    /** Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
     * world, entityPlayer */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World par2World, EntityPlayer entityPlayer)
    {
        if (par2World.isRemote)
        {
            MovingObjectPosition objectMouseOver = entityPlayer.rayTrace(JU_LI, 1);

            if (objectMouseOver != null)
            {
                TileEntity tileEntity = par2World.getTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

                // Do not scan if the target is a
                // missile launcher
                if(!(tileEntity instanceof TileLauncherPrefab))
                {
                    // Check for electricity
                    if(this.getEnergy(itemStack) > YONG_DIAN_LIANG)
                    {
                    	ArrayList data = new ArrayList();
                    	
                    	data.add(objectMouseOver.blockX);
                    	data.add(objectMouseOver.blockY);
                    	data.add(objectMouseOver.blockZ);
                    	
                    	DefenseTech.netHandler.sendToServer(new ItemMessage(data));
                        setEnergy(itemStack, getEnergy(itemStack) - YONG_DIAN_LIANG);
                        entityPlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.radarGun.scanned").replaceAll("%x", "" + objectMouseOver.blockX).replace("%y", "" + objectMouseOver.blockY).replaceAll("%z", "" + objectMouseOver.blockZ).replaceAll("%s", "" + Math.round(new Pos3D(entityPlayer).distance(new Pos3D(objectMouseOver))))));
                    }
                    else {
                        entityPlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.radarGun.nopower")));
                    }
                }
            }
        }

        return itemStack;
    }

    /** Callback for item usage. If the item does something special on right clicking, he will have
     * one of those. Return True if something happen and false if it don't. This is for ITEMS, not
     * BLOCKS ! */
    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        Block block = par3World.getBlock(x, y, z);
        int blockMetadata = par3World.getBlockMetadata(x, y, z);

        if (block == DefenseTechBlocks.blockMachine)
        {
            TileEntity tileEntity = par3World.getTileEntity(x, y, z);

            if (tileEntity != null)
            {
                if (tileEntity instanceof TileLauncherScreen)
                {
                    TileLauncherScreen missileLauncher = (TileLauncherScreen) tileEntity;

                    Pos3D savedCords = this.getLink(par1ItemStack);

                    // If the vector is NOT 0
                    if (!savedCords.equals(new Pos3D()))
                    {
                        if (missileLauncher.getTarget() == null)
                        {
                            missileLauncher.setTarget(new Pos3D());
                        }

                        missileLauncher.getTarget().xPos = (int) savedCords.xPos;
                        missileLauncher.getTarget().zPos = (int) savedCords.zPos;

                        if (par3World.isRemote)
                        {
                        	ArrayList data = new ArrayList();
                        	
                        	data.add(2);
                        	data.add((int)savedCords.xPos);
                        	data.add((int)missileLauncher.getTarget().yPos);
                        	data.add((int)savedCords.zPos);
                        	
                        	Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(missileLauncher), data));
                            par2EntityPlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.radarGun.transfer")));
                        }
                    }
                    else
                    {
                        if (par3World.isRemote)
                            par2EntityPlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.radarGun.noCoords")));
                    }
                }
                else if (tileEntity instanceof TileCruiseLauncher)
                {
                    TileCruiseLauncher missileLauncher = (TileCruiseLauncher) tileEntity;

                    Pos3D savedCords = this.getLink(par1ItemStack);

                    if (!savedCords.equals(new Pos3D()))
                    {
                        if (missileLauncher.getTarget() == null)
                        {
                            missileLauncher.setTarget(new Pos3D());
                        }

                        missileLauncher.setTarget(savedCords.clone());

                        if (par3World.isRemote)
                        {
                        	ArrayList data = new ArrayList();
                        	
                        	data.add(2);
                        	data.add((int)savedCords.xPos);
                        	data.add((int)missileLauncher.getTarget().yPos);
                        	data.add((int)savedCords.zPos);
                        	
                        	Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(missileLauncher), data));
                            par2EntityPlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.radarGun.transfer")));
                        }
                    }
                    else
                    {
                        if (par3World.isRemote)
                        {
                            par2EntityPlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.radarGun.noCoords")));
                        }
                    }
                }
            }
        }

        return false;
    }

    public void setLink(ItemStack itemStack, Pos3D position)
    {
        // Saves the frequency in the ItemStack
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        position.write(itemStack.getTagCompound());
    }

    public Pos3D getLink(ItemStack itemStack)
    {
        if (itemStack.getTagCompound() == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        return Pos3D.read(itemStack.getTagCompound());
    }

    @Override
    public void handlePacket(EntityPlayer player, ByteBuf dataStream)
    {
        ItemStack itemStack = (ItemStack)player.getCurrentEquippedItem();
        this.setLink(itemStack, new Pos3D(dataStream.readInt(), dataStream.readInt(), dataStream.readInt()));
        
        if (itemStack.getItem() instanceof ItemEnergized)
        {
        	ItemEnergized item = (ItemEnergized)itemStack.getItem();
        	
            item.setEnergy(itemStack, item.getEnergy(itemStack) - ItemRadarGun.YONG_DIAN_LIANG);
        }
    }
}
