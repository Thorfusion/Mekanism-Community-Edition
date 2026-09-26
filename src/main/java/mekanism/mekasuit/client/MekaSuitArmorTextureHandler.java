package mekanism.mekasuit.client;

import mekanism.mekasuit.common.MekanismMekaSuit;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Stitches the material textures referenced by the stable worn-suit OBJ. */
@SideOnly(Side.CLIENT)
public final class MekaSuitArmorTextureHandler {

    public static final MekaSuitArmorTextureHandler INSTANCE = new MekaSuitArmorTextureHandler();

    private MekaSuitArmorTextureHandler() {
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        register(event, "mekasuit_player");
        register(event, "mekasuit_armor_body");
        register(event, "mekasuit_armor_helmet");
        register(event, "mekasuit_armor_exoskeleton");
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        ModelMekaSuitArmor.warmUp();
    }

    private static void register(TextureStitchEvent.Pre event, String name) {
        event.getMap().registerSprite(new ResourceLocation(
              MekanismMekaSuit.MODID, "entity/armor/" + name));
    }
}
