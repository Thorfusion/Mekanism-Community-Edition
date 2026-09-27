package mekanism.mekasuit.client;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;
import mekanism.mekasuit.common.item.ItemMekaSuitArmor;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Prevents the player's optional outer skin layers from clipping through worn MekaSuit pieces. */
@SideOnly(Side.CLIENT)
public final class MekaSuitSkinLayerHandler {

    public static final MekaSuitSkinLayerHandler INSTANCE = new MekaSuitSkinLayerHandler();

    private final Map<EntityPlayer, Deque<VisibilityState>> states = new IdentityHashMap<>();

    private MekaSuitSkinLayerHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderLivingPre(RenderLivingEvent.Pre event) {
        if (!(event.getEntity() instanceof EntityPlayer) || !(event.getRenderer() instanceof RenderPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        boolean hideHeadwear = isMekaSuit(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
        boolean hideUpperOuter = isMekaSuit(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
        boolean hideLegOuter = isMekaSuit(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS))
              || isMekaSuit(player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
        if (!hideHeadwear && !hideUpperOuter && !hideLegOuter) {
            return;
        }

        // RenderPlayer fires its player pre-event before setModelVisibilities, so
        // hiding these parts there is immediately undone. RenderLivingEvent.Pre
        // runs from the subsequent super call, after vanilla has reset them.
        ModelPlayer model = ((RenderPlayer) event.getRenderer()).getMainModel();
        states.computeIfAbsent(player, ignored -> new ArrayDeque<>()).push(new VisibilityState(model));
        if (hideHeadwear) {
            model.bipedHeadwear.showModel = false;
        }
        if (hideUpperOuter) {
            model.bipedBodyWear.showModel = false;
            model.bipedLeftArmwear.showModel = false;
            model.bipedRightArmwear.showModel = false;
        }
        if (hideLegOuter) {
            model.bipedLeftLegwear.showModel = false;
            model.bipedRightLegwear.showModel = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (!(event.getEntity() instanceof EntityPlayer) || !(event.getRenderer() instanceof RenderPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntity();
        Deque<VisibilityState> playerStates = states.get(player);
        if (playerStates == null || playerStates.isEmpty()) {
            return;
        }
        playerStates.pop().restore();
        if (playerStates.isEmpty()) {
            states.remove(player);
        }
    }

    private static boolean isMekaSuit(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemMekaSuitArmor;
    }

    private static final class VisibilityState {

        private final ModelPlayer model;
        private final boolean headwear;
        private final boolean bodyWear;
        private final boolean leftArmWear;
        private final boolean rightArmWear;
        private final boolean leftLegWear;
        private final boolean rightLegWear;

        private VisibilityState(ModelPlayer model) {
            this.model = model;
            headwear = model.bipedHeadwear.showModel;
            bodyWear = model.bipedBodyWear.showModel;
            leftArmWear = model.bipedLeftArmwear.showModel;
            rightArmWear = model.bipedRightArmwear.showModel;
            leftLegWear = model.bipedLeftLegwear.showModel;
            rightLegWear = model.bipedRightLegwear.showModel;
        }

        private void restore() {
            model.bipedHeadwear.showModel = headwear;
            model.bipedBodyWear.showModel = bodyWear;
            model.bipedLeftArmwear.showModel = leftArmWear;
            model.bipedRightArmwear.showModel = rightArmWear;
            model.bipedLeftLegwear.showModel = leftLegWear;
            model.bipedRightLegwear.showModel = rightLegWear;
        }
    }
}
