package defense.common.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import mekanism.common.PacketHandler;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import defense.common.base.IItemPacket;
import defense.common.network.PacketItem.ItemMessage;

public class PacketItem implements IMessageHandler<ItemMessage, IMessage>
{
	@Override
	public IMessage onMessage(ItemMessage message, MessageContext context) 
	{
		ItemStack itemstack = PacketHandler.getPlayer(context).getCurrentEquippedItem();
		
		if(itemstack != null && itemstack.getItem() instanceof IItemPacket)
		{
			try {
				((IItemPacket)itemstack.getItem()).handlePacket(PacketHandler.getPlayer(context), message.storedBuffer);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static class ItemMessage implements IMessage
	{
		public ArrayList data;
		
		public ByteBuf storedBuffer = null;
		
		public ItemMessage() {}
	
		public ItemMessage(ArrayList list)
		{
			data = list;
		}
	
		@Override
		public void toBytes(ByteBuf dataStream)
		{
			PacketHandler.encode(data.toArray(), dataStream);
		}
	
		@Override
		public void fromBytes(ByteBuf dataStream)
		{
			storedBuffer = dataStream.copy();
		}
	}
}
