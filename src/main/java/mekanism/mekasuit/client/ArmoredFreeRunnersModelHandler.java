package mekanism.mekasuit.client;

import mekanism.client.render.item.ItemLayerWrapper;
import mekanism.mekasuit.common.MekaSuitItems;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Converts the Armored Free Runners item model into a built-in 3D renderer. */
public final class ArmoredFreeRunnersModelHandler {

    public static final ArmoredFreeRunnersModelHandler INSTANCE = new ArmoredFreeRunnersModelHandler();

    private ArmoredFreeRunnersModelHandler() {
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        ModelResourceLocation location = new ModelResourceLocation(
              MekaSuitItems.ArmoredFreeRunners.getRegistryName(), "inventory");
        IBakedModel baked = event.getModelRegistry().getObject(location);
        if (baked != null) {
            RenderArmoredFreeRunners.model = new ItemLayerWrapper(baked);
            event.getModelRegistry().putObject(location, RenderArmoredFreeRunners.model);
        }
    }
}
