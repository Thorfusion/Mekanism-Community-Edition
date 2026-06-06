package defense.common.item;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import mekanism.api.EnumColor;
import mekanism.api.Pos3D;
import mekanism.common.item.ItemEnergized;
import mekanism.common.util.LangUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import defense.common.CreativeTabHandler;
import defense.common.DefenseTech;
import defense.common.DefenseTechBlocks;
import defense.common.Reference;
import defense.common.base.IItemPacket;
import defense.common.block.BlockExplosive;
import defense.common.explosive.Explosive;
import defense.common.network.PacketItem.ItemMessage;
import defense.common.tile.TileExplosive;

public class ItemRemoteDetonator extends ItemEnergized implements IItemPacket
{
    public static final int BAN_JING = 100;
    public static final int ENERGY = 1500;

    public ItemRemoteDetonator()
    {
        super(1000000);
        
        setUnlocalizedName("remoteDetonator");
        this.setCreativeTab(CreativeTabHandler.INSTANCE);
        this.setTextureName(Reference.PREFIX + "remoteDetonator");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
       itemIcon = reg.registerIcon(getIconString());
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List par3List, boolean par4)
    {
        Pos3D coord = getSavedCoord(itemStack);

        if (this.isValidExplosive(coord.getCoord(player.worldObj.provider.dimensionId).getTileEntity(player.worldObj)))
        {
            par3List.add("\uaa74" + LangUtils.localize("info.detonator.linked"));
            par3List.add(LangUtils.localize("gui.misc.x") + " " + (int) coord.xPos + ", " + LangUtils.localize("gui.misc.y") + " " + (int) coord.yPos + ", " + LangUtils.localize("gui.misc.z") + " " + (int) coord.zPos);
        }
        else
        {
            par3List.add("\u00a74" + LangUtils.localize("info.detonator.noLink"));
        }
		super.addInformation(itemStack, player, par3List, par4);
    }

    /** Lock the remote to an explosive if it exists. */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (entityPlayer.isSneaking() && tileEntity != null)
        {
            if (this.isValidExplosive(tileEntity))
            {
                // Check for electricity
                if (this.getEnergy(itemStack) > ENERGY)
                {
                    this.setSavedCoords(itemStack, new Pos3D(x, y, z));
                    this.setEnergy(itemStack, this.getEnergy(itemStack) - ENERGY);
                    
                    if (world.isRemote)
                    {
                        entityPlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.detonator.locked").replaceAll("%x", "" + x).replace("%y", "" + y).replace("%z", "" + z)));
                    }
                }
                else if (world.isRemote)
                {
                    entityPlayer.addChatMessage(new ChatComponentText(Reference.CHAT_DESC + EnumColor.GREY + " " + LangUtils.localize("message.detonator.nopower")));
                }

                return true;
            }
        }

        return false;
    }

    /** Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack,
     * world, entityPlayer */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            if (!player.isSneaking())
            {
                MovingObjectPosition objectMouseOver = player.rayTrace(BAN_JING, 1);

                if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectType.BLOCK)
                {

                    TileEntity tileEntity = world.getTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                    Block block = world.getBlock(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
                    TileEntity tile = world.getTileEntity(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);

                    if (tile != null)
                    {
                        if (tile instanceof TileExplosive)
                        {
                            if (block == DefenseTechBlocks.blockMachine)
                            {
                                return itemStack;
                            }
                            else if (this.isValidExplosive(tileEntity))
                            {
                                // Check for electricity
                                if (this.getEnergy(itemStack) > ENERGY)
                                {
                                	ArrayList data = new ArrayList();
                                	
                                	data.add(tile.xCoord);
                                	data.add(tile.yCoord);
                                	data.add(tile.zCoord);
                                	
                                	DefenseTech.netHandler.sendToServer(new ItemMessage(new ArrayList()));
                                    this.setEnergy(itemStack, this.getEnergy(itemStack) - ENERGY);
                                    return itemStack;
                                }
                                else
                                {
                                    player.addChatMessage(new ChatComponentText(LangUtils.localize(Reference.CHAT_DESC + EnumColor.GREY + " " + "message.detonator.nopower")));
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                if (this.getEnergy(itemStack) > ENERGY)
                {
                    TileEntity tileEntity = this.getSavedCoord(itemStack).getCoord(world.provider.dimensionId).getTileEntity(world);

                    if (this.isValidExplosive(tileEntity))
                    {
                    	ArrayList data = new ArrayList();
                    	
                    	data.add(tileEntity.xCoord);
                    	data.add(tileEntity.yCoord);
                    	data.add(tileEntity.zCoord);
                    	
                    	DefenseTech.netHandler.sendToServer(new ItemMessage(data));
                        this.setEnergy(itemStack, this.getEnergy(itemStack) - ENERGY);
                    }
                }
                else
                {
                    player.addChatMessage(new ChatComponentText(LangUtils.localize(Reference.CHAT_DESC + EnumColor.GREY + " " + "message.detonator.nopower")));
                }
            }
        }

        return itemStack;
    }

    public boolean isValidExplosive(TileEntity tileEntity)
    {
        if (tileEntity != null)
        {
            if (tileEntity instanceof TileExplosive)
            {
                return ((TileExplosive) tileEntity).explosiveID == Explosive.condensed.getID() || ((TileExplosive) tileEntity).explosiveID == Explosive.breaching.getID() || ((TileExplosive) tileEntity).explosiveID == Explosive.sMine.getID();
            }
        }

        return false;
    }

    public void setSavedCoords(ItemStack itemStack, Pos3D position)
    {
        if (itemStack.stackTagCompound == null)
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        itemStack.stackTagCompound.setInteger("x", (int)position.xPos);
        itemStack.stackTagCompound.setInteger("y", (int)position.yPos);
        itemStack.stackTagCompound.setInteger("z", (int)position.zPos);
    }

    public Pos3D getSavedCoord(ItemStack par1ItemStack)
    {
        if (par1ItemStack.stackTagCompound == null)
        {
            return new Pos3D();
        }

        return new Pos3D(par1ItemStack.stackTagCompound.getInteger("x"), par1ItemStack.stackTagCompound.getInteger("y"), par1ItemStack.stackTagCompound.getInteger("z"));
    }

    @Override
    public void handlePacket(EntityPlayer player, ByteBuf dataStream)
    {
    	ItemStack stack = player.getCurrentEquippedItem();
    	
        this.setEnergy(stack, this.getEnergy(stack) - ENERGY);
        
        int x = dataStream.readInt();
        int y = dataStream.readInt();
        int z = dataStream.readInt();
        
        if(isValidExplosive(player.worldObj.getTileEntity(x, y, z)))
        {
        	BlockExplosive.detonate(player.worldObj, x, y, z, ((TileExplosive)player.worldObj.getTileEntity(x, y, z)).explosiveID, 0);
        }
    }
}
