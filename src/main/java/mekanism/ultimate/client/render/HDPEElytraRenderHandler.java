package mekanism.ultimate.client.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mekanism.ultimate.common.UltimateItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

/** Renders the stable HDPE wing texture for the reinforced Elytra. */
@SideOnly(Side.CLIENT)
public final class HDPEElytraRenderHandler
{
    public static final HDPEElytraRenderHandler INSTANCE =
          new HDPEElytraRenderHandler();

    private HDPEElytraRenderHandler()
    {
    }

    @SubscribeEvent
    public void onRenderSpecials(RenderPlayerEvent.Specials.Post event)
    {
        ItemStack chest = event.entityPlayer.getCurrentArmor(2);
        if (chest != null && chest.getItem() == UltimateItems.HDPEElytra)
        {
            EtFuturumElytraRenderer.render(event.entityPlayer, chest,
                  event.partialRenderTick);
        }
    }
}
