package mekanism.nuclear.common.content.fission;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;

/** Defers structural revalidation until a relevant block actually changes. */
public final class FissionReactorFormationManager
{
    public static final FissionReactorFormationManager INSTANCE =
          new FissionReactorFormationManager();

    private final Set<TileEntityFissionReactorPort> loadedPorts =
          Collections.newSetFromMap(
                new IdentityHashMap<TileEntityFissionReactorPort, Boolean>());

    private FissionReactorFormationManager()
    {
    }

    public void register(TileEntityFissionReactorPort port)
    {
        loadedPorts.add(port);
    }

    public void unregister(TileEntityFissionReactorPort port)
    {
        loadedPorts.remove(port);
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event)
    {
        structureChanged(event.world,
              new FissionReactorPosition(event.x, event.y, event.z));
    }

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent event)
    {
        structureChanged(event.world,
              new FissionReactorPosition(event.x, event.y, event.z));
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
        for (TileEntityFissionReactorPort port
              : new ArrayList<TileEntityFissionReactorPort>(loadedPorts))
        {
            if (!port.isInvalid() && port.getWorldObj() == world
                  && port.watchesChunk(chunkX, chunkZ))
            {
                port.queueValidation();
            }
        }
    }

    private void structureChanged(World world,
          FissionReactorPosition position)
    {
        if (world.isRemote)
        {
            return;
        }
        for (TileEntityFissionReactorPort port
              : new ArrayList<TileEntityFissionReactorPort>(loadedPorts))
        {
            if (!port.isInvalid() && port.getWorldObj() == world
                  && port.watches(position))
            {
                port.queueValidation();
            }
        }
    }
}
