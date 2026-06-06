package defense.common.base;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public interface IItemPacket 
{
	public void handlePacket(EntityPlayer player, ByteBuf dataStream);
}
