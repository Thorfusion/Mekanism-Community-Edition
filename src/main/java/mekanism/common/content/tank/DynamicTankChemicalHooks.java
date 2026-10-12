package mekanism.common.content.tank;

import javax.annotation.Nullable;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.common.tile.TileEntityDynamicTank;
import mekanism.common.tile.TileEntityDynamicValve;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

/** Optional boundary between the Core-owned tank and Ultimate chemicals. */
public final class DynamicTankChemicalHooks
{
    /** Stable modern value: 16,000 buckets per tank block. */
    public static final long CHEMICAL_PER_TANK = 16_000_000L;

    private static Bridge bridge;

    private DynamicTankChemicalHooks()
    {
    }

    public static synchronized void register(Bridge implementation)
    {
        if(implementation == null)
        {
            throw new NullPointerException("Dynamic Tank chemical bridge");
        }
        if(bridge != null && bridge != implementation)
        {
            throw new IllegalStateException(
                  "A Dynamic Tank chemical bridge is already registered");
        }
        bridge = implementation;
    }

    public static long getCapacity(int volume)
    {
        return Math.max(0, volume) * CHEMICAL_PER_TANK;
    }

    public static boolean isChemicalContainer(ItemStack stack)
    {
        return bridge != null && bridge.isChemicalContainer(stack);
    }

    public static boolean manageInventory(TileEntityDynamicTank tile,
          SynchronizedTankData data)
    {
        return bridge != null && bridge.manageInventory(tile, data);
    }

    public static boolean manageHeldItem(EntityPlayer player,
          TileEntityDynamicTank tile, ItemStack stack)
    {
        return bridge != null && bridge.manageHeldItem(player, tile, stack);
    }

    @Nullable
    public static Object getChemicalHandler(TileEntityDynamicValve valve)
    {
        return bridge == null || valve.structure == null ? null
              : bridge.getChemicalHandler(valve, valve.structure);
    }

    public static int receiveGas(TileEntityDynamicValve valve,
          ForgeDirection side, GasStack stack, boolean doTransfer)
    {
        return bridge == null ? 0
              : bridge.receiveGas(valve, side, stack, doTransfer);
    }

    @Nullable
    public static GasStack drawGas(TileEntityDynamicValve valve,
          ForgeDirection side, int amount, boolean doTransfer)
    {
        return bridge == null ? null
              : bridge.drawGas(valve, side, amount, doTransfer);
    }

    public static boolean canReceiveGas(TileEntityDynamicValve valve,
          ForgeDirection side, Gas gas)
    {
        return bridge != null && bridge.canReceiveGas(valve, side, gas);
    }

    public static boolean canDrawGas(TileEntityDynamicValve valve,
          ForgeDirection side, Gas gas)
    {
        return bridge != null && bridge.canDrawGas(valve, side, gas);
    }

    public static GasTankInfo[] getGasTankInfo(TileEntityDynamicValve valve)
    {
        return bridge == null ? new GasTankInfo[0]
              : bridge.getGasTankInfo(valve);
    }

    public static String getDisplayName(DynamicTankChemicalStack stack)
    {
        return bridge == null ? stack.registryName
              : bridge.getDisplayName(stack);
    }

    @Nullable
    public static FluidStack getRenderStack(DynamicTankChemicalStack stack)
    {
        return bridge == null ? null : bridge.getRenderStack(stack);
    }

    public interface Bridge
    {
        boolean isChemicalContainer(ItemStack stack);

        boolean manageInventory(TileEntityDynamicTank tile,
              SynchronizedTankData data);

        boolean manageHeldItem(EntityPlayer player,
              TileEntityDynamicTank tile, ItemStack stack);

        Object getChemicalHandler(TileEntityDynamicValve valve,
              SynchronizedTankData data);

        int receiveGas(TileEntityDynamicValve valve, ForgeDirection side,
              GasStack stack, boolean doTransfer);

        GasStack drawGas(TileEntityDynamicValve valve, ForgeDirection side,
              int amount, boolean doTransfer);

        boolean canReceiveGas(TileEntityDynamicValve valve,
              ForgeDirection side, Gas gas);

        boolean canDrawGas(TileEntityDynamicValve valve,
              ForgeDirection side, Gas gas);

        GasTankInfo[] getGasTankInfo(TileEntityDynamicValve valve);

        String getDisplayName(DynamicTankChemicalStack stack);

        FluidStack getRenderStack(DynamicTankChemicalStack stack);
    }
}
