package mekanism.nuclear.common.content.sps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import mekanism.nuclear.common.tile.TileEntitySPSPort;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;

/** Defers SPS revalidation until a relevant block or chunk changes. */
public final class SPSFormationManager
{
    public static final SPSFormationManager INSTANCE =
          new SPSFormationManager();

    private final Set<TileEntitySPSPort> loadedPorts =
          Collections.newSetFromMap(
                new IdentityHashMap<TileEntitySPSPort, Boolean>());

    private SPSFormationManager()
    {
    }

    public void register(TileEntitySPSPort port)
    {
        loadedPorts.add(port);
    }

    public void unregister(TileEntitySPSPort port)
    {
        loadedPorts.remove(port);
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event)
    {
        structureChanged(event.world,
              new SPSPosition(event.x, event.y, event.z));
    }

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent event)
    {
        structureChanged(event.world,
              new SPSPosition(event.x, event.y, event.z));
    }

    @SubscribeEvent
    public void onChunkLoaded(ChunkEvent.Load event)
    {
        World world = event.world;
        if (world.isRemote)
        {
            return;
        }
        int chunkX = event.getChunk().xPosition;
        int chunkZ = event.getChunk().zPosition;
        for (TileEntitySPSPort port
              : new ArrayList<TileEntitySPSPort>(loadedPorts))
        {
            if (!port.isInvalid() && port.getWorldObj() == world
                  && port.watchesChunk(chunkX, chunkZ))
            {
                port.queueValidation();
            }
        }
    }

    private void structureChanged(World world, SPSPosition position)
    {
        if (world.isRemote)
        {
            return;
        }
        for (TileEntitySPSPort port
              : new ArrayList<TileEntitySPSPort>(loadedPorts))
        {
            if (!port.isInvalid() && port.getWorldObj() == world
                  && port.watches(position))
            {
                port.queueValidation();
            }
        }
    }
}
