package mekanism.mekasuit.client;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.IdentityHashMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;

/** Hides the vanilla head and hat while a MekaSuit helmet is worn. */
@SideOnly(Side.CLIENT)
public final class MekaSuitSkinLayerHandler
{
    public static final MekaSuitSkinLayerHandler INSTANCE =
          new MekaSuitSkinLayerHandler();

    private final Map<EntityPlayer, Deque<VisibilityState>> previousVisibility =
          new IdentityHashMap<EntityPlayer, Deque<VisibilityState>>();

    private MekaSuitSkinLayerHandler()
    {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderLivingPre(RenderLivingEvent.Pre event)
    {
        if (!(event.entity instanceof EntityPlayer)
              || !(event.renderer instanceof RenderPlayer))
        {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entity;
        ItemStack helmet = player.getCurrentArmor(3);
        if (helmet == null
              || !(helmet.getItem() instanceof ItemMekaSuitArmor))
        {
            return;
        }

        ModelRenderer head = ((RenderPlayer)event.renderer)
              .modelBipedMain.bipedHead;
        ModelRenderer headwear = ((RenderPlayer)event.renderer)
              .modelBipedMain.bipedHeadwear;
        Deque<VisibilityState> states = previousVisibility.get(player);
        if (states == null)
        {
            states = new ArrayDeque<VisibilityState>();
            previousVisibility.put(player, states);
        }
        states.push(new VisibilityState(head, headwear));
        head.showModel = false;
        headwear.showModel = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderLivingPost(RenderLivingEvent.Post event)
    {
        if (!(event.entity instanceof EntityPlayer))
        {
            return;
        }
        EntityPlayer player = (EntityPlayer)event.entity;
        Deque<VisibilityState> states = previousVisibility.get(player);
        if (states != null && !states.isEmpty())
        {
            states.pop().restore();
            if (states.isEmpty())
            {
                previousVisibility.remove(player);
            }
        }
    }

    private static final class VisibilityState
    {
        private final ModelRenderer head;
        private final ModelRenderer headwear;
        private final boolean headVisible;
        private final boolean headwearVisible;

        private VisibilityState(ModelRenderer head, ModelRenderer headwear)
        {
            this.head = head;
            this.headwear = headwear;
            headVisible = head.showModel;
            headwearVisible = headwear.showModel;
        }

        private void restore()
        {
            head.showModel = headVisible;
            headwear.showModel = headwearVisible;
        }
    }
}
