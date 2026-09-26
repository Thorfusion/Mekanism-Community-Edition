package mekanism.mekasuit.common.content.gear;

import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.common.MekaSuitItems;
import mekanism.mekasuit.common.item.ItemMekaModule;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Recovers installed units when an abandoned modular item expires in the world. */
public final class MekaSuitModuleDropHandler {

    public static final MekaSuitModuleDropHandler INSTANCE = new MekaSuitModuleDropHandler();

    private MekaSuitModuleDropHandler() {
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
        EntityItem entity = event.getEntityItem();
        ItemStack host = entity.getItem();
        if (entity.world.isRemote || host.isEmpty() || !(host.getItem() instanceof IModuleContainerItem)) {
            return;
        }
        IModuleContainerItem item = (IModuleContainerItem) host.getItem();
        for (ModuleData module : ModuleContainer.fromStack(host, item.getModuleTarget()).getModules()) {
            ItemMekaModule moduleItem = MekaSuitItems.getModuleItem(module.getType());
            if (moduleItem != null) {
                EntityItem drop = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ,
                      new ItemStack(moduleItem, module.getInstalledCount()));
                drop.setDefaultPickupDelay();
                entity.world.spawnEntity(drop);
            }
        }
    }
}
