package mekanism.common.item;

import java.util.List;

import mekanism.api.EnumColor;
import mekanism.api.IConfigCardAccess;
import mekanism.api.IConfigCardAccess.ISpecialConfigData;
import mekanism.api.Coord4D;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.IRedstoneControl.RedstoneControl;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.base.ITileNetwork;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityContainerBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemConfigurationCard extends ItemMekanism
{
	private static final int DATA_VERSION = 2;

	public ItemConfigurationCard()
	{
		super();
		
		setMaxStackSize(1);
	}
	
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag)
	{
		super.addInformation(itemstack, entityplayer, list, flag);
		
		list.add(EnumColor.GREY + LangUtils.localize("gui.data") + ": " + EnumColor.INDIGO + LangUtils.localize(getDataType(itemstack)));
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			
			if(tileEntity instanceof IConfigCardAccess)
			{
				if(!SecurityUtils.canAccess(player, tileEntity))
				{
					SecurityUtils.displayNoAccess(player);
					return true;
				}

				if(player.isSneaking())
				{
					NBTTagCompound data = getBaseData(tileEntity);
					
					if(tileEntity instanceof ISpecialConfigData)
					{
						data = ((ISpecialConfigData)tileEntity).getConfigurationData(data);
					}
					
					if(data != null)
					{
						sanitizeConfigurationData(data);
						data.setInteger("dataVersion", DATA_VERSION);
						data.setString("dataType", getNameFromTile(tileEntity));
						data.setString("sourceBlock",
								tileEntity.getBlockType().getUnlocalizedName());
						setData(stack, data);
						player.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism] " + EnumColor.GREY + LangUtils.localize("tooltip.configurationCard.got").replaceAll("%s", EnumColor.INDIGO + LangUtils.localize(data.getString("dataType")) + EnumColor.GREY)));
					}
					
					return true;
				}
				else if(getData(stack) != null)
				{
					if(getNameFromTile(tileEntity).equals(getDataType(stack)))
					{
						NBTTagCompound data = getData(stack);
						sanitizeConfigurationData(data);
						setBaseData(data, tileEntity);
						
						if(tileEntity instanceof ISpecialConfigData)
						{
							((ISpecialConfigData)tileEntity).setConfigurationData(data);
						}
						tileEntity.markDirty();
						updateTile(tileEntity);
						
						player.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism] " + EnumColor.DARK_GREEN + LangUtils.localize("tooltip.configurationCard.set").replaceAll("%s", EnumColor.INDIGO + LangUtils.localize(getDataType(stack)) + EnumColor.DARK_GREEN)));
					}
					else {
						player.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE + "[Mekanism] " + EnumColor.RED + LangUtils.localize("tooltip.configurationCard.unequal") + "."));
					}
					
					return true;
				}
			}
		}
		
		return false;
	}

	/** Sneak-use in the air clears encoded data without consuming the card. */
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,
			EntityPlayer player)
	{
		if(player.isSneaking() && getData(stack) != null && !world.isRemote)
		{
			setData(stack, null);
			player.addChatMessage(new ChatComponentText(EnumColor.DARK_BLUE
					+ "[Mekanism] " + EnumColor.GREY
					+ LangUtils.localize(
							"tooltip.configurationCard.cleared")));
		}
		return stack;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void updateTile(TileEntity tile)
	{
		if(tile instanceof ITileNetwork)
		{
			ITileNetwork network = (ITileNetwork)tile;
			Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(
					Coord4D.get(tile), network.getNetworkedData(
							new java.util.ArrayList())),
					new Range4D(Coord4D.get(tile)));
		}
	}
	
	private NBTTagCompound getBaseData(TileEntity tile)
	{
		NBTTagCompound nbtTags = new NBTTagCompound();
		
		if(tile instanceof IRedstoneControl)
		{
			nbtTags.setInteger("controlType", ((IRedstoneControl)tile).getControlType().ordinal());
		}
		
		if(tile instanceof ISideConfiguration)
		{
			((ISideConfiguration)tile).getConfig().write(nbtTags);
			((ISideConfiguration)tile).getEjector().write(nbtTags);
		}
		
		return nbtTags;
	}
	
	private void setBaseData(NBTTagCompound nbtTags, TileEntity tile)
	{
		if(tile instanceof IRedstoneControl)
		{
			int control = nbtTags.getInteger("controlType");
			if(nbtTags.hasKey("controlType") && control >= 0
					&& control < RedstoneControl.values().length)
			{
				((IRedstoneControl)tile).setControlType(
						RedstoneControl.values()[control]);
			}
		}
		
		if(tile instanceof ISideConfiguration)
		{
			((ISideConfiguration)tile).getConfig().read(nbtTags);
			((ISideConfiguration)tile).getEjector().read(nbtTags);
		}
	}
	
	private String getNameFromTile(TileEntity tile)
	{
		String ret = Integer.toString(tile.hashCode());
		
		if(tile instanceof TileEntityContainerBlock)
		{
			ret = tile.getBlockType().getUnlocalizedName() + "." + ((TileEntityContainerBlock)tile).fullName + ".name";
		}
		
		if(tile instanceof ISpecialConfigData)
		{
			ret = ((ISpecialConfigData)tile).getDataType();
		}
		
		return ret;
	}
	
	public void setData(ItemStack itemstack, NBTTagCompound data)
	{
		if(itemstack.stackTagCompound == null)
		{
			itemstack.setTagCompound(new NBTTagCompound());
		}

		if(data != null)
		{
			itemstack.stackTagCompound.setTag("data", data);
		}
		else {
			itemstack.stackTagCompound.removeTag("data");
		}
	}

	public NBTTagCompound getData(ItemStack itemstack)
	{
		if(itemstack.stackTagCompound == null)
		{
			return null;
		}
		
		NBTTagCompound data = itemstack.stackTagCompound.getCompoundTag("data");
		
		if(data.hasNoTags())
		{
			return null;
		}
		else {
			return itemstack.stackTagCompound.getCompoundTag("data");
		}
	}

	public String getDataType(ItemStack itemstack)
	{
		NBTTagCompound data = getData(itemstack);
		
		if(data != null)
		{
			return data.getString("dataType");
		}
		
		return "gui.none";
	}

	private static void sanitizeConfigurationData(NBTTagCompound data)
	{
		data.removeTag("owner");
		data.removeTag("ownerUUID");
		data.removeTag("trusted");
		data.removeTag("security");
		data.removeTag("securityMode");
	}
}
