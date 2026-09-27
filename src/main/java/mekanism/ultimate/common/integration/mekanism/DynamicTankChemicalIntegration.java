package mekanism.ultimate.common.integration.mekanism;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Coord4D;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasItem;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.tank.DynamicTankChemicalHooks;
import mekanism.common.content.tank.DynamicTankChemicalStack;
import mekanism.common.content.tank.SynchronizedTankData;
import mekanism.common.content.tank.SynchronizedTankData.ValveData;
import mekanism.common.tile.TileEntityDynamicTank;
import mekanism.common.tile.TileEntityDynamicValve;
import mekanism.common.util.FluidContainerUtils.ContainerEditMode;
import mekanism.common.util.MekanismUtils;
import mekanism.ultimate.api.Action;
import mekanism.ultimate.api.chemical.ChemicalKind;
import mekanism.ultimate.api.chemical.IChemicalHandlerCE;
import mekanism.ultimate.api.chemical.IChemicalStackCE;
import mekanism.ultimate.api.chemical.IChemicalTankCE;
import mekanism.ultimate.api.chemical.IChemicalTypeCE;
import mekanism.ultimate.common.UltimateFluids;
import mekanism.ultimate.common.capability.UltimateChemicalCapabilities;
import mekanism.ultimate.common.content.chemical.ChemicalStackCE;
import mekanism.ultimate.common.content.chemical.LongChemicalTank;
import mekanism.ultimate.common.content.chemical.PigmentTypeCE;
import mekanism.ultimate.common.content.chemical.UltimateChemicalRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

/** Ultimate implementation of the neutral Dynamic Tank chemical bridge. */
public final class DynamicTankChemicalIntegration implements DynamicTankChemicalHooks.Bridge {

    public static final DynamicTankChemicalIntegration INSTANCE = new DynamicTankChemicalIntegration();
    private static final long ITEM_TRANSFER_RATE = 1_024;

    private DynamicTankChemicalIntegration() {
    }

    @Override
    public boolean isChemicalContainer(ItemStack stack) {
        return stack != null && !stack.isEmpty() && (getFacadeHandler(stack) != null
              || stack.getItem() instanceof IGasItem);
    }

    @Override
    public boolean manageInventory(TileEntityDynamicTank tile, SynchronizedTankData data) {
        if (tile.getWorld() == null || tile.getWorld().isRemote || data.fluidStored != null
              || data.inventory.get(0).isEmpty() || !isChemicalContainer(data.inventory.get(0))) {
            return false;
        }
        ItemStack input = data.inventory.get(0);
        ItemStack handled = ItemHandlerHelper.copyStackWithSize(input, 1);
        DynamicTankChemicalStack snapshot = copy(data.chemicalStored);
        DynamicTankTankView tank = new DynamicTankTankView(tile, data);
        boolean fillContainer = data.editMode == ContainerEditMode.FILL
              || data.editMode == ContainerEditMode.BOTH && !itemHasChemical(handled);
        long moved = fillContainer ? fillItem(handled, tank) : drainItem(handled, tank);
        if (moved <= 0 || !canAcceptOutput(data.inventory.get(1), handled)) {
            data.chemicalStored = snapshot;
            return false;
        }
        if (data.inventory.get(1).isEmpty()) {
            data.inventory.set(1, handled);
        } else {
            data.inventory.get(1).grow(1);
        }
        input.shrink(1);
        tile.markDirty();
        return true;
    }

    @Override
    public boolean manageHeldItem(EntityPlayer player, TileEntityDynamicTank tile,
          EnumHand hand, ItemStack stack) {
        if (!isChemicalContainer(stack) || tile.structure == null || tile.structure.fluidStored != null) {
            return false;
        }
        if (tile.getWorld().isRemote) {
            return true;
        }
        ItemStack handled = ItemHandlerHelper.copyStackWithSize(stack, 1);
        DynamicTankChemicalStack snapshot = copy(tile.structure.chemicalStored);
        DynamicTankTankView tank = new DynamicTankTankView(tile, tile.structure);
        long moved = itemHasChemical(handled) ? drainItem(handled, tank) : fillItem(handled, tank);
        if (moved <= 0) {
            tile.structure.chemicalStored = snapshot;
            return false;
        }
        if (!player.capabilities.isCreativeMode) {
            if (stack.getCount() == 1) {
                player.setHeldItem(hand, handled);
            } else if (player.inventory.addItemStackToInventory(handled)) {
                stack.shrink(1);
            } else {
                tile.structure.chemicalStored = snapshot;
                return false;
            }
        }
        player.inventory.markDirty();
        tile.markDirty();
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability) {
        return capability != null && (capability == UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY
              || capability == Capabilities.GAS_HANDLER_CAPABILITY);
    }

