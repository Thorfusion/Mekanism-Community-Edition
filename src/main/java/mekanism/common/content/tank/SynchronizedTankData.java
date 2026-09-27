package mekanism.common.content.tank;

import java.util.HashSet;
import java.util.Set;
import mekanism.api.Coord4D;
import mekanism.common.multiblock.SynchronizedData;
import mekanism.common.util.FluidContainerUtils.ContainerEditMode;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

public class SynchronizedTankData extends SynchronizedData<SynchronizedTankData> {

    public FluidStack fluidStored;

    /** Long-backed chemical contents; mutually exclusive with fluidStored. */
    public DynamicTankChemicalStack chemicalStored;

    /**
     * For use by rendering segment
     */
    public FluidStack prevFluid;
    public DynamicTankChemicalStack prevChemical;
    public int prevFluidStage = 0;

    public ContainerEditMode editMode = ContainerEditMode.BOTH;

    public NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    public Set<ValveData> valves = new HashSet<>();

    public boolean needsRenderUpdate() {
        if ((chemicalStored == null && prevChemical != null) || (chemicalStored != null && prevChemical == null)) {
            return true;
        }
        if (chemicalStored != null) {
            int totalStage = (volHeight - 2) * (TankUpdateProtocol.FLUID_PER_TANK / 100);
            int currentStage = (int) ((chemicalStored.amount / (double) DynamicTankChemicalHooks.getCapacity(volume)) * totalStage);
            boolean stageChanged = currentStage != prevFluidStage;
            prevFluidStage = currentStage;
            return prevChemical == null || !chemicalStored.isSameType(prevChemical) || stageChanged;
        }
        if ((fluidStored == null && prevFluid != null) || (fluidStored != null && prevFluid == null)) {
            return true;
        }
        if (fluidStored != null) {
            int totalStage = (volHeight - 2) * (TankUpdateProtocol.FLUID_PER_TANK / 100);
            int currentStage = (int) ((fluidStored.amount / (float) (volume * TankUpdateProtocol.FLUID_PER_TANK)) * totalStage);
            boolean stageChanged = currentStage != prevFluidStage;
            prevFluidStage = currentStage;
            return (fluidStored.getFluid() != prevFluid.getFluid()) || stageChanged;
        }
        return false;
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    public static class ValveData {

        public EnumFacing side;
        public Coord4D location;

        public boolean prevActive;
        public int activeTicks;

        public void onTransfer() {
            activeTicks = 30;
        }

        @Override
        public int hashCode() {
            int code = 1;
            code = 31 * code + side.ordinal();
            code = 31 * code + location.hashCode();
            return code;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ValveData && ((ValveData) obj).side == side && ((ValveData) obj).location.equals(location);
        }
    }
}
