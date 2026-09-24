package mekanism.nuclear.common.content.fission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import mekanism.nuclear.common.tile.TileEntityFissionReactorPort;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Defers structural revalidation until a relevant block actually changes. */
public final class FissionReactorFormationManager {

    public static final FissionReactorFormationManager INSTANCE = new FissionReactorFormationManager();

    private final Set<TileEntityFissionReactorPort> loadedPorts = Collections.newSetFromMap(new IdentityHashMap<>());

    private FissionReactorFormationManager() {
    }

    public void register(TileEntityFissionReactorPort port) {
        loadedPorts.add(port);
    }

    public void unregister(TileEntityFissionReactorPort port) {
        loadedPorts.remove(port);
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        structureChanged(event.getWorld(), event.getPos());
    }

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent event) {
        structureChanged(event.getWorld(), event.getPos());
    }

    @SubscribeEvent
    public void onChunkLoaded(ChunkEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }
        int chunkX = event.getChunk().x;
        int chunkZ = event.getChunk().z;
        for (TileEntityFissionReactorPort port : new ArrayList<>(loadedPorts)) {
            if (!port.isInvalid() && port.getWorld() == world && port.watchesChunk(chunkX, chunkZ)) {
                port.queueValidation();
            }
        }
    }

    private void structureChanged(World world, BlockPos pos) {
        if (world.isRemote) {
            return;
        }
        for (TileEntityFissionReactorPort port : new ArrayList<>(loadedPorts)) {
            if (!port.isInvalid() && port.getWorld() == world && port.watches(pos)) {
                port.queueValidation();
            }
        }
    }
}
