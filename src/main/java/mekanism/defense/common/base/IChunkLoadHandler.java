package defense.common.base;

import net.minecraftforge.common.ForgeChunkManager.Ticket;

public interface IChunkLoadHandler
{
    public void chunkLoaderInit(Ticket ticket);
}
