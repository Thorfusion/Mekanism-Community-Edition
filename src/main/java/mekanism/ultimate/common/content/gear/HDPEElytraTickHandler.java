package mekanism.ultimate.common.content.gear;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import mekanism.ultimate.common.UltimateItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

/** Adapts the custom reinforced Elytra to 1.12's vanilla-item-only flight tick. */
public final class HDPEElytraTickHandler {

    public static final HDPEElytraTickHandler INSTANCE = new HDPEElytraTickHandler();

    @SuppressWarnings("deprecation")
    private static final Field ARMOR_CACHE = ReflectionHelper.findField(
          EntityLivingBase.class, "armorArray", "field_184631_bt", "bw");

    private final Map<UUID, SwapData> serverSwaps = new ConcurrentHashMap<>();
    private final Map<UUID, SwapData> clientSwaps = new ConcurrentHashMap<>();

    private HDPEElytraTickHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerTickStart(PlayerTickEvent event) {
        if (event.phase != Phase.START) {
            return;
        }
        Map<UUID, SwapData> swaps = swaps(event.side);
        SwapData stale = swaps.remove(event.player.getUniqueID());
        if (stale != null) {
            restore(event.player, stale);
        }
        ItemStack chest = event.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!event.player.isElytraFlying() || chest.getItem() != UltimateItems.HDPEElytra
              || !net.minecraft.item.ItemElytra.isUsable(chest)) {
            return;
        }

        ItemStack proxy = new ItemStack(Items.ELYTRA);
        if (chest.hasTagCompound()) {
            // Preserve enchantments so Unbreaking is evaluated exactly once on the vanilla proxy.
            proxy.setTagCompound(chest.getTagCompound().copy());
        }
        proxy.setItemDamage(0);
        ItemStack cached = event.side == Side.SERVER
              ? armorCache(event.player).get(chestIndex()).copy() : ItemStack.EMPTY;
        SwapData data = new SwapData(chest, proxy, cached);
        swaps.put(event.player.getUniqueID(), data);
        setChestSilently(event.player, proxy);
        if (event.side == Side.SERVER) {
            armorCache(event.player).set(chestIndex(), proxy.copy());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTickEnd(PlayerTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        SwapData data = swaps(event.side).remove(event.player.getUniqueID());
        if (data != null) {
            restore(event.player, data);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        SwapData data = serverSwaps.remove(event.player.getUniqueID());
        if (data != null) {
            restore(event.player, data);
        }
        clientSwaps.remove(event.player.getUniqueID());
    }

    private static void restore(EntityPlayer player, SwapData data) {
        ItemStack current = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (current == data.proxy) {
            setChestSilently(player, data.original);
            if (!player.world.isRemote) {
                applyUse(data);
                armorCache(player).set(chestIndex(), data.cachedOriginal);
            }
        } else if (current != data.original && !data.original.isEmpty()) {
            if (!player.inventory.addItemStackToInventory(data.original)) {
                player.dropItem(data.original, false);
            }
            if (!player.world.isRemote) {
                armorCache(player).set(chestIndex(), current.isEmpty() ? ItemStack.EMPTY : current.copy());
            }
        }
    }

    private static void applyUse(SwapData data) {
        int used = data.proxy.getItemDamage();
        if (used > 0) {
            int maximumUsableDamage = Math.max(0, data.original.getMaxDamage() - 1);
            data.original.setItemDamage(Math.min(maximumUsableDamage,
                  data.original.getItemDamage() + used));
        }
    }

    private static Map<UUID, SwapData> swaps(Side side) {
        return side == Side.CLIENT ? INSTANCE.clientSwaps : INSTANCE.serverSwaps;
    }

    @SuppressWarnings("unchecked")
    private static NonNullList<ItemStack> armorCache(EntityPlayer player) {
        try {
            return (NonNullList<ItemStack>) ARMOR_CACHE.get(player);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to access the 1.12 living-equipment cache", e);
        }
    }

    private static int chestIndex() {
        return EntityEquipmentSlot.CHEST.getIndex();
    }

    private static void setChestSilently(EntityPlayer player, ItemStack stack) {
        player.inventory.armorInventory.set(chestIndex(), stack);
    }

    private static final class SwapData {
        private final ItemStack original;
        private final ItemStack proxy;
        private final ItemStack cachedOriginal;

        private SwapData(ItemStack original, ItemStack proxy, ItemStack cachedOriginal) {
            this.original = original;
            this.proxy = proxy;
            this.cachedOriginal = cachedOriginal;
        }
    }
}
