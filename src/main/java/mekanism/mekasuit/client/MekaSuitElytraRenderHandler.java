package mekanism.mekasuit.client;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Lazily attaches the custom wing layer to both normal and slim player renderers. */
@SideOnly(Side.CLIENT)
public final class MekaSuitElytraRenderHandler {

    public static final MekaSuitElytraRenderHandler INSTANCE = new MekaSuitElytraRenderHandler();

    private final Set<RenderPlayer> configured = Collections.newSetFromMap(new IdentityHashMap<>());

    private MekaSuitElytraRenderHandler() {
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        RenderPlayer renderer = event.getRenderer();
        if (configured.add(renderer)) {
            renderer.addLayer(new LayerMekaSuitElytra(renderer));
        }
    }
}
