package mekanism.mekasuit.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.mekasuit.common.content.gear.MekaSuitElytraHelper;
import mekanism.ultimate.client.render.EtFuturumElytraRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

/** Renders HDPE wings for an enabled MekaSuit Elytra Unit. */
@SideOnly(Side.CLIENT)
public final class MekaSuitElytraRenderHandler
{
    public static final MekaSuitElytraRenderHandler INSTANCE =
          new MekaSuitElytraRenderHandler();

    private MekaSuitElytraRenderHandler()
    {
    }

    @SubscribeEvent
    public void onRenderSpecials(RenderPlayerEvent.Specials.Post event)
    {
        ItemStack chest = event.entityPlayer.getCurrentArmor(2);
        if (MekaSuitElytraHelper.shouldRenderWings(chest))
        {
            EtFuturumElytraRenderer.render(event.entityPlayer, chest,
                  event.partialRenderTick);
        }
    }
}
