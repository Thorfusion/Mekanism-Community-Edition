package mekanism.ultimate.client;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Lazily attaches the reinforced Elytra layer to normal and slim player renderers. */
public final class HDPEElytraRenderHandler {

    public static final HDPEElytraRenderHandler INSTANCE = new HDPEElytraRenderHandler();
    private final Set<RenderPlayer> configured = Collections.newSetFromMap(new IdentityHashMap<>());

    private HDPEElytraRenderHandler() {
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        RenderPlayer renderer = event.getRenderer();
        if (configured.add(renderer)) {
            renderer.addLayer(new LayerHDPEElytra(renderer));
        }
    }
}
