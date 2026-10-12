package mekanism.mekasuit.common.content.gear;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mekanism.mekasuit.api.gear.IModuleContainerItem;
import mekanism.mekasuit.api.gear.ModuleData;
import mekanism.mekasuit.common.item.ItemMekaModule;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Recovers installed units when an abandoned modular item expires. */
public final class MekaSuitModuleDropHandler
{
    public static final MekaSuitModuleDropHandler INSTANCE =
          new MekaSuitModuleDropHandler();

    private MekaSuitModuleDropHandler()
    {
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event)
    {
        EntityItem entity = event.entityItem;
        if (entity == null || entity.worldObj == null
              || entity.worldObj.isRemote)
        {
            return;
        }
        for (ItemStack recovered : collectModuleDrops(entity.getEntityItem()))
        {
            EntityItem drop = new EntityItem(entity.worldObj, entity.posX,
                  entity.posY, entity.posZ, recovered);
            drop.delayBeforeCanPickup = 10;
            entity.worldObj.spawnEntityInWorld(drop);
        }
    }

    static List<ItemStack> collectModuleDrops(ItemStack host)
    {
        if (host == null
              || !(host.getItem() instanceof IModuleContainerItem))
        {
            return Collections.emptyList();
        }
        IModuleContainerItem item = (IModuleContainerItem)host.getItem();
        List<ItemStack> recovered = new ArrayList<ItemStack>();
        for (ModuleData module : ModuleContainer.fromStack(host,
              item.getModuleTarget()).getModules())
        {
            ItemMekaModule moduleItem = ItemMekaModule.getFor(
                  module.getType());
            if (moduleItem != null)
            {
                recovered.add(new ItemStack(moduleItem,
                      module.getInstalledCount()));
            }
        }
        return recovered;
    }
}
