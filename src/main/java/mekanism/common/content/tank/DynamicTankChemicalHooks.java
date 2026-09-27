package mekanism.common.content.tank;

import javax.annotation.Nullable;
import mekanism.common.tile.TileEntityDynamicTank;
import mekanism.common.tile.TileEntityDynamicValve;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;

/**
 * Optional bridge between the Core-owned Dynamic Tank and a module-owned
 * chemical implementation. Core persists only a stable neutral identity.
 */
public final class DynamicTankChemicalHooks {

    /** Stable 10.7 value: 16,000 buckets of chemical capacity per tank block. */
    public static final long CHEMICAL_PER_TANK = 16_000_000L;

    private static Bridge bridge;

    private DynamicTankChemicalHooks() {
    }

    public static synchronized void register(Bridge implementation) {
        if (implementation == null) {
            throw new NullPointerException("Dynamic Tank chemical bridge");
        }
        if (bridge != null && bridge != implementation) {
            throw new IllegalStateException("A Dynamic Tank chemical bridge is already registered");
        }
        bridge = implementation;
    }

    public static long getCapacity(int volume) {
        return Math.max(0, volume) * CHEMICAL_PER_TANK;
    }

    public static boolean isChemicalContainer(ItemStack stack) {
        return bridge != null && bridge.isChemicalContainer(stack);
    }

    public static boolean manageInventory(TileEntityDynamicTank tile, SynchronizedTankData data) {
        return bridge != null && bridge.manageInventory(tile, data);
    }

    public static boolean manageHeldItem(EntityPlayer player, TileEntityDynamicTank tile,
          EnumHand hand, ItemStack stack) {
        return bridge != null && bridge.manageHeldItem(player, tile, hand, stack);
    }

    public static boolean hasCapability(Capability<?> capability) {
        return bridge != null && bridge.hasCapability(capability);
    }

    @Nullable
    public static <T> T getCapability(TileEntityDynamicValve valve, Capability<T> capability,
          @Nullable EnumFacing side) {
        return bridge == null ? null : bridge.getCapability(valve, capability, side);
    }

    public static String getDisplayName(DynamicTankChemicalStack stack) {
        return bridge == null ? stack.registryName : bridge.getDisplayName(stack);
    }

    @Nullable
    public static FluidStack getRenderStack(DynamicTankChemicalStack stack) {
        return bridge == null ? null : bridge.getRenderStack(stack);
    }

    public interface Bridge {

        boolean isChemicalContainer(ItemStack stack);

        boolean manageInventory(TileEntityDynamicTank tile, SynchronizedTankData data);

        boolean manageHeldItem(EntityPlayer player, TileEntityDynamicTank tile,
              EnumHand hand, ItemStack stack);

        boolean hasCapability(Capability<?> capability);

        @Nullable
        <T> T getCapability(TileEntityDynamicValve valve, Capability<T> capability,
              @Nullable EnumFacing side);

        String getDisplayName(DynamicTankChemicalStack stack);

        @Nullable
        FluidStack getRenderStack(DynamicTankChemicalStack stack);
    }
}