    @Nullable
    @Override
    public <T> T getCapability(TileEntityDynamicValve valve, Capability<T> capability,
          @Nullable EnumFacing side) {
        if (valve.structure == null) {
            return null;
        }
        DynamicTankHandler handler = new DynamicTankHandler(valve, valve.structure);
        if (capability == UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY) {
            return UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY.cast(handler);
        }
        if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
            return Capabilities.GAS_HANDLER_CAPABILITY.cast(handler);
        }
        return null;
    }

    @Override
    public String getDisplayName(DynamicTankChemicalStack stack) {
        IChemicalTypeCE type = resolve(stack);
        if (type instanceof MekGasChemicalType) {
            return ((MekGasChemicalType) type).getGas().getLocalizedName();
        }
        if (type instanceof PigmentTypeCE) {
            String path = ((PigmentTypeCE) type).getPath().replace('_', ' ');
            StringBuilder display = new StringBuilder(path.length() + 8);
            boolean capitalize = true;
            for (int i = 0; i < path.length(); i++) {
                char c = path.charAt(i);
                display.append(capitalize ? Character.toUpperCase(c) : c);
                capitalize = c == ' ';
            }
            return display.append(" Pigment").toString();
        }
        return stack.registryName;
    }

    @Nullable
    @Override
    public FluidStack getRenderStack(DynamicTankChemicalStack stack) {
        IChemicalTypeCE type = resolve(stack);
        if (type == null || UltimateFluids.ChemicalRender == null) {
            return null;
        }
        int color = 0xFFFFFF;
        if (type instanceof MekGasChemicalType) {
            color = ((MekGasChemicalType) type).getGas().getTint();
        } else if (type instanceof PigmentTypeCE) {
            color = ((PigmentTypeCE) type).getColor();
        }
        FluidStack render = new FluidStack(UltimateFluids.ChemicalRender, 1);
        render.tag = new NBTTagCompound();
        render.tag.setInteger("renderColor", color & 0xFFFFFF);
        render.tag.setString("chemicalKind", stack.kind);
        render.tag.setString("chemicalName", stack.registryName);
        return render;
    }

    private static long drainItem(ItemStack item, DynamicTankTankView target) {
        IChemicalHandlerCE handler = getFacadeHandler(item);
        if (handler != null) {
            for (int index = 0; index < handler.getChemicalTankCount(null); index++) {
                IChemicalTankCE source = handler.getChemicalTank(index, null);
                IChemicalStackCE available = source.getStack();
                if (available == null || available.isEmpty()
                      || !handler.canExtractChemical(index, available.getType(), null)) {
                    continue;
                }
                long offered = Math.min(ITEM_TRANSFER_RATE, available.getAmount());
                long accepted = target.insert(available.copyWithAmount(offered), Action.SIMULATE);
                IChemicalStackCE simulated = source.extract(accepted, Action.SIMULATE);
                if (accepted <= 0 || simulated == null || simulated.getAmount() != accepted) {
                    continue;
                }
                IChemicalStackCE extracted = source.extract(accepted, Action.EXECUTE);
                return extracted == null ? 0 : target.insert(extracted, Action.EXECUTE);
            }
            return 0;
        }
        if (item.getItem() instanceof IGasItem) {
            IGasItem gasItem = (IGasItem) item.getItem();
            GasStack available = gasItem.getGas(item);
            if (available == null || available.amount <= 0) {
                return 0;
            }
            long offered = Math.min(ITEM_TRANSFER_RATE,
                  Math.min(gasItem.getRate(item), available.amount));
            ChemicalStackCE chemical = new ChemicalStackCE(new MekGasChemicalType(available.getGas()), offered);
            int accepted = (int) target.insert(chemical, Action.SIMULATE);
            GasStack extracted = gasItem.removeGas(item, accepted);
            return extracted == null ? 0 : target.insert(
                  new ChemicalStackCE(new MekGasChemicalType(extracted.getGas()), extracted.amount), Action.EXECUTE);
        }
        return 0;
    }

    private static long fillItem(ItemStack item, DynamicTankTankView source) {
        IChemicalStackCE available = source.getStack();
        if (available == null) {
            return 0;
        }
        IChemicalHandlerCE handler = getFacadeHandler(item);
        if (handler != null) {
            for (int index = 0; index < handler.getChemicalTankCount(null); index++) {
                if (!handler.canInsertChemical(index, available.getType(), null)) {
                    continue;
                }
                IChemicalTankCE target = handler.getChemicalTank(index, null);
                long offered = Math.min(ITEM_TRANSFER_RATE, source.getStored());
                long accepted = target.insert(available.copyWithAmount(offered), Action.SIMULATE);
                IChemicalStackCE simulated = source.extract(accepted, Action.SIMULATE);
                if (accepted <= 0 || simulated == null || simulated.getAmount() != accepted) {
                    continue;
                }
                accepted = target.insert(simulated, Action.EXECUTE);
                source.extract(accepted, Action.EXECUTE);
                return accepted;
            }
            return 0;
        }
        if (item.getItem() instanceof IGasItem && available.getType() instanceof MekGasChemicalType) {
            IGasItem gasItem = (IGasItem) item.getItem();
            Gas gas = ((MekGasChemicalType) available.getType()).getGas();
            if (!gasItem.canReceiveGas(item, gas)) {
                return 0;
            }
            int offered = (int) Math.min(Integer.MAX_VALUE,
                  Math.min(ITEM_TRANSFER_RATE, Math.min(gasItem.getRate(item), source.getStored())));
            int accepted = gasItem.addGas(item, new GasStack(gas, offered));
            source.extract(accepted, Action.EXECUTE);
            return accepted;
        }
        return 0;
    }

    private static boolean itemHasChemical(ItemStack item) {
        IChemicalHandlerCE handler = getFacadeHandler(item);
        if (handler != null) {
            for (int index = 0; index < handler.getChemicalTankCount(null); index++) {
                IChemicalStackCE stack = handler.getChemicalTank(index, null).getStack();
                if (stack != null && !stack.isEmpty()) {
                    return true;
                }
            }
            return false;
        }
        return item.getItem() instanceof IGasItem && ((IGasItem) item.getItem()).getGas(item) != null;
    }

    @Nullable
    private static IChemicalHandlerCE getFacadeHandler(ItemStack stack) {
        return stack == null || stack.isEmpty()
              || UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY == null
              || !stack.hasCapability(UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null)
              ? null : stack.getCapability(UltimateChemicalCapabilities.CHEMICAL_HANDLER_CAPABILITY, null);
    }

    private static boolean canAcceptOutput(ItemStack output, ItemStack handled) {
        return output.isEmpty() || ItemHandlerHelper.canItemStacksStack(output, handled)
              && output.getCount() < output.getMaxStackSize();
    }

    @Nullable
    private static DynamicTankChemicalStack copy(@Nullable DynamicTankChemicalStack stack) {
        return stack == null ? null : stack.copy();
    }

    @Nullable
    private static IChemicalTypeCE resolve(DynamicTankChemicalStack stack) {
        try {
            return UltimateChemicalRegistry.INSTANCE.resolve(ChemicalKind.valueOf(stack.kind), stack.registryName);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static final class DynamicTankTankView implements IChemicalTankCE {

        private final TileEntityDynamicTank tile;
        private final SynchronizedTankData data;

        private DynamicTankTankView(TileEntityDynamicTank tile, SynchronizedTankData data) {
            this.tile = tile;
            this.data = data;
        }

        @Nullable
        @Override
        public IChemicalStackCE getStack() {
            IChemicalTypeCE type = data.chemicalStored == null ? null : resolve(data.chemicalStored);
            return type == null ? null : new ChemicalStackCE(type, data.chemicalStored.amount);
        }

        @Override
        public long getStored() {
            return data.chemicalStored == null ? 0 : data.chemicalStored.amount;
        }

        @Override
        public long getCapacity() {
            return DynamicTankChemicalHooks.getCapacity(data.volume);
        }

        @Override
        public long insert(IChemicalStackCE stack, Action action) {
            if (stack == null || stack.isEmpty() || data.fluidStored != null
                  || !DynamicTankChemicalStack.isValidId(stack.getType().getKind().name())
                  || !DynamicTankChemicalStack.isValidId(stack.getType().getRegistryName())) {
                return 0;
            }
            DynamicTankChemicalStack stored = data.chemicalStored;
            if (stored != null && (!stored.kind.equals(stack.getType().getKind().name())
                  || !stored.registryName.equals(stack.getType().getRegistryName()))) {
                return 0;
            }
            long accepted = Math.min(getCapacity() - getStored(), stack.getAmount());
            if (accepted > 0 && action.execute()) {
                data.chemicalStored = new DynamicTankChemicalStack(stack.getType().getKind().name(),
                      stack.getType().getRegistryName(), getStored() + accepted);
                changed();
            }
            return accepted;
        }

        @Nullable
        @Override
        public IChemicalStackCE extract(long amount, Action action) {
            IChemicalStackCE stored = getStack();
            if (amount <= 0 || stored == null) {
                return null;
            }
            long extracted = Math.min(amount, stored.getAmount());
            if (action.execute()) {
                long remaining = stored.getAmount() - extracted;
                data.chemicalStored = remaining <= 0 ? null
                      : new DynamicTankChemicalStack(stored.getType().getKind().name(),
                            stored.getType().getRegistryName(), remaining);
                changed();
            }
            return stored.copyWithAmount(extracted);
        }

        private void changed() {
            tile.markDirty();
            MekanismUtils.saveChunk(tile);
            if (tile instanceof TileEntityDynamicValve) {
                Coord4D location = Coord4D.get(tile);
                for (ValveData valve : data.valves) {
                    if (location.equals(valve.location)) {
                        valve.onTransfer();
                    }
                }
            }
        }
    }

    private static final class DynamicTankHandler implements IChemicalHandlerCE, IGasHandler {

        private final DynamicTankTankView tank;
        private final GasTankInfo info = new GasInfo();

        private DynamicTankHandler(TileEntityDynamicTank tile, SynchronizedTankData data) {
            tank = new DynamicTankTankView(tile, data);
        }

        @Override
        public int getChemicalTankCount(@Nullable EnumFacing side) {
            return 1;
        }

        @Override
        public IChemicalTankCE getChemicalTank(int index, @Nullable EnumFacing side) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Chemical tank index: " + index);
            }
            return tank;
        }

        @Override
        public boolean canInsertChemical(int index, IChemicalTypeCE type, @Nullable EnumFacing side) {
            return index == 0 && type != null
                  && tank.insert(new ChemicalStackCE(type, 1), Action.SIMULATE) > 0;
        }

        @Override
        public boolean canExtractChemical(int index, IChemicalTypeCE type, @Nullable EnumFacing side) {
            IChemicalStackCE stored = tank.getStack();
            return index == 0 && type != null && stored != null
                  && LongChemicalTank.isSameType(stored.getType(), type);
        }

        @Override
        public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
            if (stack == null || stack.getGas() == null || stack.amount <= 0) {
                return 0;
            }
            return (int) tank.insert(new ChemicalStackCE(new MekGasChemicalType(stack.getGas()), stack.amount),
                  doTransfer ? Action.EXECUTE : Action.SIMULATE);
        }

        @Nullable
        @Override
        public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer) {
            IChemicalStackCE stored = tank.getStack();
            if (amount <= 0 || stored == null || !(stored.getType() instanceof MekGasChemicalType)) {
                return null;
            }
            IChemicalStackCE extracted = tank.extract(amount,
                  doTransfer ? Action.EXECUTE : Action.SIMULATE);
            return extracted == null ? null : new GasStack(
                  ((MekGasChemicalType) extracted.getType()).getGas(), (int) extracted.getAmount());
        }

        @Override
        public boolean canReceiveGas(EnumFacing side, Gas type) {
            return type != null && tank.insert(new ChemicalStackCE(new MekGasChemicalType(type), 1),
                  Action.SIMULATE) > 0;
        }

        @Override
        public boolean canDrawGas(EnumFacing side, Gas type) {
            IChemicalStackCE stored = tank.getStack();
            return stored != null && stored.getType() instanceof MekGasChemicalType
                  && (type == null || ((MekGasChemicalType) stored.getType()).getGas() == type);
        }

        @Nonnull
        @Override
        public GasTankInfo[] getTankInfo() {
            return new GasTankInfo[]{info};
        }

        private final class GasInfo implements GasTankInfo {

            @Nullable
            @Override
            public GasStack getGas() {
                IChemicalStackCE stored = tank.getStack();
                return stored != null && stored.getType() instanceof MekGasChemicalType
                      ? new GasStack(((MekGasChemicalType) stored.getType()).getGas(),
                            saturatingInt(stored.getAmount())) : null;
            }

            @Override
            public int getStored() {
                GasStack gas = getGas();
                return gas == null ? 0 : gas.amount;
            }

            @Override
            public int getMaxGas() {
                return saturatingInt(tank.getCapacity());
            }
        }
    }

    private static int saturatingInt(long value) {
        return value >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.max(0, value);
    }
}
